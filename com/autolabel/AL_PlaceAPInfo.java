package com.autolabel;

//This class is for the keywords matching result, which is used to deploy in the end-user's smartphone
//The keywords matching result contains: Place/Store Name, GPS Lat, GPS Long, <AP MAC Address, LbRSS, UbRSS, MeanRSS, Order>s 

import java.util.ArrayList;
import java.util.List;


public class AL_PlaceAPInfo {
	private String m_sPlaceName = "";
	private double m_fGpsLat = 0.0f;
	private double m_fGpsLong = 0.0f;
	private List<AL_APInfoForDB> m_lstAPInfoForDB = new ArrayList<AL_APInfoForDB>();
	
	public AL_PlaceAPInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public AL_PlaceAPInfo(String sPlaceName, double fGpsLat, double fGpsLong, List<AL_APInfoForDB> lstAPInfoForDB) {
		m_sPlaceName = sPlaceName;
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstAPInfoForDB = lstAPInfoForDB;
	}
	
	public void setPlaceAPInfo(String sPlaceName, double fGpsLat, double fGpsLong, List<AL_APInfoForDB> lstAPInfoForDB) {
		m_sPlaceName = sPlaceName;
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstAPInfoForDB = lstAPInfoForDB;
	}
	
	public String getPlaceName() {
		return m_sPlaceName;
	}
	
	public void setPlaceName(String sPlaceName) {
		m_sPlaceName = sPlaceName;
	}
	
	public double getGpsLat() {
		return m_fGpsLat;
	}
	
	public void setGpsLat(double fGpsLat) {
		m_fGpsLat = fGpsLat;
	}

	public double getGpsLong() {
		return m_fGpsLong;
	}
	
	public void setGpsLong(double fGpsLong) {
		m_fGpsLong = fGpsLong;
	}
	
	public List<AL_APInfoForDB> getAPInfoForDB() {
		return m_lstAPInfoForDB;
	}
	
	public void setAPInfo(List<AL_APInfoForDB> lstAPInfoForDB) {
		m_lstAPInfoForDB = lstAPInfoForDB;
	}
	
}
