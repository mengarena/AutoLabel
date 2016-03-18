package com.autolabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClusteringProcessorSmart {

//	private static String m_sPlaceAPInfoFile = "E:\\UIUC\\Run\\SpecialWiFiMap_ForClustering\\Mall_sub1_PlaceApDatabaseFile_Old_NewWiFi.csv";
//	private static String m_sPlaceAPInfoFile = "E:\\UIUC\\Run\\SpecialWiFiMap_ForClustering\\Street_PlaceApDatabaseFile_withPlaceNameMatching_Old_NewWiFi.csv";
//	private static String m_sPlaceAPInfoFile = "E:\\UIUC\\Run\\SpecialWiFiMap_ForClustering\\Mall_Sub2_PlaceApDatabaseFile_Old_NewWiFi.csv";

//	private static String m_sPlaceAPInfoFile = "E:\\UIUC\\Run\\SpecialWiFiMap_ForClustering\\Mall_sub1_PlaceApDatabaseFile_OnlyNewWiFi.csv";	

	private static String m_sPlaceAPInfoFile = "E:\\UIUC\\Run\\SpecialWiFiMap_ForClustering\\Street_PlaceApDatabaseFile_withPlaceNameMatching_OnlyNewWiFi.csv";
	
//	private static String m_sPlaceAPInfoFile = "E:\\UIUC\\Run\\SpecialWiFiMap_ForClustering\\Mall_sub2_PlaceApDatabaseFile_OnlyNewWiFi.csv";
	
	
	
	public ClusteringProcessorSmart() {
		// TODO Auto-generated constructor stub
	}
	
	public static void doCluster(String sWebDataFile, String sCrowdsourcedDataFolder, String sFrameIndexFile, String sResultFolder, int nTotalStoreCnt, int nMaxPicNumPerStore, int nLoopCnt) {
		String sResultFilePathNameFull = "";
		String sResultFilePathNameSimple = ""; 
	    String sResultFilePathNameStat = "";
		Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    List<String> lstPlaceNameList = new ArrayList<String>();
 	    List<List<String>> lstlstPlaceMac = Utility.getPlaceAPInfo(m_sPlaceAPInfoFile, lstPlaceNameList);  //Each place has a list of MAC
 	    
 	    if (lstlstPlaceMac.size() == 0) {
 	    	Utility.DEBUG_INFO("No WiFi Data!!!!");
 	    	return;
 	    }
 	    
		if (Utility.MATCH_WITH_PLACENAME == true) { 
			sResultFilePathNameSimple = sResultFolder + File.separator + "withPlaceName" + File.separator + "ClusteringResult_withPlaceName_" + nTotalStoreCnt + "_" + nMaxPicNumPerStore + "_" + nLoopCnt + "_" + spdCurrentTime.format(dtFileStart) + "_Simple.csv";
			sResultFilePathNameStat = sResultFolder + File.separator + "withPlaceName" + File.separator + "ClusteringResult_stat_withPlaceName.csv";
		} else {
			sResultFilePathNameSimple = sResultFolder + File.separator + "withoutPlaceName" + File.separator + "ClusteringResult_withoutPlaceName_" + nTotalStoreCnt + "_" + nMaxPicNumPerStore + "_" + nLoopCnt + "_" + spdCurrentTime.format(dtFileStart) + "_Simple.csv";			
			sResultFilePathNameStat = sResultFolder + File.separator + "withoutPlaceName" + File.separator + "ClusteringResult_stat_withoutPlaceName.csv";
		}
		
		//In result file, each line, besides the first one(full) or two(simple) fields, in the remaining fields, first half is Ground Truth Names, second half is Predicated Place Names
		//In each iteration, randomly select #store and up to #pic/store from each selected stores, which forms a set of pics, then carry out partition and matching	 
		for (int i=0; i<nLoopCnt; i++) {
			try {
				if (Utility.MATCH_WITH_PLACENAME == true) { 
					sResultFilePathNameFull = sResultFolder + File.separator + "withPlaceName" + File.separator + "ClusteringResult_withPlaceName_" + nTotalStoreCnt + "_" + nMaxPicNumPerStore + "_" + nLoopCnt + "_" + (i+1) + "_" + spdCurrentTime.format(dtFileStart) + "_Full.csv";
				} else {
					sResultFilePathNameFull = sResultFolder + File.separator + "withoutPlaceName" + File.separator + "ClusteringResult_withoutPlaceName_" + nTotalStoreCnt + "_" + nMaxPicNumPerStore + "_" + nLoopCnt + "_" + (i+1) + "_" + spdCurrentTime.format(dtFileStart) + "_Full.csv";
				}

				FileWriter fwClusteringFull = new FileWriter(sResultFilePathNameFull, true);   //Append
				FileWriter fwClusteringSimple = new FileWriter(sResultFilePathNameSimple, true);   //Append
				FileWriter fwClusteringStat = new FileWriter(sResultFilePathNameStat, true);   //Append
				
				
				Utility.DEBUG_INFO("=====================Loop:   " + (i+1));
				Utility.PrintCurrentTime();
				
				ProcessCluster(i+1, sWebDataFile, sCrowdsourcedDataFolder, sFrameIndexFile, nTotalStoreCnt, nMaxPicNumPerStore, fwClusteringFull, fwClusteringSimple, fwClusteringStat, lstlstPlaceMac, lstPlaceNameList);
				
				Utility.PrintCurrentTime();
				
				fwClusteringFull.close();
				fwClusteringSimple.close();
				fwClusteringStat.close();
				
			} catch (Exception e) {
				System.out.println("Error========================================" + e.toString());
				e.printStackTrace();
			}
		}
			
	}
	

	/* Logic:
	 *   The scenario simulated: In an area, which has X stores, we get a set of pics (say W pics), these pics are from a subset (say Y stores) of the X stores; 
	 *   we need to figure out the source of the W pics; i.e. among the W pics, w1 from Store y1; w2 from Store y2; w3 from Store y3...
	 *   
	 *   Steps:
	 *   1.  From a set of X candidate crowdsourced stores, randomly select N stores to study
	 *   2.  From each of the N stores, select up to M pics from each store
	 *   3.  Match each of M pics with all the X candidate store website; 
	 *       if none is matched, the pic is filtered; if one website is matched, the matched web store is considered the correct store for the pic; if more than one website matched, the source of these pics will be determined later
	 *   4.  For the 2+ matched pics, form possible combination based on their commonly matched websites, say we get S combinations 
	 *       It is possible/allowed that one combination only contains one pic
	 *   5.  Check common WiFi APs for all the S combinations, if the pics in one combinations do not have common WiFi APs, this combination is invalid; after this step we have T combinations
	 *   6.  Partition the pics based on the T combinations, in each partition, every pic only occurs once
	 *   7.  Match the crowdsourced data with web data for each partition. (In each partition, a combination is a unit). Calculate the overall matching score.
	 *   8.  From all the partitions, select the partition which has the highest overall matching score as the "correct" partition.
	 *   9.  Based on the "correct" partition, we have ground truth place for each pic and also the predicated place for each pic; by comparing the similarity between ground truth and predicated, we have a similarity score.
	 *       In calculating the similarity score, if one pic matches none website (in Step 3), the pic is not counted.
	 *   
	 */
	public static void ProcessCluster(int nLoopIndex, String sWebDataFile, String sCrowdsourcedDataFolder, String sFrameIndexFile, int nTotalStoreCnt, int nMaxPicNumPerStore, 
			                          FileWriter fwClusteringFull, FileWriter fwClusteringSimple, FileWriter fwClusteringStat, 
			                          List<List<String>> lstlstPlaceMac,  List<String> lstWiFiPlaceNameList) {
		int i,j,k;

		double fPartitionScoreSum = 0.0;
		double fMaxPartitionScoreSum = -1;

		String sMaxOnePartitionGroundTruthPlaceNames = "";
		String sMaxOnePartitionPredicatedPlaceNames = "";
		String sMaxOnePartitionFrameIndex = "";
		
		List<String> lstPlaceNameList = new ArrayList<String>();
		List<String> lstSubFolderFullPathList = new ArrayList<String>();
		
		List<String> lstSelectedFullPath = new ArrayList<String>();
		
		List<String> lstGroundTruthPlaceName = new ArrayList<String>();
		
		List<String> lstTotalSelectedFrames = new ArrayList<String>();
				
		List<String> lstTotalSelectedFramesRaw = new ArrayList<String>();
		List<String> lstTotalSelectedFramesGroundTruth = new ArrayList<String>();
		
		List<String> lstTotalSelectedFramesPredicatedPlaceName = new ArrayList<String>();
		List<List<Integer>> lstlstTotalSelectedFramesMatchedWebIndex = new ArrayList<List<Integer>>();
		
		//Frames which have 2+ web matched
		List<String> lstTotalSelectedFramesMultiMatch = new ArrayList<String>();
		List<String> lstTotalSelectedFramesRawMultiMatch = new ArrayList<String>();
				
		List<List<Integer>> lstlstTotalSelectedFramesMatchedWebIndexMultiMatch = new ArrayList<List<Integer>>();
		List<Integer> lstOriginalIndexForMultiMatchFrame = new ArrayList<Integer>();
		
		List<List<List<Integer>>> lstlstlstWebIndexMultiMatch = new ArrayList<List<List<Integer>>>();  
		
		List<AL_PlaceWebData> lstPlaceWebData = new ArrayList<AL_PlaceWebData>();
		
		String sWord; 
		int nOccurrenceCnt;
		List<List<Double>> lstlstPlaceWebDataIDF = new ArrayList<List<Double>>();
		double fIDF = 0.0;
		String sSubsetContent = "";
		String sSubsetContentRaw = "";
		String sGroundTruthPlaceName = "";
		String sSubsetGroundTruth = "";
		
		String sLineFull = "";
		String sLineSimple = "";
		String sStoreFrameStat = "";
		
		String sSubsetContentBase = "";
		String sSubsetContentBaseRaw = "";

		String sSubsetContentBaseCalB = "";
		String sSubsetContentBaseRawCalB = "";
		
		int nSubsetSize;

		List<String> lstCandidatePlaceNames = new ArrayList<String>();
		
		lstPlaceWebData = Utility.getPlaceKeywordsList(sWebDataFile);
		
		//Determine IDF for each word in lstPlaceWebData
		for (i=0; i<lstPlaceWebData.size(); i++) {
			List<Double> lstIDF = null;
			
			AL_PlaceWebData placeWebData = lstPlaceWebData.get(i);
			List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
			
			lstIDF = new ArrayList<Double>();
			
			for (j=0; j<lstKeywordWeight.size(); j++) {
				sWord = lstKeywordWeight.get(j).getKeyword();
				nOccurrenceCnt = Utility.CalculateWordOccurrenceCount(sWord, lstPlaceWebData, -1);
				if (nOccurrenceCnt > 0) {
					fIDF = 1 + Math.log(lstPlaceWebData.size()/nOccurrenceCnt);
				} else {
					fIDF = 0;
				}
				
				lstIDF.add(fIDF);
			}
			
			lstlstPlaceWebDataIDF.add(lstIDF);
			
			lstCandidatePlaceNames.add(lstPlaceWebData.get(i).getPlaceName().toLowerCase().trim());
		}
		
		
		//Get folder paths of crowdsourced data
		Utility.getSubFolderList(sCrowdsourcedDataFolder, lstPlaceNameList, lstSubFolderFullPathList);
		
		//From all the candidate stores, Randomly select places
		List<Integer> lstPlaceIndex = Utility.getRandom(lstPlaceNameList.size(), nTotalStoreCnt);

		System.out.println("Total Places: " + lstPlaceNameList.size());
		System.out.println("Place Index: ");
		Utility.PrintList(lstPlaceIndex);
		
		List<Integer> lstSelectedWebIndex = new ArrayList<Integer>();
		List<Integer> lstFrameMacListIndex = new ArrayList<Integer>();
		
		//Get ground truth for the crowdsourced stores
		for (i=0; i<nTotalStoreCnt; i++) {			
			lstSelectedFullPath.add(lstSubFolderFullPathList.get(lstPlaceIndex.get(i)-1));
			
			String sGroundTruthPlaceNameTmp = Utility.getGroundTruthPlaceNameFromFolder(lstSubFolderFullPathList.get(lstPlaceIndex.get(i)-1)).toLowerCase().trim();
			lstGroundTruthPlaceName.add(sGroundTruthPlaceNameTmp);
			
			//Check whether there is corresponding web data existing
			int nWebIndexTmp = -1;
			nWebIndexTmp = lstCandidatePlaceNames.indexOf(sGroundTruthPlaceNameTmp);
			
			if (nWebIndexTmp == -1) {
				Utility.DEBUG_INFO("Ground Truth Place Name does not match with any Store Name!");
				return;
			} else {
				lstSelectedWebIndex.add(nWebIndexTmp);
			}
			
			int nMacListIndexTmp = -1;
			nMacListIndexTmp = lstWiFiPlaceNameList.indexOf(sGroundTruthPlaceNameTmp);

			if (nMacListIndexTmp == -1) {
				Utility.DEBUG_INFO("Ground Truth Place Name does not match with any Store Name with WiFi Data!");
				return;
			} else {
				lstFrameMacListIndex.add(nMacListIndexTmp);
			}
			
		}
		
		//Utility.DEBUG_INFO("1");
		
		List<List<String>> lstlstSubsetWordsBase = new ArrayList<List<String>>();
		List<List<Double>> lstlstfTFWeightBase = new ArrayList<List<Double>>();
		List<List<String>> lstlstSubsetWordsRawBase = new ArrayList<List<String>>();
	
		List<Integer> lstFrameCnt = new ArrayList<Integer>();
		
		List<List<String>> lstlstTotalSelectedFramesCalB = new ArrayList<List<String>>();
		List<List<String>> lstlstTotalSelectedFramesRawCalB = new ArrayList<List<String>>();
		List<List<String>> lstlstTotalSelectedFramesGroundTruthCalB = new ArrayList<List<String>>();
		
		List<List<String>> lstlstTotalSelectedFramesPredicatedPlaceNameCalB = new ArrayList<List<String>>();
		
		List<Integer> lstTotalFrameMacListIndex = new ArrayList<Integer>();

		//From the selected stores (crowdsourced data), Randomly select up to #nMaxPicNumPerStore pics
		for (i=0; i<nTotalStoreCnt; i++) {
			Utility.DEBUG_INFO("Selecting Frame from Place : " + (i+1));
			List<String> lstFrames = new ArrayList<String>();
			List<String> lstFramesRaw = new ArrayList<String>();
			sSubsetContentBase = "";
			sSubsetContentBaseRaw = "";
			
			List<String> lstOnePlaceSelectedFramesCalB = new ArrayList<String>();
			List<String> lstOnePlaceSelectedFramesRawCalB = new ArrayList<String>();
			List<String> lstOnePlaceSelectedFramesGroundTruthCalB = new ArrayList<String>();
			List<String> lstOnePlaceSelectedFramesPredicatedPlaceNameCalB = new ArrayList<String>();
			
			//Randomly determine how many pictures should be selected from each store
			nSubsetSize = Utility.getRandom(nMaxPicNumPerStore);
			
			//lstFramesRaw:  Selected pic text (each item corresponds to one pic), text is not processed
			//lstFrames:  Selected pic text (each item corresponds to one pic), text is noun-extracted.
			lstFrames = getFrames(lstSelectedFullPath.get(i), nSubsetSize, lstFramesRaw);  //Select frames from one place
			
			//Utility.DEBUG_INFO("-----meng 1");
			//Set frames and their corresponding ground truth
			for (j=0; j<lstFrames.size(); j++) {
				lstTotalSelectedFrames.add(lstFrames.get(j));          //Noun/Proper name extracted
				lstTotalSelectedFramesRaw.add(lstFramesRaw.get(j));    //Raw frame
				lstTotalSelectedFramesGroundTruth.add(lstGroundTruthPlaceName.get(i));   //Ground truth for each pic (frame)
				lstTotalSelectedFramesPredicatedPlaceName.add("");    //Predicated Place Name for each pic (frame), will be predicated later
				
				lstOnePlaceSelectedFramesCalB.add(lstFrames.get(j));          //Noun/Proper name extracted
				lstOnePlaceSelectedFramesRawCalB.add(lstFramesRaw.get(j));    //Raw frame
				lstOnePlaceSelectedFramesGroundTruthCalB.add(lstGroundTruthPlaceName.get(i));   //Ground truth
				lstOnePlaceSelectedFramesPredicatedPlaceNameCalB.add("");    //Predicated Place Name for each pic (frame), will be predicated later
				
				lstTotalFrameMacListIndex.add(lstFrameMacListIndex.get(i));  //Record Store WiFi information for each frame
				
				sSubsetContentBase = sSubsetContentBase + lstFrames.get(j) + ",";
				sSubsetContentBaseRaw = sSubsetContentBaseRaw + lstFramesRaw.get(j) + ",";
			}
			//Utility.DEBUG_INFO("-----meng 2");
			
			lstlstTotalSelectedFramesCalB.add(lstOnePlaceSelectedFramesCalB);     //Place by Place
			lstlstTotalSelectedFramesRawCalB.add(lstOnePlaceSelectedFramesRawCalB);
			lstlstTotalSelectedFramesGroundTruthCalB.add(lstOnePlaceSelectedFramesGroundTruthCalB);
			lstlstTotalSelectedFramesPredicatedPlaceNameCalB.add(lstOnePlaceSelectedFramesPredicatedPlaceNameCalB);
			
			lstFrameCnt.add(lstFrames.size());  //Remember:  how many pics are selected from each store
			
			List<String> lstSubsetWordsBase = new ArrayList<String>();
			List<Double> lstfTFWeightBase = new ArrayList<Double>();
			
			//lstSubsetWordsBase:  (non-duplicated) words from the selected pics of one store;
			//lstfTFWeightBase: Corresponding TF
			CalculateTF(sSubsetContentBase, lstSubsetWordsBase, lstfTFWeightBase);  //Calculate TF for each subset (TF of the word in pics from one store)
			
			//Utility.DEBUG_INFO("-----meng 3");
			
			List<String> lstSubsetWordsRawBase = Utility.splitStringList(sSubsetContentBaseRaw);
			
			//Utility.DEBUG_INFO("-----meng 4");
			lstlstSubsetWordsBase.add(lstSubsetWordsBase);   //One partition
			lstlstfTFWeightBase.add(lstfTFWeightBase);
			
			lstlstSubsetWordsRawBase.add(lstSubsetWordsRawBase);
			
			//Utility.DEBUG_INFO("-----meng 5");
			
			if (i != nTotalStoreCnt-1) {
				//System.out.println("Here....................feng 001");
				sStoreFrameStat = sStoreFrameStat + lstGroundTruthPlaceName.get(i)  + "," + lstPlaceIndex.get(i) + "," + lstFrames.size() + "; ";
				
			} else {
				//System.out.println("Here....................feng 002");
				sStoreFrameStat = sStoreFrameStat + lstGroundTruthPlaceName.get(i)  + "," + lstPlaceIndex.get(i) + "," + lstFrames.size();
			}
			
			//Utility.DEBUG_INFO("Place: [" + (i+1) + "] Frames are selected!");
			//System.out.println("Here....................feng 0");
		}

	    ///////////////////////////////////////////////////////
		//SO FAR, CROWDSOURCED STORE/PICS ARE SELECTED
		///////////////////////////////////////////////////////
		
		
/*		
		//Calculate ground truth matching score
		List<Double> lstBaseMatchingScore = new ArrayList<Double>();
		List<String> lstPredicatedPlaceNamesBase = new ArrayList<String>();  //Each subset has one predicated place name

		lstBaseMatchingScore = GroupMatchingS(lstPlaceWebData, lstSelectedWebIndex, lstlstSubsetWordsBase, lstlstfTFWeightBase, lstlstSubsetWordsRawBase, lstPredicatedPlaceNamesBase);
		double fBaseMatchingScore = Utility.getTotal(lstBaseMatchingScore);
		
		String sStoreFrameStatLine = fBaseMatchingScore + ";" + sStoreFrameStat + "\n";
		
		//Write first line of the file:
		//It contains:  Base matching score; Place name1, place index1, number of selected frames; Place name2, place index2, number of selected frames;....
		try {
			fwClusteringFull.write(sStoreFrameStatLine);  
		} catch (Exception e) {
			
		}
*/
		
		//System.out.println("Here....................feng 1");
		//Utility.DEBUG_INFO("Now calculate score for raw partition.....");
		////////////////////////////////////////////////
		////Purely for raw (ground truth) partition
		List<List<Boolean>> lstlstTotalSelectedFramesMatchedWebIndexCalB = new ArrayList<List<Boolean>>();

		//Check how the Raw Frames match with the place names, 
		//result is in lstlstTotalSelectedFramesMatchedWebIndexCalB (The result is a matrix in the form of list)
		CalCandidatePlaceForFramesCalB(lstPlaceWebData, lstSelectedWebIndex, lstlstTotalSelectedFramesRawCalB, lstlstTotalSelectedFramesMatchedWebIndexCalB);

		//System.out.println("Here....................feng 2");
		List<List<String>> lstlstSubsetWordsBaseCalB = new ArrayList<List<String>>();
		List<List<Double>> lstlstfTFWeightBaseCalB = new ArrayList<List<Double>>();
		List<List<String>> lstlstSubsetWordsRawBaseCalB = new ArrayList<List<String>>();
		List<Integer> lstSelectedWebIndexCalB = new ArrayList<Integer>();

		int nOneMatchedFrameCalB = 0;  //How many pics are matched with place names ???
		
		//lstlstTotalSelectedFramesCalB has been noun/proper name extracted
		for (i=0; i<lstlstTotalSelectedFramesCalB.size(); i++) {
			sSubsetContentBaseCalB = "";  //For one store
			sSubsetContentBaseRawCalB = "";

			List<String> lstTotalSelectedFramesCalB = lstlstTotalSelectedFramesCalB.get(i);   //List of Frames of One store
			
			for (j=0; j<lstTotalSelectedFramesCalB.size(); j++) {
				if (lstlstTotalSelectedFramesMatchedWebIndexCalB.get(i).get(j) == false) {  //Check one frame, Has not matched with place name
					sSubsetContentBaseCalB = sSubsetContentBaseCalB + lstlstTotalSelectedFramesCalB.get(i).get(j) + ",";
					sSubsetContentBaseRawCalB = sSubsetContentBaseRawCalB + lstlstTotalSelectedFramesRawCalB.get(i).get(j) + ",";					
				} else {
					nOneMatchedFrameCalB = nOneMatchedFrameCalB + 1;
				}
			}

			if (sSubsetContentBaseCalB.length() == 0) continue;
			
			lstSelectedWebIndexCalB.add(lstSelectedWebIndex.get(i));  //Corresponding web
			
			List<String> lstSubsetWordsBaseCalB = new ArrayList<String>();
			List<Double> lstfTFWeightBaseCalB = new ArrayList<Double>();
			
			//System.out.println("Here....................feng 3 - 0");
			CalculateTF(sSubsetContentBaseCalB, lstSubsetWordsBaseCalB, lstfTFWeightBaseCalB);  //Calculate TF for each subset (here subset = one store)
			//System.out.println("Here....................feng 3 - 1");
			List<String> lstSubsetWordsRawBaseCalB = Utility.splitStringList(sSubsetContentBaseRawCalB);
			
			lstlstSubsetWordsBaseCalB.add(lstSubsetWordsBaseCalB);   //One partition
			lstlstfTFWeightBaseCalB.add(lstfTFWeightBaseCalB);
			
			lstlstSubsetWordsRawBaseCalB.add(lstSubsetWordsRawBaseCalB);
		}

		
		
		List<Double> lstBaseCalBMatchingScore = new ArrayList<Double>();
		List<String> lstPredicatedPlaceNamesBaseCalB = new ArrayList<String>();  //Each subset has one predicated place name
		
		//Utility.DEBUG_INFO("000000000000000000000");
		
		//lstPredicatedPlaceNamesBaseCalB will be the predicated place name for the lstlstSubsetWordsBaseCalB
		//Here calculate: if the OCRed text matches with the corresponding (i.e. correct) web text, what the matching score will be
		lstBaseCalBMatchingScore = GroupMatchingS(lstPlaceWebData, lstSelectedWebIndexCalB, lstlstSubsetWordsBaseCalB, lstlstfTFWeightBaseCalB, lstlstSubsetWordsRawBaseCalB, lstPredicatedPlaceNamesBaseCalB);
		double fBaseCalBMatchingScore = Utility.getTotal(lstBaseCalBMatchingScore);   //Total base matching score (if all OCRed text is matched with correct Web text)

		String sStoreFrameStatLineCalB = nOneMatchedFrameCalB + "," + fBaseCalBMatchingScore + ";" + sStoreFrameStat + "\n";
		
		//Write first line of the file:
		//It contains:  Number of frames matched with Place Name, BaseCalB matching score; Place name1, place index1, number of selected frames; Place name2, place index2, number of selected frames;....
		try {
			fwClusteringFull.write(sStoreFrameStatLineCalB);  
		} catch (Exception e) {
			
		}
		//////////////////////////////////////
		
		
		Utility.DEBUG_INFO("2");
		
		int nRawFrameCnt = lstTotalSelectedFramesRaw.size();
		
		int nNullMatched = 0;
		int nOneMatched = 0;
		int nMultiMatched = 0;
		
		///////////////////////////////////////////////////
		//So far, frames are randomly selected
		///////////////////////////////////////////////////
		//Next, compare frame with web data to determine how many webs are matched for each frame
		///////////////////////////////////////////////////
		CalCandidatePlaceForFrames(lstPlaceWebData, lstTotalSelectedFramesRaw, lstlstTotalSelectedFramesMatchedWebIndex);
		
		//lstlstTotalSelectedFramesMatchedWebIndex is a matrix; each line is for a pic, each field in each line is index of matched web for this frame
		
		int nHereCnt = lstlstTotalSelectedFramesMatchedWebIndex.size();

		String sNullFrame = "";    //The index list for the null frames
		String sOneMatchedFrame = "";  //The index list for the frame matched only one web 
		
//		lstBaseMatchingScore = GroupMatchingS(lstPlaceWebData, lstSelectedWebIndex, lstlstSubsetWordsBase, lstlstfTFWeightBase, lstlstSubsetWordsRawBase, lstPredicatedPlaceNamesBase);

		//Filter frames
		for (i=0; i<lstlstTotalSelectedFramesMatchedWebIndex.size(); i++) {  //"i" is the index of total pics
			List<Integer> lstMatchedWebIndex = lstlstTotalSelectedFramesMatchedWebIndex.get(i);
			
			//Original  if (lstMatchedWebIndex == null) {   //This frame has no matched web
			if (lstMatchedWebIndex == null || lstMatchedWebIndex.size() == 0) {   //This frame has no matched web  (Changed 20150804)

				lstTotalSelectedFramesPredicatedPlaceName.set(i, "null");
				if (sNullFrame.length() == 0) {
					sNullFrame =  i + "";
				} else {
					sNullFrame = sNullFrame + "," + i;
				}
				nNullMatched = nNullMatched + 1;
			} else if (lstMatchedWebIndex.size() == 1) {  //Only one is matched, this is the result
				int nMatchedWebIdx = lstMatchedWebIndex.get(0);
				lstTotalSelectedFramesPredicatedPlaceName.set(i, lstPlaceWebData.get(nMatchedWebIdx).getPlaceName());  //Set predicated place name for this pic

				if (sOneMatchedFrame.length() == 0) {
					sOneMatchedFrame =  i + "";
				} else {
					sOneMatchedFrame = sOneMatchedFrame + "," + i;
				}
				
				nOneMatched = nOneMatched + 1;
			} else {  //This frame has multiple matched webs
				lstOriginalIndexForMultiMatchFrame.add(i);  //Remember the original frame index in lstlstTotalSelectedFramesMatchedWebIndex for the frames in lstlstTotalSelectedFramesMatchedWebIndexMultiMatch
			
				lstlstTotalSelectedFramesMatchedWebIndexMultiMatch.add(lstlstTotalSelectedFramesMatchedWebIndex.get(i));  //Each item (corresponds to one frame) is the indexes of the matched webs for this frame
				
				lstTotalSelectedFramesMultiMatch.add(lstTotalSelectedFrames.get(i));
				lstTotalSelectedFramesRawMultiMatch.add(lstTotalSelectedFramesRaw.get(i));
				
				nMultiMatched = nMultiMatched + 1;
			}
		}
		
		
		if (lstlstTotalSelectedFramesMatchedWebIndexMultiMatch.size() == 0) {  //No multimatched frames
			
			Utility.DEBUG_INFO("No Multimatched Frames!");
			
			for (i=0; i<lstlstTotalSelectedFramesMatchedWebIndex.size(); i++) {
				if ( i!= lstlstTotalSelectedFramesMatchedWebIndex.size()-1) {
					sMaxOnePartitionGroundTruthPlaceNames = sMaxOnePartitionGroundTruthPlaceNames + lstTotalSelectedFramesGroundTruth.get(i) + ",";
					sMaxOnePartitionPredicatedPlaceNames = sMaxOnePartitionPredicatedPlaceNames + lstTotalSelectedFramesPredicatedPlaceName.get(i) + ",";
				} else {
					sMaxOnePartitionGroundTruthPlaceNames = sMaxOnePartitionGroundTruthPlaceNames + lstTotalSelectedFramesGroundTruth.get(i);
					sMaxOnePartitionPredicatedPlaceNames = sMaxOnePartitionPredicatedPlaceNames + lstTotalSelectedFramesPredicatedPlaceName.get(i);					
				}
			}
			////////////////////////
			
			List<Integer> lstFrameStat = new ArrayList<Integer>(); 
			//Write Simple Result
			//Line:  Similarity Score;  #Partition Index, Ground Truth (i.e. base) Number of frames matched place name, Ground truth score; Number of one matched frame, Max Partition score sum; Ground Truth Place Names; Predicated Place Name; Partition
			double fSimilarityScore = Utility.calFieldsSimilarityS(sMaxOnePartitionGroundTruthPlaceNames, sMaxOnePartitionPredicatedPlaceNames, lstFrameStat);  //How (in order) similar are the Ground truth and Predicated results are for the frames
			sLineSimple = fSimilarityScore + "; " + 0 + "," + 0 + "," + 0 + "; " + nOneMatchedFrameCalB + "," + fBaseCalBMatchingScore  + "; " + nOneMatched + "," + 0 + "; " + sMaxOnePartitionGroundTruthPlaceNames + "; " + sMaxOnePartitionPredicatedPlaceNames + "; " + sMaxOnePartitionFrameIndex;
						
			sLineSimple = sLineSimple + "\n";
			
			String sStat = lstFrameStat.get(0) + "," + lstFrameStat.get(1) + "\n";  //Number of Correct match, Total Valid Number
			
			try {
				fwClusteringSimple.write(sLineSimple);
				fwClusteringStat.write(sStat);
			} catch (Exception e) {
				
			}
			
			return;
		} 
		
		
		//Here is the process for pics which have multi-matched webs 
		Utility.DEBUG_INFO("Multi Web Matched #Frames: " + lstlstTotalSelectedFramesMatchedWebIndexMultiMatch.size());
		
		Utility.PrintCurrentTime();
		Utility.DEBUG_INFO("Partition...........");
		//Create List<List<List<Integer>>> from lstlstTotalSelectedFramesMatchedWebIndex for partition
		List<List<Integer>> lstlstWebIndexMultiMatch = new ArrayList<List<Integer>>();  
		List<List<List<Integer>>> lstlstlstPossibleCombine = new ArrayList<List<List<Integer>>>();

		List<List<Integer>> lstlstWebIndexMultiMatchTemp = new ArrayList<List<Integer>>();  
		List<List<List<Integer>>> lstlstlstPossibleCombineTemp = new ArrayList<List<List<Integer>>>();
		
//		lstlstlstPossibleCombine = Utility.formPossibleCombination(lstlstTotalSelectedFramesMatchedWebIndex);

		//lstlstWebIndexMultiMatch corresponds to the each unit combination of pictures in lstlstlstPossibleCombine
		/*
		 * Here below, picture = p; web = w
		 * For example:
		 * p1 matches: w1, w4
		 * p2 matches: w2, w5, w6
		 * p3 matches: w1, w2, w4, w6
		 * p4 matches: w5
		 * p5 matches: w1
		 * p6 matches: w6
		 * p7 matches:
		 * p8 matches: w4
		 * ......
		 * (The above information is recorded in lstlstTotalSelectedFramesMatchedWebIndexMultiMatch)
		 * Now, we need to find out the possible combination of pictures based on their common matched web, 
		 * e.g. p1 and p3 could be one combination
		 * Of course, single one picture could also be one combination
		 * 
		 * Here below, in lstlstlstPossibleCombine, each row (or say item--lstlst) corresponds to the possible combination for each picture
		 * (e.g. p1 could have following possible combination based on common matched web:  (p1), (p1 p3), (p1 p5), (p1 p8), (p1 p3 p8)
		 * In lstlstWebIndexMultiMatch contains the matched web index corresponding to the combinations in lstlstlstPossibleCombine
		 * 
		 * From the combinations, we need to find overall partition (one partition contains each pic only once)
		 */
		//lstlstlstPossibleCombine:  p1, p1 p3, p1 p5, p1 p8, p1 p3 p8;  p2, p2 p3, p2 p4, p2 p3 p6;
		//lstlstlstPossibleCombine:  (p1), (p1 p3), (p1 p5), (p1 p8), (p1 p3 p8);  (p2), (p2 p3), (p2 p4), (p2 p3 p6);   (20150804 changed)
		//lstlstWebIndexMultiMatch:   w1,   w1,      w1,      w4,      w4;          w2,   w2,      w5,      w6
		lstlstlstPossibleCombineTemp = Utility.formPossibleCombination(lstlstTotalSelectedFramesMatchedWebIndexMultiMatch, lstlstWebIndexMultiMatchTemp);   
//		lstlstlstPossibleCombine = Utility.formPossibleCombination_UniqueCombine(lstlstTotalSelectedFramesMatchedWebIndexMultiMatch, lstlstlstWebIndexMultiMatch);   
		
		//////////////////////////////////////////////////////////////////////
		////Filter Combination based on the WiFi Data////
		////i.e. check whether e.g. (p1 p3) is a valid combination based on whether they have common WiFi mac
		//////////////////////////////////////////////////////////////////////
		int nCombineCntBefore = 0;
		int nCombineCntAfter = 0;
		
		Utility.DEBUG_INFO("Filter combination based on WiFi...........");
		
		for (int t=0; t<lstlstlstPossibleCombineTemp.size(); t++) {
			List<List<Integer>> lstlstPossibleCombineTemp = lstlstlstPossibleCombineTemp.get(t);    //Combination corresponding to one pic; e.g.  (p1), (p1 p3), (p1 p5), (p1 p8), (p1 p3 p8);  
			List<Integer> lstWebIndexMultiMatchTemp = lstlstWebIndexMultiMatchTemp.get(t);
			
			List<List<Integer>> lstlstValidCombine = new ArrayList<List<Integer>>();
			List<Integer> lstValidWebIndex = new ArrayList<Integer>();
			
			for (int s=0; s<lstlstPossibleCombineTemp.size(); s++) {  //Process combinations of related to each pic
				int nWebIndexTemp = lstWebIndexMultiMatchTemp.get(s);
				List<Integer> lstCombine = lstlstPossibleCombineTemp.get(s);  //e.g. (p1 p3)
				
				nCombineCntBefore = nCombineCntBefore + 1;
				
				if (lstCombine.size() == 1) {  //If only one frame in this combination, it is anyway valid
					lstlstValidCombine.add(lstCombine);
					lstValidWebIndex.add(nWebIndexTemp);
					
					continue;   
				}
				
				List<Integer> lstMacListIndexForCombine = new ArrayList<Integer>();
				for (int p=0; p<lstCombine.size(); p++) {
					int nOriginalFrameIndexTmp = lstOriginalIndexForMultiMatchFrame.get(lstCombine.get(p));
					int nMacListIndexTemp = lstTotalFrameMacListIndex.get(nOriginalFrameIndexTmp);
					lstMacListIndexForCombine.add(nMacListIndexTemp);
				}
				
				boolean bPossibleSameStore = checkSameStorePossibility(lstlstPlaceMac, lstMacListIndexForCombine);
				
				if (bPossibleSameStore == true) {
					lstlstValidCombine.add(lstCombine);
					lstValidWebIndex.add(nWebIndexTemp);
					
					nCombineCntAfter = nCombineCntAfter + 1;
				}
			}
			
			if (lstlstValidCombine.size() > 0) {
				lstlstlstPossibleCombine.add(lstlstValidCombine);
				lstlstWebIndexMultiMatch.add(lstValidWebIndex);
			}
			
		}
		
		System.out.println("========================Number of Combine Filter:  [Before] " + nCombineCntBefore + " ==== [After] " + nCombineCntAfter);
		///////////////////////////////////////////////////////////////////////
		
		List<List<List<Integer>>> lstlstlstValidPartition = new ArrayList<List<List<Integer>>>();
		List<List<Integer>> lstlstSelectedCombineIndex = new ArrayList<List<Integer>>();  //Record in lstlstlstValidPartition, each partition, for each lstlstPossibleCombine, which lstPossibleCombine is selected (index)
		
		//Now get valid partitions based on the combinations of pics
		//In each partition, each pic occurs once
		//lstlstlstValidPartition contains valid partitions, e.g. [1 3] [2] [4 7] [5 8] [6];  [1 5] [2 4] [3] [6 8] [7]
		//lstlstSelectedCombineIndex contains list of indexes, which tells which combination in lstlstlstPossibleCombine is selected to form lstlstlstValidPartition
		Utility.getValidPartitionS(lstlstlstPossibleCombine, lstlstlstValidPartition, lstlstSelectedCombineIndex);   //Get partitions of frames which has 2+ matched webs
	
/*
		////////////////////////////// Simply Print
		for (i=0; i<lstlstlstValidPartition.size(); i++) {
			List<List<Integer>> lstlstOnePartition = lstlstlstValidPartition.get(i);
			List<Integer> lstSelectedCombineIndex = lstlstSelectedCombineIndex.get(i);
			
			String sLineLine = "";
			
			for (j=0; j<lstlstOnePartition.size(); j++) {
				List<Integer> lstCombine = lstlstOnePartition.get(j);
				
				String sTmpLine = " [";
				for (k=0; k<lstCombine.size(); k++) {
					if (k < lstCombine.size()-1) {
						sTmpLine = sTmpLine + lstCombine.get(k) + ",";
					} else {
						//sTmpLine = sTmpLine + lstCombine.get(k) + " # ";
						sTmpLine = sTmpLine + lstCombine.get(k);
					}
				}
				
				//int nWebIndex = Utility.getWebIndex(lstlstlstPossibleCombine, lstlstWebIndexMultiMatch, lstCombine);  //??
				//int nWebIndex = Utility.getWebIndexS(lstlstlstPossibleCombine, lstlstWebIndex, lstCombine);
				//sTmpLine = sTmpLine + nWebIndex + "]";
				//if (j != lstlstOnePartition.size()-1) {
				//	sWebIndex = sWebIndex + nWebIndex + ",";
				//} else {
				//	sWebIndex = sWebIndex + nWebIndex + "}";
				//}
				
				sTmpLine = sTmpLine + "]";
				
				sLineLine = sLineLine + sTmpLine;
			}
			
			String sWebIndex = " {";
			for (k=0; k<lstSelectedCombineIndex.size(); k++) {
				int nSelectedIdx = lstSelectedCombineIndex.get(k);
				if (nSelectedIdx >= 0) {
					if (k != lstSelectedCombineIndex.size() -1) {
						sWebIndex = sWebIndex + lstlstWebIndexMultiMatch.get(k).get(nSelectedIdx) + ",";
					} else {
						sWebIndex = sWebIndex + lstlstWebIndexMultiMatch.get(k).get(nSelectedIdx);
					}
				}
			}
			sWebIndex = sWebIndex + "}";
			
			sLineLine = sLineLine + "  " + sWebIndex;
			System.out.println(sLineLine);
		}
*/
		//////////////////////////////
		
		//SO FAR, we have all possible valid partition of the pics in lstlstlstValidPartition
		//e.g. [1 3] [2] [4 7] [5 8] [6];  [1 5] [2 4] [3] [6 8] [7]
		//But we don't know which partition is "correct"; i.e. which matching between all the combinations and the webs is correct
		//We do matching and select the one which achieves highest matching score as the correct partition.
		//
		
		int nPartitionCnt = lstlstlstValidPartition.size();
		
		Utility.DEBUG_INFO("#Total Frame: " +  lstTotalSelectedFramesRaw.size() +  " ==Null Frame: " + nNullMatched + " ==One Matched Frame: " + nOneMatched + " &&& [MultiMatched = " + nMultiMatched +" ]   ==Multi Matched #Frames: " + lstlstTotalSelectedFramesMatchedWebIndexMultiMatch.size() +  " ====#Partition: " + nPartitionCnt);

//		Utility.DEBUG_INFO("#Frame: " +  lstTotalSelectedFramesRaw.size() +  "===Multi Web Matched #Frames: " + lstlstTotalSelectedFramesMatchedWebIndexMultiMatch.size() +  "===============#Partition: " + lstlstlstValidPartition.size());
				
		//Here below, do matching to decide which store the subsets (i.e. combination) belongs to
		//Method A:   
		//            For each partition, compare with all candidate webs, and calculate the sum(max(subset matching score)),  the partition which gets the highest sum score is considered as the correct partition.
		//            This methods have less partitions
		//Method B (now used):   
		//            For each partition, each subset (e.g. pic [1 3]) only compare with its already matched webs, sum of the matching scores, the partition which gets the highest sum score is considered as the correct partition. 
		//            This methods have more partitions, because if one subset matches with more than one web, it will generate different combination of partitions		
		Utility.DEBUG_INFO("3");
		
		List<Double> lstPartitionMatchingScoreSum = new ArrayList<Double>();
		
		for (i=0; i<nPartitionCnt; i++) {
			Utility.DEBUG_INFO("[Loop Index = " + nLoopIndex +"]" + "  [Partition Index]" + "-------" + i);
			
			String sPartitionFrameIndex = "";  //Record the partition (of frame index)
			
			List<String> lstTotalSelectedFramesPredicatedPlaceNameOnePartition = new ArrayList<String>();

			Utility.copyStringList(lstTotalSelectedFramesPredicatedPlaceName, lstTotalSelectedFramesPredicatedPlaceNameOnePartition);
			
			List<List<Integer>> lstlstOnePartitionIndexSet = lstlstlstValidPartition.get(i);  //Frame Index of one partition
			List<Integer> lstSelectedCombineIndex = lstlstSelectedCombineIndex.get(i);  //Selected combine list of each the frame in one partition
			List<List<String>> lstlstSubsetWords = new ArrayList<List<String>>();
			List<List<Double>> lstlstfTFWeight = new ArrayList<List<Double>>();
			List<List<String>> lstlstSubsetWordsRaw = new ArrayList<List<String>>();
			
			List<Integer> lstWebIndex = new ArrayList<Integer>();  //Web index for one partition, e.g. (w1 w3 w4 w6 w8)
			
			for (k=0; k<lstSelectedCombineIndex.size(); k++) {
				int nSelectedCombineIdx = lstSelectedCombineIndex.get(k);
				if (nSelectedCombineIdx >= 0) {
					//lstWebIndex.add(nSelectedCombineIdx);
					lstWebIndex.add(lstlstWebIndexMultiMatch.get(k).get(nSelectedCombineIdx));
				}
			}

//			String sOnePartitionGroundTruthPlaceNames = "";
//			String sOnePartitionPredicatedPlaceNames = "";
			List<List<String>> lstlstOnePartitionGroudTruthPlaceNames = new ArrayList<List<String>>();  //Ground Truth for frames
			
			List<Double> lstPartitionSubsetMaxMatchingScore = new ArrayList<Double>();  //List of max matching scores for the subsets of one partition
			List<Integer> lstOnePartitionFrameOriginalIndex = new ArrayList<Integer>();
			
			for (j=0; j<lstlstOnePartitionIndexSet.size(); j++) {  //Frame index of one partition, e.g. [1 3] [2] [4 7] [5 8] [6]
				
				//Utility.DEBUG_INFO("Group-" + i + "==== Subset-" + j);
				
				sSubsetContent = "";   //For one partition
				List<String> lstSubsetGroundTruth = new ArrayList<String>();
				
				sSubsetContentRaw = "";
				
				List<Integer> lstSubsetIndex = lstlstOnePartitionIndexSet.get(j);  //Index of frames in a subset (the index should be changed to original frame index based on lstOriginalIndexForMultiMatchFrame, e.g. [1 3]
				
				sPartitionFrameIndex = sPartitionFrameIndex + " [";
				
				for (k=0; k<lstSubsetIndex.size(); k++) { 
					int nNewFrameIndex = lstSubsetIndex.get(k);
					int nOriginalFrameIndex = lstOriginalIndexForMultiMatchFrame.get(nNewFrameIndex);
					
					if (k == lstSubsetIndex.size()-1) {
						sPartitionFrameIndex = sPartitionFrameIndex + nOriginalFrameIndex;
					} else {
						sPartitionFrameIndex = sPartitionFrameIndex + nOriginalFrameIndex + ",";
					}
					
					lstOnePartitionFrameOriginalIndex.add(nOriginalFrameIndex);
					
					sSubsetContent = sSubsetContent + lstTotalSelectedFrames.get(nOriginalFrameIndex) + ",";
					
					sGroundTruthPlaceName = lstTotalSelectedFramesGroundTruth.get(nOriginalFrameIndex);
					lstSubsetGroundTruth.add(sGroundTruthPlaceName);
					
//					if ((j == lstlstOnePartitionIndexSet.size()-1) && (k == lstSubsetIndex.size()-1)) {
//						sOnePartitionGroundTruthPlaceNames = sOnePartitionGroundTruthPlaceNames + sGroundTruthPlaceName;					
//					} else {
//						sOnePartitionGroundTruthPlaceNames = sOnePartitionGroundTruthPlaceNames + sGroundTruthPlaceName + ",";
//					}
					
					sSubsetContentRaw = sSubsetContentRaw + lstTotalSelectedFramesRaw.get(nOriginalFrameIndex) + ",";
				}
				
				sPartitionFrameIndex = sPartitionFrameIndex + "] "; 
				
				List<String> lstSubsetWords = new ArrayList<String>();
				List<Double> lstfTFWeight = new ArrayList<Double>();
				
				CalculateTF(sSubsetContent, lstSubsetWords, lstfTFWeight);  //Calculate TF for each subset
				
				List<String> lstSubsetWordsRaw = Utility.splitStringList(sSubsetContentRaw);
				
				lstlstSubsetWords.add(lstSubsetWords);   //One partition
				lstlstfTFWeight.add(lstfTFWeight);
				
				lstlstSubsetWordsRaw.add(lstSubsetWordsRaw);
				lstlstOnePartitionGroudTruthPlaceNames.add(lstSubsetGroundTruth);   //Every frame has its Ground Truth Place Name
			}
			
			//Utility.DEBUG_INFO("4 - 2" + "-------" + i);
			List<String> lstPredicatedPlaceNames = new ArrayList<String>();  //Each subset has one predicated place name

//			lstPartitionSubsetMaxMatchingScore = GroupMatching(lstPlaceWebData, lstlstPlaceWebDataIDF, lstlstSubsetWords, lstlstfTFWeight, lstlstSubsetWordsRaw, lstPredicatedPlaceNames);
			lstPartitionSubsetMaxMatchingScore = GroupMatchingS(lstPlaceWebData, lstWebIndex, lstlstSubsetWords, lstlstfTFWeight, lstlstSubsetWordsRaw, lstPredicatedPlaceNames);
			
			fPartitionScoreSum = Utility.getTotal(lstPartitionSubsetMaxMatchingScore);
			lstPartitionMatchingScoreSum.add(fPartitionScoreSum);
			
			//Write Full Result, in each line:  Partition Score, List of Ground Truth place name for frames, List of Predicated place name for frames
			//sLineFull = fPartitionScoreSum + "," + sOnePartitionGroundTruthPlaceNames;
			
			List<String> lstOnePartitionPredicatedPlaceNames = new ArrayList<String>();
			
			for (j=0; j<lstPredicatedPlaceNames.size(); j++) {
				for (k=0; k<lstlstOnePartitionIndexSet.get(j).size(); k++) {
//					if ((j == lstPredicatedPlaceNames.size()-1) && (k == lstlstOnePartitionIndexSet.get(j).size()-1)) {
//						sOnePartitionPredicatedPlaceNames = sOnePartitionPredicatedPlaceNames + lstPredicatedPlaceNames.get(j);						
//					} else {
//						sOnePartitionPredicatedPlaceNames = sOnePartitionPredicatedPlaceNames + lstPredicatedPlaceNames.get(j) + ",";
//					}
					
					lstOnePartitionPredicatedPlaceNames.add(lstPredicatedPlaceNames.get(j));  //In the very original order
				}
			}
			
			for (j=0; j<lstOnePartitionFrameOriginalIndex.size(); j++) {
				int nOriginalFrameIndex = lstOnePartitionFrameOriginalIndex.get(j);
				
				lstTotalSelectedFramesPredicatedPlaceNameOnePartition.set(nOriginalFrameIndex, lstOnePartitionPredicatedPlaceNames.get(j));
			}
			
			String sOnePartitionGroundTruthPlaceNames = "";
			String sOnePartitionPredicatedPlaceNames = "";
			
			if (lstTotalSelectedFramesPredicatedPlaceNameOnePartition.size() != lstTotalSelectedFramesGroundTruth.size()) {
				Utility.DEBUG_INFO(" Number of Ground Truth Place Name does not equal to Number of Predicated Place Name");
				return;
			} 
			
			for (j=0; j<lstTotalSelectedFramesPredicatedPlaceNameOnePartition.size(); j++) {
				if (j != lstTotalSelectedFramesPredicatedPlaceNameOnePartition.size() -1) {
					sOnePartitionGroundTruthPlaceNames = sOnePartitionGroundTruthPlaceNames + lstTotalSelectedFramesGroundTruth.get(j) + ",";
					sOnePartitionPredicatedPlaceNames = sOnePartitionPredicatedPlaceNames + lstTotalSelectedFramesPredicatedPlaceNameOnePartition.get(j) + ",";
				} else {
					sOnePartitionGroundTruthPlaceNames = sOnePartitionGroundTruthPlaceNames + lstTotalSelectedFramesGroundTruth.get(j);
					sOnePartitionPredicatedPlaceNames = sOnePartitionPredicatedPlaceNames + lstTotalSelectedFramesPredicatedPlaceNameOnePartition.get(j);					
				}
			}
			
			//sNullFrame:  Null frame index
			//sOneMatchedFrame: One matched frame index
			//sPartitionFrameIndex: Multi-matched frame index
			String sFullPartition = sNullFrame + "; " + sOneMatchedFrame + "; " + sPartitionFrameIndex;
			sLineFull = nOneMatched + "," + fPartitionScoreSum + "; " + sOnePartitionGroundTruthPlaceNames + "; " + sOnePartitionPredicatedPlaceNames + "; " + sFullPartition + "\n";
			
			try {
				//One line for each partition
				//Number of frames matched with place names, Partition score; Ground Truth place names; Predicated Place names; Partition of Frame Index
				fwClusteringFull.write(sLineFull);
			} catch (Exception e) {
				
			}
			
			if (fMaxPartitionScoreSum < fPartitionScoreSum) {
				fMaxPartitionScoreSum = fPartitionScoreSum;
				
				sMaxOnePartitionFrameIndex = sFullPartition;
				sMaxOnePartitionGroundTruthPlaceNames = sOnePartitionGroundTruthPlaceNames;
				sMaxOnePartitionPredicatedPlaceNames = sOnePartitionPredicatedPlaceNames;
			}
			
		}   //for (i=0; i<nPartitionCnt; i++)
		
		
				
		Utility.DEBUG_INFO("Writing Result.........");
		
		List<Integer> lstFrameStat = new ArrayList<Integer>(); 
		//Write Simple Result
		//Line:  Similarity Score; #Partition; Ground Truth (i.e. base) Number of frames matched place name, Ground truth score; Number of one matched frame, Max Partition score sum; Ground Truth Place Names; Predicated Place Name; Partition
		double fSimilarityScore = Utility.calFieldsSimilarityS(sMaxOnePartitionGroundTruthPlaceNames, sMaxOnePartitionPredicatedPlaceNames, lstFrameStat);  //How (in order) similar are the Ground truth and Predicated results are for the frames
		
		//New added nCombineCntBefore + "," + nCombineCntAfter on 2014/12/04
		sLineSimple = fSimilarityScore +"; " + nCombineCntBefore + "," + nCombineCntAfter + "," + nPartitionCnt + "; "+ nOneMatchedFrameCalB + "," + fBaseCalBMatchingScore  + "; " + nOneMatched + "," + fMaxPartitionScoreSum  + "; " + sMaxOnePartitionGroundTruthPlaceNames + "; " + sMaxOnePartitionPredicatedPlaceNames + "; " + sMaxOnePartitionFrameIndex;
		
		sLineSimple = sLineSimple + "\n";
		
		String sStat = lstFrameStat.get(0) + "," + lstFrameStat.get(1) + "\n";  //Correct, Total Valid
		
		try {
			fwClusteringSimple.write(sLineSimple);
			fwClusteringStat.write(sStat);
		} catch (Exception e) {
			
		}
		
		
		
	}
	
	
	
	
	
	//Check whether the WiFi information for the frames in lstMacListIndexForCombine has any overlap;
	//If they have overlap, they possibly come from the same store
	//If any one of them has no overlap with others, they are considered as not coming from the same store, this combination is invalid and should be removed
	public static boolean checkSameStorePossibility(List<List<String>> lstlstPlaceMac, List<Integer> lstMacListIndexForCombine) {
		boolean bPossibleSameStore = true;
		int i,j;
		int nMacListIndexA, nMacListIndexB;
		boolean bOverlapped;
		
		for (i=0; i<lstMacListIndexForCombine.size(); i++) {
			nMacListIndexA = lstMacListIndexForCombine.get(i);
			
			for (j=i+1; j<lstMacListIndexForCombine.size(); j++) {
				nMacListIndexB = lstMacListIndexForCombine.get(j);
				if (nMacListIndexA != nMacListIndexB) {
					bOverlapped = Utility.checkOverlap(lstlstPlaceMac.get(nMacListIndexA), lstlstPlaceMac.get(nMacListIndexB));
					if (bOverlapped == false) {  //Two frames do not have common WiFi MAC, they are not from the same store
						return false;
					}
				}
			}
		}
		
		return bPossibleSameStore;
	}
	
	
	//Calculate candidate webs for each frames in the selected frame set
	//The result is a matrix in the form of list
	public static void CalCandidatePlaceForFrames(List<AL_PlaceWebData> lstPlaceWebData, List<String> lstTotalSelectedFramesRaw, List<List<Integer>> lstlstTotalSelectedFramesMatchedWebIndex) {
		int i,j;
		String sRawFrame = "";
		List<Integer> lstnMatchedWebIndex = null;
		
		for (i=0; i<lstTotalSelectedFramesRaw.size(); i++) {
			sRawFrame = lstTotalSelectedFramesRaw.get(i);
			
			lstnMatchedWebIndex = new ArrayList<Integer>();
			
			lstnMatchedWebIndex = CalCandidatePlaceForFrame(lstPlaceWebData, sRawFrame);
			
			lstlstTotalSelectedFramesMatchedWebIndex.add(lstnMatchedWebIndex);
		}
	}
	

	//Calculate candidate webs for each frames in the selected frame set
	//The result is a matrix in the form of list
	public static void CalCandidatePlaceForFramesCalB(List<AL_PlaceWebData> lstPlaceWebData, List<Integer> lstSelectedWebIndex, List<List<String>> lstlstTotalSelectedFramesRaw, List<List<Boolean>> lstlstFramesMatchedWebIndex) {
		int i,j;
		String sRawFrame = "";
		
		for (i=0; i<lstlstTotalSelectedFramesRaw.size(); i++) {
			List<String> lstTotalSelectedFramesRaw = lstlstTotalSelectedFramesRaw.get(i);
			
			List<Boolean> lstnMatchedWebIndex = new ArrayList<Boolean>();
			
			for (j=0; j<lstTotalSelectedFramesRaw.size(); j++) {
				sRawFrame = lstTotalSelectedFramesRaw.get(j);   //Text of one pic
			
				boolean bMatchedWithPlaceName = false;
			
				bMatchedWithPlaceName = CalCandidatePlaceForFrameCalB(lstPlaceWebData, lstSelectedWebIndex.get(i), sRawFrame);  //Check whether the words in sRawFrame hit the place name
			
				lstnMatchedWebIndex.add(bMatchedWithPlaceName);
			}
			
			lstlstFramesMatchedWebIndex.add(lstnMatchedWebIndex);
		}
	}
	
	
	//Calculate candidate webs for each frame
	public static List<Integer> CalCandidatePlaceForFrame(List<AL_PlaceWebData> lstPlaceWebData, String sRawFrame) {		
		List<Integer> lstnMatchedWebIndex = new ArrayList<Integer>();
		
		List<String> lstRawFrameString = Utility.splitStringList(sRawFrame);
				
		if (lstRawFrameString != null) {
			lstnMatchedWebIndex = Utility.checkFrameRelatedWeb(lstPlaceWebData, lstRawFrameString);
		}	
		
		return lstnMatchedWebIndex;
	}
	
	//Calculate candidate webs for each frame
	public static boolean CalCandidatePlaceForFrameCalB(List<AL_PlaceWebData> lstPlaceWebData, int nWebIndex, String sRawFrame) {
		boolean blnMatchedWebIndex = false;
		
		if (Utility.MATCH_WITH_PLACENAME == false) return false;
		
		List<String> lstRawFrameString = Utility.splitStringList(sRawFrame);
				
		if (lstRawFrameString != null) {
			blnMatchedWebIndex = Utility.checkFrameRelatedWebCalB(lstPlaceWebData, nWebIndex, lstRawFrameString);  //Check whether the words in the Raw Frame Strings hit the place name
		}	
		
		return blnMatchedWebIndex;
	}

	
	public static void CalculateTF(String sSubsetContent, List<String> lstSubsetWords, List<Double> lstfTFWeight) {
		String sarrWord[] = Utility.splitString(sSubsetContent);
		List<Integer> lstWordCnt = new ArrayList<Integer>();
		int i, j;
		int nPos;
		int nCount;
		double fWeight;
		
		//Calculate the #occurrence of each word
		for (i=0; i<sarrWord.length; i++) {
			if (i==0) {
				lstSubsetWords.add(sarrWord[i]);
				lstWordCnt.add(1);
			} else {
				nPos = lstSubsetWords.indexOf(sarrWord[i]);
				if (nPos == -1) {
					lstSubsetWords.add(sarrWord[i]);
					lstWordCnt.add(1);
				} else {
					nCount = lstWordCnt.get(nPos) + 1;
					lstWordCnt.set(nPos,  nCount);
				}
				
			}
		}
		
		int nMaxCnt = Utility.getMaxInt(lstWordCnt);
		int nTotalWordCnt = sarrWord.length;
		
		//Assign weight for OCRed words based on their frequencies
		for (i=0; i<lstWordCnt.size(); i++) {
			if (nMaxCnt > 0) {
				if (Utility.USE_TF_IDF_WEIGHT) {
					fWeight = 0.5 + 0.5*lstWordCnt.get(i).intValue()/nMaxCnt;
					fWeight = Math.round(10000.0*fWeight)/10000.0;
				} else {
					fWeight = Math.round(10000.0*lstWordCnt.get(i).intValue()/nTotalWordCnt)/10000.0;
				}
			} else {
				fWeight = 0.0;
			}
			
			lstfTFWeight.add(fWeight);
		}
		
	}


	//lstlstSubsetWords corresponds to [1 3] [2] [4 7] [5 8] [6]
	//lstWebIndex is the indexes of the corresponding web for each combination, e.g. here w1 w3 w5 w6 w8
	//Here each matching is only between one combination of pics (e.g. [1 3]) and its corresponding web data (e.g. w1)
	//Here the matching is not put in the whole environment of web, i.e. [1 3] is not going to match with all webs (i.e. w1 w3 w5 w6 w8), which is unnecessary, because we already know pic [1 3] only matches w1
	//Then (result) get a list of the matching scores, in this example, the list has 5 items.
	//
	public static List<Double> GroupMatchingS(List<AL_PlaceWebData> lstPlaceWebData, List<Integer> lstWebIndex, 
											  List<List<String>> lstlstSubsetWords, List<List<Double>> lstlstfTFWeight, List<List<String>> lstlstSubsetWordsRaw, 
											  List<String> lstPredicatedGroupPlaceNames) {

		List<Double> lstGroupMaxMatchingScore = new ArrayList<Double>();
		double fMaxMatchingScore = 0.0;
		int i, j;
		int nMaxIndex;
		int nWebIndex;
		
		for (i=0; i<lstlstSubsetWords.size(); i++) {
			nWebIndex = lstWebIndex.get(i);
			
			List<Integer> lstPlaceIndexForSubset = new ArrayList<Integer>();
			lstPlaceIndexForSubset.add(nWebIndex);
			
			List<List<Double>> lstlstPlaceWebDataForSubsetIDF = new ArrayList<List<Double>>();
			List<AL_PlaceWebData> lstPlaceWebDataForSubset = new ArrayList<AL_PlaceWebData>();
			
			//For Web data, we need to know IDF
			lstlstPlaceWebDataForSubsetIDF = Utility.calcIDFForSelectedSet(lstPlaceWebData, lstPlaceIndexForSubset, lstPlaceWebDataForSubset);
			
			List<Double> lstMatchingScoreTmp = new ArrayList<Double>();
			
			lstMatchingScoreTmp = SubsetMatching(lstPlaceWebDataForSubset, lstlstPlaceWebDataForSubsetIDF, lstlstSubsetWords.get(i), lstlstfTFWeight.get(i), lstlstSubsetWordsRaw.get(i));
			
			nMaxIndex = -1;
			fMaxMatchingScore = -1;
			
			//Decide which place name is matched (which has maximal matching score)
			for (j=0; j<lstMatchingScoreTmp.size(); j++) {
				if (fMaxMatchingScore < lstMatchingScoreTmp.get(j)) {
					fMaxMatchingScore = lstMatchingScoreTmp.get(j);
					nMaxIndex = j;
				}
			}
			
			lstGroupMaxMatchingScore.add(fMaxMatchingScore);
			lstPredicatedGroupPlaceNames.add(lstPlaceWebDataForSubset.get(nMaxIndex).getPlaceName());
		}
		
		return lstGroupMaxMatchingScore;
	}
	

	
	public static List<Double> GroupMatching(List<AL_PlaceWebData> lstPlaceWebData, List<List<Double>> lstlstPlaceWebDataIDF, 
			                                 List<List<String>> lstlstSubsetWords, List<List<Double>> lstlstfTFWeight, List<List<String>> lstlstSubsetWordsRaw, 
			                                 List<String> lstPredicatedGroupPlaceNames) {

		List<Double> lstGroupMaxMatchingScore = new ArrayList<Double>();
		double fMaxMatchingScore = 0.0;
		int i, j;
		int nMaxIndex;
		List<String> lstWords = null;
		
		for (i=0; i<lstlstSubsetWords.size(); i++) {
			lstWords = lstlstSubsetWords.get(i);
			List<Double> lstMatchingScoreTmp = new ArrayList<Double>();
			
			lstMatchingScoreTmp = SubsetMatching(lstPlaceWebData, lstlstPlaceWebDataIDF, lstlstSubsetWords.get(i), lstlstfTFWeight.get(i), lstlstSubsetWordsRaw.get(i));
			
			nMaxIndex = -1;
			fMaxMatchingScore = -1;
			
			for (j=0; j<lstMatchingScoreTmp.size(); j++) {
				if (fMaxMatchingScore < lstMatchingScoreTmp.get(j)) {
					fMaxMatchingScore = lstMatchingScoreTmp.get(j);
					nMaxIndex = j;
				}
			}
			
			lstGroupMaxMatchingScore.add(fMaxMatchingScore);
			lstPredicatedGroupPlaceNames.add(lstPlaceWebData.get(nMaxIndex).getPlaceName());
		}
		
		return lstGroupMaxMatchingScore;
	}
	
	
	public static List<Double> SubsetMatching(List<AL_PlaceWebData> lstPlaceWebData, List<List<Double>> lstlstPlaceWebDataIDF, List<String> lstWords, List<Double> lstfTFWeight, List<String> lstWordsRaw) {
		ConstructMacPlaceMapping locConstructMacPlaceMapping = new ConstructMacPlaceMapping();
		List<Double> lstMatchingScore = new ArrayList<Double>();
		
		//On Web side, should know IDF; On OCRed text side, should know TF 
		lstMatchingScore = locConstructMacPlaceMapping.matchKeywordsWithTFIDF_Subset(lstPlaceWebData, lstlstPlaceWebDataIDF, lstWords, lstfTFWeight, lstWordsRaw);
		
		return lstMatchingScore;
	}
	
	
	//lstSelectedFramesRaw:  Which will contain the selected raw pic text (i.e. Noun extraction is not carried out)
	//Return:  List of noun-extracted pic text
	public static List<String> getFrames(String sFullPathFolder, int nSubsetSize, List<String> lstSelectedFramesRaw) {
		List<String> lstTotalFrames = new ArrayList<String>();
		List<String> lstSelectedFrames = new ArrayList<String>();
		
		List<String> lstFileNameList = new ArrayList<String>();
		List<String> lstFileFullPathList = new ArrayList<String>();
		
		List<Integer> lstSelectedFrameIndex = new ArrayList<Integer>();
		
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		int nTotalFrameCnt = 0;
		
		//Get list of OCRed text files in the folder of one store
		if (Utility.USE_FULL_TEXT) {
			Utility.getOCRedWordsFileList_FullText_Cluster(sFullPathFolder, lstFileFullPathList);
		} else {
			Utility.getOCRedWordsFileList_Cluster(sFullPathFolder,lstFileFullPathList);	
		}
		
		//Get all the pic text of the store
		for (String sOCRedTextFile : lstFileFullPathList) {
			//In each file, one line is one pic
			try {
				fr = new FileReader(sOCRedTextFile);
				br = new BufferedReader(fr);
			
				while((sLine = br.readLine()) != null) {
					
					sLine = sLine.trim();
					if (sLine.startsWith("%")) continue;    // treat the beginning % as comment in OCRed text file
					
					nTotalFrameCnt = nTotalFrameCnt + 1;
					lstTotalFrames.add(sLine);    //Noun Extraction here?
				}
				
				fr.close();
			} catch (Exception e) {
				
			}
		}  //For
		
		
		lstSelectedFrameIndex = Utility.getRandom(nTotalFrameCnt, nSubsetSize); //Start from 1, decide which pics to select
		
		//Select pics from the store
		for (int i=0; i<lstSelectedFrameIndex.size(); i++) {
			int nIndex = lstSelectedFrameIndex.get(i) - 1;
			
			String sRawFrameContent = lstTotalFrames.get(nIndex);
			String sNounProperNameResult = getNounProperName(sRawFrameContent);
			lstSelectedFrames.add(sNounProperNameResult);   //Noun Extracted
			
			lstSelectedFramesRaw.add(sRawFrameContent);
		}

		
		return lstSelectedFrames;
	}
	
	
	public static String getNounProperName(String sRawContent) {
		String sResultNounProperName = "";
		List<String> lstNonDuplicateWords = new ArrayList<String>();
		List<String> lstResultValidWords = new ArrayList<String>();
		int nWordIdx;
		int i;
		
		KeywordExtraction locKeywordExtraction = new KeywordExtraction();
		
		//Get the non-duplicate noun/proper name
		lstNonDuplicateWords = locKeywordExtraction.extractKeywordFromContents(sRawContent);
		
		
		String sarrWord[] = Utility.splitString(sRawContent);
		
		if (sarrWord == null) return sResultNounProperName;
		
		//Get all valid words
		for (i = 0; i<sarrWord.length; i++) {
			String sTmpWord = sarrWord[i].toLowerCase().trim();
			
			nWordIdx = -1;
			for (int k=0; k<lstNonDuplicateWords.size(); k++) {
				if (sTmpWord.compareToIgnoreCase(lstNonDuplicateWords.get(k)) == 0) {
					nWordIdx = k;
					break;
				}
			}
			
			if (nWordIdx != -1) {
				lstResultValidWords.add(sTmpWord);
			}
								
		}				
		
		for (i=0; i<lstResultValidWords.size(); i++) {
			sResultNounProperName = sResultNounProperName + lstResultValidWords.get(i) + ",";
		}
		
		return sResultNounProperName;
	}
	
}
