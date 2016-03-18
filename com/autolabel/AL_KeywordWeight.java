package com.autolabel;

//This is the class for <Keyword, Weight> tuple from website analysis

public class AL_KeywordWeight {

	private String m_sKeyword = "";
	private double m_fWeight = 0.0f;
	
	public AL_KeywordWeight() {
		// TODO Auto-generated constructor stub
	}
	
	public AL_KeywordWeight(String sKeyword, double fWeight) {
		m_sKeyword = sKeyword;
		m_fWeight = fWeight;
	}

	public void setKeywordWeight(String sKeyword, double fWeight) {
		m_sKeyword = sKeyword;
		m_fWeight = fWeight;
	}
	
	public String getKeyword() {
		return m_sKeyword;
	}
	
	public void setKeyword(String sKeyword) {
		m_sKeyword = sKeyword;
	}
	
	public double getWeight() {
		return m_fWeight;
	}
	
	public void setWeight(double fWeight) {
		m_fWeight = fWeight;
	}
	
}
