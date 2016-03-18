package com.autolabel;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

////////////////////////////////////////////////////////
//Folder structure of raw crowdsourced data
//Topfolder----BestBuy---GpsAP----------GpsAP1.csv
//      |         |        |------------GpsAP2.csv  
//      |         |     
//      |         |------OCRedWords-----OCRed1.csv
//      |                     |---------OCRed2.csv
//      |
//      |-------Store 2---GpsAP---------
////////////////////////////////////////////////////////

public class CrowdsourcedDataProcessor {

	private String m_sTopParentFolder = "";   //The most top folder of the crowdsourced data
	private int m_nApCnt = 0;
	private String m_sGpsCoordinates = "";
	private int m_nFrameCnt = Utility.NO_FRAME_SAMPLE;
	private int m_nIndexSetCnt = 0;
	private OnePlaceDataProcessor m_OnePlaceDataProcessor = new OnePlaceDataProcessor();
	
	public CrowdsourcedDataProcessor() {
		// TODO Auto-generated constructor stub
	}
	
	public CrowdsourcedDataProcessor(String sTopParentFolder, int nApCnt, String sGpsCoordinates, int nFrameCnt) {
		m_sTopParentFolder = sTopParentFolder;
		m_nApCnt = nApCnt;
		m_sGpsCoordinates = sGpsCoordinates;
		m_nFrameCnt = nFrameCnt;
	}

	public CrowdsourcedDataProcessor(String sTopParentFolder, int nApCnt, String sGpsCoordinates, int nFrameCnt, int nIndexSetCnt) {
		m_sTopParentFolder = sTopParentFolder;
		m_nApCnt = nApCnt;
		m_sGpsCoordinates = sGpsCoordinates;
		m_nFrameCnt = nFrameCnt;
		m_nIndexSetCnt = nIndexSetCnt;
	}
		
	//Merge every store's GpsAP data and OCRed Words
	//For each store, there could be many GpsAP files and OCRed Words files
	public void MergeCrowdsourcedData() {
		List<String> lstPlaceName = new ArrayList<String>();
		List<String> lstFullFolderName = new ArrayList<String>();
		String sLine;
		String sAllOCRedWordsLine = "";
		String sOnePlaceMergedResultFile = "";
		
		//Get sub folders (first layer) under the top parent folder
		Utility.getSubFolderList(m_sTopParentFolder, lstPlaceName, lstFullFolderName);
		
		for (int i=0; i<lstPlaceName.size(); i++) {
			List<String> lstGpsAPFiles = new ArrayList<String>();
			List<String> lstOCRedWordsFiles = new ArrayList<String>();
			List<String> lststrAllOCRedWords = new ArrayList<String>();
			
			//Get the GpsAp file list and OCRed words file list for a store
			Utility.getGpsAPFileList(m_sTopParentFolder, lstPlaceName.get(i), lstGpsAPFiles);
			if (Utility.USE_FULL_TEXT) {
				Utility.getOCRedWordsFileList_FullText(m_sTopParentFolder, lstPlaceName.get(i), lstOCRedWordsFiles);				
			} else {
				Utility.getOCRedWordsFileList(m_sTopParentFolder, lstPlaceName.get(i), lstOCRedWordsFiles);
			}
			
			//Merge the Gps AP files and OCRed words files of a store
			//Return:  GPS, <MAC, LbRss, UbRss, MeanRss, Order>s, OCRed Words/Weights list
			
			//Method 1: No duplicate OCRed Words, Noun/Proper Name Extraction is not applied
			//sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSet(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt);  
			
			//Method 2: Duplicate words exist (i.e. weight), Noun/Proper Name Extraction is not applied
			//sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSetDuplicateWordsWithoutNounExtraction(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt); 
			
			//Method 3: Duplicate words exist (i.e. weight), Nour/Proper Name Extraction is applied

			//##Original
			//##Original   sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSetDuplicateWordsWithNounExtraction(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt, lststrAllOCRedWords, m_sGpsCoordinates); 
			
			//##Temp
			List<String> lststrOnePlaceOCRedWords = new ArrayList<String>();  //Merged OCRed Text
			List<Integer> lstnOnePlaceOCRedWordsFreq = new ArrayList<Integer>();  //Frequency (=time of occurrence) of these words
			List<Double> lstfOnePlaceOCRedWordsWeight = new ArrayList<Double>();  //Weight of these words
			
			List<Integer> lstSelectedFrames = new ArrayList<Integer>();
			
			lstSelectedFrames = Utility.getFrameIndex(m_sTopParentFolder, lstPlaceName.get(i), m_nFrameCnt);
			
//			sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSetDuplicateWordsWithNounExtraction_Temp(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt, lststrAllOCRedWords, lststrOnePlaceOCRedWords,lstnOnePlaceOCRedWordsFreq,lstfOnePlaceOCRedWordsWeight, m_sGpsCoordinates); 
//			sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSetDuplicateWordsWithNounExtraction_Temp_withFrameSample(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt, lststrAllOCRedWords, lststrOnePlaceOCRedWords,lstnOnePlaceOCRedWordsFreq,lstfOnePlaceOCRedWordsWeight, m_sGpsCoordinates, m_nFrameCnt);
			sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSetDuplicateWordsWithNounExtraction_Temp_withFrameSample(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt, lststrAllOCRedWords, lststrOnePlaceOCRedWords,lstnOnePlaceOCRedWordsFreq,lstfOnePlaceOCRedWordsWeight, m_sGpsCoordinates, lstSelectedFrames);
			
			sAllOCRedWordsLine = "";
			
			if (sLine.length() > 0) {
				sLine = sLine + "\n";
							
				for (int j=0; j<lststrAllOCRedWords.size(); j++) {
					sAllOCRedWordsLine = sAllOCRedWordsLine + lststrAllOCRedWords.get(j) + ",";
				}
				
				sAllOCRedWordsLine = sAllOCRedWordsLine + "\n";
				
//				sOnePlaceMergedResultFile = Utility.getOnePlaceMergedResultFile(m_sTopParentFolder, lstPlaceName.get(i));
				sOnePlaceMergedResultFile = Utility.getOnePlaceMergedResultFile(m_sTopParentFolder, lstPlaceName.get(i), m_nFrameCnt);
				
				FileWriter fwWMerged = null;
				
				try {
					fwWMerged = new FileWriter(sOnePlaceMergedResultFile, false);   //Overwrite
					
					fwWMerged.write(sLine);  //Save the OCRed words, all these words are Noun/Proper name extracted, words are duplicated, so weight is associated

					fwWMerged.write(sAllOCRedWordsLine);   //Also save all the non-duplicate OCRed words (the words in this line is not applied with Noun/Proper Name extraction).
					
					fwWMerged.close();
				} catch (Exception e) {
					
				}
				
				
				//[TEMP BEGIN]******************************************
				String sOnePlaceMergedResultFile_Full = Utility.getOnePlaceMergedResultFile_Full(m_sTopParentFolder, lstPlaceName.get(i), m_nFrameCnt);
				
				FileWriter fwWMerged_Full = null;
				
				try {
					fwWMerged_Full = new FileWriter(sOnePlaceMergedResultFile_Full, false);   //Overwrite
					
					String sLine_Full = "";
					
					for (int j=0; j<lststrOnePlaceOCRedWords.size(); j++) {
						sLine_Full = Utility.getStringWithRequiredLen(lststrOnePlaceOCRedWords.get(j),25) + "," + Utility.getStringWithRequiredLen(lstnOnePlaceOCRedWordsFreq.get(j).toString(),5) + "," + lstfOnePlaceOCRedWordsWeight.get(j) + "\n";
						fwWMerged_Full.write(sLine_Full);
						
					}
										
					fwWMerged_Full.close();
				} catch (Exception e) {
					
				}
				//[TEMP END]********************************************
				
			}
			
		}

		return;
	}

	
	
	//Merge every store's GpsAP data and OCRed Words
	//For each store, there could be many GpsAP files and OCRed Words files
	public void MergeCrowdsourcedData_MultipleIndexSet() {
		List<String> lstPlaceName = new ArrayList<String>();
		List<String> lstFullFolderName = new ArrayList<String>();
		String sLine;
		String sAllOCRedWordsLine = "";
		String sOnePlaceMergedResultFile = "";
		
		//Get sub folders (first layer) under the top parent folder
		Utility.getSubFolderList(m_sTopParentFolder, lstPlaceName, lstFullFolderName);
		
		for (int i=0; i<lstPlaceName.size(); i++) {
			List<String> lstGpsAPFiles = new ArrayList<String>();
			List<String> lstOCRedWordsFiles = new ArrayList<String>();
			List<String> lststrAllOCRedWords = new ArrayList<String>();
			
			System.out.println("...........Processing......." + lstPlaceName.get(i));
			
			//Get the GpsAp file list and OCRed words file list for a store
			Utility.getGpsAPFileList(m_sTopParentFolder, lstPlaceName.get(i), lstGpsAPFiles);
			if (Utility.USE_FULL_TEXT) {
				Utility.getOCRedWordsFileList_FullText(m_sTopParentFolder, lstPlaceName.get(i), lstOCRedWordsFiles);				
			} else {
				Utility.getOCRedWordsFileList(m_sTopParentFolder, lstPlaceName.get(i), lstOCRedWordsFiles);
			}
			
			//Merge the Gps AP files and OCRed words files of a store
			//Return:  GPS, <MAC, LbRss, UbRss, MeanRss, Order>s, OCRed Words/Weights list
			
			//Method 1: No duplicate OCRed Words, Noun/Proper Name Extraction is not applied
			//sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSet(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt);  
			
			//Method 2: Duplicate words exist (i.e. weight), Noun/Proper Name Extraction is not applied
			//sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSetDuplicateWordsWithoutNounExtraction(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt); 
			
			//Method 3: Duplicate words exist (i.e. weight), Nour/Proper Name Extraction is applied

			//##Original
			//##Original   sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSetDuplicateWordsWithNounExtraction(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt, lststrAllOCRedWords, m_sGpsCoordinates); 
			
			for (int jj=1; jj<=m_nIndexSetCnt; jj++) {
				//##Temp
				List<String> lststrOnePlaceOCRedWords = new ArrayList<String>();  //Merged OCRed Text
				List<Integer> lstnOnePlaceOCRedWordsFreq = new ArrayList<Integer>();  //Frequency (=time of occurrence) of these words
				List<Double> lstfOnePlaceOCRedWordsWeight = new ArrayList<Double>();  //Weight of these words
				
				List<Integer> lstSelectedFrames = new ArrayList<Integer>();
				
				lstSelectedFrames = Utility.getFrameIndex_MultipleIndexSet(m_sTopParentFolder, lstPlaceName.get(i), m_nFrameCnt, jj);
				
	//			sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSetDuplicateWordsWithNounExtraction_Temp(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt, lststrAllOCRedWords, lststrOnePlaceOCRedWords,lstnOnePlaceOCRedWordsFreq,lstfOnePlaceOCRedWordsWeight, m_sGpsCoordinates); 
	//			sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSetDuplicateWordsWithNounExtraction_Temp_withFrameSample(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt, lststrAllOCRedWords, lststrOnePlaceOCRedWords,lstnOnePlaceOCRedWordsFreq,lstfOnePlaceOCRedWordsWeight, m_sGpsCoordinates, m_nFrameCnt);
				sLine = m_OnePlaceDataProcessor.mergeOnePlaceDataSetDuplicateWordsWithNounExtraction_Temp_withFrameSample(lstGpsAPFiles, lstOCRedWordsFiles, m_nApCnt, lststrAllOCRedWords, lststrOnePlaceOCRedWords,lstnOnePlaceOCRedWordsFreq,lstfOnePlaceOCRedWordsWeight, m_sGpsCoordinates, lstSelectedFrames);
				
				sAllOCRedWordsLine = "";
				
				if (sLine.length() > 0) {
					sLine = sLine + "\n";
								
					for (int j=0; j<lststrAllOCRedWords.size(); j++) {
						sAllOCRedWordsLine = sAllOCRedWordsLine + lststrAllOCRedWords.get(j) + ",";
					}
					
					sAllOCRedWordsLine = sAllOCRedWordsLine + "\n";
					
	//				sOnePlaceMergedResultFile = Utility.getOnePlaceMergedResultFile(m_sTopParentFolder, lstPlaceName.get(i));
					sOnePlaceMergedResultFile = Utility.getOnePlaceMergedResultFile_MultipleIndexSet(m_sTopParentFolder, lstPlaceName.get(i), m_nFrameCnt, jj);
					
					FileWriter fwWMerged = null;
					
					try {
						fwWMerged = new FileWriter(sOnePlaceMergedResultFile, false);   //Overwrite
						
						fwWMerged.write(sLine);  //Save the OCRed words, all these words are Noun/Proper name extracted, words are duplicated, so weight is associated
	
						fwWMerged.write(sAllOCRedWordsLine);   //Also save all the non-duplicate OCRed words (the words in this line is not applied with Noun/Proper Name extraction).
						
						fwWMerged.close();
					} catch (Exception e) {
						
					}
					
					
					//[TEMP BEGIN]******************************************
					String sOnePlaceMergedResultFile_Full = Utility.getOnePlaceMergedResultFile_Full_MultipleIndexSet(m_sTopParentFolder, lstPlaceName.get(i), m_nFrameCnt, jj);
					
					FileWriter fwWMerged_Full = null;
					
					try {
						fwWMerged_Full = new FileWriter(sOnePlaceMergedResultFile_Full, false);   //Overwrite
						
						String sLine_Full = "";
						
						for (int j=0; j<lststrOnePlaceOCRedWords.size(); j++) {
							sLine_Full = Utility.getStringWithRequiredLen(lststrOnePlaceOCRedWords.get(j),25) + "," + Utility.getStringWithRequiredLen(lstnOnePlaceOCRedWordsFreq.get(j).toString(),5) + "," + lstfOnePlaceOCRedWordsWeight.get(j) + "\n";
							fwWMerged_Full.write(sLine_Full);
							
						}
											
						fwWMerged_Full.close();
					} catch (Exception e) {
						
					}
					//[TEMP END]********************************************
					
				}
			}
			
		}

		return;
	}
	
	
	
}
