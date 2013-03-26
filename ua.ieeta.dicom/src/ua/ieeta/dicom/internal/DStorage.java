package ua.ieeta.dicom.internal;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.PDVInputStream;
import org.dcm4che2.net.service.DicomService;
import org.dcm4che2.net.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.ieeta.dicom.dm.IData;
import ua.ieeta.dicom.dm.IData.Info;
import ua.ieeta.dicom.internal.dm.DataImpl;

public class DStorage {
	private final Logger log = LoggerFactory.getLogger(DStorage.class);
	
	private final File directory;
	
	//serieUID - imageUID - fileRef
	private final Map<String, Map<String, FileRef>> files = new ConcurrentHashMap<String, Map<String, FileRef>>();
	
	private class FileRef {
		private final Info info;
		private final File file;
		
		private volatile int refCount = 0;
		
		public FileRef(Info info, File file) {
			this.info = info;
			this.file = file;
		}
		
		public Info getInfo() {return info;}
		public File getFile() {return file;}
		
		public void addRef() {
			refCount++;
			log.debug("Add ref to file count={}: {}", refCount, file.getPath());
		}
		
		public void removeRef() {
			refCount--;
			log.debug("Remove ref from file count={}: {}", refCount, file.getPath());
			if(refCount == 0) {
				file.delete();
				files.get(info.getSerieUID()).remove(info.getImageUID());
				log.debug("Deleted file: " + file.getPath());
			}
		}
	}
	
	public DStorage(String storePath) {
		this.directory = new File(storePath);
		directory.mkdirs();
	}
	
	public DMove createMove(String serieUID) {
		return new DMove(this, serieUID);
	}
	
	public void destroyMove(DMove move) {
		final String serieUID = move.getSerieUID();
		final Map<String, FileRef> serieFiles = files.get(serieUID);
		
		if(serieFiles != null) {
			for(FileRef rFile: serieFiles.values())
				rFile.removeRef();
		
			if(serieFiles.isEmpty()) {
				files.remove(serieUID);
				log.debug("Serie destroyed: {}", serieUID);
			}
		}
	}
	
	public List<IData> getDataset(String sUID) {
		final List<IData> dataset = new LinkedList<IData>();
		final Map<String, FileRef> serieFiles = files.get(sUID);
		
		for(FileRef rFile: serieFiles.values()) {
			IData data = new DataImpl(rFile.getInfo(), rFile.getFile());
			dataset.add(data);
		}
		
		return dataset;
	}
	
	public DicomService createStorageService() {
		return new StorageService(DCapabilities.INSTANCE.getStoreCUIDs()) {
			@Override
			protected void onCStoreRQ(Association as, int pcid, DicomObject rq, PDVInputStream dataStream, String tsuid, DicomObject rsp) throws IOException, DicomServiceException {
				//C-STORE => 1.2.840.10008.1.2.1
				log.debug("C-STORE-RQ =>{}\n{}", pcid, rq);
				
				final String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
				final String cuid = rq.getString(Tag.AffectedSOPClassUID);
				
				final DicomObject dObject = dataStream.readDataset();
				dObject.initFileMetaInformation(cuid, iuid, tsuid);
				
				final String sopClassUID = dObject.getString(Tag.SOPClassUID);
				
				if(sopClassUID != null && sopClassUID.startsWith("1.2.840.10008.5.1.4.1.1.4")){
					final String suid = dObject.getString(Tag.SeriesInstanceUID);
					save(suid, iuid, dObject);
				} else {
					log.info("Not a valid image: " + iuid);
				}
				
				log.info("C-STORE-RSP =>\n{}", rsp);
			}
			
			private void save(String sUID, String iUID, DicomObject dObject) throws IOException {
				final String uid = sUID + "_" + iUID;
				final String fileName = uid + ".dcm";

				Map<String, FileRef> serieFiles = files.get(sUID);
				if(serieFiles == null) {
					serieFiles = new ConcurrentHashMap<String, FileRef>();
					files.put(sUID, serieFiles);
				}
				
				FileRef fileRef = serieFiles.get(iUID);
				if(fileRef == null) {
					final File file = new File(directory, fileName);
					file.createNewFile();
					
					final Info info = new Info(dObject);
					
					final DicomOutputStream dos = new DicomOutputStream(file);
					dos.writeDicomFile(dObject);
					dos.close();
					
					fileRef = new FileRef(info, file);
					serieFiles.put(iUID, fileRef);
					log.info("FILE-STORE: {}", fileName);
				} else
					log.info("FILE-EXIST: {}", fileName);
				
				fileRef.addRef();
			}
		};
	}
}
