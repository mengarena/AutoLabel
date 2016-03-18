package com.autolabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.paukov.combinatorics.IFilter;
import org.paukov.combinatorics.IntegerFactory;
import org.paukov.combinatorics.IntegerGenerator;
import org.paukov.combinatorics.IntegerVector;
import org.paukov.combinatorics.util.ComplexCombinationGenerator;

//This class is for supporting functions
public class Utility {

	
	private static final String WEB_TEXT_FOLDER = "WebText";
	
	//The following two should be merged
	private static final String FULL_WEB_TEXT_FOLDER = "WebText_Full";  //WebText_Full
	//private static final String WEB_TEXT_FOLDER_TEMP = "WebText_WordCount_Temp";
	
	private static final String WEB_IMAGE_FOLDER = "WebImage";
	private static final String WEB_IMAGE_TEXT_FOLDER = "WebImageText";
	private static final String PLACE_KEYWORD_FILE_PRE = "PlaceKeywordList";
	private static final String WEB_TEXT_FILE_POST = "_Web_Text.csv";
	private static final String WEB_IMAGE_FILE_POST = "_Image_Text.csv";
	
	
	private static final String ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE = "MergedGpsApOCR";

	private static final String ONE_PLACE_MERGED_OCR_RESULT_FILE_FULL_PRE = "MergedOCR_FULL";
	
	private static final String FULLTEXT_OCR = "FullText";
	
	private static final String FRAME_STAT_FILE_PRE = "FrameStat";
	
//	private static final String ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_TEMP = "MergedGpsApOCR_Temp.csv";
	
	private static final String ONE_PLACE_GROUND_TRUTH_FILE = "GroundTruth.csv";
	
	private static final String CROWDSOURCE_GPSAP_FOLDER = "GpsAP";
	private static final String CROWDSOURCE_OCR_FOLDER = "OCRedWords";
	
	private static final String OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME = "withoutPlaceName";
	private static final String OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME = "withPlaceName";
		
	private static final String PLACE_AP_DB_FILE_PRE = "PlaceApDatabaseFile";
	private static final String KEYWORD_MATCHING_EVAL_RESULT_FILE_PRE = "KeywordMatchingEval";
	private static final String KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE = "KeywordMatchingCM";
	private static final String MATCHED_COMMON_WORD_FILE_PRE = "MatchedCommonWords";
	
	private static final String RANK_FILE_POSTFIX = "CorrectPlaceRanking";
	
	private static final double DISTANCE_THRESHOLD = 5000000;  //200 meters
	
	public static boolean USE_FULL_TEXT = false;   //true: Use all text of web and OCR data; false:  For web, only use menu item; For In-store Text, only use above-eye level, eye level
	
	public static final boolean USE_WEB_IMAGE_TEXT = false;   //true:  Use web image text;  false: Do not use web image text
	public static final boolean USE_WEB_META_KEYWORD = true;  //true:  meta keyword is used; false: meta keyword is not used ****

	public static boolean MATCH_WITH_PLACENAME = true;  //true:  when matching In-store OCRed words with web data, first will match the place name;  false: do not try matching with place name
	public static final boolean MATCH_WITH_PLACENAME_WITH_PHRASE = false; //true: match place name by phrase; false: match place name by words
	
	public static final boolean DOWNLOAD_WEB_IMAGE = false;   //true: download; false: don't download
	
	public static final boolean USE_TF_IDF_WEIGHT = true;  //true: use 0.5+0.5*f(t,d)/MaxFreq(d) to calcualte weight for terms;  false: use count/totalcount to calculate weight
	
	public static final int DEFAULT_TOP_KEYWORD_CNT = 9999;   //The number of top N keywords by default
	
	public static final int NO_FRAME_SAMPLE = 9999;  //Do not sample frame
	
	
	//Type of similarity
	public static final int SIMILARITY_WEB = 1;
	public static final int SIMILARITY_STORE = 2;
	
	//Weight for each part
	public static final double DEFAULT_TOTAL_WEIGHT_WEB_CONTENT = 1;
	public static final double DEFAULT_TOTAL_WEIGHT_WEB_IMG = 0;
	public static final double DEFAULT_TOTAL_WEIGHT_META = 0;	
	
	public static double MATCHED_WEIGHT_THRESHOLD = 0.5;   //Possible maximum weight is 1.0

	public static final int RSS_STD_CNT = 2;    // u-sigma,  how many sigma in deciding lower bound and upper bound of RSS
	public static int DEFAULT_AP_MAC_CNT = 20;   // How many AP MAC to be saved  10, 20
	
	public static final int DEFAULT_GOOGLE_PALCE_SEARCH_RADIUS = 100;  //The default radius for Google Place Search
	
	public static final int DB_SOURCE_DATA_TYPE_FILE = 1;  //One file
	public static final int DB_SOURCE_DATA_TYPE_FOLDER = 2;
	
//Modified on 04/02/2015  public static final int SENSING_FILE_GPS_LAT_IDX = 22;   //Start from 0  		
//Modified on 04/02/2015 public static final int SENSING_FILE_GPS_LONG_IDX = 23;  //Start from 0   		

	public static final int SENSING_FILE_GPS_LAT_IDX = 25;   //Start from 0
	public static final int SENSING_FILE_GPS_LONG_IDX = 26;  //Start from 0
	
//Modified on 04/02/2015 public static final int SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX = 29; //Start from 0, the start index of first WiFi tuple  			
//Modified on 04/02/2015 public static final int SENSING_FILE_WIFI_FIRST_TUPLE_END_IDX = 31; //Start from 0, the end index of first WiFi tuple  			
	public static final int SENSING_FILE_WIFI_FIRST_TUPLE_START_IDX = 32; //Start from 0, the start index of first WiFi tuple
	public static final int SENSING_FILE_WIFI_FIRST_TUPLE_END_IDX = 33; //Start from 0, the end index of first WiFi tuple

//Modified on 04/02/2015 public static final int SENSING_FILE_WIFI_TUPLE_LEN = 3;  //<SSID, BSSID, RSS>   		

	public static final int SENSING_FILE_WIFI_TUPLE_LEN = 2;  //<BSSID, RSS>
	
	//Line prefix in Full Web Text file
	public static final String FULL_WEBTEXT_LINE_PRE_PLACENAME = "Place Name:";
	public static final String FULL_WEBTEXT_LINE_PRE_GPS = "GPS:";
	public static final String FULL_WEBTEXT_LINE_PRE_META = "Meta Keywords:";
	public static final String FULL_WEBTEXT_LINE_PRE_CONTENT = "Web Keywords:";
	
	public static final String FULL_WEBTEXT_LINE_PRE_META_RAW_TEXT = "Meta Text:";
	public static final String FULL_WEBTEXT_LINE_PRE_CONTENT_RAW_TEXT = "Web Text:";
	
	public static final int TOTAL_PLACE_CNT = 40;   //Total place number
	
	private static final String[] SEASON_BLACKLIST = {
		"valentine", "halloween", "thanksgiving",  "christmas", "xmas", "merry", "black", "friday" 
	};
	
	private static final String[] PREP_BLACKLIST = {
		"about", "above", "after", "along", "among", "and",
		"around", "as", "at", "before", "behind", "below",
		"besides", "between", "by", "down", "except",
		"from", "for", "in", "into", "of", "off",
		"on", "onto", "or", "over", "since",
		"than", "then", "through", "to", "under",
		"until", "up", "via", "with", "within"		
	};
	
	private static final String[] INDEF_PRONOUN_BE_VERB = {
		"a", "an",
		"is", "was", "am", "are", "were",		
	};
	
	private static final String[] PLACENAME_BLACKLIST = {
		"a", "an",
		"is", "was", "am", "are", "were",
		"the", "this", "that", "these", "those",
		"it", "he", "she", "I", "they",
		"me", "you","him", "her", "them",
		"his", "my", "your", "its", "their",
		"mine", "yours", "theirs",

//		"one", "two", "three", "four", "five","six", "seven", "eight", "nine", "ten",
	};

	
//	private String[] m_strArrKeywordsOnWeb = {
//			"Office",
//			"Company",
//			"Shop",
//			"Store",
//			"Restaurant",
//			"Hotel",
//			"Inn",
//			"Mall",
//			"Club",
//			"Conference",
//			"building",
//			"station",
//			"bar",
//			"market"
//		};
	
	private static String[] m_strPostfixCommon = {
		".com", ".gov", ".net", ".org", ".edu", ".int", ".mil", ".biz",
	};
	
	private static String[] m_strPostfix = {
		".co.", ".go.", ".ne.", ".or.", ".ed.", ".ac."
	};
	
	private static String[] m_strPrefix = {
		"http", "www", 
	};
	
	public static String[] m_strIgnoreOCRFolders = {
		"",
		"",
		"",
		"",
		"",
		"",
		""
	};
	
	public Utility() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getScaledFilePathName(String sDataFile) {
		String sScaledFilePathName = "";
		int nIndex = -1;
		
		nIndex = sDataFile.lastIndexOf(".");
		
		sScaledFilePathName = sDataFile.substring(0, nIndex) + ".scale";
		
		return sScaledFilePathName;
	}
	
	
	public static void TestList() {
		List<Integer> lstValue = new ArrayList<Integer>();
		
		List<List<Integer>> lstlstValue = new ArrayList<List<Integer>>();
		
		List<String> lstStrValue = new ArrayList<String>();
		
		lstValue.add(3);
		lstValue.add(null);
		lstValue.add(8);
		
		lstStrValue.add("meng");
		lstStrValue.add(null);
		lstStrValue.add("feng");
		
		List<Integer> lstTmp1 = new ArrayList<Integer>();
		lstTmp1.add(334);
		lstTmp1.add(null);
		lstTmp1.add(90);
		
		lstlstValue.add(lstTmp1);
		lstlstValue.add(null);

		List<Integer> lstTmp2 = new ArrayList<Integer>();

		lstTmp2.add(54);
		lstTmp2.add(null);
		
		lstlstValue.add(lstTmp2);
		
		if (lstValue.get(1) == null) {
			int i=0;
			
			i = lstStrValue.get(1).length();
			
			i=i+1;
			
			
		}
		
		return;
	}
	
	
	public static boolean checkOverlap(List<String> lstValuesA, List<String> lstValuesB) {
		boolean bOverlapped = false;
		
		for (String sValueA : lstValuesA) {
			if (lstValuesB.contains(sValueA) == true) {
				bOverlapped = true;
				break;
			}
		}
		
		return bOverlapped;
	}
	
	
	//Get the matched frame list for the given web index
	public static List<Integer> getSameWebMatchedFrames(List<List<Integer>> lstlstTotalSelectedFramesMatchedWebIndex, int nMatchedWebIndex, int nStartFrameIdx) {
		List<Integer> lstFrameIndex = new ArrayList<Integer>();
		int i,j;
		
		for (i=nStartFrameIdx; i<lstlstTotalSelectedFramesMatchedWebIndex.size(); i++) {     //Corresponding to frames
			List<Integer> lstMatchedWebIndex = lstlstTotalSelectedFramesMatchedWebIndex.get(i);
			
			if (lstMatchedWebIndex.contains(nMatchedWebIndex) == true) {
				lstFrameIndex.add(i); 
			}
			
		}
		
		return lstFrameIndex;
	}
	
	
	//Get all possible combination corresponding to one web
	public static List<List<Integer>> getAllOrderedCombination(List<Integer> lstValues) {
		List<List<Integer>> lstlstCombination = new ArrayList<List<Integer>>();
		int nFirstValue = lstValues.get(0);

		ICombinatoricsVector<Integer> initialSet = Factory.createVector(lstValues);
		
		Generator<Integer> gen = Factory.createSubSetGenerator(initialSet);

		for (ICombinatoricsVector<Integer> subSet : gen) {
			if (subSet.getSize() > 0 &&  subSet.getValue(0) == nFirstValue) {
				List<Integer> lstOneCombine = new ArrayList<Integer>();
				
				for (int i=0; i<subSet.getSize(); i++) {
					lstOneCombine.add(subSet.getValue(i));
				}
				
				lstlstCombination.add(lstOneCombine);
			}
		}		
		
		return lstlstCombination;
	}
	
	
	//Return Result:   p1, p1 p3, p1 p5, p1 p8, p1 p3 p8;  p2, p2 p3, p2 p4, p2 p3 p6;
	//lstlstWebIndex:  w1,    w1,    w1,    w4,       w4;  w2,    w2,    w5,       w6;	
	public static List<List<List<Integer>>> formPossibleCombination(List<List<Integer>> lstlstTotalSelectedFramesMatchedWebIndex, List<List<Integer>> lstlstWebIndex) {
		List<List<List<Integer>>> lstlstlstPossibleCombine = new ArrayList<List<List<Integer>>>();
		int i,j,k;
		int nMatchedWebIndex;
		
		for (i=0; i<lstlstTotalSelectedFramesMatchedWebIndex.size(); i++) {
			List<Integer> lstMatchedWebIndex = lstlstTotalSelectedFramesMatchedWebIndex.get(i); //Matched Web index of (one) current frame
						
			List<List<Integer>> lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
			
			List<Integer> lstWebIndex = new ArrayList<Integer>();
			
			for (j=0; j<lstMatchedWebIndex.size(); j++) {  //Matched Web Index of each frame
				nMatchedWebIndex = lstMatchedWebIndex.get(j);
				
				List<Integer> lstSameWebMatchedFrames = new ArrayList<Integer>();
				
				lstSameWebMatchedFrames = getSameWebMatchedFrames(lstlstTotalSelectedFramesMatchedWebIndex, nMatchedWebIndex, i);
				
				List<List<Integer>> lstlstCombination = getAllOrderedCombination(lstSameWebMatchedFrames);
				
				for (k=0; k<lstlstCombination.size(); k++) {
					lstlstOneFramePossibleCombine.add(lstlstCombination.get(k));   //Check whether combination exists
					lstWebIndex.add(nMatchedWebIndex);     //Update matched webIndex;  should be lstlstWebIndex
				}
			}
						
			lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
			
			lstlstWebIndex.add(lstWebIndex);
		}
		
		return lstlstlstPossibleCombine;
	}
	

	//Return Result:         p1,    p1 p3, p1 p5, p1 p8, p1 p3 p8;  p2, p2 p3, p2 p4, p2 p3 p6;
	//lstlstWebIndex:  [w1, w4], [w1, w5],    w1,    w4,       w4;  w2,    w2,    w5,       w6;	
	public static List<List<List<Integer>>> formPossibleCombination_UniqueCombine(List<List<Integer>> lstlstTotalSelectedFramesMatchedWebIndex, List<List<List<Integer>>> lstlstlstWebIndex) {
		List<List<List<Integer>>> lstlstlstPossibleCombine = new ArrayList<List<List<Integer>>>();
		int i,j,k;
		int nMatchedWebIndex;
		
		for (i=0; i<lstlstTotalSelectedFramesMatchedWebIndex.size(); i++) {
			List<Integer> lstMatchedWebIndex = lstlstTotalSelectedFramesMatchedWebIndex.get(i); //Matched Web index of (one) current frame
						
			List<List<Integer>> lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
			List<List<Integer>> lstlstWebIndex = new ArrayList<List<Integer>>();
			
			for (j=0; j<lstMatchedWebIndex.size(); j++) {  //Matched Web Index of each frame
				nMatchedWebIndex = lstMatchedWebIndex.get(j);
				
				List<Integer> lstSameWebMatchedFrames = new ArrayList<Integer>();
				
				lstSameWebMatchedFrames = getSameWebMatchedFrames(lstlstTotalSelectedFramesMatchedWebIndex, nMatchedWebIndex, i);
				
				List<List<Integer>> lstlstCombination = getAllOrderedCombination(lstSameWebMatchedFrames);
				
				for (k=0; k<lstlstCombination.size(); k++) {
					
					int nPos = lstlstOneFramePossibleCombine.indexOf(lstlstCombination.get(k));
					
					if (nPos == -1) {
						lstlstOneFramePossibleCombine.add(lstlstCombination.get(k));   //Check whether combination exists
					
						List<Integer> lstWebIndex = new ArrayList<Integer>();
						lstWebIndex.add(nMatchedWebIndex);     //Update matche webIndex;  should be lstlstWebIndex
						
						lstlstWebIndex.add(lstWebIndex);
					} else {
						List<Integer> lstWebIndex = lstlstWebIndex.get(nPos);
						lstWebIndex.add(nMatchedWebIndex);
						
						lstlstWebIndex.set(nPos, lstWebIndex);
					}
				}
			}
						
			lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
			
			lstlstlstWebIndex.add(lstlstWebIndex);
		}
		
		return lstlstlstPossibleCombine;
	}
	
	
	public static int getWebIndex(List<List<List<Integer>>> lstlstlstPossibleCombine, List<List<Integer>> lstlstWebIndex, List<Integer> lstCombine) {
		int i,j,k;
		int nWebIndex = 9999;
		
		for (i=0; i<lstlstlstPossibleCombine.size(); i++) {
			List<List<Integer>> lstlstCombine = lstlstlstPossibleCombine.get(i);
			List<Integer> lstWebIndex = lstlstWebIndex.get(i);
			
			int nPos = lstlstCombine.indexOf(lstCombine);
			
			if (nPos != -1) {
				nWebIndex = lstWebIndex.get(nPos);
				return nWebIndex;
			}
			
		}
		
		return nWebIndex;
	}
	
	
	public static void testFrameSubsetA() {
		List<List<List<Integer>>> lstlstlstPossibleCombine = new ArrayList<List<List<Integer>>>();
		
		List<List<Integer>> lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
		List<Integer> lstOneCombine = new ArrayList<Integer>();
		
		lstOneCombine.add(0);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(0);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(0);lstOneCombine.add(1);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(0);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
		////////////
		lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
		lstOneCombine = new ArrayList<Integer>();
		
		lstOneCombine.add(1);
		lstlstOneFramePossibleCombine.add(lstOneCombine);

		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(1);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
				
		lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
		///////////////////
				
		List<List<List<Integer>>> lstlstlstValidCombination = new ArrayList<List<List<Integer>>>();
		
		getValidPartition(lstlstlstPossibleCombine, lstlstlstValidCombination);
		
		DEBUG_INFO("Total......" + lstlstlstValidCombination.size());
		
	}
	
	
	
	public static void testFrameSubset() {
		List<List<List<Integer>>> lstlstlstPossibleCombine = new ArrayList<List<List<Integer>>>();
		
		List<List<Integer>> lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
		List<Integer> lstOneCombine = new ArrayList<Integer>();
		
		lstOneCombine.add(1);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(1);lstOneCombine.add(3);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(1);lstOneCombine.add(5);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(1);lstOneCombine.add(8);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(1);lstOneCombine.add(3);lstOneCombine.add(8);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
		
		////////////
		lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
		lstOneCombine = new ArrayList<Integer>();
		
		lstOneCombine.add(2);
		lstlstOneFramePossibleCombine.add(lstOneCombine);

		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(2);lstOneCombine.add(3);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(2);lstOneCombine.add(4);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(2);lstOneCombine.add(6);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(2);lstOneCombine.add(7);
		lstlstOneFramePossibleCombine.add(lstOneCombine);

		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(2);lstOneCombine.add(3);lstOneCombine.add(6);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
		///////////////////
		
		lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
		lstOneCombine = new ArrayList<Integer>();
		
		lstOneCombine.add(3);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(3);lstOneCombine.add(6);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(3);lstOneCombine.add(8);
		lstlstOneFramePossibleCombine.add(lstOneCombine);

		lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
		///////////////////////
		
		lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
		lstOneCombine = new ArrayList<Integer>();
		
		lstOneCombine.add(4);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(4);lstOneCombine.add(7);
		lstlstOneFramePossibleCombine.add(lstOneCombine);

		lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
		///////////////////////

		lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
		lstOneCombine = new ArrayList<Integer>();
		
		lstOneCombine.add(5);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(5);lstOneCombine.add(8);
		lstlstOneFramePossibleCombine.add(lstOneCombine);

		lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
		///////////////////////

		lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
		lstOneCombine = new ArrayList<Integer>();
		
		lstOneCombine.add(6);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstOneCombine = new ArrayList<Integer>();
		lstOneCombine.add(6);lstOneCombine.add(8);
		lstlstOneFramePossibleCombine.add(lstOneCombine);

		lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
		///////////////////////
		
		lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
		lstOneCombine = new ArrayList<Integer>();
		
		lstOneCombine.add(7);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
		///////////////////////
	
		lstlstOneFramePossibleCombine = new ArrayList<List<Integer>>();
		lstOneCombine = new ArrayList<Integer>();
		
		lstOneCombine.add(8);
		lstlstOneFramePossibleCombine.add(lstOneCombine);
		
		lstlstlstPossibleCombine.add(lstlstOneFramePossibleCombine);
		///////////////////////
		
		List<List<List<Integer>>> lstlstlstValidCombination = new ArrayList<List<List<Integer>>>();
		
		getValidPartition(lstlstlstPossibleCombine, lstlstlstValidCombination);
		
		DEBUG_INFO("Total......" + lstlstlstValidCombination.size());
		
	}
	
	
	public static void getValidPartition(List<List<List<Integer>>> lstlstlstFrameGroup, List<List<List<Integer>>> lstlstlstValidCombination) {
		int nGroupIndex = 0;
		
		List<List<Integer>> lstlstOneFramePossibleCombine = lstlstlstFrameGroup.get(nGroupIndex);
		
		for (int i=0; i<lstlstOneFramePossibleCombine.size(); i++) {
			//DEBUG_INFO("Here------" + i);
			List<Integer> lstTotalSelected = new ArrayList<Integer>();
			List<List<Integer>> lstlstPartition = new ArrayList<List<Integer>>();

			List<Integer> lstOneCombine = lstlstOneFramePossibleCombine.get(i);
			
			lstlstPartition.add(lstOneCombine);
			for (int j=0; j<lstOneCombine.size(); j++) {
				lstTotalSelected.add(lstOneCombine.get(j));
			}
			
			nGroupIndex = 0;
			getValidPartition(lstlstlstFrameGroup, nGroupIndex+1, lstTotalSelected, lstlstPartition, lstlstlstValidCombination);
		}
		
		return;
	}

	
	public static void getValidPartitionS(List<List<List<Integer>>> lstlstlstFrameGroup, List<List<List<Integer>>> lstlstlstValidCombination, List<List<Integer>> lstlstCombineIndex) {
		int nGroupIndex = 0;
		
		List<List<Integer>> lstlstOneFramePossibleCombine = lstlstlstFrameGroup.get(0);
		
		for (int i=0; i<lstlstOneFramePossibleCombine.size(); i++) {
			List<Integer> lstCombineIndex = new ArrayList<Integer>();
			//DEBUG_INFO("Here------" + i);
			List<Integer> lstTotalSelected = new ArrayList<Integer>();
			List<List<Integer>> lstlstPartition = new ArrayList<List<Integer>>();

			List<Integer> lstOneCombine = lstlstOneFramePossibleCombine.get(i);  //e.g. (p1 p3)
			
			lstCombineIndex.add(i);
			
			lstlstPartition.add(lstOneCombine);
			for (int j=0; j<lstOneCombine.size(); j++) {
				lstTotalSelected.add(lstOneCombine.get(j));
			}
			
			nGroupIndex = 0;
			getValidPartitionS(lstlstlstFrameGroup, nGroupIndex+1, lstTotalSelected, lstlstPartition, lstlstlstValidCombination, lstlstCombineIndex, lstCombineIndex);
			
			//lstlstCombineIndex.add(lstCombineIndex);
		}
		
		return;
	}
	
	
	
	public static void ShowParition(List<List<Integer>> lstlstPartition) {
/*				
		String sPartition = "";
		
		for (int i=0; i<lstlstPartition.size(); i++) {
			sPartition = sPartition + "[";
			List<Integer> lstPartition = lstlstPartition.get(i);
			for (int j=0; j<lstPartition.size(); j++) {
				if (j<lstPartition.size()-1) {
					sPartition = sPartition + lstPartition.get(j) + ",";
				} else {
					sPartition = sPartition + lstPartition.get(j);
				}
			}
			
			sPartition = sPartition + "] ";
		}
		
		System.out.println(sPartition);
*/		
	}
	
	
	public static boolean IsListExisting(List<Integer> lstTotalSelected, List<Integer> lstFrameGroup) {
		boolean bExisting = false;
		
		for (int i=0; i<lstFrameGroup.size(); i++) {
			if (lstTotalSelected.contains(lstFrameGroup.get(i)) == true) {
				bExisting = true;
				break;
			}
		}
		
		return bExisting;
		
	}
	
	public static void copyStringList(List<String> lstOrg, List<String> lstNew) {
		for (String sVal : lstOrg) {
			lstNew.add(sVal);
		}
	}
	
	
	public static void copyList(List<Integer> lstOrg, List<Integer> lstNew) {
		for (int i=0; i<lstOrg.size(); i++) {
			lstNew.add(lstOrg.get(i));
		}
	}
	
	public static void copyListList(List<List<Integer>> lstlstOrg, List<List<Integer>> lstlstNew) {
		
		for (int i=0; i<lstlstOrg.size(); i++) {
			List<Integer> lstOrg = lstlstOrg.get(i);
			lstlstNew.add(lstOrg);
		}
	}
	
	
	public static void getValidPartition(List<List<List<Integer>>> lstlstlstFrameGroup, int nGroupIndex, List<Integer> lstTotalSelected, List<List<Integer>> lstlstPartition, List<List<List<Integer>>> lstlstlstValidCombination) {
		if (lstTotalSelected.size() == lstlstlstFrameGroup.size()) {  //All frames are included
			ShowParition(lstlstPartition);
			lstlstlstValidCombination.add(lstlstPartition);
			return;
		}
		
		if ((lstTotalSelected.contains(nGroupIndex+1)) == true) {
			List<Integer> lstTotalSelectedTmp = new ArrayList<Integer>();
			List<List<Integer>> lstlstPartitionTmp = new ArrayList<List<Integer>>();

			copyList(lstTotalSelected, lstTotalSelectedTmp);
			copyListList(lstlstPartition, lstlstPartitionTmp);

			getValidPartition(lstlstlstFrameGroup, nGroupIndex+1, lstTotalSelectedTmp, lstlstPartitionTmp, lstlstlstValidCombination);
			return;
		}
		
//		copyList(lstTotalSelected, lstTotalSelectedOrg);
//		copyListList(lstlstPartition, lstlstPartitionOrg);
		
		List<List<Integer>> lstlstFrameGroup = lstlstlstFrameGroup.get(nGroupIndex);
		
		for (int i=0; i<lstlstFrameGroup.size(); i++) {
			List<Integer> lstFrameGroup = lstlstFrameGroup.get(i);
			List<Integer> lstTotalSelectedTmp = new ArrayList<Integer>();
			List<List<Integer>> lstlstPartitionTmp = new ArrayList<List<Integer>>();

			copyList(lstTotalSelected, lstTotalSelectedTmp);
			copyListList(lstlstPartition, lstlstPartitionTmp);
			
			if (IsListExisting(lstTotalSelectedTmp, lstFrameGroup) == true) continue;
			
			lstlstPartitionTmp.add(lstFrameGroup);
			
			for (int j=0; j<lstFrameGroup.size(); j++) {
				lstTotalSelectedTmp.add(lstFrameGroup.get(j));
			}
			
			if (nGroupIndex < lstlstlstFrameGroup.size() -1) {
				getValidPartition(lstlstlstFrameGroup, nGroupIndex+1, lstTotalSelectedTmp, lstlstPartitionTmp, lstlstlstValidCombination);
			} else {
				ShowParition(lstlstPartitionTmp);
				lstlstlstValidCombination.add(lstlstPartitionTmp);
				
				return;
			}
		}
	}
	

	public static void getValidPartitionS(List<List<List<Integer>>> lstlstlstFrameGroup, int nGroupIndex, List<Integer> lstTotalSelected, List<List<Integer>> lstlstPartition, List<List<List<Integer>>> lstlstlstValidCombination, 
			                              List<List<Integer>> lstlstCombineIndex, List<Integer> lstCombineIndex) {
		if (lstTotalSelected.size() == lstlstlstFrameGroup.size()) {  //All frames are included
			ShowParition(lstlstPartition);
			lstlstlstValidCombination.add(lstlstPartition);
			lstlstCombineIndex.add(lstCombineIndex);
			return;
		}
		
		if ((lstTotalSelected.contains(nGroupIndex+1)) == true) {
			List<Integer> lstTotalSelectedTmp = new ArrayList<Integer>();
			List<List<Integer>> lstlstPartitionTmp = new ArrayList<List<Integer>>();
			List<Integer> lstCombineIndexTmp = new ArrayList<Integer>();
			
			copyList(lstTotalSelected, lstTotalSelectedTmp);
			copyListList(lstlstPartition, lstlstPartitionTmp);
			copyList(lstCombineIndex, lstCombineIndexTmp);
			
			lstCombineIndexTmp.add(-1);
			getValidPartitionS(lstlstlstFrameGroup, nGroupIndex+1, lstTotalSelectedTmp, lstlstPartitionTmp, lstlstlstValidCombination, lstlstCombineIndex, lstCombineIndexTmp);
			return;
		}
		
//		copyList(lstTotalSelected, lstTotalSelectedOrg);
//		copyListList(lstlstPartition, lstlstPartitionOrg);
		
		List<List<Integer>> lstlstFrameGroup = lstlstlstFrameGroup.get(nGroupIndex);
		
		for (int i=0; i<lstlstFrameGroup.size(); i++) {
			List<Integer> lstFrameGroup = lstlstFrameGroup.get(i);
			List<Integer> lstTotalSelectedTmp = new ArrayList<Integer>();
			List<List<Integer>> lstlstPartitionTmp = new ArrayList<List<Integer>>();
			List<Integer> lstCombineIndexTmp = new ArrayList<Integer>();

			copyList(lstTotalSelected, lstTotalSelectedTmp);
			copyListList(lstlstPartition, lstlstPartitionTmp);
			copyList(lstCombineIndex, lstCombineIndexTmp);
			
			if (IsListExisting(lstTotalSelectedTmp, lstFrameGroup) == true) continue;
			
			lstCombineIndexTmp.add(i);
			
			lstlstPartitionTmp.add(lstFrameGroup);
			
			for (int j=0; j<lstFrameGroup.size(); j++) {
				lstTotalSelectedTmp.add(lstFrameGroup.get(j));
			}
			
			if (nGroupIndex < lstlstlstFrameGroup.size() -1) {
				getValidPartitionS(lstlstlstFrameGroup, nGroupIndex+1, lstTotalSelectedTmp, lstlstPartitionTmp, lstlstlstValidCombination, lstlstCombineIndex, lstCombineIndexTmp);
			} else {
				ShowParition(lstlstPartitionTmp);
				lstlstlstValidCombination.add(lstlstPartitionTmp);
				lstlstCombineIndex.add(lstCombineIndexTmp);
				return;
			}
		}
	}
	
	
	
	
	public static void DEBUG_INFO(String sInfo) {
		System.out.println("[DEBUG]================" + sInfo);
	}
	
	public static void PrintCurrentTime() {
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyy/MM/dd-HH:mm:ss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);

 	    System.out.println("Time:  " + spdCurrentTime.format(dtFileStart));	
	}
	
	
	public static boolean checkSameSame(List<Integer> lstGroup, List<Integer> lstGroupTmp) {
		boolean bSame = true;
		int i,j;
		
		//if (lstGroup.size() != lstGroupTmp.size()) {
		//	bSame = false;
		//} else {
			for (i=0; i<lstGroup.size(); i++) {
				int nValue = lstGroup.get(i);
				
				if (lstGroupTmp.contains(nValue) == false) {
					bSame = false;
					break;
				}
			}
		//}
		
		return bSame;
	}
	
	public static boolean checkExistinging(List<List<Integer>> lstlstGroup, List<Integer> lstGroupTmp) {
		boolean bExistinging = false;
		int i,j;
		
		for (i=0; i<lstlstGroup.size(); i++) {
			List<Integer> lstGroup = lstlstGroup.get(i);
			
			if (checkSameSame(lstGroup, lstGroupTmp) == true) {
				bExistinging = true;
				break;
			}
		}
		
		
		return bExistinging;
	}
	
	public static boolean checkSame(List<List<Integer>> lstGroupTmp, List<List<Integer>> lstlstGroup) {
		boolean bSame = true;
		int i,j;
		
		//if (lstGroupTmp.size() != lstlstGroup.size()) {
		//	bSame = false; 
		//} else {
			for (i=0; i<lstGroupTmp.size(); i++) {
				List<Integer> groupTmp = new ArrayList<Integer>();
				groupTmp = lstGroupTmp.get(i);
				boolean bExist = false;
				
				bExist = checkExistinging(lstlstGroup, groupTmp);
				
				if (bExist == false) {
					bSame = false;
					break;
				}
				
			}
		//}
		
		return bSame;
	}
	
	
	public static boolean checkExisting(List<List<List<Integer>>> lstlstlstGroupSet, List<List<Integer>> lstlstGroup) {
		boolean bExist = false;
		int i;
		int j;
		
		for (i=0; i<lstlstlstGroupSet.size(); i++) {
			List<List<Integer>> lstGroupTmp = lstlstlstGroupSet.get(i);
			
			boolean bSame = false;
			
			bSame = checkSame(lstGroupTmp, lstlstGroup);
			if (bSame == true) {
				bExist = true;
				break;
			}
		}
		
		return bExist;
	}
	
	public static List<List<List<Integer>>> getFrameIndexList_Partion(int nTotalFrame, int nGroupSize) {
		Integer[] narrFrames = new Integer[nTotalFrame];
		int i;
		
		long lnCount = 0;
		
		int nGroupPerm = 1;
		
		int nSelectIndex = 0;
		
		List<List<List<Integer>>> lstlstlstGroupSet = new ArrayList<List<List<Integer>>>();
		
		int nGroupNum = (int)(nTotalFrame/nGroupSize);
		
		for (i=1; i<=nTotalFrame; i++) {
			narrFrames[i-1] = Integer.valueOf(i);
		}
		
		for (i=1; i<=nGroupNum; i++) {
			nGroupPerm = nGroupPerm * i;
		}
		
		boolean bCorrect = false;
		ICombinatoricsVector<Integer> initialVector = Factory.createVector(narrFrames);
		Generator<ICombinatoricsVector<Integer>> gen = new ComplexCombinationGenerator<Integer>(initialVector, nGroupNum);
		//Generator<Integer> gen = Factory.createPermutationGenerator(initialVector);
		
		System.out.println("------Cnt: " +  gen.generateAllObjects().size());

		/*
		System.out.println("Generated.....Selecting...........");
		
		for (ICombinatoricsVector<ICombinatoricsVector<Integer>> perm : gen) {
			System.out.println("-------0--------");
			List<ICombinatoricsVector<Integer>> lstVec = perm.getVector();
			bCorrect = true;
			System.out.println("-------000--------");
			for (i=0; i<lstVec.size(); i++) {
				System.out.println("========================000000--------");
				if (lstVec.get(i).getSize() != nGroupSize) {
					bCorrect = false;
					break;
				}
			}
			
			System.out.println("-------1--------");
			if (bCorrect == false) continue;
			System.out.println("#############################2-------------------------------------");
			if (nSelectIndex % nGroupPerm != 0) {
				nSelectIndex = (nSelectIndex + 1) % nGroupPerm;
				continue;
			} else {
				lnCount = lnCount + 1;
				nSelectIndex = (nSelectIndex + 1) % nGroupPerm;
				
				List<List<Integer>> lstlstGroup = new ArrayList<List<Integer>>();
				
				for (i=0; i<lstVec.size(); i++) {
	//				List<Integer> lstGroup = new ArrayList<Integer>();
	//				
	//				ICombinatoricsVector<Integer> lstN = lstVec.get(i).;
					
					lstlstGroup.add(lstVec.get(i).getVector());
				}
				
				//boolean bExist = checkExisting(lstlstlstGroupSet, lstlstGroup);
				//if (bExist == false) {
					lstlstlstGroupSet.add(lstlstGroup);
					
				//	System.out.println(ComplexCombinationGenerator.convert2String(perm));
				//}
				
				System.out.println("----" + lnCount + "-------" +  ComplexCombinationGenerator.convert2String(perm));
	//			List<Integer> kk = perm.getVector();
	//			  String sLine = "";
	//			  for(i=0; i<kk.size(); i++) {
	//				  sLine = sLine + kk.get(i) + ",";
	//			  }
	//			  sLine = sLine + "\n";
	//		      System.out.println(sLine);
			}
		}
		
		System.out.println("=================" + lnCount);
		 
		*/
		
		return lstlstlstGroupSet;
	}	
	
	
	public static List<List<Integer>> GetListListGroup(List<Integer> lstValues, int nGroupSize, int nGroupCnt) {
		List<List<Integer>> lstlstGroup = new ArrayList<List<Integer>>();
		int nValue;
		int nValueTmp;
		
		for (int i=0; i<nGroupCnt; i++) {
			List<Integer> lstGroup = new ArrayList<Integer>();
			
			for (int j=i*nGroupSize; j<=(i+1)*nGroupSize-1; j++) {
				nValue = lstValues.get(j);
				
				if (j == i*nGroupSize) {
					lstGroup.add(nValue);
				} else {
					int nPos = -1;
					for (int k=0; k<lstGroup.size(); k++) {
						if (nValue < lstGroup.get(k)) {
							nPos = k;
							break;
						}
					}
					
					if (nPos == -1) {
						lstGroup.add(nValue);
					} else {
						lstGroup.add(nPos, nValue);
					}
					
				}
			}
			
			if (i==0) {
				lstlstGroup.add(lstGroup);
			} else {
				int nPos = -1;
				nValueTmp = lstGroup.get(0);
				for (int kk=0; kk<lstlstGroup.size(); kk++) {
					if (nValueTmp < lstlstGroup.get(kk).get(0)) {
						nPos = kk;
						break;
					}
				}
				
				if (nPos == -1) {
					lstlstGroup.add(lstGroup);
				} else {
					lstlstGroup.add(nPos,lstGroup);
				}
			}
		}
		
		return lstlstGroup;
	}

	
	public static List<List<List<Integer>>> getFrameIndexList_Special(int nTotalFrame, int nGroupSize) {
		Integer[] narrFrames = new Integer[nTotalFrame];
		int i;
		int nGroupCnt = (int)(nTotalFrame/nGroupSize);
		//long lnCount = 0;
		long nTrueCnt = 0;
		
		List<List<List<Integer>>> lstlstlstGroupSet = new ArrayList<List<List<Integer>>>();
				
		for (i=1; i<=nTotalFrame; i++) {
			narrFrames[i-1] = Integer.valueOf(i);
		}
		
		ICombinatoricsVector<Integer> initialVector = Factory.createVector(narrFrames);
		Generator<Integer> gen = Factory.createPermutationGenerator(initialVector);
		
		System.out.println("Data Generated");
		PrintCurrentTime();
		
		for (ICombinatoricsVector<Integer> perm : gen) {
			//lnCount = lnCount + 1;
			
			List<List<Integer>> lstlstGroup = GetListListGroup(perm.getVector(), nGroupSize, nGroupCnt);
			
			//boolean bExist = checkExisting(lstlstlstGroupSet, lstlstGroup);

//			if (bExist == false) {
			
		//	if (lstlstlstGroupSet.contains(lstlstGroup) == false) {
			
				nTrueCnt = nTrueCnt + 1;
				lstlstlstGroupSet.add(lstlstGroup);
				
//				String sLine = "";
//				for (i=0; i<lstlstGroup.size(); i++) {
//					List<Integer> lstGroup = lstlstGroup.get(i);
//					sLine = sLine + " [";
//					for (int j=0; j<lstGroup.size(); j++) {
//						if (j != lstGroup.size()-1) {
//							sLine = sLine + lstGroup.get(j) + ",";
//						} else {
//							sLine = sLine + lstGroup.get(j);
//						}
//					}
//					sLine = sLine + "] ";
//				}
//				
//				sLine = sLine + "\n";
//				System.out.println(sLine);
				
				//System.out.println("---------------" + nTrueCnt);
		//	}
			
			
//			List<Integer> kk = perm.getVector();
//			  String sLine = "";
//			  for(i=0; i<kk.size(); i++) {
//				  sLine = sLine + kk.get(i) + ",";
//			  }
//			  sLine = sLine + "\n";
//		      System.out.println(sLine);
		}

//		System.out.println("\n==========Total: " + lnCount + "=====True Count: " + nTrueCnt);
		
		System.out.println("=====True Count: " + nTrueCnt);
		 
		return lstlstlstGroupSet;
	}
	
	

	public static List<List<List<Integer>>> getFrameIndexList(int nTotalFrame, int nGroupSize) {
		Integer[] narrFrames = new Integer[nTotalFrame];
		int i;
		int nGroupCnt = (int)(nTotalFrame/nGroupSize);
		//long lnCount = 0;
		long nTrueCnt = 0;
		
		List<List<List<Integer>>> lstlstlstGroupSet = new ArrayList<List<List<Integer>>>();
				
		for (i=1; i<=nTotalFrame; i++) {
			narrFrames[i-1] = Integer.valueOf(i);
		}
		
		ICombinatoricsVector<Integer> initialVector = Factory.createVector(narrFrames);
		Generator<Integer> gen = Factory.createPermutationGenerator(initialVector);
		
		System.out.println("Data Generated");
		PrintCurrentTime();
		
		for (ICombinatoricsVector<Integer> perm : gen) {
			//lnCount = lnCount + 1;
			
			List<List<Integer>> lstlstGroup = GetListListGroup(perm.getVector(), nGroupSize, nGroupCnt);
			
			//boolean bExist = checkExisting(lstlstlstGroupSet, lstlstGroup);

//			if (bExist == false) {
			
			if (lstlstlstGroupSet.contains(lstlstGroup) == false) {
			
				nTrueCnt = nTrueCnt + 1;
				lstlstlstGroupSet.add(lstlstGroup);
				
//				String sLine = "";
//				for (i=0; i<lstlstGroup.size(); i++) {
//					List<Integer> lstGroup = lstlstGroup.get(i);
//					sLine = sLine + " [";
//					for (int j=0; j<lstGroup.size(); j++) {
//						if (j != lstGroup.size()-1) {
//							sLine = sLine + lstGroup.get(j) + ",";
//						} else {
//							sLine = sLine + lstGroup.get(j);
//						}
//					}
//					sLine = sLine + "] ";
//				}
//				
//				sLine = sLine + "\n";
//				System.out.println(sLine);
				
				//System.out.println("---------------" + nTrueCnt);
			}
			
			
//			List<Integer> kk = perm.getVector();
//			  String sLine = "";
//			  for(i=0; i<kk.size(); i++) {
//				  sLine = sLine + kk.get(i) + ",";
//			  }
//			  sLine = sLine + "\n";
//		      System.out.println(sLine);
		}

//		System.out.println("\n==========Total: " + lnCount + "=====True Count: " + nTrueCnt);
		
		System.out.println("=====True Count: " + nTrueCnt);
		 
		return lstlstlstGroupSet;
	}
	
	
	public static double getTotal(List<Double> lstValues) {
		double fTotalValue = 0.0;
		
		for (Double fValue: lstValues) {
			fTotalValue = fTotalValue + fValue;
		}
		
		return fTotalValue;
	}
	
	
	public static void writeFrameIndexFile(String sFilePathName, List<List<List<Integer>>> lstlstlstGroupIndex) {
		String sLine;
		try {
			FileWriter fwGroupIndex = new FileWriter(sFilePathName, false);   //Overwrite
			
			for (int i=0; i<lstlstlstGroupIndex.size(); i++) {
				List<List<Integer>> lstlstGroupIdx = lstlstlstGroupIndex.get(i);
				
				sLine = "";
				
				for (int j=0; j<lstlstGroupIdx.size(); j++) {
					List<Integer> lstGroupIdx = lstlstGroupIdx.get(j);
					
					for (int k=0; k<lstGroupIdx.size(); k++) {
						if (k != lstGroupIdx.size()-1) {
							sLine = sLine + lstGroupIdx.get(k) + ",";
						} else {
							sLine = sLine + lstGroupIdx.get(k) + ";";
						}
					}
					
				}
				
				sLine = sLine + System.getProperty("line.separator");
		
				fwGroupIndex.write(sLine);
			}
			
			
			fwGroupIndex.close();
		} catch (Exception e) {
			 
		}
		
	}
	

	public static void writeFrameIndexFile(String sFileFolder, int nTotalFrameCnt, int nSubsetSize, List<List<List<Integer>>> lstlstlstGroupIndex) {
		String sLine;
		String sFilePathName = sFileFolder + File.separator + "FrameIndex_" + nTotalFrameCnt + "_" + nSubsetSize + ".csv";
		
		try {
			FileWriter fwGroupIndex = new FileWriter(sFilePathName, false);   //Overwrite
			
			for (int i=0; i<lstlstlstGroupIndex.size(); i++) {
				List<List<Integer>> lstlstGroupIdx = lstlstlstGroupIndex.get(i);
				
				sLine = "";
				
				for (int j=0; j<lstlstGroupIdx.size(); j++) {
					List<Integer> lstGroupIdx = lstlstGroupIdx.get(j);
					
					for (int k=0; k<lstGroupIdx.size(); k++) {
						if (k != lstGroupIdx.size()-1) {
							sLine = sLine + lstGroupIdx.get(k) + ",";
						} else {
							sLine = sLine + lstGroupIdx.get(k) + ";";
						}
					}
					
				}
				
				sLine = sLine + System.getProperty("line.separator");
		
				fwGroupIndex.write(sLine);
			}
			
			
			fwGroupIndex.close();
		} catch (Exception e) {
			 
		}
		
	}
	
	public static List<List<Integer>> getOneGroupIndexSet(String sLine) {
		List<List<Integer>> lstlstGroupIndexSet = new ArrayList<List<Integer>>();
    	String[] wordsGroup = null;
    	
    	if (sLine.length() > 0) {
    		wordsGroup = sLine.split(";");
    	}

    	for (int i=0; i<wordsGroup.length; i++) {
    		List<Integer> lstGroupIndexSet = new ArrayList<Integer>();
    		
    		String[] words = null;
    		
    		words = wordsGroup[i].split(",");
    		
    		for (int j=0; j<words.length; j++) {
    			lstGroupIndexSet.add(Integer.valueOf(words[j]));
    		}
    		
    		lstlstGroupIndexSet.add(lstGroupIndexSet);
    	}
    	
		return lstlstGroupIndexSet;
	}
	
	
	public static List<List<List<Integer>>> getFrameGroupIndex(String sFrameIndexFile) {
		List<List<List<Integer>>> lstlstlstGroupIndexSet = new ArrayList<List<List<Integer>>>();
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		
		try {
			fr = new FileReader(sFrameIndexFile);
			br = new BufferedReader(fr);
		
			while((sLine = br.readLine()) != null) {
				List<List<Integer>> lstlstGroupIndexSet = new ArrayList<List<Integer>>();
				
				lstlstGroupIndexSet = getOneGroupIndexSet(sLine);
				
				lstlstlstGroupIndexSet.add(lstlstGroupIndexSet);
			}
			
			fr.close();
		} catch (Exception e) {
			
		}
		
		return lstlstlstGroupIndexSet;
		
	}
	
	
	//Get Uniformly distributed numbers
	//nMaxValue:  Maximal value of random numbers
	//nNum:  The number of required random numbers	
	public static List<Integer> getUniRandom(int nMaxValue, int nNum) {
		List<Integer> lstnRandomNum = new ArrayList<Integer>();
		
		int nStepSize = 1;
		
		int nStartPos = 1;
		int nPos = 0;
		
		double fNum = 0.0;
		int nRemained = 0;
		
		if (nNum >= nMaxValue) {
			for (int i=1; i<=nMaxValue; i++) {
				lstnRandomNum.add(i);
			}
			
			return lstnRandomNum;
		}
		
		nStepSize = (nMaxValue-1)/(nNum-1);  
		
		nRemained = nMaxValue - nStepSize*(nNum-1);
		
		fNum = Math.random();
		
		nStartPos = 1 + (int)(Math.floor((nRemained*fNum)));

		for (int i=1; i<=nNum; i++) {
			nPos = nStartPos + (i-1)*nStepSize;
			
			lstnRandomNum.add(nPos);
		}
		
		return lstnRandomNum;
	}

	
	public static int getRandom(int nMaxValue) {
		int nRandom;
		
		double fNum = Math.random();
		
		//fNum = generator.nextDouble();
		
		nRandom = 1 + (int)(Math.floor((nMaxValue*fNum)));
		
		return nRandom;
	}
	
	//Get random numbers (sorted)
	//nMaxValue:  Maximal value of random numbers
	//nNum:  The number of required random numbers
	public static List<Integer> getRandom(int nMaxValue, int nNum) {
		int nCnt = 0;
		List<Integer> lstnRandomNum = new ArrayList<Integer>();
		double fNum = 0.0;
		int nRandom;
		int nPos;
		
		if (nNum >= nMaxValue) {
			for (int i=1; i<=nMaxValue; i++) {
				lstnRandomNum.add(i);
			}
			
			return lstnRandomNum;
		}
		
		//Random generator = new Random();
		
		while (nCnt < nNum) {
			fNum = Math.random();
			
			//fNum = generator.nextDouble();
			
			nRandom = 1 + (int)(Math.floor((nMaxValue*fNum)));
			if (lstnRandomNum.contains(Integer.valueOf(nRandom))) continue;
			
			nPos = -1;
			for (int j=0; j<lstnRandomNum.size(); j++) {
				if (nRandom < lstnRandomNum.get(j)) {
					nPos = j;
					break;
				}
			}
			
			if (nPos == -1) {
				lstnRandomNum.add(nRandom);
			} else {
				lstnRandomNum.add(nPos, nRandom);
			}
			nCnt = nCnt + 1;
		}
				
		return lstnRandomNum;
	}


	//Get random numbers
	//nMaxValue:  Maximal value of random numbers
	//nNum:  The number of required random numbers
	public static List<Integer> getRandom(int nMaxValue, int nNum, List<Integer> lstnExistingVal) {
		int nCnt = 0;
		List<Integer> lstnRandomNum = new ArrayList<Integer>();
		double fNum = 0.0;
		int nRandom;
		int nPos;
		int nToGenerate = 0;		
		
		if (nNum >= nMaxValue) {
			for (int i=1; i<=nMaxValue; i++) {
				lstnRandomNum.add(i);
			}
			
			return lstnRandomNum;
		}
		
		if (lstnExistingVal == null || lstnExistingVal.size() == 0) {
			nToGenerate = nNum;
		} else {
			nToGenerate = nNum - lstnExistingVal.size();
			for (int i=0; i<lstnExistingVal.size(); i++) {
				lstnRandomNum.add(lstnExistingVal.get(i));
			}
		}
		
		while (nCnt < nToGenerate) {
			fNum = Math.random();
			
			nRandom = 1 + (int)(Math.floor((nMaxValue*fNum)));
			
			if (lstnRandomNum.contains(Integer.valueOf(nRandom))) continue;
			
			nPos = -1;
			for (int j=0; j<lstnRandomNum.size(); j++) {
				if (nRandom < lstnRandomNum.get(j)) {
					nPos = j;
					break;
				}
			}
			
			if (nPos == -1) {
				lstnRandomNum.add(nRandom);
			} else {
				lstnRandomNum.add(nPos, nRandom);
			}
			nCnt = nCnt + 1;
		}
				
		return lstnRandomNum;
	}
	
	
	public static boolean IsSameRandomList(List<Integer> lstnTmp1, List<Integer> lstnTmp2) {
		boolean bSame = true;
		int nTmp1, nTmp2;
		
		if (lstnTmp1.size() != lstnTmp2.size()) return false;
				
		for (int i=0; i<lstnTmp1.size(); i++) {
			nTmp1 = lstnTmp1.get(i);
			nTmp2 = lstnTmp2.get(i);
			
			if (nTmp1 != nTmp2) {
				bSame = false;
				break;
			}
		}
		
		return bSame;
	}
	
	
	public static List<List<Integer>> getRandomNumSet(int nTotalNum, int nSubsetSize, int nSubsetCnt) {
		List<List<Integer>> lstlstRandomNumSet = new ArrayList<List<Integer>>();
		List<Integer> lstRandomNum = new ArrayList<Integer>();
		boolean bExisting = false;
	
		
		while(lstlstRandomNumSet.size() < nSubsetCnt) {
			lstRandomNum = getRandom(nTotalNum, nSubsetSize);
			
			bExisting = false;
			
			for (int i=0; i<lstlstRandomNumSet.size(); i++) {
				List<Integer> lstTmpRandomNum = lstlstRandomNumSet.get(i);
				
				bExisting = IsSameRandomList(lstRandomNum, lstTmpRandomNum);
				if (bExisting == true) break;
			}
			
			if (bExisting == false) {
				lstlstRandomNumSet.add(lstRandomNum);
			}
			
		}
		
		return lstlstRandomNumSet;		
	}

	
	public static void PrintList(List<Integer> lstnVal) {
		
		for (int i=0; i<lstnVal.size(); i++) {
			System.out.println("[" + String.format("%2d", (i+1)) + "] === " +  String.format("%3d", lstnVal.get(i)));
		}
		
		System.out.println("\n");
	}
	
	
	//This function remove the postfix of from the word
	//This is used to eliminate the ".com", ".edu", ".org"... from the word
	public static String getPureWord(String sWord) {
		String sPureWord = "";
		int nPos = -1;
		
		for (int i=0; i<m_strPostfixCommon.length; i++) {
			nPos = sWord.toLowerCase().indexOf(m_strPostfixCommon[i]);
			if (nPos != -1) break;
		}
		
		if (nPos == -1) {
			for (int j=0; j<m_strPostfix.length; j++) {
				nPos = sWord.toLowerCase().indexOf(m_strPostfix[j]);
				if (nPos != -1) break;
			}
		}
		
		if (nPos == -1) {		
			sPureWord = sWord;
		} else {
			sPureWord = sWord.substring(0, nPos);
		}
		
		return sPureWord;
	}
	
	
	//Remove unnecessary information fromnthe content, these removed information could affect the noun/proper name extraction.
	//So call this function before doing noun/proper name extraction.
	public static String purifyContent(String sContent) {
		String sPureContent = "";
		int i;
		
		sPureContent = sContent + " ";   //Add the last " " to help in elimiating .com like postfix it comes at last (i.e. be the last part of the whole string

		for (i=0; i<m_strPrefix.length; i++) {
			sPureContent = sPureContent.replace(m_strPrefix[i], " ");
		}
		
		for (i=0; i<m_strPostfix.length; i++) {
			sPureContent = sPureContent.replace(m_strPostfix[i], " ");
		}
		
		for (i=0; i<m_strPostfixCommon.length; i++) {
			sPureContent = sPureContent.replaceAll(m_strPostfixCommon[i] + "[^a-zA-Z]", " ");
		}

		// "-" is not included, because many words use this as part of the words, such as "built-in", "Blue-ray"
		sPureContent = sPureContent.replaceAll("[`~!@#$%^&*()_+=\\{}\\[\\]|;':\",./<>?]", " ");
		
		return sPureContent;
	}
	
	
	//Check whether the given word is on the blacklist for keyword
	public static boolean isKeywordBlackListed(String sWord) {
		boolean bRet = false;
		int i;
		
		for (i=0; i<PREP_BLACKLIST.length; i++) {
			if (sWord.compareToIgnoreCase(PREP_BLACKLIST[i]) == 0) {
				bRet = true;
				break;
			}
		}
		
		if (bRet == false) {
			for (i=0; i<INDEF_PRONOUN_BE_VERB.length; i++) {
				if (sWord.compareToIgnoreCase(INDEF_PRONOUN_BE_VERB[i]) == 0) {
					bRet = true;
					break;
				}
			}			
		}
	
		if (bRet == false) {
			for (i=0; i<SEASON_BLACKLIST.length; i++) {
				if (sWord.compareToIgnoreCase(SEASON_BLACKLIST[i]) == 0) {
					bRet = true;
					break;
				}
			}	
		}
		
		return bRet;

	}
	
	public static String getStringWithRequiredLen(String sContent, int nLen) {
		int nStrLen = sContent.length();
		String sResult = sContent;
		String sTmp = " ";
		
		if (nStrLen >= nLen) return sResult;
		
		for (int i=1; i<= nLen - nStrLen; i++) {
			sResult = sResult + sTmp;
		}
		
		return sResult;
	}
	
	
	//Get Placename, Meta Keyword, Content Keyword from XXX_Web_Text.csv
	public static String getFullWebPlaceNameKeyword(String sFilePathName, List<String> lstMetaKeyword, List<String> lstKeyword, List<Double> lstKeywordWeight) {				
    	FileReader fr = null;
    	BufferedReader br = null;
    	String[] sField = null;
    	String sLine = "";
    	int nLineIdx = 0;
    	String sPlaceName = "";
    	    	
		try { 
			File fl = new File(sFilePathName);

	    	if (fl.exists() == false) {
	    		return sPlaceName;
	    	}
			
			fr = new FileReader(sFilePathName);
			br = new BufferedReader(fr);
		
			while ((sLine = br.readLine()) != null) {
				nLineIdx = nLineIdx + 1;
								
				if (nLineIdx == 1) {   // Place Name
					sPlaceName = sLine.substring(FULL_WEBTEXT_LINE_PRE_PLACENAME.length());
				} else if (nLineIdx == 3) {  //Meta Keyword
					String sMetas = sLine.substring(FULL_WEBTEXT_LINE_PRE_META.length()).trim();
					
					if (sMetas.length() > 0) {
						sField = sMetas.split(",");
						
						for (int i=0; i<sField.length; i++) {
							lstMetaKeyword.add(sField[i].trim());
						}
					}

					//lstMetaKeyword = getFieldsList(sMetas);
				} else if (nLineIdx == 2 || nLineIdx == 4 || nLineIdx == 5) {
					
				} else {
					sField = getFields(sLine);
										
					if (sField.length == 3) {
						lstKeyword.add(sField[0].trim());
						lstKeywordWeight.add(Double.valueOf(sField[2].trim()));
					}
					
				}
				
			}
			
			fr.close();
		} catch (Exception e) {
			
		}

		return sPlaceName;
	}
	
	
	//Get OCRed word/weight from MergedOCR_FULL.csv
	public static void getInStoreOCRedWordWeight(String sFilePathName, List<String> lstWord, List<Double> lstWordWeight) {				
    	FileReader fr = null;
    	BufferedReader br = null;
    	String[] sField = null;
    	String sLine = "";
    	int nLineIdx = 0;
    	    	
		try {
			File fl = new File(sFilePathName);

	    	if (fl.exists() == false) {
	    		return;
	    	}
			
			fr = new FileReader(sFilePathName);
			br = new BufferedReader(fr);
		
			while ((sLine = br.readLine()) != null) {
				nLineIdx = nLineIdx + 1;
								
				sField = getFields(sLine);
								
				if (sField.length == 3) {
					lstWord.add(sField[0].trim());
					lstWordWeight.add(Double.valueOf(sField[2].trim()));
				}
									
			}
			
			fr.close();
		} catch (Exception e) {
			
		}

	}
	

	public static String getWebTextFileNameFullPath(String sParentFolder, String sWebTextFileName) {
		String sWebTextFileNameFullPath = sParentFolder + File.separator + WEB_TEXT_FOLDER + File.separator + sWebTextFileName;
		
		return sWebTextFileNameFullPath;
	}

	public static String getFullWebTextFileNameFullPath(String sParentFolder, String sWebTextFileName) {
		String sFullWebTextFileNameFullPath = "";
		String sPlaceName = getPlaceNameFromWebTextFileName(sWebTextFileName);
		
		sFullWebTextFileNameFullPath = sParentFolder + File.separator + FULL_WEB_TEXT_FOLDER + File.separator + sPlaceName + "_Full" + WEB_TEXT_FILE_POST;
		
		return sFullWebTextFileNameFullPath;
	}
	
	public static String getPlaceNameFromWebTextFileName(String sWebTextFileName) {
		int nPos = sWebTextFileName.indexOf(WEB_TEXT_FILE_POST);
		String sPlaceName =  sWebTextFileName.substring(0, nPos);

	    return sPlaceName;
	}
	
	public static String getPlaceNameFromFullWebTextFileName(String sFullWebTextFilePathName) {
		String sPlaceName = "";
		File fl = new File(sFullWebTextFilePathName);
		String sFileName = fl.getName();
		
		sPlaceName = getPlaceNameFromWebTextFileName(sFileName);
		
		return sPlaceName;
	}
	
	
	//Get the full path filename of Web image Text file corresponding to the web text file name
	public static String getWebImageTextFileNameFullPath(String sParentFolder, String sWebTextFileName) {
		String sWebImageTextFileNameFullPath = "";
		String sPlaceName = getPlaceNameFromWebTextFileName(sWebTextFileName);

		sWebImageTextFileNameFullPath = sParentFolder + File.separator + WEB_IMAGE_TEXT_FOLDER + File.separator + sPlaceName + WEB_IMAGE_FILE_POST;
		
		return sWebImageTextFileNameFullPath;
	}
	
	
	public static List<String> getFullWebTextFileList(String sParentFolder) {
		List<String> lstsWebTextFileList = null;
		String sWebTextFileFolder = sParentFolder + File.separator + FULL_WEB_TEXT_FOLDER;
		File flFolder;
		
		flFolder = new File(sWebTextFileFolder);
		try {
			File[] flarrFileList = flFolder.listFiles();
			if (flarrFileList != null && flarrFileList.length > 0) {
				lstsWebTextFileList = new ArrayList<String>();
				
				for (int i=0; i<flarrFileList.length; i++) {
					//lstsWebTextFileList.add(flarrFileList[i].getName());
					lstsWebTextFileList.add(flarrFileList[i].getAbsolutePath());

				}
				
			}
		} catch (Exception e) {
			
		}
		
		return lstsWebTextFileList;
		
	}
	
	public static List<String> getWebTextFileList(String sParentFolder) {
		List<String> lstsWebTextFileList = null;
		String sWebTextFileFolder = sParentFolder + File.separator + WEB_TEXT_FOLDER;
		File flFolder;
		
		flFolder = new File(sWebTextFileFolder);
		try {
			File[] flarrFileList = flFolder.listFiles();
			if (flarrFileList != null && flarrFileList.length > 0) {
				lstsWebTextFileList = new ArrayList<String>();
				
				for (int i=0; i<flarrFileList.length; i++) {
					lstsWebTextFileList.add(flarrFileList[i].getName());
				}
				
			}
		} catch (Exception e) {
			
		}
		
		return lstsWebTextFileList;
		
	}
	
	public static String getWebTextFolder(String sParentFolder) {
		String sWebTextFolder = sParentFolder + File.separator + WEB_TEXT_FOLDER;
		
		return sWebTextFolder;
	}
	
	
	public static String getWebImageFolder(String sParentFolder) {
		String sWebImageFolder = sParentFolder + File.separator + WEB_IMAGE_FOLDER;
		
		return sWebImageFolder;
	}
	
	
	public static String getWebImageTextFolder(String sParentFolder) {
		String sWebImageTextFolder = sParentFolder + File.separator + WEB_IMAGE_TEXT_FOLDER;
		
		return sWebImageTextFolder;
	}
	
	
	public static String getPlaceKeywordFile(String sParentFolder, String sGpsCoordinates) {
		String sPlaceKeywordFile = sParentFolder + File.separator + PLACE_KEYWORD_FILE_PRE;
		String[] sarrGps = sGpsCoordinates.split(",");
		
		sPlaceKeywordFile = sPlaceKeywordFile + "_" + sarrGps[0] + "_" + sarrGps[1] + ".csv";
		
		return sPlaceKeywordFile;
	}
	
	
	public static String getPlaceKeywordFile(String sParentFolder) {
		String sPlaceKeywordFile = "";
		
		File dir = new File(sParentFolder);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(PLACE_KEYWORD_FILE_PRE);
			}
		});
		
		if (files != null && files.length > 0) {
			sPlaceKeywordFile = files[0].getName();
			sPlaceKeywordFile = sParentFolder + File.separator + sPlaceKeywordFile;
		}
		
		return sPlaceKeywordFile;
	}
	
	
	public static String getPlaceKeywordFile(String sParentDataFolder, double fMetaWeight, double fContentWeight, double fImageWeight, int nTopFinalKeywordNum) {
		String sPlaceKeywordFile = sParentDataFolder + File.separator + PLACE_KEYWORD_FILE_PRE;
		
//		if (USE_WEB_META_KEYWORD) {
//			sPlaceKeywordFile = sPlaceKeywordFile + "_1_" + fMetaWeight;
//		} else {
//			sPlaceKeywordFile = sPlaceKeywordFile + "_0_" + fMetaWeight;
//		}
//		
//		sPlaceKeywordFile = sPlaceKeywordFile + "_1_" + fContentWeight;
//		
//		if (USE_WEB_IMAGE_TEXT) {
//			sPlaceKeywordFile = sPlaceKeywordFile + "_1_" + fImageWeight;
//		} else {
//			sPlaceKeywordFile = sPlaceKeywordFile + "_0_" + fImageWeight;
//		}
		
		sPlaceKeywordFile = sPlaceKeywordFile + "_" +  nTopFinalKeywordNum + ".csv";
		
		return sPlaceKeywordFile;
	}
	
	
	//Get the webimage place sub folder (i.e. the folder which stores the web image for a given store)
	public static String getWebImagePlaceSubFolder(String sParentFolder, String sPlaceName) {	
		String sWebImagePlaceSubFolder = sParentFolder + File.separator + WEB_IMAGE_FOLDER  + File.separator + sPlaceName;
		
		return sWebImagePlaceSubFolder;
	}
	

	//Get the web text file path name (i.e. the name of the file which stores the Store name, GPS, Meta keyword, Top N Web Cotent Keywords+Weight)
	public static String getWebTextFilePathName(String sParentFolder, String sPlaceName) {	
		String sWebTextFilePathName = sParentFolder + File.separator + WEB_TEXT_FOLDER  + File.separator + sPlaceName + WEB_TEXT_FILE_POST;
		
		return sWebTextFilePathName;
	}

	//[FULL BEGIN]******************************************
	//Get the web text file path name (i.e. the name of the file which stores the Store name, GPS, Meta keyword, Top N Web Cotent Keywords+Weight)
	public static String getWebTextFilePathName_Full(String sParentFolder, String sPlaceName) {	
		String sWebTextFilePathName_Full = sParentFolder + File.separator + FULL_WEB_TEXT_FOLDER  + File.separator + sPlaceName + WEB_TEXT_FILE_POST;
		
		return sWebTextFilePathName_Full;
	}
	//[FULL END]********************************************
	
	//Get the webimage place sub folder (i.e. the folder which stores the web image for a given store)
	public static String getWebImageTextFilePathName(String sParentFolder, String sPlaceName) {	
		String sWebImageTextFilePathName = sParentFolder + File.separator + WEB_IMAGE_FOLDER  + File.separator + sPlaceName + WEB_IMAGE_FILE_POST;
		
		return sWebImageTextFilePathName;
	}

	
	public static void createFolder(String sParentFolder, String sSubFolderName) {
		String sSubFolder = sParentFolder + File.separator + sSubFolderName;
		
		File flFolder;
		
		flFolder = new File(sSubFolder);
		if (!flFolder.exists()) {
			//Does not exist, create it
			if (flFolder.mkdir()) {

			} else {

			}
		}
		
		return;
	}
	

	public static void createWebImagePlaceSubFolder(String sParentFolder, String sPlaceName) {
		String sSubFolder = sParentFolder + File.separator + WEB_IMAGE_FOLDER  + File.separator + sPlaceName;
		
		File flFolder;
		
		flFolder = new File(sSubFolder);
		if (!flFolder.exists()) {
			//Does not exist, create it
			if (flFolder.mkdir()) {

			} else {

			}
		}
		
		return;
	}
	
	
	//This function is used to create the subfolder and final PlaceKeyword file in the given folder
	public static void createFolderContent(String sParentFolder, String sGpsCoordinates, int nTopKeywordNum) {
		String sWebTextFolder = sParentFolder + File.separator + WEB_TEXT_FOLDER;
		String sWebImageFolder = sParentFolder + File.separator + WEB_IMAGE_FOLDER;
		String sWebImageTextFolder = sParentFolder + File.separator + WEB_IMAGE_TEXT_FOLDER;
		String sPlaceKeywordFile = sParentFolder + File.separator + PLACE_KEYWORD_FILE_PRE;
		
		//[FULL BEGIN]***************************
		String sWebTextFolder_Full = sParentFolder + File.separator + FULL_WEB_TEXT_FOLDER;
		//[FULL END]*****************************
		
    	File flFolder, flFile;

		flFolder = new File(sWebTextFolder);
		if (!flFolder.exists()) {
			//Does not exist, create it
			if (flFolder.mkdir()) {

			} else {

			}
		}
		
		flFolder = new File(sWebImageFolder);
		if (!flFolder.exists()) {
			//Does not exist, create it
			if (flFolder.mkdir()) {

			} else {

			}
		}

		flFolder = new File(sWebImageTextFolder);
		if (!flFolder.exists()) {
			//Does not exist, create it
			if (flFolder.mkdir()) {

			} else {

			}
		}
		
		if (sGpsCoordinates.length() > 0) {
			String[] sarrGps = sGpsCoordinates.split(",");
		
			sPlaceKeywordFile = sPlaceKeywordFile + "_" + nTopKeywordNum + "_" + sarrGps[0] + "_" + sarrGps[1] + ".csv";
		} else {
			sPlaceKeywordFile = sPlaceKeywordFile + "_" + nTopKeywordNum + ".csv";
		}
		
//		flFile = new File(sPlaceKeywordFile);
//		if (!flFile.exists()) {
//			try {
//				FileWriter fwPlaceKeywordFile = new FileWriter(sPlaceKeywordFile, false);   //Overwrite
//				 
//				fwPlaceKeywordFile.close();
//			} catch (Exception e) {
//				 
//			}
//			
//		}
		
		//[FULL BEGIN]***************************
		flFolder = new File(sWebTextFolder_Full);
		if (!flFolder.exists()) {
			//Does not exist, create it
			if (flFolder.mkdir()) {

			} else {

			}
		}
		//[FULL END]*****************************
		
		return;
		
	}
	
	public static int getMaxInt(List<Integer> lstValue) {
		int nMax = -99999;
		
		for (int i=0; i<lstValue.size(); i++) {
			if (nMax < lstValue.get(i).intValue()) {
				nMax = lstValue.get(i).intValue();
			}
		}
		
		return nMax;
	}

	public static double getMaxDouble(List<Double> lstValue) {
		double dMax = -99999;
		
		for (int i=0; i<lstValue.size(); i++) {
			if (dMax < lstValue.get(i).doubleValue()) {
				dMax = lstValue.get(i).doubleValue();
			}
		}
		
		return dMax;
	}
	
	public static String[] getFields_Special(String sLine) {
		List<Integer> lstPos = new ArrayList<Integer>();
		lstPos = getSeparatePos(sLine);
		String[] fields = new String[lstPos.size()];
		int nStartPos, nStopPos;
		
		nStartPos = -1;
		
		for (int i=0; i<lstPos.size(); i++) {
			nStopPos = lstPos.get(i);
			
			if (nStopPos - nStartPos <= 1) {
				fields[i] = "";
			} else {
				fields[i] = sLine.substring(nStartPos+1, nStopPos);
			}
			
			nStartPos  = nStopPos;
		}

		
		return fields;
	}	
	
	public static List<Integer> getSeparatePos(String sLine) {
		List<Integer> lstSeparatePos = new ArrayList<Integer>();
		int nLen = sLine.length();
		String sTmpLine = "";
		int nPos = -1;
		String sSeparate = ",";
		int nStartPos = 0;
	
		nPos = sLine.indexOf(sSeparate, nStartPos);
		
		while (nPos != -1) {
			lstSeparatePos.add(nPos);
			nStartPos = nPos + 1;
			nPos = sLine.indexOf(sSeparate, nStartPos);
		}
		
		return lstSeparatePos;
	}	
	
	public static String[] getFields(String sLine) {
		String[] fields = null;
		
		if (sLine.length() > 0) {
			fields = sLine.split(",");
		}
		
		return fields;
	}

	public static List<String> getFieldsList(String sLine) {
		List<String> lstFields = null;
		String[] fields = null;
		
		if (sLine.length() > 0) {
			fields = sLine.split(",");
			lstFields = new ArrayList<String>();
			for (int i=0; i<fields.length; i++) {
				lstFields.add(fields[i].trim());
			}
		}
		
		return lstFields;
	}
	
	public static ArrayList<String> getFieldsArrayList(String sLine) {
		List<String> lstFields = null;
		String[] fields = null;
		
		if (sLine.length() > 0) {
			fields = sLine.split(",");
			lstFields = new ArrayList<String>();
			for (int i=0; i<fields.length; i++) {
				lstFields.add(fields[i].trim());
			}
		}
		
		return (ArrayList<String>) (lstFields);
	}

	
	public static List<Double> getFieldsListValue(String sLine) {
		List<Double> lstFields = null;
		String[] fields = null;
		
		if (sLine.length() > 0) {
			fields = sLine.split(",");
			lstFields = new ArrayList<Double>();
			for (int i=0; i<fields.length; i++) {
				lstFields.add(Double.valueOf(fields[i].trim()));
			}
		}
		
		return lstFields;
	}
	
	
	public static double calFieldsSimilarity(String sFieldsA, String sFieldsB) {
		String[] wordsA = null;
		String[] wordsB = null;
		double fSimilarity = 0.0;
		int nCount = 0;
		int i;
		
		if (sFieldsA.trim().length() == 0 || sFieldsB.trim().length() == 0) return fSimilarity;
		
		wordsA = getFields(sFieldsA);
		wordsB = getFields(sFieldsB);
		
		if (wordsA.length != wordsB.length || wordsA.length == 0 || wordsB.length == 0) {
			return fSimilarity;
		}
	
		for (i=0; i<wordsA.length; i++) {
			if (wordsA[i].compareToIgnoreCase(wordsB[i]) ==0) {
				nCount = nCount + 1;
			}
		}
		
		fSimilarity = nCount*1.0/wordsA.length;
		return fSimilarity;
	}
	
	//sFieldsGTA: GT--Ground Truth
	public static double calFieldsSimilarityS(String sFieldsGTA, String sFieldsPredicatedB, List<Integer> lstFrameStat) {
		String[] wordsGTA = null;
		String[] wordsPredicatedB = null;
		double fSimilarity = 0.0;
		int nMatchedCount = 0;
		int nTotalValidCount = 0;
		int i;
		
		if (sFieldsGTA.trim().length() == 0 || sFieldsPredicatedB.trim().length() == 0) return fSimilarity;
		
		wordsGTA = getFields(sFieldsGTA);
		wordsPredicatedB = getFields(sFieldsPredicatedB);
		
		if (wordsGTA.length != wordsPredicatedB.length || wordsGTA.length == 0 || wordsPredicatedB.length == 0) {
			return fSimilarity;
		}
	
		for (i=0; i<wordsPredicatedB.length; i++) {
			if (wordsPredicatedB[i].compareToIgnoreCase("null") == 0) {
				continue;
			} 
			
			nTotalValidCount = nTotalValidCount + 1;
			
			if (wordsPredicatedB[i].compareToIgnoreCase(wordsGTA[i]) == 0) {  //Correct match
				nMatchedCount = nMatchedCount + 1;
			}
		}
		
		lstFrameStat.add(nMatchedCount);
		lstFrameStat.add(nTotalValidCount);
		
		if (nTotalValidCount == 0) {
			fSimilarity = 0;
		} else {
			fSimilarity = nMatchedCount*1.0/nTotalValidCount;
		}
		
		return fSimilarity;
	}
	
	
	public static String[] splitString(String strContent) {
		String delims = "[ `~!@#$%^&*()_+=\\{}\\[\\]|;':\",./<>?]+";   // "-" is not included, because many words use this as part of the words, such as "built-in"
    	String[] words = null;
    	
    	if (strContent.length() > 0) {
    		words = strContent.split(delims);
    	}
    	
    	return words;
    }

	public static List<String> splitStringList(String strContent) {
		String delims = "[ `~!@#$%^&*()_+=\\{}\\[\\]|;':\",./<>?]+";   // "-" is not included, because many words use this as part of the words, such as "built-in"
    	String[] words = null;
    	List<String> lstWords = null;
    	
    	if (strContent.length() > 0) {
    		words = strContent.split(delims);
    		
    		if (words.length > 0) {
    			lstWords = new ArrayList<String>();
    			for (int i=0; i<words.length; i++) {
    				lstWords.add(words[i].toLowerCase().trim());
    			}
    		}
    	}
    	
    	return lstWords;
    }

	//Split the whole string into phrases (phrases are separated by "|" and ","
	public static String[] splitStringPhrase(String strContent) {
		String delims = "[|,]+";   // "-" is not included, because many words use this as part of the words, such as "built-in"
    	String[] words = null;
    	
    	if (strContent.length() > 0) {
    		words = strContent.split(delims);
    	}
    	
    	return words;
    }
	
	
	//Get the list of Place AP database file under the given folder
	public static List<String> getPlaceApDatabaseFileList(String sParentFolder) {
		List<String> lstDBFileList = null;
		
	   	File flFolder;
		flFolder = new File(sParentFolder);
		
		if (flFolder.exists()) {
			File[] files = flFolder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.startsWith(PLACE_AP_DB_FILE_PRE);
				}
			});

			if (files != null && files.length > 0) {
				lstDBFileList = new ArrayList<String>();
				
				for (int i=0; i<files.length; i++) {
					lstDBFileList.add(files[i].getAbsolutePath());
				}
			}
		
		}
		
		return lstDBFileList;
	}
	
	
	//Get all the filename/file pull path name of the files under the given folder
	public static void getFileList(String sParentFolder, List<String> lstFileNameList, List<String> lstFileFullPathList) {
	   	File flFolder;
	    String sFullPath;
		flFolder = new File(sParentFolder);
		
		if (flFolder.exists()) {
			File[] flFiles = null;
			
			flFiles = flFolder.listFiles();
			
			if (flFiles != null) {

				for (int i=0; i<flFiles.length; i++) {
					if (flFiles[i].isFile()) {
						lstFileNameList.add(flFiles[i].getName());
						lstFileFullPathList.add(flFiles[i].getAbsolutePath());						
					}
				}
			}
		}
		
	}
	

	//Get all the filename/file pull path name of the files under the given folder
	public static void getOCRFileList(String sParentFolder, List<String> lstFileNameList, List<String> lstFileFullPathList, boolean bFullText) {
	   	File flFolder;
	    String sFullPath;
		flFolder = new File(sParentFolder);
		
		if (flFolder.exists()) {
			File[] flFiles = null;
			
			flFiles = flFolder.listFiles();
			
			if (flFiles != null) {

				for (int i=0; i<flFiles.length; i++) {
					if (flFiles[i].isFile()) {
						if (bFullText == true) {
							if (flFiles[i].getName().startsWith(FULLTEXT_OCR)) {
								lstFileNameList.add(flFiles[i].getName());
								lstFileFullPathList.add(flFiles[i].getAbsolutePath());								
							}
						} else {
							if (flFiles[i].getName().startsWith(FULLTEXT_OCR) == false) {
								lstFileNameList.add(flFiles[i].getName());
								lstFileFullPathList.add(flFiles[i].getAbsolutePath());								
							}							
						}
					}
				}
			}
		}
		
	}
	
	
	//Get the sub folder (name and full path) under the given parent folder (only one layer)
	public static void getSubFolderList(String sParentFolder, List<String> lstSubFolderNameList, List<String> lstSubFolderFullPathList) {
	   	File flFolder;
	    String sFullPath;
		flFolder = new File(sParentFolder);
		
		if (flFolder.exists()) {
			String[] sFileFolders = null;
			
			sFileFolders = flFolder.list();
			
			if (sFileFolders != null) {

				for (int i=0; i<sFileFolders.length; i++) {
					sFullPath = sParentFolder + File.separator + sFileFolders[i];
					
					File flTmp = new File(sFullPath);
					
					if (flTmp.isDirectory()) {

						lstSubFolderNameList.add(sFileFolders[i]);
						lstSubFolderFullPathList.add(sFullPath);
					}
				}
			}
			
		}
		
		return;
	}

	
	//This function get the list of MergedOCR_FULL.csv file (with full path) and the corresponding ground truth place file (GroundTruth.csv) from each sub place folder under the crowdsourced top parent folder
	public static void getCrowdsourcedFullMergedGroundTruthFiles(String sTopParentFolder, List<String> lstMergedFullOCRFiles, List<String> lstGroundTruthFiles, int nFrameCnt) {
		String sMergedFullOCRFile = "";
		String sGroundTruthFile = "";
		List<String> lstSubFolderNameList = new ArrayList<String>();
		List<String> lstSubFolderFullPathList = new ArrayList<String>();
		int i;
		File fl_fullOCRedText, fl_groundTruth;
		
		getSubFolderList(sTopParentFolder, lstSubFolderNameList, lstSubFolderFullPathList);
		
		if (lstSubFolderFullPathList.size() == 0) return;
		
		for (String sFolderFullPath : lstSubFolderFullPathList) {
			if (nFrameCnt == NO_FRAME_SAMPLE) {
				sMergedFullOCRFile = sFolderFullPath + File.separator + ONE_PLACE_MERGED_OCR_RESULT_FILE_FULL_PRE + ".csv";
			} else {
				sMergedFullOCRFile = sFolderFullPath + File.separator + ONE_PLACE_MERGED_OCR_RESULT_FILE_FULL_PRE + "_" + nFrameCnt + ".csv";				
			}
			
			sGroundTruthFile = sFolderFullPath + File.separator + ONE_PLACE_GROUND_TRUTH_FILE;
			
			fl_fullOCRedText = new File(sMergedFullOCRFile);
			fl_groundTruth = new File(sGroundTruthFile);
			
			if (fl_fullOCRedText.exists() == false || fl_groundTruth.exists() == false) continue;
			
			lstMergedFullOCRFiles.add(sMergedFullOCRFile);
			
			lstGroundTruthFiles.add(sGroundTruthFile);
		}
		
	}

	//Get the sub folder (name and full path) under the given parent folder (only one layer)
	public static void getSubFolderList_StoreOCR(String sParentFolder, List<String> lstSubFolderNameList, List<String> lstSubFolderFullPathList) {
	   	File flFolder;
	    String sFullPath;
		flFolder = new File(sParentFolder);
		boolean bIgnore = false;
		
		if (flFolder.exists()) {
			String[] sFileFolders = null;
			
			sFileFolders = flFolder.list();
			
			if (sFileFolders != null) {

				for (int i=0; i<sFileFolders.length; i++) {
					
					bIgnore = false;
					//Ignore some In-Store OCR Folder
					for (int j=0; j<m_strIgnoreOCRFolders.length; j++) {
						if (m_strIgnoreOCRFolders[j].compareToIgnoreCase(sFileFolders[i]) == 0) {
							bIgnore = true;
							break;
						}
					}
					
					if (bIgnore == true) continue;
					
					sFullPath = sParentFolder + File.separator + sFileFolders[i];
					
					File flTmp = new File(sFullPath);
					
					if (flTmp.isDirectory()) {

						lstSubFolderNameList.add(sFileFolders[i]);
						lstSubFolderFullPathList.add(sFullPath);
					}
				}
			}
			
		}
		
		return;
	}
	

	//This function get the list of MergedOCR_FULL.csv file (with full path) and the corresponding ground truth place file (GroundTruth.csv) from each sub place folder under the crowdsourced top parent folder
	public static void getCrowdsourcedFullMergedGroundTruthFiles_StoreOCR(String sTopParentFolder, List<String> lstMergedFullOCRFiles, List<String> lstGroundTruthFiles, int nFrameCnt) {
		String sMergedFullOCRFile = "";
		String sGroundTruthFile = "";
		List<String> lstSubFolderNameList = new ArrayList<String>();
		List<String> lstSubFolderFullPathList = new ArrayList<String>();
		int i;
		File fl_fullOCRedText, fl_groundTruth;
		
		getSubFolderList_StoreOCR(sTopParentFolder, lstSubFolderNameList, lstSubFolderFullPathList);
		
		if (lstSubFolderFullPathList.size() == 0) return;
		
		for (String sFolderFullPath : lstSubFolderFullPathList) {
			if (nFrameCnt == NO_FRAME_SAMPLE) {
				sMergedFullOCRFile = sFolderFullPath + File.separator + ONE_PLACE_MERGED_OCR_RESULT_FILE_FULL_PRE + ".csv";
			} else {
				sMergedFullOCRFile = sFolderFullPath + File.separator + ONE_PLACE_MERGED_OCR_RESULT_FILE_FULL_PRE + "_" + nFrameCnt + ".csv";				
			}
			
			sGroundTruthFile = sFolderFullPath + File.separator + ONE_PLACE_GROUND_TRUTH_FILE;
			
			fl_fullOCRedText = new File(sMergedFullOCRFile);
			fl_groundTruth = new File(sGroundTruthFile);
			
			if (fl_fullOCRedText.exists() == false || fl_groundTruth.exists() == false) continue;
			
			lstMergedFullOCRFiles.add(sMergedFullOCRFile);
			
			lstGroundTruthFiles.add(sGroundTruthFile);
		}
		
	}
	
	
	//This function get the list of MergedGpsApOCR.csv file (with full path) and the corresponding ground truth place file (GroundTruth.csv) from each sub place folder under the crowdsourced top parent folder
	public static void getCrowdsourcedMergedGroundTruthFiles(String sTopParentFolder, List<String> lstMergedGpsApOCRFiles, List<String> lstGroundTruthFiles, int nFrameCnt) {
		String sMergedGpsApOCRFile = "";
		String sGroundTruthFile = "";
		List<String> lstSubFolderNameList = new ArrayList<String>();
		List<String> lstSubFolderFullPathList = new ArrayList<String>();
		int i;
		
		getSubFolderList(sTopParentFolder, lstSubFolderNameList, lstSubFolderFullPathList);
		
		if (lstSubFolderFullPathList.size() == 0) return;
		
		for (String sFolderFullPath : lstSubFolderFullPathList) {
			if (nFrameCnt == NO_FRAME_SAMPLE) {
				sMergedGpsApOCRFile = sFolderFullPath + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + ".csv";
			} else {
				sMergedGpsApOCRFile = sFolderFullPath + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + "_" + nFrameCnt + ".csv";				
			}
			
			lstMergedGpsApOCRFiles.add(sMergedGpsApOCRFile);
			
			sGroundTruthFile = sFolderFullPath + File.separator + ONE_PLACE_GROUND_TRUTH_FILE;
			lstGroundTruthFiles.add(sGroundTruthFile);
		}
		
	}

	//This function get the list of MergedGpsApOCR.csv file (with full path) and the corresponding ground truth place file (GroundTruth.csv) from each sub place folder under the crowdsourced top parent folder
	public static void getCrowdsourcedMergedGroundTruthFiles_MultipleIndexSet(String sTopParentFolder, List<String> lstMergedGpsApOCRFiles, List<String> lstGroundTruthFiles, int nFrameCnt, int nSetIndex) {
		String sMergedGpsApOCRFile = "";
		String sGroundTruthFile = "";
		List<String> lstSubFolderNameList = new ArrayList<String>();
		List<String> lstSubFolderFullPathList = new ArrayList<String>();
		int i;
		
		getSubFolderList(sTopParentFolder, lstSubFolderNameList, lstSubFolderFullPathList);
		
		if (lstSubFolderFullPathList.size() == 0) return;
		
		for (String sFolderFullPath : lstSubFolderFullPathList) {
			if (nFrameCnt == NO_FRAME_SAMPLE) {
				sMergedGpsApOCRFile = sFolderFullPath + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + ".csv";
			} else {
				sMergedGpsApOCRFile = sFolderFullPath + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + "_" + nFrameCnt + "_" + nSetIndex + ".csv";				
			}
			
			lstMergedGpsApOCRFiles.add(sMergedGpsApOCRFile);
			
			sGroundTruthFile = sFolderFullPath + File.separator + ONE_PLACE_GROUND_TRUTH_FILE;
			lstGroundTruthFiles.add(sGroundTruthFile);
		}
		
	}
	
	
	
	//This function get the list of MergedGpsApOCR.csv file (with full path) and the corresponding ground truth place file (GroundTruth.csv) from each sub place folder under the crowdsourced top parent folder
	public static void getCrowdsourcedMergedGroundTruthFilesByPlaceName(String sTopParentFolder, List<String> lstPlaceName, List<String> lstMergedGpsApOCRFiles, List<String> lstGroundTruthFiles, int nFrameCnt) {
		String sMergedGpsApOCRFile = "";
		String sGroundTruthFile = "";
				
		for (String sPlaceName : lstPlaceName) {
			if (nFrameCnt == NO_FRAME_SAMPLE) {
				sMergedGpsApOCRFile = sTopParentFolder + File.separator + sPlaceName + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + ".csv";
			} else {
				sMergedGpsApOCRFile = sTopParentFolder + File.separator + sPlaceName + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + "_" + nFrameCnt + ".csv";				
			}
			
			lstMergedGpsApOCRFiles.add(sMergedGpsApOCRFile);
			
			sGroundTruthFile = sTopParentFolder + File.separator + sPlaceName + File.separator + ONE_PLACE_GROUND_TRUTH_FILE;
			lstGroundTruthFiles.add(sGroundTruthFile);
		}
		
	}
	

	//This function get the list of MergedGpsApOCR.csv file (with full path) and the corresponding ground truth place file (GroundTruth.csv) from each sub place folder under the crowdsourced top parent folder
	public static void getCrowdsourcedMergedGroundTruthFilesByPlaceName_MultipleIndexSet(String sTopParentFolder, List<String> lstPlaceName, List<String> lstMergedGpsApOCRFiles, List<String> lstGroundTruthFiles, int nFrameCnt, int nIndexSetNum) {
		String sMergedGpsApOCRFile = "";
		String sGroundTruthFile = "";
		int nIndex = 1;
				
		for (String sPlaceName : lstPlaceName) {
			if (nFrameCnt == NO_FRAME_SAMPLE) {
				sMergedGpsApOCRFile = sTopParentFolder + File.separator + sPlaceName + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + ".csv";
			} else {
				nIndex = getRandom(nIndexSetNum);
				sMergedGpsApOCRFile = sTopParentFolder + File.separator + sPlaceName + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + "_" + nFrameCnt + "_" + nIndex + ".csv";				
			}
			
			lstMergedGpsApOCRFiles.add(sMergedGpsApOCRFile);
			
			sGroundTruthFile = sTopParentFolder + File.separator + sPlaceName + File.separator + ONE_PLACE_GROUND_TRUTH_FILE;
			lstGroundTruthFiles.add(sGroundTruthFile);
		}
		
	}
	
	
	//Get Merged Gps AP OCR information from MergedGpsApOCR.csv
	//The file content is one line: GPS (lat, long), <MAC, LbRss, UbRss, MeanRss, Order>s, OCRed Words/weight list
	public static AL_CrowdSourcedPlaceData getCrowdSourcedPlaceDataFromMerged(String sMergedGpsApOCRFile, int nApCnt) {
		AL_CrowdSourcedPlaceData locCrowdSourcedPlaceData = null;
		String sLine = "";
    	FileReader fr = null;
    	BufferedReader br = null;
    	String[] sField = null;
    	int i;
    	boolean blnGpsApOCRedLineProcessed = false;
    	boolean blnAllOCRedLineProcessed = false;
    	    	
		try {
	    	File fl = new File(sMergedGpsApOCRFile);

	    	if (fl.exists() == false) {
	    		return null;
	    	}
			
			fr = new FileReader(sMergedGpsApOCRFile);
			br = new BufferedReader(fr);
		
			while ((sLine = br.readLine()) != null) {
				if (blnGpsApOCRedLineProcessed == true && blnAllOCRedLineProcessed == true) break;
				
				if (blnGpsApOCRedLineProcessed == false) {
					sField = getFields(sLine);
					
					if (sField.length > 2 + 5*nApCnt) {  //There are cases, where no actual nApCnt AP exists, but afte that there are word exist, so the "for" below set MAC to "null" like things
						locCrowdSourcedPlaceData = new AL_CrowdSourcedPlaceData();
						
						locCrowdSourcedPlaceData.setGpsLat(Double.valueOf(sField[0]).doubleValue());
						locCrowdSourcedPlaceData.setGpsLong(Double.valueOf(sField[1]).doubleValue());
						
						//Get AP information
						List<AL_APInfoForDB> lstAPInfoForDB = new ArrayList<AL_APInfoForDB>();
						
						for (i=1; i<=nApCnt; i++) {
							AL_APInfoForDB locAPInfoForDB = new AL_APInfoForDB();
							if (sField[(i-1)*5+2].length() == 0) {
								locAPInfoForDB.setMAC("null");
							} else {
								locAPInfoForDB.setMAC(sField[(i-1)*5+2]);
							}
							
							if (sField[(i-1)*5+3].length() == 0) {
								locAPInfoForDB.setLbRSS(-200);
							} else {
								locAPInfoForDB.setLbRSS(Double.valueOf(sField[(i-1)*5+3]).doubleValue());
							}
							
							if (sField[(i-1)*5+4].length() == 0) {
								locAPInfoForDB.setUbRSS(-200);
							} else {
								locAPInfoForDB.setUbRSS(Double.valueOf(sField[(i-1)*5+4]).doubleValue());
							}
							
							if (sField[(i-1)*5+5].length() == 0) {
								locAPInfoForDB.setMeanRSS(-200);
							} else {
								locAPInfoForDB.setMeanRSS(Double.valueOf(sField[(i-1)*5+5]).doubleValue());
							}
							
							if (sField[(i-1)*5+6].length() == 0) {
								locAPInfoForDB.setOrder(100+i);  //Randomly set a big order number
							} else {
								locAPInfoForDB.setOrder(Integer.valueOf(sField[(i-1)*5+6]).intValue());
							}
							
							lstAPInfoForDB.add(locAPInfoForDB);
						}
						
						locCrowdSourcedPlaceData.setAPInfoForDB(lstAPInfoForDB);
						
						//Get OCRed Word/Weight information
						List<String> lstOCRedWord = new ArrayList<String>();
						//List<Integer> lstnOCRedWordFreq = new ArrayList<Integer>();
						List<Double> lstfOCRedWordsWeight = new ArrayList<Double>();
						
						//for (i=2+5*nApCnt; i<sField.length; i++) {
						for (i=2+5*nApCnt; i<sField.length; i=i+2) {
							lstOCRedWord.add(sField[i]);
							//lstnOCRedWordFreq.add(Integer.valueOf(sField[i+1]));
							lstfOCRedWordsWeight.add(Double.valueOf(sField[i+1]));
						}
						
						locCrowdSourcedPlaceData.setOCRedWord(lstOCRedWord);
						//locCrowdSourcedPlaceData.setOCRedWordFreq(lstnOCRedWordFreq);
						locCrowdSourcedPlaceData.setOCRedWordsWeight(lstfOCRedWordsWeight);
					}
				
					blnGpsApOCRedLineProcessed = true;
					
				} else if (blnAllOCRedLineProcessed == false) {
					sField = getFields(sLine);
					
					List<String> lstAllOCRedWord = new ArrayList<String>();
					for (int j=0; j<sField.length; j++) {
						lstAllOCRedWord.add(sField[j]);
					}
					
					locCrowdSourcedPlaceData.setAllOCRedWord(lstAllOCRedWord);
					
					blnAllOCRedLineProcessed = true;
				}
				
				
			}
		
			fr.close();
		} catch (Exception e) {
			
		}
    	
		return locCrowdSourcedPlaceData;
	}
	
	
	public static String getGroundTruthPlaceNameFromFolder(String sParentFolder) {
		String sGroundTruthFile = sParentFolder + File.separator + ONE_PLACE_GROUND_TRUTH_FILE;
		
		String sGroundTruthPlaceName = getGroundTruthPlaceName(sGroundTruthFile);
		
		return sGroundTruthPlaceName;
	}
	
	//Get the ground truth place name from Ground Truth file GroundTruth.csv
	public static String getGroundTruthPlaceName(String sGroundTruthFile) {
		String sGroundTruthPlaceName = "";
		String sLine = "";
    	FileReader fr = null;
    	BufferedReader br = null;

		try {
	    	File fl = new File(sGroundTruthFile);

	    	if (fl.exists() == false) {
	    		return "";
	    	}
						
			fr = new FileReader(sGroundTruthFile);
			br = new BufferedReader(fr);
		
			while ((sLine = br.readLine()) != null) {
				sGroundTruthPlaceName = sLine;
				break;
			}
		
			fr.close();
		} catch (Exception e) {
			
		}
    	
		return sGroundTruthPlaceName;
	}
	
	
	//From the list of places/stores' MergedGpsApOCR.csv and GroundTruth.csv, get the GpsAp and OCR words/frequencies, as well as the ground truth place names
	public static void getCrowdsourcedMergedGroundTruth(List<String> lstMergedGpsApOCRFiles, List<String> lstGroundTruthFiles, int nApCnt, List<AL_CrowdSourcedPlaceData> lstCrowdsourcedPlaceData, List<String> lstGroundTruthPlaceNames) {
		int i;
		String sMergedGpsApOCRFile = "";
		String sGroundTruthFile = "";
		String sGroundTruthPlaceName = "";
		
		//AL_CrowdSourcedPlaceData locCrowdSourcedPlaceData = null;
		
		if (lstMergedGpsApOCRFiles == null || 
		    lstMergedGpsApOCRFiles.size() == 0 || 
		    lstGroundTruthFiles == null || 
		    lstGroundTruthFiles.size() == 0) {
			
			return;
		}
		
		for (i=0; i<lstMergedGpsApOCRFiles.size(); i++) {
			//Process MergedGpsApOCR.csv
			sMergedGpsApOCRFile = lstMergedGpsApOCRFiles.get(i);
			
			AL_CrowdSourcedPlaceData locCrowdSourcedPlaceData = null;
			locCrowdSourcedPlaceData = getCrowdSourcedPlaceDataFromMerged(sMergedGpsApOCRFile, nApCnt);
			
			if (locCrowdSourcedPlaceData == null) continue;
			
			lstCrowdsourcedPlaceData.add(locCrowdSourcedPlaceData);
			
			//Process GroundTruth.csv
			sGroundTruthFile = lstGroundTruthFiles.get(i);
			
			sGroundTruthPlaceName = getGroundTruthPlaceName(sGroundTruthFile);
			
			lstGroundTruthPlaceNames.add(sGroundTruthPlaceName);  //If GroundTruth.csv file does not exist, GroundTruth place name is set to be ""
		}
		
	}
	
	
	public static String getOnePlaceMergedResultFile(String sTopParentFolder, String sPlaceNameFolderName, int nFrameCnt) {
		String sOnePlaceMergedResultFile = "";
		
		if (nFrameCnt == NO_FRAME_SAMPLE) {
			sOnePlaceMergedResultFile = sTopParentFolder +  File.separator + sPlaceNameFolderName + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + ".csv";			
		} else {
			sOnePlaceMergedResultFile = sTopParentFolder +  File.separator + sPlaceNameFolderName + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + "_" + nFrameCnt + ".csv";
		}
		
		return sOnePlaceMergedResultFile; 
	}


	public static String getOnePlaceMergedResultFile_MultipleIndexSet(String sTopParentFolder, String sPlaceNameFolderName, int nFrameCnt, int nSetIndex) {
		String sOnePlaceMergedResultFile = "";
		
		if (nFrameCnt == NO_FRAME_SAMPLE) {
			sOnePlaceMergedResultFile = sTopParentFolder +  File.separator + sPlaceNameFolderName + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + ".csv";			
		} else {
			sOnePlaceMergedResultFile = sTopParentFolder +  File.separator + sPlaceNameFolderName + File.separator + ONE_PLACE_MERGED_GPS_AP_OCR_RESULT_FILE_PRE + "_" + nFrameCnt + "_" + nSetIndex + ".csv";
		}
		
		return sOnePlaceMergedResultFile; 
	}

	
	public static String getOnePlaceMergedResultFile_Full(String sTopParentFolder, String sPlaceNameFolderName, int nFrameCnt) {
		String sOnePlaceMergedResultFile_Full = "";
		
		if (nFrameCnt == NO_FRAME_SAMPLE) {
			sOnePlaceMergedResultFile_Full = sTopParentFolder +  File.separator + sPlaceNameFolderName + File.separator + ONE_PLACE_MERGED_OCR_RESULT_FILE_FULL_PRE + ".csv";			
		} else {	
			sOnePlaceMergedResultFile_Full = sTopParentFolder +  File.separator + sPlaceNameFolderName + File.separator + ONE_PLACE_MERGED_OCR_RESULT_FILE_FULL_PRE + "_" + nFrameCnt + ".csv";
		}
		
		return sOnePlaceMergedResultFile_Full; 
	}
	
	public static String getOnePlaceMergedResultFile_Full_MultipleIndexSet(String sTopParentFolder, String sPlaceNameFolderName, int nFrameCnt, int nSetIndex) {
		String sOnePlaceMergedResultFile_Full = "";
		
		if (nFrameCnt == NO_FRAME_SAMPLE) {
			sOnePlaceMergedResultFile_Full = sTopParentFolder +  File.separator + sPlaceNameFolderName + File.separator + ONE_PLACE_MERGED_OCR_RESULT_FILE_FULL_PRE + ".csv";			
		} else {	
			sOnePlaceMergedResultFile_Full = sTopParentFolder +  File.separator + sPlaceNameFolderName + File.separator + ONE_PLACE_MERGED_OCR_RESULT_FILE_FULL_PRE + "_" + nFrameCnt + "_" + nSetIndex + ".csv";
		}
		
		return sOnePlaceMergedResultFile_Full; 
	}

	
	public static String getOnePlaceGroundTruthFile(String sTopParentFolder, String sPlaceNameFolderName) {
		String sOnePlaceGroundTruthFile = sTopParentFolder +  File.separator + sPlaceNameFolderName + File.separator + ONE_PLACE_GROUND_TRUTH_FILE;
		
		return sOnePlaceGroundTruthFile; 
	}

	
	public static String getGpsAPFolder(String sParentFolder) {
		String sGpsAPFolder = sParentFolder + File.separator + CROWDSOURCE_GPSAP_FOLDER;
		return sGpsAPFolder;
	}
	
	public static String getOCRedWordsFolder(String sParentFolder) {
		String sOCRedWordsFolder = sParentFolder + File.separator + CROWDSOURCE_OCR_FOLDER;
		return sOCRedWordsFolder;		
	}
	
	//Get file list in GpsAP folder from the given top folder and place name (i.e. get the GpsAP file list of this place
	public static void getGpsAPFileList(String sParentFolder, String sPlaceName, List<String> lstFileFullPathList) {
		List<String> lstFileNameListTmp = new ArrayList<String>();
		String sSubParentFoler = sParentFolder + File.separator + sPlaceName + File.separator + CROWDSOURCE_GPSAP_FOLDER;
		
		getFileList(sSubParentFoler, lstFileNameListTmp, lstFileFullPathList);
	}

	
	//Get file list in OCRedWords folder from the given top folder and place name (i.e. get the OCRed words file list of this place
	public static void getOCRedWordsFileList(String sParentFolder, String sPlaceName, List<String> lstFileFullPathList) {
		List<String> lstFileNameListTmp = new ArrayList<String>();
		boolean bFullText = false;
		String sSubParentFoler = sParentFolder + File.separator + sPlaceName + File.separator + CROWDSOURCE_OCR_FOLDER;
		
		getOCRFileList(sSubParentFoler, lstFileNameListTmp, lstFileFullPathList, bFullText);
	}
	
	//Get file list in OCRedWords folder from the given top folder and place name (i.e. get the OCRed words file list of this place
	public static void getOCRedWordsFileList_FullText(String sParentFolder, String sPlaceName, List<String> lstFileFullPathList) {
		List<String> lstFileNameListTmp = new ArrayList<String>();
		boolean bFullText = true;
		String sSubParentFoler = sParentFolder + File.separator + sPlaceName + File.separator + CROWDSOURCE_OCR_FOLDER;
		
		getOCRFileList(sSubParentFoler, lstFileNameListTmp, lstFileFullPathList, bFullText);
	}

	
	//Get file list in OCRedWords folder from the given top folder and place name (i.e. get the OCRed words file list of this place
	public static void getOCRedWordsFileList_Cluster(String sParentFolder, List<String> lstFileFullPathList) {
		List<String> lstFileNameListTmp = new ArrayList<String>();
		boolean bFullText = false;
		String sSubParentFoler = sParentFolder + File.separator + CROWDSOURCE_OCR_FOLDER;
		
		getOCRFileList(sSubParentFoler, lstFileNameListTmp, lstFileFullPathList, bFullText);
	}
	
	//Get file list in OCRedWords folder from the given top folder and place name (i.e. get the OCRed words file list of this place
	public static void getOCRedWordsFileList_FullText_Cluster(String sParentFolder, List<String> lstFileFullPathList) {
		List<String> lstFileNameListTmp = new ArrayList<String>();
		boolean bFullText = true;
		String sSubParentFoler = sParentFolder + File.separator + CROWDSOURCE_OCR_FOLDER;
		
		getOCRFileList(sSubParentFoler, lstFileNameListTmp, lstFileFullPathList, bFullText);
	}
	
	
	//This fucntion get the Place Keywords List from the PlaceKeywordList_Lat_Long.csv
	//In the sPlaceKeywordListGpsFile, following information is stored: Place/Store name, Lat, Long, Keyword1, Weight1, Keyword2, Weight2... (Top N)
	//One line for each store
	//The result should be saved in List<AL_PlaceWebData>
	public static List<AL_PlaceWebData> getPlaceKeywordsList(String sPlaceKeywordListGpsFile) {
		List<AL_PlaceWebData> lstPlaceWebData = null;
    	String sLine;
    	FileReader fr = null;
    	BufferedReader br = null;
    	boolean bFirst = false;

		try {
	    	File fl = new File(sPlaceKeywordListGpsFile);

			if (fl.exists() == false) {
	    		return null;
	    	}
	    	
	    	lstPlaceWebData = new ArrayList<AL_PlaceWebData>();
						
			fr = new FileReader(sPlaceKeywordListGpsFile);
			br = new BufferedReader(fr);
		
			while ((sLine = br.readLine()) != null) {
				String[] sFields = getFields(sLine); 
				if (sFields.length >= 5) {  //At least there is one Keyword/Weight Pair
					if (bFirst == false) {
						lstPlaceWebData = new ArrayList<AL_PlaceWebData>();
						bFirst = true;
					}
					
					AL_PlaceWebData locPlaceWebData = new AL_PlaceWebData();
					locPlaceWebData.setPlaceName(sFields[0]);
					locPlaceWebData.setGpsLat(Double.valueOf(sFields[1]).doubleValue());
					locPlaceWebData.setGpsLong(Double.valueOf(sFields[2]).doubleValue());
					
					List<AL_KeywordWeight> lstKeywordWeight = new ArrayList<AL_KeywordWeight>();
					
					for (int i=3; i<sFields.length; i=i+2) {
						AL_KeywordWeight locKeywordWeight = new AL_KeywordWeight();
						
						locKeywordWeight.setKeyword(sFields[i]);
						locKeywordWeight.setWeight(Double.valueOf(sFields[i+1]).doubleValue());
						
						lstKeywordWeight.add(locKeywordWeight);
					}
					
					locPlaceWebData.setKeywordWeight(lstKeywordWeight);
					
					lstPlaceWebData.add(locPlaceWebData);
				}
				
			}
		
			fr.close();
		} catch (Exception e) {
			
		}
    	
    	return lstPlaceWebData;
	}
	
	
	//Get timestamped Place AP Database file (full path)
	public static String getPlaceAPDatabaseFile(String sPlaceApResultTopFolder, int nFrameCnt) {
		String sPlaceAPDatabaseFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sPlaceAPDatabaseFile = sPlaceApResultTopFolder + File.separator +  PLACE_AP_DB_FILE_PRE + "_withPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sPlaceAPDatabaseFile = sPlaceApResultTopFolder + File.separator +  PLACE_AP_DB_FILE_PRE + "_withoutPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 	    	
 	    }
 	    
		return sPlaceAPDatabaseFile;
	}   

	//Get timestamped Place AP Database file (full path)
	public static String getPlaceAPDatabaseFile_S(String sPlaceApResultTopFolder, int nFrameCnt) {
		String sPlaceAPDatabaseFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    String sSubFolderFullPath = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sSubFolderFullPath = sPlaceApResultTopFolder + File.separator + OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME;
 	    	createFolder(sPlaceApResultTopFolder, OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME);
 	    } else {
 	    	sSubFolderFullPath = sPlaceApResultTopFolder + File.separator + OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME;
 	    	createFolder(sPlaceApResultTopFolder, OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME);
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sPlaceAPDatabaseFile = sSubFolderFullPath + File.separator +  PLACE_AP_DB_FILE_PRE + "_withPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sPlaceAPDatabaseFile = sSubFolderFullPath + File.separator +  PLACE_AP_DB_FILE_PRE + "_withoutPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 	    	
 	    }
 	    
		return sPlaceAPDatabaseFile;
	}   
	

	//Get timestamped Place AP Database file (full path), for Multiple Set Index
	public static String getPlaceAPDatabaseFile_MultipleIndexSet(String sPlaceApResultTopFolder, int nFrameCnt, int nSetIndex) {
		String sPlaceAPDatabaseFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt + "_" + nSetIndex;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sPlaceAPDatabaseFile = sPlaceApResultTopFolder + File.separator +  PLACE_AP_DB_FILE_PRE + "_withPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sPlaceAPDatabaseFile = sPlaceApResultTopFolder + File.separator +  PLACE_AP_DB_FILE_PRE + "_withoutPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 	    	
 	    }
 	    
		return sPlaceAPDatabaseFile;
	}   
	
	
	//Get timestamped Place AP Database file (full path)
	public static String getMatchedCommonWordsFile(String sPlaceApResultTopFolder, int nFrameCnt) {
		String sMatchedCommonFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sMatchedCommonFile = sPlaceApResultTopFolder + File.separator +  MATCHED_COMMON_WORD_FILE_PRE + "_withPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sMatchedCommonFile = sPlaceApResultTopFolder + File.separator +  MATCHED_COMMON_WORD_FILE_PRE + "_withoutPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 	    	
 	    }
 	    
		return sMatchedCommonFile;
	}   

	//Get timestamped Place AP Database file (full path)
	public static String getMatchedCommonWordsFile_S(String sPlaceApResultTopFolder, int nFrameCnt) {
		String sMatchedCommonFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    String sSubFolderFullPath = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sSubFolderFullPath = sPlaceApResultTopFolder + File.separator + OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME;
 	    	createFolder(sPlaceApResultTopFolder, OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME);
 	    } else {
 	    	sSubFolderFullPath = sPlaceApResultTopFolder + File.separator + OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME;
 	    	createFolder(sPlaceApResultTopFolder, OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME);
 	    }

 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sMatchedCommonFile = sSubFolderFullPath + File.separator +  MATCHED_COMMON_WORD_FILE_PRE + "_withPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sMatchedCommonFile = sSubFolderFullPath + File.separator +  MATCHED_COMMON_WORD_FILE_PRE + "_withoutPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 	    	
 	    }
 	    
		return sMatchedCommonFile;
	}   

	//Get timestamped Place AP Database file, for Multiple Set Index
	public static String getMatchedCommonWordsFile_MultipleIndexSet(String sPlaceApResultTopFolder, int nFrameCnt, int nSetIndex) {
		String sMatchedCommonFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt + "_" + nSetIndex;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sMatchedCommonFile = sPlaceApResultTopFolder + File.separator +  MATCHED_COMMON_WORD_FILE_PRE + "_withPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sMatchedCommonFile = sPlaceApResultTopFolder + File.separator +  MATCHED_COMMON_WORD_FILE_PRE + "_withoutPlaceNameMatching_" + spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 	    	
 	    }
 	    
		return sMatchedCommonFile;
	}   
	
	
	
	//Get timestamped Keyword Matching Evaluation result file (full path)
	public static String getKeywordMatchingEvalResultFile(String sPlaceApResultTopFolder, int nFrameCnt) {
		String sKeywordMatchingEvalResultFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sKeywordMatchingEvalResultFile = sPlaceApResultTopFolder + File.separator +  KEYWORD_MATCHING_EVAL_RESULT_FILE_PRE + "_withPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sKeywordMatchingEvalResultFile = sPlaceApResultTopFolder + File.separator +  KEYWORD_MATCHING_EVAL_RESULT_FILE_PRE + "_withoutPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 		   
 	    }
 	    
		return sKeywordMatchingEvalResultFile;
	}

	
	//Get timestamped Keyword Matching Evaluation result file (full path)
	public static String getKeywordMatchingEvalResultFile_S(String sPlaceApResultTopFolder, int nFrameCnt) {
		String sKeywordMatchingEvalResultFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    String sSubFolderFullPath = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sSubFolderFullPath = sPlaceApResultTopFolder + File.separator + OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME;
 	    	createFolder(sPlaceApResultTopFolder, OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME);
 	    } else {
 	    	sSubFolderFullPath = sPlaceApResultTopFolder + File.separator + OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME;
 	    	createFolder(sPlaceApResultTopFolder, OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME);
 	    }
 	     	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sKeywordMatchingEvalResultFile = sSubFolderFullPath + File.separator +  KEYWORD_MATCHING_EVAL_RESULT_FILE_PRE + "_withPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sKeywordMatchingEvalResultFile = sSubFolderFullPath + File.separator +  KEYWORD_MATCHING_EVAL_RESULT_FILE_PRE + "_withoutPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 		   
 	    }
 	    
		return sKeywordMatchingEvalResultFile;
	}

	
	//Get timestamped Keyword Matching Evaluation result file (full path), for Multiple Set Index
	public static String getKeywordMatchingEvalResultFile_MultipleIndexSet(String sPlaceApResultTopFolder, int nFrameCnt, int nSetIndex) {
		String sKeywordMatchingEvalResultFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt + "_" + nSetIndex;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sKeywordMatchingEvalResultFile = sPlaceApResultTopFolder + File.separator +  KEYWORD_MATCHING_EVAL_RESULT_FILE_PRE + "_withPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sKeywordMatchingEvalResultFile = sPlaceApResultTopFolder + File.separator +  KEYWORD_MATCHING_EVAL_RESULT_FILE_PRE + "_withoutPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 		   
 	    }
 	    
		return sKeywordMatchingEvalResultFile;
	}
	
	
	//Get timestamped Keyword Matching confusion matrix file (full path)
	public static String getKeywordMatchingConfusionMatrixFile(String sPlaceApResultTopFolder, int nFrameCnt) {
		String sKeywordMatchingConfusionMatrixFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sKeywordMatchingConfusionMatrixFile = sPlaceApResultTopFolder + File.separator +  KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE + "_withPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sKeywordMatchingConfusionMatrixFile = sPlaceApResultTopFolder + File.separator +  KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE + "_withoutPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 	    	
 	    }
 	    
		return sKeywordMatchingConfusionMatrixFile;
	}

	
	//Get timestamped Keyword Matching confusion matrix file (full path)
	public static String getKeywordMatchingConfusionMatrixFile_S(String sPlaceApResultTopFolder, int nFrameCnt) {
		String sKeywordMatchingConfusionMatrixFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    String sSubFolderFullPath = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sSubFolderFullPath = sPlaceApResultTopFolder + File.separator + OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME;
 	    	createFolder(sPlaceApResultTopFolder, OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME);
 	    } else {
 	    	sSubFolderFullPath = sPlaceApResultTopFolder + File.separator + OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME;
 	    	createFolder(sPlaceApResultTopFolder, OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME);
 	    }
 	     	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sKeywordMatchingConfusionMatrixFile = sSubFolderFullPath + File.separator +  KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE + "_withPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sKeywordMatchingConfusionMatrixFile = sSubFolderFullPath + File.separator +  KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE + "_withoutPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 	    	
 	    }
 	    
		return sKeywordMatchingConfusionMatrixFile;
	}
	

	//Get timestamped Keyword Matching confusion matrix file (full path), for Multiple Set Index
	public static String getKeywordMatchingConfusionMatrixFile_MultipleIndexSet(String sPlaceApResultTopFolder, int nFrameCnt, int nSetIndex) {
		String sKeywordMatchingConfusionMatrixFile = "";
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sPostFix = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sPostFix = "";
 	    } else {
 	    	sPostFix = "_" + nFrameCnt + "_" + nSetIndex;
 	    }
 	    
 	    if (MATCH_WITH_PLACENAME) {
 	    	sKeywordMatchingConfusionMatrixFile = sPlaceApResultTopFolder + File.separator +  KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE + "_withPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv";
 	    } else {
 	    	sKeywordMatchingConfusionMatrixFile = sPlaceApResultTopFolder + File.separator +  KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE + "_withoutPlaceNameMatching_" +spdCurrentTime.format(dtFileStart) + sPostFix + ".csv"; 	    	
 	    }
 	    
		return sKeywordMatchingConfusionMatrixFile;
	}
	
	
	public static String getSimilarityResultFile(String sSrcDataFileFolder, String sSimilarityResultFolder, int nDataSourceType, int nFrameCnt) {
		String sSimilarityResultFile = "";
		File fl = new File(sSimilarityResultFolder);
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);
 	    String sFrameCntPostFix = "";
 	    
 	    if (nFrameCnt == NO_FRAME_SAMPLE) {
 	    	sFrameCntPostFix = "";
 	    } else {
 	    	sFrameCntPostFix = "_" + nFrameCnt;
 	    }
		 	    
		if (fl.getAbsoluteFile().exists()) {
		//if (fl.exists()) {

			if (nDataSourceType == SIMILARITY_WEB) {
				int nIdxPre = -1;
				nIdxPre = sSrcDataFileFolder.indexOf(PLACE_KEYWORD_FILE_PRE);
				
				int nIdxPost = -1;
				nIdxPost = sSrcDataFileFolder.indexOf(".csv");
				
				String sPostfix = "";
				
				if (nIdxPre != -1 && nIdxPost != -1) {
					sPostfix = sSrcDataFileFolder.substring(nIdxPre + PLACE_KEYWORD_FILE_PRE.length(), nIdxPost);
				}
				
				if (sPostfix.length() > 0) {
					sSimilarityResultFile = sSimilarityResultFolder + File.separator +  "WebSimilarity" + sPostfix + "_" + spdCurrentTime.format(dtFileStart) + sFrameCntPostFix + ".csv";
				} else {
					sSimilarityResultFile = sSimilarityResultFolder + File.separator +  "WebSimilarity" + "_" + spdCurrentTime.format(dtFileStart) + sFrameCntPostFix + ".csv";					
				}
			} else {
				sSimilarityResultFile = sSimilarityResultFolder + File.separator +  "InStoreOCRSimilarity" + "_" +spdCurrentTime.format(dtFileStart) + sFrameCntPostFix + ".csv";				
			}
			
			System.out.println(sSimilarityResultFolder + "-----Existing......");
		}
		
		return sSimilarityResultFile;
	}
	
	
	public static double calculateMeanDouble(List<Double> lstfValues) {
		double fMean = 0.0;
		double fTotal = 0.0;
		
		if (lstfValues == null) return 0.0;
		
		for (int i=0; i<lstfValues.size(); i++) {
			fTotal = fTotal  + lstfValues.get(i);
		}
		
		fMean = fTotal/lstfValues.size();
		
		return fMean;
	}

	public static double calculateStdDouble(List<Double> lstfValues) {
		double fStd = 0.0;
		double fMean = 0.0;
		double fTotal = 0.0;
		int i;
		int nCnt = 0;
		
		if (lstfValues == null) return 0.0;
		
		nCnt = lstfValues.size();
		
		if (nCnt == 1) return 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + lstfValues.get(i);
		}
		
		fMean = fTotal/nCnt;
		
		fTotal = 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + Math.pow((lstfValues.get(i)-fMean), 2);
		}
		
		fStd = Math.sqrt(fTotal/(nCnt-1));
		
		return fStd;
	}

	
	public static double calculateMeanDouble(double[] arrfValues) {
		double fMean = 0.0;
		double fTotal = 0.0;
		
		if (arrfValues == null) return 0.0;
		
		for (int i=0; i<arrfValues.length; i++) {
			fTotal = fTotal  + arrfValues[i];
		}
		
		fMean = fTotal/arrfValues.length;
		
		return fMean;
	}
	
	
	public static double calculateStdDouble(double[] arrfValues) {
		double fStd = 0.0;
		double fMean = 0.0;
		double fTotal = 0.0;
		int i;
		int nCnt = 0;
		
		if (arrfValues == null) return 0.0;
		
		nCnt = arrfValues.length;
		
		if (nCnt == 1) return 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + arrfValues[i];
		}
		
		fMean = fTotal/nCnt;
		
		fTotal = 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + Math.pow((arrfValues[i]-fMean), 2);
		}
		
		fStd = Math.sqrt(fTotal/(nCnt-1));
		
		return fStd;
	}

/////////////////////
	
	public static double calculateMeanInteger(List<Integer> lstnValues) {
		double fMean = 0.0;
		double fTotal = 0.0;
		
		if (lstnValues == null) return 0.0;
		
		for (int i=0; i<lstnValues.size(); i++) {
			fTotal = fTotal  + lstnValues.get(i);
		}
		
		fMean = fTotal/lstnValues.size();
		
		return fMean;
	}

	public static double calculateStdInteger(List<Integer> lstnValues) {
		double fStd = 0.0;
		double fMean = 0.0;
		double fTotal = 0.0;
		int i;
		int nCnt = 0;
		
		if (lstnValues == null) return 0.0;
		
		nCnt = lstnValues.size();
		
		if (nCnt == 1) return 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + lstnValues.get(i);
		}
		
		fMean = fTotal/nCnt;
		
		fTotal = 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + Math.pow((lstnValues.get(i)-fMean), 2);
		}
		
		fStd = Math.sqrt(fTotal/(nCnt-1));
		
		return fStd;
	}

	
	public static double calculateMeanInteger(int[] arrnValues) {
		double fMean = 0.0;
		double fTotal = 0.0;
		
		if (arrnValues == null) return 0.0;
		
		for (int i=0; i<arrnValues.length; i++) {
			fTotal = fTotal  + arrnValues[i];
		}
		
		fMean = fTotal/arrnValues.length;
		
		return fMean;
	}
	
	
	public static double calculateStdInteger(int[] arrnValues) {
		double fStd = 0.0;
		double fMean = 0.0;
		double fTotal = 0.0;
		int i;
		int nCnt = 0;
		
		if (arrnValues == null) return 0.0;
		
		nCnt = arrnValues.length;
		
		if (nCnt == 1) return 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + arrnValues[i];
		}
		
		fMean = fTotal/nCnt;
		
		fTotal = 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + Math.pow((arrnValues[i]-fMean), 2);
		}
		
		fStd = Math.sqrt(fTotal/(nCnt-1));
		
		return fStd;
	}
	
	
	public static double calculateGpsDistance(double fLat1, double fLong1, double fLat2, double fLong2) {
		double fEarthRadius = 6371000;   //Meters
		double fDistance = 0.0;  //In meters
		double fLatGap = fLat2 - fLat1;
		double fLongGap = fLong2 - fLong1;
		
		fLatGap = fLatGap * Math.PI/180;
		fLongGap = fLongGap * Math.PI/180;
		
		double a = Math.sin(fLatGap / 2.0) * Math.sin(fLatGap / 2.0) +  
				Math.cos(fLat1*Math.PI/180) * Math.cos(fLat2*Math.PI/180) * Math.sin(fLongGap/2.0) * Math.sin(fLongGap/2.0);  
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));  
        
        fDistance = fEarthRadius * c;  
		
		return fDistance;
	}
	
	
	//Check whether two GPS coordinates are close
	public static boolean isGpsCoordinatesClose(double fLat1, double fLong1, double fLat2, double fLong2) {
		boolean bRet = false;
	//	double fLatGap = fLat1 - fLat2;
	//	double fLongGap = fLong1 - fLong2;
		
	//	double fX = fLongGap/0.0001*14.2;   //Convert Long gap to distance in meters
	//	double fY = fLatGap/0.0001*18.5;  //Convert Lat gap to distance in meters
		
//		double fDistance = Math.sqrt(fX*fX + fY*fY);

		double fDistance = calculateGpsDistance(fLat1, fLong1, fLat2, fLong2);
	
		if (fDistance > DISTANCE_THRESHOLD) {
			bRet = false;
		} else {
			bRet = true;
		}
		
		return bRet;
	}
	
	//Calculate the matching score
	//lstOCRedWords: The list of words OCRed from the pictures taken by the user
	public static double calculateMatchingScoreWithSingleWeight(List<String> lstOCRedWords, List<String> lstPlaceKeywords, List<Double> lstfPlaceKeywordWeights) {
		double fMatchingScore = 0.0f;
		
		for (String sOCRedWord:lstOCRedWords) {

			for (int i=0; i<lstPlaceKeywords.size(); i++) {
				if (sOCRedWord.compareToIgnoreCase(lstPlaceKeywords.get(i)) == 0) {
					fMatchingScore = fMatchingScore + lstfPlaceKeywordWeights.get(i);
					break;
				}
			}
		}
		
		return fMatchingScore;
	}

	
	//Calculate the matching score
	//lstOCRedWords: The list of words OCRed from the pictures taken by the user
	public static double calculateMatchingScoreWithSingleWeight(List<String> lstOCRedWords, List<AL_KeywordWeight> lstPlaceKeywordWeight) {
		double fMatchingScore = 0.0f;
		
		for (String sOCRedWord:lstOCRedWords) {

			for (int i=0; i<lstPlaceKeywordWeight.size(); i++) {
				if (sOCRedWord.compareToIgnoreCase(lstPlaceKeywordWeight.get(i).getKeyword()) == 0) {
					fMatchingScore = fMatchingScore + lstPlaceKeywordWeight.get(i).getWeight();
					break;
				}
			}
		}
		
		return fMatchingScore;
	}
	

	//Calculate the matching score: Both the place keywords and OCRed words have weight
	//lstOCRedWords: The list of words OCRed from the pictures taken by the user
	//lstfOCRedWordsWeight: The list of weight of OCRed words
	//
	//##Matching Score Calculation: 
	//		If one OCRed word matches a Place keyword,  the matching score is OCRed Word Weight * Place Keyword Weight/(OCRed Word Weight + Place Keyword Weight)
	//		Sum up all the matching score for all the matched words/keywords
	//The total weight of OCRed words are 1.0;  The total weight of Place keywords are 1.0.
	//After matching, the total score = sum(ab/(a+b)), the maximal score = sum(ai*ai/2ai) = sum(ai/2) = 1/2 = 0.5;
	//So the final score is multiplid by 2, to make the possible maximal score be to 1.0 
	//
	public static double calculateMatchingScoreWithDoubleWeight(List<String> lstOCRedWords, List<Double> lstfOCRedWordsWeight, List<AL_KeywordWeight> lstPlaceKeywordWeight) {
		double fMatchingScore = 0.0f;
		int i,j;
		String sOCRedWord;
		double fOCRedWordWeight;
		double fPlaceKeywordWeight;
		
		for (i=0; i<lstOCRedWords.size(); i++) {
			sOCRedWord = lstOCRedWords.get(i);
			fOCRedWordWeight = lstfOCRedWordsWeight.get(i).doubleValue();
			
			for (j=0; j<lstPlaceKeywordWeight.size(); j++) {
				if (sOCRedWord.compareToIgnoreCase(lstPlaceKeywordWeight.get(j).getKeyword()) == 0) {
					fPlaceKeywordWeight = lstPlaceKeywordWeight.get(j).getWeight();
					
					fMatchingScore = fMatchingScore + fOCRedWordWeight*fPlaceKeywordWeight/(fOCRedWordWeight + fPlaceKeywordWeight);
					break;
				}
			}
		}
		
		fMatchingScore = fMatchingScore * 2;
		
		
		return fMatchingScore;
	}
	
	

	public static boolean isNumeric(String str)
	{
		return str.matches("(\\+|-)?\\d+(\\.\\d+)?");  //match a number with optional '-' or '+' and decimal.
	}
	
	
	public static boolean isValidStarted(String str) {
		boolean bRet = false;
		
		if ((str.charAt(0) >= 'a' && str.charAt(0) <= 'z') || 
			(str.charAt(0) >= 'A' && str.charAt(0) <= 'Z')) {
			
			bRet = true;
		}
				
		return bRet;
	}
	
	
	public static boolean isBlackListedPlaceWord(String sWord) {
		boolean bRet = false;
		int i;
		
		if (sWord.length() <= 1) return true;
		
		if (isNumeric(sWord) == true) return true;
		
		for (i=0; i<PLACENAME_BLACKLIST.length; i++) {
			if (sWord.compareToIgnoreCase(PLACENAME_BLACKLIST[i]) == 0) {
				bRet = true;
				break;
			}
		}
	
		if (bRet == false) {
			for (i=0; i<PREP_BLACKLIST.length; i++) {
				if (sWord.compareToIgnoreCase(PREP_BLACKLIST[i]) == 0) {
					bRet = true;
					break;
				}
			}			
		}
				
		return bRet;
	}
	
	
	public static List<String> getValidPlaceNameWords(String sPlaceName) {
		List<String> lstValidWords = null;
		
		String[] sWords = splitString(sPlaceName);
		
		for (int i=0; i<sWords.length; i++) {
			if (isBlackListedPlaceWord(sWords[i]) == false) {
				if (lstValidWords == null) {
					lstValidWords = new ArrayList<String>();
				}
				
				if (lstValidWords.contains(sWords[i].toLowerCase()) == false) {
					lstValidWords.add(sWords[i].toLowerCase());
				}
			}
		}
		
		return lstValidWords;
	}
	
	
	//Here calculate how many words in the place name occur in lstAllOCRedWords (which is non-duplicate words, without noun/proper name extraction)
	public static int calculateHit(String sPlaceName, List<String> lstAllOCRedWords) {
		int nHitCnt = 0;
		int i, j;
		String sPlaceWord = "";
		List<String> lstsValidWords = getValidPlaceNameWords(sPlaceName);  //Words are not duplicate
		
		if (lstsValidWords == null) return 0;
		
		for (i=0; i<lstsValidWords.size(); i++) {
			sPlaceWord = lstsValidWords.get(i);
			
			for (j=0; j<lstAllOCRedWords.size(); j++) {
				if (sPlaceWord.compareToIgnoreCase(lstAllOCRedWords.get(j)) == 0) {
					nHitCnt = nHitCnt + 1;
					break;
				}
			}	
		}
		
		return nHitCnt;
	}

	
	//Calculate hit based on phrase
	public static int calculateHit_withPhrase(String sPlaceName, List<String> lstAllOCRedWords) {
		int nHitCnt = 0;
		int j;
			
		for (j=0; j<lstAllOCRedWords.size(); j++) {
			if (sPlaceName.trim().compareToIgnoreCase(lstAllOCRedWords.get(j).trim()) == 0) {
				nHitCnt = nHitCnt + 1;
				break;
			}
		}	
		
		return nHitCnt;
	}
	
	
	//This function compares the All OCRed Words of a place with the candidate Place names
	//@lstCandidatePlaceNames: list of candidate place name
	//@lstAllOCRedWords: All the OCRed words of a place, the duplicate words are removed, Noun/Proper name extraction is not applied.
	//First, from the candidate place names, remove the common words like "a, an, the.....",
	//During comparing, count the hit times (one matching between a word in place name with and in lstOCRedWords is one hit)
	//
	// Update 2014/10/29:  Compare based on phrase, NOT words
	//
	public static List<Integer> matchOCRedWordsWithPlaceNames(List<String> lstCandidatePlaceNames, List<String> lstAllOCRedWords) {
		List<Integer> lstnMatchedPlaceIdx = null;
		List<Integer> lstnHitCnt = new ArrayList<Integer>();
		int i;
		int nHitCnt = 0;
		int nMaxHit = 0;
		
		if (lstCandidatePlaceNames == null || lstCandidatePlaceNames.size() == 0 || lstAllOCRedWords == null || lstAllOCRedWords.size() == 0) return null;
		
		for (i=0; i<lstCandidatePlaceNames.size(); i++) {
			if (MATCH_WITH_PLACENAME_WITH_PHRASE == false) {
				nHitCnt = calculateHit(lstCandidatePlaceNames.get(i), lstAllOCRedWords);
			} else {
				nHitCnt = calculateHit_withPhrase(lstCandidatePlaceNames.get(i), lstAllOCRedWords);
			}
			
			lstnHitCnt.add(Integer.valueOf(nHitCnt));  //Record the hit number of each candidate place name with the OCRed Words list
			
			if (nMaxHit < nHitCnt) nMaxHit = nHitCnt;
		}
		
		if (nMaxHit > 0) {
			lstnMatchedPlaceIdx = new ArrayList<Integer>();
			
			for (i=0; i<lstnHitCnt.size(); i++) {
				//if (lstnHitCnt.get(i) == nMaxHit) {
				if (lstnHitCnt.get(i) > 0) {

					lstnMatchedPlaceIdx.add(i);     //Record the indexes of the place name 
				}
			}  
		}
		
		return lstnMatchedPlaceIdx;
	}
	
	
	//Calculate how many times a word occurs among a set of webs/stores
	public static int CalculateWordCount(List<List<String>> lstlstWord, String sWord) {
		int nWordOccurrencedCnt = 0;
		
		for (List<String> lstWord: lstlstWord) {
			if (lstWord.contains(sWord)) {
				nWordOccurrencedCnt = nWordOccurrencedCnt + 1;
			}
		}
		
		return nWordOccurrencedCnt;
	}
	
	
	//Calculate IDF for each word among the same set of places themselves
	public static List<List<Double>> CalculateIDF(List<List<String>> lstlstWord) {
		List<List<Double>> lstlstIDF = new ArrayList<List<Double>>();
		int nPlaceCnt = lstlstWord.size();
		int nWordOccurrencedCnt = 0;
		double fIDF = 0.0;
		
		for (List<String> lstWord:lstlstWord) {	
			
			List<Double> lstIDF = new ArrayList<Double>();
			
			for (String sWord:lstWord) {
				nWordOccurrencedCnt = CalculateWordCount(lstlstWord, sWord);
				
				fIDF = 1 + Math.log(nPlaceCnt/nWordOccurrencedCnt);  //Calculate IDF

				lstIDF.add(fIDF);
			}  
			
			lstlstIDF.add(lstIDF);
			
		}
		
		return lstlstIDF;
	}
	
	
	//Calculate IDF for the unknown words based on the candidate places
	public static List<Double> CalculateIDF(List<String> lstSrcWords, List<List<String>> lstlstCandidateWords, int nIgnoreIdx) {
		String sWord = "";
		int nOccurrenceCnt = 0;
		int nPlaceCnt = lstlstCandidateWords.size();
		double fIDF = 0.0;

		List<Double> lstSrcIDF = new ArrayList<Double>();
		
		for (int i=0; i<lstSrcWords.size(); i++) {
		
			sWord = lstSrcWords.get(i);
			nOccurrenceCnt = CalculateWordOccurrenceCountCommon(sWord, lstlstCandidateWords, nIgnoreIdx);
			
			if (nOccurrenceCnt > 0) {
				if (nIgnoreIdx != -1) {
					fIDF = 1 + Math.log((nPlaceCnt-1)/nOccurrenceCnt);
				} else {
					fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
				}
			} else {
				fIDF = 0;
			}
			
			lstSrcIDF.add(fIDF);
		}
		
		return lstSrcIDF;
		
	}
	
	
	//Calculate Overall weight (i.e. TF * IDF) for each word
	public static List<List<Double>> CalculateOverallWeight(List<List<Double>> lstlstTF, List<List<Double>> lstlstIDF) {
		List<List<Double>> lstlstOverallWeight = new ArrayList<List<Double>>();
		int nPlaceCnt = lstlstTF.size();
		double fOverallWeight = 0.0;
		int nIdx = 0;
		
		for (int i=0; i<nPlaceCnt; i++) {
			List<Double> lstTF = lstlstTF.get(i);
			List<Double> lstIDF = lstlstIDF.get(i);
			
			List<Double> lstOverallWeight = new ArrayList<Double>();  //Result overall weight
			
			for (int j=0; j<lstTF.size(); j++) {	
				double fTF = lstTF.get(j);   // TF weight (normalized)
				double fIDF = lstIDF.get(j);
				
				fOverallWeight = fTF * fIDF;   // TF-IDF weight
				
				lstOverallWeight.add(fOverallWeight);
				nIdx = nIdx + 1;
			}
			
			lstlstOverallWeight.add(lstOverallWeight);
		}
		
		return lstlstOverallWeight;
	}

	//Not USED
	//Calculate matching score between two websites
	//Currently, only compare similarity with content keywords
	//Need to involve Meta keyword
	public static double CalculateWebMatchingScore(List<String> lstMetaKeyword, List<String> lstKeyword, List<Double> lstOverallWeight, 
			                                       List<String> lstCandidateMetaKeyword, List<String> lstCandidateKeyword, List<Double> lstCandidateOverallWeight) {
		double fMatchingScore = 0.0;
		
		String sWord;
		
		int nIdx;
		int i;
		
		List<Double> lstMatchedWeight = new ArrayList<Double>();
		List<Double> lstCandidateMatchedWeight = new ArrayList<Double>();
		
		for (i=0; i<lstKeyword.size(); i++) {
			sWord = lstKeyword.get(i);
			
			nIdx = lstCandidateKeyword.indexOf(sWord);
			if (nIdx != -1) {
				lstMatchedWeight.add(lstOverallWeight.get(i));
				lstCandidateMatchedWeight.add(lstCandidateOverallWeight.get(nIdx));
			}
		}
		
		double fMatchingWeight = 0.0;
		double fMatchedWeightSquare = 0.0;
		double fCandidateMatchedWeightSqure = 0.0;
		
		for (i=0; i<lstMatchedWeight.size(); i++) {
			fMatchingWeight = fMatchingWeight + lstMatchedWeight.get(i).doubleValue() * lstCandidateMatchedWeight.get(i).doubleValue(); 
		}
		
		for (i=0; i<lstOverallWeight.size(); i++) {
			fMatchedWeightSquare = fMatchedWeightSquare + Math.pow(lstOverallWeight.get(i).doubleValue(), 2);
		}
		
		for (i=0; i<lstCandidateOverallWeight.size(); i++) {
			fCandidateMatchedWeightSqure = fCandidateMatchedWeightSqure + Math.pow(lstCandidateOverallWeight.get(i).doubleValue(), 2);
		}
		
		//Calculate Normalized Cosine Similarity
		if (fMatchedWeightSquare > 0 && fCandidateMatchedWeightSqure > 0) {
			fMatchingScore = fMatchingWeight/(Math.sqrt(fMatchedWeightSquare * fCandidateMatchedWeightSqure));
		} else {
			fMatchingScore = 0.0;
		}
		
		return fMatchingScore;
	}
	
	
	//NOT USED
	//Calculate matching score between two OCRed stores
	public static double CalculateInStoreOCRMatchingScore(List<String> lstSrcInStoreOCRedWord, List<Double> lstSrcInStoreOCRedOverallWeight,
														  List<String> lstCandidateInStoreOCRedWord, List<Double> lstCandidateInStoreOCRedOverallWeight) {
		double fMatchingScore = 0.0;
		String sWord;
		
		int nIdx;
		int i;
		
		List<Double> lstSrcMatchedWeight = new ArrayList<Double>();
		List<Double> lstCandidateMatchedWeight = new ArrayList<Double>();
		
		for (i=0; i<lstSrcInStoreOCRedWord.size(); i++) {
			sWord = lstSrcInStoreOCRedWord.get(i);
			
			nIdx = lstCandidateInStoreOCRedWord.indexOf(sWord);
			if (nIdx != -1) {
				lstSrcMatchedWeight.add(lstSrcInStoreOCRedOverallWeight.get(i));
				lstCandidateMatchedWeight.add(lstCandidateInStoreOCRedOverallWeight.get(nIdx));
			}
		}
		
		double fMatchingWeight = 0.0;
		double fSrcMatchedWeightSquare = 0.0;
		double fCandidateMatchedWeightSqure = 0.0;
		
		for (i=0; i<lstSrcMatchedWeight.size(); i++) {
			fMatchingWeight = fMatchingWeight + lstSrcMatchedWeight.get(i).doubleValue() * lstCandidateMatchedWeight.get(i).doubleValue(); 
		}
		
		for (i=0; i<lstSrcInStoreOCRedOverallWeight.size(); i++) {
			fSrcMatchedWeightSquare = fSrcMatchedWeightSquare + Math.pow(lstSrcInStoreOCRedOverallWeight.get(i).doubleValue(), 2);
		}
		
		for (i=0; i<lstCandidateInStoreOCRedOverallWeight.size(); i++) {
			fCandidateMatchedWeightSqure = fCandidateMatchedWeightSqure + Math.pow(lstCandidateInStoreOCRedOverallWeight.get(i).doubleValue(), 2);
		}
		
		//Calculate Normalized Cosine Similarity
		if (fSrcMatchedWeightSquare > 0 && fCandidateMatchedWeightSqure > 0) {
			fMatchingScore = fMatchingWeight/(Math.sqrt(fSrcMatchedWeightSquare * fCandidateMatchedWeightSqure));
		} else {
			fMatchingScore = 0.0;
		}
		
		return fMatchingScore;
	}

	
	public static int CalculateWordOccurrenceCount(String sWord, List<AL_PlaceWebData> lstCollectionCandidate, int nIgnoreIdx) {
		int nCount = 0;
		int i,j;
		
		for (i=0; i<lstCollectionCandidate.size(); i++) {
			if (i == nIgnoreIdx) continue;
			
			AL_PlaceWebData placeWebData = lstCollectionCandidate.get(i);
			List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
			
			for (j=0; j<lstKeywordWeight.size(); j++) {
				if (sWord.compareToIgnoreCase(lstKeywordWeight.get(j).getKeyword()) == 0) {
					nCount = nCount + 1;
					break;
				}
			}
		}
		
		return nCount;
	}
	

	public static int CalculateWordOccurrenceCountCommon(String sWord, List<List<String>> lstlstCandidateWords, int nIgnoreIdx) {
		int nCount = 0;
		int i,j;
		
		for (i=0; i<lstlstCandidateWords.size(); i++) {
			if (i == nIgnoreIdx) continue;

			List<String> lstCandidateWords = lstlstCandidateWords.get(i);
			
			for (j=0; j<lstCandidateWords.size(); j++) {
				if (sWord.compareToIgnoreCase(lstCandidateWords.get(j)) == 0) {
					nCount = nCount + 1;
					break;
				}
			}
		}
		
		return nCount;
	}
	
	//Matching a set of words with a collection
	//@return value: lstPlaceName  (Candidate place name list)
	//			   : lstfMatchingScore (Matching score for each of these place name)
	public static void CalculateCollectionMatchingWeb(List<AL_KeywordWeight> lstUnknown, List<AL_PlaceWebData> lstCollectionCandidate, 
			                                          List<String> lstPlaceName, List<Double> lstfMatchingScore, int nIgnoreIdx) {
		int i, j, k;
		String sWord; 
		List<Double> lstUnknownIDF = new ArrayList<Double>();
		List<List<Double>> lstlstCollectionIDF = new ArrayList<List<Double>>();
		
		int nPlaceCnt = lstCollectionCandidate.size();
		int nOccurrenceCnt = 0;
		double fIDF = 0.0;
		double fMatchingScore = 0;
		
		double fUnknownTotalWeight = 0;
		double fCandidateTotalWeight = 0;
		double fMatchedWeight = 0;
		int nIdx = -1;
		
		//First, determine IDF for each word in lstlstCollectionCandidate
		for (i=0; i<lstCollectionCandidate.size(); i++) {
			List<Double> lstIDF = null;

			if (i == nIgnoreIdx) {
				//continue;   //
			} else {
				AL_PlaceWebData placeWebData = lstCollectionCandidate.get(i);
				List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
				
				lstIDF = new ArrayList<Double>();
				
				for (j=0; j<lstKeywordWeight.size(); j++) {
					sWord = lstKeywordWeight.get(j).getKeyword();
					nOccurrenceCnt = CalculateWordOccurrenceCount(sWord, lstCollectionCandidate, nIgnoreIdx);
					if (nOccurrenceCnt > 0) {
						if (nIgnoreIdx != -1) {
							fIDF = 1 + Math.log((nPlaceCnt-1)/nOccurrenceCnt);
						} else {
							fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
						}
					} else {
						fIDF = 0;
					}
					
					lstIDF.add(fIDF);
				}
			}
			
			lstlstCollectionIDF.add(lstIDF);
		}
		
		//Second, determine IDF for each word in lstUnknown
		for (i=0; i<lstUnknown.size(); i++) {
			sWord = lstUnknown.get(i).getKeyword();
			nOccurrenceCnt = CalculateWordOccurrenceCount(sWord, lstCollectionCandidate, nIgnoreIdx);
			
			if (nOccurrenceCnt > 0) {
				if (nIgnoreIdx != -1) {
					fIDF = 1 + Math.log((nPlaceCnt-1)/nOccurrenceCnt);
				} else {
					fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
				}
			} else {
				fIDF = 0;
			}
			
			lstUnknownIDF.add(fIDF);
			
			fUnknownTotalWeight = fUnknownTotalWeight + Math.pow(fIDF*lstUnknown.get(i).getWeight(), 2);
		}
		
		fUnknownTotalWeight = Math.sqrt(fUnknownTotalWeight);
		
		//Third, calculate matching scores for each candidate place in lstlstCollectionCandidate
		for (i=0; i<lstCollectionCandidate.size(); i++) {

			fCandidateTotalWeight = 0;
			
			AL_PlaceWebData placeWebData = lstCollectionCandidate.get(i);
			
			if (i == nIgnoreIdx) {
				fMatchingScore = 0; 
			} else {			
				List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
				List<Double> lstIDF = lstlstCollectionIDF.get(i);
				
				for (j=0; j<lstKeywordWeight.size(); j++) {
					fCandidateTotalWeight = fCandidateTotalWeight + Math.pow(lstKeywordWeight.get(j).getWeight()*lstIDF.get(j).doubleValue(), 2);
				}
				
				fCandidateTotalWeight = Math.sqrt(fCandidateTotalWeight);
				
				fMatchedWeight = 0;
				
				for (j=0; j<lstUnknown.size(); j++) {
					sWord = lstUnknown.get(j).getKeyword();
					
					nIdx = -1;
					for (k=0; k<lstKeywordWeight.size(); k++) {
						if (sWord.compareToIgnoreCase(lstKeywordWeight.get(k).getKeyword()) == 0) {
							nIdx = k;
							break;
						}
					}
					
					if (nIdx != -1) {
						fMatchedWeight = fMatchedWeight + lstUnknown.get(j).getWeight()*lstUnknownIDF.get(j).doubleValue()*lstKeywordWeight.get(nIdx).getWeight()*lstIDF.get(nIdx).doubleValue();
					}
					
				}
				
				if (fUnknownTotalWeight == 0 || fCandidateTotalWeight == 0) {
					fMatchingScore = 0;
				} else {
					fMatchingScore = fMatchedWeight/(fUnknownTotalWeight*fCandidateTotalWeight);
				}
				
				fMatchingScore = Math.round(fMatchingScore*1000)/1000.0;
			}
			
			lstfMatchingScore.add(fMatchingScore);
			lstPlaceName.add(placeWebData.getPlaceName());
		}
		
	}

	
	//Matching a set of words with a collection
	//@return value: lstPlaceName  (Candidate place name list)
	//			   : lstfMatchingScore (Matching score for each of these place name)
	public static void CalculateCollectionMatchingWeb(List<String> lstUnknownKeyword, List<Double> lstUnknownKeywordWeight, 
			                                       List<AL_PlaceWebData> lstCollectionCandidate, 
			                                       List<String> lstPlaceName, List<Double> lstfMatchingScore, int nIgnoreIdx) {
		int i, j, k;
		String sWord; 
		List<Double> lstUnknownIDF = new ArrayList<Double>();
		List<List<Double>> lstlstCollectionIDF = new ArrayList<List<Double>>();
		
		int nPlaceCnt = lstCollectionCandidate.size();
		int nOccurrenceCnt = 0;
		double fIDF = 0.0;
		double fMatchingScore = 0;
		
		double fUnknownTotalWeight = 0;
		double fCandidateTotalWeight = 0;
		double fMatchedWeight = 0;
		int nIdx = -1;
		
		
		//First, determine IDF for each word in lstlstCollectionCandidate
		for (i=0; i<lstCollectionCandidate.size(); i++) {
			List<Double> lstIDF = null;
			
			if (i == nIgnoreIdx) {
				//continue;   //
			} else {
			
				AL_PlaceWebData placeWebData = lstCollectionCandidate.get(i);
				List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
				
				lstIDF = new ArrayList<Double>();
				
				for (j=0; j<lstKeywordWeight.size(); j++) {
					sWord = lstKeywordWeight.get(j).getKeyword();
					nOccurrenceCnt = CalculateWordOccurrenceCount(sWord, lstCollectionCandidate, nIgnoreIdx);
					if (nOccurrenceCnt > 0) {
						if (nIgnoreIdx != -1) {
							fIDF = 1 + Math.log((nPlaceCnt-1)/nOccurrenceCnt);
						} else {
							fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
						}
					} else {
						fIDF = 0;
					}
					
					lstIDF.add(fIDF);
				}
			
			}
			
			lstlstCollectionIDF.add(lstIDF);
		}
		
		
		//Second, determine IDF for each word in lstUnknown
		for (i=0; i<lstUnknownKeyword.size(); i++) {
			sWord = lstUnknownKeyword.get(i);
			nOccurrenceCnt = CalculateWordOccurrenceCount(sWord, lstCollectionCandidate, nIgnoreIdx);
			
			if (nOccurrenceCnt > 0) {
				if (nIgnoreIdx != -1) {
					fIDF = 1 + Math.log((nPlaceCnt-1)/nOccurrenceCnt);
				} else {
					fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
				}
			} else {
				fIDF = 0;
			}
			
			lstUnknownIDF.add(fIDF);
			
			fUnknownTotalWeight = fUnknownTotalWeight + Math.pow(fIDF*lstUnknownKeywordWeight.get(i), 2);
		}
		
		fUnknownTotalWeight = Math.sqrt(fUnknownTotalWeight);
		
		
		//Third, calculate matching scores for each candidate place in lstlstCollectionCandidate
		for (i=0; i<lstCollectionCandidate.size(); i++) {
			fCandidateTotalWeight = 0;
			
			AL_PlaceWebData placeWebData = lstCollectionCandidate.get(i);
			
			if (i == nIgnoreIdx) {
				fMatchingScore = 0; 
			} else {			
			
				List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
				List<Double> lstIDF = lstlstCollectionIDF.get(i);
				
				for (j=0; j<lstKeywordWeight.size(); j++) {
					fCandidateTotalWeight = fCandidateTotalWeight + Math.pow(lstKeywordWeight.get(j).getWeight()*lstIDF.get(j).doubleValue(),2);
				}
				
				fCandidateTotalWeight = Math.sqrt(fCandidateTotalWeight);
				
				fMatchedWeight = 0;
				
				for (j=0; j<lstUnknownKeyword.size(); j++) {
					sWord = lstUnknownKeyword.get(j);
					
					nIdx = -1;
					for (k=0; k<lstKeywordWeight.size(); k++) {
						if (sWord.compareToIgnoreCase(lstKeywordWeight.get(k).getKeyword()) == 0) {
							nIdx = k;
							break;
						}
					}
					
					if (nIdx != -1) {
						fMatchedWeight = fMatchedWeight + lstUnknownKeywordWeight.get(j)*lstUnknownIDF.get(j).doubleValue()*lstKeywordWeight.get(nIdx).getWeight()*lstIDF.get(nIdx).doubleValue();
					}
					
				}
				
				if (fUnknownTotalWeight == 0 || fCandidateTotalWeight == 0) {
					fMatchingScore = 0;
				} else {
					fMatchingScore = fMatchedWeight/(fUnknownTotalWeight*fCandidateTotalWeight);
				}
				
				fMatchingScore = Math.round(fMatchingScore*1000)/1000.0;
			}
			
			lstfMatchingScore.add(fMatchingScore);
			lstPlaceName.add(placeWebData.getPlaceName());
		}
		
	}


	//Matching a set of words with a collection
	//@return value: lstPlaceName  (Candidate place name list)
	//			   : lstfMatchingScore (Matching score for each of these place name)
	public static void CalculateCollectionMatching(List<String> lstSrcWord, List<Double> lstSrcTF, 
			                                       List<List<String>> lstlstCandidateWord, List<List<Double>> lstlstCandidateWeight,
												   List<Double> lstfMatchingScore, int nIgnoreIdx) {
		int i, j, k;
		String sWord; 
		List<Double> lstSrcIDF = new ArrayList<Double>();
		List<List<Double>> lstlstCollectionIDF = new ArrayList<List<Double>>();
		
		int nPlaceCnt = lstlstCandidateWord.size();
		int nOccurrenceCnt = 0;
		double fIDF = 0.0;
		double fMatchingScore = 0;
		
		double fSrcTotalWeight = 0;
		double fCandidateTotalWeight = 0;
		double fMatchedWeight = 0;
		int nIdx = -1;
		
		
		//First, determine IDF for each word in lstlstCollectionCandidate
		for (i=0; i<lstlstCandidateWord.size(); i++) {
			List<Double> lstIDF = null;
			
			if (i == nIgnoreIdx) {
				//continue;   //
			} else {				
				List<String> lstCandidateWord = lstlstCandidateWord.get(i);
				
				lstIDF = new ArrayList<Double>();
				
				for (j=0; j<lstCandidateWord.size(); j++) {
					sWord = lstCandidateWord.get(j);
					nOccurrenceCnt = CalculateWordOccurrenceCountCommon(sWord, lstlstCandidateWord, nIgnoreIdx);
					if (nOccurrenceCnt > 0) {
						if (nIgnoreIdx != -1) {
							fIDF = 1 + Math.log((nPlaceCnt-1)/nOccurrenceCnt);
						} else {
							fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
						}
					} else {
						fIDF = 0;
					}
					
					lstIDF.add(fIDF);
				}
			
			}
			
			lstlstCollectionIDF.add(lstIDF);
		}
		
		
		//Second, determine IDF for each word in lstUnknown
		for (i=0; i<lstSrcWord.size(); i++) {
			sWord = lstSrcWord.get(i);
			nOccurrenceCnt = CalculateWordOccurrenceCountCommon(sWord, lstlstCandidateWord, nIgnoreIdx);
			
			if (nOccurrenceCnt > 0) {
				if (nIgnoreIdx != -1) {
					fIDF = 1 + Math.log((nPlaceCnt-1)/nOccurrenceCnt);
				} else {
					fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
				}
			} else {
				fIDF = 0;
			}
			
			lstSrcIDF.add(fIDF);
			
			fSrcTotalWeight = fSrcTotalWeight + Math.pow(fIDF*lstSrcTF.get(i), 2);
		}
		
		fSrcTotalWeight = Math.sqrt(fSrcTotalWeight);
		
		
		//Third, calculate matching scores for each candidate place in lstlstCandidateInStoreOCRedWord
		for (i=0; i<lstlstCandidateWord.size(); i++) {
			fCandidateTotalWeight = 0;
						
			if (i == nIgnoreIdx) {
				fMatchingScore = 0; 
			} else {			
			
				List<String> lstCandidateWord = lstlstCandidateWord.get(i);
				List<Double> lstCandidateWeight = lstlstCandidateWeight.get(i);				
				List<Double> lstIDF = lstlstCollectionIDF.get(i);
				
				for (j=0; j<lstCandidateWeight.size(); j++) {
					fCandidateTotalWeight = fCandidateTotalWeight + Math.pow(lstCandidateWeight.get(j)*lstIDF.get(j).doubleValue(),2);
				}
				
				fCandidateTotalWeight = Math.sqrt(fCandidateTotalWeight);
				
				fMatchedWeight = 0;
				
				for (j=0; j<lstSrcWord.size(); j++) {
					sWord = lstSrcWord.get(j);
					
					nIdx = -1;
					for (k=0; k<lstCandidateWord.size(); k++) {
						if (sWord.compareToIgnoreCase(lstCandidateWord.get(k)) == 0) {
							nIdx = k;
							break;
						}
					}
					
					if (nIdx != -1) {
						fMatchedWeight = fMatchedWeight + lstSrcTF.get(j)*lstSrcIDF.get(j).doubleValue()*lstCandidateWeight.get(nIdx)*lstIDF.get(nIdx).doubleValue();
					}
					
				}
				
				if (fSrcTotalWeight == 0 || fCandidateTotalWeight == 0) {
					fMatchingScore = 0;
				} else {
					fMatchingScore = fMatchedWeight/(fSrcTotalWeight*fCandidateTotalWeight);
				}
				
				fMatchingScore = Math.round(fMatchingScore*1000)/1000.0;
			}
			
			lstfMatchingScore.add(fMatchingScore);
		}
		
	}
	
	
	
	
	//Matching a set of words with a collection
	//@return value: lstPlaceName  (Candidate place name list)
	//			   : lstfMatchingScore (Matching score for each of these place name)
	public static void CalculateCollectionMatching_withSaving(FileWriter fwMatchedCommonWordsFile, String sGroundTruthPlaceName, List<String> lstUnknownKeyword, List<Double> lstUnknownKeywordWeight, List<AL_PlaceWebData> lstCollectionCandidate, List<String> lstPlaceName, List<Double> lstfMatchingScore, int nIgnoreIdx) {
		int i, j, k;
		String sWord; 
		List<Double> lstUnknownIDF = new ArrayList<Double>();
		List<List<Double>> lstlstCollectionIDF = new ArrayList<List<Double>>();
		
		int nPlaceCnt = lstCollectionCandidate.size();
		int nOccurrenceCnt = 0;
		double fIDF = 0.0;
		double fMatchingScore = 0;
		
		double fUnknownTotalWeight = 0;
		double fCandidateTotalWeight = 0;
		double fMatchedWeight = 0;
		int nIdx = -1;
		
		String sLine = "";
		
		//First, determine IDF for each word in lstlstCollectionCandidate
		for (i=0; i<lstCollectionCandidate.size(); i++) {
			List<Double> lstIDF = null;
			
			if (i == nIgnoreIdx) {
				//continue;   //
			} else {

				AL_PlaceWebData placeWebData = lstCollectionCandidate.get(i);
				List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
				
				lstIDF = new ArrayList<Double>();
				
				for (j=0; j<lstKeywordWeight.size(); j++) {
					sWord = lstKeywordWeight.get(j).getKeyword();
					nOccurrenceCnt = CalculateWordOccurrenceCount(sWord, lstCollectionCandidate, nIgnoreIdx);
					if (nOccurrenceCnt > 0) {
						if (nIgnoreIdx != -1) {
							fIDF = 1 + Math.log((nPlaceCnt-1)/nOccurrenceCnt);
						} else {
							fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
						}
					} else {
						fIDF = 0;
					}
					
					lstIDF.add(fIDF);
				}
			}
			
			lstlstCollectionIDF.add(lstIDF);
		}
		
		//Second, determine IDF for each word in lstUnknown
		for (i=0; i<lstUnknownKeyword.size(); i++) {
			sWord = lstUnknownKeyword.get(i);
			nOccurrenceCnt = CalculateWordOccurrenceCount(sWord, lstCollectionCandidate, nIgnoreIdx);
			
			if (nOccurrenceCnt > 0) {
				if (nIgnoreIdx != -1) {
					fIDF = 1 + Math.log((nPlaceCnt-1)/nOccurrenceCnt);
				} else {
					fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
				}
			} else {
				fIDF = 0;
			}
			
			lstUnknownIDF.add(fIDF);
			
			fUnknownTotalWeight = fUnknownTotalWeight + Math.pow(fIDF*lstUnknownKeywordWeight.get(i), 2);
		}
		
		fUnknownTotalWeight = Math.sqrt(fUnknownTotalWeight);
		
		
		//Third, calculate matching scores for each candidate place in lstlstCollectionCandidate
		for (i=0; i<lstCollectionCandidate.size(); i++) {
			fCandidateTotalWeight = 0;
			
			AL_PlaceWebData placeWebData = lstCollectionCandidate.get(i);
			
			if (i == nIgnoreIdx) {
				fMatchingScore = 0; 
			} else {			
			
				List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
				List<Double> lstIDF = lstlstCollectionIDF.get(i);
				
				//For Saving in matchedcommonword file
				try {
					sLine = "%[GT] " + sGroundTruthPlaceName + "    [Candidate] " + placeWebData.getPlaceName() + "\n";
					fwMatchedCommonWordsFile.write(sLine);
				} catch (Exception e) {
					
				}
				//
				
				for (j=0; j<lstKeywordWeight.size(); j++) {
					fCandidateTotalWeight = fCandidateTotalWeight + Math.pow(lstKeywordWeight.get(j).getWeight()*lstIDF.get(j).doubleValue(),2);
				}
				
				fCandidateTotalWeight = Math.sqrt(fCandidateTotalWeight);
				
				fMatchedWeight = 0;
				
				sLine = "";
				for (j=0; j<lstUnknownKeyword.size(); j++) {
					sWord = lstUnknownKeyword.get(j);
					
					nIdx = -1;
					for (k=0; k<lstKeywordWeight.size(); k++) {
						if (sWord.compareToIgnoreCase(lstKeywordWeight.get(k).getKeyword()) == 0) {
							nIdx = k;
							
							sLine = sLine + sWord.trim() + ",";
							
							break;
						}
					}
					
					if (nIdx != -1) {
						fMatchedWeight = fMatchedWeight + lstUnknownKeywordWeight.get(j)*lstUnknownIDF.get(j).doubleValue()*lstKeywordWeight.get(nIdx).getWeight()*lstIDF.get(nIdx).doubleValue();
					}
					
				}
				
				sLine = sLine + "\n";			
				//For Saving in matchedcommonword file
				try {
					fwMatchedCommonWordsFile.write(sLine);
				} catch (Exception e) {
					
				}
				//
				
				if (fUnknownTotalWeight == 0 || fCandidateTotalWeight == 0) {
					fMatchingScore = 0;
				} else {
					fMatchingScore = fMatchedWeight/(fUnknownTotalWeight*fCandidateTotalWeight);
				}
				
				fMatchingScore = Math.round(fMatchingScore*1000)/1000.0;

				sLine = "[Matching Score] " + fMatchingScore + "\n\n";
				
				//For Saving in matchedcommonword file
				try {
					fwMatchedCommonWordsFile.write(sLine);
				} catch (Exception e) {
					
				}
				//

			} 
			
			lstfMatchingScore.add(fMatchingScore);
			lstPlaceName.add(placeWebData.getPlaceName());
		}

		
	}
	

	public static boolean CheckFrameWordExistance(List<AL_KeywordWeight> lstKeywordWeight, List<String> lstWords) {
		boolean bExist = false;
		int i, j;
		String sKeyword = "";
		
		for (i=0; i<lstKeywordWeight.size(); i++) {
			sKeyword = lstKeywordWeight.get(i).getKeyword();
			
			for (j=0; j<lstWords.size(); j++) {
				if (sKeyword.compareToIgnoreCase(lstWords.get(j)) == 0) {
					bExist = true;
					return bExist;
				}
			}
			
		}
		
		return bExist;
	}

	////////////////////////////////////////////////////////////////////////
	//This function matches keywords with TF-IDF weight
	public static boolean checkFrameRelatedWebCalB(List<AL_PlaceWebData> lstPlaceWebData, int nWebIndex, List<String> lstWords) {
		boolean bMatched = false;
		int nHitCnt = 0;
		
		nHitCnt = calculateHit(lstPlaceWebData.get(nWebIndex).getPlaceName(), lstWords);
		
		if (nHitCnt > 0) {
			bMatched = true;
		}
		
		return bMatched;
	}
	
	////////////////////////////////////////////////////////////////////////
	//This function matches keywords with TF-IDF weight
	public static List<Integer> checkFrameRelatedWeb(List<AL_PlaceWebData> lstPlaceWebData, List<String> lstWords) {
		int i;
		boolean bExist = false;
		
		List<String> lstPlaceNames = new ArrayList<String>();
		
		
		for (i=0; i<lstPlaceWebData.size(); i++) {
			lstPlaceNames.add(lstPlaceWebData.get(i).getPlaceName());
		}
		
		if (lstWords == null || lstWords.size() == 0) {
			return null;
		}
		
		List<Integer> lstMatchedPlaceIdx = null;
		
		if (Utility.MATCH_WITH_PLACENAME) {
			lstMatchedPlaceIdx = Utility.matchOCRedWordsWithPlaceNames(lstPlaceNames, lstWords);
		}

		if (lstMatchedPlaceIdx == null) {  //No place name is matched, so need to match every place's keywords

			for (i=0; i<lstPlaceWebData.size(); i++) {
				List<AL_KeywordWeight> lstKeywordWeight = lstPlaceWebData.get(i).getKeywordWeight();
				
				bExist = false;
				
				bExist = Utility.CheckFrameWordExistance(lstKeywordWeight, lstWords);
				
				if (bExist == true) {
					if (lstMatchedPlaceIdx == null) {
						lstMatchedPlaceIdx = new ArrayList<Integer>();
					}
					
					lstMatchedPlaceIdx.add(i);
				}
				
			}
			
		} 
		
		return lstMatchedPlaceIdx;
	}

	
	//Matching a set of words with a collection
	//@return value: lstPlaceName  (Candidate place name list)
	//			   : lstfMatchingScore (Matching score for each of these place name)
	public static void CalculateCollectionMatching_Clustering(List<String> lstUnknownKeyword, List<Double> lstUnknownKeywordWeight, 
			                                                  List<AL_PlaceWebData> lstCollectionCandidate, List<List<Double>> lstlstCollectionIDF, 
			                                                  List<Double> lstfMatchingScore) {
		int i, j, k;
		String sWord; 
		List<Double> lstUnknownIDF = new ArrayList<Double>();
		
		int nPlaceCnt = lstCollectionCandidate.size();
		int nOccurrenceCnt = 0;
		double fIDF = 0.0;
		double fMatchingScore = 0;
		
		double fUnknownTotalWeight = 0;
		double fCandidateTotalWeight = 0;
		double fMatchedWeight = 0;
		int nIdx = -1;
		
		//First, determine IDF for each word in lstUnknown
		for (i=0; i<lstUnknownKeyword.size(); i++) {
			sWord = lstUnknownKeyword.get(i);
			nOccurrenceCnt = CalculateWordOccurrenceCount(sWord, lstCollectionCandidate, -1);
			
			if (nOccurrenceCnt > 0) {
				fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
			} else {
				fIDF = 0;
			}
			
			lstUnknownIDF.add(fIDF);
			
			fUnknownTotalWeight = fUnknownTotalWeight + Math.pow(fIDF*lstUnknownKeywordWeight.get(i), 2);
		}
		
		fUnknownTotalWeight = Math.sqrt(fUnknownTotalWeight);
		
		//Second, calculate matching scores for each candidate place in lstCollectionCandidate
		for (i=0; i<lstCollectionCandidate.size(); i++) {
			fCandidateTotalWeight = 0;
			
			AL_PlaceWebData placeWebData = lstCollectionCandidate.get(i);
			
			List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
			List<Double> lstIDF = lstlstCollectionIDF.get(i);
			
			for (j=0; j<lstKeywordWeight.size(); j++) {
				fCandidateTotalWeight = fCandidateTotalWeight + Math.pow(lstKeywordWeight.get(j).getWeight()*lstIDF.get(j).doubleValue(),2);
			}
			
			fCandidateTotalWeight = Math.sqrt(fCandidateTotalWeight);
			
			fMatchedWeight = 0;
			
			for (j=0; j<lstUnknownKeyword.size(); j++) {
				sWord = lstUnknownKeyword.get(j);
				
				nIdx = -1;
				for (k=0; k<lstKeywordWeight.size(); k++) {
					if (sWord.compareToIgnoreCase(lstKeywordWeight.get(k).getKeyword()) == 0) {
						nIdx = k;				
						break;
					}
				}
				
				if (nIdx != -1) {
					fMatchedWeight = fMatchedWeight + lstUnknownKeywordWeight.get(j)*lstUnknownIDF.get(j).doubleValue()*lstKeywordWeight.get(nIdx).getWeight()*lstIDF.get(nIdx).doubleValue();
				}
			}
							
			if (fUnknownTotalWeight == 0 || fCandidateTotalWeight == 0) {
				fMatchingScore = 0;
			} else {
				fMatchingScore = fMatchedWeight/(fUnknownTotalWeight*fCandidateTotalWeight);
			}
						
			lstfMatchingScore.add(fMatchingScore);
		}

	}
	
	
	//Matching a set of words with a collection
	//@return value: lstPlaceName  (Candidate place name list)
	//			   : lstfMatchingScore (Matching score for each of these place name)
	public static void CalculateCollectionMatching_withSaving_TFOnly(FileWriter fwMatchedCommonWordsFile, String sGroundTruthPlaceName, List<String> lstUnknownKeyword, List<Double> lstUnknownKeywordWeight, List<AL_PlaceWebData> lstCollectionCandidate, List<String> lstPlaceName, List<Double> lstfMatchingScore, int nIgnoreIdx) {
		int i, j, k;
		String sWord; 
		List<Double> lstUnknownIDF = new ArrayList<Double>();
		List<List<Double>> lstlstCollectionIDF = new ArrayList<List<Double>>();
		
		int nPlaceCnt = lstCollectionCandidate.size();
		int nOccurrenceCnt = 0;
		double fIDF = 0.0;
		double fMatchingScore = 0;
		
		double fUnknownTotalWeight = 0;
		double fCandidateTotalWeight = 0;
		double fMatchedWeight = 0;
		int nIdx = -1;
		
		String sLine = "";
		
		//FIRST, determine IDF for each word in lstlstCollectionCandidate
		for (i=0; i<lstCollectionCandidate.size(); i++) {
			List<Double> lstIDF = null;
			
			if (i == nIgnoreIdx) {
				//continue;   //
			} else {

				AL_PlaceWebData placeWebData = lstCollectionCandidate.get(i);
				List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
				
				lstIDF = new ArrayList<Double>();
				
				for (j=0; j<lstKeywordWeight.size(); j++) {
					sWord = lstKeywordWeight.get(j).getKeyword();
					nOccurrenceCnt = CalculateWordOccurrenceCount(sWord, lstCollectionCandidate, nIgnoreIdx);
					if (nOccurrenceCnt > 0) {
						if (nIgnoreIdx != -1) {
							fIDF = 1 + Math.log((nPlaceCnt-1)/nOccurrenceCnt);
						} else {
							fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
						}
					} else {
						fIDF = 0;
					}
					
					//if (USE_TF_IDF_WEIGHT) {
					//	lstIDF.add(fIDF);  //TF-IDF solution
					//} else {
						fIDF = 1.0;
						lstIDF.add(fIDF);  //Non TF-IDF solution, or TF-Only
					//}
				}
			}
			
			lstlstCollectionIDF.add(lstIDF);
		}
		
		//SECOND, determine IDF for each word in lstUnknown
		for (i=0; i<lstUnknownKeyword.size(); i++) {
			sWord = lstUnknownKeyword.get(i);
			nOccurrenceCnt = CalculateWordOccurrenceCount(sWord, lstCollectionCandidate, nIgnoreIdx);
			
			if (nOccurrenceCnt > 0) {
				if (nIgnoreIdx != -1) {
					fIDF = 1 + Math.log((nPlaceCnt-1)/nOccurrenceCnt);
				} else {
					fIDF = 1 + Math.log(nPlaceCnt/nOccurrenceCnt);
				}
			} else {
				fIDF = 0;
			}
			
			//if (USE_TF_IDF_WEIGHT) {
			//	lstUnknownIDF.add(fIDF);   //TF-IDF solution
			//} else {
				fIDF = 1.0;
				lstUnknownIDF.add(fIDF);
			//}
			
			fUnknownTotalWeight = fUnknownTotalWeight + Math.pow(fIDF*lstUnknownKeywordWeight.get(i), 2);
		}
		
		fUnknownTotalWeight = Math.sqrt(fUnknownTotalWeight);
		
		
		//THIRD, calculate matching scores for each candidate place in lstlstCollectionCandidate
		for (i=0; i<lstCollectionCandidate.size(); i++) {
			fCandidateTotalWeight = 0;
			
			AL_PlaceWebData placeWebData = lstCollectionCandidate.get(i);
			
			if (i == nIgnoreIdx) {
				fMatchingScore = 0; 
			} else {			
			
				List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
				List<Double> lstIDF = lstlstCollectionIDF.get(i);
				
				//For Saving in matchedcommonword file
				try {
					sLine = "%[GT] " + sGroundTruthPlaceName + "    [Candidate] " + placeWebData.getPlaceName() + "\n";
					fwMatchedCommonWordsFile.write(sLine);
				} catch (Exception e) {
					
				}
				//
				
				for (j=0; j<lstKeywordWeight.size(); j++) {
					fCandidateTotalWeight = fCandidateTotalWeight + Math.pow(lstKeywordWeight.get(j).getWeight()*lstIDF.get(j).doubleValue(),2);
				}
				
				fCandidateTotalWeight = Math.sqrt(fCandidateTotalWeight);
				
				fMatchedWeight = 0;
				
				fMatchingScore = 0;
				
				sLine = "";
				for (j=0; j<lstUnknownKeyword.size(); j++) {
					sWord = lstUnknownKeyword.get(j);
					
					nIdx = -1;
					for (k=0; k<lstKeywordWeight.size(); k++) {
						if (sWord.compareToIgnoreCase(lstKeywordWeight.get(k).getKeyword()) == 0) {
							nIdx = k;
							
							sLine = sLine + sWord.trim() + ",";
							
							break;
						}
					}
					
					if (nIdx != -1) {
						
						if (USE_TF_IDF_WEIGHT) {
							fMatchedWeight = fMatchedWeight + lstUnknownKeywordWeight.get(j)*lstUnknownIDF.get(j).doubleValue()*lstKeywordWeight.get(nIdx).getWeight()*lstIDF.get(nIdx).doubleValue();
						} else {
							fMatchingScore = fMatchingScore + lstUnknownKeywordWeight.get(j)*lstKeywordWeight.get(nIdx).getWeight()/(lstUnknownKeywordWeight.get(j) + lstKeywordWeight.get(nIdx).getWeight());
						}
					}
					
				}
				
				sLine = sLine + "\n";			
				//For Saving in matchedcommonword file
				try {
					fwMatchedCommonWordsFile.write(sLine);
				} catch (Exception e) {
					
				}
				//
				
				
				if (USE_TF_IDF_WEIGHT) {
					if (fUnknownTotalWeight == 0 || fCandidateTotalWeight == 0) {
						fMatchingScore = 0;
					} else {
						fMatchingScore = fMatchedWeight/(fUnknownTotalWeight*fCandidateTotalWeight);
					}
				} else {
					fMatchingScore = fMatchingScore * 2;
				}
				
				fMatchingScore = Math.round(fMatchingScore*1000)/1000.0;

				sLine = "[Matching Score] " + fMatchingScore + "\n\n";
				
				//For Saving in matchedcommonword file
				try {
					fwMatchedCommonWordsFile.write(sLine);
				} catch (Exception e) {
					
				}
				//

			} 
			
			lstfMatchingScore.add(fMatchingScore);
			lstPlaceName.add(placeWebData.getPlaceName());
		}
		
	}
	
	
	
	public static List<String> getCMFileList(String sDataFolder) {
		List<String> lstCMFileList = null;
		
	   	File flFolder;
		flFolder = new File(sDataFolder);
		
		if (flFolder.exists()) {
			File[] files = flFolder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.startsWith(KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE);
				}
			});

			if (files != null && files.length > 0) {
				lstCMFileList = new ArrayList<String>();
				
				for (int i=0; i<files.length; i++) {
					if (files[i].getAbsolutePath().indexOf(".csv") != -1 && files[i].getAbsolutePath().indexOf(RANK_FILE_POSTFIX) == -1) {
						lstCMFileList.add(files[i].getAbsolutePath());
					}
				}
			}
		
		}
				
		return lstCMFileList;
	}

	
	public static int extractFrameValue(String sFullPathFileName) {
		int nFrameCnt = Utility.NO_FRAME_SAMPLE;
		String sFrameCnt;
		int nAllPicFileNameLen;
		int nLastUnderScorePos;
		int nStartPos;
		String sFileName = "";
		int nFileNameStartPos;
		
		nFileNameStartPos = sFullPathFileName.indexOf(KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE);
		sFileName = sFullPathFileName.substring(nFileNameStartPos);
		
		if (sFileName.indexOf(OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME) != -1) {
			nAllPicFileNameLen = KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE.length() + OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME.length() + String.valueOf("Matching").length() + 14 + 2 + 4;
			if (sFileName.length() == nAllPicFileNameLen) {  //All
				
			} else {
				nStartPos = KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE.length() + OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME.length() + String.valueOf("Matching").length() + 14 + 3;
				nLastUnderScorePos = sFileName.lastIndexOf("_");
				sFrameCnt = sFileName.substring(nStartPos, nLastUnderScorePos);
				nFrameCnt = Integer.valueOf(sFrameCnt).intValue();
			}
			
		} else if (sFileName.indexOf(OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME) != -1) {
			nAllPicFileNameLen = KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE.length() + OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME.length() + String.valueOf("Matching").length() + 14 + 2 + 4;
			if (sFileName.length() == nAllPicFileNameLen) {  //All
				
			} else {
				nStartPos = KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE.length() + OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME.length() + String.valueOf("Matching").length() + 14 + 3;
				nLastUnderScorePos = sFileName.lastIndexOf("_");
				
				sFrameCnt = sFileName.substring(nStartPos, nLastUnderScorePos);
				nFrameCnt = Integer.valueOf(sFrameCnt).intValue();
			}
			
		}
		
		return nFrameCnt;
	}
	
	
	public static List<Integer> getCMFile_FrameCntList(String sDataFolder) {
		List<Integer> lstFrameCnt = new ArrayList<Integer>();
		int nFrameCnt = Utility.NO_FRAME_SAMPLE;
		boolean bExist = false;
		int nPos = 0;
		
		List<String> lstCMFileList = getCMFileList(sDataFolder);
		
		for (String sCMFile : lstCMFileList) {
			nFrameCnt = extractFrameValue(sCMFile);
			nPos = -1;
			bExist = false;
			for (int i=0; i<lstFrameCnt.size(); i++) {
				if (nFrameCnt == lstFrameCnt.get(i).intValue()) {
					bExist = true;
					break;
				} else if (nFrameCnt < lstFrameCnt.get(i).intValue()) {
					nPos = i;
					break;
				}
			}
			
			if (bExist == false) {
				if (nPos == -1) {
					lstFrameCnt.add(nFrameCnt);
				} else {
					lstFrameCnt.add(nPos, nFrameCnt);
				}
			}
			
		}
		
		return lstFrameCnt;
	}
	
	
	public static boolean IsAllFrameFile(String sFileName) {
		boolean bIsAllFrame = false;
		int nAllFrameFileNameLenWithPlaceName;
		int nAllFrameFileNameLenWithoutPlaceName;
		
		nAllFrameFileNameLenWithoutPlaceName = KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE.length() + OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME.length() + String.valueOf("Matching").length() + 14 + 2 + 4;
		
		nAllFrameFileNameLenWithPlaceName = KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE.length() + OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME.length() + String.valueOf("Matching").length() + 14 + 2 + 4;
		
		
		if (sFileName.indexOf(OCR_WEB_RESULT_SUB_FOLDER_WO_PLACENAME) != -1 && sFileName.length() == nAllFrameFileNameLenWithoutPlaceName) {
			bIsAllFrame = true;
		} else if (sFileName.indexOf(OCR_WEB_RESULT_SUB_FOLDER_W_PLACENAME) != -1 && sFileName.length() == nAllFrameFileNameLenWithPlaceName) {
			bIsAllFrame = true;
		}
		
		return bIsAllFrame;
	}
	
	
	public static List<List<String>> getCMFileList_MultipleIndexSet(String sDataFolder) {
		List<List<String>> lstlstCMFileList = new ArrayList<List<String>>();
		
		List<Integer> lstFrameCnt = new ArrayList<Integer>();
		
		lstFrameCnt = getCMFile_FrameCntList(sDataFolder);
		
		int nFrameCnt;
		String sFileIndexPosix = "";
		
		for (int i=0; i<lstFrameCnt.size(); i++) {
		   	File flFolder;
			flFolder = new File(sDataFolder);
			
			nFrameCnt = lstFrameCnt.get(i);
			
			if (flFolder.exists()) {
				List<String> lstCMFileList = new ArrayList<String>();

				File[] files = flFolder.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.startsWith(KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE);
					}
				});
				
	
				if (files != null && files.length > 0) {
					
					sFileIndexPosix = "_" + nFrameCnt + "_";
					
					if (nFrameCnt == Utility.NO_FRAME_SAMPLE) {
						for (int j=0; j<files.length; j++) {
							String sFileName = "";
							int nFileNameStartPos;
							
							nFileNameStartPos = files[j].getAbsolutePath().indexOf(KEYWORD_MATCHING_EVAL_CONFUSION_MATRIX_FILE_PRE);
							sFileName = files[j].getAbsolutePath().substring(nFileNameStartPos);
							
							if (IsAllFrameFile(sFileName) == true) {
								lstCMFileList.add(files[j].getAbsolutePath());
							}
							
						}
					} else {
						for (int j=0; j<files.length; j++) {
							if (files[j].getAbsolutePath().indexOf(".csv") != -1 && files[j].getAbsolutePath().indexOf(sFileIndexPosix) != -1 && files[j].getAbsolutePath().indexOf(RANK_FILE_POSTFIX) == -1) {
								lstCMFileList.add(files[j].getAbsolutePath());
							}
						}
					}
				}
				
				lstlstCMFileList.add(lstCMFileList);
			}
			
		}
		
		return lstlstCMFileList;
	}
	
	
	public static void extractCM(String sCMFIle, List<String> lstGTPlaceName, List<String> lstCandidatePlaceName, List<List<Double>> lstlstMatchingScore) {
		int i,j;
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		int nLineIdx = 0;
		List<String> lstGTPlaceNameTmp;
		List<String> lstCandidatePlaceNameTmp;
		
		try {
			fr = new FileReader(sCMFIle);
			br = new BufferedReader(fr);
		
			while ((sLine = br.readLine()) != null) {
				sLine = sLine.trim();
				if (sLine.startsWith("%")) continue;    // treat the beginning % as comment in OCRed text file
				
				nLineIdx = nLineIdx + 1;
				if (nLineIdx == 1) {
					lstGTPlaceNameTmp = getFieldsList(sLine);
					for (String sTmpGTPlaceName : lstGTPlaceNameTmp) {
						lstGTPlaceName.add(sTmpGTPlaceName);
					}
				} else if (nLineIdx == 2) {
					lstCandidatePlaceNameTmp = getFieldsList(sLine);
					for (String sTmpCandidatePlaceName : lstCandidatePlaceNameTmp) {
						lstCandidatePlaceName.add(sTmpCandidatePlaceName);
					}
					
				} else {
					List<Double> lstMatchingScore = new ArrayList<Double>();
					lstMatchingScore = getFieldsListValue(sLine);
					lstlstMatchingScore.add(lstMatchingScore);
				}

			}
			
			fr.close();
		} catch (Exception e) {
			
		}
		
	}
	
	
	//Calculate the ranking of the matching score between a store and a set of candidate places
	public static List<Integer> calculateMatchingScoreRanking(List<String> lstGTPlaceName, List<String> lstCandidatePlaceName, List<List<Double>> lstlstMatchingScore) {
		List<Integer> lstRanking = new ArrayList<Integer>();
		int i,j,k;
		String sGTPlaceName = "";
		String sCandidatePlaceName = "";
		double fMatchingScore = 0.0;
		int nPos;
		int nRank;
		
		if (lstlstMatchingScore == null) return lstRanking;
		
		for (i=0; i<lstGTPlaceName.size(); i++) {
			sGTPlaceName = lstGTPlaceName.get(i);
			
			List<Double> lstMatchingScore = lstlstMatchingScore.get(i);
			
			nPos = -1;
			for (j=0; j<lstCandidatePlaceName.size(); j++) {
				sCandidatePlaceName = lstCandidatePlaceName.get(j);
				if (sGTPlaceName.trim().compareToIgnoreCase(sCandidatePlaceName.trim()) == 0) {
					nPos = j;
					break;
				}
			}
			
			if (nPos != -1) {
				fMatchingScore = lstMatchingScore.get(nPos);
			} else {
				fMatchingScore = 0;
			}
			
			
			
			if (fMatchingScore == 0) {
				nRank = 9999;
			} else {
				nRank = 1;
				for (k=0; k<lstMatchingScore.size(); k++) {
					if (lstMatchingScore.get(k) > fMatchingScore) {
						nRank = nRank + 1;
					}
				}
			}
			
			lstRanking.add(nRank);
		}
		
		return lstRanking;
	}
	
	
	
	public static String getMatchingScoreRankingFile(String sFilePathName) {
		String sResultFile = "";
		String sWithoutPre = "KeywordMatchingCM_withoutPlaceNameMatching";
		String sWithPre = "KeywordMatchingCM_withPlaceNameMatching";
		
		int nStartPos = 0;
		int nPos = sFilePathName.lastIndexOf(".");
		int nLastUnderscorePos = sFilePathName.lastIndexOf("_");;
		
		nStartPos = sFilePathName.indexOf(sWithoutPre);
		if (nStartPos == -1) {
			nStartPos = sFilePathName.indexOf(sWithPre);
			
			if (nPos - nLastUnderscorePos < 8) {
				sResultFile = sFilePathName.substring(0, nStartPos + sWithPre.length()) + "_" + RANK_FILE_POSTFIX + "_" + sFilePathName.substring(nLastUnderscorePos+1, nPos) + ".csv";
			} else {
				sResultFile = sFilePathName.substring(0, nStartPos + sWithPre.length()) + "_" + RANK_FILE_POSTFIX + "_" + NO_FRAME_SAMPLE + ".csv";				
			}
			
		} else {
			if (nPos - nLastUnderscorePos < 8) {
				sResultFile = sFilePathName.substring(0, nStartPos + sWithoutPre.length()) + "_" + RANK_FILE_POSTFIX + "_" + sFilePathName.substring(nLastUnderscorePos+1, nPos) + ".csv";
			} else {
				sResultFile = sFilePathName.substring(0, nStartPos + sWithoutPre.length()) + "_" + RANK_FILE_POSTFIX + "_" + NO_FRAME_SAMPLE + ".csv";				
			}
			
		}
				
		return sResultFile;
	}
	

	public static String getMatchingScoreRankingFile_MultipleIndexSet(String sFilePathName) {
		String sResultFile = "";
		String sWithoutPre = "KeywordMatchingCM_withoutPlaceNameMatching";
		String sWithPre = "KeywordMatchingCM_withPlaceNameMatching";
		
		int nStartPos = 0;
		int nPos = sFilePathName.lastIndexOf(".");
		int nLastUnderscorePos = sFilePathName.lastIndexOf("_");
		
		int nFrameCnt = extractFrameValue(sFilePathName);
		
		nStartPos = sFilePathName.indexOf(sWithoutPre);
		if (nStartPos == -1) {
			nStartPos = sFilePathName.indexOf(sWithPre);
			
			sResultFile = sFilePathName.substring(0, nStartPos + sWithPre.length()) + "_" + RANK_FILE_POSTFIX + "_" + nFrameCnt + ".csv";
			
		} else {
			sResultFile = sFilePathName.substring(0, nStartPos + sWithoutPre.length()) + "_" + RANK_FILE_POSTFIX + "_" + nFrameCnt + ".csv";
			
		}
				
		return sResultFile;
	}
	
	
	public static String getFrameStatResultFile(String sResultFolder) {
	    Date dtFileStart = new Date();
 	    final String DATE_FORMAT = "yyyyMMddHHmmss";
 	    SimpleDateFormat spdCurrentTime = new SimpleDateFormat(DATE_FORMAT);

		String sFrameStatResultFile = sResultFolder + File.separator + FRAME_STAT_FILE_PRE + "_" + spdCurrentTime.format(dtFileStart) + ".csv";
		
		return sFrameStatResultFile;
	}
		
	public static void writeFrameIndex(String sParentOCRFolder, String sSubFolder, List<Integer> lstFrameIdx, int nFrameCnt) {
		String sFilePath = "";
		String sLine = "";

		if (nFrameCnt != NO_FRAME_SAMPLE) {
			sFilePath = sParentOCRFolder + File.separator + sSubFolder + File.separator + "FrameIndex_" + nFrameCnt + ".csv";
		} else {
			sFilePath = sParentOCRFolder + File.separator + sSubFolder + File.separator + "FrameIndex" + ".csv";
		}
		
		try {
			FileWriter fwFrameIndex = null;
			
			fwFrameIndex = new FileWriter(sFilePath, false);   //Overwrite
			
			for (int i=0; i<lstFrameIdx.size(); i++) {
				sLine = sLine + lstFrameIdx.get(i) + ",";
			}
			
			sLine = sLine + "\n";
			
			fwFrameIndex.write(sLine);
			
			fwFrameIndex.close();
		} catch (Exception e) {
			
		}
	}

	
	public static void writeFrameIndex_MultipleSet(String sParentOCRFolder, String sSubFolder, List<Integer> lstFrameIdx, int nFrameCnt, int nSetIndex) {
		String sFilePath = "";
		String sLine = "";

		if (nFrameCnt != NO_FRAME_SAMPLE) {
			sFilePath = sParentOCRFolder + File.separator + sSubFolder + File.separator + "FrameIndex_" + nFrameCnt + "_" + nSetIndex + ".csv";
		} else {
			sFilePath = sParentOCRFolder + File.separator + sSubFolder + File.separator + "FrameIndex" + ".csv";
		}
		
		try {
			FileWriter fwFrameIndex = null;
			
			fwFrameIndex = new FileWriter(sFilePath, false);   //Overwrite
			
			for (int i=0; i<lstFrameIdx.size(); i++) {
				sLine = sLine + lstFrameIdx.get(i) + ",";
			}
			
			sLine = sLine + "\n";
			
			fwFrameIndex.write(sLine);
			
			fwFrameIndex.close();
		} catch (Exception e) {
			
		}
	}
	
	
	public static List<Integer> getFrameIndex(String sParentOCRFolder, String sSubFolder, int nFrameCnt) {
		List<Integer> lstSelectedFrames = new ArrayList<Integer>();
		String sFilePath = "";
		
		if (nFrameCnt != NO_FRAME_SAMPLE) {
			sFilePath = sParentOCRFolder + File.separator + sSubFolder + File.separator + "FrameIndex_" + nFrameCnt + ".csv";
		} else {
			sFilePath = sParentOCRFolder + File.separator + sSubFolder + File.separator + "FrameIndex" + ".csv";
		}
		
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		
		try {
			fr = new FileReader(sFilePath);
			br = new BufferedReader(fr);
			List<String> lstValue = new ArrayList<String>();
			
			while ((sLine = br.readLine()) != null) {
				sLine = sLine.trim();
				if (sLine.startsWith("%")) continue;    // treat the beginning % as comment in OCRed text file

				lstValue = getFieldsList(sLine);
				
				break;
			}
			
			for (int i=0; i<lstValue.size(); i++) {
				lstSelectedFrames.add(Integer.valueOf(lstValue.get(i)));
			}
			
			fr.close();
		} catch (Exception e) {
			
		}

		return lstSelectedFrames;
	}

	
	public static List<Integer> getFrameIndex_MultipleIndexSet(String sParentOCRFolder, String sSubFolder, int nFrameCnt, int nSetIndex) {
		List<Integer> lstSelectedFrames = new ArrayList<Integer>();
		String sFilePath = "";
		
		if (nFrameCnt != NO_FRAME_SAMPLE) {
			sFilePath = sParentOCRFolder + File.separator + sSubFolder + File.separator + "FrameIndex_" + nFrameCnt + "_" + nSetIndex + ".csv";
		} else {
			sFilePath = sParentOCRFolder + File.separator + sSubFolder + File.separator + "FrameIndex" + ".csv";
		}
		
		FileReader fr = null;
		BufferedReader br = null;
		String sLine = "";
		
		try {
			fr = new FileReader(sFilePath);
			br = new BufferedReader(fr);
			List<String> lstValue = new ArrayList<String>();
			
			while ((sLine = br.readLine()) != null) {
				sLine = sLine.trim();
				if (sLine.startsWith("%")) continue;    // treat the beginning % as comment in OCRed text file

				lstValue = getFieldsList(sLine);
				
				break;
			}
			
			for (int i=0; i<lstValue.size(); i++) {
				lstSelectedFrames.add(Integer.valueOf(lstValue.get(i)));
			}
			
			fr.close();
		} catch (Exception e) {
			
		}

		return lstSelectedFrames;
	}
	
	
	
	//Calculate IDF for the selected Place Set
	//The resultant Place Web Data list is decided by lstSelectedPlaceIndex 
	//Return IDF for the words in selected places & select Place Web Data list
	public static List<List<Double>> calcIDFForSelectedSet(List<AL_PlaceWebData> lstAllPlaceWebData, List<Integer> lstSelectedPlaceIndex, List<AL_PlaceWebData> lstPlaceWebDataNew) {		
		String sWord; 
		int nOccurrenceCnt;
		List<List<Double>> lstlstPlaceWebDataNewIDF = new ArrayList<List<Double>>();
		double fIDF = 0.0;
		int i,j;
		int nIndex;
		
		for (i=0; i<lstSelectedPlaceIndex.size(); i++) {
			nIndex = lstSelectedPlaceIndex.get(i);
			
			lstPlaceWebDataNew.add(lstAllPlaceWebData.get(nIndex));
		}
				
		//Determine IDF for each word in lstPlaceWebDataNew
		for (i=0; i<lstPlaceWebDataNew.size(); i++) {
			List<Double> lstIDF = null;
			
			AL_PlaceWebData placeWebData = lstPlaceWebDataNew.get(i);
			List<AL_KeywordWeight> lstKeywordWeight = placeWebData.getKeywordWeight();
			
			lstIDF = new ArrayList<Double>();
			
			for (j=0; j<lstKeywordWeight.size(); j++) {
				sWord = lstKeywordWeight.get(j).getKeyword();
				nOccurrenceCnt = Utility.CalculateWordOccurrenceCount(sWord, lstPlaceWebDataNew, -1);
				if (nOccurrenceCnt > 0) {
					fIDF = 1 + Math.log(lstPlaceWebDataNew.size()/nOccurrenceCnt);
				} else {
					fIDF = 0;
				}
				
				lstIDF.add(fIDF);
			}
			
			lstlstPlaceWebDataNewIDF.add(lstIDF);
		}
		
		return lstlstPlaceWebDataNewIDF;
		
	}
	
	
	public static List<List<String>> getPlaceAPInfo(String sPlaceAPInfoFile, List<String> lstPlaceNameList) {
		int i;
    	FileReader fr;
    	BufferedReader br;
    	String sLine = "";
    	String[] fieldsData;
    	String[] fields;
//   	int nAPCount = 10;
    	int nAPCount = Utility.DEFAULT_AP_MAC_CNT;
//    	int nFieldCnt = 53;
    	int nFieldCnt = 3 + Utility.DEFAULT_AP_MAC_CNT*5;
    	    	
    	List<List<String>> lstlstMacList = new ArrayList<List<String>>();
    	
		try {
			fr = new FileReader(sPlaceAPInfoFile);
			br = new BufferedReader(fr);
			
			while( (sLine = br.readLine()) != null) {
				fields = new String[nFieldCnt];
				
				for (int j=0; j<nFieldCnt; j++) {
					fields[j]="";
				}
				
				fieldsData = sLine.split(",");
				for (int k=0; k<fieldsData.length; k++) {
					fields[k] = fieldsData[k];
				}
				
				lstPlaceNameList.add(fields[0].trim().toLowerCase());
				
				List<String> lstOneStoreMacList = new ArrayList<String>();
				for (int j=1; j<=nAPCount; j++) {
					String sMac = fields[(j-1)*5+3];
					double meanRss = Double.valueOf(fields[(j-1)*5+6]).doubleValue();
					
					if (meanRss <= -180) {
					} else {
						lstOneStoreMacList.add(sMac.trim().toLowerCase());
					}
				}
				
				lstlstMacList.add(lstOneStoreMacList);
			} // while
			
			fr.close();
		} catch (Exception e) {
			System.out.println("-----Read Place AP File----" + e.toString());			
		}
			
		return lstlstMacList;
	}	
	
	
}
