package com.autolabel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ProcessEvaluationData {
	private String m_sDataFolder = ""; 
	
	public ProcessEvaluationData() {
		// TODO Auto-generated constructor stub
	}
	
	public ProcessEvaluationData(String sDataFolder) {
		m_sDataFolder = sDataFolder;
	}
	
	public static void CalculateRank(String sDataFolder) {
		int i;
		String sLine = "";
		String sRankingFile = "";
		
		System.out.println("---" + sDataFolder);
		
		List<String> lstCMFiles = Utility.getCMFileList(sDataFolder);
				
		for (String sCMFIle : lstCMFiles) {
			List<String> lstGTPlaceName = new ArrayList<String>();
			List<String> lstCandidatePlaceName = new ArrayList<String>();
			List<List<Double>> lstlstMatchingScore = new ArrayList<List<Double>>();

			Utility.extractCM(sCMFIle, lstGTPlaceName, lstCandidatePlaceName, lstlstMatchingScore);
			
			sRankingFile = Utility.getMatchingScoreRankingFile(sCMFIle);
			
			List<Integer> lstRanking = new ArrayList<Integer>();
			
			lstRanking = Utility.calculateMatchingScoreRanking(lstGTPlaceName, lstCandidatePlaceName, lstlstMatchingScore);
			
			sLine = "";
			try {
				FileWriter fwRankingFile = new FileWriter(sRankingFile, false);  //Overwrite
				
				for (i=0; i<lstRanking.size(); i++) {
					sLine = sLine + lstRanking.get(i) + ",";
				}
				
				sLine = sLine + "\n";
				
				fwRankingFile.write(sLine);
				
				fwRankingFile.close();
			} catch (Exception e) {
				
			}
			
		}
		
		
	}
	
	public static void CalculateRankAllSub(String sParentDataFolder) {
		List<String> lstSubFolderList = new ArrayList<String>();
		List<String> lstSubFolderNameList = new ArrayList<String>();
		
		Utility.getSubFolderList(sParentDataFolder, lstSubFolderNameList, lstSubFolderList);
		
		for (String sSubFolder : lstSubFolderList) {
			CalculateRank(sSubFolder);
		}
	
	}


	public static void CalculateRank_MultipleIndexSet(String sDataFolder) {
		int i;
		String sLine = "";
		String sRankingFile = "";
		
		System.out.println("---" + sDataFolder);
		
		List<List<String>> lstlstCMFiles = Utility.getCMFileList_MultipleIndexSet(sDataFolder);
	
		
		for (List<String> lstCMFiles : lstlstCMFiles) {
		
			sRankingFile = Utility.getMatchingScoreRankingFile_MultipleIndexSet(lstCMFiles.get(0));
			
			try {
				FileWriter fwRankingFile = new FileWriter(sRankingFile, false);  //Overwrite
				
				for (String sCMFIle : lstCMFiles) {
					List<String> lstGTPlaceName = new ArrayList<String>();
					List<String> lstCandidatePlaceName = new ArrayList<String>();
					List<List<Double>> lstlstMatchingScore = new ArrayList<List<Double>>();
		
					Utility.extractCM(sCMFIle, lstGTPlaceName, lstCandidatePlaceName, lstlstMatchingScore);
					
					List<Integer> lstRanking = new ArrayList<Integer>();
					
					lstRanking = Utility.calculateMatchingScoreRanking(lstGTPlaceName, lstCandidatePlaceName, lstlstMatchingScore);
					
					sLine = "";
						
					for (i=0; i<lstRanking.size(); i++) {
						sLine = sLine + lstRanking.get(i) + ",";
					}
					
					sLine = sLine + "\n";
					
					fwRankingFile.write(sLine);
				}
				 	
				fwRankingFile.close();	
					
			} catch (Exception e) {
				
			}
		}
		
	}
	
	
	public static void CalculateRankAllSub_MultipleIndexSet(String sParentDataFolder) {
		List<String> lstSubFolderList = new ArrayList<String>();
		List<String> lstSubFolderNameList = new ArrayList<String>();
		
		Utility.getSubFolderList(sParentDataFolder, lstSubFolderNameList, lstSubFolderList);
		
		for (String sSubFolder : lstSubFolderList) {
			CalculateRank_MultipleIndexSet(sSubFolder);
		}
	
	}
	
	
}
