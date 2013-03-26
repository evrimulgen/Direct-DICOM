package ua.ieeta.dicom.internal;

import org.dcm4che2.data.UID;
import org.dcm4che2.net.ExtQueryTransferCapability;
import org.dcm4che2.net.TransferCapability;

public enum DCapabilities {
	INSTANCE;
	
	private final String[] storecuids = {UID.MRImageStorage};
	private final TransferCapability[] tcs;
	
	private DCapabilities() {
		final String[] NATIVE_LE_TS = {UID.ExplicitVRLittleEndian, UID.ImplicitVRLittleEndian};
		
		final String[] findcuids = {
				UID.StudyRootQueryRetrieveInformationModelFIND,
				UID.PatientRootQueryRetrieveInformationModelFIND,
				UID.PatientStudyOnlyQueryRetrieveInformationModelFINDRetired};
		
		final String[] movecuids = {
				UID.StudyRootQueryRetrieveInformationModelMOVE,
				UID.PatientRootQueryRetrieveInformationModelMOVE,
				UID.PatientStudyOnlyQueryRetrieveInformationModelMOVERetired};
		
		tcs = new TransferCapability[findcuids.length + movecuids.length + storecuids.length];
		
		int i = 0;
		
		for (String cuid : findcuids)
			tcs[i++] = new ExtQueryTransferCapability(cuid, NATIVE_LE_TS, TransferCapability.SCU);

		for (String cuid : movecuids)
			tcs[i++] = new ExtQueryTransferCapability(cuid, NATIVE_LE_TS, TransferCapability.SCU);
		
		for (String cuid : storecuids)
			tcs[i++] = new TransferCapability(cuid, NATIVE_LE_TS, TransferCapability.SCP);
	}
	
	
	public String[] getStoreCUIDs() {return storecuids;}
	public TransferCapability[] getTransferCapabilities() {return tcs;}
}
