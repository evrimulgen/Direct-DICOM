package ua.ieeta.dicom;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.CommandUtils;
import org.dcm4che2.net.DataWriter;
import org.dcm4che2.net.DataWriterAdapter;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.DimseRSPHandler;
import org.dcm4che2.net.NewThreadExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.ieeta.dicom.dm.IData;
import ua.ieeta.dicom.dm.ISerie;
import ua.ieeta.dicom.internal.DMove;
import ua.ieeta.dicom.internal.DField;
import ua.ieeta.dicom.internal.IDicom;
import ua.ieeta.dicom.internal.dm.DBase;
import ua.ieeta.util.Reflector;

public class DAssociation {
	final Logger log = LoggerFactory.getLogger(DAssociation.class);
		
	final DLink link;
	final String tsuid = UID.ImplicitVRLittleEndian;
	final String uid = UUID.randomUUID().toString();
	
	Association as = null;
	
	final Set<DMove> moves = new HashSet<DMove>();
	
	volatile String recentError = null;
	
	private final DimseRSPHandler moveRspHandler = new DimseRSPHandler() {		
		@Override
		public void onDimseRSP(Association as, DicomObject cmd, DicomObject data) {
			final int status = cmd.getInt(Tag.Status);
			
			if(recentError == null) {
				if(status == 65280) {
					log.debug("C-STORE-STATUS PENDING on association {} =>\n{}", uid, cmd);
				} else if(status != 0) {
					log.error("C-STORE-STATUS FAIL on association {} =>\n{}", uid, cmd);
					recentError = "Dicom C-MOVE-STATUS error! status = " + status;
					unlock();
				} else if(status == 0) {
					final int remaining = cmd.getInt(Tag.NumberOfRemainingSuboperations);
					
					if(remaining != 0) {
						log.debug("C-STORE-STATUS OK on association {} =>\n{}", uid, cmd);
					} else {
						log.debug("C-MOVE-STATUS COMPLETE on association {} =>\n{}", uid, cmd);
						unlock();
					}
				}
			} else
				log.debug("C-STORE-STATUS IGNORED (error already detected, ignoring all other messages) on association {} =>\n{}", uid, cmd);

		}
	};
	
	
	private final DimseRSPHandler storeRspHandler = new DimseRSPHandler() {
		@Override
		public void onDimseRSP(Association as, DicomObject cmd, DicomObject data) {
			// 65280 => pending
			int status = cmd.getInt(Tag.Status);
			if (status != 0 && status != 65280) {
				log.error("C-STORE-RSP FAIL on association {} =>\n{}", uid, cmd);
				recentError = "Dicom C-STORE-RSP error!";
			}

			if(status != 65280)	//if not pending (OK, ERROR) => end store action
				unlock();
		}
	};

	DAssociation(DLink link) {
		this.link = link;

		try {
			as = link.client.appEntity.connect(link.appEntity, new NewThreadExecutor(link.AET));
			link.associations.add(this);
			log.info("Association({}) OPEN to {}", uid, link.AET);
		} catch (Exception e) {
			log.error("Association({}) FAILED to {}", uid, link.AET);
			throw new RuntimeException(e);
		}
	}
	
	public DLink getLink() {return link;}
	
	public boolean echo() {
		try {
			CommandUtils.setIncludeUIDinRSP(true);
			
			final DimseRSP rsp = as.cecho(UID.StudyRootQueryRetrieveInformationModelFIND);
			log.info("C-ECHO on association {}", uid);
			return rsp.next();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
		

	public List<IData> loadData(String studyUID, String serieUID) {
		final DicomObject keys = new BasicDicomObject();
		
		keys.putString(ISerie.F_STUDY_UID.getTag(), ISerie.F_STUDY_UID.getVR(), studyUID);
		keys.putString(ISerie.ID.getTag(), ISerie.ID.getVR(), serieUID);
		
		keys.putString(Tag.QueryRetrieveLevel, VR.CS, "SERIES");
		keys.putString(Tag.Modality, VR.CS, "MR");
		
		final String aet = getLink().getClient().getAET();
		return cMove(keys, aet).getData();
	}

	public <T extends IDicom> T findUnique(T dicom, DField... loadFields) {
		final List<T> result = find(dicom, loadFields);

		if(!result.isEmpty())
			return result.get(0);
		return null;
	}
	
	public <T extends IDicom> List<T> find(T dicom, DField ...loadFields) {
		DBase example = (DBase) dicom;
		
		final DicomObject keys = new BasicDicomObject();
		example.getDicomObject().copyTo(keys);
		
		//request fields from server
		for(DField f: loadFields)
			if(!keys.contains(f.getTag()))
				keys.putNull(f.getTag(), f.getVR());
		
		keys.putString(Tag.QueryRetrieveLevel, VR.CS, example.getRetrieveLevel());
		keys.putString(Tag.Modality, VR.CS, "MR");
		
		final List<DicomObject> result = cFind(keys);
		
		final Constructor<T> constructor = Reflector.getConstructor(dicom.getClass(), DicomObject.class);
		final List<T> pResult = new LinkedList<T>();
		for(DicomObject dObj: result) {
			T instance = Reflector.create(constructor, dObj);
			pResult.add(instance);
		}

		return pResult;
	}
	
	public void store(InputStream is) {
		try {
			final DicomInputStream dis = new DicomInputStream(is);
			final DicomObject dObject = dis.readDicomObject();
			cStore(dObject);
			dis.close();
			is.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void close() {
		closeWithoutRemove();
		link.associations.remove(this);
	}
	
	//----------------------------------------------------------------------------------------------------------------
	private List<DicomObject> cFind(DicomObject keys){
		try {
			log.debug("C-FIND-RQ on association({}) =>\n{}", uid, keys);
			final DimseRSP rsp = as.cfind(UID.StudyRootQueryRetrieveInformationModelFIND, 0, keys, tsuid, Integer.MAX_VALUE);
			
			final List<DicomObject> result = new ArrayList<DicomObject>();
			while (rsp.next()) {
				DicomObject cmd = rsp.getCommand();
				if (CommandUtils.isPending(cmd)) {
					DicomObject data = rsp.getDataset();
					result.add(data);
					
					log.debug("C-FIND-RSP on association({}) =>\n{}", uid, data);
				}
			}	
			
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private DMove cMove(DicomObject keys, String aetDestination) {
		final String serieID = keys.getString(ISerie.ID.getTag());
		final DMove dMove = link.getClient().getStorage().createMove(serieID);
		
		try {
			recentError = null;

			log.debug("C-MOVE-RQ on association({}) =>\n{}", uid, keys);
			as.cmove(UID.StudyRootQueryRetrieveInformationModelMOVE, 0, keys, tsuid, aetDestination, moveRspHandler);
			
			lock();
			
			if(recentError != null) {
				dMove.setError(recentError);
				throw new RuntimeException(recentError);
			}
			
			moves.add(dMove);
			log.info("GET-MOVE: " + serieID);
			
			return dMove;
		} catch (Exception e) {
			dMove.delete();
			throw new RuntimeException(e);
		}
	}
	
	private void cStore(DicomObject dObject) {
		try {
			final String iuid = dObject.getString(Tag.SOPInstanceUID);		
			final DataWriter data = new DataWriterAdapter(dObject);
			
			recentError = null;
			
			log.debug("C-STORE-RQ on association({}) =>\n{}", uid, dObject);
			as.cstore(UID.MRImageStorage, iuid, 0, data, tsuid, storeRspHandler);
			
			lock();
			
			if(recentError != null)
				throw new RuntimeException(recentError);
			
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private synchronized void lock() throws InterruptedException {
		wait();
	}
	
	private synchronized void unlock() {
		notify();
	}
	
	void closeWithoutRemove() {
		try {
			as.release(false);
			log.info("Association({}) CLOSED to {}", uid, link.AET);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			for(DMove move: moves)
				move.delete();
		}
	}
}
