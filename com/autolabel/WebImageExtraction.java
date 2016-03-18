package com.autolabel;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

//This class is used to extract all the images from the webpages
public class WebImageExtraction {

	private static int m_nImgIndex = 0;
	static String[] m_sarrImgExt = {
		".jpg", ".jpeg", ".bmp", ".png", ".gif", ".tif", ".tiff", ".ico"
	};


	public void extractWebImage_JSOUPA(String sPlaceName, String sURL, String sSaveFolder) {
		int nImageCount = 0;
		String userAgent = "UIUC (+http://uiuc.edu/bot)"; // institute name and bot homepage
		boolean bTriedSecond = false;
		m_nImgIndex = 0;
		
		
		try {
	        Document doc = Jsoup.connect(sURL).userAgent(userAgent).timeout(5*1000).get();
	        Elements imgURLs = doc.select("img[src]");
	        
	        System.out.println("Image Count: " + imgURLs.size());

	        for (Element imgElement : imgURLs) {
            	String sImageSrc = imgElement.attr("abs:src");
            	
				String sTrueImageSrc = getTrueImageSrc(sImageSrc);
				if (sTrueImageSrc.length() == 0) continue;
				
				nImageCount = nImageCount + 1;
				
				saveImage(sTrueImageSrc, sSaveFolder, sPlaceName);
	        }
	        
	        if (nImageCount == 0) {
	        	extractWebImage(sPlaceName, sURL, sSaveFolder);
	        	bTriedSecond = true;
	        }

		} catch (Exception e) {
			if (bTriedSecond == false) {
				extractWebImage(sPlaceName, sURL, sSaveFolder);
			}
			
			System.out.println("Not get: " + sURL);
			System.out.println("Error: " + e.toString());			
		}
		
	}
	
		
	public void extractWebImage_JSOUP (String sPlaceName, String sURL, String sSaveFolder) {
		String sTrueImageSrc = "";
		//String sURL = "http://www.walgreens.com/";
		
		m_nImgIndex = 0;
		
		try {
			Document doc = Jsoup.connect(sURL).get();
			Elements img = doc.getElementsByTag("img");
			if (img.size() == 0) {
				System.out.println("Can't find image!!!!!!!!!!!!!!!!!");
				return;
			} else {
//				System.out.println("Img # " + img.size());
			}
			
			for (Element el : img) {
				String sImageSrc = el.absUrl("src");

				sTrueImageSrc = getTrueImageSrc(sImageSrc);
				if (sTrueImageSrc.length() == 0) continue;
				
				saveImage(sTrueImageSrc, sSaveFolder, sPlaceName);
			}
			
		} catch (IOException e) {
			System.out.println("Error here!!!!!");
		}
	}
	
	//Extract the images on the webpage corresponding to the given URL and save in the place specified b
	public void extractWebImage(String sPlaceName, String sURL, String sSaveFolder) {
		//String sURL = "http://www.cvs.com/";
		HtmlPage page = null;
		
		m_nImgIndex = 0;
		
		//LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 
		//java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		
		WebClient webClient = new WebClient();

		try {
			page = webClient.getPage(sURL);
		} catch (Exception e) {
			System.out.println("Going out.........................!!! " + e.toString());
			
			return;
		}
		
		DomNodeList<DomElement> elements = page.getElementsByTagName("img");
		String sImageSrc = "";
		String sTrueImageSrc = ""; 		 
				 
		System.out.println("[Total Image Num] " + elements.size());
		 
		for (DomElement domElement : elements) {
			HtmlImage htmlImg = (HtmlImage) domElement;
			sImageSrc = htmlImg.getSrcAttribute();
			sTrueImageSrc = getTrueImageSrc(sImageSrc);
			if (sTrueImageSrc.length() == 0) continue;
			 			 
			try {
				System.out.println("[Img SRC] " + sTrueImageSrc);
				 
				saveImage(sTrueImageSrc, sSaveFolder, sPlaceName);
			} catch (Exception e) {
				System.out.println(">>>>>>>>"); 
			}
		}
		 		 
		webClient.closeAllWindows();		
		return;
	}
	
	
	private void saveImage(String srcImg, String sSaveFolder, String sPlaceName) {
		String sImgFilePath = "";
		int nPos = -1;
		String sExt = "";
		URL imgUrl = null;
		InputStream in = null;
		FileOutputStream out = null;
		boolean bRead = false;
		
		nPos = srcImg.lastIndexOf(".");
		if (nPos != -1) {
			sExt = srcImg.substring(nPos);
		}
		
		try {
			imgUrl = new URL(srcImg);
			m_nImgIndex = m_nImgIndex + 1;

			URLConnection urlConn = imgUrl.openConnection();
			urlConn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.5 Chrome/19.0.1084.52 Safari/536.5");
			urlConn.setConnectTimeout(5*1000);
			
			in = urlConn.getInputStream();

			if (sSaveFolder.charAt(sSaveFolder.length() - 1) != '\\') {
				sImgFilePath = sSaveFolder + "\\" + sPlaceName + "_Img_" + m_nImgIndex + sExt;
			} else {
				sImgFilePath = sSaveFolder + sPlaceName + "_Img_" + m_nImgIndex + sExt;
			}

			out = new FileOutputStream(sImgFilePath); 
			
			int b = 0;

			for (; (b = in.read()) != -1; ) {
				out.write(b);
				bRead = true;
			}
						
			out.close();
			in.close();
		} catch (Exception e) {
			System.out.println("Error in saveImage: " + e.toString());
		}
		
		
	}
	
	//This function extract the actual image URL in the "src" value of <img
	//Some <img src value looks like: 
	//"http://pisces.bbystatic.com/image2/BestBuy_US/en_US/images/abn/2014/global/ghp/140810/GHP-Focusc3.jpg;canvasHeight=170;canvasWidth=306"
	private String getTrueImageSrc(String sImgSrc) {
		String sTrueImgSrc = "";
		int nIndex = -1;
		boolean bFind = false;
		int nStartIndex = 0;
		
		if (sImgSrc.length() == 0) return "";
		
		for (int i=0; i<m_sarrImgExt.length; i++) {
			nIndex = sImgSrc.toLowerCase().indexOf(m_sarrImgExt[i]);
			if (nIndex != -1) {
				sTrueImgSrc = sImgSrc.substring(0, nIndex+m_sarrImgExt[i].length());
				bFind = true;
				break;
			}
		}
		
		if (bFind == false) {
			sTrueImgSrc = sImgSrc;
		}
		
		if (sTrueImgSrc.toLowerCase().startsWith("http") == false) {
			for (int j=0; j<sTrueImgSrc.length(); j++) {
				if (sTrueImgSrc.charAt(j) != '/') {
					nStartIndex = j;
					break;
				}
			}
			
			sTrueImgSrc = "http://" + sTrueImgSrc.substring(nStartIndex);			
		}
		
		return sTrueImgSrc;
	}
	
	public WebImageExtraction() {
		// TODO Auto-generated constructor stub
	}

}
