package com.autolabel;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

//This class is used to generate Database data files by doing keyword matching between Web Data and Crowdsourced Data

public class DatabaseDataGenerator {

	private String m_sWebDataFile = "";  //PlaceKeywordList_xxxxx.csv
	private String m_sCrowdsourcedTopFolder = "";
	private String m_sPlaceApResultTopFolder = "";
	
	//private String m_sPlaceKeywordListGpsFile = "";
	
	private int m_nApCnt;
	private int m_nFrameCnt;
	private int m_nIndexSetCnt;
	
	private ConstructMacPlaceMapping m_ConstructMacPlaceMapping = new ConstructMacPlaceMapping();
	
	public DatabaseDataGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	public DatabaseDataGenerator(String sWebDataFile, String sCrowdsourcedTopFolder, String sPlaceApResultTopFolder, int nApCnt, int nFrameCnt) {
		m_sWebDataFile = sWebDataFile;
		m_sCrowdsourcedTopFolder = sCrowdsourcedTopFolder;
		m_sPlaceApResultTopFolder = sPlaceApResultTopFolder;
		
		m_nApCnt = nApCnt;
		m_nFrameCnt = nFrameCnt;
	}

	public DatabaseDataGenerator(String sWebDataFile, String sCrowdsourcedTopFolder, String sPlaceApResultTopFolder, int nApCnt, int nFrameCnt,  int nIndexSetCnt) {
		m_sWebDataFile = sWebDataFile;
		m_sCrowdsourcedTopFolder = sCrowdsourcedTopFolder;
		m_sPlaceApResultTopFolder = sPlaceApResultTopFolder;
		
		m_nApCnt = nApCnt;
		m_nFrameCnt = nFrameCnt;
		
		m_nIndexSetCnt = nIndexSetCnt;
	}
	
	
	public void GenerateDatabaseData() {
		int i, j;
		List<String> lstMergedGpsApOCRFiles = new ArrayList<String>();
		List<String> lstGroundTruthFiles = new ArrayList<String>();
		
		List<AL_CrowdSourcedPlaceData> lstCrowdsourcedPlaceData = new ArrayList<AL_CrowdSourcedPlaceData>();
		List<String> lstGroundTruthPlaceNames = new ArrayList<String>();
		
		String sFinalPlaceAPDatabaseFile = "";
		String sKeywordMatchingEvalResultFile = "";
		String sKeywordMatchingConfusionMatrixFile = "";
		String sMatchedCommonWordsFile = "";
		String sGroundTruthPlaceName = "";
		
		String sLine = "";
		
		String sMatchingScoreLine = "";
		
		System.out.println("[DatabaseDataGenerator DEBUG 1] Get Place Keywords List...");
		//Get final processed web data from PlaceKeywordList_Lat_Long.csv
		//m_sPlaceKeywordListGpsFile = Utility.getPlaceKeywordFile(m_sWebDataTopFolder);
		List<AL_PlaceWebData> lstPlaceWebData = Utility.getPlaceKeywordsList(m_sWebDataFile);
		
		if (lstPlaceWebData == null || lstPlaceWebData.size() == 0) return;
		
		System.out.println("[DatabaseDataGenerator DEBUG 2] Process and get Crowdsourced Data...");
		//Processed and get crowdsourced data
		Utility.getCrowdsourcedMergedGroundTruthFiles(m_sCrowdsourcedTopFolder, lstMergedGpsApOCRFiles, lstGroundTruthFiles, m_nFrameCnt);  //Get the list of MergedGpsApOCR.csv files and GroundTruth.csv files
		Utility.getCrowdsourcedMergedGroundTruth(lstMergedGpsApOCRFiles, lstGroundTruthFiles, m_nApCnt, lstCrowdsourcedPlaceData, lstGroundTruthPlaceNames);

		//So far, the Place-Keyword/Weight data and Crowdsourced GpsAp-OCRed Text data are ready
		//Here below, do Keyword Matching
		//In lstPlaceWebData, each record is for one place/store
		//In lstCrowdsourcedPlaceData, each record is for one place/store
		//
		//After keyword matching, it generates Three files under m_sPlaceApResultTopFolder, the files are timestamped in filename
		//The FIRST file will be the AP MAC Database file, in which, it contains: Place/Store Name, Lat, Long, <AP MAC, lbRSS, ubRSS, meanRSS, Order>s
		//Each line is for one place/store
		//The SECOND file is for evaluating the performance of KeywordMatching, in this file, it contains: Matching Result (1=matching is correct, 0=matching is wrong), Matching score, Recognized Place/Store name, Ground Truth Place/Store Name
		//The matching result (0,1) is derived by comparing the recognized place/store name with the ground truth place name
		//Each line for one place/store
		//The THIRD file is for keyword matching confusion matrix
		//The first line is the Ground Truth place names from lstGroundTruthPlaceNames
		//The second line is the Candidate place names from lstPlaceWebData
		//From 3rd line, each line (for each ground truth place) contains the matching scores list between one Ground Truth place and all the candidate place name 
		//(i.e. each line shows how each crowdsourced data at one place is recognized as these candidate places)
		//
		
		//These result files need to be written
		sFinalPlaceAPDatabaseFile = Utility.getPlaceAPDatabaseFile(m_sPlaceApResultTopFolder, m_nFrameCnt);
		sKeywordMatchingEvalResultFile = Utility.getKeywordMatchingEvalResultFile(m_sPlaceApResultTopFolder, m_nFrameCnt);
		sKeywordMatchingConfusionMatrixFile = Utility.getKeywordMatchingConfusionMatrixFile(m_sPlaceApResultTopFolder, m_nFrameCnt);
		sMatchedCommonWordsFile = Utility.getMatchedCommonWordsFile(m_sPlaceApResultTopFolder, m_nFrameCnt);
		
		try {
			FileWriter fwKeywordMatchingConfusionMatrixFile = new FileWriter(sKeywordMatchingConfusionMatrixFile, false);   //Overwrite
			FileWriter fwKeywordMatchingEvalResultFile = new FileWriter(sKeywordMatchingEvalResultFile, false);   //Overwrite
			FileWriter fwFinalPlaceAPDatabaseFile = new FileWriter(sFinalPlaceAPDatabaseFile, false);   //Overwrite
			FileWriter fwMatchedCommonWordsFile = new FileWriter(sMatchedCommonWordsFile, false);  //Overwrite
			
			//First Line: Ground Truth Place Names
			for (i=0; i<lstGroundTruthPlaceNames.size(); i++) {
				sLine = sLine + lstGroundTruthPlaceNames.get(i) + ",";
			}
			
			sLine = sLine + "\n";
			
			fwKeywordMatchingConfusionMatrixFile.write(sLine);
			
			sLine = "";
			
			//Second Line: Candidate Place Names (How close the crowdsourced data will be recognized as these places is to be measured below) 
			for (i=0; i<lstPlaceWebData.size(); i++) {
				sLine = sLine + lstPlaceWebData.get(i).getPlaceName() + ",";
			}
			
			sLine = sLine + "\n";
			
			fwKeywordMatchingConfusionMatrixFile.write(sLine);
			
			//This part does matching
			for (i=0; i<lstCrowdsourcedPlaceData.size(); i++) {
				AL_CrowdSourcedPlaceData locCrowdSourcedPlaceData = lstCrowdsourcedPlaceData.get(i);
				List<Double> lstfMaxMatchingScore = new ArrayList<Double>();
				double fMaxMatchingScore = 0.0f;
				List<Double> lstfMatchingScore = new ArrayList<Double>();
				
				sGroundTruthPlaceName = lstGroundTruthPlaceNames.get(i);
				//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywords(lstPlaceWebData, locCrowdSourcedPlaceData, fMatchingScore);
				//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithSingleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, fMaxMatchingScore, lstfMatchingScore);
				//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithDoubleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, fMaxMatchingScore, lstfMatchingScore);
				//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithDoubleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, lstfMaxMatchingScore, lstfMatchingScore);
				AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithTFIDF(lstPlaceWebData, locCrowdSourcedPlaceData, lstfMaxMatchingScore, lstfMatchingScore, fwMatchedCommonWordsFile, sGroundTruthPlaceName);
						
				fMaxMatchingScore = lstfMaxMatchingScore.get(0).doubleValue();
				
				if (placeAPInfo != null) {  //Matched a place/store
					List<AL_APInfoForDB> lstAPInfoForDB = placeAPInfo.getAPInfoForDB();
					if (lstAPInfoForDB != null && lstAPInfoForDB.size() > 0) {
						sLine = placeAPInfo.getPlaceName() + "," + placeAPInfo.getGpsLat() + "," + placeAPInfo.getGpsLong();
						
						for (j=0; j<lstAPInfoForDB.size(); j++) {
							AL_APInfoForDB locAPInfoForDB = lstAPInfoForDB.get(j);
							sLine = sLine + "," + locAPInfoForDB.getMAC() + "," + locAPInfoForDB.getLbRSS() + "," + locAPInfoForDB.getUbRSS() + "," + locAPInfoForDB.getMeanRSS() + "," + locAPInfoForDB.getOrder();
						}
						
						sLine = sLine + "\n";
						
						fwFinalPlaceAPDatabaseFile.write(sLine);
					}
					
					//Matched result (0 or 1==matched), Matching score, Ground Truth Place/Store Name, Recognized Place/Store Name, 
					sLine = 1 + "," + fMaxMatchingScore + "," + lstGroundTruthPlaceNames.get(i) + "," + placeAPInfo.getPlaceName() + "," +  "\n";
					fwKeywordMatchingEvalResultFile.write(sLine);
					
					//Save Matching score line
					sMatchingScoreLine = "";
					
					for (j=0; j<lstfMatchingScore.size(); j++) {
						sMatchingScoreLine = sMatchingScoreLine + lstfMatchingScore.get(j).doubleValue() + ",";
					}
					
					sMatchingScoreLine = sMatchingScoreLine + "\n";
					
					fwKeywordMatchingConfusionMatrixFile.write(sMatchingScoreLine);
					
				} else {  //Not matched 
					
					//Save evaluation result
					//sLine = 0 + "," +  fMaxMatchingScore + "," + lstGroundTruthPlaceNames.get(i) + "," + "," + "\n";
					sLine = 0 + "," +  0.0 + "," + lstGroundTruthPlaceNames.get(i) + "," + "," + "\n";

					fwKeywordMatchingEvalResultFile.write(sLine);
					
					//Write Matching score line
					sMatchingScoreLine = "";
					
					for (j=0; j<lstPlaceWebData.size(); j++) {
						sMatchingScoreLine = sMatchingScoreLine + 0.0 + ",";
					}
					
					sMatchingScoreLine = sMatchingScoreLine + "\n";
					
					fwKeywordMatchingConfusionMatrixFile.write(sMatchingScoreLine);
					
				}
			}
			
			fwMatchedCommonWordsFile.close();
			fwFinalPlaceAPDatabaseFile.close();
			fwKeywordMatchingEvalResultFile.close();
			fwKeywordMatchingConfusionMatrixFile.close();

		} catch (Exception e) {
			 
		}
		
	}

	
	
	
	//OCR has Multiple Index Sets
	public void GenerateDatabaseData_MultipleIndexSet() {
		int i, j;
				
		System.out.println("[DatabaseDataGenerator DEBUG 1] Get Place Keywords List...");
		//Get final processed web data from PlaceKeywordList_Lat_Long.csv
		//m_sPlaceKeywordListGpsFile = Utility.getPlaceKeywordFile(m_sWebDataTopFolder);
		List<AL_PlaceWebData> lstPlaceWebData = Utility.getPlaceKeywordsList(m_sWebDataFile);
		
		if (lstPlaceWebData == null || lstPlaceWebData.size() == 0) return;
		
		System.out.println("[DatabaseDataGenerator DEBUG 2] Process and get Crowdsourced Data...");
		
		
		for (int k=1; k<=m_nIndexSetCnt; k++) {
			List<String> lstMergedGpsApOCRFiles = new ArrayList<String>();
			List<String> lstGroundTruthFiles = new ArrayList<String>();
			
			List<AL_CrowdSourcedPlaceData> lstCrowdsourcedPlaceData = new ArrayList<AL_CrowdSourcedPlaceData>();
			List<String> lstGroundTruthPlaceNames = new ArrayList<String>();
		
			String sFinalPlaceAPDatabaseFile = "";
			String sKeywordMatchingEvalResultFile = "";
			String sKeywordMatchingConfusionMatrixFile = "";
			String sMatchedCommonWordsFile = "";
			String sGroundTruthPlaceName = "";
			
			String sLine = "";
			
			String sMatchingScoreLine = "";
			
			//Processed and get crowdsourced data
			Utility.getCrowdsourcedMergedGroundTruthFiles_MultipleIndexSet(m_sCrowdsourcedTopFolder, lstMergedGpsApOCRFiles, lstGroundTruthFiles, m_nFrameCnt, k);  //Get the list of MergedGpsApOCR.csv files and GroundTruth.csv files
			Utility.getCrowdsourcedMergedGroundTruth(lstMergedGpsApOCRFiles, lstGroundTruthFiles, m_nApCnt, lstCrowdsourcedPlaceData, lstGroundTruthPlaceNames);
	
			//So far, the Place-Keyword/Weight data and Crowdsourced GpsAp-OCRed Text data are ready
			//Here below, do Keyword Matching
			//In lstPlaceWebData, each record is for one place/store
			//In lstCrowdsourcedPlaceData, each record is for one place/store
			//
			//After keyword matching, it generates Three files under m_sPlaceApResultTopFolder, the files are timestamped in filename
			//The FIRST file will be the AP MAC Database file, in which, it contains: Place/Store Name, Lat, Long, <AP MAC, lbRSS, ubRSS, meanRSS, Order>s
			//Each line is for one place/store
			//The SECOND file is for evaluating the performance of KeywordMatching, in this file, it contains: Matching Result (1=matching is correct, 0=matching is wrong), Matching score, Recognized Place/Store name, Ground Truth Place/Store Name
			//The matching result (0,1) is derived by comparing the recognized place/store name with the ground truth place name
			//Each line for one place/store
			//The THIRD file is for keyword matching confusion matrix
			//The first line is the Ground Truth place names from lstGroundTruthPlaceNames
			//The second line is the Candidate place names from lstPlaceWebData
			//From 3rd line, each line (for each ground truth place) contains the matching scores list between one Ground Truth place and all the candidate place name 
			//(i.e. each line shows how each crowdsourced data at one place is recognized as these candidate places)
			//
			
			//These result files need to be written
			sFinalPlaceAPDatabaseFile = Utility.getPlaceAPDatabaseFile_MultipleIndexSet(m_sPlaceApResultTopFolder, m_nFrameCnt, k);
			sKeywordMatchingEvalResultFile = Utility.getKeywordMatchingEvalResultFile_MultipleIndexSet(m_sPlaceApResultTopFolder, m_nFrameCnt, k);
			sKeywordMatchingConfusionMatrixFile = Utility.getKeywordMatchingConfusionMatrixFile_MultipleIndexSet(m_sPlaceApResultTopFolder, m_nFrameCnt, k);
			sMatchedCommonWordsFile = Utility.getMatchedCommonWordsFile_MultipleIndexSet(m_sPlaceApResultTopFolder, m_nFrameCnt, k);
			
			try {
				FileWriter fwKeywordMatchingConfusionMatrixFile = new FileWriter(sKeywordMatchingConfusionMatrixFile, false);   //Overwrite
				FileWriter fwKeywordMatchingEvalResultFile = new FileWriter(sKeywordMatchingEvalResultFile, false);   //Overwrite
				FileWriter fwFinalPlaceAPDatabaseFile = new FileWriter(sFinalPlaceAPDatabaseFile, false);   //Overwrite
				FileWriter fwMatchedCommonWordsFile = new FileWriter(sMatchedCommonWordsFile, false);  //Overwrite
				
				//First Line: Ground Truth Place Names
				for (i=0; i<lstGroundTruthPlaceNames.size(); i++) {
					sLine = sLine + lstGroundTruthPlaceNames.get(i) + ",";
				}
				
				sLine = sLine + "\n";
				
				fwKeywordMatchingConfusionMatrixFile.write(sLine);
				
				sLine = "";
				
				//Second Line: Candidate Place Names (How close the crowdsourced data will be recognized as these places is to be measured below) 
				for (i=0; i<lstPlaceWebData.size(); i++) {
					sLine = sLine + lstPlaceWebData.get(i).getPlaceName() + ",";
				}
				
				sLine = sLine + "\n";
				
				fwKeywordMatchingConfusionMatrixFile.write(sLine);
				
				//This part does matching
				for (i=0; i<lstCrowdsourcedPlaceData.size(); i++) {
					AL_CrowdSourcedPlaceData locCrowdSourcedPlaceData = lstCrowdsourcedPlaceData.get(i);
					List<Double> lstfMaxMatchingScore = new ArrayList<Double>();
					double fMaxMatchingScore = 0.0f;
					List<Double> lstfMatchingScore = new ArrayList<Double>();
					
					sGroundTruthPlaceName = lstGroundTruthPlaceNames.get(i);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywords(lstPlaceWebData, locCrowdSourcedPlaceData, fMatchingScore);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithSingleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, fMaxMatchingScore, lstfMatchingScore);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithDoubleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, fMaxMatchingScore, lstfMatchingScore);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithDoubleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, lstfMaxMatchingScore, lstfMatchingScore);
					AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithTFIDF(lstPlaceWebData, locCrowdSourcedPlaceData, lstfMaxMatchingScore, lstfMatchingScore, fwMatchedCommonWordsFile, sGroundTruthPlaceName);
							
					fMaxMatchingScore = lstfMaxMatchingScore.get(0).doubleValue();
					
					if (placeAPInfo != null) {  //Matched a place/store
						List<AL_APInfoForDB> lstAPInfoForDB = placeAPInfo.getAPInfoForDB();
						if (lstAPInfoForDB != null && lstAPInfoForDB.size() > 0) {
							sLine = placeAPInfo.getPlaceName() + "," + placeAPInfo.getGpsLat() + "," + placeAPInfo.getGpsLong();
							
							for (j=0; j<lstAPInfoForDB.size(); j++) {
								AL_APInfoForDB locAPInfoForDB = lstAPInfoForDB.get(j);
								sLine = sLine + "," + locAPInfoForDB.getMAC() + "," + locAPInfoForDB.getLbRSS() + "," + locAPInfoForDB.getUbRSS() + "," + locAPInfoForDB.getMeanRSS() + "," + locAPInfoForDB.getOrder();
							}
							
							sLine = sLine + "\n";
							
							fwFinalPlaceAPDatabaseFile.write(sLine);
						}
						
						//Matched result (0 or 1==matched), Matching score, Ground Truth Place/Store Name, Recognized Place/Store Name, 
						sLine = 1 + "," + fMaxMatchingScore + "," + lstGroundTruthPlaceNames.get(i) + "," + placeAPInfo.getPlaceName() + "," +  "\n";
						fwKeywordMatchingEvalResultFile.write(sLine);
						
						//Save Matching score line
						sMatchingScoreLine = "";
						
						for (j=0; j<lstfMatchingScore.size(); j++) {
							sMatchingScoreLine = sMatchingScoreLine + lstfMatchingScore.get(j).doubleValue() + ",";
						}
						
						sMatchingScoreLine = sMatchingScoreLine + "\n";
						
						fwKeywordMatchingConfusionMatrixFile.write(sMatchingScoreLine);
						
					} else {  //Not matched 
						
						//Save evaluation result
						//sLine = 0 + "," +  fMaxMatchingScore + "," + lstGroundTruthPlaceNames.get(i) + "," + "," + "\n";
						sLine = 0 + "," +  0.0 + "," + lstGroundTruthPlaceNames.get(i) + "," + "," + "\n";
	
						fwKeywordMatchingEvalResultFile.write(sLine);
						
						//Write Matching score line
						sMatchingScoreLine = "";
						
						for (j=0; j<lstPlaceWebData.size(); j++) {
							sMatchingScoreLine = sMatchingScoreLine + 0.0 + ",";
						}
						
						sMatchingScoreLine = sMatchingScoreLine + "\n";
						
						fwKeywordMatchingConfusionMatrixFile.write(sMatchingScoreLine);
						
					}
				}
				
				fwMatchedCommonWordsFile.close();
				fwFinalPlaceAPDatabaseFile.close();
				fwKeywordMatchingEvalResultFile.close();
				fwKeywordMatchingConfusionMatrixFile.close();
	
			} catch (Exception e) {
				 
			}
		
		}
		
	}
	
	
}
