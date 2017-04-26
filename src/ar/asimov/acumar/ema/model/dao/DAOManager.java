package ar.asimov.acumar.ema.model.dao;

import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;

public class DAOManager {
	
	private static final ThreadLocal<StationDAO> STATION_DAO;
	private static final ThreadLocal<WeatherFileDAO> FILE_DAO;
	private static final ThreadLocal<WeatherSummaryDAO> SUMMARY_DAO;
	private static final ThreadLocal<WeatherDataDAO> DATA_DAO;
	private static final ThreadLocal<ProcessInformationDAO> PROCESS_INFORMATION_DAO;
	static{
		STATION_DAO = new ThreadLocal<>();
		FILE_DAO = new ThreadLocal<>();
		SUMMARY_DAO = new ThreadLocal<>();
		DATA_DAO = new ThreadLocal<>();
		PROCESS_INFORMATION_DAO = new ThreadLocal<>();
	}
	
	public static final StationDAO getStationDAO(){
		if(null == STATION_DAO.get()){
			STATION_DAO.set(new StationDAO(EntityManagerHelper.getEntityManager()));
		}
		return STATION_DAO.get();
	}
	
	public static final WeatherFileDAO getFileDAO(){
		if(null == FILE_DAO.get()){
			FILE_DAO.set(new WeatherFileDAO(EntityManagerHelper.getEntityManager()));
		}
		return FILE_DAO.get();
	}
	
	public static final WeatherSummaryDAO getSummaryDAO(){
		if(null == SUMMARY_DAO.get()){
			SUMMARY_DAO.set(new WeatherSummaryDAO(EntityManagerHelper.getEntityManager()));
		}
		return SUMMARY_DAO.get();
	}
	
	public static final WeatherDataDAO getDataDAO(){
		if(null == DATA_DAO.get()){
			DATA_DAO.set(new WeatherDataDAO(EntityManagerHelper.getEntityManager()));
		}
		return DATA_DAO.get();
	}
	
	public static final ProcessInformationDAO getProcessInformationDAO(){
		if(null == PROCESS_INFORMATION_DAO.get()){
			PROCESS_INFORMATION_DAO.set(new ProcessInformationDAO(EntityManagerHelper.getEntityManager()));
		}
		return PROCESS_INFORMATION_DAO.get();
	}

	public static void beginTransaction() {
		EntityManagerHelper.beginTransaction();
		
	}

	public static void commitTransaction() {
		EntityManagerHelper.commitTransaction();
	}

	public static void rollBackTransaction() {
		EntityManagerHelper.rollbackTransaction();
	}

	public static void close() {
		EntityManagerHelper.closeEntityManager();
		STATION_DAO.remove();
		FILE_DAO.remove();
		SUMMARY_DAO.remove();
		DATA_DAO.remove();
		PROCESS_INFORMATION_DAO.remove();
	}

}
