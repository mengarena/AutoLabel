package com.autolabel;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class SubsetComparisonProcessor {

	private String m_sWebDataFile;
	private String m_sCrowdsourcedDataFolder;
	private String m_sPlaceApDbFileDataFolder;
	private int m_nAPNum;
	private int m_nFrameCnt;
	private List<Integer> m_lstnFrameCnt;
	private int m_nSubsetPlaceCnt;
	private int m_nSubsetCnt;
	
	public SubsetComparisonProcessor() {
		// TODO Auto-generated constructor stub
	}

	public SubsetComparisonProcessor(String sWebDataFile, String sCrowdsourcedDataFolder, String sPlaceApDbFileDataFolder, int nAPNum, int nFrameCnt, int nSubsetPlaceCnt, int nSubsetCnt) {
		m_sWebDataFile = sWebDataFile;
		m_sCrowdsourcedDataFolder = sCrowdsourcedDataFolder;
		m_sPlaceApDbFileDataFolder = sPlaceApDbFileDataFolder;
		m_nAPNum = nAPNum;
		m_nFrameCnt = nFrameCnt;
		m_nSubsetPlaceCnt = nSubsetPlaceCnt;
		m_nSubsetCnt = nSubsetCnt;
	}
	
	public SubsetComparisonProcessor(String sWebDataFile, String sCrowdsourcedDataFolder, String sPlaceApDbFileDataFolder, int nAPNum, List<Integer> lstnFrameCnt, int nSubsetPlaceCnt, int nSubsetCnt) {
		m_sWebDataFile = sWebDataFile;
		m_sCrowdsourcedDataFolder = sCrowdsourcedDataFolder;
		m_sPlaceApDbFileDataFolder = sPlaceApDbFileDataFolder;
		m_nAPNum = nAPNum;
		m_lstnFrameCnt = lstnFrameCnt;
		m_nSubsetPlaceCnt = nSubsetPlaceCnt;
		m_nSubsetCnt = nSubsetCnt;
	}
	
	
	public void compareSubset() {
		List<List<Integer>> lstlstSubsetPlaceIdx = new ArrayList<List<Integer>>();
		
		//Get a series of subset place index
		lstlstSubsetPlaceIdx = Utility.getRandomNumSet(Utility.TOTAL_PLACE_CNT, m_nSubsetPlaceCnt, m_nSubsetCnt);
		
		System.out.println("Using Place Name.....");
		
		//With PlaceName
		Utility.MATCH_WITH_PLACENAME = true;
		
		for (int k=0; k<m_lstnFrameCnt.size(); k++) {
			System.out.println("Frame Cnt: " + m_lstnFrameCnt.get(k));
			compareSubset(lstlstSubsetPlaceIdx, m_lstnFrameCnt.get(k));
		}
		
		//Without PlaceName
		System.out.println("Not Using Place Name.....");
		Utility.MATCH_WITH_PLACENAME = false;
		
		for (int k=0; k<m_lstnFrameCnt.size(); k++) {
			System.out.println("Frame Cnt: " + m_lstnFrameCnt.get(k));
			compareSubset(lstlstSubsetPlaceIdx, m_lstnFrameCnt.get(k));
		}	
		
		//Calcualte Ranking
		System.out.println("Calculating Ranking.....");
		for (int i=0; i<lstlstSubsetPlaceIdx.size(); i++) {
			String sResultSubFolder = "" + (i+1);
						
			String sMatchingResultFolder = m_sPlaceApDbFileDataFolder + File.separator + sResultSubFolder;
			
			ProcessEvaluationData.CalculateRankAllSub(sMatchingResultFolder);
		}
		
	}
	
	
	
	public void compareSubset_MultipleIndexSet(int nIndexSetNum) {
		List<List<Integer>> lstlstSubsetPlaceIdx = new ArrayList<List<Integer>>();
		
		//Get a series of subset place index
		lstlstSubsetPlaceIdx = Utility.getRandomNumSet(Utility.TOTAL_PLACE_CNT, m_nSubsetPlaceCnt, m_nSubsetCnt);
		
		System.out.println("Using Place Name.....");
		
		//With PlaceName
		Utility.MATCH_WITH_PLACENAME = true;
		
		for (int k=0; k<m_lstnFrameCnt.size(); k++) {
			System.out.println("Frame Cnt: " + m_lstnFrameCnt.get(k));
			compareSubset_MultipleIndexSet(lstlstSubsetPlaceIdx, m_lstnFrameCnt.get(k), nIndexSetNum);
		}
		
		//Without PlaceName
		System.out.println("Not Using Place Name.....");
		Utility.MATCH_WITH_PLACENAME = false;
		
		for (int k=0; k<m_lstnFrameCnt.size(); k++) {
			System.out.println("Frame Cnt: " + m_lstnFrameCnt.get(k));
			compareSubset_MultipleIndexSet(lstlstSubsetPlaceIdx, m_lstnFrameCnt.get(k), nIndexSetNum);
		}	
		
		//Calcualte Ranking
		System.out.println("Calculating Ranking.....");
		for (int i=0; i<lstlstSubsetPlaceIdx.size(); i++) {
			String sResultSubFolder = "" + (i+1);
						
			String sMatchingResultFolder = m_sPlaceApDbFileDataFolder + File.separator + sResultSubFolder;
			
			ProcessEvaluationData.CalculateRankAllSub(sMatchingResultFolder);
		}		
	}
	
	
	
	//This function generate different subset of Web and OCR data to compare
	//First, it generate a subset of Web and hence get the corresponding OCR set 
	//(use the place name of folder, so the folder in OCR parent folder must be meaningful and the same as the place name in PlaceKeywordList_xxx.csv
	//Second, it compare the Web subset and OCR subset
	public void compareSubset(List<List<Integer>> lstlstSubsetPlaceIdx, int nFrameCnt) {
		int i,j;
		
		List<Integer> lstPlaceIdx = new ArrayList<Integer>();
		List<AL_PlaceWebData> lstPlaceWebDataSubset = null;
		List<String> lstPlaceNameSubset = null;
		List<String> lstMergedGpsApOCRFiles = null;
		List<String> lstGroundTruthFilesSubset = null;
		List<AL_CrowdSourcedPlaceData> lstCrowdsourcedPlaceDataSubset = null;
		List<String> lstGroundTruthPlaceNamesSubset = null;
		String sResultSubFolder = "";
		String sMatchingResultFolder = "";
		
		String sFinalPlaceAPDatabaseFile = "";
		String sKeywordMatchingEvalResultFile = "";
		String sKeywordMatchingConfusionMatrixFile = "";
		String sMatchedCommonWordsFile = "";
		String sLine = "";
		String sGroundTruthPlaceName = "";
		String sMatchingScoreLine = "";
		
		ConstructMacPlaceMapping locConstructMacPlaceMapping = new ConstructMacPlaceMapping();
		
		List<AL_PlaceWebData> lstAllPlaceWebData = Utility.getPlaceKeywordsList(m_sWebDataFile);
		
		if (lstAllPlaceWebData == null || lstAllPlaceWebData.size() == 0) return;
		
		for (i=0; i<lstlstSubsetPlaceIdx.size(); i++) {
			System.out.println("------------- Subset ------" + (i+1));
			
			lstPlaceIdx = lstlstSubsetPlaceIdx.get(i);   //Value starts from 1
			lstPlaceWebDataSubset = new ArrayList<AL_PlaceWebData>();
			lstPlaceNameSubset = new ArrayList<String>();
			lstMergedGpsApOCRFiles = new ArrayList<String>();
			lstGroundTruthFilesSubset = new ArrayList<String>();
			lstCrowdsourcedPlaceDataSubset = new ArrayList<AL_CrowdSourcedPlaceData>();
			lstGroundTruthPlaceNamesSubset = new ArrayList<String>();
			
			//Get the subset Web Data & Place Names
			for (j=0; j<lstPlaceIdx.size(); j++) {
				lstPlaceWebDataSubset.add(lstAllPlaceWebData.get(lstPlaceIdx.get(j)-1));  //Index starts from 0
				lstPlaceNameSubset.add(lstAllPlaceWebData.get(lstPlaceIdx.get(j)-1).getPlaceName());
			}
			
			//Processed and get crowdsourced data and ground truth
			Utility.getCrowdsourcedMergedGroundTruthFilesByPlaceName(m_sCrowdsourcedDataFolder, lstPlaceNameSubset, lstMergedGpsApOCRFiles, lstGroundTruthFilesSubset, nFrameCnt);  //Get the list of MergedGpsApOCR.csv files and GroundTruth.csv files
			Utility.getCrowdsourcedMergedGroundTruth(lstMergedGpsApOCRFiles, lstGroundTruthFilesSubset, m_nAPNum, lstCrowdsourcedPlaceDataSubset, lstGroundTruthPlaceNamesSubset);
			
			sResultSubFolder = "" + (i+1);
			
			Utility.createFolder(m_sPlaceApDbFileDataFolder, sResultSubFolder);
			
			sMatchingResultFolder = m_sPlaceApDbFileDataFolder + File.separator + sResultSubFolder;
			
			sFinalPlaceAPDatabaseFile = Utility.getPlaceAPDatabaseFile_S(sMatchingResultFolder, nFrameCnt);
			sKeywordMatchingEvalResultFile = Utility.getKeywordMatchingEvalResultFile_S(sMatchingResultFolder, nFrameCnt);
			sKeywordMatchingConfusionMatrixFile = Utility.getKeywordMatchingConfusionMatrixFile_S(sMatchingResultFolder, nFrameCnt);
			sMatchedCommonWordsFile = Utility.getMatchedCommonWordsFile_S(sMatchingResultFolder, nFrameCnt);

			sLine = "";
			
			try {
				FileWriter fwKeywordMatchingConfusionMatrixFile = new FileWriter(sKeywordMatchingConfusionMatrixFile, false);   //Overwrite
				FileWriter fwKeywordMatchingEvalResultFile = new FileWriter(sKeywordMatchingEvalResultFile, false);   //Overwrite
				FileWriter fwFinalPlaceAPDatabaseFile = new FileWriter(sFinalPlaceAPDatabaseFile, false);   //Overwrite
				FileWriter fwMatchedCommonWordsFile = new FileWriter(sMatchedCommonWordsFile, false);  //Overwrite
				
				//First Line: Ground Truth Place Names
				for (int ii=0; ii<lstGroundTruthPlaceNamesSubset.size(); ii++) {
					sLine = sLine + lstGroundTruthPlaceNamesSubset.get(ii) + ",";
				}
				
				sLine = sLine + "\n";
				
				fwKeywordMatchingConfusionMatrixFile.write(sLine);
				
				sLine = "";
				
				//Second Line: Candidate Place Names (How close the crowdsourced data will be recognized as these places is to be measured below) 
				for (int ii=0; ii<lstPlaceWebDataSubset.size(); ii++) {
					sLine = sLine + lstPlaceWebDataSubset.get(ii).getPlaceName() + ",";
				}
				
				sLine = sLine + "\n";
				
				fwKeywordMatchingConfusionMatrixFile.write(sLine);
				
				//This part does matching
				for (int ii=0; ii<lstCrowdsourcedPlaceDataSubset.size(); ii++) {
					AL_CrowdSourcedPlaceData locCrowdSourcedPlaceData = lstCrowdsourcedPlaceDataSubset.get(ii);
					List<Double> lstfMaxMatchingScore = new ArrayList<Double>();
					double fMaxMatchingScore = 0.0f;
					List<Double> lstfMatchingScore = new ArrayList<Double>();
					
					sGroundTruthPlaceName = lstGroundTruthPlaceNamesSubset.get(ii);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywords(lstPlaceWebData, locCrowdSourcedPlaceData, fMatchingScore);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithSingleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, fMaxMatchingScore, lstfMatchingScore);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithDoubleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, fMaxMatchingScore, lstfMatchingScore);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithDoubleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, lstfMaxMatchingScore, lstfMatchingScore);
					AL_PlaceAPInfo placeAPInfo = locConstructMacPlaceMapping.matchKeywordsWithTFIDF(lstPlaceWebDataSubset, locCrowdSourcedPlaceData, lstfMaxMatchingScore, lstfMatchingScore, fwMatchedCommonWordsFile, sGroundTruthPlaceName);
							
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
						sLine = 1 + "," + fMaxMatchingScore + "," + lstGroundTruthPlaceNamesSubset.get(ii) + "," + placeAPInfo.getPlaceName() + "," +  "\n";
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
						sLine = 0 + "," +  0.0 + "," + lstGroundTruthPlaceNamesSubset.get(ii) + "," + "," + "\n";

						fwKeywordMatchingEvalResultFile.write(sLine);
						
						//Write Matching score line
						sMatchingScoreLine = "";
						
						for (j=0; j<lstPlaceWebDataSubset.size(); j++) {
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

	
	
	//This function generate different subset of Web and OCR data to compare
	//First, it generate a subset of Web and hence get the corresponding OCR set 
	//(use the place name of folder, so the folder in OCR parent folder must be meaningful and the same as the place name in PlaceKeywordList_xxx.csv
	//Second, it compare the Web subset and OCR subset
	public void compareSubset_MultipleIndexSet(List<List<Integer>> lstlstSubsetPlaceIdx, int nFrameCnt, int nIndexSetNum) {
		int i,j;
		
		List<Integer> lstPlaceIdx = new ArrayList<Integer>();
		List<AL_PlaceWebData> lstPlaceWebDataSubset = null;
		List<String> lstPlaceNameSubset = null;
		List<String> lstMergedGpsApOCRFiles = null;
		List<String> lstGroundTruthFilesSubset = null;
		List<AL_CrowdSourcedPlaceData> lstCrowdsourcedPlaceDataSubset = null;
		List<String> lstGroundTruthPlaceNamesSubset = null;
		String sResultSubFolder = "";
		String sMatchingResultFolder = "";
		
		String sFinalPlaceAPDatabaseFile = "";
		String sKeywordMatchingEvalResultFile = "";
		String sKeywordMatchingConfusionMatrixFile = "";
		String sMatchedCommonWordsFile = "";
		String sLine = "";
		String sGroundTruthPlaceName = "";
		String sMatchingScoreLine = "";
		
		ConstructMacPlaceMapping locConstructMacPlaceMapping = new ConstructMacPlaceMapping();
		
		List<AL_PlaceWebData> lstAllPlaceWebData = Utility.getPlaceKeywordsList(m_sWebDataFile);
		
		if (lstAllPlaceWebData == null || lstAllPlaceWebData.size() == 0) return;
		
		for (i=0; i<lstlstSubsetPlaceIdx.size(); i++) {
			System.out.println("------------- Subset ------" + (i+1));
			
			lstPlaceIdx = lstlstSubsetPlaceIdx.get(i);   //Value starts from 1
			lstPlaceWebDataSubset = new ArrayList<AL_PlaceWebData>();
			lstPlaceNameSubset = new ArrayList<String>();
			lstMergedGpsApOCRFiles = new ArrayList<String>();
			lstGroundTruthFilesSubset = new ArrayList<String>();
			lstCrowdsourcedPlaceDataSubset = new ArrayList<AL_CrowdSourcedPlaceData>();
			lstGroundTruthPlaceNamesSubset = new ArrayList<String>();
			
			//Get the subset Web Data & Place Names
			for (j=0; j<lstPlaceIdx.size(); j++) {
				lstPlaceWebDataSubset.add(lstAllPlaceWebData.get(lstPlaceIdx.get(j)-1));  //Index starts from 0
				lstPlaceNameSubset.add(lstAllPlaceWebData.get(lstPlaceIdx.get(j)-1).getPlaceName());
			}
			
			//Processed and get crowdsourced data and ground truth
			Utility.getCrowdsourcedMergedGroundTruthFilesByPlaceName_MultipleIndexSet(m_sCrowdsourcedDataFolder, lstPlaceNameSubset, lstMergedGpsApOCRFiles, lstGroundTruthFilesSubset, nFrameCnt, nIndexSetNum);  //Get the list of MergedGpsApOCR.csv files and GroundTruth.csv files
			Utility.getCrowdsourcedMergedGroundTruth(lstMergedGpsApOCRFiles, lstGroundTruthFilesSubset, m_nAPNum, lstCrowdsourcedPlaceDataSubset, lstGroundTruthPlaceNamesSubset);
			
			sResultSubFolder = "" + (i+1);
			
			Utility.createFolder(m_sPlaceApDbFileDataFolder, sResultSubFolder);
			
			sMatchingResultFolder = m_sPlaceApDbFileDataFolder + File.separator + sResultSubFolder;
			
			sFinalPlaceAPDatabaseFile = Utility.getPlaceAPDatabaseFile_S(sMatchingResultFolder, nFrameCnt);
			sKeywordMatchingEvalResultFile = Utility.getKeywordMatchingEvalResultFile_S(sMatchingResultFolder, nFrameCnt);
			sKeywordMatchingConfusionMatrixFile = Utility.getKeywordMatchingConfusionMatrixFile_S(sMatchingResultFolder, nFrameCnt);
			sMatchedCommonWordsFile = Utility.getMatchedCommonWordsFile_S(sMatchingResultFolder, nFrameCnt);

			sLine = "";
			
			try {
				FileWriter fwKeywordMatchingConfusionMatrixFile = new FileWriter(sKeywordMatchingConfusionMatrixFile, false);   //Overwrite
				FileWriter fwKeywordMatchingEvalResultFile = new FileWriter(sKeywordMatchingEvalResultFile, false);   //Overwrite
				FileWriter fwFinalPlaceAPDatabaseFile = new FileWriter(sFinalPlaceAPDatabaseFile, false);   //Overwrite
				FileWriter fwMatchedCommonWordsFile = new FileWriter(sMatchedCommonWordsFile, false);  //Overwrite
				
				//First Line: Ground Truth Place Names
				for (int ii=0; ii<lstGroundTruthPlaceNamesSubset.size(); ii++) {
					sLine = sLine + lstGroundTruthPlaceNamesSubset.get(ii) + ",";
				}
				
				sLine = sLine + "\n";
				
				fwKeywordMatchingConfusionMatrixFile.write(sLine);
				
				sLine = "";
				
				//Second Line: Candidate Place Names (How close the crowdsourced data will be recognized as these places is to be measured below) 
				for (int ii=0; ii<lstPlaceWebDataSubset.size(); ii++) {
					sLine = sLine + lstPlaceWebDataSubset.get(ii).getPlaceName() + ",";
				}
				
				sLine = sLine + "\n";
				
				fwKeywordMatchingConfusionMatrixFile.write(sLine);
				
				//This part does matching
				for (int ii=0; ii<lstCrowdsourcedPlaceDataSubset.size(); ii++) {
					AL_CrowdSourcedPlaceData locCrowdSourcedPlaceData = lstCrowdsourcedPlaceDataSubset.get(ii);
					List<Double> lstfMaxMatchingScore = new ArrayList<Double>();
					double fMaxMatchingScore = 0.0f;
					List<Double> lstfMatchingScore = new ArrayList<Double>();
					
					sGroundTruthPlaceName = lstGroundTruthPlaceNamesSubset.get(ii);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywords(lstPlaceWebData, locCrowdSourcedPlaceData, fMatchingScore);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithSingleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, fMaxMatchingScore, lstfMatchingScore);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithDoubleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, fMaxMatchingScore, lstfMatchingScore);
					//AL_PlaceAPInfo placeAPInfo = m_ConstructMacPlaceMapping.matchKeywordsWithDoubleWeights(lstPlaceWebData, locCrowdSourcedPlaceData, lstfMaxMatchingScore, lstfMatchingScore);
					AL_PlaceAPInfo placeAPInfo = locConstructMacPlaceMapping.matchKeywordsWithTFIDF(lstPlaceWebDataSubset, locCrowdSourcedPlaceData, lstfMaxMatchingScore, lstfMatchingScore, fwMatchedCommonWordsFile, sGroundTruthPlaceName);
							
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
						sLine = 1 + "," + fMaxMatchingScore + "," + lstGroundTruthPlaceNamesSubset.get(ii) + "," + placeAPInfo.getPlaceName() + "," +  "\n";
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
						sLine = 0 + "," +  0.0 + "," + lstGroundTruthPlaceNamesSubset.get(ii) + "," + "," + "\n";

						fwKeywordMatchingEvalResultFile.write(sLine);
						
						//Write Matching score line
						sMatchingScoreLine = "";
						
						for (j=0; j<lstPlaceWebDataSubset.size(); j++) {
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
