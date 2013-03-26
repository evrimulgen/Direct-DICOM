package ua.ieeta.dicom.internal.dm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;

import ua.ieeta.dicom.dm.IData;
import ua.ieeta.util.StreamUtil;

public class DataImpl implements IData {
	final Info info;
	final File file;
	
	public DataImpl(Info info, File file) {
		this.info = info;
		this.file = file;
	}
	
	@Override
	public Info getInfo() {return info;}

	@Override
	public InputStream getStream() {
		//if(!move.isAvailable())
		//	throw new RuntimeException("Data ("+info.getName()+") is not available! DAssociation used to retrive this data is closed.");

		try {
			return new DicomInputStream(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void copyTo(OutputStream dst) {
		StreamUtil.copy(getStream(), dst);
	}
	
	/*@Override
	public void store() {
		Context.get(DAssociation.class).store(getStream());
	}*/
	
	//public File getFile() {return file;}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;

		if ((obj == null) || (obj.getClass() != this.getClass())) return false;
		
		final Info objInf = ((DataImpl)obj).info;
		
		return (info.getStudyUID().equals(objInf.getStudyUID()) &&
				info.getSerieNumber() == objInf.getSerieNumber() &&
				info.getImageNumber() == objInf.getImageNumber());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + info.getStudyUID().hashCode();
		hash = 31 * hash + info.getSerieNumber();
		hash = 31 * hash + info.getImageNumber();
		return hash;
	}
	
	
	//---------------------------------------------------------------------------------------------------
	public static class Loader implements ILoader {

		@Override
		public IData create(File file) {
			try(DicomInputStream dis = new DicomInputStream(file)) {
				final DicomObject dObject = dis.readDicomObject();		
				final Info info = new Info(dObject);
				
				return new DataImpl(info, file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
}
