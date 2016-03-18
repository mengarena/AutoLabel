package com.autolabel;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
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
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.StringUtils;
import edu.stanford.nlp.util.Triple;

public class ProperNameExtraction {

	public ProperNameExtraction() {
		// TODO Auto-generated constructor stub
	}

	
	//Check whether sWord is an invalid proper name
	//Here the invalid proper name is like "iiiiiiiiii", "mllllllllllllllmmmmmmmmmmmmm", current Stanford NER tool recognizes these as proper name, but they are not
	//Here we use the rule:  if more than 3 consecutive letters are the same in a word, this is an invalid name
	//"www", "iii" are valid;  "wwww", "iiii" are invalid
	private boolean isValidProperName(String sWord) {
		int nThreshold = 3;
		int nTmpCnt = 0;
		char cCurrentLetter;
		
		cCurrentLetter = sWord.charAt(0);
		nTmpCnt = 1;
		for (int i=1; i<sWord.length(); i++) {
			if (sWord.charAt(i) == cCurrentLetter) {
				nTmpCnt = nTmpCnt + 1;
				if (nTmpCnt > nThreshold) return false;  //Invalid proper name
			} else {
				cCurrentLetter = sWord.charAt(i);
				nTmpCnt = 1;
			}
		}
		
		return true;  //Valid proper name
	}
	
	
	public void extractProperNamebyFile(String serializedClassifier, String sWordsFile, List<String> lststrNERKeywords,  List<String> lststrNonNERKeywords) {
		//Separate proper names and non-proper words in the file. The result is saved into two separated lists.
		try {
			AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

		    String fileContents = IOUtils.slurpFile(sWordsFile);
		    List<List<CoreLabel>> out = classifier.classify(fileContents);
		//    List<Triple<String,Integer,Integer>> myout =  classifier.classifyToCharacterOffsets(fileContents);
		    
		    for (List<CoreLabel> sentence : out) {
		        for (CoreLabel word : sentence) {
		        	if (word.get(CoreAnnotations.AnswerAnnotation.class).equalsIgnoreCase("O") == false) {
		        		//if (isValidProperName(word.word().toLowerCase()) == true) {
			        	if (isValidProperName(word.word().toLowerCase()) == true && Utility.isValidStarted(word.word()) == true && Utility.isKeywordBlackListed(word.word()) == false) {

		        			lststrNERKeywords.add(word.word().toLowerCase());   //This separates "New York" into two words
		        		}
		        	} else {
		        		lststrNonNERKeywords.add(word.word().toLowerCase());   //The words in this will be checked through WordNet
		        	}
		        }
		    }
		  
		  //  This code make "New York" one phrase  
		  //  for (Triple<String,Integer,Integer> triple : myout) {
		  //  	lststrNERKeywords.add(fileContents.substring(triple.second, triple.third).toLowerCase());
		  //  }
		    
		} catch (Exception e) {
		
		}
		
		return;
	}

	
	public void extractProperNamebyContents(String serializedClassifier, String fileContents, List<String> lststrNERKeywords,  List<String> lststrNonNERKeywords) {
		//Separate proper names and non-proper words in the file. The result is saved into two separated lists.
		try {
			AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

		    //String fileContents = IOUtils.slurpFile(sWordsFile);
		    List<List<CoreLabel>> out = classifier.classify(fileContents);
		//    List<Triple<String,Integer,Integer>> myout =  classifier.classifyToCharacterOffsets(fileContents);
		    
		    for (List<CoreLabel> sentence : out) {
		        for (CoreLabel word : sentence) {
		        	if (word.get(CoreAnnotations.AnswerAnnotation.class).equalsIgnoreCase("O") == false) {
		        		//if (isValidProperName(word.word().toLowerCase()) == true) {
			        	if (isValidProperName(word.word().toLowerCase()) == true  && Utility.isValidStarted(word.word()) == true && Utility.isKeywordBlackListed(word.word()) == false) {

		        			lststrNERKeywords.add(word.word().toLowerCase());   //This separates "New York" into two words
		        		}
		        	} else {
		        		lststrNonNERKeywords.add(word.word().toLowerCase());   //The words in this will be checked through WordNet
		        	}
		        }
		    }
		    
			  //  This code make "New York" one phrase  
			  //  for (Triple<String,Integer,Integer> triple : myout) {
			  //  	lststrNERKeywords.add(fileContents.substring(triple.second, triple.third).toLowerCase());
			  //  }
		    
		} catch (Exception e) {
		
		}
		
		return;
	}
	
}
