package ua.ieeta.dicom;

import java.util.HashSet;
import java.util.Set;

import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DLink {
	final Logger log = LoggerFactory.getLogger(DLink.class);
	
	final DClient client;
	final String AET;
	
	final NetworkConnection connection = new NetworkConnection();
	final NetworkApplicationEntity appEntity = new NetworkApplicationEntity();
	
	final Set<DAssociation> associations = new HashSet<DAssociation>();
	
	DLink(DClient client, String aet, String host, int port) {
		this.client = client;
		this.AET = aet;
		
		connection.setHostname(host);
		connection.setPort(port);
		
		appEntity.setAETitle(aet);
		appEntity.setNetworkConnection(connection);
		appEntity.setInstalled(true);
		appEntity.setAssociationAcceptor(true);
		
		client.links.add(this);
		log.info("Link {} -> {} OPEN", client.AET, AET);
	}
	
	public String getAET() {return AET;}
	public DClient getClient() {return client;}
	
	public DAssociation associate(){
		return new DAssociation(this);
	}
	
	public void close() {
		closeWithoutRemove();
		client.links.remove(this);
	}
	
	void closeWithoutRemove() {
		for(DAssociation ass: associations)
			ass.closeWithoutRemove();
		associations.clear();
		
		connection.unbind();
		log.info("Link {} -> {} CLOSED", client.AET, AET);
	}
}
