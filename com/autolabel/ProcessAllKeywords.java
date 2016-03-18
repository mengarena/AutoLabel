package com.autolabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ProcessAllKeywords {

	private String m_sParentDataFolder = "";
		
	private double m_fMetaWeight = Utility.DEFAULT_TOTAL_WEIGHT_META;
	private double m_fContentWeight = Utility.DEFAULT_TOTAL_WEIGHT_WEB_CONTENT;
	private double m_fImageWeight = Utility.DEFAULT_TOTAL_WEIGHT_WEB_IMG;
	
	private int m_nTopFinalKeywordNum = Utility.DEFAULT_TOP_KEYWORD_CNT;
	
	public ProcessAllKeywords() {
		// TODO Auto-generated constructor stub
	}
	
	public ProcessAllKeywords(String sParentDataFolder, double fMetaWeight, double fContentWeight, double fImageWeight, int nTopFinalKeywordNum) {
		m_sParentDataFolder = sParentDataFolder;
		
		m_fMetaWeight = fMetaWeight;
		m_fContentWeight = fContentWeight;
		m_fImageWeight = fImageWeight;
		
		m_nTopFinalKeywordNum = nTopFinalKeywordNum;
	}

	//This function process all the WebText files and WebImageText files and assign keyword weights and save all the result into one file: PlaceKeywordList_Lat_Long.csv
	//The final file is under the Top folder of web data, 
	//In this file, one line for each place/store
	//In each line: Place/Store name, Lat, Long, Keyword1, Weight1, Keyword2, Weight2...
	public void ProcessPlaceKeyword() {
		
		//File flFolder;
		List<String> lstsWebTextFileList = null;
		int i;
		String sWebTextFileName;
		String sLine = "";
		String sPlaceKeywordListFile = "";
		
		if (m_sParentDataFolder.length() == 0) return;
		
//		flFolder = new File(m_sParentDataFolder);
//		try {
//			flarrFileList = flFolder.listFiles();
//		} catch (Exception e) {
//			
//		}
		
		lstsWebTextFileList = Utility.getWebTextFileList(m_sParentDataFolder);
		
		if (lstsWebTextFileList == null || lstsWebTextFileList.size() == 0) return;
		
		//sPlaceKeywordListFile = Utility.getPlaceKeywordFile(m_sParentDataFolder); //PlaceKeywordList_Lat_Long.csv
		sPlaceKeywordListFile = Utility.getPlaceKeywordFile(m_sParentDataFolder, m_fMetaWeight, m_fContentWeight, m_fImageWeight, m_nTopFinalKeywordNum); //PlaceKeywordList_Lat_Long.csv (this will be the result)

		System.out.println("[DEBUG 1] Get Place Keyword List File: " + sPlaceKeywordListFile);
		
		try {
			FileWriter fwPlaceKeywordListFile = new FileWriter(sPlaceKeywordListFile, false);   //Overwrite
			//for (i=0; i<flarrFileList.length; i++) {
			//	sWebTextFileName = flarrFileList[i].getName();
			for (i=0; i<lstsWebTextFileList.size(); i++) {
				sWebTextFileName = lstsWebTextFileList.get(i);
				
				System.out.println("[DEBUG 2] --- Process One Place Web Text File: " + sWebTextFileName);
				
				///sLine = ProcessOnePlaceKeyword(sWebTextFileName);
				
				if (Utility.USE_FULL_TEXT == false) {
					sLine = ProcessOnePlaceKeyword_Simple(sWebTextFileName);
				} else {
					sLine = ProcessOnePlaceKeyword_AllWebText(sWebTextFileName); 
				}
				
				sLine = sLine + "\n";
				
				fwPlaceKeywordListFile.write(sLine); 
			}
			
			fwPlaceKeywordListFile.close();
		} catch (Exception e) {
				 
		}
				
	}
	
	
	//This function process the webtext file and web image text file, and finally return one line, 
	//The final line will be: Place/Store name, Lat, Long, Keyword1, Weight1, Keyword2, Weight2...
	//These content will be saved in PlaceKeywordList_Lat_Long.csv file
	private String ProcessOnePlaceKeyword(String sWebTextFileName) {
		String sLine = "";
		String sWebTextFileNameFullPath = Utility.getWebTextFileNameFullPath(m_sParentDataFolder, sWebTextFileName);
		String sWebImageTextFileNameFullPath = Utility.getWebImageTextFileNameFullPath(m_sParentDataFolder, sWebTextFileName);
		
		FileReader frWebText = null;
		BufferedReader brWebText = null;
		FileReader frWebImageText = null;
		BufferedReader brWebImageText = null;

		String sContentLine = "";
		int nLineNum = 0;
		String sStoreName = "";
		String sGps = "";
		String sMetaKeywordList = "";
		String sContentKeywordList = "";
		
		String sTotalWebImageContentLine = "";
		
		int nColonPos;
		int i,j;
		List<String> lststrMetaKeywords = null;
		List<Double> lstfMetaWeights = null;
		
		List<String> lststrContentKeywords = null;
		List<Double> lstfContentWeights = null;

		List<String> lststrImageKeywords = null;
		List<Double> lstfImageWeights = null;

		List<String> lststrMergedSortedKeywords = new ArrayList<String>();
		List<Double> lstfMergedSortedWeights = new ArrayList<Double>();
		
		List<String> lststrFinalKeywords = new ArrayList<String>();
		List<Double> lstfFinalWeights = new ArrayList<Double>();
		
		double fMaxWeight = 0;
		double fTfWeight = 0;
		
		//Process files under WebText\ folder
		try {
			frWebText = new FileReader(sWebTextFileNameFullPath);
			brWebText = new BufferedReader(frWebText);
			
			while((sContentLine = brWebText.readLine()) != null) {
				nLineNum = nLineNum + 1;
				nColonPos = sContentLine.indexOf(':'); 
				
				if (nLineNum == 1) {  //Store Name
					sStoreName = sContentLine.substring(nColonPos+1);
					
				} else if (nLineNum == 2) { //GPS
					sGps = sContentLine.substring(nColonPos+1);
					
				} else if (nLineNum == 3) { //Metakeyword
					sMetaKeywordList = sContentLine.substring(nColonPos+1);
					if (sMetaKeywordList.length() > 0) {
						String[] fields = Utility.getFields(sMetaKeywordList);
						if (fields != null && fields.length > 0) {
							lststrMetaKeywords = new ArrayList<String>();
							lstfMetaWeights = new ArrayList<Double>();
							
							//Here set weight for meta keywords
							double fWeight = Math.round(1*10000.0/fields.length)/10000.0;  //Uniformly assign weight
							
							for (i=0; i<fields.length; i++) {
								lststrMetaKeywords.add(fields[i]);								
								lstfMetaWeights.add(fWeight);
							}
						}
						
					}
					
				} else if (nLineNum == 4) {  //Top N web content keywords
					sContentKeywordList = sContentLine.substring(nColonPos+1);
					if (sContentKeywordList.length() > 0) {
						String[] fields = Utility.getFields(sContentKeywordList);
						
						if (fields != null && fields.length > 0) {
							lststrContentKeywords = new ArrayList<String>();
							lstfContentWeights = new ArrayList<Double>();
							
							for (i=0; i<fields.length/2; i++) {
								lststrContentKeywords.add(fields[i*2]);
								lstfContentWeights.add(Double.valueOf(fields[i*2+1]).doubleValue());
							}
						}
					}
				}
				
			}
			
			frWebText.close();
		} catch (Exception e) {
			
		}

		
		if (Utility.USE_WEB_IMAGE_TEXT) {  // Use Web Image Text or not
			//Process files under WebImageText\ folder
			//The content in these files are raw OCRed data from in-store pictures
			try {
				
				File flImageText = new File(sWebImageTextFileNameFullPath);
				
				if (flImageText.exists()) {
					
					System.out.println("[DEBUG 3]===Processing Web Image Text File: " + sWebImageTextFileNameFullPath);
					
					frWebImageText = new FileReader(sWebImageTextFileNameFullPath);
					brWebImageText = new BufferedReader(frWebImageText);
					
					while ((sContentLine = brWebImageText.readLine()) != null) {
						sTotalWebImageContentLine = sTotalWebImageContentLine + sContentLine + ",";
					}
				
					frWebImageText.close();
				}
				
			} catch (Exception e) {
				
			}
			
			if (sTotalWebImageContentLine.length() > 0) {
				lststrImageKeywords = new ArrayList<String>();
				lstfImageWeights = new ArrayList<Double>();
				
				System.out.println("[DEBUG 4] ---Analyzing Web Image Text");
				AnalyzeWebImageText(sTotalWebImageContentLine, m_nTopFinalKeywordNum, lststrImageKeywords, lstfImageWeights);
				
			}
			
		}
		
		
		System.out.println("[DEBUG 5] --- Merge Meta/Content/Image Keywords together !");
		//Merge Meta Keywords, Content Keywords, Image Keywords into Representative Keywords
		MergeKeywords(lststrMetaKeywords, lstfMetaWeights, lststrContentKeywords, lstfContentWeights, lststrImageKeywords, lstfImageWeights, lststrMergedSortedKeywords, lstfMergedSortedWeights);

		if (lststrMergedSortedKeywords.size() > 0) {
			System.out.println("[DEBUG 6] --- Select Top Keywords");
			SelectTopKeywords(lststrMergedSortedKeywords, lstfMergedSortedWeights, lststrFinalKeywords, lstfFinalWeights);

			sLine = sStoreName + "," + sGps;
			
			if (Utility.USE_TF_IDF_WEIGHT) {  //Change the overall weight to TF weighting
				fMaxWeight = Utility.getMaxDouble(lstfFinalWeights);
				
				for (j=0; j<lstfFinalWeights.size(); j++) {
					if (fMaxWeight > 0) {
						fTfWeight = 0.5 + 0.5*lstfFinalWeights.get(j)/fMaxWeight;
						fTfWeight = Math.round(fTfWeight*10000)/10000.0;
					} else {
						fTfWeight = 0;
					}
					
					sLine = sLine + "," + lststrFinalKeywords.get(j) + "," + fTfWeight;
				}
				
			} else {
				
				for (i=0; i<lststrFinalKeywords.size(); i++) {
					sLine = sLine + "," + lststrFinalKeywords.get(i) + "," + lstfFinalWeights.get(i);
				}
			}
		}
		
		return sLine;
	}
	
	
	//Process the web image text file content, assign weights based on their frequencies
	public void AnalyzeWebImageText(String sTotalWebImageContentLine, int nTopFinalKeywordNum, List<String> lststrImageKeywords, List<Double> lstfImageWeights) {
		KeywordExtraction locKeywordExtraction = new KeywordExtraction();
		List<String> lstWords = new ArrayList<String>();
		List<Integer> lstWordCount = new ArrayList<Integer>();
				
		List<Integer> lstFreqIdx = new ArrayList<Integer>();   //This list contains the index of words in lstWords, whose frquency is in DESC order
		boolean bFind = false;
		
		List<String> lststrKeywords = new ArrayList<String>();
		
		//Get the non-duplicate noun/proper name
		lststrKeywords = locKeywordExtraction.extractKeywordFromContents(sTotalWebImageContentLine);
		
		String sArrWord[] = Utility.splitString(sTotalWebImageContentLine);
		
		//Calculate word frequency
		for (int i = 0; i<sArrWord.length; i++) {
			String sTmpWord = sArrWord[i].toLowerCase();
			
			if (lststrKeywords.contains(sTmpWord)) {
				if (lstWords.contains(sTmpWord)) {  // Exist, increase the count
					int nIndex = lstWords.indexOf(sTmpWord);
					int nCurCount = lstWordCount.get(nIndex).intValue();
					lstWordCount.set(nIndex, Integer.valueOf(nCurCount + 1));
				} else {  // Not exist, insert it
					lstWords.add(sTmpWord);
					lstWordCount.add(Integer.valueOf(1));
				}
			}
		}
		

		//Calculate the order of frequency of words and its corresponding index (desc order)
		lstFreqIdx.add(Integer.valueOf(0));
				
		for (int k=1; k<lstWordCount.size(); k++) {
			int nCurFreq = lstWordCount.get(k).intValue();
						
			bFind = false;
			for (int kk=0; kk<lstFreqIdx.size(); kk++) {  //Look for which position to insert
				int nIndex = lstFreqIdx.get(kk).intValue();
				int nFreq = lstWordCount.get(nIndex).intValue();
				if (nCurFreq > nFreq) {
					lstFreqIdx.add(kk, Integer.valueOf(k));
					bFind = true;
					break;
				}
			}
			
			if (bFind == false) {   //Should add to the end
				lstFreqIdx.add(Integer.valueOf(k));
			}
		}
		
		List<Integer> lstTopFreqCount = new ArrayList<Integer>();
		
		int nTotalCount = 0;
		
		//This code get the top 10 frequent words
		for (int i=0; i<lstFreqIdx.size(); i++) {
			int nIndex = lstFreqIdx.get(i).intValue();
			lststrImageKeywords.add(lstWords.get(nIndex));
			lstTopFreqCount.add(lstWordCount.get(nIndex));
			nTotalCount = nTotalCount + lstWordCount.get(nIndex);
			if (i >= nTopFinalKeywordNum -1) break;
		}
		
		if (nTotalCount > 0) {
			//Assign weight (based on total weight=1.0)
			for (int i=0; i<lststrImageKeywords.size(); i++) {
				double fWeight = Math.round(10000.0*lstTopFreqCount.get(i).intValue()/nTotalCount)/10000.0;
				lstfImageWeights.add(fWeight);
			}
		}
		
		return;
		
	}

	
	//Merge keyword/weight from three parts
	private void MergeKeywords(List<String> lststrMetaKeywods, List<Double> lstfMetaWeights, List<String> lststrContentKeywords, List<Double> lstfContentWeights, List<String> lststrImageKeywords, List<Double> lstfImageWeights, List<String> lststrMergedSortedKeywords, List<Double> lstfMergedSortedWeights) {
		int i,j;
		int nPos, nInsertPos;
		
		double fTotalWeightMeta;
		double fTotalWeightContent;
		double fTotalWeightImage;
		double fWeight, fTmpWeight;
		boolean bUseMeta = false;
		
		List<String> lststrMergedKeywords = new ArrayList<String>();
		List<Double> lstfMergedWeights = new ArrayList<Double>();
		
		if (Utility.USE_WEB_META_KEYWORD == false || lststrMetaKeywods == null) {
			bUseMeta = false;
		} else {
			bUseMeta = true;
		}
		
		//Rules for weight of each part
//		if (lststrMetaKeywods == null) {
		if (bUseMeta == false) {
			if (lststrContentKeywords != null && lststrImageKeywords != null) {
				fTotalWeightMeta = 0;
				fTotalWeightContent = m_fContentWeight + m_fMetaWeight * m_fContentWeight/(m_fContentWeight + m_fImageWeight);
				fTotalWeightImage = m_fImageWeight + m_fMetaWeight * m_fImageWeight /(m_fContentWeight + m_fImageWeight);
			} else if (lststrContentKeywords != null && lststrImageKeywords == null) {
				fTotalWeightMeta = 0;
				fTotalWeightContent = 1.0;
				fTotalWeightImage = 0;
			} else if (lststrContentKeywords == null && lststrImageKeywords != null) {
				fTotalWeightMeta = 0;
				fTotalWeightContent = 0;
				fTotalWeightImage = 1.0;
			} else {
				fTotalWeightMeta = 0;
				fTotalWeightContent = 0;
				fTotalWeightImage = 0;
				return;
			}
		
		} else {
			
			if (lststrContentKeywords == null && lststrImageKeywords != null) {
				fTotalWeightMeta = m_fMetaWeight;
				fTotalWeightContent = 0;
				fTotalWeightImage = m_fContentWeight + m_fImageWeight;
			} else if (lststrContentKeywords != null && lststrImageKeywords == null) {			
				fTotalWeightMeta = m_fMetaWeight;
				fTotalWeightContent = m_fContentWeight + m_fImageWeight;
				fTotalWeightImage = 0;			
			} else if (lststrContentKeywords != null && lststrImageKeywords != null) {
				fTotalWeightMeta = m_fMetaWeight;
				fTotalWeightContent = m_fContentWeight;
				fTotalWeightImage = m_fImageWeight;				
			} else {
				fTotalWeightMeta = 1.0;
				fTotalWeightContent = 0;
				fTotalWeightImage = 0;
			}
		}
		
		System.out.println("===" + fTotalWeightMeta + "===" + fTotalWeightContent + "===" + fTotalWeightImage);
		
		//Adjust weight based on the portion of each part
		//if (lststrMetaKeywods != null) {
		if (bUseMeta) {
			for (i=0; i<lstfMetaWeights.size(); i++) {
				fWeight = lstfMetaWeights.get(i).doubleValue() * fTotalWeightMeta;
				lstfMetaWeights.set(i, fWeight);
			}
		}
		
		if (lststrContentKeywords != null) {
			for (i=0; i<lstfContentWeights.size(); i++) {
				fWeight = lstfContentWeights.get(i).doubleValue() * fTotalWeightContent;
				lstfContentWeights.set(i, fWeight);
			}
		}

		if (lststrImageKeywords != null) {
			for (i=0; i<lstfImageWeights.size(); i++) {
				fWeight = lstfImageWeights.get(i).doubleValue() * fTotalWeightImage;
				lstfImageWeights.set(i, fWeight);
			}
		}
		
		//Merge keyword list and weight
//        if (lststrMetaKeywods != null) {
        if (bUseMeta) {
        	for (i=0; i<lststrMetaKeywods.size(); i++) {
        		lststrMergedKeywords.add(lststrMetaKeywods.get(i));
        		lstfMergedWeights.add(lstfMetaWeights.get(i));
        	}
        }
		
        if (lststrContentKeywords != null) {
        	for (i=0; i<lststrContentKeywords.size(); i++) {
        		nPos = lststrMergedKeywords.indexOf(lststrContentKeywords.get(i));
        		if (nPos == -1) {  //Not exist
            		lststrMergedKeywords.add(lststrContentKeywords.get(i));
            		lstfMergedWeights.add(lstfContentWeights.get(i));        			
        		} else { //Exist, update weight
        			fWeight = lstfMergedWeights.get(nPos).doubleValue() + lstfContentWeights.get(i).doubleValue();
        			lstfMergedWeights.set(nPos, fWeight);
        		}
        	}
        }
		
        if (lststrImageKeywords != null) {
        	for (i=0; i<lststrImageKeywords.size(); i++) {
        		nPos = lststrMergedKeywords.indexOf(lststrImageKeywords.get(i));
        		if (nPos == -1) {  //Not exist
            		lststrMergedKeywords.add(lststrImageKeywords.get(i));
            		lstfMergedWeights.add(lstfImageWeights.get(i));        			
        		} else { //Exist, update weight
        			fWeight = lstfMergedWeights.get(nPos).doubleValue() + lstfImageWeights.get(i).doubleValue();
        			lstfMergedWeights.set(nPos, fWeight);
        		}
        	}        	
        }
        
        
        //Sort lststrMergedKeywords, lstfMergedWeights in DESC order of weight
        if (lststrMergedKeywords.size() > 0) {
        	lststrMergedSortedKeywords.add(lststrMergedKeywords.get(0));
        	lstfMergedSortedWeights.add(lstfMergedWeights.get(0));
        	
        	for (i=1; i<lststrMergedKeywords.size(); i++) {
        		fWeight = lstfMergedWeights.get(i).doubleValue();
        		
        		nInsertPos = -1;
        		
        		for (j=0; j<lstfMergedSortedWeights.size(); j++) {
        			fTmpWeight = lstfMergedSortedWeights.get(j).doubleValue();
        			
        			if (fWeight > fTmpWeight) {
        				nInsertPos = j;
        				break;
        			}
        		}
        		
        		if (nInsertPos == -1) {
        			lststrMergedSortedKeywords.add(lststrMergedKeywords.get(i));
        			lstfMergedSortedWeights.add(lstfMergedWeights.get(i));
        		} else {
        			lststrMergedSortedKeywords.add(nInsertPos, lststrMergedKeywords.get(i));
        			lstfMergedSortedWeights.add(nInsertPos, lstfMergedWeights.get(i));
        		}
        		
        	}
        	        	
        }
        
		
	}
	
		
	
	//Select only Top N keywords as final keywords and adjust their weights correspondingly
	private void SelectTopKeywords(List<String> lststrMergedSortedKeywords,List<Double> lstfMergedSortedWeights, List<String> lststrFinalKeywords, List<Double> lstfFinalWeights) {
		int i;
		double fTotalTopNWeight = 0.0;
		double fNewWeight;
		
		if (lststrMergedSortedKeywords == null || lststrMergedSortedKeywords.size() == 0) return;
		
		for (i=0; i<lststrMergedSortedKeywords.size(); i++) {
			lststrFinalKeywords.add(lststrMergedSortedKeywords.get(i));
			lstfFinalWeights.add(lstfMergedSortedWeights.get(i));
			fTotalTopNWeight = fTotalTopNWeight + lstfMergedSortedWeights.get(i).doubleValue();
			if (i >= m_nTopFinalKeywordNum -1) break;
		}
		
		//Adjust the weights for the Top N keywords again, to make their sum = 1.0
		for (i=0; i<lstfFinalWeights.size(); i++) {
			fNewWeight = Math.round(10000.0*lstfFinalWeights.get(i).doubleValue()/fTotalTopNWeight)/10000.0;
			lstfFinalWeights.set(i, fNewWeight);
		}
		
	}
	
	
	
	//This function process the webtext file and web image text file, and finally return one line, 
	//The final line will be: Place/Store name, Lat, Long, Keyword1, Weight1, Keyword2, Weight2...
	//These content will be saved in PlaceKeywordList_Lat_Long.csv file
	private String ProcessOnePlaceKeyword_Simple(String sWebTextFileName) {
		String sLine = "";
		String sWebTextFileNameFullPath = Utility.getWebTextFileNameFullPath(m_sParentDataFolder, sWebTextFileName);
		String sWebImageTextFileNameFullPath = Utility.getWebImageTextFileNameFullPath(m_sParentDataFolder, sWebTextFileName);
		
		FileReader frWebText = null;
		BufferedReader brWebText = null;
		FileReader frWebImageText = null;
		BufferedReader brWebImageText = null;

		String sContentLine = "";
		int nLineNum = 0;
		String sStoreName = "";
		String sGps = "";
		String sMetaKeywordList = "";
		String sContentKeywordList = "";
		
		String sTotalWebImageContentLine = "";
		
		int nColonPos;
		int i,j;
		
		//Process files under WebText\ folder
		try {
			frWebText = new FileReader(sWebTextFileNameFullPath);
			brWebText = new BufferedReader(frWebText);
			
			while((sContentLine = brWebText.readLine()) != null) {
				nLineNum = nLineNum + 1;
				nColonPos = sContentLine.indexOf(':'); 
				
				if (nLineNum == 1) {  //Store Name
					sStoreName = sContentLine.substring(nColonPos+1);
					
				} else if (nLineNum == 2) { //GPS
					sGps = sContentLine.substring(nColonPos+1);
					
				} else if (nLineNum == 3) { //Metakeyword
					sMetaKeywordList = sContentLine.substring(nColonPos+1);
					
				} else if (nLineNum == 4) {  //Top N web content keywords
					sContentKeywordList = sContentLine.substring(nColonPos+1);	  //This part already contains Meta keywords				
				}
				
			}
			
			frWebText.close();
		} catch (Exception e) {
			
		}
		
		sLine = sStoreName + "," + sGps + "," + sContentKeywordList;

		return sLine;
	}
	
	
	//This function process all the web related text of one place
	//The source text are in \WebText, in which, each file contains "M:" field, which lists the Meta Text, "C" field, which lists the Content Text, frequency/weight has not been calculated
	//The source text also comes from WebImageText\, in which, each file contains the text OCRed from web images in \WebImage
	//This function generates two types of information:
	//		Full text in  \WebText_Full
	//PlaceKeywordList_xxx.csv
	private String ProcessOnePlaceKeyword_AllWebText(String sWebTextFileName) {
		String sWebTextFileNameFullPath = Utility.getWebTextFileNameFullPath(m_sParentDataFolder, sWebTextFileName);
		String sWebImageTextFileNameFullPath = Utility.getWebImageTextFileNameFullPath(m_sParentDataFolder, sWebTextFileName);

		String sFullWebTextFileNameFullPath = Utility.getFullWebTextFileNameFullPath(m_sParentDataFolder, sWebTextFileName);
		
		FileReader frWebText = null;
		BufferedReader brWebText = null;
		FileReader frWebImageText = null;
		BufferedReader brWebImageText = null;
		
		FileWriter fwFullWebText = null; 

		List<Integer> lstFreqIdx = new ArrayList<Integer>();   //This list contains the index of words in lstWords, whose frequency is in DESC order
		boolean bFind = false;
		
		String sLine = "";
		String sFullWebTextLine = "";
		
		String sContentLine = "";
		int nLineNum = 0;
		String sStoreName = "";
		String sGps = "";
		
		String sMetaText = "";
		String sContentText = "";
		
		String sTotalWebImageContentLine = "";
		
		String sTotalWebText = "";
		
		int nColonPos;
		int i,j;
		
		
		//Process files under WebText\ folder
		try {
			frWebText = new FileReader(sWebTextFileNameFullPath);
			brWebText = new BufferedReader(frWebText);
			
			while((sContentLine = brWebText.readLine()) != null) {
				nLineNum = nLineNum + 1;
				nColonPos = sContentLine.indexOf(':'); 
				
				if (nLineNum == 1) {  //Store Name
					sStoreName = sContentLine.substring(nColonPos+1);
					
				} else if (nLineNum == 2) { //GPS
					sGps = sContentLine.substring(nColonPos+1);
					
				} else if (nLineNum == 3) { //Meta text
					sMetaText = sContentLine.substring(nColonPos+1);					
				} else if (nLineNum == 4) {  //Top web content text
					sContentText = sContentLine.substring(nColonPos+1);
				}
				
			}
			
			frWebText.close();
		} catch (Exception e) {
			
		}

		
		if (Utility.USE_WEB_IMAGE_TEXT) {  // Use Web Image Text or not
			//Process files under WebImageText\ folder
			//The content in these files are raw OCRed data from in-store pictures
			try {
				
				File flImageText = new File(sWebImageTextFileNameFullPath);
				
				if (flImageText.exists()) {
					
					System.out.println("[DEBUG 3]===Processing Web Image Text File: " + sWebImageTextFileNameFullPath);
					
					frWebImageText = new FileReader(sWebImageTextFileNameFullPath);
					brWebImageText = new BufferedReader(frWebImageText);
					
					while ((sContentLine = brWebImageText.readLine()) != null) {
						sTotalWebImageContentLine = sTotalWebImageContentLine + sContentLine + ",";
					}
				
					frWebImageText.close();
				}
				
			} catch (Exception e) {
				
			}
						
		}
		
		
		sTotalWebText = sMetaText + "," + sContentText + "," + sTotalWebImageContentLine;
		
		
		List<String> lstWords = new ArrayList<String>();
		List<Integer> lstWordCount = new ArrayList<Integer>();
		KeywordExtraction locKeywordExtraction = new KeywordExtraction();
		
		List<String> lststrFinalKeywords = new ArrayList<String>();
		lststrFinalKeywords = locKeywordExtraction.extractKeywordFromContents(sTotalWebText);
		
		if (lststrFinalKeywords.size() == 0) {
			return sLine;
		}
		
		int nTotalWordCount = 0;
		
/*	If only need to process single words, not noun phrases, then use this code	*/
//		System.out.println(sContent);
		String sArrWord[] = Utility.splitString(sTotalWebText);
		
		//Calculate word frequency
		for (i = 0; i<sArrWord.length; i++) {
			String sTmpWord = sArrWord[i].toLowerCase();
			
			if (lststrFinalKeywords.contains(sTmpWord)) {
				if (lstWords.contains(sTmpWord)) {  // Exist, increase the count
					int nIndex = lstWords.indexOf(sTmpWord);
					int nCurCount = lstWordCount.get(nIndex).intValue();
					lstWordCount.set(nIndex, Integer.valueOf(nCurCount + 1));
				} else {  // Not exist, insert it
					lstWords.add(sTmpWord);
					lstWordCount.add(Integer.valueOf(1));
				}
			}
		}
		
		//Calculate the order of frequency of words and its corresponding index (desc order)
		lstFreqIdx.add(Integer.valueOf(0));
		
		nTotalWordCount = lstWordCount.get(0).intValue();
		
		for (int k=1; k<lstWordCount.size(); k++) {
			int nCurFreq = lstWordCount.get(k).intValue();
			
			nTotalWordCount = nTotalWordCount + nCurFreq;
			
			bFind = false;
			for (int kk=0; kk<lstFreqIdx.size(); kk++) {  //Look for which position to insert
				int nIndex = lstFreqIdx.get(kk).intValue();
				int nFreq = lstWordCount.get(nIndex).intValue();
				if (nCurFreq > nFreq) {
					lstFreqIdx.add(kk, Integer.valueOf(k));
					bFind = true;
					break;
				}
			}
			
			if (bFind == false) {   //Should add to the end
				lstFreqIdx.add(Integer.valueOf(k));
			}
		}

		
		List<String> lstTopFreqWords = new ArrayList<String>();
		List<Integer> lstTopFreqCount = new ArrayList<Integer>();
		List<Double> lstWeights = new ArrayList<Double>();  //This stores the normalized weights of the words
		
		for (i=0; i<lstFreqIdx.size(); i++) {
			int nIndex = lstFreqIdx.get(i).intValue();
			lstTopFreqWords.add(lstWords.get(nIndex));
			lstTopFreqCount.add(lstWordCount.get(nIndex));
		}
	
    	int nTotalFrequencies = 0;
    	
    	for (int k=0; k<lstTopFreqCount.size(); k++) {
    		nTotalFrequencies = nTotalFrequencies + lstTopFreqCount.get(k);
    	}

		try {
			fwFullWebText = new FileWriter(sFullWebTextFileNameFullPath, false);   //Overwrite
  	
	    	sLine = sStoreName + "," + sGps;
	
	    	sFullWebTextLine = Utility.FULL_WEBTEXT_LINE_PRE_PLACENAME + sStoreName + "\n";
	    	fwFullWebText.write(sFullWebTextLine);
	    	
	    	sFullWebTextLine = Utility.FULL_WEBTEXT_LINE_PRE_GPS + sGps + "\n";
	    	fwFullWebText.write(sFullWebTextLine);
	    	
	    	sFullWebTextLine =  Utility.FULL_WEBTEXT_LINE_PRE_META + sMetaText + "\n";
	    	fwFullWebText.write(sFullWebTextLine);
	    	
	    	sFullWebTextLine = "\n" + Utility.FULL_WEBTEXT_LINE_PRE_CONTENT + "\n";
	    	fwFullWebText.write(sFullWebTextLine);
	    	
	    	int nMaxCount = Utility.getMaxInt(lstTopFreqCount);
	    	double fWeight = 0.0;
    	  	
	    	//Calculate words frequency
	    	for (int kk=0; kk<lstTopFreqCount.size(); kk++) {
	    		if (Utility.USE_TF_IDF_WEIGHT) {
	    			fWeight = 0.5 + 0.5*lstTopFreqCount.get(kk).intValue()/nMaxCount;
	    			fWeight = Math.round(fWeight*10000.0)/10000.0;
	    		} else {
	    			fWeight = Math.round((lstTopFreqCount.get(kk).intValue()*1.0/nTotalFrequencies)*10000)/10000.0;
	    		}
	    	
	    		sLine = sLine + "," + lstTopFreqWords.get(kk) + "," + fWeight;
	    		
	    		sFullWebTextLine = Utility.getStringWithRequiredLen(lstTopFreqWords.get(kk), 25) + ", " + Utility.getStringWithRequiredLen(Integer.valueOf(lstTopFreqCount.get(kk)).toString(), 10) + ", " + fWeight + "\n";
    		
	    		fwFullWebText.write(sFullWebTextLine);
	    		
	    		lstWeights.add(Double.valueOf(fWeight));
	    	}
		
	    	fwFullWebText.close();
		} catch (Exception e) {
			
		}
		
		return sLine;
	}
	
	
}
