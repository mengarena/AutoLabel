package com.autolabel;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//This class is for Google Web search and Google Place search
public class GoogleSearch {
	private static int MAX_TITLE_CNT = 10000;
	private static int SEARCH_RADIUS = 800;
	private static int[] m_narrRadius = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};  
	
//	private static String GOOGLE_API_KEY = "AIzaSyCV8kvg-oGgPS3nVlKijrQzvjQG0Z0lmuk";  
	private static String GOOGLE_API_KEY = "AIzaSyAfddRQvtZ0hbosJWgAtlIZZL0LpF1ICpk";
	
	private static int RADIUS_STEP = 5;   // meters
	
	//######[Begin] Added for dynamic keys
	private static int m_nKeyIdx = 0;
	private static int m_nKeyStep = 0;
	private static int m_nSearchIdx = 0;
	
	private RepresentativeKeywordExtraction m_RepresentativeKeywordExtraction  = new RepresentativeKeywordExtraction();
	private WebImageExtraction m_WebImageExtraction = new WebImageExtraction();
	
	private static String[] strarrGoogleApiKeys = {
		//SsidPlaceMap Keys [Begin]
		"AIzaSyCmgwlv3F8iLIQgjKnkJh0mStpLjS9UrAo",
		"AIzaSyDrwkQRyoUtwSyaBha_qpaLW6nCT9lebCI",
		"AIzaSyCZHoOSohQG7pbqZjc9LK5tpROUnprFRvk",
		"AIzaSyDZ9ho6sCPnjHHO-555R2MjFLtuSOwBsSc",
		"AIzaSyCfa0E63019sxzT1DkWqCqfnQSsguO_zvM",
		"AIzaSyBQOe8yGMtnQ--3oSQfoshxBRnphi1u4Yg",
		"AIzaSyD2hqRptAlRtt-MMcDSX7K3FXBpdONbmfE",
		"AIzaSyCV8kvg-oGgPS3nVlKijrQzvjQG0Z0lmuk",

		//SSIDPlace Keys [Begin]
		"AIzaSyCceem9T3MjiTH9SkCZHuFHFhNH_CAMpQY",
		"AIzaSyBrx3BN6Yyp942M4V0QwSR_uj2Yek3w_Z4",
		"AIzaSyD_m6Xp0LkASEtZd3jIHh_FHOGSlGVv7WI",
		"AIzaSyAtcdrm5BOG6rTOvVBQuYlELx7nnXX6yn0",
		"AIzaSyCpczCe1YOPOev9UHRtLvZg9aGkQaoqhQI",
		"AIzaSyDvr9oo-3tUb3arUNt8Dn8QZ47Y6SeeVFU",
		"AIzaSyBWJ7O35v3SRVvbAmEspOEMVI9sGS6D7ic",
		"AIzaSyC2WPabs390QNJDqa36m9WcszCb7wjbOcU",
//		"AIzaSyDPRjdEBQKdlXgXIN6dGmyldxEt85sLdAs",
//		"AIzaSyCaAsthl2VURzEbcfxfZ4tFQxUACmYLuRg",
//		"AIzaSyC01HQkVelzBuYGIhibJn6fhWz1YOuS2w8",
//		"AIzaSyDI7bQ1mccDmORxaWhaejcA5qC8qfbcxUM",
//		"AIzaSyD6OG3BTNThhItf_KFu5ejOn7WhJ8AtKno",
//		"AIzaSyCkAEqJvuts05atgxvNXm6nvyd5Q6MGJoE",
//		"AIzaSyBob3k6zXrTyAbd9uNx-CYS2QEmAANK_b4",

				
		//GoogleS2 Keys [Begin]
		"AIzaSyC4rc11pOBp0QX6IQ6UfubSxK4xUhEDYn8",
		"AIzaSyBtLLaOBsb0bLjY3OezpDEjJZ7PsYQvv-k",
		"AIzaSyCcxVoUZyyaO8ey9vhkcnklZOWKSdhPGv0",
		"AIzaSyCvK7l6eimRYU76KBzxeoO1xjKQ2fbIGo4",
		"AIzaSyBD_7V0XP65L38xG3Sn1Q59aTrOd_1mOdk",
		"AIzaSyADgxfNUgLIVMoTi4ij6bKT8j4PMTu87jE",
		"AIzaSyAfddRQvtZ0hbosJWgAtlIZZL0LpF1ICpk",
		"AIzaSyCoVTTC-5pS9Lk40HUD1ucQ93Mhrn5AaBM"
	}; 
	//######[End] Added for dynamic keys
	
	
	private static String[] m_strarrIgnorePlaceType = {
		"administrative_area_level_1", "administrative_area_level_2", "administrative_area_level_3",
		"administrative_area_level_4", "administrative_area_level_5", "colloquial_area",
		"country", "floor", "geocode", "intersection", "locality", "natural_feature",
		"neighborhood", "political", "point_of_interest", "post_box", "postal_code", "postal_code_prefix",
		"postal_town", "premise", "room", "route", "street_address", "street_number",
		"sublocality", "sublocality_level_4", "sublocality_level_5", "sublocality_level_3",
		"sublocality_level_2", "sublocality_level_1", "subpremise", "transit_station"		
	};
	
	private static String SENSOR_STATUS = "sensor=false";
	private static String PLACE_TYPE = "airport|bank|bar|book_store|cafe|clothing_store|convenience_store|electronics_store|establishment|finance|florist|food|furniture_store|gas_station|grocery_or_supermarket|gym|hair_care|health|home_goods_store|hospital|jewelry_store|library|liquor_store|lodging|meal_delivery|meal_takeaway|movie_theater|museum|park|pharmacy|post_office|real_estate_agency|restaurant|school|shoe_store|shopping_mall|stadium|store|university|zoo";

	private List<String> m_lststrIgnorePlaceTypes = new ArrayList<String>();
	
	private List<String> m_lststrResultTitles = new ArrayList<String>();   //Store the titles returned from Google Web search
	private List<String> m_lststrPlaceNames = new ArrayList<String>();     //Store the full/exact names of the stores/businesses
	
	private List<String> m_lststrCandidatePlaces = new ArrayList<String>();  //The candidate place, which is derived from m_lststrPlaceNames by removing the duplicated place names
	
	private int m_nTopType = 1;  //1 = Top number 10;   2 = Top percentage 10=>0.1
	private int m_nTop = 10;
	private String m_sWebpageTopWordsFile = "E:\\UIUC\\WordFile\\WebpageWord\\TopFrequentWord";
	
	
	public GoogleSearch() {
		// TODO Auto-generated constructor stub
		for (int i=0; i<m_strarrIgnorePlaceType.length; i++) {
			m_lststrIgnorePlaceTypes.add(m_strarrIgnorePlaceType[i]);
		}
	}

	//######[Begin] Added for dynamic keys
	public void resetKeyIndex() {
		m_nKeyIdx = 0;
		m_nKeyStep = 0;
		m_nSearchIdx = 0;
	}
	
	public void setKeyStep(int nTotal) {
		m_nKeyStep = nTotal/strarrGoogleApiKeys.length + 1;
	}
	//######[End] Added for dynamic keys
	
	public List<String> getResultTitles() {
		return m_lststrResultTitles;
	}
	
	public List<String> getPlaceNames() {
		return m_lststrPlaceNames;
	}
	
    //Analyze the html file to locate and extract the Google suggested search phrase with "Did you mean"
    //The corresponding html part will be like:
    //##########################################
    //		>Did you mean:</ 
    //		<a class="spell" href="/search?ie=UTF-8&amp;q=cocomero+champaign+il&amp;spell=1&amp;sa=X&amp;ei=iiiZU8zmOoqayATrgoLIDQ&amp;ved=0CBwQBSgA">
    //				<b><i>cocomero</i></b> champaign il
    //		</a> 
    //##########################################
    //If didn't find it, return "", which means that Google does not suggest search phrase.
    private static String getDidYouMean(String strHtmlContent) {
		String strSuggestedPhrase = "";
		String strPosKeywords = ">Did you mean:</";
		String strCorrectPosStart = "<b><i>";
		String strCorrectPosEnd = "</a>";
		int nPos = -1;
		int nPosCorrectStart = -1;
		int nPosCorrectEnd = -1;
		String strParseContent = "";
		
		nPos = strHtmlContent.indexOf(strPosKeywords);
		if (nPos != -1) {
			strParseContent = strHtmlContent.substring(nPos + strPosKeywords.length());
			nPosCorrectStart = strParseContent.indexOf(strCorrectPosStart);
			
			if (nPosCorrectStart == -1) {
				return strSuggestedPhrase;
			}
			
			nPosCorrectEnd = strParseContent.indexOf(strCorrectPosEnd);
			if (nPosCorrectEnd == -1) {
				return strSuggestedPhrase;
			}
			
			strParseContent = strParseContent.substring(nPosCorrectStart,nPosCorrectEnd);
			
			strSuggestedPhrase = Jsoup.parse(strParseContent).text();
		}
		
		return strSuggestedPhrase;
	}
	
    
    //Get the content of the webpage for the given URL
    private static String getWebPageContent(String strURL) {
		URL url;
	    HttpURLConnection connection=null;  
	    String sSearchResult = "";
	    
	    try{
		    url=new URL(strURL);
		    connection= (HttpURLConnection) url.openConnection();
		    connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.52 Safari/536.5");
		    connection.connect();		  
		    InputStreamReader reader=new InputStreamReader(connection.getInputStream());
		    BufferedReader in=new BufferedReader(reader);
	        String line;
	        StringBuffer response=new StringBuffer(); 
	        
	        while((line=in.readLine())!=null) {
        		response.append(line);
        		//response.append("\n");
        	}
	        
	        in.close();

		    sSearchResult = response.toString();		    
//		    System.out.println(sSearchResult);
	      
	    } catch (Exception e) {
	    	//e.printStackTrace();
	    	return "";
	    }
    	
	    return sSearchResult;
    }
	
    
    //If "Did you mean" exists, return the suggested search phrase; otherwise, return ""
	private String checkDidYouMean(String sQuery) {
    	String sAddress = "";
    	String sCharset = "UTF-8";
		String sBase = "https://www.google.com/search?q=";
		String sSearchResult = "";
				
		try {
			sAddress = sBase + URLEncoder.encode(sQuery, sCharset);
			sSearchResult = getWebPageContent(sAddress);
			sSearchResult = getDidYouMean(sSearchResult);
		} catch (Exception o) {
			return "";
		}
				
		return sSearchResult;
    }
	
	
	//Google Web search with the given search phrase and store the returned titles
	private void doGoogleWebSearch(String sSearchPhrase) {
		String google = "http://www.google.com/search?q=";
		
		String charset = "UTF-8";
		String userAgent = "UIUC (+http://uiuc.edu/bot)"; // institute name and bot homepage
		int nResultIdx = 0;
		
		System.out.println("[Google Web search phrase]:  " + sSearchPhrase);
		
		try {
			Elements links = Jsoup.connect(google + URLEncoder.encode(sSearchPhrase, charset)).userAgent(userAgent).get().select("li.g>h3>a");
	
			for (Element link : links) {
			    String title = link.text();
			    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
			    
			    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
			    
			    System.out.println("===> " + url);
			    
			    if (!url.startsWith("http")) {
			        continue; // Ads/news/etc.
			    }
	
			    nResultIdx = nResultIdx + 1;
			    if (nResultIdx >  MAX_TITLE_CNT) break;
			    
			    //Store the titles
			    m_lststrResultTitles.add(title);
			}	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("############################Search is wrong############################");
		}
		
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			
		}
	}

	
	/////////////////////////////////////////////////////////////////
	//Google Place Search with Text
	//Return the number of places found
	/////////////////////////////////////////////////////////////////
	private int GooglePlace_Text(String strURL) {
		String sCharset = "UTF-8";
		GooglePlaceTextResults results = null;
		URL url = null;
		Reader reader = null;
		int nResultIdx = 0;
		String sSearchResult = "";
		int nPlaceCnt = 0;
		
		nPlaceCnt = MAX_TITLE_CNT;
			
		try {
			url = new URL(strURL);
			reader = new InputStreamReader(url.openStream(), sCharset);
		} catch (Exception o) {
			return nPlaceCnt;
		}
		
		results = new Gson().fromJson(reader, GooglePlaceTextResults.class);
					
		int total = results.getResults().size();
	
		if (nPlaceCnt > total) nPlaceCnt = total;
		
		// Show title and URL of each results in ListView
		for(int i=0; i<=nPlaceCnt-1; i++) {
			//nResultIdx = nResultIdx + 1;
			//sSearchResult = sSearchResult + "Result: [" + Integer.valueOf(nResultIdx).toString() + "]" + System.getProperty("line.separator");
			//sSearchResult = sSearchResult + "Name: " + results.getResults().get(i).getName() + System.getProperty("line.separator");
			//sSearchResult = sSearchResult + "Address: " + results.getResults().get(i).getFormattedAddress() + System.getProperty("line.separator") + System.getProperty("line.separator");
			
			m_lststrPlaceNames.add(results.getResults().get(i).getName());
		}
			
		return nPlaceCnt;
	}
	
	/////////////////////////////////////////////////////////////////	
	//Google Place Nearby Search
	//Return the number of places found
	/////////////////////////////////////////////////////////////////	
	private int GooglePlace_Nearby(String strURL) {
		String sCharset = "UTF-8";
		
		GooglePlaceNearbyResults results = null;
		URL url = null;
		Reader reader = null;
		int nResultIdx = 0;
		String sSearchResult = "";
		int nPlaceCnt = 0;
		boolean bIgnore = false;
		
		nPlaceCnt = MAX_TITLE_CNT;
			
		try {
			url = new URL(strURL);
			reader = new InputStreamReader(url.openStream(), sCharset);
		} catch (Exception o) {
			return nPlaceCnt;
		}
		
		results = new Gson().fromJson(reader, GooglePlaceNearbyResults.class);
				
		int total = results.getResults().size();
	
		if (nPlaceCnt > total) nPlaceCnt = total;
		
		// Show title and URL of each results in ListView
		for(int i=0; i<=nPlaceCnt-1; i++) {
			//nResultIdx = nResultIdx + 1;
			//sSearchResult = sSearchResult + "Result: [" + Integer.valueOf(nResultIdx).toString() + "]" + System.getProperty("line.separator");
			//sSearchResult = sSearchResult + "Name: " + results.getResults().get(i).getName() + System.getProperty("line.separator");
			//sSearchResult = sSearchResult + "Vicinity: " + results.getResults().get(i).getVicinity() + System.getProperty("line.separator") + System.getProperty("line.separator");
			bIgnore = false;
			
			List<String> lststrTypes = results.getResults().get(i).getTypes();
			for (int j=0; j<lststrTypes.size(); j++) {
				if (m_lststrIgnorePlaceTypes.contains(lststrTypes.get(j))) {
					bIgnore = true;
					break;
				}
			}
			
			if (bIgnore == false) {
				m_lststrPlaceNames.add(results.getResults().get(i).getName());
			}
		}
		
		return nPlaceCnt;
	}
	
	
	//Do Google Web search with the given search phrase.
	//It checks whether the "Did you mean" exists, if yes, it extracts the suggested search phrase
	//At the same time, it extracts the titles
	public int GoogleWebSearch(String sSearchPhrase) {
		String sSuggestedSearchPhrase = "";
		int nTitleCnt = 0;
		
		m_lststrResultTitles = new ArrayList<String>();
		m_lststrPlaceNames = new ArrayList<String>();
		
		//Check "Did you mean"
		sSuggestedSearchPhrase = checkDidYouMean(sSearchPhrase);
		
		//Extract titles from Google Web search result
		doGoogleWebSearch(sSearchPhrase);
		
		if (sSuggestedSearchPhrase.length() > 0) {
			//Search with the Google Suggested phrase
			doGoogleWebSearch(sSuggestedSearchPhrase);
		}
		
		nTitleCnt = m_lststrResultTitles.size();
		
		return nTitleCnt;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//Do Google Place search with the given place keywords
	//nSearchType = 1:   Nearby search
	//nSearchType = 2:   Text search
	//sAreaInfo:   Is the rough area information, e.g. "champaign il",    "E Green St Champaign Il"
	//sLocation:   GPS coordinates, e.g. "40.11033,-88.22915"
	//Return: the number of places found
	////////////////////////////////////////////////////////////////////////////////////////////////////
	public int GooglePlaceSearch(int nSearchType, List<String> lststrPlaceKeywords, String sAreaInfo, String sLocation) {		
		int nPlaceCnt = 0;
		String sCharset = "UTF-8";
		String sPlaceSearchBase_Nearby = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
		String sPlaceSearchBase_Text = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
		String sSearchURL = "";
		String sPlaceKeywords = "";
		String sQuery = "";
		
		String sApiKey = "";
		
		for (int i=0; i<lststrPlaceKeywords.size(); i++) {
			if (i==0) {
				sPlaceKeywords = lststrPlaceKeywords.get(i);
			} else {
				sPlaceKeywords = sPlaceKeywords + " " + lststrPlaceKeywords.get(i) ;
			}
		} 
		
		try {
			//######[Begin] Added for dynamic keys
			m_nKeyIdx = m_nSearchIdx/m_nKeyStep;
			sApiKey = strarrGoogleApiKeys[m_nKeyIdx];
			m_nSearchIdx = m_nSearchIdx + 1;
			//######[End] Added for dynamic keys
			
			if (nSearchType == 1) {
				//Google Place search --- Nearby
				//Input: Sensor, Key, Types, Location (GPS coordinates), Radius,   Keyword or Name:   set with Store Name Keyword(s)		
				sSearchURL = sPlaceSearchBase_Nearby + SENSOR_STATUS + "&key=" + GOOGLE_API_KEY + "&types=" + PLACE_TYPE + "&location=" + sLocation + "&radius=" + SEARCH_RADIUS + "&keyword=" + URLEncoder.encode(sPlaceKeywords, sCharset);
//				sSearchURL = sPlaceSearchBase_Nearby + SENSOR_STATUS + "&key=" + sApiKey + "&types=" + PLACE_TYPE + "&location=" + sLocation + "&keyword=" + URLEncoder.encode(sPlaceKeywords, sCharset);

				nPlaceCnt = GooglePlace_Nearby(sSearchURL);
			} else {
				//Google Place search --- Text
				//Input: Sensor, Key, Types, Query: set with Store Name Keyword(s) + Area information, (Optional) Location + Radius		
				sQuery  = sPlaceKeywords + " " + sAreaInfo;
				sSearchURL = sPlaceSearchBase_Text + SENSOR_STATUS + "&key=" + GOOGLE_API_KEY + "&types=" + PLACE_TYPE + "&query=" + URLEncoder.encode(sQuery, sCharset);
//				sSearchURL = sPlaceSearchBase_Text + SENSOR_STATUS + "&key=" + sApiKey + "&types=" + PLACE_TYPE + "&query=" + URLEncoder.encode(sQuery, sCharset);

				nPlaceCnt = GooglePlace_Text(sSearchURL);
			}
		} catch (Exception e) {
			return 0;
		}
				
		return nPlaceCnt;
	}

	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//This function Do Google Web search on the store names in lststrPlaceNames,
	//For each store name, get URLs of the top N result
	//Get the webpage corresponding to each URL and extract the top M words
	//Setting top N=5 URL for each 
	public void AnalyzePlaceWebpage(List<String> lststrPlaceNames) {
		
	}
	
	
	//Google Web search with the given search phrase and store the returned titles
	private void doGoogleWebSearch(String sSearchPhrase, List<String> lststrTitle, List<String> lststrURL) {
		String google = "http://www.google.com/search?q=";
		
		String charset = "UTF-8";
		String userAgent = "UIUC (+http://uiuc.edu/bot)"; // institute name and bot homepage
		int nResultIdx = 0;
		int nCnt = 0;
		
		System.out.println("\n[Google Web search phrase]:  " + sSearchPhrase);
		
		try {
			Elements links = Jsoup.connect(google + URLEncoder.encode(sSearchPhrase, charset)).userAgent(userAgent).get().select("li.g>h3>a");
	
			for (Element link : links) {
			    String title = link.text();
			    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
			    
			    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
			    
			    //System.out.println("===> " + url);
			    
			    if (!url.startsWith("http")) {
			        continue; // Ads/news/etc.
			    }
	
			    nResultIdx = nResultIdx + 1;
			    if (nResultIdx >  MAX_TITLE_CNT) break;
			    
			    //Store the titles & url
			    lststrTitle.add(title);
			    lststrURL.add(url);
				nCnt = nCnt + 1;
				
				if (nCnt >= 3) {  //Only store the top 3 result
					break;
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("############################Search is wrong############################");
		}
		
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			
		}
	}

	
	//Do Google Web search with the given search phrase.
	//It checks whether the "Did you mean" exists, if yes, it extracts the suggested search phrase
	//At the same time, it extracts the titles
	public int GoogleWebSearch(String sSearchPhrase, List<String> lststrTitle, List<String> lststrURL) {
		String sSuggestedSearchPhrase = "";
		int nTitleCnt = 0;
				
		//Extract titles from Google Web search result
		doGoogleWebSearch(sSearchPhrase, lststrTitle, lststrURL);
		
		nTitleCnt = lststrTitle.size();
		
		return nTitleCnt;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//Do Google Place search with the given place keywords
	//nSearchType = 1:   Nearby search (Coordinates)
	//nSearchType = 2:   Text search (Area Info)
	//sAreaInfo:   Is the rough area information, e.g. "champaign il",    "E Green St Champaign Il"
	//sLocation:   GPS coordinates, e.g. "40.11033,-88.22915"
	//Return: the number of places found
	////////////////////////////////////////////////////////////////////////////////////////////////////
//	public int GooglePlaceSearch(int nSearchType, String sAreaInfo, String sLocation, String sPlaceFile, String sPlaceGoogleTitleURLFile) {		
//	public int GooglePlaceSearch(int nSearchType, String sAreaInfo, String sLocation, int nRadius, String sPlaceFile, String sPlaceGoogleTitleURLFile, String sTopFrequentWordFilePrefix, int nTopType, int nTopNum) {

	//This search will download the webimage. When searching, it will search every possible radius (10-meter gap)
	public int GooglePlaceSearch(int nSearchType, String sAreaInfo, String sLocation, int nRadius, String sPlaceFile, String sPlaceGoogleTitleURLFile, String sTopFrequentWordFilePrefix, int nTopType, int nTopNum, String sWebImgFolder) {
		int nPlaceCnt = 0;
		String sCharset = "UTF-8";
		String sPlaceSearchBase_Nearby = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
		String sPlaceSearchBase_Text = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
		String sSearchURL = "";
		String sQuery = "";
		String sApiKey = "";
		String sLine = "";
		FileWriter fwCandidatePlace = null;
		FileWriter fwCandidatePlaceGoogleTitleURL = null;
		int nStepRadius = 10;
		//int nTargetRadius = 10;
		int nTitleCnt;
		
		int nWebImgFolderLen = sWebImgFolder.length();
		
		//if (nTargetRadius < nRadius)  nTargetRadius = nRadius;
		
		try {
			//######[Begin] Added for dynamic keys
		//	m_nKeyIdx = m_nSearchIdx/m_nKeyStep;
		//	sApiKey = strarrGoogleApiKeys[m_nKeyIdx];
		//	m_nSearchIdx = m_nSearchIdx + 1;
			//######[End] Added for dynamic keys
		/////	for (int i=0; i<m_narrRadius.length; i++) {
		////		nStepRadius = m_narrRadius[i];
			while (nStepRadius <= nRadius) {	
				
				if (nSearchType == 1) {
					//Google Place search --- Nearby
					//Input: Sensor, Key, Types, Location (GPS coordinates), Radius,   Keyword or Name:   set with Store Name Keyword(s)		
					sSearchURL = sPlaceSearchBase_Nearby + SENSOR_STATUS + "&key=" + GOOGLE_API_KEY + "&location=" + sLocation + "&radius=" + nStepRadius;
	//				sSearchURL = sPlaceSearchBase_Nearby + SENSOR_STATUS + "&key=" + GOOGLE_API_KEY + "&types=" + PLACE_TYPE + "&location=" + sLocation + "&radius=" + SEARCH_RADIUS;
	//				sSearchURL = sPlaceSearchBase_Nearby + SENSOR_STATUS + "&key=" + sApiKey + "&types=" + PLACE_TYPE + "&location=" + sLocation + "&keyword=" + URLEncoder.encode(sPlaceKeywords, sCharset);
	
					nPlaceCnt = GooglePlace_Nearby(sSearchURL);
				} else {
					//Google Place search --- Text
					//Input: Sensor, Key, Types, Query: set with Store Name Keyword(s) + Area information, (Optional) Location + Radius		
					sQuery  = sAreaInfo;
					sSearchURL = sPlaceSearchBase_Text + SENSOR_STATUS + "&key=" + GOOGLE_API_KEY + "&types=" + PLACE_TYPE + "&query=" + URLEncoder.encode(sQuery, sCharset);
	//				sSearchURL = sPlaceSearchBase_Text + SENSOR_STATUS + "&key=" + sApiKey + "&types=" + PLACE_TYPE + "&query=" + URLEncoder.encode(sQuery, sCharset);
	
					nPlaceCnt = GooglePlace_Text(sSearchURL);
				}
				
				//Modify 2015/04/09 nStepRadius = nStepRadius + 10;   
				nStepRadius = nStepRadius + RADIUS_STEP;
			}
		} catch (Exception e) {
			return 0;
		}
		
		if (m_lststrPlaceNames.size() == 0) return 0;
		
		//Remove duplicated place
		for (String sPlaceName: m_lststrPlaceNames) {
			if (!m_lststrCandidatePlaces.contains(sPlaceName)) {
				m_lststrCandidatePlaces.add(sPlaceName);
			}
		}
		
		try {
			fwCandidatePlace = new FileWriter(sPlaceFile, false);
			
			for (int i=0; i<m_lststrCandidatePlaces.size(); i++) {
				sLine = m_lststrCandidatePlaces.get(i) + "\n";
				fwCandidatePlace.write(sLine);
			}
			
			fwCandidatePlace.close();
		
		} catch (Exception e) {
			
		}

		nPlaceCnt = m_lststrCandidatePlaces.size();
		
		//Get the top N titles & URLs by Googling the place name
		FileWriter fwWebpageTopWords = null;
		String sWebpageTopWordsFile = "";
		
//		if (m_nTopType == 1) {
//			sWebpageTopWordsFile = m_sWebpageTopWordsFile  + "_TopNum_" + m_nTop + ".txt";	
//		} else {
//			sWebpageTopWordsFile = m_sWebpageTopWordsFile  + "_TopPercent_" + m_nTop + ".txt";				
//		}

		if (nTopType == 1) {
			sWebpageTopWordsFile = sTopFrequentWordFilePrefix  + "_TopNum_" + nTopNum + ".txt";	
		} else {
			sWebpageTopWordsFile = sTopFrequentWordFilePrefix  + "_TopPercent_" + nTopNum + ".txt";				
		}

		//Get the URL for the place through Google Web Search; and then get the keywords from each webpages
		try {
			if (nPlaceCnt > 0) {
				fwWebpageTopWords = new FileWriter(sWebpageTopWordsFile, true);
				fwCandidatePlaceGoogleTitleURL = new FileWriter(sPlaceGoogleTitleURLFile, false);
				
				//Do Google Web Search,  get the top 3 title & URL for each place
				for (int i=0; i<m_lststrCandidatePlaces.size(); i++) {
					List<String> lststrTitle = new ArrayList<String>();
					List<String> lststrURL = new ArrayList<String>();
					nTitleCnt = GoogleWebSearch(m_lststrCandidatePlaces.get(i), lststrTitle, lststrURL);
					
					if (nTitleCnt > 0) {
						sLine = "Place:	" + m_lststrCandidatePlaces.get(i) + "\n";
						fwCandidatePlaceGoogleTitleURL.write(sLine);
						
						for (int j=0; j<lststrTitle.size(); j++) {
							sLine = "Title:	" + lststrTitle.get(j) + "\n";
							fwCandidatePlaceGoogleTitleURL.write(sLine);
							sLine = "URL: " + lststrURL.get(j) + "\n";
							fwCandidatePlaceGoogleTitleURL.write(sLine);
						}
						
						fwCandidatePlaceGoogleTitleURL.write("\n");
						
						//Extract the representative keywords from the first URL corresponding to each place
						m_RepresentativeKeywordExtraction.ExtractRepresentativeKeyword(m_lststrCandidatePlaces.get(i), lststrURL.get(0), m_nTopType, m_nTop, fwWebpageTopWords);
						
						//Download the image on the webpage corresponding to the first URL
						if (nWebImgFolderLen > 0) {
							m_WebImageExtraction.extractWebImage_JSOUPA(m_lststrCandidatePlaces.get(i), lststrURL.get(0), sWebImgFolder);
						}
					
					}
				}
				
				fwCandidatePlaceGoogleTitleURL.close();
				fwWebpageTopWords.close();
			}
		} catch (Exception e) {
			
		}
		
		
		return nPlaceCnt;
	}
	
	
	
	public int GooglePlaceSearchByGps(String sGpsLocation, int nRadius, String sSaveFolder, int nTopNum) {
		int nPlaceCnt = 0;
		String sCharset = "UTF-8";
		String sPlaceSearchBase_Nearby = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
		String sPlaceSearchBase_Text = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
		String sSearchURL = "";
		String sQuery = "";
		String sApiKey = "";
		String sLine = "";
		FileWriter fwCandidatePlace = null;
		FileWriter fwCandidatePlaceGoogleTitleURL = null;
		int nStepRadius = 10;
		//int nTargetRadius = 10;
		int nTitleCnt;
		int i;
		
		
	//	int nWebImgFolderLen = sWebImgFolder.length();
		
		//if (nTargetRadius < nRadius)  nTargetRadius = nRadius;
		
		try {
			//######[Begin] Added for dynamic keys
		//	m_nKeyIdx = m_nSearchIdx/m_nKeyStep;
		//	sApiKey = strarrGoogleApiKeys[m_nKeyIdx];
		//	m_nSearchIdx = m_nSearchIdx + 1;
			//######[End] Added for dynamic keys
		/////	for (int i=0; i<m_narrRadius.length; i++) {
		////		nStepRadius = m_narrRadius[i];
			while (nStepRadius <= nRadius) {	
				
				//Google Place search --- Nearby
				//Input: Sensor, Key, Types, Location (GPS coordinates), Radius,   Keyword or Name:   set with Store Name Keyword(s)		
				sSearchURL = sPlaceSearchBase_Nearby + SENSOR_STATUS + "&key=" + GOOGLE_API_KEY + "&location=" + sGpsLocation + "&radius=" + nStepRadius;
//				sSearchURL = sPlaceSearchBase_Nearby + SENSOR_STATUS + "&key=" + GOOGLE_API_KEY + "&types=" + PLACE_TYPE + "&location=" + sLocation + "&radius=" + SEARCH_RADIUS;
//				sSearchURL = sPlaceSearchBase_Nearby + SENSOR_STATUS + "&key=" + sApiKey + "&types=" + PLACE_TYPE + "&location=" + sLocation + "&keyword=" + URLEncoder.encode(sPlaceKeywords, sCharset);

				nPlaceCnt = GooglePlace_Nearby(sSearchURL);
				
				//Modify 2015/0409 nStepRadius = nStepRadius + 10;
				nStepRadius = nStepRadius + RADIUS_STEP;
				
			}
		} catch (Exception e) {
			return 0;
		}
		
		if (m_lststrPlaceNames.size() == 0) return 0;
		
		//Remove duplicated place
		for (String sPlaceName: m_lststrPlaceNames) {
			if (!m_lststrCandidatePlaces.contains(sPlaceName)) {
				m_lststrCandidatePlaces.add(sPlaceName);
			}
		}
		
//		try {
//			fwCandidatePlace = new FileWriter(sPlaceFile, false);
//			
//			for (int i=0; i<m_lststrCandidatePlaces.size(); i++) {
//				sLine = m_lststrCandidatePlaces.get(i) + "\n";
//				fwCandidatePlace.write(sLine);
//			}
//			
//			fwCandidatePlace.close();
//		
//		} catch (Exception e) {
//			
//		}

		nPlaceCnt = m_lststrCandidatePlaces.size();
		
		//Create sub folder for each store for storing web images
//		for (String sPlaceName: m_lststrCandidatePlaces) {
//			Utility.createWebImagePlaceSubFolder(sSaveFolder, sPlaceName);
//		}
		
		
		//Get the top N titles & URLs by Googling the place name
		FileWriter fwWebpageTopWords = null;
		String sWebpageTopWordsFile = "";
		
		//Get the URL for the place through Google Web Search; and then get the keywords from each webpages
//		try {
			if (nPlaceCnt > 0) {
//				fwWebpageTopWords = new FileWriter(sWebpageTopWordsFile, true);
//				fwCandidatePlaceGoogleTitleURL = new FileWriter(sPlaceGoogleTitleURLFile, false);
				
				//Do Google Web Search,  get the top 3 title & URL for each place
				for (i=0; i<m_lststrCandidatePlaces.size(); i++) {
					String sPlaceName = m_lststrCandidatePlaces.get(i);
					List<String> lststrTitle = new ArrayList<String>();
					List<String> lststrURL = new ArrayList<String>();
					nTitleCnt = GoogleWebSearch(sPlaceName, lststrTitle, lststrURL);
					
					//Create the folder which stores the 
					Utility.createWebImagePlaceSubFolder(sSaveFolder, sPlaceName);
					
					if (nTitleCnt > 0) {
//						sLine = "Place:	" + m_lststrCandidatePlaces.get(i) + "\n";
//						fwCandidatePlaceGoogleTitleURL.write(sLine);
//						
//						for (int j=0; j<lststrTitle.size(); j++) {
//							sLine = "Title:	" + lststrTitle.get(j) + "\n";
//							fwCandidatePlaceGoogleTitleURL.write(sLine);
//							sLine = "URL: " + lststrURL.get(j) + "\n";
//							fwCandidatePlaceGoogleTitleURL.write(sLine);
//						}
//						
//						fwCandidatePlaceGoogleTitleURL.write("\n");
						
						//Extract the representative keywords from the first URL corresponding to each place
						//if (Utility.USE_WEB_IMAGE_TEXT == false) {
						if (Utility.USE_FULL_TEXT == false) {
							m_RepresentativeKeywordExtraction.ExtractRepresentativeKeyword_NonWebImage(sPlaceName, lststrURL.get(0), nTopNum, sSaveFolder, sGpsLocation);
						} else {
							m_RepresentativeKeywordExtraction.ExtractRepresentativeKeyword_WithWebImage(sPlaceName, lststrURL.get(0), sSaveFolder, sGpsLocation);
						}
						
						String sWebImgFolder = Utility.getWebImagePlaceSubFolder(sSaveFolder, sPlaceName);
						
						//Download the image on the webpage corresponding to the first URL
					//	if (nWebImgFolderLen > 0) {
						m_WebImageExtraction.extractWebImage_JSOUPA(sPlaceName, lststrURL.get(0), sWebImgFolder);
					//	}
					
					}
				}
				
//				fwCandidatePlaceGoogleTitleURL.close();
//				fwWebpageTopWords.close();
			}
//		} catch (Exception e) {
//			
//		}
		
		
		return nPlaceCnt;
	}
	

	
	
	public void GooglePlaceSearchByPlace(String sGpsLocation, String sPlaceName, String sURL, String sSaveFolder, int nTopNum) {
										
		//Create the folder which stores the 
		Utility.createWebImagePlaceSubFolder(sSaveFolder, sPlaceName);
		
		//Extract the representative keywords from the first URL corresponding to each place
		//m_RepresentativeKeywordExtraction.ExtractRepresentativeKeyword(sPlaceName, sURL, nTopNum, sSaveFolder, sGpsLocation);
		
		//if (Utility.USE_WEB_IMAGE_TEXT == false) {
		if (Utility.USE_FULL_TEXT == false)	{
			m_RepresentativeKeywordExtraction.ExtractRepresentativeKeyword_NonWebImage(sPlaceName, sURL, nTopNum, sSaveFolder, sGpsLocation);
		} else {
			m_RepresentativeKeywordExtraction.ExtractRepresentativeKeyword_WithWebImage(sPlaceName, sURL, sSaveFolder, sGpsLocation);
		}
		
		String sWebImgFolder = Utility.getWebImagePlaceSubFolder(sSaveFolder, sPlaceName);
		
		//Download the image on the webpage corresponding to the URL
		m_WebImageExtraction.extractWebImage_JSOUPA(sPlaceName, sURL, sWebImgFolder);
			
	}
	
	
	public void GooglePlaceSearchByPlaceListFile(String sGpsLocation, String sPlaceListFile, String sSaveFolder, int nTopNum) {
		Map<String, String> mapPlaceNameUrl = new HashMap<String, String>();
		
		try {
			List<String> lstLines = Files.readAllLines(Paths.get(sPlaceListFile), StandardCharsets.UTF_8);
			for (String sLine : lstLines) {
				if (sLine.trim().length() == 0) continue;
				if (sLine.startsWith("%"))  continue;
				
				String[] sFields = Utility.getFields(sLine);
				mapPlaceNameUrl.put(sFields[0].trim(), sFields[1].trim());
				
			}
		} catch (Exception e) {
			
		}
		
		for (String sPlaceName : mapPlaceNameUrl.keySet()) {
			String sURL = mapPlaceNameUrl.get(sPlaceName);
			
			GooglePlaceSearchByPlace(sGpsLocation, sPlaceName, sURL, sSaveFolder, nTopNum);
		}
		
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////
	///////////Classes for processing results from Google Web and Place search////////////////
	//////////////////////////////////////////////////////////////////////////////////////////	
	class GoogleResults{
	    private ResponseData responseData;
	    public ResponseData getResponseData() { return responseData; }
	    public void setResponseData(ResponseData responseData) { this.responseData = responseData; }
	    public String toString() { return "ResponseData[" + responseData + "]"; }
	 
	    class ResponseData {
	        private List<Result> results;
	        public List<Result> getResults() { return results; }
	        public void setResults(List<Result> results) { this.results = results; }
	        public String toString() { return "Results[" + results + "]"; }
	    }
	 
	    class Result {
	        private String url;
	        private String title;
	        public String getUrl() { return url; }
	        public String getTitle() { return title; }
	        public void setUrl(String url) { this.url = url; }
	        public void setTitle(String title) { this.title = title; }
	        public String toString() { return "Result[url:" + url +",title:" + title + "]"; }
	    }
	}
     
	
	class GooglePlaceNearbyResults {
		private List<HtmlAttr> htmlAttrs;
		private String nextpagetoken;
		private List<PlaceNearbyResult> results;
		private String status;
		
		public String getNextPageToken() {return nextpagetoken; }
		public void setNextPageToken(String nextpagetoken) {this.nextpagetoken = nextpagetoken; }
		
		public List<HtmlAttr> getAttrs() {return htmlAttrs; }
		public void setAttrs(List<HtmlAttr> htmlAttrs) {this.htmlAttrs = htmlAttrs; }
        
		public List<PlaceNearbyResult> getResults() { return results; }
        public void setResults(List<PlaceNearbyResult> results) { this.results = results; }
        
        public String getStatus() {return status; }
        public void setStatus(String status) {this.status = status; }
        
        public String toString() { return "Results[" + results + "]"; }
        
        class HtmlAttr {
        	private String attr;
        	public String getAttr() {return attr; }
        	public void setAttr(String attr) {this.attr = attr; }
        }
        
	    class PlaceNearbyResult {
	        private String name;
	        private String vicinity;
	        private List<String> types;
	        public String getName() { return name; }
	        public String getVicinity() {return vicinity; }
	        public List<String> getTypes() {return types; }
	        public void setName(String name) { this.name = name; }
	        public void setVicinity(String vicinity) {this.vicinity = vicinity; }
	        public void setTypes(List<String> types) {this.types = types; }
	        public String toString() { return "Result[Name:" + name + ", Vicinity:" + vicinity + "]"; }
	    }
	    
	}
	

	class GooglePlaceTextResults {
		private List<HtmlAttr> htmlAttrs;
		private String nextpagetoken;
		private List<PlaceTextResult> results;
		private String status;
		
		public String getNextPageToken() {return nextpagetoken; }
		public void setNextPageToken(String nextpagetoken) {this.nextpagetoken = nextpagetoken; }
		
		public List<HtmlAttr> getAttrs() {return htmlAttrs; }
		public void setAttrs(List<HtmlAttr> htmlAttrs) {this.htmlAttrs = htmlAttrs; }
        
		public List<PlaceTextResult> getResults() { return results; }
        public void setResults(List<PlaceTextResult> results) { this.results = results; }
        
        public String getStatus() {return status; }
        public void setStatus(String status) {this.status = status; }
        
        public String toString() { return "Results[" + results + "]"; }
        
        class HtmlAttr {
        	private String attr;
        	public String getAttr() {return attr; }
        	public void setAttr(String attr) {this.attr = attr; }
        }
        
	    class PlaceTextResult {
	        private String name;
	        private String formatted_address;
	        public String getName() { return name; }
	        public String getFormattedAddress() {return formatted_address; }
	        public void setName(String name) { this.name = name; }
	        public void setFormattedAddressy(String formatted_address) {this.formatted_address = formatted_address; }
	        public String toString() { return "Result[Name:" + name + ", Address:" + formatted_address + "]"; }
	    }
	}
	
	
}

