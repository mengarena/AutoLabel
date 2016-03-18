package com.autolabel;

//This class is for the final Crowdsourced store/place data of each store/place

import java.util.ArrayList;
import java.util.List;

public class AL_CrowdSourcedPlaceData {

	private double m_fGpsLat = 0.0f;
	private double m_fGpsLong = 0.0f;
	private List<AL_APInfoForDB> m_lstAPInfoForDB = new ArrayList<AL_APInfoForDB>();
	private List<String> m_lstOCRedWord = new ArrayList<String>();   //Duplicate words are converted to corresponding weight, noun/proper name extraction is done
	
	//private List<Integer> m_lstnOCRedWordFreq = new ArrayList<Integer>();
	private List<Double> m_lstfOCRedWordsWeight = new ArrayList<Double>();
	
	private List<String> m_lstAllOCRedWord = new ArrayList<String>();   //Store all the OCRed words (duplicate words are removed, no weight is associated, noun/proper names are not extracted.)
	
	
	public AL_CrowdSourcedPlaceData() {
		// TODO Auto-generated constructor stub
	}

	public AL_CrowdSourcedPlaceData(double fGpsLat, double fGpsLong, List<AL_APInfoForDB> lstAPInfoForDB, List<String> lstOCRedWord) {
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstAPInfoForDB = lstAPInfoForDB;
		m_lstOCRedWord = lstOCRedWord;
	}
	
	public AL_CrowdSourcedPlaceData(double fGpsLat, double fGpsLong, List<AL_APInfoForDB> lstAPInfoForDB, List<String> lstOCRedWord, List<Double> lstfOCRedWordsWeight) {
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstAPInfoForDB = lstAPInfoForDB;
		m_lstOCRedWord = lstOCRedWord;
		m_lstfOCRedWordsWeight = lstfOCRedWordsWeight;
	}
	
	public AL_CrowdSourcedPlaceData(double fGpsLat, double fGpsLong, List<AL_APInfoForDB> lstAPInfoForDB, List<String> lstOCRedWord, List<Double> lstfOCRedWordsWeight, List<String> lstAllOCRedWord) {
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstAPInfoForDB = lstAPInfoForDB;
		m_lstOCRedWord = lstOCRedWord;
		m_lstfOCRedWordsWeight = lstfOCRedWordsWeight;
		m_lstAllOCRedWord = lstAllOCRedWord;
	}
	
	public void setCrowdSourcedPlaceData(double fGpsLat, double fGpsLong, List<AL_APInfoForDB> lstAPInfoForDB, List<String> lstOCRedWord) {
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstAPInfoForDB = lstAPInfoForDB;
		m_lstOCRedWord = lstOCRedWord;
	}

//	public void setCrowdSourcedPlaceData(double fGpsLat, double fGpsLong, List<AL_APInfoForDB> lstAPInfoForDB, List<String> lstOCRedWord, List<Integer> lstnOCRedWordFreq) {
//		m_fGpsLat = fGpsLat;
//		m_fGpsLong = fGpsLong;
//		m_lstAPInfoForDB = lstAPInfoForDB;
//		m_lstOCRedWord = lstOCRedWord;
//		m_lstnOCRedWordFreq = lstnOCRedWordFreq;
//	}

	
	public void setCrowdSourcedPlaceData(double fGpsLat, double fGpsLong, List<AL_APInfoForDB> lstAPInfoForDB, List<String> lstOCRedWord, List<Double> lstfOCRedWordsWeight) {
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstAPInfoForDB = lstAPInfoForDB;
		m_lstOCRedWord = lstOCRedWord;
		m_lstfOCRedWordsWeight = lstfOCRedWordsWeight;
	}

	public void setCrowdSourcedPlaceData(double fGpsLat, double fGpsLong, List<AL_APInfoForDB> lstAPInfoForDB, List<String> lstOCRedWord, List<Double> lstfOCRedWordsWeight, List<String> lstAllOCRedWord) {
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstAPInfoForDB = lstAPInfoForDB;
		m_lstOCRedWord = lstOCRedWord;
		m_lstfOCRedWordsWeight = lstfOCRedWordsWeight;
		m_lstAllOCRedWord = lstAllOCRedWord;
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

	public void setAPInfoForDB(List<AL_APInfoForDB> lstAPInfoForDB) {
		m_lstAPInfoForDB = lstAPInfoForDB;
	}
	
	public List<String> getOCRedWord() {
		return m_lstOCRedWord;
	}

	public void setOCRedWord(List<String> lstOCRedWord) {
		m_lstOCRedWord = lstOCRedWord;
	}
	
//	public List<Integer> getOCRedWordFreq() {
//		return m_lstnOCRedWordFreq;
//	}
//	
//	public void setOCRedWordFreq(List<Integer> lstnOCRedWordFreq) {
//		m_lstnOCRedWordFreq = lstnOCRedWordFreq;
//	}

	public List<Double> getOCRedWordsWeight() {
		return m_lstfOCRedWordsWeight;
	}
	
	public void setOCRedWordsWeight(List<Double> lstfOCRedWordsWeight) {
		m_lstfOCRedWordsWeight = lstfOCRedWordsWeight;
	}
	
	public List<String> getAllOCRedWord() {
		return m_lstAllOCRedWord;
	}
	
	public void setAllOCRedWord(List<String> lstAllOCRedWord) {
		m_lstAllOCRedWord = lstAllOCRedWord;
	}
	
	
}
