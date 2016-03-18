package com.autolabel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

//This class is used to process all the crowdsourced data for one place
//For each place/store, many crowdsourcers could collect following data: GPS coordinates, Pictures--hence OCRed text, AP info (MAC address, RSS)

public class OnePlaceDataProcessor {

	private List<String> m_lststrOnePlaceOCRedTextFiles = null;
	private List<String> m_lststrOnePlaceGpsApInfoFiles = null;
	private String m_sDefaultGpsCoordinates = null;
	
	private List<String> m_lststrOnePlaceOCRedWords = null;  //Merged OCRed Text
	private List<Integer> m_lstnOnePlaceOCRedWordsFreq = null;  //Frequency (=time of occurrence) of these words
	private List<Double> m_lstfOnePlaceOCRedWordsWeight = null;  //Weight of these words
	
	private double m_fGpsLat = 0.0f;
	private double m_fGpsLong = 0.0f;
	private int m_nGpsCnt = 0;
	private List<String> m_lststrApMac = null;
	private List<Integer> m_lstnApRss = null;
	
	private List<Integer> m_lstnApMacOccurredCnt = null;   //The times each MAC occurred
	private List<List<Integer>> m_lstlstfAPRss = null;    //Record the RSS of each AP MAC

	private List<Double> m_lstfMeanApRss = null;
	private List<Double> m_lstfStdApRss = null;
	private List<Double> m_lstfLbApRss = null;
	private List<Double> m_lstfUbApRss = null;
	
	private List<String> m_lststrOrderedApMac = null;
	private List<Double> m_lstfOrderedMeanApRss = null;
	private List<Double> m_lstfOrderedStdApRss = null;
	private List<Double> m_lstfOrderedLbApRss = null;
	private List<Double> m_lstfOrderedUbApRss = null;
	private List<Integer> m_lstnOrderedApMacOccurredCnt = null;

	private static FileWriter m_fwCrowdsourceOnePlaceDataDBFile = null;
	
	public OnePlaceDataProcessor() {
		// TODO Auto-generated constructor stub
	}
	
	public OnePlaceDataProcessor(List<String> lststrOnePlaceOCRedTextFiles, List<String> lststrOnePlaceGpsApInfoFiles, FileWriter fwCrowdsourceOnePlaceDataDBFile, String sDefaultGpsCoordinates) {
		m_lststrOnePlaceOCRedTextFiles = lststrOnePlaceOCRedTextFiles;
		m_lststrOnePlaceGpsApInfoFiles = lststrOnePlaceGpsApInfoFiles;
		m_fwCrowdsourceOnePlaceDataDBFile = fwCrowdsourceOnePlaceDataDBFile;
		m_sDefaultGpsCoordinates = sDefaultGpsCoordinates;
	}
	
	public void setOnePlaceDataSetFile(List<String> lststrOnePlaceOCRedTextFiles, List<String> lststrOnePlaceGpsApInfoFiles, FileWriter fwCrowdsourceOnePlaceDataDBFile, String sDefaultGpsCoordinates) {
		m_lststrOnePlaceOCRedTextFiles = lststrOnePlaceOCRedTextFiles;
		m_lststrOnePlaceGpsApInfoFiles = lststrOnePlaceGpsApInfoFiles;
		m_fwCrowdsourceOnePlaceDataDBFile = fwCrowdsourceOnePlaceDataDBFile;
		m_sDefaultGpsCoordinates = sDefaultGpsCoordinates;
	}
		
	public void mergeOnePlaceDataSet() {
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		int i;
		String[] fields = null;
		String sOCRedWord = "";
		String sApMac = "";
		int nIdx = 0;
		int nRss = 0;
		double fRss = 0.0;
		int nCnt;

		//Merge OCRed text 
		m_lststrOnePlaceOCRedWords = new ArrayList<String>();
		
		for (String sOCRedTextFile : m_lststrOnePlaceOCRedTextFiles) {
			try {
				fr = new FileReader(sOCRedTextFile);
				br = new BufferedReader(fr);
				
				while((sLine = br.readLine()) != null) {
					fields = Utility.getFields(sLine);
					
					for (int j=0; j<fields.length; j++) {
						sOCRedWord = fields[j].toLowerCase();
						if (m_lststrOnePlaceOCRedWords.contains(sOCRedWord) == false) {
							m_lststrOnePlaceOCRedWords.add(sOCRedWord);
						}
					}
					
					//Assume every file only contains one line
					break;
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
		}
		
		//Merge GPS coordinates, AP info
		m_lststrApMac = new ArrayList<String>();;
		m_lstnApRss = new ArrayList<Integer>();;
		m_lstnApMacOccurredCnt = new ArrayList<Integer>();
		
		m_fGpsLat = 0;
		m_fGpsLong = 0;
		m_nGpsCnt = 0;
		
		for (String sGpsApInfoFile: m_lststrOnePlaceGpsApInfoFiles) {
			try {
				fr = new FileReader(sGpsApInfoFile);
				br = new BufferedReader(fr);
				
				while((sLine = br.readLine()) != null) {
					fields = Utility.getFields(sLine);
					
					m_nGpsCnt = m_nGpsCnt + 1;
					m_fGpsLat = m_fGpsLat + Double.valueOf(fields[0]).doubleValue();
					m_fGpsLong = m_fGpsLong + Double.valueOf(fields[1]).doubleValue();
					
					for (int j=2; j<fields.length; j++) {
						if (j % 2 == 0) {  //Field for Mac address
							sApMac = fields[j].toLowerCase();
						} else {
							nRss = Integer.valueOf(fields[j]).intValue();
							nIdx = m_lststrApMac.indexOf(sApMac);
							if (nIdx == -1) {
								m_lststrApMac.add(sApMac);
								m_lstnApRss.add(nRss);
								m_lstnApMacOccurredCnt.add(1);
							} else {
								nRss = nRss + m_lstnApRss.get(nIdx);
								nCnt = m_lstnApMacOccurredCnt.get(nIdx) + 1;
								m_lstnApRss.set(nIdx, nRss);
								m_lstnApMacOccurredCnt.set(nIdx, nCnt);
							}
						}
					}
					
					//Assume every file only contains one line
					break;
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
			
		}  //For
		
		if (m_nGpsCnt == 0) {
			String[] sarrLatLong = Utility.getFields(m_sDefaultGpsCoordinates);
			m_fGpsLat = Double.valueOf(sarrLatLong[0]).doubleValue();
			m_fGpsLong = Double.valueOf(sarrLatLong[1]).doubleValue();
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		} else {
			//Average GPS
			m_fGpsLat = m_fGpsLat/m_nGpsCnt;
			m_fGpsLong = m_fGpsLong/m_nGpsCnt;
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		}
		
		//Take the average of RSS
		m_lstfMeanApRss = new ArrayList<Double>();
		for (i=0; i<m_lstnApRss.size(); i++) {
			double fMeanRss = m_lstnApRss.get(i)*1.0 / m_lstnApMacOccurredCnt.get(i);
			fMeanRss = Math.round(fMeanRss*100)/100.0;
			m_lstfMeanApRss.add(fMeanRss);
		}
		 
		
		//Order Ap MAC based on RSS
		m_lststrOrderedApMac = new ArrayList<String>();
		m_lstfOrderedMeanApRss = new ArrayList<Double>();
		
		for (i=0; i<m_lstfMeanApRss.size(); i++) {
			if (i==0) {
				m_lststrOrderedApMac.add(m_lststrApMac.get(i));
				m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
			} else {
				
				nIdx = -1;
				fRss = m_lstfMeanApRss.get(i);
				for (int j=0; j<m_lstfOrderedMeanApRss.size(); j++) {
					if (fRss > m_lstfOrderedMeanApRss.get(j)) {
						nIdx = j;
						break;
					}
				}
				
				if (nIdx == -1) {
					m_lststrOrderedApMac.add(m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(fRss);
				} else {
					m_lststrOrderedApMac.add(nIdx, m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(nIdx, fRss);					
				}
				
			}
		}
		
		//By here, GPS are averaged, Ap MAC are merged, RSS are averaged and ordered, OCRed text are merged
		//Now need to write into file.
		//For AP info, only write up to 10 APs (i.e. top 10 of RSS)
		sLine = m_fGpsLat + "," + m_fGpsLong;
		
		if (m_lststrOrderedApMac.size() > Utility.DEFAULT_AP_MAC_CNT) {
			nCnt = Utility.DEFAULT_AP_MAC_CNT;
		} else {
			nCnt = m_lststrOrderedApMac.size();
		}
		
		for (i=0; i<nCnt; i++) {
			sLine = sLine + "," + m_lststrOrderedApMac.get(i) + "," + m_lstfOrderedMeanApRss.get(i) + "," + (i+1);
		}
		
		for (i=nCnt; i<Utility.DEFAULT_AP_MAC_CNT; i++) {
			sLine = sLine + ",,,";
		}
		
		for (i=0; i<m_lststrOnePlaceOCRedWords.size(); i++) {
			sLine = sLine + "," + m_lststrOnePlaceOCRedWords.get(i);
		}
		
		sLine = sLine + System.getProperty("line.separator");
		
		try {
			m_fwCrowdsourceOnePlaceDataDBFile.write(sLine);
		} catch (Exception e) {
			
		}
	}

	
	////////////////////////////////////////////////////////////////////////////////
	//The Fields indexes are set corresponding to the Sensing App result
	//For the OCRed Text, the duplicate words are REMOVED
	//Noun/Proper name extraction are NOT applied here.
	////////////////////////////////////////////////////////////////////////////////
	public String mergeOnePlaceDataSet(List<String> lststrOnePlaceGpsApInfoFiles, List<String> lststrOnePlaceOCRedTextFiles, int nTopAPCnt, String sDefaultGpsCoordinates) {
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		int i;
		String[] fields = null;
		String sOCRedWord = "";
		String sApMac = "";
		int nIdx = 0;
		int nRss = 0;
		double fRss = 0.0;
		int nCnt;
		String sTotalContentLine = "";
		
		double fMeanRss, fStdRss, fLbRss, fUbRss;
		
		if (lststrOnePlaceOCRedTextFiles == null || 
			lststrOnePlaceOCRedTextFiles.size() == 0 ||
			lststrOnePlaceGpsApInfoFiles == null ||
			lststrOnePlaceGpsApInfoFiles.size() == 0) {
			return sLine;
		}
		
		//Merge OCRed text 
		//Duplicate words are removed
		m_lststrOnePlaceOCRedWords = new ArrayList<String>();
		
		for (String sOCRedTextFile : lststrOnePlaceOCRedTextFiles) {
			sTotalContentLine = "";
			
			try {
				fr = new FileReader(sOCRedTextFile);
				br = new BufferedReader(fr);
			
				while((sLine = br.readLine()) != null) {
					sTotalContentLine = sTotalContentLine + sLine + ",";				
				}
				
				fr.close();

				fields = Utility.getFields(sTotalContentLine);	
				for (int j=0; j<fields.length; j++) {
					sOCRedWord = fields[j].toLowerCase();
					if (m_lststrOnePlaceOCRedWords.contains(sOCRedWord) == false) {
						m_lststrOnePlaceOCRedWords.add(sOCRedWord);
					}
				}

			} catch (Exception e) {
				
			}
		}
		

		//Merge GPS coordinates, AP info
		m_lststrApMac = new ArrayList<String>();
		m_lstnApRss = new ArrayList<Integer>();
		m_lstnApMacOccurredCnt = new ArrayList<Integer>();
		
		m_lstlstfAPRss = new ArrayList<List<Integer>>();
		
		m_fGpsLat = 0;
		m_fGpsLong = 0;
		m_nGpsCnt = 0;
		
		for (String sGpsApInfoFile: lststrOnePlaceGpsApInfoFiles) {
			try {
				fr = new FileReader(sGpsApInfoFile);
				br = new BufferedReader(fr);
				
				while((sLine = br.readLine()) != null) {
					fields = Utility.getFields(sLine);
					
					if (fields.length < Utility.SENSING_FILE_GPS_LONG_IDX + 1) {   //In this line, no GPS information, no WiFi information
						continue;
					}
					
					if (fields[Utility.SENSING_FILE_GPS_LAT_IDX].length() > 0 && fields[Utility.SENSING_FILE_GPS_LONG_IDX].length() > 0) {
						m_nGpsCnt = m_nGpsCnt + 1;
						m_fGpsLat = m_fGpsLat + Double.valueOf(fields[Utility.SENSING_FILE_GPS_LAT_IDX]).doubleValue();
						m_fGpsLong = m_fGpsLong + Double.valueOf(fields[Utility.SENSING_FILE_GPS_LONG_IDX]).doubleValue();
					}
					
					if (fields.length < Utility.SENSING_FILE_WIFI_FIRST_TUPLE_END_IDX + 1) {  //No WiFi information
						continue;
					}

/*				
					//Tuple: <SSID, BSSID(MAC), RSS>
					for (int j=Utility.SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX; j+Utility.SENSING_FILE_WIFI_TUPLE_LEN-1<fields.length; j=j+Utility.SENSING_FILE_WIFI_TUPLE_LEN) {
						sApMac = fields[j+1].toLowerCase();
						nRss = Integer.valueOf(fields[j+2]).intValue();
*/
					
					//Tuple: <BSSID(MAC), RSS>
					for (int j=Utility.SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX; j+Utility.SENSING_FILE_WIFI_TUPLE_LEN-1<fields.length; j=j+Utility.SENSING_FILE_WIFI_TUPLE_LEN) {
						sApMac = fields[j].toLowerCase();
						nRss = Integer.valueOf(fields[j+1]).intValue();
												
						nIdx = m_lststrApMac.indexOf(sApMac);
						if (nIdx == -1) {
							m_lststrApMac.add(sApMac);
							//m_lstnApRss.add(nRss);
							m_lstnApMacOccurredCnt.add(1);
							List<Integer> lstRss = new ArrayList<Integer>();
							lstRss.add(nRss);
							m_lstlstfAPRss.add(lstRss);
						} else { //This MAC Exists
							nCnt = m_lstnApMacOccurredCnt.get(nIdx) + 1;
							m_lstnApMacOccurredCnt.set(nIdx, nCnt);
							
							List<Integer> lstRss = m_lstlstfAPRss.get(nIdx);
							lstRss.add(nRss);
							m_lstlstfAPRss.set(nIdx, lstRss);

							//nRss = nRss + m_lstnApRss.get(nIdx);
							//m_lstnApRss.set(nIdx, nRss);
						}
					}
					
					//Assume every file only contains one line ????
					//////////NOt assume only one line in this file break;
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
			
		}  //For
		
		
		if (m_nGpsCnt == 0) {
			String[] sarrLatLong = Utility.getFields(sDefaultGpsCoordinates);
			m_fGpsLat = Double.valueOf(sarrLatLong[0]).doubleValue();
			m_fGpsLong = Double.valueOf(sarrLatLong[1]).doubleValue();
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		} else {
			//Average GPS
			m_fGpsLat = m_fGpsLat/m_nGpsCnt;
			m_fGpsLong = m_fGpsLong/m_nGpsCnt;
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		}
		
		
//		//Take the average of RSS
//		m_lstfMeanApRss = new ArrayList<Double>();
//		for (i=0; i<m_lstnApRss.size(); i++) {
//			m_lstfMeanApRss.add(m_lstnApRss.get(i)*1.0 / m_lstnApMacOccurredCnt.get(i));
//		}
		m_lstfMeanApRss = new ArrayList<Double>();
		m_lstfStdApRss = new ArrayList<Double>();
		m_lstfLbApRss = new ArrayList<Double>();
		m_lstfUbApRss = new ArrayList<Double>();

		//Calcualte mean/std, lower bound, upper bound
		for (i=0; i<m_lststrApMac.size(); i++) {
			List<Integer> lstRss = m_lstlstfAPRss.get(i);
			fMeanRss = Utility.calculateMeanInteger(lstRss);			
			fMeanRss = Math.round(fMeanRss*100)/100.0;
			
			fStdRss = Utility.calculateStdInteger(lstRss);
			fStdRss = Math.round(fStdRss*100)/100.0;
			
			fLbRss = fMeanRss - fStdRss * Utility.RSS_STD_CNT;
			fUbRss = fMeanRss + fStdRss * Utility.RSS_STD_CNT;
			
			m_lstfMeanApRss.add(fMeanRss);
			m_lstfStdApRss.add(fStdRss);
			m_lstfLbApRss.add(fLbRss);
			m_lstfUbApRss.add(fUbRss);
		}
		
		
		//So far, the following information are ready for each AP MAC:
		//MAC, Occurred count, Mean, Std, Lower bound, Upper bound
		//Here below, need to order these MACs and only keep top N AP MAC
		//To eliminate the effect from mobile APs, the occurred count is taken into consideration 
		//First, high occurred count  --> high order;  if occurred counts are the same, high mean RSS gets higher order
		
		//Order Ap MAC based on RSS
		m_lststrOrderedApMac = new ArrayList<String>();
		m_lstfOrderedMeanApRss = new ArrayList<Double>();
		m_lstfOrderedStdApRss = new ArrayList<Double>();
		m_lstfOrderedLbApRss = new ArrayList<Double>();
		m_lstfOrderedUbApRss = new ArrayList<Double>();
		m_lstnOrderedApMacOccurredCnt = new ArrayList<Integer>();

		for (i=0; i<m_lstnApMacOccurredCnt.size(); i++) {
			if (i==0) {
				m_lststrOrderedApMac.add(m_lststrApMac.get(i));
				m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
				m_lstfOrderedStdApRss.add(m_lstfStdApRss.get(i));
				m_lstfOrderedLbApRss.add(m_lstfLbApRss.get(i));
				m_lstfOrderedUbApRss.add(m_lstfUbApRss.get(i));
				m_lstnOrderedApMacOccurredCnt.add(m_lstnApMacOccurredCnt.get(i));
			} else {
				nIdx = -1;
				nCnt = m_lstnApMacOccurredCnt.get(i);
				fMeanRss = m_lstfMeanApRss.get(i);
				
//				for (int j=0; j<m_lstnOrderedApMacOccurredCnt.size(); j++) {
//					if (nCnt > m_lstnOrderedApMacOccurredCnt.get(j).intValue()) {
//						nIdx = j;
//						break;
//					} else if (nCnt == m_lstnOrderedApMacOccurredCnt.get(j).intValue() && fMeanRss > m_lstfOrderedMeanApRss.get(j)) {
//						nIdx = j;
//						break;
//					}
//				}

				for (int j=0; j<m_lstnOrderedApMacOccurredCnt.size(); j++) {
					if (fMeanRss > m_lstfOrderedMeanApRss.get(j)) {
						nIdx = j;
						break;
					} else if (fMeanRss == m_lstfOrderedMeanApRss.get(j) && nCnt > m_lstnOrderedApMacOccurredCnt.get(j).intValue()) {
						nIdx = j;
						break;						
					}
				}
				
				
				if (nIdx == -1) {
					m_lststrOrderedApMac.add(m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
					m_lstfOrderedStdApRss.add(m_lstfStdApRss.get(i));
					m_lstfOrderedLbApRss.add(m_lstfLbApRss.get(i));
					m_lstfOrderedUbApRss.add(m_lstfUbApRss.get(i));
					m_lstnOrderedApMacOccurredCnt.add(m_lstnApMacOccurredCnt.get(i));					
				} else {
					m_lststrOrderedApMac.add(nIdx, m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(nIdx, m_lstfMeanApRss.get(i));
					m_lstfOrderedStdApRss.add(nIdx, m_lstfStdApRss.get(i));
					m_lstfOrderedLbApRss.add(nIdx, m_lstfLbApRss.get(i));
					m_lstfOrderedUbApRss.add(nIdx, m_lstfUbApRss.get(i));
					m_lstnOrderedApMacOccurredCnt.add(nIdx, m_lstnApMacOccurredCnt.get(i));					
				}
				
			}
		}
		
		
		
//Old version		
//		for (i=0; i<m_lstfMeanApRss.size(); i++) {
//			if (i==0) {
//				m_lststrOrderedApMac.add(m_lststrApMac.get(i));
//				m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
//			} else {
//				
//				nIdx = -1;
//				fRss = m_lstfMeanApRss.get(i);
//				for (int j=0; j<m_lstfOrderedMeanApRss.size(); j++) {
//					if (fRss > m_lstfOrderedMeanApRss.get(j)) {
//						nIdx = j;
//						break;
//					}
//				}
//				
//				if (nIdx == -1) {
//					m_lststrOrderedApMac.add(m_lststrApMac.get(i));
//					m_lstfOrderedMeanApRss.add(fRss);
//				} else {
//					m_lststrOrderedApMac.add(nIdx, m_lststrApMac.get(i));
//					m_lstfOrderedMeanApRss.add(nIdx, fRss);					
//				}
//				
//			}
//		}
		
		
		//By here, GPS are averaged, Ap MAC are merged, RSS are averaged and ordered, OCRed text are merged
		//Now need to write into file.
		//For AP info, only write up to 10 APs (i.e. top 10 of RSS)
		sLine = m_fGpsLat + "," + m_fGpsLong;
		
		if (m_lststrOrderedApMac.size() > nTopAPCnt) {
			nCnt = nTopAPCnt;
		} else {
			nCnt = m_lststrOrderedApMac.size();
		}
		
		//MAC, Lower bound, Up bound, Mean, Order
		for (i=0; i<nCnt; i++) {
			sLine = sLine + "," + m_lststrOrderedApMac.get(i) + "," + m_lstfOrderedLbApRss.get(i) + "," + m_lstfOrderedUbApRss.get(i) + "," + m_lstfOrderedMeanApRss.get(i) + "," + (i+1);
		}
		
		for (i=nCnt; i<nTopAPCnt; i++) {
			sLine = sLine + ",,,,,";
		}
		
		for (i=0; i<m_lststrOnePlaceOCRedWords.size(); i++) {
			sLine = sLine + "," + m_lststrOnePlaceOCRedWords.get(i);
		}
		
		//sLine = sLine + System.getProperty("line.separator");
		
		return sLine;
	}
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	//This function process all the GpsAP files (under \GpsAP\) and OCRedWords files (under \OCRedWords\) for one place.
	//The Fields indexes are set corresponding to the Sensing App result
	//For the OCRed Text, duplicated words are KEPT
	//Here Noun/Proper Name Extraction are applied.
	////////////////////////////////////////////////////////////////////////////////
	public String mergeOnePlaceDataSetDuplicateWordsWithNounExtraction(List<String> lststrOnePlaceGpsApInfoFiles, List<String> lststrOnePlaceOCRedTextFiles, int nTopAPCnt, List<String> lststrAllOCRedWords, String sDefaultGpsCoordinates) {
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		int i;
		String[] fields = null;
		String sApMac = "";
		int nIdx = 0;
		int nRss = 0;
		int nCnt;
		String sTotalContentLine = "";
		int nWordIdx = -1;
		int nTotalWordCnt = 0;
		double fWeight = 0.0; 
		
		double fMeanRss, fStdRss, fLbRss, fUbRss;
		
		KeywordExtraction locKeywordExtraction = new KeywordExtraction();

		if (lststrOnePlaceOCRedTextFiles == null || 
			lststrOnePlaceOCRedTextFiles.size() == 0 ||
			lststrOnePlaceGpsApInfoFiles == null ||
			lststrOnePlaceGpsApInfoFiles.size() == 0) {
			
			return sLine;
		}
		
		//Merge OCRed text 
		//Duplicate words are removed
		m_lststrOnePlaceOCRedWords = new ArrayList<String>();
		m_lstnOnePlaceOCRedWordsFreq = new ArrayList<Integer>();
		
		m_lstfOnePlaceOCRedWordsWeight = new ArrayList<Double>();
		
		sTotalContentLine = "";
		
		for (String sOCRedTextFile : lststrOnePlaceOCRedTextFiles) {
			
			try {
				fr = new FileReader(sOCRedTextFile);
				br = new BufferedReader(fr);
			
				while((sLine = br.readLine()) != null) {
					sTotalContentLine = sTotalContentLine + sLine + ",";				
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
			
		}
				
				
		//Keep all OCRed Words (no duplicated words, Noun/Proper names are not applied), these words are latter to be used to compare with the place name
		fields = Utility.splitString(sTotalContentLine);	
		for (int j=0; j<fields.length; j++) {
			String sOCRedWord = fields[j].toLowerCase();
			if (lststrAllOCRedWords.contains(sOCRedWord) == false) {
				lststrAllOCRedWords.add(sOCRedWord);
			}
		}
		
		//Get the non-duplicate noun/proper name
		m_lststrOnePlaceOCRedWords = locKeywordExtraction.extractKeywordFromContents(sTotalContentLine);
		
		//Initialize count for each word
		for (i=0; i<m_lststrOnePlaceOCRedWords.size(); i++) {
			m_lstnOnePlaceOCRedWordsFreq.add(0);
		}
		
		String sarrWord[] = Utility.splitString(sTotalContentLine);
		
		//Calculate word frequency
		for (i = 0; i<sarrWord.length; i++) {
			String sTmpWord = sarrWord[i];
			
			nWordIdx = -1;
			for (int k=0; k<m_lststrOnePlaceOCRedWords.size(); k++) {
				if (sTmpWord.compareToIgnoreCase(m_lststrOnePlaceOCRedWords.get(k)) == 0) {
					nWordIdx = k;
					break;
				}
			}
			
			if (nWordIdx != -1) {
				int nCurCount = m_lstnOnePlaceOCRedWordsFreq.get(nWordIdx);
				m_lstnOnePlaceOCRedWordsFreq.set(nWordIdx, nCurCount + 1);
			}
								
		}				

		
		nTotalWordCnt = 0;
		for (i=0; i<m_lstnOnePlaceOCRedWordsFreq.size(); i++) {
			nTotalWordCnt = nTotalWordCnt + m_lstnOnePlaceOCRedWordsFreq.get(i);
		}
		
		//Assign weight for OCRed words based on their frequencies
		for (i=0; i<m_lstnOnePlaceOCRedWordsFreq.size(); i++) {
			fWeight = Math.round(10000.0*m_lstnOnePlaceOCRedWordsFreq.get(i).intValue()/nTotalWordCnt)/10000.0;
			
			m_lstfOnePlaceOCRedWordsWeight.add(fWeight);
		}
		
		//Merge GPS coordinates, AP info
		m_lststrApMac = new ArrayList<String>();
		m_lstnApRss = new ArrayList<Integer>();
		m_lstnApMacOccurredCnt = new ArrayList<Integer>();
		
		m_lstlstfAPRss = new ArrayList<List<Integer>>();
		
		m_fGpsLat = 0;
		m_fGpsLong = 0;
		m_nGpsCnt = 0;
		
		for (String sGpsApInfoFile: lststrOnePlaceGpsApInfoFiles) {
			try {
				fr = new FileReader(sGpsApInfoFile);
				br = new BufferedReader(fr);
				
				while((sLine = br.readLine()) != null) {
					fields = Utility.getFields(sLine);
					if (fields.length < Utility.SENSING_FILE_GPS_LONG_IDX + 1) {   //In this line, no GPS information, no WiFi information
						continue;
					}
					
					if (fields[Utility.SENSING_FILE_GPS_LAT_IDX].length() > 0 && fields[Utility.SENSING_FILE_GPS_LONG_IDX].length() > 0) {
						m_nGpsCnt = m_nGpsCnt + 1;
						m_fGpsLat = m_fGpsLat + Double.valueOf(fields[Utility.SENSING_FILE_GPS_LAT_IDX]).doubleValue();
						m_fGpsLong = m_fGpsLong + Double.valueOf(fields[Utility.SENSING_FILE_GPS_LONG_IDX]).doubleValue();
					}
					
					if (fields.length < Utility.SENSING_FILE_WIFI_FIRST_TUPLE_END_IDX + 1) {  //No WiFi information
						continue;
					}

/* Modified on 4/2/2015
					//Tuple: <SSID, BSSID(MAC), RSS>
					for (int j=Utility.SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX; j+Utility.SENSING_FILE_WIFI_TUPLE_LEN-1<fields.length; j=j+Utility.SENSING_FILE_WIFI_TUPLE_LEN) {
						sApMac = fields[j+1].toLowerCase();
						nRss = Integer.valueOf(fields[j+2]).intValue();
*/
					
					//Tuple: <BSSID(MAC), RSS>
					for (int j=Utility.SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX; j+Utility.SENSING_FILE_WIFI_TUPLE_LEN-1<fields.length; j=j+Utility.SENSING_FILE_WIFI_TUPLE_LEN) {
						sApMac = fields[j].toLowerCase();
						nRss = Integer.valueOf(fields[j+1]).intValue();
						
						nIdx = m_lststrApMac.indexOf(sApMac);
						if (nIdx == -1) {
							m_lststrApMac.add(sApMac);
							//m_lstnApRss.add(nRss);
							m_lstnApMacOccurredCnt.add(1);
							List<Integer> lstRss = new ArrayList<Integer>();
							lstRss.add(nRss);
							m_lstlstfAPRss.add(lstRss);
						} else { //This MAC Exists
							nCnt = m_lstnApMacOccurredCnt.get(nIdx) + 1;
							m_lstnApMacOccurredCnt.set(nIdx, nCnt);
							
							List<Integer> lstRss = m_lstlstfAPRss.get(nIdx);
							lstRss.add(nRss);
							m_lstlstfAPRss.set(nIdx, lstRss);

							//nRss = nRss + m_lstnApRss.get(nIdx);
							//m_lstnApRss.set(nIdx, nRss);
						}
					}
					
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
			
		}  //For
		
		if (m_nGpsCnt == 0) {
			String[] sarrLatLong = Utility.getFields(sDefaultGpsCoordinates);
			m_fGpsLat = Double.valueOf(sarrLatLong[0]).doubleValue();
			m_fGpsLong = Double.valueOf(sarrLatLong[1]).doubleValue();
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		} else {
			//Average GPS
			m_fGpsLat = m_fGpsLat/m_nGpsCnt;
			m_fGpsLong = m_fGpsLong/m_nGpsCnt;
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		}
		
//		//Take the average of RSS
//		m_lstfMeanApRss = new ArrayList<Double>();
//		for (i=0; i<m_lstnApRss.size(); i++) {
//			m_lstfMeanApRss.add(m_lstnApRss.get(i)*1.0 / m_lstnApMacOccurredCnt.get(i));
//		}
		m_lstfMeanApRss = new ArrayList<Double>();
		m_lstfStdApRss = new ArrayList<Double>();
		m_lstfLbApRss = new ArrayList<Double>();
		m_lstfUbApRss = new ArrayList<Double>();

		//Calcualte mean/std, lower bound, upper bound
		for (i=0; i<m_lststrApMac.size(); i++) {
			List<Integer> lstRss = m_lstlstfAPRss.get(i);
			fMeanRss = Utility.calculateMeanInteger(lstRss);
			fMeanRss = Math.round(fMeanRss*100)/100.0;
			
			fStdRss = Utility.calculateStdInteger(lstRss);
			fStdRss = Math.round(fStdRss*100)/100.0;
			
			fLbRss = fMeanRss - fStdRss * Utility.RSS_STD_CNT;
			fUbRss = fMeanRss + fStdRss * Utility.RSS_STD_CNT;
			
			m_lstfMeanApRss.add(fMeanRss);
			m_lstfStdApRss.add(fStdRss);
			m_lstfLbApRss.add(fLbRss);
			m_lstfUbApRss.add(fUbRss);
		}
		
		
		//So far, the following information are ready for each AP MAC:
		//MAC, Occurred count, Mean, Std, Lower bound, Upper bound
		//Here below, need to order these MACs and only keep top N AP MAC
		//To eliminate the effect from mobile APs, the occurred count is taken into consideration 
		//First, high occurred count  --> high order;  if occurred counts are the same, high mean RSS gets higher order
		
		//Order Ap MAC based on RSS
		m_lststrOrderedApMac = new ArrayList<String>();
		m_lstfOrderedMeanApRss = new ArrayList<Double>();
		m_lstfOrderedStdApRss = new ArrayList<Double>();
		m_lstfOrderedLbApRss = new ArrayList<Double>();
		m_lstfOrderedUbApRss = new ArrayList<Double>();
		m_lstnOrderedApMacOccurredCnt = new ArrayList<Integer>();

		for (i=0; i<m_lstnApMacOccurredCnt.size(); i++) {
			if (i==0) {
				m_lststrOrderedApMac.add(m_lststrApMac.get(i));
				m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
				m_lstfOrderedStdApRss.add(m_lstfStdApRss.get(i));
				m_lstfOrderedLbApRss.add(m_lstfLbApRss.get(i));
				m_lstfOrderedUbApRss.add(m_lstfUbApRss.get(i));
				m_lstnOrderedApMacOccurredCnt.add(m_lstnApMacOccurredCnt.get(i));
			} else {
				nIdx = -1;
				nCnt = m_lstnApMacOccurredCnt.get(i);
				fMeanRss = m_lstfMeanApRss.get(i);
				
//				for (int j=0; j<m_lstnOrderedApMacOccurredCnt.size(); j++) {
//					if (nCnt > m_lstnOrderedApMacOccurredCnt.get(j).intValue()) {
//						nIdx = j;
//						break;
//					} else if (nCnt == m_lstnOrderedApMacOccurredCnt.get(j).intValue() && fMeanRss > m_lstfOrderedMeanApRss.get(j)) {
//						nIdx = j;
//						break;
//					}
//				}

				for (int j=0; j<m_lstnOrderedApMacOccurredCnt.size(); j++) {
					if (fMeanRss > m_lstfOrderedMeanApRss.get(j)) {
						nIdx = j;
						break;
					} else if (fMeanRss == m_lstfOrderedMeanApRss.get(j) && nCnt > m_lstnOrderedApMacOccurredCnt.get(j).intValue()) {
						nIdx = j;
						break;						
					}
				}
				
				if (nIdx == -1) {
					m_lststrOrderedApMac.add(m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
					m_lstfOrderedStdApRss.add(m_lstfStdApRss.get(i));
					m_lstfOrderedLbApRss.add(m_lstfLbApRss.get(i));
					m_lstfOrderedUbApRss.add(m_lstfUbApRss.get(i));
					m_lstnOrderedApMacOccurredCnt.add(m_lstnApMacOccurredCnt.get(i));					
				} else {
					m_lststrOrderedApMac.add(nIdx, m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(nIdx, m_lstfMeanApRss.get(i));
					m_lstfOrderedStdApRss.add(nIdx, m_lstfStdApRss.get(i));
					m_lstfOrderedLbApRss.add(nIdx, m_lstfLbApRss.get(i));
					m_lstfOrderedUbApRss.add(nIdx, m_lstfUbApRss.get(i));
					m_lstnOrderedApMacOccurredCnt.add(nIdx, m_lstnApMacOccurredCnt.get(i));					
				}
				
			}
		}
		
//Old version		
//		for (i=0; i<m_lstfMeanApRss.size(); i++) {
//			if (i==0) {
//				m_lststrOrderedApMac.add(m_lststrApMac.get(i));
//				m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
//			} else {
//				
//				nIdx = -1;
//				fRss = m_lstfMeanApRss.get(i);
//				for (int j=0; j<m_lstfOrderedMeanApRss.size(); j++) {
//					if (fRss > m_lstfOrderedMeanApRss.get(j)) {
//						nIdx = j;
//						break;
//					}
//				}
//				
//				if (nIdx == -1) {
//					m_lststrOrderedApMac.add(m_lststrApMac.get(i));
//					m_lstfOrderedMeanApRss.add(fRss);
//				} else {
//					m_lststrOrderedApMac.add(nIdx, m_lststrApMac.get(i));
//					m_lstfOrderedMeanApRss.add(nIdx, fRss);					
//				}
//				
//			}
//		}
		
		
		//By here, GPS are averaged, Ap MAC are merged, RSS are averaged and ordered, OCRed text are merged
		//Now need to write into file.
		//For AP info, only write up to 10 APs (i.e. top 10 of RSS)
		sLine = m_fGpsLat + "," + m_fGpsLong;
		
		if (m_lststrOrderedApMac.size() > nTopAPCnt) {
			nCnt = nTopAPCnt;
		} else {
			nCnt = m_lststrOrderedApMac.size();
		}
		
		//MAC, Lower bound, Up bound, Mean, Order
		for (i=0; i<nCnt; i++) {
			sLine = sLine + "," + m_lststrOrderedApMac.get(i) + "," + m_lstfOrderedLbApRss.get(i) + "," + m_lstfOrderedUbApRss.get(i) + "," + m_lstfOrderedMeanApRss.get(i) + "," + (i+1);
		}
		
		for (i=nCnt; i<nTopAPCnt; i++) {
			sLine = sLine + ",,,,,";
		}
		
		//////////////////////Extra: Sort OCRed words based on Frequency///////////////////
		List<String> lststrOnePlaceOCRedWords = new ArrayList<String>();  //Merged OCRed Text
		List<Integer> lstnOnePlaceOCRedWordsFreq = new ArrayList<Integer>();  //Frequency (=time of occurrence) of these words
		List<Double> lstfOnePlaceOCRedWordsWeight = new ArrayList<Double>();  //Weight of these words
		int nInsertPos = -1;
		
		for (i=0; i<m_lststrOnePlaceOCRedWords.size(); i++) {
			if (i == 0) {
				lststrOnePlaceOCRedWords.add(m_lststrOnePlaceOCRedWords.get(i));
				lstnOnePlaceOCRedWordsFreq.add(m_lstnOnePlaceOCRedWordsFreq.get(i));
				lstfOnePlaceOCRedWordsWeight.add(m_lstfOnePlaceOCRedWordsWeight.get(i));
			} else {
				nInsertPos = -1;
				for (int j=0; j<lstnOnePlaceOCRedWordsFreq.size(); j++) {
					if (m_lstnOnePlaceOCRedWordsFreq.get(i) > lstnOnePlaceOCRedWordsFreq.get(j)) {
						nInsertPos = j;
						break;
					}
				}
				
				if (nInsertPos != -1) {
					lststrOnePlaceOCRedWords.add(nInsertPos, m_lststrOnePlaceOCRedWords.get(i));
					lstnOnePlaceOCRedWordsFreq.add(nInsertPos, m_lstnOnePlaceOCRedWordsFreq.get(i));
					lstfOnePlaceOCRedWordsWeight.add(nInsertPos, m_lstfOnePlaceOCRedWordsWeight.get(i));
					
				} else {
					lststrOnePlaceOCRedWords.add(m_lststrOnePlaceOCRedWords.get(i));
					lstnOnePlaceOCRedWordsFreq.add(m_lstnOnePlaceOCRedWordsFreq.get(i));
					lstfOnePlaceOCRedWordsWeight.add(m_lstfOnePlaceOCRedWordsWeight.get(i));
					
				}
				
			}
		}
		
		for (i=0; i<lststrOnePlaceOCRedWords.size(); i++) {
			sLine = sLine + "," + lststrOnePlaceOCRedWords.get(i) + "," + lstfOnePlaceOCRedWordsWeight.get(i);
		}
		
		
		////////////////////////////////////////////////////////////////////////////////////
		

		//### Here below is Original, the OCRed words are not sorted based on their frequencies
		//Save OCR words and its occurrence count
//###	for (i=0; i<m_lststrOnePlaceOCRedWords.size(); i++) {
			//sLine = sLine + "," + m_lststrOnePlaceOCRedWords.get(i) + "," + m_lstnOnePlaceOCRedWordsFreq.get(i);
//###		sLine = sLine + "," + m_lststrOnePlaceOCRedWords.get(i) + "," + m_lstfOnePlaceOCRedWordsWeight.get(i);
//###	}
		
		//sLine = sLine + System.getProperty("line.separator");
		
		return sLine;
	}
	

	
	//########################################################################################################
	//########################################################################################################
	//########################################################################################################
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	//The Fields indexes are set corresponding to the Sensing App result
	//For the OCRed Text, duplicated words are KEPT
	//Noun/Proper name extraction is NOT done
	////////////////////////////////////////////////////////////////////////////////
	public String mergeOnePlaceDataSetDuplicateWordsWithoutNounExtraction(List<String> lststrOnePlaceGpsApInfoFiles, List<String> lststrOnePlaceOCRedTextFiles, int nTopAPCnt, String sDefaultGpsCoordinates) {
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		int i;
		String[] fields = null;
		String sOCRedWord = "";
		String sApMac = "";
		int nIdx = 0;
		int nRss = 0;
		double fRss = 0.0;
		int nCnt;
		String sTotalContentLine = "";
		int nWordIdx = 0;
		int nWordFreq = 0;
		int nTotalWordCnt = 0;
		double fWeight = 0.0; 
		
		double fMeanRss, fStdRss, fLbRss, fUbRss;
		
		if (lststrOnePlaceOCRedTextFiles == null || 
			lststrOnePlaceOCRedTextFiles.size() == 0 ||
			lststrOnePlaceGpsApInfoFiles == null ||
			lststrOnePlaceGpsApInfoFiles.size() == 0) {
			return sLine;
		}
		
		//Merge OCRed text 
		//Duplicate words are removed
		m_lststrOnePlaceOCRedWords = new ArrayList<String>();
		m_lstnOnePlaceOCRedWordsFreq = new ArrayList<Integer>();
		
		m_lstfOnePlaceOCRedWordsWeight = new ArrayList<Double>();
		
		for (String sOCRedTextFile : lststrOnePlaceOCRedTextFiles) {
			sTotalContentLine = "";
			
			try {
				fr = new FileReader(sOCRedTextFile);
				br = new BufferedReader(fr);
			
				while((sLine = br.readLine()) != null) {
					sTotalContentLine = sTotalContentLine + sLine + ",";				
				}
				
				fr.close();
				
				fields = Utility.getFields(sTotalContentLine);	
				for (int j=0; j<fields.length; j++) {
					sOCRedWord = fields[j];
					
					nWordIdx = -1;
					for (int k=0; k<m_lststrOnePlaceOCRedWords.size(); k++) {
						if (sOCRedWord.compareToIgnoreCase(m_lststrOnePlaceOCRedWords.get(k)) == 0) {
							nWordIdx = k;
							break;
						}
					}
					
					if (nWordIdx == -1) { //Not exist
						m_lststrOnePlaceOCRedWords.add(sOCRedWord);
						m_lstnOnePlaceOCRedWordsFreq.add(1);
					} else {
						nWordFreq = m_lstnOnePlaceOCRedWordsFreq.get(nWordIdx) + 1;
						m_lstnOnePlaceOCRedWordsFreq.set(nWordIdx, nWordFreq);
						
					}
					
				}

			} catch (Exception e) {
				
			}
		}
		
		
		nTotalWordCnt = 0;
		for (i=0; i<m_lstnOnePlaceOCRedWordsFreq.size(); i++) {
			nTotalWordCnt = nTotalWordCnt + m_lstnOnePlaceOCRedWordsFreq.get(i);
		}
		
		//Assign weight for OCRed words based on their frequencies
		for (i=0; i<m_lstnOnePlaceOCRedWordsFreq.size(); i++) {
			fWeight = Math.round(10000.0*m_lstnOnePlaceOCRedWordsFreq.get(i).intValue()/nTotalWordCnt)/10000.0;
			
			m_lstfOnePlaceOCRedWordsWeight.add(fWeight);
		}
		
		//Merge GPS coordinates, AP info
		m_lststrApMac = new ArrayList<String>();
		m_lstnApRss = new ArrayList<Integer>();
		m_lstnApMacOccurredCnt = new ArrayList<Integer>();
		
		m_lstlstfAPRss = new ArrayList<List<Integer>>();
		
		m_fGpsLat = 0;
		m_fGpsLong = 0;
		m_nGpsCnt = 0;
		
		for (String sGpsApInfoFile: lststrOnePlaceGpsApInfoFiles) {
			try {
				fr = new FileReader(sGpsApInfoFile);
				br = new BufferedReader(fr);
				
				while((sLine = br.readLine()) != null) {
					fields = Utility.getFields(sLine);
					if (fields.length < Utility.SENSING_FILE_GPS_LONG_IDX + 1) {   //In this line, no GPS information, no WiFi information
						continue;
					}
					
					if (fields[Utility.SENSING_FILE_GPS_LAT_IDX].length() > 0 && fields[Utility.SENSING_FILE_GPS_LONG_IDX].length() > 0) {
						m_nGpsCnt = m_nGpsCnt + 1;
						m_fGpsLat = m_fGpsLat + Double.valueOf(fields[Utility.SENSING_FILE_GPS_LAT_IDX]).doubleValue();
						m_fGpsLong = m_fGpsLong + Double.valueOf(fields[Utility.SENSING_FILE_GPS_LONG_IDX]).doubleValue();
					}
					
					if (fields.length < Utility.SENSING_FILE_WIFI_FIRST_TUPLE_END_IDX + 1) {  //No WiFi information
						continue;
					}

/*	Modified on 4/2/2015			
					//Tuple: <SSID, BSSID(MAC), RSS>
					for (int j=Utility.SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX; j+Utility.SENSING_FILE_WIFI_TUPLE_LEN-1<fields.length; j=j+Utility.SENSING_FILE_WIFI_TUPLE_LEN) {
						sApMac = fields[j+1].toLowerCase();
						nRss = Integer.valueOf(fields[j+2]).intValue();
*/
					//Tuple: <BSSID(MAC), RSS>
					for (int j=Utility.SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX; j+Utility.SENSING_FILE_WIFI_TUPLE_LEN-1<fields.length; j=j+Utility.SENSING_FILE_WIFI_TUPLE_LEN) {
						sApMac = fields[j].toLowerCase();
						nRss = Integer.valueOf(fields[j+1]).intValue();
						
						nIdx = m_lststrApMac.indexOf(sApMac);
						if (nIdx == -1) {
							m_lststrApMac.add(sApMac);
							//m_lstnApRss.add(nRss);
							m_lstnApMacOccurredCnt.add(1);
							List<Integer> lstRss = new ArrayList<Integer>();
							lstRss.add(nRss);
							m_lstlstfAPRss.add(lstRss);
						} else { //This MAC Exists
							nCnt = m_lstnApMacOccurredCnt.get(nIdx) + 1;
							m_lstnApMacOccurredCnt.set(nIdx, nCnt);
							
							List<Integer> lstRss = m_lstlstfAPRss.get(nIdx);
							lstRss.add(nRss);
							m_lstlstfAPRss.set(nIdx, lstRss);

							//nRss = nRss + m_lstnApRss.get(nIdx);
							//m_lstnApRss.set(nIdx, nRss);
						}
					}
					
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
			
		}  //For
		
		if (m_nGpsCnt == 0) {
			String[] sarrLatLong = Utility.getFields(sDefaultGpsCoordinates);
			m_fGpsLat = Double.valueOf(sarrLatLong[0]).doubleValue();
			m_fGpsLong = Double.valueOf(sarrLatLong[1]).doubleValue();
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		} else {
			//Average GPS
			m_fGpsLat = m_fGpsLat/m_nGpsCnt;
			m_fGpsLong = m_fGpsLong/m_nGpsCnt;
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		}
		
//		//Take the average of RSS
//		m_lstfMeanApRss = new ArrayList<Double>();
//		for (i=0; i<m_lstnApRss.size(); i++) {
//			m_lstfMeanApRss.add(m_lstnApRss.get(i)*1.0 / m_lstnApMacOccurredCnt.get(i));
//		}
		m_lstfMeanApRss = new ArrayList<Double>();
		m_lstfStdApRss = new ArrayList<Double>();
		m_lstfLbApRss = new ArrayList<Double>();
		m_lstfUbApRss = new ArrayList<Double>();

		//Calcualte mean/std, lower bound, upper bound
		for (i=0; i<m_lststrApMac.size(); i++) {
			List<Integer> lstRss = m_lstlstfAPRss.get(i);
			fMeanRss = Utility.calculateMeanInteger(lstRss);
			fMeanRss = Math.round(fMeanRss*100)/100.0;
			
			fStdRss = Utility.calculateStdInteger(lstRss);
			fStdRss = Math.round(fStdRss*100)/100.0;
			
			fLbRss = fMeanRss - fStdRss * Utility.RSS_STD_CNT;
			fUbRss = fMeanRss + fStdRss * Utility.RSS_STD_CNT;
			
			m_lstfMeanApRss.add(fMeanRss);
			m_lstfStdApRss.add(fStdRss);
			m_lstfLbApRss.add(fLbRss);
			m_lstfUbApRss.add(fUbRss);
		}
		
		
		//So far, the following information are ready for each AP MAC:
		//MAC, Occurred count, Mean, Std, Lower bound, Upper bound
		//Here below, need to order these MACs and only keep top N AP MAC
		//To eliminate the effect from mobile APs, the occurred count is taken into consideration 
		//First, high occurred count  --> high order;  if occurred counts are the same, high mean RSS gets higher order
		
		//Order Ap MAC based on RSS
		m_lststrOrderedApMac = new ArrayList<String>();
		m_lstfOrderedMeanApRss = new ArrayList<Double>();
		m_lstfOrderedStdApRss = new ArrayList<Double>();
		m_lstfOrderedLbApRss = new ArrayList<Double>();
		m_lstfOrderedUbApRss = new ArrayList<Double>();
		m_lstnOrderedApMacOccurredCnt = new ArrayList<Integer>();

		for (i=0; i<m_lstnApMacOccurredCnt.size(); i++) {
			if (i==0) {
				m_lststrOrderedApMac.add(m_lststrApMac.get(i));
				m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
				m_lstfOrderedStdApRss.add(m_lstfStdApRss.get(i));
				m_lstfOrderedLbApRss.add(m_lstfLbApRss.get(i));
				m_lstfOrderedUbApRss.add(m_lstfUbApRss.get(i));
				m_lstnOrderedApMacOccurredCnt.add(m_lstnApMacOccurredCnt.get(i));
			} else {
				nIdx = -1;
				nCnt = m_lstnApMacOccurredCnt.get(i);
				fMeanRss = m_lstfMeanApRss.get(i);
				
//				for (int j=0; j<m_lstnOrderedApMacOccurredCnt.size(); j++) {
//					if (nCnt > m_lstnOrderedApMacOccurredCnt.get(j).intValue()) {
//						nIdx = j;
//						break;
//					} else if (nCnt == m_lstnOrderedApMacOccurredCnt.get(j).intValue() && fMeanRss > m_lstfOrderedMeanApRss.get(j)) {
//						nIdx = j;
//						break;
//					}
//				}

				for (int j=0; j<m_lstnOrderedApMacOccurredCnt.size(); j++) {
					if (fMeanRss > m_lstfOrderedMeanApRss.get(j)) {
						nIdx = j;
						break;
					} else if (fMeanRss == m_lstfOrderedMeanApRss.get(j) && nCnt > m_lstnOrderedApMacOccurredCnt.get(j).intValue()) {
						nIdx = j;
						break;						
					}
				}
				
				if (nIdx == -1) {
					m_lststrOrderedApMac.add(m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
					m_lstfOrderedStdApRss.add(m_lstfStdApRss.get(i));
					m_lstfOrderedLbApRss.add(m_lstfLbApRss.get(i));
					m_lstfOrderedUbApRss.add(m_lstfUbApRss.get(i));
					m_lstnOrderedApMacOccurredCnt.add(m_lstnApMacOccurredCnt.get(i));					
				} else {
					m_lststrOrderedApMac.add(nIdx, m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(nIdx, m_lstfMeanApRss.get(i));
					m_lstfOrderedStdApRss.add(nIdx, m_lstfStdApRss.get(i));
					m_lstfOrderedLbApRss.add(nIdx, m_lstfLbApRss.get(i));
					m_lstfOrderedUbApRss.add(nIdx, m_lstfUbApRss.get(i));
					m_lstnOrderedApMacOccurredCnt.add(nIdx, m_lstnApMacOccurredCnt.get(i));					
				}
				
			}
		}
		
		
		
//Old version		
//		for (i=0; i<m_lstfMeanApRss.size(); i++) {
//			if (i==0) {
//				m_lststrOrderedApMac.add(m_lststrApMac.get(i));
//				m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
//			} else {
//				
//				nIdx = -1;
//				fRss = m_lstfMeanApRss.get(i);
//				for (int j=0; j<m_lstfOrderedMeanApRss.size(); j++) {
//					if (fRss > m_lstfOrderedMeanApRss.get(j)) {
//						nIdx = j;
//						break;
//					}
//				}
//				
//				if (nIdx == -1) {
//					m_lststrOrderedApMac.add(m_lststrApMac.get(i));
//					m_lstfOrderedMeanApRss.add(fRss);
//				} else {
//					m_lststrOrderedApMac.add(nIdx, m_lststrApMac.get(i));
//					m_lstfOrderedMeanApRss.add(nIdx, fRss);					
//				}
//				
//			}
//		}
		
		
		//By here, GPS are averaged, Ap MAC are merged, RSS are averaged and ordered, OCRed text are merged
		//Now need to write into file.
		//For AP info, only write up to 10 APs (i.e. top 10 of RSS)
		sLine = m_fGpsLat + "," + m_fGpsLong;
		
		if (m_lststrOrderedApMac.size() > nTopAPCnt) {
			nCnt = nTopAPCnt;
		} else {
			nCnt = m_lststrOrderedApMac.size();
		}
		
		//MAC, Lower bound, Up bound, Mean, Order
		for (i=0; i<nCnt; i++) {
			sLine = sLine + "," + m_lststrOrderedApMac.get(i) + "," + m_lstfOrderedLbApRss.get(i) + "," + m_lstfOrderedUbApRss.get(i) + "," + m_lstfOrderedMeanApRss.get(i) + "," + (i+1);
		}
		
		for (i=nCnt; i<nTopAPCnt; i++) {
			sLine = sLine + ",,,,,";
		}
		
		
		//////////////////////Extra: Sort OCRed words based on Frequency///////////////////
		List<String> lststrOnePlaceOCRedWords = new ArrayList<String>();  //Merged OCRed Text
		List<Integer> lstnOnePlaceOCRedWordsFreq = new ArrayList<Integer>();  //Frequency (=time of occurrence) of these words
		List<Double> lstfOnePlaceOCRedWordsWeight = new ArrayList<Double>();  //Weight of these words
		int nInsertPos = -1;
		
		for (i=0; i<m_lststrOnePlaceOCRedWords.size(); i++) {
			if (i == 0) {
				lststrOnePlaceOCRedWords.add(m_lststrOnePlaceOCRedWords.get(i));
				lstnOnePlaceOCRedWordsFreq.add(m_lstnOnePlaceOCRedWordsFreq.get(i));
				lstfOnePlaceOCRedWordsWeight.add(m_lstfOnePlaceOCRedWordsWeight.get(i));
			} else {
				nInsertPos = -1;
				for (int j=0; j<lstnOnePlaceOCRedWordsFreq.size(); j++) {
					if (m_lstnOnePlaceOCRedWordsFreq.get(i) > lstnOnePlaceOCRedWordsFreq.get(j)) {
						nInsertPos = j;
						break;
					}
				}
				
				if (nInsertPos != -1) {
					lststrOnePlaceOCRedWords.add(nInsertPos, m_lststrOnePlaceOCRedWords.get(i));
					lstnOnePlaceOCRedWordsFreq.add(nInsertPos, m_lstnOnePlaceOCRedWordsFreq.get(i));
					lstfOnePlaceOCRedWordsWeight.add(nInsertPos, m_lstfOnePlaceOCRedWordsWeight.get(i));
					
				} else {
					lststrOnePlaceOCRedWords.add(m_lststrOnePlaceOCRedWords.get(i));
					lstnOnePlaceOCRedWordsFreq.add(m_lstnOnePlaceOCRedWordsFreq.get(i));
					lstfOnePlaceOCRedWordsWeight.add(m_lstfOnePlaceOCRedWordsWeight.get(i));
					
				}
				
			}
		}
		
		for (i=0; i<lststrOnePlaceOCRedWords.size(); i++) {
			sLine = sLine + "," + lststrOnePlaceOCRedWords.get(i) + "," + lstfOnePlaceOCRedWordsWeight.get(i);
		}
		
		
		////////////////////////////////////////////////////////////////////////////////////
		
		//### Here below is Original, the OCRed words are not sorted based on their frequencies
		//Save OCR words and its occurrence count
//###		for (i=0; i<m_lststrOnePlaceOCRedWords.size(); i++) {
			//sLine = sLine + "," + m_lststrOnePlaceOCRedWords.get(i) + "," + m_lstnOnePlaceOCRedWordsFreq.get(i);
//###			sLine = sLine + "," + m_lststrOnePlaceOCRedWords.get(i) + "," + m_lstfOnePlaceOCRedWordsWeight.get(i);
//###		}
		
		//sLine = sLine + System.getProperty("line.separator");
		
		return sLine;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String mergeOnePlaceDataSetDuplicateWordsWithNounExtraction_Temp(List<String> lststrOnePlaceGpsApInfoFiles, 
																			List<String> lststrOnePlaceOCRedTextFiles, 
																			int nTopAPCnt, List<String> lststrAllOCRedWords,
																			List<String> lststrOnePlaceOCRedWords,
																			List<Integer> lstnOnePlaceOCRedWordsFreq,
																			List<Double> lstfOnePlaceOCRedWordsWeight,
																			String sDefaultGpsCoordinates) {
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		int i;
		String[] fields = null;
		String sApMac = "";
		int nIdx = 0;
		int nRss = 0;
		int nCnt;
		String sTotalContentLine = "";
		int nWordIdx = -1;
		int nTotalWordCnt = 0;
		double fWeight = 0.0; 
		
		double fMeanRss, fStdRss, fLbRss, fUbRss;
		
		KeywordExtraction locKeywordExtraction = new KeywordExtraction();

		if (lststrOnePlaceOCRedTextFiles == null || 
			lststrOnePlaceOCRedTextFiles.size() == 0 ||
			lststrOnePlaceGpsApInfoFiles == null ||
			lststrOnePlaceGpsApInfoFiles.size() == 0) {
			
			return sLine;
		}
		
		//Merge OCRed text 
		//Duplicate words are removed
		m_lststrOnePlaceOCRedWords = new ArrayList<String>();
		m_lstnOnePlaceOCRedWordsFreq = new ArrayList<Integer>();
		
		m_lstfOnePlaceOCRedWordsWeight = new ArrayList<Double>();
		
		sTotalContentLine = "";
		
		for (String sOCRedTextFile : lststrOnePlaceOCRedTextFiles) {
				
			try {
				fr = new FileReader(sOCRedTextFile);
				br = new BufferedReader(fr);
			
				while((sLine = br.readLine()) != null) {
					
					sLine = sLine.trim();
					if (sLine.startsWith("%")) continue;    // treat the beginning % as comment in OCRed text file
					
					sTotalContentLine = sTotalContentLine + sLine + ",";				
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
		}  //For
								
				
		//Keep all OCRed Words (no duplicated words, Noun/Proper names are not applied), these words are latter to be used to compare with the place name
		if (Utility.MATCH_WITH_PLACENAME_WITH_PHRASE == false) {
			fields = Utility.splitString(sTotalContentLine);    //Save OCRed Text by words
		} else {
			fields = Utility.splitStringPhrase(sTotalContentLine);  //Save OCRed Text by Phrase
		}
		
		for (int j=0; j<fields.length; j++) {
			String sOCRedWord = fields[j].toLowerCase().trim();
			if (lststrAllOCRedWords.contains(sOCRedWord) == false) {
				lststrAllOCRedWords.add(sOCRedWord);
			}
		}
		
		//Get the non-duplicate noun/proper name
		m_lststrOnePlaceOCRedWords = locKeywordExtraction.extractKeywordFromContents(sTotalContentLine);
		
		//Initialize count for each word
		for (i=0; i<m_lststrOnePlaceOCRedWords.size(); i++) {
			m_lstnOnePlaceOCRedWordsFreq.add(0);
		}
		
		String sarrWord[] = Utility.splitString(sTotalContentLine);
		
		//Calculate word frequency
		for (i = 0; i<sarrWord.length; i++) {
			String sTmpWord = sarrWord[i];
			
			nWordIdx = -1;
			for (int k=0; k<m_lststrOnePlaceOCRedWords.size(); k++) {
				if (sTmpWord.compareToIgnoreCase(m_lststrOnePlaceOCRedWords.get(k)) == 0) {
					nWordIdx = k;
					break;
				}
			}
			
			if (nWordIdx != -1) {
				int nCurCount = m_lstnOnePlaceOCRedWordsFreq.get(nWordIdx);
				m_lstnOnePlaceOCRedWordsFreq.set(nWordIdx, nCurCount + 1);
			}
								
		}				


		nTotalWordCnt = 0;
		for (i=0; i<m_lstnOnePlaceOCRedWordsFreq.size(); i++) {
			nTotalWordCnt = nTotalWordCnt + m_lstnOnePlaceOCRedWordsFreq.get(i);
		}
		
		int nMaxFreq = Utility.getMaxInt(m_lstnOnePlaceOCRedWordsFreq);
		
		//Assign weight for OCRed words based on their frequencies
		for (i=0; i<m_lstnOnePlaceOCRedWordsFreq.size(); i++) {
			if (nMaxFreq > 0) {
				if (Utility.USE_TF_IDF_WEIGHT) {
					fWeight = 0.5 + 0.5*m_lstnOnePlaceOCRedWordsFreq.get(i).intValue()/nMaxFreq;
					fWeight = Math.round(10000.0*fWeight)/10000.0;
				} else {
					fWeight = Math.round(10000.0*m_lstnOnePlaceOCRedWordsFreq.get(i).intValue()/nTotalWordCnt)/10000.0;
				}
			} else {
				fWeight = 0.0;
			}
			
			m_lstfOnePlaceOCRedWordsWeight.add(fWeight);
		}
		
		//Merge GPS coordinates, AP info
		m_lststrApMac = new ArrayList<String>();
		m_lstnApRss = new ArrayList<Integer>();
		m_lstnApMacOccurredCnt = new ArrayList<Integer>();
		
		m_lstlstfAPRss = new ArrayList<List<Integer>>();
		
		m_fGpsLat = 0;
		m_fGpsLong = 0;
		m_nGpsCnt = 0;
		
		for (String sGpsApInfoFile: lststrOnePlaceGpsApInfoFiles) {
			try {
				fr = new FileReader(sGpsApInfoFile);
				br = new BufferedReader(fr);
				
				while((sLine = br.readLine()) != null) {
					fields = Utility.getFields(sLine);
					if (fields.length < Utility.SENSING_FILE_GPS_LONG_IDX + 1) {   //In this line, no GPS information, no WiFi information
						continue;
					}
					
					if (fields[Utility.SENSING_FILE_GPS_LAT_IDX].length() > 0 && fields[Utility.SENSING_FILE_GPS_LONG_IDX].length() > 0) {
						m_nGpsCnt = m_nGpsCnt + 1;
						m_fGpsLat = m_fGpsLat + Double.valueOf(fields[Utility.SENSING_FILE_GPS_LAT_IDX]).doubleValue();
						m_fGpsLong = m_fGpsLong + Double.valueOf(fields[Utility.SENSING_FILE_GPS_LONG_IDX]).doubleValue();
					}
					
					if (fields.length < Utility.SENSING_FILE_WIFI_FIRST_TUPLE_END_IDX + 1) {  //No WiFi information
						continue;
					}

/* Modified on 4/2/2015			
					//Tuple: <SSID, BSSID(MAC), RSS>
					for (int j=Utility.SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX; j+Utility.SENSING_FILE_WIFI_TUPLE_LEN-1<fields.length; j=j+Utility.SENSING_FILE_WIFI_TUPLE_LEN) {
						sApMac = fields[j+1].toLowerCase();
						nRss = Integer.valueOf(fields[j+2]).intValue();
/*/
					
					//Tuple: <BSSID(MAC), RSS>
					for (int j=Utility.SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX; j+Utility.SENSING_FILE_WIFI_TUPLE_LEN-1<fields.length; j=j+Utility.SENSING_FILE_WIFI_TUPLE_LEN) {
						sApMac = fields[j].toLowerCase();
						nRss = Integer.valueOf(fields[j+1]).intValue();
						
						nIdx = m_lststrApMac.indexOf(sApMac);
						if (nIdx == -1) {
							m_lststrApMac.add(sApMac);
							//m_lstnApRss.add(nRss);
							m_lstnApMacOccurredCnt.add(1);
							List<Integer> lstRss = new ArrayList<Integer>();
							lstRss.add(nRss);
							m_lstlstfAPRss.add(lstRss);
						} else { //This MAC Exists
							nCnt = m_lstnApMacOccurredCnt.get(nIdx) + 1;
							m_lstnApMacOccurredCnt.set(nIdx, nCnt);
							
							List<Integer> lstRss = m_lstlstfAPRss.get(nIdx);
							lstRss.add(nRss);
							m_lstlstfAPRss.set(nIdx, lstRss);

							//nRss = nRss + m_lstnApRss.get(nIdx);
							//m_lstnApRss.set(nIdx, nRss);
						}
					}
					
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
			
		}  //For
		
		
		if (m_nGpsCnt == 0) {
			String[] sarrLatLong = Utility.getFields(sDefaultGpsCoordinates);
			m_fGpsLat = Double.valueOf(sarrLatLong[0]).doubleValue();
			m_fGpsLong = Double.valueOf(sarrLatLong[1]).doubleValue();
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		} else {
			//Average GPS
			m_fGpsLat = m_fGpsLat/m_nGpsCnt;
			m_fGpsLong = m_fGpsLong/m_nGpsCnt;
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		}
		
//		//Take the average of RSS
//		m_lstfMeanApRss = new ArrayList<Double>();
//		for (i=0; i<m_lstnApRss.size(); i++) {
//			m_lstfMeanApRss.add(m_lstnApRss.get(i)*1.0 / m_lstnApMacOccurredCnt.get(i));
//		}
		m_lstfMeanApRss = new ArrayList<Double>();
		m_lstfStdApRss = new ArrayList<Double>();
		m_lstfLbApRss = new ArrayList<Double>();
		m_lstfUbApRss = new ArrayList<Double>();

		//Calcualte mean/std, lower bound, upper bound
		for (i=0; i<m_lststrApMac.size(); i++) {
			List<Integer> lstRss = m_lstlstfAPRss.get(i);
			fMeanRss = Utility.calculateMeanInteger(lstRss);
			fMeanRss = Math.round(fMeanRss*100)/100.0;
			
			fStdRss = Utility.calculateStdInteger(lstRss);
			fStdRss = Math.round(fStdRss*100)/100.0;
			
			fLbRss = fMeanRss - fStdRss * Utility.RSS_STD_CNT;
			fUbRss = fMeanRss + fStdRss * Utility.RSS_STD_CNT;
			
			m_lstfMeanApRss.add(fMeanRss);
			m_lstfStdApRss.add(fStdRss);
			m_lstfLbApRss.add(fLbRss);
			m_lstfUbApRss.add(fUbRss);
		}
		
		
		//So far, the following information are ready for each AP MAC:
		//MAC, Occurred count, Mean, Std, Lower bound, Upper bound
		//Here below, need to order these MACs and only keep top N AP MAC
		//To eliminate the effect from mobile APs, the occurred count is taken into consideration 
		//First, high occurred count  --> high order;  if occurred counts are the same, high mean RSS gets higher order
		
		//Order Ap MAC based on RSS
		m_lststrOrderedApMac = new ArrayList<String>();
		m_lstfOrderedMeanApRss = new ArrayList<Double>();
		m_lstfOrderedStdApRss = new ArrayList<Double>();
		m_lstfOrderedLbApRss = new ArrayList<Double>();
		m_lstfOrderedUbApRss = new ArrayList<Double>();
		m_lstnOrderedApMacOccurredCnt = new ArrayList<Integer>();

		for (i=0; i<m_lstnApMacOccurredCnt.size(); i++) {
			if (i==0) {
				m_lststrOrderedApMac.add(m_lststrApMac.get(i));
				m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
				m_lstfOrderedStdApRss.add(m_lstfStdApRss.get(i));
				m_lstfOrderedLbApRss.add(m_lstfLbApRss.get(i));
				m_lstfOrderedUbApRss.add(m_lstfUbApRss.get(i));
				m_lstnOrderedApMacOccurredCnt.add(m_lstnApMacOccurredCnt.get(i));
			} else {
				nIdx = -1;
				nCnt = m_lstnApMacOccurredCnt.get(i);
				fMeanRss = m_lstfMeanApRss.get(i);
				
//				for (int j=0; j<m_lstnOrderedApMacOccurredCnt.size(); j++) {
//					if (nCnt > m_lstnOrderedApMacOccurredCnt.get(j).intValue()) {
//						nIdx = j;
//						break;
//					} else if (nCnt == m_lstnOrderedApMacOccurredCnt.get(j).intValue() && fMeanRss > m_lstfOrderedMeanApRss.get(j)) {
//						nIdx = j;
//						break;
//					}
//				}

				for (int j=0; j<m_lstnOrderedApMacOccurredCnt.size(); j++) {
					if (fMeanRss > m_lstfOrderedMeanApRss.get(j)) {
						nIdx = j;
						break;
					} else if (fMeanRss == m_lstfOrderedMeanApRss.get(j) && nCnt > m_lstnOrderedApMacOccurredCnt.get(j).intValue()) {
						nIdx = j;
						break;						
					}
				}
				
				if (nIdx == -1) {
					m_lststrOrderedApMac.add(m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
					m_lstfOrderedStdApRss.add(m_lstfStdApRss.get(i));
					m_lstfOrderedLbApRss.add(m_lstfLbApRss.get(i));
					m_lstfOrderedUbApRss.add(m_lstfUbApRss.get(i));
					m_lstnOrderedApMacOccurredCnt.add(m_lstnApMacOccurredCnt.get(i));					
				} else {
					m_lststrOrderedApMac.add(nIdx, m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(nIdx, m_lstfMeanApRss.get(i));
					m_lstfOrderedStdApRss.add(nIdx, m_lstfStdApRss.get(i));
					m_lstfOrderedLbApRss.add(nIdx, m_lstfLbApRss.get(i));
					m_lstfOrderedUbApRss.add(nIdx, m_lstfUbApRss.get(i));
					m_lstnOrderedApMacOccurredCnt.add(nIdx, m_lstnApMacOccurredCnt.get(i));					
				}
				
			}
		}
				
		
		//By here, GPS are averaged, Ap MAC are merged, RSS are averaged and ordered, OCRed text are merged
		//Now need to write into file.
		//For AP info, only write up to 10 APs (i.e. top 10 of RSS)
		sLine = m_fGpsLat + "," + m_fGpsLong;
		
		if (m_lststrOrderedApMac.size() > nTopAPCnt) {
			nCnt = nTopAPCnt;
		} else {
			nCnt = m_lststrOrderedApMac.size();
		}
		
		//MAC, Lower bound, Up bound, Mean, Order
		for (i=0; i<nCnt; i++) {
			sLine = sLine + "," + m_lststrOrderedApMac.get(i) + "," + m_lstfOrderedLbApRss.get(i) + "," + m_lstfOrderedUbApRss.get(i) + "," + m_lstfOrderedMeanApRss.get(i) + "," + (i+1);
		}
		
		for (i=nCnt; i<nTopAPCnt; i++) {
			sLine = sLine + ",,,,,";
		}
		
		//////////////////////Extra: Sort OCRed words based on Frequency///////////////////
//##Original		List<String> lststrOnePlaceOCRedWords = new ArrayList<String>();  //Merged OCRed Text
//##Original		List<Integer> lstnOnePlaceOCRedWordsFreq = new ArrayList<Integer>();  //Frequency (=time of occurrence) of these words
//##Original		List<Double> lstfOnePlaceOCRedWordsWeight = new ArrayList<Double>();  //Weight of these words
		int nInsertPos = -1;
		
		for (i=0; i<m_lststrOnePlaceOCRedWords.size(); i++) {
			if (i == 0) {
				lststrOnePlaceOCRedWords.add(m_lststrOnePlaceOCRedWords.get(i));
				lstnOnePlaceOCRedWordsFreq.add(m_lstnOnePlaceOCRedWordsFreq.get(i));
				lstfOnePlaceOCRedWordsWeight.add(m_lstfOnePlaceOCRedWordsWeight.get(i));
			} else {
				nInsertPos = -1;
				for (int j=0; j<lstnOnePlaceOCRedWordsFreq.size(); j++) {
					if (m_lstnOnePlaceOCRedWordsFreq.get(i) > lstnOnePlaceOCRedWordsFreq.get(j)) {
						nInsertPos = j;
						break;
					}
				}
				
				if (nInsertPos != -1) {
					lststrOnePlaceOCRedWords.add(nInsertPos, m_lststrOnePlaceOCRedWords.get(i));
					lstnOnePlaceOCRedWordsFreq.add(nInsertPos, m_lstnOnePlaceOCRedWordsFreq.get(i));
					lstfOnePlaceOCRedWordsWeight.add(nInsertPos, m_lstfOnePlaceOCRedWordsWeight.get(i));
					
				} else {
					lststrOnePlaceOCRedWords.add(m_lststrOnePlaceOCRedWords.get(i));
					lstnOnePlaceOCRedWordsFreq.add(m_lstnOnePlaceOCRedWordsFreq.get(i));
					lstfOnePlaceOCRedWordsWeight.add(m_lstfOnePlaceOCRedWordsWeight.get(i));
					
				}
				
			}
		}
		
		for (i=0; i<lststrOnePlaceOCRedWords.size(); i++) {
			sLine = sLine + "," + lststrOnePlaceOCRedWords.get(i) + "," + lstfOnePlaceOCRedWordsWeight.get(i);
		}
		
				
		return sLine;
	}
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String mergeOnePlaceDataSetDuplicateWordsWithNounExtraction_Temp_withFrameSample(List<String> lststrOnePlaceGpsApInfoFiles, 
																			List<String> lststrOnePlaceOCRedTextFiles, 
																			int nTopAPCnt, List<String> lststrAllOCRedWords,
																			List<String> lststrOnePlaceOCRedWords,
																			List<Integer> lstnOnePlaceOCRedWordsFreq,
																			List<Double> lstfOnePlaceOCRedWordsWeight,
																			String sDefaultGpsCoordinates, List<Integer> lstSelectedFrames) {
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		int i;
		String[] fields = null;
		String sApMac = "";
		int nIdx = 0;
		int nRss = 0;
		int nCnt;
		String sTotalContentLine = "";
		int nWordIdx = -1;
		int nTotalWordCnt = 0;
		double fWeight = 0.0; 
		
		double fMeanRss, fStdRss, fLbRss, fUbRss;
		int nTotalFrames = 0;
//		List<Integer> lstSelectedFrames = new ArrayList<Integer>();
		int nFrameIdx = 0;
		
		KeywordExtraction locKeywordExtraction = new KeywordExtraction();

		if (lststrOnePlaceOCRedTextFiles == null || 
			lststrOnePlaceOCRedTextFiles.size() == 0 ||
			lststrOnePlaceGpsApInfoFiles == null ||
			lststrOnePlaceGpsApInfoFiles.size() == 0) {
			
			return sLine;
		}
		
		//First, calculate totally how many frames
		for (String sOCRedTextFile : lststrOnePlaceOCRedTextFiles) {
			
			try {
				fr = new FileReader(sOCRedTextFile);
				br = new BufferedReader(fr);
			
				while ((sLine = br.readLine()) != null) {
					sLine = sLine.trim();
					if (sLine.startsWith("%")) continue;    // treat the beginning % as comment in OCRed text file

					nTotalFrames = nTotalFrames + 1;					
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
		}  //For
		
		if (nTotalFrames == 0) return "";
		
//		lstSelectedFrames = Utility.getUniRandom(nTotalFrames, nFrameCnt);
		Utility.PrintList(lstSelectedFrames);
		
		//Merge OCRed text 
		//Duplicate words are removed
		m_lststrOnePlaceOCRedWords = new ArrayList<String>();
		m_lstnOnePlaceOCRedWordsFreq = new ArrayList<Integer>();
		
		m_lstfOnePlaceOCRedWordsWeight = new ArrayList<Double>();
		
		sTotalContentLine = "";
		
		for (String sOCRedTextFile : lststrOnePlaceOCRedTextFiles) {
				
			try {
				fr = new FileReader(sOCRedTextFile);
				br = new BufferedReader(fr);
			
				while((sLine = br.readLine()) != null) {
					
					sLine = sLine.trim();
					if (sLine.startsWith("%")) continue;    // treat the beginning % as comment in OCRed text file
					
					nFrameIdx = nFrameIdx + 1;
					
					if (lstSelectedFrames.contains(nFrameIdx)) {   //Select frames
						sTotalContentLine = sTotalContentLine + sLine + ",";
					}
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
		}  //For
								
				
		//Keep all OCRed Words (no duplicated words, Noun/Proper names are not applied), these words are latter to be used to compare with the place name
		if (Utility.MATCH_WITH_PLACENAME_WITH_PHRASE == false) {
			fields = Utility.splitString(sTotalContentLine);    //Save OCRed Text by words
		} else {
			fields = Utility.splitStringPhrase(sTotalContentLine);  //Save OCRed Text by Phrase
		}
		
		for (int j=0; j<fields.length; j++) {
			String sOCRedWord = fields[j].toLowerCase().trim();
			if (lststrAllOCRedWords.contains(sOCRedWord) == false) {
				lststrAllOCRedWords.add(sOCRedWord);
			}
		}
		
		//Get the non-duplicate noun/proper name
		m_lststrOnePlaceOCRedWords = locKeywordExtraction.extractKeywordFromContents(sTotalContentLine);
		
		//Initialize count for each word
		for (i=0; i<m_lststrOnePlaceOCRedWords.size(); i++) {
			m_lstnOnePlaceOCRedWordsFreq.add(0);
		}
		
		String sarrWord[] = Utility.splitString(sTotalContentLine);
		
		//Calculate word frequency
		for (i = 0; i<sarrWord.length; i++) {
			String sTmpWord = sarrWord[i];
			
			nWordIdx = -1;
			for (int k=0; k<m_lststrOnePlaceOCRedWords.size(); k++) {
				if (sTmpWord.compareToIgnoreCase(m_lststrOnePlaceOCRedWords.get(k)) == 0) {
					nWordIdx = k;
					break;
				}
			}
			
			if (nWordIdx != -1) {
				int nCurCount = m_lstnOnePlaceOCRedWordsFreq.get(nWordIdx);
				m_lstnOnePlaceOCRedWordsFreq.set(nWordIdx, nCurCount + 1);
			}
								
		}				


		nTotalWordCnt = 0;
		for (i=0; i<m_lstnOnePlaceOCRedWordsFreq.size(); i++) {
			nTotalWordCnt = nTotalWordCnt + m_lstnOnePlaceOCRedWordsFreq.get(i);
		}
		
		int nMaxFreq = Utility.getMaxInt(m_lstnOnePlaceOCRedWordsFreq);
		
		//Assign weight for OCRed words based on their frequencies
		for (i=0; i<m_lstnOnePlaceOCRedWordsFreq.size(); i++) {
			if (nMaxFreq > 0) {
				if (Utility.USE_TF_IDF_WEIGHT) {
					fWeight = 0.5 + 0.5*m_lstnOnePlaceOCRedWordsFreq.get(i).intValue()/nMaxFreq;
					fWeight = Math.round(10000.0*fWeight)/10000.0;
				} else {
					fWeight = Math.round(10000.0*m_lstnOnePlaceOCRedWordsFreq.get(i).intValue()/nTotalWordCnt)/10000.0;
				}
			} else {
				fWeight = 0.0;
			}
			
			m_lstfOnePlaceOCRedWordsWeight.add(fWeight);
		}
		
		//Merge GPS coordinates, AP info
		m_lststrApMac = new ArrayList<String>();
		m_lstnApRss = new ArrayList<Integer>();
		m_lstnApMacOccurredCnt = new ArrayList<Integer>();
		
		m_lstlstfAPRss = new ArrayList<List<Integer>>();
		
		m_fGpsLat = 0;
		m_fGpsLong = 0;
		m_nGpsCnt = 0;
		
		for (String sGpsApInfoFile: lststrOnePlaceGpsApInfoFiles) {
			try {
				fr = new FileReader(sGpsApInfoFile);
				br = new BufferedReader(fr);
				
				while((sLine = br.readLine()) != null) {
					fields = Utility.getFields(sLine);
					if (fields.length < Utility.SENSING_FILE_GPS_LONG_IDX + 1) {   //In this line, no GPS information, no WiFi information
						continue;
					}
					
					if (fields[Utility.SENSING_FILE_GPS_LAT_IDX].length() > 0 && fields[Utility.SENSING_FILE_GPS_LONG_IDX].length() > 0) {
						m_nGpsCnt = m_nGpsCnt + 1;
						m_fGpsLat = m_fGpsLat + Double.valueOf(fields[Utility.SENSING_FILE_GPS_LAT_IDX]).doubleValue();
						m_fGpsLong = m_fGpsLong + Double.valueOf(fields[Utility.SENSING_FILE_GPS_LONG_IDX]).doubleValue();
					}
					
					if (fields.length < Utility.SENSING_FILE_WIFI_FIRST_TUPLE_END_IDX + 1) {  //No WiFi information
						continue;
					}
					
/* Modifyed on 4/5/2015				
					//Tuple: <SSID, BSSID(MAC), RSS>
					for (int j=Utility.SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX; j+Utility.SENSING_FILE_WIFI_TUPLE_LEN-1<fields.length; j=j+Utility.SENSING_FILE_WIFI_TUPLE_LEN) {
						sApMac = fields[j+1].toLowerCase();
						nRss = Integer.valueOf(fields[j+2]).intValue();
*/
					//Tuple: <BSSID(MAC), RSS>
					for (int j=Utility.SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX; j+Utility.SENSING_FILE_WIFI_TUPLE_LEN-1<fields.length; j=j+Utility.SENSING_FILE_WIFI_TUPLE_LEN) {
						sApMac = fields[j].toLowerCase();
						nRss = Integer.valueOf(fields[j+1]).intValue();
						
						nIdx = m_lststrApMac.indexOf(sApMac);
						if (nIdx == -1) {
							m_lststrApMac.add(sApMac);
							//m_lstnApRss.add(nRss);
							m_lstnApMacOccurredCnt.add(1);
							List<Integer> lstRss = new ArrayList<Integer>();
							lstRss.add(nRss);
							m_lstlstfAPRss.add(lstRss);
						} else { //This MAC Exists
							nCnt = m_lstnApMacOccurredCnt.get(nIdx) + 1;
							m_lstnApMacOccurredCnt.set(nIdx, nCnt);
							
							List<Integer> lstRss = m_lstlstfAPRss.get(nIdx);
							lstRss.add(nRss);
							m_lstlstfAPRss.set(nIdx, lstRss);

							//nRss = nRss + m_lstnApRss.get(nIdx);
							//m_lstnApRss.set(nIdx, nRss);
						}
					}
					
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
			
		}  //For
		
		
		if (m_nGpsCnt == 0) {
			String[] sarrLatLong = Utility.getFields(sDefaultGpsCoordinates);
			m_fGpsLat = Double.valueOf(sarrLatLong[0]).doubleValue();
			m_fGpsLong = Double.valueOf(sarrLatLong[1]).doubleValue();
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		} else {
			//Average GPS
			m_fGpsLat = m_fGpsLat/m_nGpsCnt;
			m_fGpsLong = m_fGpsLong/m_nGpsCnt;
			
			m_fGpsLat = Math.round(m_fGpsLat*1000000)/1000000.0;
			m_fGpsLong = Math.round(m_fGpsLong*1000000)/1000000.0;
		}
		
//		//Take the average of RSS
//		m_lstfMeanApRss = new ArrayList<Double>();
//		for (i=0; i<m_lstnApRss.size(); i++) {
//			m_lstfMeanApRss.add(m_lstnApRss.get(i)*1.0 / m_lstnApMacOccurredCnt.get(i));
//		}
		m_lstfMeanApRss = new ArrayList<Double>();
		m_lstfStdApRss = new ArrayList<Double>();
		m_lstfLbApRss = new ArrayList<Double>();
		m_lstfUbApRss = new ArrayList<Double>();

		//Calcualte mean/std, lower bound, upper bound
		for (i=0; i<m_lststrApMac.size(); i++) {
			List<Integer> lstRss = m_lstlstfAPRss.get(i);
			fMeanRss = Utility.calculateMeanInteger(lstRss);
			fMeanRss = Math.round(fMeanRss*100)/100.0;
			
			fStdRss = Utility.calculateStdInteger(lstRss);
			fStdRss = Math.round(fStdRss*100)/100.0;
			
			fLbRss = fMeanRss - fStdRss * Utility.RSS_STD_CNT;
			fUbRss = fMeanRss + fStdRss * Utility.RSS_STD_CNT;
			
			m_lstfMeanApRss.add(fMeanRss);
			m_lstfStdApRss.add(fStdRss);
			m_lstfLbApRss.add(fLbRss);
			m_lstfUbApRss.add(fUbRss);
		}
		
		
		//So far, the following information are ready for each AP MAC:
		//MAC, Occurred count, Mean, Std, Lower bound, Upper bound
		//Here below, need to order these MACs and only keep top N AP MAC
		//To eliminate the effect from mobile APs, the occurred count is taken into consideration 
		//First, high occurred count  --> high order;  if occurred counts are the same, high mean RSS gets higher order
		
		//Order Ap MAC based on RSS
		m_lststrOrderedApMac = new ArrayList<String>();
		m_lstfOrderedMeanApRss = new ArrayList<Double>();
		m_lstfOrderedStdApRss = new ArrayList<Double>();
		m_lstfOrderedLbApRss = new ArrayList<Double>();
		m_lstfOrderedUbApRss = new ArrayList<Double>();
		m_lstnOrderedApMacOccurredCnt = new ArrayList<Integer>();

		for (i=0; i<m_lstnApMacOccurredCnt.size(); i++) {
			if (i==0) {
				m_lststrOrderedApMac.add(m_lststrApMac.get(i));
				m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
				m_lstfOrderedStdApRss.add(m_lstfStdApRss.get(i));
				m_lstfOrderedLbApRss.add(m_lstfLbApRss.get(i));
				m_lstfOrderedUbApRss.add(m_lstfUbApRss.get(i));
				m_lstnOrderedApMacOccurredCnt.add(m_lstnApMacOccurredCnt.get(i));
			} else {
				nIdx = -1;
				nCnt = m_lstnApMacOccurredCnt.get(i);
				fMeanRss = m_lstfMeanApRss.get(i);
				
//				for (int j=0; j<m_lstnOrderedApMacOccurredCnt.size(); j++) {
//					if (nCnt > m_lstnOrderedApMacOccurredCnt.get(j).intValue()) {
//						nIdx = j;
//						break;
//					} else if (nCnt == m_lstnOrderedApMacOccurredCnt.get(j).intValue() && fMeanRss > m_lstfOrderedMeanApRss.get(j)) {
//						nIdx = j;
//						break;
//					}
//				}

				for (int j=0; j<m_lstnOrderedApMacOccurredCnt.size(); j++) {
					if (fMeanRss > m_lstfOrderedMeanApRss.get(j)) {
						nIdx = j;
						break;
					} else if (fMeanRss == m_lstfOrderedMeanApRss.get(j) && nCnt > m_lstnOrderedApMacOccurredCnt.get(j).intValue()) {
						nIdx = j;
						break;						
					}
				}
				
				
				if (nIdx == -1) {
					m_lststrOrderedApMac.add(m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(m_lstfMeanApRss.get(i));
					m_lstfOrderedStdApRss.add(m_lstfStdApRss.get(i));
					m_lstfOrderedLbApRss.add(m_lstfLbApRss.get(i));
					m_lstfOrderedUbApRss.add(m_lstfUbApRss.get(i));
					m_lstnOrderedApMacOccurredCnt.add(m_lstnApMacOccurredCnt.get(i));					
				} else {
					m_lststrOrderedApMac.add(nIdx, m_lststrApMac.get(i));
					m_lstfOrderedMeanApRss.add(nIdx, m_lstfMeanApRss.get(i));
					m_lstfOrderedStdApRss.add(nIdx, m_lstfStdApRss.get(i));
					m_lstfOrderedLbApRss.add(nIdx, m_lstfLbApRss.get(i));
					m_lstfOrderedUbApRss.add(nIdx, m_lstfUbApRss.get(i));
					m_lstnOrderedApMacOccurredCnt.add(nIdx, m_lstnApMacOccurredCnt.get(i));					
				}
				
			}
		}
				
		
		//By here, GPS are averaged, Ap MAC are merged, RSS are averaged and ordered, OCRed text are merged
		//Now need to write into file.
		//For AP info, only write up to 10 APs (i.e. top 10 of RSS)
		sLine = m_fGpsLat + "," + m_fGpsLong;
		
		if (m_lststrOrderedApMac.size() > nTopAPCnt) {
			nCnt = nTopAPCnt;
		} else {
			nCnt = m_lststrOrderedApMac.size();
		}
		
		//MAC, Lower bound, Up bound, Mean, Order
		for (i=0; i<nCnt; i++) {
			sLine = sLine + "," + m_lststrOrderedApMac.get(i) + "," + m_lstfOrderedLbApRss.get(i) + "," + m_lstfOrderedUbApRss.get(i) + "," + m_lstfOrderedMeanApRss.get(i) + "," + (i+1);
		}
		
		for (i=nCnt; i<nTopAPCnt; i++) {
			sLine = sLine + ",,,,,";
		}
		
		//////////////////////Extra: Sort OCRed words based on Frequency///////////////////
//##Original		List<String> lststrOnePlaceOCRedWords = new ArrayList<String>();  //Merged OCRed Text
//##Original		List<Integer> lstnOnePlaceOCRedWordsFreq = new ArrayList<Integer>();  //Frequency (=time of occurrence) of these words
//##Original		List<Double> lstfOnePlaceOCRedWordsWeight = new ArrayList<Double>();  //Weight of these words
		int nInsertPos = -1;
		
		for (i=0; i<m_lststrOnePlaceOCRedWords.size(); i++) {
			if (i == 0) {
				lststrOnePlaceOCRedWords.add(m_lststrOnePlaceOCRedWords.get(i));
				lstnOnePlaceOCRedWordsFreq.add(m_lstnOnePlaceOCRedWordsFreq.get(i));
				lstfOnePlaceOCRedWordsWeight.add(m_lstfOnePlaceOCRedWordsWeight.get(i));
			} else {
				nInsertPos = -1;
				for (int j=0; j<lstnOnePlaceOCRedWordsFreq.size(); j++) {
					if (m_lstnOnePlaceOCRedWordsFreq.get(i) > lstnOnePlaceOCRedWordsFreq.get(j)) {
						nInsertPos = j;
						break;
					}
				}
				
				if (nInsertPos != -1) {
					lststrOnePlaceOCRedWords.add(nInsertPos, m_lststrOnePlaceOCRedWords.get(i));
					lstnOnePlaceOCRedWordsFreq.add(nInsertPos, m_lstnOnePlaceOCRedWordsFreq.get(i));
					lstfOnePlaceOCRedWordsWeight.add(nInsertPos, m_lstfOnePlaceOCRedWordsWeight.get(i));
					
				} else {
					lststrOnePlaceOCRedWords.add(m_lststrOnePlaceOCRedWords.get(i));
					lstnOnePlaceOCRedWordsFreq.add(m_lstnOnePlaceOCRedWordsFreq.get(i));
					lstfOnePlaceOCRedWordsWeight.add(m_lstfOnePlaceOCRedWordsWeight.get(i));
					
				}
				
			}
		}
		
		for (i=0; i<lststrOnePlaceOCRedWords.size(); i++) {
			sLine = sLine + "," + lststrOnePlaceOCRedWords.get(i) + "," + lstfOnePlaceOCRedWordsWeight.get(i);
		}
		
				
		return sLine;
	}
	
	
	
}
