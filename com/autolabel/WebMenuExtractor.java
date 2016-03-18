package com.autolabel;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class WebMenuExtractor {
	
	// select ul or ol
	public static List<String> extract_AllMenuItem(String url)
	{

		List<String> words = null;
		//System.out.println(String.format("Fetching %s...", url));
		try {
			Document doc = Jsoup.connect(url).get();
			//Elements lists = doc.select("li > a");
			//Elements lists = doc.select("li");
			Elements lists = doc.select("ul,ol");
			words = new ArrayList<String>();
			for (Element list : lists)
			{
				String content = list.text().trim();
				if (!content.equals(""))
				{
					words.add(content);
				}
			}
		} catch (Exception e) {
			
		}
		
		return words;
	}
	
	// remove footer, select ul or ol
	public static List<String> extract_RemoveFooter(String url)
	{

		List<String> words = null;
		
		try {
			//System.out.println(String.format("Fetching %s...", url));
			Document doc = Jsoup.connect(url).get();
						
			Elements footers = doc.select("[class*=footer],[id*=footer]");
			footers.remove();
						
			//Elements lists = doc.select("li > a");
			//Elements lists = doc.select("li");
			Elements lists = doc.select("ul,ol");
			words = new ArrayList<String>();
			for (Element list : lists)
			{
				String content = list.text().trim();
				if (!content.equals(""))
				{
					words.add(content);
				}
			}
		} catch (Exception e) {
			
		}
	
		return words;
	}
	
	
	
	
	// remove footer, select ul or ol in nav
	
	public static List<String> extract_NavMenuItem_RemoveFooter(String url) 
	{
		
		List<String> words = null;
		
		try {
			Document doc = Jsoup.connect(url).get();
			
	//		// todo
	//		absoluteLinks(doc, url);
			
			Elements footers = doc.select("[class*=footer],[id*=footer]");
			footers.remove();
			
			words = new ArrayList<String>();
			// int i = 0;
			while (true) // find "*nav*" class/id elements, extract text and remove it from document
			{
				Element nav_element = doc.select("[class*=nav],[id*=nav],[class*=menu],[id*=menu],[class*=tab],[id*=tab]").first();
				if (nav_element == null)
					break;
				
	//			// todo
	//			PrintWriter out = new PrintWriter(storeName + i++ + ".html");
	//			out.println(nav_element.toString());
	//			out.close();
				
				Elements lists = nav_element.select("ul,ol");
				for (Element list : lists)
				{
					String content = list.text().trim();
					if (!content.equals(""))
					{
						words.add(content);
					}
				}			

			}
		} catch (Exception e) {
			
		}
		
		return words;
	}
	
	
	
	public static List<String> extract_NavMenuItem_RemoveFooter_Enhanced(String url) 
	{
		List<String> words = null;
		try {
	//		if (!storeName.equals("Barnes & Noble"))
	//			return new ArrayList<String>();
	//		
			String menu_string = "[class*=nav],[id*=nav],[class*=menu],[id*=menu],[class*=tab],[id*=tab]," + "[class*=Nav],[id*=Nav],[class*=Menu],[id*=Menu],[class*=Tab],[id*=Tab]," + "[class*=NAV],[id*=NAV],[class*=MENU],[id*=MENU],[class*=TAB],[id*=TAB]";
			String footer_string = "[class*=foot],[id*=foot]," + "[class*=Foot],[id*=Foot]," + "[class*=FOOT],[id*=FOOT]";
			
			
			//Document doc = Jsoup.connect(url).get();
			String userAgent = "UIUC (+http://uiuc.edu/bot)"; // institute name and bot homepage
			//Document doc = Jsoup.connect(url).userAgent(userAgent).get();
			Document doc = Jsoup.connect(url).followRedirects(true).userAgent(userAgent).get();
			
			// Remove footer area
			Elements footers = doc.select(footer_string);
			footers.remove();
			
			words = new ArrayList<String>();
			// find menu-like elements (with specific words in class/id names), extract text and remove those elements from document
			while (true) 
			{
				//System.out.println("The size of nav elements: " + doc.select(menu_string).size());			
				Element menu_element = doc.select(menu_string).first();
				if (menu_element == null) // No more menus left
					break;	
				
				// Check if this element is a ul/ol
				String nodeName = menu_element.nodeName();			
				if (nodeName.toLowerCase().equals("ul") || nodeName.toLowerCase().equals("ol") || nodeName.toLowerCase().equals("li"))
				{
					String content = menu_element.text().trim();
					//System.out.println(content);
					if (!content.equals(""))
					{
						words.add(content);
					}
					menu_element.remove();
				}
								
				else
				{
					// This element is not a ul/ol, we need to extract each ul/ol in it
					while (true)
					{
						//System.out.println("The size of sub list elements: " + nav_element.select("ul,ol").size());
						Element list_element = menu_element.select("ul,ol,UL,OL,li,LI").first();
						
						if (list_element == null)
							break;
	
						String content = list_element.text().trim();
						//System.out.println(content);
						if (!content.equals(""))
						{
							words.add(content);
						}
						list_element.remove();
					}
					
					menu_element.remove();		
					
				}
				
			}
		} catch (Exception e) {
			
		}
		
		return words;
	}
	
	

	public static List<String> extract_tmp(String storeName, String url) throws IOException
	{
		url = "http://home.ustc.edu.cn/~ss1234/downloaded/index2.html";
		System.out.println(String.format("Fetching %s...", url));
		Document doc = Jsoup.connect(url).get();
		System.out.println(doc.toString());
		
		
		while (true) // find "*nav*" class/id elements, extract text and remove it from document
		{
			Element nav_element = doc.select("[class*=nav],[id*=nav]").first();
			if (nav_element == null)
				break;
			System.out.println(nav_element.text());
			nav_element.remove();
		}
		
		/*
		Elements divs = doc.select("[class*=nav],[id*=nav]");
		System.out.println(divs.size());
		System.out.println(divs.text());
		System.out.println("begin while");
		while (divs.size() >= 0)
		{
			Element div = divs.first();
			System.out.println(div.text());
			
			divs.remove(0);
			div.remove();
		}
		*/
		System.exit(-1);
		
		
		Elements footers = doc.select("[class*=footer],[id*=footer]");
		footers.remove();
		
		PrintWriter out = new PrintWriter(storeName + ".html");
		out.println(doc.toString());
		
		//Elements lists = doc.select("li > a");
		//Elements lists = doc.select("li");
		Elements lists = doc.select("ul,ol");
		List<String> words = new ArrayList<String>();
		for (Element list : lists)
		{
			String content = list.text().trim();
			if (!content.equals(""))
			{
				words.add(content);
			}
		}
		return words;
	}
	
	
	private static void absoluteLinks(Document document, String baseUri)    {
		Elements links;
		
	    links = document.select("a[href]");
	    for (Element link : links)  {
	        if (!link.attr("href").toLowerCase().startsWith("http://"))    {
	            link.attr("href", baseUri+link.attr("href"));
	        }
	    }
	    
	    links = document.select("img[src]");
	    for (Element link : links)  {
	        if (!link.attr("src").toLowerCase().startsWith("http://"))    {
	            link.attr("src", baseUri+link.attr("src"));
	        }
	    }
	    
	    links = document.select("link[href]");
	    for (Element link : links)  {
	        if (!link.attr("href").toLowerCase().startsWith("http://"))    {
	            link.attr("href", baseUri+link.attr("href"));
	        }
	    }
	}
	
	private static void absoluteLinks2(Document document, String baseUri)    {
	Elements links;
	
    links = document.select("[src]");
    for (Element link : links)  {
        if (!link.attr("src").toLowerCase().startsWith("http://"))    {
            link.attr("src", baseUri+link.attr("src"));
        }
    }

}



	


}