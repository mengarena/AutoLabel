package com.autolabel;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompareSimilarity {

	public CompareSimilarity() {
		// TODO Auto-generated constructor stub
	}

	public static void CalSimilarity(String sSrcDataFileFolder, String sCandidateDataFileFolder, String sSimilarityResultFileDataFolder, int nSimilarityType, int nFrameCnt) {
		String sResultFile = Utility.getSimilarityResultFile(sSrcDataFileFolder, sSimilarityResultFileDataFolder, nSimilarityType, nFrameCnt);
		
		System.out.println("======" + sResultFile);
		
		if (sResultFile.length() == 0) return;
		
		System.out.println("====22==" + sResultFile);
		
		if (nSimilarityType == Utility.SIMILARITY_WEB) {
			CalWebSimilarity(sSrcDataFileFolder, sCandidateDataFileFolder, sResultFile);
		} else {
			CalStoreOCRSimilarity(sSrcDataFileFolder, sCandidateDataFileFolder, sResultFile, nFrameCnt);
		}
	}
	
	///Old Begin
	//sSrcDataFolder Should be the parent folder of "WebImage", "WebText", "WebText_WordCount_Temp"
	private static void CalWebSimilarity_fromFolder(String sSrcDataFolder, String sResultFile) {
		List<String> lstFullWebTextFiles = Utility.getFullWebTextFileList(sSrcDataFolder);
		String sPlaceName = "";
		
		int nWebCnt = 0;
		double fMatchingScore = 0.0;
		
		List<List<String>> lstlstMetaKeyword = new ArrayList<List<String>>();
		List<String> lstPlaceName = new ArrayList<String>();
		//List<Map<String, Double>> lstmapWebKeywordWeight = new ArrayList<Map<String, Double>>();
		
		List<List<String>> lstlstKeyword = new ArrayList<List<String>>();
		List<List<Double>> lstlstKeywordWeight = new ArrayList<List<Double>>();
		
		List<List<Double>> lstlstIDF = new ArrayList<List<Double>>();
		List<List<Double>> lstlstOverallWeight = new ArrayList<List<Double>>();
		
		//First, extract all the information from all the files
		for (String sFilePathName : lstFullWebTextFiles) {
			//sPlaceName = Utility.getPlaceNameFromFullWebTextFileName(sFilePathName);
			List<String> lstMetaKeyword = new ArrayList<String>();
			List<String> lstKeyword = new ArrayList<String>();
			List<Double> lstKeywordWeight = new ArrayList<Double>();
			sPlaceName = "";	
			sPlaceName = Utility.getFullWebPlaceNameKeyword(sFilePathName, lstMetaKeyword, lstKeyword, lstKeywordWeight).trim();
			if (sPlaceName.length() == 0) continue;
			
			//if (mapWebKeywordWeight != null && lstMetaKeyword != null) {
				nWebCnt = nWebCnt + 1;
				lstPlaceName.add(sPlaceName);
				lstlstMetaKeyword.add(lstMetaKeyword);
				
				lstlstKeyword.add(lstKeyword);
				lstlstKeywordWeight.add(lstKeywordWeight);
			//}
		}
		
		//Here calculate IDF and Overall Weight for each word
		lstlstIDF = Utility.CalculateIDF(lstlstKeyword);
			
		lstlstOverallWeight = Utility.CalculateOverallWeight(lstlstKeywordWeight, lstlstIDF);
				
		//Here below, do mutual comparison between all these web data
		double farrMatchingScores[][] = new double[nWebCnt][nWebCnt]; //Save matching scores
		
		for (int i=0; i<nWebCnt; i++) {
			fMatchingScore = 0.0;
			for (int j=0; j<nWebCnt; j++) {
				if (i != j) {
					fMatchingScore = Utility.CalculateWebMatchingScore(lstlstMetaKeyword.get(i), lstlstKeyword.get(i), lstlstOverallWeight.get(i), lstlstMetaKeyword.get(j), lstlstKeyword.get(j), lstlstOverallWeight.get(j));
				} else {
					fMatchingScore = 1.0;
				}
				
				farrMatchingScores[i][j] = fMatchingScore;
			}
		}
		
		//Write into result file
		try {
			FileWriter fwWebSimilarityFile = new FileWriter(sResultFile, false);   //Overwrite
			
			String sLine = "";
			
			for (String sPlaceNameTmp: lstPlaceName) {
				sLine = sLine + sPlaceNameTmp + ",";
			}
			
			sLine = sLine + "\n";
			//First Line: Ground Truth Place Names
			fwWebSimilarityFile.write(sLine);
			//Second Line: Candidate Place Names
			fwWebSimilarityFile.write(sLine);
			
			for (int i=0; i<nWebCnt; i++) {
				sLine = "";
				for (int j=0; j<nWebCnt; j++) {
					//sLine = sLine + farrMatchingScores[i][j] + ",";
					sLine = sLine + String.format("%.3f", farrMatchingScores[i][j]) + ",";
				}
				sLine = sLine + "\n";
				fwWebSimilarityFile.write(sLine);
			}
			
			fwWebSimilarityFile.close();
		} catch (Exception e) {
			 
		}
		
	}

	///Old End
	
	
	//sSrcDataFolder Should be the parent folder of "WebImage", "WebText", "WebText_WordCount_Temp"
	private static void CalWebSimilarity(String sSrcWebDataFile, String sCandidateWebDataFile, String sResultFile) {
		int i,j;
		
		//First, extract all the information from the web data files, i.e. PlaceKeywordList_xxxx.csv
		List<AL_PlaceWebData> lstSrcPlaceWebData = Utility.getPlaceKeywordsList(sSrcWebDataFile);
		List<AL_PlaceWebData> lstCandidatePlaceWebData = Utility.getPlaceKeywordsList(sCandidateWebDataFile);

		List<String> lstSrcPlaceName = new ArrayList<String>();

		List<String> lstCandidatePlaceName = new ArrayList<String>();
		
		List<List<Double>> lstlstMatchingScore = new ArrayList<List<Double>>();
		
		boolean bIgnoreIdx = false;
		
		for (i=0; i<lstSrcPlaceWebData.size(); i++) {
			AL_PlaceWebData placeWebData = lstSrcPlaceWebData.get(i);
			lstSrcPlaceName.add(placeWebData.getPlaceName());
		}

		
//		for (i=0; i<lstPlaceWebData.size(); i++) {
//			List<AL_KeywordWeight> lstUnknown = lstPlaceWebData.get(i).getKeywordWeight();
//			
//			lstPlaceName = new ArrayList<String>();
//			List<Double> lstfMatchingScore = new ArrayList<Double>();
//			
//			Utility.CalculateCollectionMatching(lstUnknown, lstPlaceWebData, lstPlaceName, lstfMatchingScore, i);
//			lstlstMatchingScore.add(lstfMatchingScore);
//		}

		if (sSrcWebDataFile.trim().compareToIgnoreCase(sCandidateWebDataFile.trim()) == 0)  bIgnoreIdx = true;  //Matching between the same set, ignore the diagnoal
		
		
		for (i=0; i<lstSrcPlaceWebData.size(); i++) {
			List<AL_KeywordWeight> lstUnknown = lstSrcPlaceWebData.get(i).getKeywordWeight();
			
			lstCandidatePlaceName = new ArrayList<String>();
			List<Double> lstfMatchingScore = new ArrayList<Double>();
			
			if (bIgnoreIdx == true) {
				Utility.CalculateCollectionMatchingWeb(lstUnknown, lstCandidatePlaceWebData, lstCandidatePlaceName, lstfMatchingScore, i);
			} else {
				Utility.CalculateCollectionMatchingWeb(lstUnknown, lstCandidatePlaceWebData, lstCandidatePlaceName, lstfMatchingScore, -1);	
			}
			
			lstlstMatchingScore.add(lstfMatchingScore);
		}
		
		
		//Write into result file
		try {
			FileWriter fwWebSimilarityFile = new FileWriter(sResultFile, false);   //Overwrite
			
			//First Line: Ground Truth Place Names
			String sLine = "";

			for (String sSrcPlaceNameTmp: lstSrcPlaceName) {
				sLine = sLine + sSrcPlaceNameTmp + ",";
			}
			
			sLine = sLine + "\n";
			
			fwWebSimilarityFile.write(sLine);
	
			//Second Line: Candidate Place Names
			sLine = "";
			for (String sCandidatePlaceNameTmp: lstCandidatePlaceName) {
				sLine = sLine + sCandidatePlaceNameTmp + ",";
			}
			
			sLine = sLine + "\n";
			
			fwWebSimilarityFile.write(sLine);
			
			for (i=0; i<lstlstMatchingScore.size(); i++) {
				sLine = "";
				
				List<Double> lstfMatchingScore = lstlstMatchingScore.get(i);
				for (j=0; j<lstfMatchingScore.size(); j++) {
					//sLine = sLine + farrMatchingScores[i][j] + ",";
					sLine = sLine + String.format("%.3f", lstfMatchingScore.get(j).doubleValue()) + ",";
				}
				sLine = sLine + "\n";
				fwWebSimilarityFile.write(sLine);
			}
			
			fwWebSimilarityFile.close();
		} catch (Exception e) {
			 
		}
		
	}
	
	
	
	//sSrcDataFolder should be the parent folder of placename sub folders
	//Only Src could be frame sample
	private static void CalStoreOCRSimilarity(String sSrcDataFolder, String sCandidateDataFolder, String sResultFile, int nFrameCnt) {
		List<String> lstSrcMergedFullOCRFiles = new ArrayList<String>();
		List<String> lstSrcGroundTruthFiles = new ArrayList<String>();
		List<String> lstSrcGroundTruthPlaceName = new ArrayList<String>();

		List<String> lstCandidateMergedFullOCRFiles = new ArrayList<String>();
		List<String> lstCandidateGroundTruthFiles = new ArrayList<String>();
		List<String> lstCandidateGroundTruthPlaceName = new ArrayList<String>();
		
		int nSrcStoreCnt = 0;
		int nCandidateStoreCnt = 0;

		String sGroundTruthPlaceName = "";
		double fMatchingScore = 0.0;
				
		int i,j;
		List<List<String>> lstlstSrcInStoreOCRedWord = new ArrayList<List<String>>();
		List<List<Double>> lstlstSrcInStoreOCRedWordWeight = new ArrayList<List<Double>>();

		List<List<String>> lstlstCandidateInStoreOCRedWord = new ArrayList<List<String>>();
		List<List<Double>> lstlstCandidateInStoreOCRedWordWeight = new ArrayList<List<Double>>();
		
		List<List<Double>> lstlstCandidateIDF = new ArrayList<List<Double>>();
		List<List<Double>> lstlstCandidateOverallWeight = new ArrayList<List<Double>>();

		List<List<Double>> lstlstSrcIDF = new ArrayList<List<Double>>();
		List<List<Double>> lstlstSrcOverallWeight = new ArrayList<List<Double>>();
		
		List<List<Double>> lstlstMatchingScore = new ArrayList<List<Double>>();
		boolean bSelfMatching = false;
		List<String> lstCandidatePlaceName = new ArrayList<String>();
		
		if (sSrcDataFolder.trim().compareToIgnoreCase(sCandidateDataFolder.trim()) == 0) {
			bSelfMatching = true;
		}
		
//		Utility.getCrowdsourcedFullMergedGroundTruthFiles(sSrcDataFolder, lstMergedFullOCRFiles, lstGroundTruthFiles);
		Utility.getCrowdsourcedFullMergedGroundTruthFiles_StoreOCR(sSrcDataFolder, lstSrcMergedFullOCRFiles, lstSrcGroundTruthFiles, nFrameCnt);			
		
		Utility.getCrowdsourcedFullMergedGroundTruthFiles_StoreOCR(sCandidateDataFolder, lstCandidateMergedFullOCRFiles, lstCandidateGroundTruthFiles, Utility.NO_FRAME_SAMPLE);
		
		nSrcStoreCnt = lstSrcMergedFullOCRFiles.size();
		nCandidateStoreCnt = lstCandidateMergedFullOCRFiles.size();
		
		//Get OCRed word,weight, Src ground truth place name from files
		for (i=0; i<nSrcStoreCnt; i++) {
//			public static void getInStoreOCRedWordWeight(String sFilePathName, List<String> lstWord, List<Double> lstWordWeight) {				
			List<String> lstWord = new ArrayList<String>();
			List<Double> lstWordWeight = new ArrayList<Double>();
			
			Utility.getInStoreOCRedWordWeight(lstSrcMergedFullOCRFiles.get(i), lstWord, lstWordWeight);
			
			sGroundTruthPlaceName = Utility.getGroundTruthPlaceName(lstSrcGroundTruthFiles.get(i));
			
			if (lstWord == null || lstWord.size() == 0 || sGroundTruthPlaceName.length() == 0) continue;
			
			lstSrcGroundTruthPlaceName.add(sGroundTruthPlaceName);
			
			lstlstSrcInStoreOCRedWord.add(lstWord);
			lstlstSrcInStoreOCRedWordWeight.add(lstWordWeight);
		}
		

		//Get OCRed word,weight, Candidate ground truth place name from files
		for (i=0; i<nCandidateStoreCnt; i++) {
//			public static void getInStoreOCRedWordWeight(String sFilePathName, List<String> lstWord, List<Double> lstWordWeight) {				
			List<String> lstWord = new ArrayList<String>();
			List<Double> lstWordWeight = new ArrayList<Double>();
			
			Utility.getInStoreOCRedWordWeight(lstCandidateMergedFullOCRFiles.get(i), lstWord, lstWordWeight);
			
			sGroundTruthPlaceName = Utility.getGroundTruthPlaceName(lstCandidateGroundTruthFiles.get(i));
			
			if (lstWord == null || lstWord.size() == 0 || sGroundTruthPlaceName.length() == 0) continue;
			
			lstCandidateGroundTruthPlaceName.add(sGroundTruthPlaceName);
			
			lstlstCandidateInStoreOCRedWord.add(lstWord);
			lstlstCandidateInStoreOCRedWordWeight.add(lstWordWeight);
		}

		//Matching
		for (i=0; i<lstlstSrcInStoreOCRedWord.size(); i++) {
			List<String> lstSrcWord = lstlstSrcInStoreOCRedWord.get(i);
			List<Double> lstSrcTF = lstlstSrcInStoreOCRedWordWeight.get(i);
			List<Double> lstfMatchingScore = new ArrayList<Double>();
			lstCandidatePlaceName = new ArrayList<String>();
			
			if (bSelfMatching == true) {
				Utility.CalculateCollectionMatching(lstSrcWord, lstSrcTF, lstlstCandidateInStoreOCRedWord, lstlstCandidateInStoreOCRedWordWeight, lstfMatchingScore, i);
			} else {
				Utility.CalculateCollectionMatching(lstSrcWord, lstSrcTF, lstlstCandidateInStoreOCRedWord, lstlstCandidateInStoreOCRedWordWeight, lstfMatchingScore, -1);				
			}
			
			lstlstMatchingScore.add(lstfMatchingScore);
		}
		
		
		//Write into result file
		try {
			FileWriter fwInStoreOCRSimilarityFile = new FileWriter(sResultFile, false);   //Overwrite
			
			//First Line: Ground Truth Place Names
			String sLine = "";
			
			for (String sGroundTruthPlaceNameTmp: lstSrcGroundTruthPlaceName) {
				sLine = sLine + sGroundTruthPlaceNameTmp + ",";
			}
			
			sLine = sLine + "\n";
			
			fwInStoreOCRSimilarityFile.write(sLine);

			//Second Line: Candidate Place Names
			sLine = "";
			for (String sCandidatePlaceNameTmp: lstCandidateGroundTruthPlaceName) {
				sLine = sLine + sCandidatePlaceNameTmp + ",";
			}
			
			sLine = sLine + "\n";
			
			fwInStoreOCRSimilarityFile.write(sLine);
			
			for (i=0; i<lstlstMatchingScore.size(); i++) {
				List<Double> lstfMatchingScore = lstlstMatchingScore.get(i);
				sLine = "";
				for (j=0; j<lstfMatchingScore.size(); j++) {
					//sLine = sLine + farrMatchingScores[i][j] + ",";
					sLine = sLine + String.format("%.3f", lstfMatchingScore.get(j)) + ",";

				}
				sLine = sLine + "\n";
				fwInStoreOCRSimilarityFile.write(sLine);
			}
			
			fwInStoreOCRSimilarityFile.close();
		} catch (Exception e) {
			 
		}
		
		
	}

		
}
