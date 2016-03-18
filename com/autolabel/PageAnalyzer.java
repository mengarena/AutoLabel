package com.autolabel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class PageAnalyzer {
	private KeywordExtraction m_KeywordExtraction;
	
	private static String[] m_strarrBlackListMetaKeywords = {
		"online", "com"
	};
	
	public PageAnalyzer() {
		// TODO Auto-generated constructor stub
		m_KeywordExtraction = new KeywordExtraction();
	}

	
	//This is old version, not using.
//	public void analyzeWebpage(String strURL, int nTopType, int nTop, List<String> lstTopFreqWords, List<Integer> lstTopFreqCount) {
	public void analyzeWebpage(String strURL, int nTopType, int nTop, List<String> lstTopFreqWords, List<Integer> lstTopFreqCount, List<String> lstMetaKeywords) {
		
		List<String> lstWords = new ArrayList<String>();
		List<Integer> lstWordCount = new ArrayList<Integer>();
				
		List<Integer> lstFreqIdx = new ArrayList<Integer>();   //This list contains the index of words in lstWords, whose frquency is in DESC order
		boolean bFind = false;
		
		String sPageContent = getWebPageContent(strURL);

		String sContent = Jsoup.parse(sPageContent).text();
				
		//Meta Keyword Extraction Begin
		//Process keywords defined in html <meta content="......" name="keywords">
		Document doc = Jsoup.parse(sPageContent);
		Elements metalinks = doc.select("meta[name=keywords]");
		if (metalinks.size() > 0) {
			String metatagcontent = metalinks.first().attr("content");
		
			if (metatagcontent.length() > 0) {
				//Split metatagcontent with "," and " " into words
				//String sMetaKeywords[] = splitMetaContent(metatagcontent);
				String sMetaKeywords[] = Utility.splitString(metatagcontent);
				
				
				
				for (int i=0; i<sMetaKeywords.length; i++) {
				//	System.out.println("--->> " + sMetaKeywords[i]);
					if (!lstMetaKeywords.contains(sMetaKeywords[i].toLowerCase())) {
						bFind = false;
						//Check black list for meta keywords
						for (int j=0; j<m_strarrBlackListMetaKeywords.length; j++) {
							if (m_strarrBlackListMetaKeywords[j].toLowerCase().compareTo(sMetaKeywords[i].toLowerCase())==0) {
								bFind = true;
								break;
							}
						}
						
						if (bFind == false) {
							lstMetaKeywords.add(sMetaKeywords[i].toLowerCase());
						}
					}
				}
			}
		}
		
		//Meta Keyword Extraction End
		
		// Begin For sContent, go through the same process as in "RecognizedTextAnalyzer" to get the proper names and nouns
		// If only need to process single words, then DO NOT use the two lines:
		List<String> lststrFinalKeywords = new ArrayList<String>();
		lststrFinalKeywords = m_KeywordExtraction.extractKeywordFromContents(sContent);
		
		int nTotalWordCount = 0;
		
/*	If only need to process single words, not noun phrases, then use this code	*/
//		System.out.println(sContent);
		String sArrWord[] = Utility.splitString(sContent);
		
		//Calculate word frequency
		for (int i = 0; i<sArrWord.length; i++) {
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
/* */

/*		
		//If only need to process Single words, then DO NOT use this "for"
		for (String sTmpWord : lststrFinalKeywords) {
			if (lstWords.contains(sTmpWord)) {  //Exist, increase the count
				int nIndex = lstWords.indexOf(sTmpWord);
				int nCurCount = lstWordCount.get(nIndex).intValue();
				lstWordCount.set(nIndex, Integer.valueOf(nCurCount + 1));
			} else {  // Not exist, insert it
				lstWords.add(sTmpWord);
				lstWordCount.add(Integer.valueOf(1));
			}			
			
		}
*/				
		//for (int j=0; j<lstWords.size(); j++) {
		//	System.out.println(lstWordCount.get(j).intValue() + " :: " + lstWords.get(j));
		//}

		//System.out.println("---------------------------------------");
		
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
		
		//Print the words in DESC order of frequency
		//for (int i=0; i<lstFreqIdx.size(); i++) {
		//	int nIndex = lstFreqIdx.get(i).intValue();
			
		//	System.out.println(lstWordCount.get(nIndex).intValue() + " :: " + lstWords.get(nIndex));
		//}
	
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		//This code get the top 10% frequent words
		//Get the top 10% frequent words
		if (nTopType == 2) {   // Use percentage (top 10%)		
			int nTopCount = (int) Math.round(nTotalWordCount * 1.0 * nTop / 100); 
			int nTmpCount = 0;
			
			for (int i=0; i<lstFreqIdx.size(); i++) {
				int nIndex = lstFreqIdx.get(i).intValue();
				nTmpCount = nTmpCount + lstWordCount.get(nIndex).intValue();
				if (nTmpCount > nTopCount) {
					if (lstTopFreqWords.size() == 0) {
						lstTopFreqWords.add(lstWords.get(nIndex));
						lstTopFreqCount.add(lstWordCount.get(nIndex));
					}
					//Not add
					break;
				} else {
					lstTopFreqWords.add(lstWords.get(nIndex));
					lstTopFreqCount.add(lstWordCount.get(nIndex));
				}
			}
		
		} else {	// Use number (e.g. top 10)			
	
			////////////////////////////////////////////////////////////////////////////////////////////////////
			//This code get the top 10 frequent words
			for (int i=0; i<lstFreqIdx.size(); i++) {
				int nIndex = lstFreqIdx.get(i).intValue();
				lstTopFreqWords.add(lstWords.get(nIndex));
				lstTopFreqCount.add(lstWordCount.get(nIndex));
				if (i >= nTop -1) break;
			}
		}		
	}
	
			
    //Get the content of the webpage for the given URL
    private static String getWebPageContent(String strURL) {
		URL url;
	    HttpURLConnection connection=null;  
	    String sSearchResult = "";
//	    String sPageBodyTag = "<body";   //Only get the text in body part (webpage title is not used)
//	    int nPosBodyStart;
	    
	    System.out.println("------getWebPageContent----1-----");
	    try{
		    url=new URL(strURL);
		    connection= (HttpURLConnection) url.openConnection();
		    connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.52 Safari/536.5");

		    connection.connect();	
		    System.out.println("------getWebPageContent----2-----");
		    InputStreamReader reader=new InputStreamReader(connection.getInputStream());
		    BufferedReader in=new BufferedReader(reader);
		    System.out.println("------getWebPageContent----3-----");
	        String line;
	        StringBuffer response=new StringBuffer(); 
	        
	        while((line=in.readLine())!=null) {
        		response.append(line);
        		//response.append("\n");
        	}
	        
	        System.out.println("------getWebPageContent----4-----");
	        in.close();

		    sSearchResult = response.toString();
//		    nPosBodyStart = sSearchResult.indexOf(sPageBodyTag);
//		    sSearchResult = sSearchResult.substring(nPosBodyStart);
//		    System.out.println(sSearchResult);
	      
	    } catch (Exception e) {
	    	//e.printStackTrace();
	    	return "";
	    }
    	
	    return sSearchResult;
    }
	
    
    //From the webpage, extract meta keywords and content keywords
    //For content keywords, the word frequency is calculated
    //For meta keywords, duplicate words are removed (every word has only one version)
	public void analyzeWebpage(String strURL, int nTopNum, List<String> lstTopFreqWords, List<Integer> lstTopFreqCount, List<String> lstMetaKeywords) {
		
		List<String> lstWords = new ArrayList<String>();
		List<Integer> lstWordCount = new ArrayList<Integer>();
				
		List<Integer> lstFreqIdx = new ArrayList<Integer>();   //This list contains the index of words in lstWords, whose frquency is in DESC order
		boolean bFind = false;
		
		String sPageContent = getWebPageContent(strURL);

		String sContent = Jsoup.parse(sPageContent).text();
				
		//Meta Keyword Extraction Begin////////////////////
		//Process keywords defined in html <meta content="......" name="keywords">
		Document doc = Jsoup.parse(sPageContent);
		Elements metalinks = doc.select("meta[name=keywords]");
		if (metalinks.size() > 0) {
			String metatagcontent = metalinks.first().attr("content");
		
			if (metatagcontent.length() > 0) {
				//Split metatagcontent with "," and " " into words
				//Changed on 2014/10/05 String sMetaKeywords[] = splitMetaContent(metatagcontent);
				//Commented 2014/10/15 String sMetaKeywords[] = splitString(metatagcontent);   //Walmart.com will be separeated
				
				//////////////////////////Do we need to do noun/proper name extraction on MetaKeyword?
				
				List<String> lstTmpMetaKeywords = new ArrayList<String>();
				
				lstTmpMetaKeywords = m_KeywordExtraction.extractKeywordFromContents(metatagcontent);
				
				for (int i=0; i<lstTmpMetaKeywords.size(); i++) {

					if (!lstMetaKeywords.contains(lstTmpMetaKeywords.get(i).toLowerCase())) {
						bFind = false;
						//Check black list for meta keywords
						for (int j=0; j<m_strarrBlackListMetaKeywords.length; j++) {
							if (m_strarrBlackListMetaKeywords[j].compareToIgnoreCase(lstTmpMetaKeywords.get(i))==0) {
								bFind = true;
								break;
							}
						}
						
						if (bFind == false) {
							lstMetaKeywords.add(lstTmpMetaKeywords.get(i).toLowerCase());
						}
					}
				}
			}
		}
		
		//Meta Keyword Extraction End/////////////////////
		
		// Begin For sContent, go through the same process as in "RecognizedTextAnalyzer" to get the proper names and nouns
		// If only need to process single words, then DO NOT use the two lines:
		List<String> lststrFinalKeywords = new ArrayList<String>();
		lststrFinalKeywords = m_KeywordExtraction.extractKeywordFromContents(sContent);
		
		int nTotalWordCount = 0;
		
/*	If only need to process single words, not noun phrases, then use this code	*/
//		System.out.println(sContent);
		String sArrWord[] = Utility.splitString(sContent);
		
		if (sArrWord == null) return;
		
		//Calculate word frequency
		for (int i = 0; i<sArrWord.length; i++) {
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
/* */

/*		
		//If only need to process Single words, then DO NOT use this "for"
		for (String sTmpWord : lststrFinalKeywords) {
			if (lstWords.contains(sTmpWord)) {  //Exist, increase the count
				int nIndex = lstWords.indexOf(sTmpWord);
				int nCurCount = lstWordCount.get(nIndex).intValue();
				lstWordCount.set(nIndex, Integer.valueOf(nCurCount + 1));
			} else {  // Not exist, insert it
				lstWords.add(sTmpWord);
				lstWordCount.add(Integer.valueOf(1));
			}			
			
		}
*/				
		//for (int j=0; j<lstWords.size(); j++) {
		//	System.out.println(lstWordCount.get(j).intValue() + " :: " + lstWords.get(j));
		//}

		//System.out.println("---------------------------------------");
		
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
		
		//Print the words in DESC order of frequency
		//for (int i=0; i<lstFreqIdx.size(); i++) {
		//	int nIndex = lstFreqIdx.get(i).intValue();
			
		//	System.out.println(lstWordCount.get(nIndex).intValue() + " :: " + lstWords.get(nIndex));
		//}
	
		///////////////////////////////////////////////////////////////////////////////////////////////////////
	
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//This code get the top 10 frequent words
		for (int i=0; i<lstFreqIdx.size(); i++) {
			int nIndex = lstFreqIdx.get(i).intValue();
			lstTopFreqWords.add(lstWords.get(nIndex));
			lstTopFreqCount.add(lstWordCount.get(nIndex));
			if (i >= nTopNum -1) break;
		}
	}
    

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
    //From the webpage, extract meta keywords and menu items
    //For menu items keywords, the word frequency is calculated
    //For meta keywords, duplicate words are removed (every word has only one version)
	//
	public void analyzeWebpage_ForMenu(String strURL, int nTopNum, List<String> lstTopFreqWords, List<Integer> lstTopFreqCount, List<String> lstMetaKeywords) {
		
		List<String> lstWords = new ArrayList<String>();
		List<Integer> lstWordCount = new ArrayList<Integer>();
				
		String sMetatagcontent = "";
		List<Integer> lstFreqIdx = new ArrayList<Integer>();   //This list contains the index of words in lstWords, whose frquency is in DESC order
		boolean bFind = false;
		
		System.out.println("----------1---------------");
		
		String sPageContent = getWebPageContent(strURL);
		System.out.println("----------2---------------");		
		//Meta Keyword Extraction Begin////////////////////
		//Process keywords defined in html <meta content="......" name="keywords">
		Document doc = Jsoup.parse(sPageContent);
		Elements metalinks = doc.select("meta[name=keywords]");
		if (metalinks.size() > 0) {
			sMetatagcontent = metalinks.first().attr("content");
		
			if (sMetatagcontent.length() > 0) {
				//Split metatagcontent with "," and " " into words
				//Changed on 2014/10/05 String sMetaKeywords[] = splitMetaContent(metatagcontent);
				//Commented 2014/10/15 String sMetaKeywords[] = splitString(metatagcontent);   //Walmart.com will be separeated
				
				//////////////////////////Do we need to do noun/proper name extraction on MetaKeyword?
				
				
				List<String> lstTmpMetaKeywords = new ArrayList<String>();
				
				lstTmpMetaKeywords = m_KeywordExtraction.extractKeywordFromContents(sMetatagcontent);
				
				for (int i=0; i<lstTmpMetaKeywords.size(); i++) {

					if (!lstMetaKeywords.contains(lstTmpMetaKeywords.get(i).toLowerCase())) {
						bFind = false;
						//Check black list for meta keywords
						for (int j=0; j<m_strarrBlackListMetaKeywords.length; j++) {
							if (m_strarrBlackListMetaKeywords[j].compareToIgnoreCase(lstTmpMetaKeywords.get(i))==0) {
								bFind = true;
								break;
							}
						}
						
						if (bFind == false) {
							lstMetaKeywords.add(lstTmpMetaKeywords.get(i).toLowerCase());
						}
					}
				}
			}
		}
		
		//Meta Keyword Extraction End/////////////////////
		System.out.println("----------3---------------");
		// Begin For sContent, go through the same process as in "RecognizedTextAnalyzer" to get the proper names and nouns
		// If only need to process single words, then DO NOT use the two lines:
		//Originally all content on webpage:  String sContent = Jsoup.parse(sPageContent).text();
		
		//List<String> lstWebMean = WebMenuExtractor.extract_AllMenuItem(strURL);
		//List<String> lstWebMean = WebMenuExtractor.extract_NavMenuItem_RemoveFooter(strURL);
		List<String> lstWebMean = WebMenuExtractor.extract_NavMenuItem_RemoveFooter_Enhanced(strURL);
		System.out.println("----------4---------------");
		
		if (lstWebMean == null || lstWebMean.size() == 0) return;
				
		String sContent = "";
		
		//### Merge Meta keywords with Web Menu Items 2014/10/30 ###//
		sContent = sMetatagcontent;
		
		for (String sMenu:lstWebMean) {
			sContent = sContent + sMenu + ",";
		}
		
		System.out.println("----------4---1------------");
		List<String> lststrFinalKeywords = new ArrayList<String>();
		lststrFinalKeywords = m_KeywordExtraction.extractKeywordFromContents(sContent);
		System.out.println("----------5---------------");
		int nTotalWordCount = 0;
		
/*	If only need to process single words, not noun phrases, then use this code	*/
//		System.out.println(sContent);
		String sArrWord[] = Utility.splitString(sContent);
		
		//Calculate word frequency
		for (int i = 0; i<sArrWord.length; i++) {
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
		
		//Print the words in DESC order of frequency
		//for (int i=0; i<lstFreqIdx.size(); i++) {
		//	int nIndex = lstFreqIdx.get(i).intValue();
			
		//	System.out.println(lstWordCount.get(nIndex).intValue() + " :: " + lstWords.get(nIndex));
		//}
	
		///////////////////////////////////////////////////////////////////////////////////////////////////////
	
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//This code get the top 10 frequent words
		for (int i=0; i<lstFreqIdx.size(); i++) {
			int nIndex = lstFreqIdx.get(i).intValue();
			lstTopFreqWords.add(lstWords.get(nIndex));
			lstTopFreqCount.add(lstWordCount.get(nIndex));
			if (i >= nTopNum -1) break;
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
    //From the webpage, extract meta keywords and menu items
	//
	//It merges meta  with content words, frequency are calculated for each word
	//
	//Meta keywords are returned as an original string, it is only to show in the file, but later NOT used
	//
	public String analyzeWebpage_ForMenu(String strURL, int nTopNum, List<String> lstTopFreqWords, List<Integer> lstTopFreqCount) {
		
		List<String> lstWords = new ArrayList<String>();
		List<Integer> lstWordCount = new ArrayList<Integer>();
				
		String sMetatagcontent = "";
		List<Integer> lstFreqIdx = new ArrayList<Integer>();   //This list contains the index of words in lstWords, whose frquency is in DESC order
		boolean bFind = false;
		
		System.out.println("----------1---------------");
		
		String sPageContent = getWebPageContent(strURL);
		System.out.println("----------2---------------");		
		//Meta Keyword Extraction Begin////////////////////
		//Process keywords defined in html <meta content="......" name="keywords">
		Document doc = Jsoup.parse(sPageContent);
		Elements metalinks = doc.select("meta[name=keywords]");
		if (metalinks.size() > 0) {
			sMetatagcontent = metalinks.first().attr("content");
			
		    if (false) {   //20141030 Here below, commetted for now not separately process meta keywords. Meta keywords are processed with web menu items together
//				if (sMetatagcontent.length() > 0) {
//					//Split metatagcontent with "," and " " into words
//					//Changed on 2014/10/05 String sMetaKeywords[] = splitMetaContent(metatagcontent);
//					//Commented 2014/10/15 String sMetaKeywords[] = splitString(metatagcontent);   //Walmart.com will be separeated
//					
//					//////////////////////////Do we need to do noun/proper name extraction on MetaKeyword?
//					
//					
//					List<String> lstTmpMetaKeywords = new ArrayList<String>();
//					
//					lstTmpMetaKeywords = m_KeywordExtraction.extractKeywordFromContents(sMetatagcontent);
//					
//					for (int i=0; i<lstTmpMetaKeywords.size(); i++) {
//	
//						if (!lstMetaKeywords.contains(lstTmpMetaKeywords.get(i).toLowerCase())) {
//							bFind = false;
//							//Check black list for meta keywords
//							for (int j=0; j<m_strarrBlackListMetaKeywords.length; j++) {
//								if (m_strarrBlackListMetaKeywords[j].compareToIgnoreCase(lstTmpMetaKeywords.get(i))==0) {
//									bFind = true;
//									break;
//								}
//							}
//							
//							if (bFind == false) {
//								lstMetaKeywords.add(lstTmpMetaKeywords.get(i).toLowerCase());
//							}
//						}
//					}
//				}
		    }
		}
		
		//Meta Keyword Extraction End/////////////////////
		System.out.println("----------3---------------");
		// Begin For sContent, go through the same process as in "RecognizedTextAnalyzer" to get the proper names and nouns
		// If only need to process single words, then DO NOT use the two lines:
		//Originally all content on webpage:  String sContent = Jsoup.parse(sPageContent).text();
		
		//List<String> lstWebMean = WebMenuExtractor.extract_AllMenuItem(strURL);
		//List<String> lstWebMean = WebMenuExtractor.extract_NavMenuItem_RemoveFooter(strURL);
		List<String> lstWebMenu = WebMenuExtractor.extract_NavMenuItem_RemoveFooter_Enhanced(strURL);
		System.out.println("----------4---------------");
		
		if (lstWebMenu == null || lstWebMenu.size() == 0) {
			if (sMetatagcontent.trim().length() == 0) {
				return "";
			}
		}
				
		String sContent = "";
		
		//### Added for Merging Meta keywords with Web Menu Items 2014/10/30 ###//
		sContent = sMetatagcontent;
		
		if (lstWebMenu != null && lstWebMenu.size() != 0) { 
			for (String sMenu:lstWebMenu) {
				sContent = sContent + sMenu + ",";
			}
		}
		
		System.out.println("----------4---1------------");
		List<String> lststrFinalKeywords = new ArrayList<String>();
		lststrFinalKeywords = m_KeywordExtraction.extractKeywordFromContents(sContent);
		System.out.println("----------5---------------");
		int nTotalWordCount = 0;
		
/*	If only need to process single words, not noun phrases, then use this code	*/
//		System.out.println(sContent);
		String sArrWord[] = Utility.splitString(sContent);
		
		//Calculate word frequency
		for (int i = 0; i<sArrWord.length; i++) {
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
		
		//Print the words in DESC order of frequency
		//for (int i=0; i<lstFreqIdx.size(); i++) {
		//	int nIndex = lstFreqIdx.get(i).intValue();
			
		//	System.out.println(lstWordCount.get(nIndex).intValue() + " :: " + lstWords.get(nIndex));
		//}
	
		///////////////////////////////////////////////////////////////////////////////////////////////////////
	
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//This code get the top 10 frequent words
		for (int i=0; i<lstFreqIdx.size(); i++) {
			int nIndex = lstFreqIdx.get(i).intValue();
			lstTopFreqWords.add(lstWords.get(nIndex));
			lstTopFreqCount.add(lstWordCount.get(nIndex));
			if (i >= nTopNum -1) break;
		}
		
		return sMetatagcontent;
	}
	
	
	//This function is for whole web text
	//Here only need to extract meta keywords and all web text content,
	//Later, the web text will be merged with web image text,
	//And hence, noun/proper name extraction is carried out.
    public void analyzeWebpage_TextContent(String strURL, List<String> lstTextContent) {
		
		String sPageContent = getWebPageContent(strURL);

		String sContent = Jsoup.parse(sPageContent).text();
				
		//Meta Keyword Extraction Begin////////////////////
		//Process keywords defined in html <meta content="......" name="keywords">
		Document doc = Jsoup.parse(sPageContent);
		Elements metalinks = doc.select("meta[name=keywords]");
		String sMetaTagContent = "";
		if (metalinks.size() > 0) {
			sMetaTagContent = metalinks.first().attr("content");		
		}
		
		//Meta Keyword Extraction End/////////////////////
		
		lstTextContent.add(sMetaTagContent);
		lstTextContent.add(sContent);    	
    }	
    
}
