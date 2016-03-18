package com.autolabel;

//This class is for store/place data from Program scanning/analyzing website
//After analyzing a store/place website, following information is obtained:
// Store name, Gps Lat, Gps Long, Keyword1, Weight1, Keyword2, Weight2, Keyword3, Weight3.....
//This class is used to organize the data for one place/store

import java.util.ArrayList;
import java.util.List;

public class AL_PlaceWebData {

	private String m_sPlaceName = "";
	private double m_fGpsLat = 0.0f;
	private double m_fGpsLong = 0.0f;
	private List<AL_KeywordWeight> m_lstKeywordWeight = new ArrayList<AL_KeywordWeight>();
	
	public AL_PlaceWebData() {
		// TODO Auto-generated constructor stub
	}
	
	public AL_PlaceWebData(String sPlaceName, double fGpsLat, double fGpsLong, List<AL_KeywordWeight> lstKeywordWeight) {
		m_sPlaceName = sPlaceName;
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstKeywordWeight = lstKeywordWeight;
	}
	
	public void setPlaceWebData(String sPlaceName, double fGpsLat, double fGpsLong, List<AL_KeywordWeight> lstKeywordWeight) {
		m_sPlaceName = sPlaceName;
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstKeywordWeight = lstKeywordWeight;
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
	
	public List<AL_KeywordWeight> getKeywordWeight() {
		return m_lstKeywordWeight;
	}

	public void setKeywordWeight(List<AL_KeywordWeight> lstKeywordWeight) {
		m_lstKeywordWeight = lstKeywordWeight;
	}
	
}
