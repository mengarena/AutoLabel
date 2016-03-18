package com.autolabel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class FrameStat {

	public FrameStat() {
		// TODO Auto-generated constructor stub
	}

	
	public static void calculateFrameStat(String sParentOCRFolder, String sResultFolder) {
		String sFrameStatResultFile = Utility.getFrameStatResultFile(sResultFolder);
		
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		FileWriter fwFrameStat = null;
		List<String> lstPlaceName = new ArrayList<String>();
		List<String> lstFullFolderName = new ArrayList<String>();
		String sAllOCRedWordsLine = "";
		String sOnePlaceMergedResultFile = "";
		int nTotalFrames;
		
		//Get sub folders (first layer) under the top parent folder
		Utility.getSubFolderList(sParentOCRFolder, lstPlaceName, lstFullFolderName);
		
		try {
			fwFrameStat = new FileWriter(sFrameStatResultFile, false);   //Overwrite
		
			for (int i=0; i<lstPlaceName.size(); i++) {
				List<String> lstOCRedWordsFiles = new ArrayList<String>();
				
				if (Utility.USE_FULL_TEXT == false) { 
					Utility.getOCRedWordsFileList(sParentOCRFolder, lstPlaceName.get(i), lstOCRedWordsFiles);
				} else {
					Utility.getOCRedWordsFileList_FullText(sParentOCRFolder, lstPlaceName.get(i), lstOCRedWordsFiles);
				}
				
				String sGroundTruthFile = Utility.getOnePlaceGroundTruthFile(sParentOCRFolder, lstPlaceName.get(i));
				String sGroundTruthPlaceName = Utility.getGroundTruthPlaceName(sGroundTruthFile);
				
				nTotalFrames = 0;
				
				for (String sOCRedTextFile : lstOCRedWordsFiles) {
					
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
				
				sLine = String.format("%3d", nTotalFrames) + "," + sGroundTruthPlaceName + "\n";
				fwFrameStat.write(sLine);
				
				
				List<Integer> lstFrameIdx5 = Utility.getRandom(nTotalFrames, 5);
				List<Integer> lstFrameIdx10 = Utility.getRandom(nTotalFrames, 10, lstFrameIdx5);
				List<Integer> lstFrameIdx15 = Utility.getRandom(nTotalFrames, 15, lstFrameIdx10);
				List<Integer> lstFrameIdx20 = Utility.getRandom(nTotalFrames, 20, lstFrameIdx15);
				List<Integer> lstFrameIdx25 = Utility.getRandom(nTotalFrames, 25, lstFrameIdx20);
				List<Integer> lstFrameIdx30 = Utility.getRandom(nTotalFrames, 30, lstFrameIdx25);
				List<Integer> lstFrameIdx35 = Utility.getRandom(nTotalFrames, 35, lstFrameIdx30);
				List<Integer> lstFrameIdx40 = Utility.getRandom(nTotalFrames, 40, lstFrameIdx35);
//				List<Integer> lstFrameIdx45 = Utility.getRandom(nTotalFrames, 45, lstFrameIdx40);
//				List<Integer> lstFrameIdx50 = Utility.getRandom(nTotalFrames, 50, lstFrameIdx45);				
				List<Integer> lstFrameIdxNoFrameSample = Utility.getRandom(nTotalFrames, Utility.NO_FRAME_SAMPLE);
				
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx5, 5);
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx10, 10);
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx15, 15);
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx20, 20);
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx25, 25);
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx30, 30);
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx35, 35);
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx40, 40);
//				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx45, 45);
//				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx50, 50);				
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdxNoFrameSample, Utility.NO_FRAME_SAMPLE);
				
			}
			
			fwFrameStat.close();
			
		} catch (Exception e) {
			
		}
		
	}
	
	
	public static void calculateFrameStat_FromExisting(String sParentOCRFolder, String sResultFolder) {
		String sFrameStatResultFile = Utility.getFrameStatResultFile(sResultFolder);
		
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		FileWriter fwFrameStat = null;
		List<String> lstPlaceName = new ArrayList<String>();
		List<String> lstFullFolderName = new ArrayList<String>();
		String sAllOCRedWordsLine = "";
		String sOnePlaceMergedResultFile = "";
		int nTotalFrames;
		
		//Get sub folders (first layer) under the top parent folder
		Utility.getSubFolderList(sParentOCRFolder, lstPlaceName, lstFullFolderName);
		
		try {
			//fwFrameStat = new FileWriter(sFrameStatResultFile, false);   //Overwrite
		
			for (int i=0; i<lstPlaceName.size(); i++) {
				List<String> lstOCRedWordsFiles = new ArrayList<String>();
				
				Utility.getOCRedWordsFileList(sParentOCRFolder, lstPlaceName.get(i), lstOCRedWordsFiles);
				
				String sGroundTruthFile = Utility.getOnePlaceGroundTruthFile(sParentOCRFolder, lstPlaceName.get(i));
				String sGroundTruthPlaceName = Utility.getGroundTruthPlaceName(sGroundTruthFile);
				
				nTotalFrames = 0;
				
				for (String sOCRedTextFile : lstOCRedWordsFiles) {
					
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
				
				sLine = String.format("%3d", nTotalFrames) + "," + sGroundTruthPlaceName + "\n";
				//fwFrameStat.write(sLine);
				
				
				
				List<Integer> lstFrameIdx30 = Utility.getFrameIndex(sParentOCRFolder, lstPlaceName.get(i), 30);
						
				List<Integer> lstFrameIdx35 = Utility.getRandom(nTotalFrames, 35, lstFrameIdx30);
				List<Integer> lstFrameIdx40 = Utility.getRandom(nTotalFrames, 40, lstFrameIdx35);
				List<Integer> lstFrameIdx45 = Utility.getRandom(nTotalFrames, 45, lstFrameIdx40);
				List<Integer> lstFrameIdx50 = Utility.getRandom(nTotalFrames, 50, lstFrameIdx45);
				
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx35, 35);
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx40, 40);
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx45, 45);
				Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx50, 50);
								
			}
			
			//fwFrameStat.close();
			
		} catch (Exception e) {
			
		}
		
	}	

	
	
	public static void calculateFrameStat_MultiIndexSet(String sParentOCRFolder, String sResultFolder, int nIndexSetNum) {
		String sFrameStatResultFile = Utility.getFrameStatResultFile(sResultFolder);
		
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		FileWriter fwFrameStat = null;
		List<String> lstPlaceName = new ArrayList<String>();
		List<String> lstFullFolderName = new ArrayList<String>();
		String sAllOCRedWordsLine = "";
		String sOnePlaceMergedResultFile = "";
		int nTotalFrames;
		
		//Get sub folders (first layer) under the top parent folder
		Utility.getSubFolderList(sParentOCRFolder, lstPlaceName, lstFullFolderName);
		
		try {
			fwFrameStat = new FileWriter(sFrameStatResultFile, false);   //Overwrite
		
			for (int i=0; i<lstPlaceName.size(); i++) {
				List<String> lstOCRedWordsFiles = new ArrayList<String>();
				
				if (Utility.USE_FULL_TEXT == false) { 
					Utility.getOCRedWordsFileList(sParentOCRFolder, lstPlaceName.get(i), lstOCRedWordsFiles);
				} else {
					Utility.getOCRedWordsFileList_FullText(sParentOCRFolder, lstPlaceName.get(i), lstOCRedWordsFiles);
				}
				
				String sGroundTruthFile = Utility.getOnePlaceGroundTruthFile(sParentOCRFolder, lstPlaceName.get(i));
				String sGroundTruthPlaceName = Utility.getGroundTruthPlaceName(sGroundTruthFile);
				
				nTotalFrames = 0;
				
				for (String sOCRedTextFile : lstOCRedWordsFiles) {
					
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
				
				sLine = String.format("%3d", nTotalFrames) + "," + sGroundTruthPlaceName + "\n";
				fwFrameStat.write(sLine);
				
				
				for (int j=1; j<=nIndexSetNum; j++) {
					List<Integer> lstFrameIdx5 = Utility.getRandom(nTotalFrames, 5);
					List<Integer> lstFrameIdx10 = Utility.getRandom(nTotalFrames, 10);
					List<Integer> lstFrameIdx15 = Utility.getRandom(nTotalFrames, 15);
					List<Integer> lstFrameIdx20 = Utility.getRandom(nTotalFrames, 20);
					List<Integer> lstFrameIdx25 = Utility.getRandom(nTotalFrames, 25);
					List<Integer> lstFrameIdx30 = Utility.getRandom(nTotalFrames, 30);
					List<Integer> lstFrameIdx35 = Utility.getRandom(nTotalFrames, 35);
					List<Integer> lstFrameIdx40 = Utility.getRandom(nTotalFrames, 40);
	//				List<Integer> lstFrameIdx45 = Utility.getRandom(nTotalFrames, 45);
	//				List<Integer> lstFrameIdx50 = Utility.getRandom(nTotalFrames, 50);				
					List<Integer> lstFrameIdxNoFrameSample = Utility.getRandom(nTotalFrames, Utility.NO_FRAME_SAMPLE);
					
					Utility.writeFrameIndex_MultipleSet(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx5, 5, j);
					Utility.writeFrameIndex_MultipleSet(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx10, 10, j);
					Utility.writeFrameIndex_MultipleSet(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx15, 15, j);
					Utility.writeFrameIndex_MultipleSet(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx20, 20, j);
					Utility.writeFrameIndex_MultipleSet(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx25, 25, j);
					Utility.writeFrameIndex_MultipleSet(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx30, 30, j);
					Utility.writeFrameIndex_MultipleSet(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx35, 35, j);
					Utility.writeFrameIndex_MultipleSet(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx40, 40, j);
	//				Utility.writeFrameIndex_MultipleSet(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx45, 45, j);
	//				Utility.writeFrameIndex_MultipleSet(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdx50, 50, j);				
					Utility.writeFrameIndex(sParentOCRFolder, lstPlaceName.get(i), lstFrameIdxNoFrameSample, Utility.NO_FRAME_SAMPLE);
				}
			}
			
			fwFrameStat.close();
			
		} catch (Exception e) {
			
		}
		
	}
	
	
	
	
}
