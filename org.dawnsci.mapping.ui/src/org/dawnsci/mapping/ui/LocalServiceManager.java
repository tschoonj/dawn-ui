package org.dawnsci.mapping.ui;

import org.eclipse.dawnsci.analysis.api.io.ILoaderService;
import org.eclipse.dawnsci.analysis.api.io.IRemoteDatasetService;
import org.eclipse.dawnsci.analysis.api.persistence.IPersistenceService;
import org.eclipse.dawnsci.nexus.INexusFileFactory;
import org.eclipse.scanning.api.event.IEventService;
import org.osgi.service.event.EventAdmin;

public class LocalServiceManager {

	private static ILoaderService lservice;
	private static IPersistenceService pservice;
	private static IRemoteDatasetService dservice;
	private static INexusFileFactory nexusFactory;
	private static EventAdmin eventAdmin;
	
	public static void setLoaderService(ILoaderService s) {
		lservice = s;
	}

	public static ILoaderService getLoaderService() {
		return lservice;
	}
	
	public static void setPersistenceService(IPersistenceService s) {
		pservice = s;
	}
	
	public static IPersistenceService getPersistenceService() {
		return pservice;
	}

	public static IRemoteDatasetService getRemoteDatasetService() {
		return dservice;
	}

	public static void setRemoteDatasetService(IRemoteDatasetService d) {
		dservice = d;
	}
	
	public static INexusFileFactory getNexusFactory() {
		return nexusFactory;
	}

	public static void setNexusFactory(INexusFileFactory nexusFactory) {
		LocalServiceManager.nexusFactory = nexusFactory;
	}
	
	public static EventAdmin getEventAdmin() {
		return eventAdmin;
	}

	public static void setEventAdmin(EventAdmin eventAdmin) {
		LocalServiceManager.eventAdmin = eventAdmin;
	}
	
}