package com.autolabel;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class RepresentativeKeywordExtraction {

	private PageAnalyzer m_PageAnalyzer = new PageAnalyzer();
	
	public RepresentativeKeywordExtraction() {
		// TODO Auto-generated constructor stub
	}

	public void ExtractRepresentativeKeyword(String sPlaceName,  String sURL, int nTopType, int nTop, FileWriter fwWebpageTopWords) {
		
		try {
			String sLine = "";
			
			List<String> lstTopFreqWords = new ArrayList<String>();   //This store the top 10% frequent word
			List<Integer> lstTopFreqCount = new ArrayList<Integer>();
			List<Double> lstWeights = new ArrayList<Double>();  //This stores the normalized weights of the words
    		List<String> lstMetaKeywords = new ArrayList<String>();
			
			System.out.println("Processing..." + sURL);
//	    	m_PageAnalyzer.analyzeWebpage(sURL, nTopType, nTop, lstTopFreqWords, lstTopFreqCount);
	    	m_PageAnalyzer.analyzeWebpage(sURL, nTopType, nTop, lstTopFreqWords, lstTopFreqCount, lstMetaKeywords);
	    	
	    	//Calculate the weight of each words based on the frequency
	    	//For example, three words: AA, BB, CC,    the frequencies are 100, 50, 50, then the weights are 0.5, 0.25, 0.25
	    	int nTotalFrequencies = 0;
	    	
	    	for (int k=0; k<lstTopFreqCount.size(); k++) {
	    		nTotalFrequencies = nTotalFrequencies + lstTopFreqCount.get(k);
	    	}
	    	        	
	    	for (int kk=0; kk<lstTopFreqCount.size(); kk++) {
	    		double fWeight = Math.round((lstTopFreqCount.get(kk).intValue()*1.0/nTotalFrequencies)*10000)/10000.0;
	    		lstWeights.add(Double.valueOf(fWeight));
	    	}
	    			    	
	//        	sLine = m_arrsWebURLs[i] + "\n";
	//        	fwWebpageTopWords.write(sLine);
	    	sLine = sPlaceName;
	    	
	    	for (int j=0; j<lstTopFreqWords.size(); j++) {
	    		//sLine = lstTopFreqCount.get(j) + " , " + lstTopFreqWords.get(j) + "\n";
	    		sLine = sLine + "," + lstTopFreqWords.get(j) + "," + lstWeights.get(j).doubleValue();
	    		//fwWebpageTopWords.write(sLine);
	    	}
	    	
	    	sLine = sLine + "\n";
	    	fwWebpageTopWords.write(sLine);
	
			//Show the top 10% frequent words
		//	System.out.println("Top frequent words:");
			for (int ii=0; ii<lstTopFreqWords.size(); ii++) {
		//		System.out.println(lstTopFreqCount.get(ii) + " : " + lstTopFreqWords.get(ii));
			}
		//	System.out.println("--------------------------------------");
	       				
	        
		} catch (Exception e) {
			
		}
				
	}

	
	//This function is used to extract web text, 
	//This function does not consider Text from Web Image later
	public void ExtractRepresentativeKeyword_NonWebImage(String sPlaceName,  String sURL, int nTopKeywordNum, String sSaveFolder, String sGpsLocation) {
		int i;
		
		String sLine = "";
		String sLine_Temp = "";
		
		double fWeight = 0.0;
		
		List<String> lstTopFreqWords = new ArrayList<String>();   //This store the top 10% frequent word
		List<Integer> lstTopFreqCount = new ArrayList<Integer>();
		List<Double> lstWeights = new ArrayList<Double>();  //This stores the normalized weights of the words
		//List<String> lstMetaKeywords = new ArrayList<String>();
		String sMetaKeywords = "";
		
		System.out.println("Processing..." + sURL);
//	    	m_PageAnalyzer.analyzeWebpage(sURL, nTopType, nTop, lstTopFreqWords, lstTopFreqCount);
///    	m_PageAnalyzer.analyzeWebpage(sURL, nTopKeywordNum, lstTopFreqWords, lstTopFreqCount, lstMetaKeywords);
		sMetaKeywords = m_PageAnalyzer.analyzeWebpage_ForMenu(sURL, nTopKeywordNum, lstTopFreqWords, lstTopFreqCount);   //For merging meta keywords with content keywords
    	
    	//Calculate the weight of each words based on the frequency
    	//For example, three words: AA, BB, CC,    the frequencies are 100, 50, 50, then the weights are 0.5, 0.25, 0.25
    	int nTotalFrequencies = 0;
    	
    	for (int k=0; k<lstTopFreqCount.size(); k++) {
    		nTotalFrequencies = nTotalFrequencies + lstTopFreqCount.get(k);
    	}
    	
    	int nMaxCount = Utility.getMaxInt(lstTopFreqCount);
    	
    	//Calculate words frequency
    	for (int kk=0; kk<lstTopFreqCount.size(); kk++) {
    		if (Utility.USE_TF_IDF_WEIGHT) {
    			fWeight = 0.5 + 0.5*lstTopFreqCount.get(kk).intValue()/nMaxCount;
    			fWeight = Math.round(fWeight*10000.0)/10000.0;
    		} else {
    			fWeight = Math.round((lstTopFreqCount.get(kk).intValue()*1.0/nTotalFrequencies)*10000)/10000.0;
    		}
    	
    		lstWeights.add(Double.valueOf(fWeight));
    	}
    	
    	//Save Metakeyword and content keyword information into file into WebText folder
    	//Filename will be like: Bestbuy_Web_Text.csv
    	//Content will be:
    	//N: Store name
    	//G: GPS coordinates (Lat, Long)
    	//M: Meta keyword list
    	//C: Web Content Keywords
    	//This file will later be processed with WebImage Text file to form the final keyword/weight information for the place
		FileWriter fwWebText = null;
		FileWriter fwWebText_Temp = null;
		String sWebTextFile = Utility.getWebTextFilePathName(sSaveFolder, sPlaceName);
		
		String sWebTextFile_Temp = Utility.getWebTextFilePathName_Full(sSaveFolder, sPlaceName);
		
		try {
			fwWebText = new FileWriter(sWebTextFile, false);   //Overwrite
			fwWebText_Temp = new FileWriter(sWebTextFile_Temp, false);   //Overwrite

			sLine = "N:" + sPlaceName + "\n";
			fwWebText.write(sLine);
						
			sLine_Temp = Utility.FULL_WEBTEXT_LINE_PRE_PLACENAME + sPlaceName + "\n";
			fwWebText_Temp.write(sLine_Temp);
			
			sLine = "G:" + sGpsLocation + "\n";
			fwWebText.write(sLine);
			
			sLine_Temp = Utility.FULL_WEBTEXT_LINE_PRE_GPS + sGpsLocation + "\n";
			fwWebText_Temp.write(sLine_Temp);
			
			sLine = "M:";
			sLine_Temp = Utility.FULL_WEBTEXT_LINE_PRE_META;

			sLine = sLine + sMetaKeywords;
			sLine_Temp = sLine_Temp + sMetaKeywords;
			
//			for (i=0; i<lstMetaKeywords.size(); i++) {
//				if (i == 0) {
//					sLine = sLine + lstMetaKeywords.get(i);
//					sLine_Temp = sLine_Temp + lstMetaKeywords.get(i);
//				} else {
//					sLine = sLine + "," + lstMetaKeywords.get(i);
//					sLine_Temp = sLine_Temp + "," + lstMetaKeywords.get(i);
//				}
//			}
			
			sLine = sLine + "\n";
			sLine_Temp = sLine_Temp + "\n";
			
			fwWebText.write(sLine);
			fwWebText_Temp.write(sLine_Temp);
			
			sLine = "C:";
			sLine_Temp = "\n" + Utility.FULL_WEBTEXT_LINE_PRE_CONTENT + "\n";
			
			fwWebText_Temp.write(sLine_Temp);
			
			sLine_Temp = "";
			
			for (i=0; i<lstTopFreqWords.size(); i++) {
				if (i == 0) {
					sLine = sLine + lstTopFreqWords.get(i) + "," + lstWeights.get(i);
				} else {
					sLine = sLine + "," + lstTopFreqWords.get(i) + "," + lstWeights.get(i);
				}
				
				sLine_Temp = Utility.getStringWithRequiredLen(lstTopFreqWords.get(i), 20) + ", " + Utility.getStringWithRequiredLen(Integer.valueOf(lstTopFreqCount.get(i)).toString(), 5) + ", " + lstWeights.get(i) + "\n";
				fwWebText_Temp.write(sLine_Temp);
			}
			
			sLine = sLine + "\n"; 
			fwWebText.write(sLine);
			
			fwWebText.close();
			fwWebText_Temp.close();
		} catch (Exception e) {
			
		}
	    				
	}
	

	//For Full Text: Meta and Content are NOT merged here
	//
	//This function is used to extract web text, 
	//This function considers Text from Web Image later
	//
//	public void ExtractRepresentativeKeyword_WithWebImage(String sPlaceName,  String sURL, int nTopKeywordNum, String sSaveFolder, String sGpsLocation) {
	public void ExtractRepresentativeKeyword_WithWebImage(String sPlaceName,  String sURL, String sSaveFolder, String sGpsLocation) {

		int i;
		
		String sLine = "";
		String sLine_Full = "";
		
		double fWeight = 0.0;
		
//		List<String> lstTopFreqWords = new ArrayList<String>();   //This store the top 10% frequent word
//		List<Integer> lstTopFreqCount = new ArrayList<Integer>();
//		List<Double> lstWeights = new ArrayList<Double>();  //This stores the normalized weights of the words
		//List<String> lstMetaKeywords = new ArrayList<String>();
		String sMetaKeywords = "";
		String sContent = "";
		
		List<String> lstTextContent = new ArrayList<String>();
		
		System.out.println("Processing..." + sURL);
//	    	m_PageAnalyzer.analyzeWebpage(sURL, nTopType, nTop, lstTopFreqWords, lstTopFreqCount);
///    	m_PageAnalyzer.analyzeWebpage(sURL, nTopKeywordNum, lstTopFreqWords, lstTopFreqCount, lstMetaKeywords);
//		sMetaKeywords = m_PageAnalyzer.analyzeWebpage_ForMenu(sURL, nTopKeywordNum, lstTopFreqWords, lstTopFreqCount);   
		m_PageAnalyzer.analyzeWebpage_TextContent(sURL, lstTextContent);  //For merging meta keywords with content keywords
		
		sMetaKeywords = lstTextContent.get(0);
		sContent = lstTextContent.get(1);
		    	
    	//Save Metakeyword and content keyword information into file into WebText folder
    	//Filename will be like: Bestbuy_Web_Text.csv
    	//Content will be:
    	//N: Store name
    	//G: GPS coordinates (Lat, Long)
    	//M: Meta keyword list
    	//C: Web Content Keywords
    	//This file will later be processed with WebImage Text file to form the final keyword/weight information for the place
		FileWriter fwWebText = null;
		//FileWriter fwWebText_Full = null;
		String sWebTextFile = Utility.getWebTextFilePathName(sSaveFolder, sPlaceName);
		
		//String sWebTextFile_Full = Utility.getWebTextFilePathName_Full(sSaveFolder, sPlaceName);
		
		try {
			fwWebText = new FileWriter(sWebTextFile, false);   //Overwrite
			//fwWebText_Full = new FileWriter(sWebTextFile_Full, false);   //Overwrite

			sLine = "N:" + sPlaceName + "\n";
			fwWebText.write(sLine);
						
			//sLine_Full = Utility.FULL_WEBTEXT_LINE_PRE_PLACENAME + sPlaceName + "\n";
			//fwWebText_Full.write(sLine_Full);
			
			sLine = "G:" + sGpsLocation + "\n";
			fwWebText.write(sLine);
			
			//sLine_Full = Utility.FULL_WEBTEXT_LINE_PRE_GPS + sGpsLocation + "\n";
			//fwWebText_Full.write(sLine_Full);
			
			sLine = "M:" + sMetaKeywords  + "\n";
			fwWebText.write(sLine);
			//sLine_Full = Utility.FULL_WEBTEXT_LINE_PRE_META;
			//sLine_Full = Utility.FULL_WEBTEXT_LINE_PRE_META_RAW_TEXT;

			//sLine_Full = sLine_Full + sMetaKeywords;
			//sLine_Full = sLine_Full + "\n";
			
			
			//fwWebText_Full.write(sLine_Full);
			
			sLine = "C:" + sContent + "\n"; 
			fwWebText.write(sLine);
			//sLine_Full = "\n" + Utility.FULL_WEBTEXT_LINE_PRE_CONTENT + "\n";
			//sLine_Full = "\n" + Utility.FULL_WEBTEXT_LINE_PRE_CONTENT_RAW_TEXT + "\n";
			
			//fwWebText_Full.write(sLine_Full);			
			
			
			fwWebText.close();
			//fwWebText_Full.close();
		} catch (Exception e) {
			
		}
	    				
	}
	
	
}
