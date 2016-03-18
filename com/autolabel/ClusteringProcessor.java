package com.autolabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ClusteringProcessor {

	public ClusteringProcessor() {
		// TODO Auto-generated constructor stub
	}
	
	public static void doCluster(String sWebDataFile, String sCrowdsourcedDataFolder, String sFrameIndexFile, String sResultFolder, int nTotalFrameCnt, int nSubsetSize, int nLoopCnt) {
		String sResultFilePathNameFull = "";
		String sResultFilePathNameSimple = ""; 

		if (Utility.MATCH_WITH_PLACENAME == true) { 
			sResultFilePathNameFull = sResultFolder + File.separator + "ClusteringResultFull_withPlaceName_" + nTotalFrameCnt + "_" + nSubsetSize + "_" + nLoopCnt + ".csv";
			sResultFilePathNameSimple = sResultFolder + File.separator + "ClusteringResultSimple_withPlaceName_" + nTotalFrameCnt + "_" + nSubsetSize + "_" + nLoopCnt + ".csv";
		} else {
			sResultFilePathNameFull = sResultFolder + File.separator + "ClusteringResultFull_withoutPlaceName_" + nTotalFrameCnt + "_" + nSubsetSize + "_" + nLoopCnt + ".csv";
			sResultFilePathNameSimple = sResultFolder + File.separator + "ClusteringResultSimple_withoutPlaceName_" + nTotalFrameCnt + "_" + nSubsetSize + "_" + nLoopCnt + ".csv";			
		}
		
		//In result file, each line, besides the first one(full) or two(simple) fields, in the remaining fields, first half is Ground Truth Names, second half is Predicated Place Names
				 
		for (int i=0; i<nLoopCnt; i++) {
			try {
				FileWriter fwClusteringFull = new FileWriter(sResultFilePathNameFull, true);   //Append
				FileWriter fwClusteringSimple = new FileWriter(sResultFilePathNameSimple, true);   //Append

				ProcessCluster(sWebDataFile, sCrowdsourcedDataFolder, sFrameIndexFile, fwClusteringFull, fwClusteringSimple, nTotalFrameCnt, nSubsetSize);

				fwClusteringFull.close();
				fwClusteringSimple.close();
			} catch (Exception e) {
				 
			}
			
		}
			
	}
	

	public static void ProcessCluster(String sWebDataFile, String sCrowdsourcedDataFolder, String sFrameIndexFile, FileWriter fwClusteringFull, FileWriter fwClusteringSimple, int nTotalFrameCnt, int nSubsetSize) {
		int nPlaceCnt = nTotalFrameCnt/nSubsetSize;
		int i,j,k;

		double fGroupMaxMatchingScoreSum = 0.0;
		double fMaxGroupMaxMatchingScoreSum = 0.0;
		int nMaxGroupIndex = 0;
		String sMaxOneGroupGroundTruthPlaceNames = "";
		String sMaxOneGroupPredicatedPlaceNames = "";		
		
		List<String> lstPlaceNameList = new ArrayList<String>();
		List<String> lstSubFolderFullPathList = new ArrayList<String>();
		
		List<String> lstSelectedPlace = new ArrayList<String>();
		List<String> lstSelectedFullPath = new ArrayList<String>();
		
		List<String> lstGroundTruthPlaceName = new ArrayList<String>();
		
		List<String> lstTotalSelectedFrames = new ArrayList<String>();
		List<String> lstTotalSelectedFramesRaw = new ArrayList<String>();
		List<String> lstTotalSelectedFramesGroundTruth = new ArrayList<String>();
		
		List<AL_PlaceWebData> lstPlaceWebData = new ArrayList<AL_PlaceWebData>();
		
		lstPlaceWebData = Utility.getPlaceKeywordsList(sWebDataFile);
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
		
		//Determine IDF for each word in lstlstCollectionCandidate
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
		}
		
		
		Utility.getSubFolderList(sCrowdsourcedDataFolder, lstPlaceNameList, lstSubFolderFullPathList);
		
		List<Integer> lstPlaceIndex = Utility.getRandom(lstPlaceNameList.size(), nPlaceCnt);

		for (i=0; i<nPlaceCnt; i++) {			
			lstSelectedPlace.add(lstPlaceNameList.get(lstPlaceIndex.get(i)-1));
			lstSelectedFullPath.add(lstSubFolderFullPathList.get(lstPlaceIndex.get(i)-1));
			
			String sGroundTruthPlaceNameTmp = Utility.getGroundTruthPlaceNameFromFolder(lstSubFolderFullPathList.get(lstPlaceIndex.get(i)-1));
			lstGroundTruthPlaceName.add(sGroundTruthPlaceNameTmp);
		}
		
		Utility.DEBUG_INFO("1");
		
		for (i=0; i<nPlaceCnt; i++) {
			List<String> lstFrames = new ArrayList<String>();
			List<String> lstFramesRaw = new ArrayList<String>();
			
			lstFrames = getFrames(lstSelectedFullPath.get(i), nSubsetSize, lstFramesRaw);  //Select frames from one place
			
			for (j=0; j<lstFrames.size(); j++) {
				lstTotalSelectedFrames.add(lstFrames.get(j));
				lstTotalSelectedFramesRaw.add(lstFramesRaw.get(j));
				lstTotalSelectedFramesGroundTruth.add(lstGroundTruthPlaceName.get(i));
			}
		}
		
		Utility.DEBUG_INFO("2");
		
		//So far, frames are selected, each frame has a ground truth place name
		//Next, based on the subset defined in sFrameIndexFile, to partition the frames
		//In sFrameIndexFile, frame indexs are like:  2,3,4; 5 1,6;
		List<List<List<Integer>>> lstlstlstGroupIndex = Utility.getFrameGroupIndex(sFrameIndexFile);
		
		Utility.DEBUG_INFO("3");
		
		List<Double> lstGroupMatchingScoreSum = new ArrayList<Double>();
		
		for (i=0; i<lstlstlstGroupIndex.size(); i++) {
			Utility.DEBUG_INFO("4 - 1" + "-------" + i);
			List<List<Integer>> lstlstOneGroupIndexSet = lstlstlstGroupIndex.get(i);
			List<List<String>> lstlstSubsetWords = new ArrayList<List<String>>();
			List<List<Double>> lstlstfTFWeight = new ArrayList<List<Double>>();
			List<List<String>> lstlstSubsetWordsRaw = new ArrayList<List<String>>();
			
			String sOneGroupGroundTruthPlaceNames = "";
			String sOneGroupPredicatedPlaceNames = "";
			List<List<String>> lstlstOneGroupGroudTruthPlaceNames = new ArrayList<List<String>>();
			
			List<Double> lstGroupSubsetMaxMatchingScore = new ArrayList<Double>();
			
			for (j=0; j<lstlstOneGroupIndexSet.size(); j++) {
				
				Utility.DEBUG_INFO("Group-" + i + "==== Subset-" + j);
				
				sSubsetContent = "";
				List<String> lstSubsetGroundTruth = new ArrayList<String>();
				
				sSubsetContentRaw = "";
				
				List<Integer> lstSubsetIndex = lstlstOneGroupIndexSet.get(j);
				
				for (k=0; k<lstSubsetIndex.size(); k++) {
					sSubsetContent = sSubsetContent + lstTotalSelectedFrames.get(lstSubsetIndex.get(k)-1) + ",";
					
					sGroundTruthPlaceName = lstTotalSelectedFramesGroundTruth.get(lstSubsetIndex.get(k)-1);
					lstSubsetGroundTruth.add(sGroundTruthPlaceName);
					sOneGroupGroundTruthPlaceNames = sOneGroupGroundTruthPlaceNames + sGroundTruthPlaceName + ",";
					
					sSubsetContentRaw = sSubsetContentRaw + lstTotalSelectedFramesRaw.get(lstSubsetIndex.get(k)-1) + ",";
				}
				
				List<String> lstSubsetWords = new ArrayList<String>();
				List<Double> lstfTFWeight = new ArrayList<Double>();
				
				CalculateTF(sSubsetContent, lstSubsetWords, lstfTFWeight);
				
				List<String> lstSubsetWordsRaw = Utility.splitStringList(sSubsetContentRaw);
				
				lstlstSubsetWords.add(lstSubsetWords);
				lstlstfTFWeight.add(lstfTFWeight);
				
				lstlstSubsetWordsRaw.add(lstSubsetWordsRaw);
				lstlstOneGroupGroudTruthPlaceNames.add(lstSubsetGroundTruth);   //Every frame has its Ground Truth Place Name
			}
			
			Utility.DEBUG_INFO("4 - 2" + "-------" + i);
			List<String> lstPredicatedPlaceNames = new ArrayList<String>();  //Each subset has one predicated place name

			lstGroupSubsetMaxMatchingScore = GroupMatching(lstPlaceWebData, lstlstPlaceWebDataIDF, lstlstSubsetWords, lstlstfTFWeight, lstlstSubsetWordsRaw, lstPredicatedPlaceNames);
			
			fGroupMaxMatchingScoreSum = Utility.getTotal(lstGroupSubsetMaxMatchingScore);
			lstGroupMatchingScoreSum.add(fGroupMaxMatchingScoreSum);
			
			//Write Full Result
			sLineFull = fGroupMaxMatchingScoreSum + "," + sOneGroupGroundTruthPlaceNames;
			
			for (j=0; j<lstPredicatedPlaceNames.size(); j++) {
				for (k=0; k<nSubsetSize; k++) {
					sOneGroupPredicatedPlaceNames = sOneGroupPredicatedPlaceNames + lstPredicatedPlaceNames.get(j) + ",";
				}
			}
			
			Utility.DEBUG_INFO("4 - 3" + "-------" + i);
			sLineFull = sLineFull + sOneGroupPredicatedPlaceNames + "\n";
			
			try {
				fwClusteringFull.write(sLineFull);
			} catch (Exception e) {
				
			}
			
			Utility.DEBUG_INFO("4 - 4" + "-------" + i);
			if (fMaxGroupMaxMatchingScoreSum < fGroupMaxMatchingScoreSum) {
				fMaxGroupMaxMatchingScoreSum = fGroupMaxMatchingScoreSum;
				nMaxGroupIndex = i;
				
				sMaxOneGroupGroundTruthPlaceNames = sOneGroupGroundTruthPlaceNames;
				sMaxOneGroupPredicatedPlaceNames = sOneGroupPredicatedPlaceNames;
			}
			
		}
		
		Utility.DEBUG_INFO("5");
		
		//Write Simple Result
		double fSimilarityScore = Utility.calFieldsSimilarity(sMaxOneGroupGroundTruthPlaceNames, sMaxOneGroupPredicatedPlaceNames);  //How (in order) similar are the Ground truth and Predicated results are for the frames
		sLineSimple = fMaxGroupMaxMatchingScoreSum + "," + fSimilarityScore + "," + sMaxOneGroupGroundTruthPlaceNames + ";" + sMaxOneGroupPredicatedPlaceNames;
		
		sLineSimple = sLineSimple + "\n";
		
		try {
			fwClusteringSimple.write(sLineSimple);
		} catch (Exception e) {
			
		}
		
	}
	
	
	public static void CalculateTF(String sSubsetContent, List<String> lstSubsetWords, List<Double> lstfTFWeight) {
		String sarrWord[] = Utility.splitString(sSubsetContent);
		List<Integer> lstWordCnt = new ArrayList<Integer>();
		int i, j;
		int nPos;
		int nCount;
		double fWeight;
		
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
		
		lstMatchingScore = locConstructMacPlaceMapping.matchKeywordsWithTFIDF_Subset(lstPlaceWebData, lstlstPlaceWebDataIDF, lstWords, lstfTFWeight, lstWordsRaw);
		
		return lstMatchingScore;
	}
	
	
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
				
		if (Utility.USE_FULL_TEXT) {
			Utility.getOCRedWordsFileList_FullText_Cluster(sFullPathFolder, lstFileFullPathList);
		} else {
			Utility.getOCRedWordsFileList_Cluster(sFullPathFolder,lstFileFullPathList);	
		}
		
		for (String sOCRedTextFile : lstFileFullPathList) {
			
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
		
		lstSelectedFrameIndex = Utility.getRandom(nTotalFrameCnt, nSubsetSize); //Start from 1
		
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
