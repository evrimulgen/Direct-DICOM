package ua.ieeta.dicom;

import java.util.HashSet;
import java.util.Set;

import org.dcm4che2.net.Device;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.ieeta.dicom.internal.DCapabilities;
import ua.ieeta.dicom.internal.DStorage;

public class DClient {
	final Logger log = LoggerFactory.getLogger(DClient.class);

	final String AET;
	
	final NetworkConnection connection = new NetworkConnection();
	final NetworkApplicationEntity appEntity = new NetworkApplicationEntity();
	
	final Device device = new Device();
	final DStorage storageService;	
	
	final Set<DLink> links = new HashSet<DLink>();
	
	public DClient(String aet, String host, int port, String storePath) {
		AET = aet;
		
		connection.setHostname(host);
		connection.setPort(port);
		
		storageService = new DStorage(storePath);
		
		appEntity.setAETitle(AET);
		appEntity.setNetworkConnection(connection);
		appEntity.setAssociationInitiator(true);
		appEntity.setAssociationAcceptor(true);
		appEntity.setTransferCapability(DCapabilities.INSTANCE.getTransferCapabilities());
		appEntity.register(storageService.createStorageService());
		
		device.setNetworkApplicationEntity(appEntity);
		device.setNetworkConnection(connection);
		
		try {
			connection.bind(new NewThreadExecutor(AET + "-STORAGE"));
					
			while (!connection.isListening()) {
				log.info("Waiting for local {} listening state", AET);
				Thread.sleep(500);
			}
			log.info("Local {} is UP.", AET);
		}  catch (Exception e) {
			log.error("Failed to establish local {}", AET);
			throw new RuntimeException(e);
		}
		
	}
	
	public DStorage getStorage() {return storageService;}
	
	public String getAET() {return AET;}
	
	public DLink linkTo(String aet, String host, int port) {
		return new DLink(this, aet, host, port);
	}
	
	public void close() {
		for(DLink link: links)
			link.closeWithoutRemove();
		links.clear();
		
		connection.unbind();
		log.info("Local {} is DOWN", AET);
	}
}
