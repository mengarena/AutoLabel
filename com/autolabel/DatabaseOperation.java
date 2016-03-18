package com.autolabel;

//This class is used to upload data into Server database through server php script

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;  
import org.apache.http.HttpResponse;  
import org.apache.http.NameValuePair;  
import org.apache.http.ParseException;  
import org.apache.http.client.ClientProtocolException;  
import org.apache.http.client.entity.UrlEncodedFormEntity;  
import org.apache.http.client.methods.HttpGet;  
import org.apache.http.client.methods.HttpPost;  
import org.apache.http.client.methods.HttpUriRequest;  
import org.apache.http.impl.client.DefaultHttpClient;  
import org.apache.http.message.BasicNameValuePair;  
import org.apache.http.protocol.HTTP;  
import org.apache.http.util.EntityUtils;
import org.apache.*;
import org.apache.http.*;
import org.apache.http.client.*;
  

public class DatabaseOperation {
//	private final String m_sURL = "http://130.126.136.127/datastore/golive/autolabel_upload.php";
	private final String m_sURL = "http://130.126.136.95/datastore/golive/autolabel_upload.php";

	private HttpClient m_httpClt = null;
	private HttpPost m_httpPost = null;
				
	public DatabaseOperation() {
		m_httpClt = new DefaultHttpClient();
		m_httpPost = new HttpPost(m_sURL);
	}

/*	
	public void uploadTest() {
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("placename", "bestbuy"));
			nameValuePairs.add(new BasicNameValuePair("score",  "34.543"));
			m_httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			m_httpClt.execute(m_httpPost);
			
			//HttpResponse response = m_httpClt.execute(m_httpPost);
			//HttpEntity resEntity = response.getEntity();
			//if(resEntity != null) {
			//	String responseMsg = EntityUtils.toString(resEntity).trim();
			//	System.out.println("......Response: " + responseMsg); 
			//show}
		} catch (Exception e) {
			System.out.println("-------A----1--------" + e.toString());
		}					
	}
*/	
	
	public void uploadPlaceAPInfo(String[] sarrPlaceAPInfoFiles) {
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
    			
    	String sFieldName_MAC = "";
    	String sFieldName_LbRSS = "";
    	String sFieldName_UbRSS = "";
    	String sFieldName_MeanRSS = "";
    	String sFieldName_Order = "";
    	
		for (i=0; i<sarrPlaceAPInfoFiles.length; i++) {
			
			try {
				fr = new FileReader(sarrPlaceAPInfoFiles[i]);
				br = new BufferedReader(fr);
				
				while( (sLine = br.readLine()) != null) {
					fields = new String[nFieldCnt];
					
					for (int j=0; j<nFieldCnt; j++) {
						int k = (int)((j-2)/5);   //new
						int nRemain = (j-2) % 5;  //new
						if ((k >= 1) && (nRemain == 0)) {  //new
							fields[j]= "" + k;
						} else {
							fields[j]="0";   //fields[j]=""; Original
						}
					}
					
					fieldsData = sLine.split(",");
					for (int k=0; k<fieldsData.length; k++) {
						fields[k] = fieldsData[k];
					}
										
					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("placename", fields[0]));
						nameValuePairs.add(new BasicNameValuePair("gpslat",  fields[1]));
						nameValuePairs.add(new BasicNameValuePair("gpslong",  fields[2]));
						
						for (int j=1; j<=nAPCount; j++) {
							sFieldName_MAC = "mac" + j;
							sFieldName_LbRSS = "lbrss" + j;
							sFieldName_UbRSS = "ubrss" + j;
							sFieldName_MeanRSS = "meanrss" + j;
							sFieldName_Order = "order" + j;
							
							nameValuePairs.add(new BasicNameValuePair(sFieldName_MAC, fields[(j-1)*5+3]));
							nameValuePairs.add(new BasicNameValuePair(sFieldName_LbRSS, fields[(j-1)*5+4]));
							nameValuePairs.add(new BasicNameValuePair(sFieldName_UbRSS, fields[(j-1)*5+5]));
							nameValuePairs.add(new BasicNameValuePair(sFieldName_MeanRSS, fields[(j-1)*5+6]));
							nameValuePairs.add(new BasicNameValuePair(sFieldName_Order, fields[(j-1)*5+7]));
						}
						
						m_httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = m_httpClt.execute(m_httpPost);
						if (response != null) {
							EntityUtils.consume(response.getEntity());
							System.out.println(response.toString());
							//System.out.println("-------A-BB---1--------");
						}
						
						//HttpResponse response = m_httpClt.execute(m_httpPost);
						//if (response != null) {
						//	EntityUtils.consume(response.getEntity());
						//}
						
						//if (response.getStatusLine().getStatusCode() == 200) {
						//	String responseMsg = EntityUtils.toString(response.getEntity());
						//	System.out.println("---Response: " + responseMsg);
						//}
						
						//HttpEntity resEntity = response.getEntity();
						//if (resEntity != null) {
						//	resEntity.consumeContent();
						//}
						//if(resEntity != null) {
						//	String responseMsg = EntityUtils.toString(resEntity).trim();
						//	System.out.println("......Response: " + responseMsg); 
						//}

						//try {
						//	Thread.sleep(4000);
						//} catch (Exception e) {
							
						
					} catch (Exception e) {
						System.out.println("-------A----1--------" + e.toString());
					}					
					
				} // while
				
				fr.close();
			} catch (Exception e) {
				System.out.println("-------A-------2-----" + e.toString());			
			}
			
		}  //for
		
		return;
		
	}



	
	public void uploadPlaceAPInfo(List<String> lststrPlaceAPInfoFiles) {
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
    	
    	String sFieldName_MAC = "";
    	String sFieldName_LbRSS = "";
    	String sFieldName_UbRSS = "";
    	String sFieldName_MeanRSS = "";
    	String sFieldName_Order = "";
    	
		for (i=0; i<lststrPlaceAPInfoFiles.size(); i++) {
			
			try {
				fr = new FileReader(lststrPlaceAPInfoFiles.get(i));
				br = new BufferedReader(fr);
				
				while( (sLine = br.readLine()) != null) {
					fields = new String[nFieldCnt];
					
					for (int j=0; j<nFieldCnt; j++) {
						int k = (int)((j-2)/5);   //new
						int nRemain = (j-2) % 5;  //new
						if ((k >= 1) && (nRemain == 0)) {  //new
							fields[j]= "" + k;
						} else {
							fields[j]="0";   //fields[j]=""; Original
						}
					}
					
					fieldsData = sLine.split(",");
					for (int k=0; k<fieldsData.length; k++) {
						fields[k] = fieldsData[k];
					}
										
					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("placename", fields[0]));
						nameValuePairs.add(new BasicNameValuePair("gpslat",  fields[1]));
						nameValuePairs.add(new BasicNameValuePair("gpslong",  fields[2]));
						
						for (int j=1; j<=nAPCount; j++) {
							sFieldName_MAC = "mac" + j;
							sFieldName_LbRSS = "lbrss" + j;
							sFieldName_UbRSS = "ubrss" + j;
							sFieldName_MeanRSS = "meanrss" + j;
							sFieldName_Order = "order" + j;
							
							nameValuePairs.add(new BasicNameValuePair(sFieldName_MAC, fields[(j-1)*5+3]));
							nameValuePairs.add(new BasicNameValuePair(sFieldName_LbRSS, fields[(j-1)*5+4]));
							nameValuePairs.add(new BasicNameValuePair(sFieldName_UbRSS, fields[(j-1)*5+5]));
							nameValuePairs.add(new BasicNameValuePair(sFieldName_MeanRSS, fields[(j-1)*5+6]));
							nameValuePairs.add(new BasicNameValuePair(sFieldName_Order, fields[(j-1)*5+7]));
						}
						
						m_httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = m_httpClt.execute(m_httpPost);
						if (response != null) {
							EntityUtils.consume(response.getEntity());
							System.out.println(response.toString());
							//System.out.println("-------A-AA---1--------");
						}
												
					} catch (Exception e) {
						System.out.println("-------A----1--------" + e.toString());
					}					
					
				} // while
				
				fr.close();
			} catch (Exception e) {
				System.out.println("-------A-------2-----" + e.toString());			
			}
			
		}  //for
		
		return;
		
	}
	
	
	
	public void uploadPlaceAPInfo(String sPlaceAPInfoFile) {
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
    	
    	String sFieldName_MAC = "";
    	String sFieldName_LbRSS = "";
    	String sFieldName_UbRSS = "";
    	String sFieldName_MeanRSS = "";
    	String sFieldName_Order = "";
    	
			
		try {
			fr = new FileReader(sPlaceAPInfoFile);
			br = new BufferedReader(fr);
			
			while( (sLine = br.readLine()) != null) {
				fields = new String[nFieldCnt];
				
				for (int j=0; j<nFieldCnt; j++) {
					int k = (int)((j-2)/5);   //new
					int nRemain = (j-2) % 5;  //new
					if ((k >= 1) && (nRemain == 0)) {  //new
						fields[j]= "" + k;
					} else {
						fields[j]="0";   //fields[j]=""; Original
					}
				}
				
				fieldsData = sLine.split(",");
				for (int k=0; k<fieldsData.length; k++) {
					fields[k] = fieldsData[k];
				}
									
				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("placename", fields[0]));
					nameValuePairs.add(new BasicNameValuePair("gpslat",  fields[1]));
					nameValuePairs.add(new BasicNameValuePair("gpslong",  fields[2]));
					
					for (int j=1; j<=nAPCount; j++) {
						sFieldName_MAC = "mac" + j;
						sFieldName_LbRSS = "lbrss" + j;
						sFieldName_UbRSS = "ubrss" + j;
						sFieldName_MeanRSS = "meanrss" + j;
						sFieldName_Order = "order" + j;
						
						nameValuePairs.add(new BasicNameValuePair(sFieldName_MAC, fields[(j-1)*5+3]));
						nameValuePairs.add(new BasicNameValuePair(sFieldName_LbRSS, fields[(j-1)*5+4]));
						nameValuePairs.add(new BasicNameValuePair(sFieldName_UbRSS, fields[(j-1)*5+5]));
						nameValuePairs.add(new BasicNameValuePair(sFieldName_MeanRSS, fields[(j-1)*5+6]));
						nameValuePairs.add(new BasicNameValuePair(sFieldName_Order, fields[(j-1)*5+7]));
					}
					
					m_httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = m_httpClt.execute(m_httpPost);
					if (response != null) {
						EntityUtils.consume(response.getEntity());
						System.out.println(response.toString());
						//System.out.println("-------A-CC---1--------");
					}
												
					
				} catch (Exception e) {
					System.out.println("-------A----1--------" + e.toString());
				}					
				
			} // while
			
			fr.close();
		} catch (Exception e) {
			System.out.println("-------A-------2-----" + e.toString());			
		}
			
		
		return;
		
	}
	
}
