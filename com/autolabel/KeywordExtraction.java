package com.autolabel;

//This class extract nouns and proper names

import java.util.ArrayList;
import java.util.List;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class KeywordExtraction {

    String serializedClassifier1 = "classifiers/english.all.3class.caseless.distsim.crf.ser.gz";
    String serializedClassifier2 = "classifiers/english.nowiki.3class.caseless.distsim.crf.ser.gz";
    String serializedClassifier3 = "classifiers/english.conll.4class.caseless.distsim.crf.ser.gz";
    String serializedClassifier4 = "classifiers/english.muc.7class.caseless.distsim.crf.ser.gz";
    
    String sWordNetDict = "C:\\WordNet\\2.1\\dict\\";   //WordNet database dir
    
    ProperNameExtraction m_ProperNameExtraction = new ProperNameExtraction();
	
	public KeywordExtraction() {
		// TODO Auto-generated constructor stub		
	}

	
	//Get the list of non-duplicate noun/proper name
	public List<String> extractKeywordFromFile(String sWordsFile) {
		List<String> lststrInitResultNERKeywords = new ArrayList<String>();  //Initial NER keywords (duplicates exist)
		List<String> lststrInitNonNERWords = new ArrayList<String>();   //Initial remained words (duplicates exist)
		List<String> lststrResultNERKeywords = new ArrayList<String>();  //Final NER keywords (no duplicates)
		List<String> lststrNonNERWords = new ArrayList<String>();  //Final remained words before WordNet check (no duplicates)
		
		List<String> lststrFinalKeywords = new ArrayList<String>();   //Store the final keyword (proper names & nouns)
		
		//Separate proper names and non-proper words in the file. The result is saved into two separated files.		
		m_ProperNameExtraction.extractProperNamebyFile(serializedClassifier1, sWordsFile, lststrInitResultNERKeywords, lststrInitNonNERWords);
		m_ProperNameExtraction.extractProperNamebyFile(serializedClassifier2, sWordsFile, lststrInitResultNERKeywords, lststrInitNonNERWords);
		m_ProperNameExtraction.extractProperNamebyFile(serializedClassifier3, sWordsFile, lststrInitResultNERKeywords, lststrInitNonNERWords);
		m_ProperNameExtraction.extractProperNamebyFile(serializedClassifier4, sWordsFile, lststrInitResultNERKeywords, lststrInitNonNERWords);
		
		//Remove duplicated proper name words in lststrInitResultNERKeywords 
		for (String sProperNameWord : lststrInitResultNERKeywords) {
			sProperNameWord = sProperNameWord.trim();
			
			if (!lststrResultNERKeywords.contains(sProperNameWord)) {
				//if ((sProperNameWord.length() > 1) && (sProperNameWord.charAt(0) != '$') && (isNumeric(sProperNameWord) == false)) {
				//if ((sProperNameWord.length() > 1) && (sProperNameWord.charAt(0) != '$') && (Utility.isNumeric(sProperNameWord) == false) && (Utility.isValidStarted(sProperNameWord) == true)) {
				if ((sProperNameWord.length() > 1) && (Utility.isValidStarted(sProperNameWord) == true) && (Utility.isKeywordBlackListed(sProperNameWord) == false)) {

					lststrResultNERKeywords.add(sProperNameWord);
					lststrFinalKeywords.add(sProperNameWord);
				}
			}
		}

		//Before check the remained words with WordNet, remove the duplicated words in lststrInitNonNERWords 
		//and remove the words which have been recognized as proper name words
		for (String sWord : lststrInitNonNERWords) {
			sWord = sWord.trim();
			
			if (sWord.length() > 1) {    //If == 1, it might be Punctuation, number of single letter, which could not be valid noun
				if (sWord.charAt(0) >= '0' && sWord.charAt(0) <= '9') {  //Number or xx%
					
				} else if (!lststrNonNERWords.contains(sWord) && !lststrResultNERKeywords.contains(sWord)) {
					//if ((sWord.charAt(0) != '$') && (sWord.charAt(0) != '-') && (Utility.isNumeric(sWord) == false) && (Utility.isValidStarted(sWord) == true)) {
					if (Utility.isValidStarted(sWord) == true  && Utility.isKeywordBlackListed(sWord) == false) {

						lststrNonNERWords.add(sWord);
					}
				}				
			}
		}
		
		//Check through WordNet for the remained words in lststrInitNonNERWords
		System.setProperty("wordnet.database.dir", sWordNetDict);
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		for (String sCheckedWord : lststrNonNERWords) {
			Synset[] synsets = database.getSynsets(sCheckedWord, SynsetType.NOUN);    //, false) gives exact matching
			if (synsets.length > 0) {   //It is a noun
				lststrFinalKeywords.add(sCheckedWord);				
			}
		}
				
		return lststrFinalKeywords;
				
	}
	
	
	//Get the list of non-duplicate noun/proper name
	public List<String> extractKeywordFromContents(String sOrgContents) {
		List<String> lststrInitResultNERKeywords = new ArrayList<String>();  //Initial NER keywords (duplicates exist)
		List<String> lststrInitNonNERWords = new ArrayList<String>();   //Initial remained words (duplicates exist)
		List<String> lststrResultNERKeywords = new ArrayList<String>();  //Final NER keywords (no duplicates)
		List<String> lststrNonNERWords = new ArrayList<String>();  //Final remained words before WordNet check (no duplicates)
		
		List<String> lststrFinalKeywords = new ArrayList<String>();   //Store the final keyword (proper names & nouns)
		
		String sContents = Utility.purifyContent(sOrgContents);
		
		//Separate proper names and non-proper words in the file. The result is saved into two separated files.		
		m_ProperNameExtraction.extractProperNamebyContents(serializedClassifier1, sContents, lststrInitResultNERKeywords, lststrInitNonNERWords);
		m_ProperNameExtraction.extractProperNamebyContents(serializedClassifier2, sContents, lststrInitResultNERKeywords, lststrInitNonNERWords);
		m_ProperNameExtraction.extractProperNamebyContents(serializedClassifier3, sContents, lststrInitResultNERKeywords, lststrInitNonNERWords);
		m_ProperNameExtraction.extractProperNamebyContents(serializedClassifier4, sContents, lststrInitResultNERKeywords, lststrInitNonNERWords);
		
		//Remove duplicated proper names in lststrInitResultNERKeywords 
		for (String sProperNameWord : lststrInitResultNERKeywords) {
			sProperNameWord = sProperNameWord.trim();
			
			if (!lststrResultNERKeywords.contains(sProperNameWord)) {
				//if ((sProperNameWord.length() > 1) && (sProperNameWord.charAt(0) != '$') && (isNumeric(sProperNameWord) == false)) {
				//if ((sProperNameWord.length() > 1) && (sProperNameWord.charAt(0) != '$') && (Utility.isNumeric(sProperNameWord) == false) && (Utility.isValidStarted(sProperNameWord) == true)) {
				if ((sProperNameWord.length() > 1) && (Utility.isValidStarted(sProperNameWord) == true) && (Utility.isKeywordBlackListed(sProperNameWord) == false)) {

					lststrResultNERKeywords.add(sProperNameWord);
					lststrFinalKeywords.add(sProperNameWord);
				}
			}
		}

		//Before check the remained words with WordNet, remove the duplicated words in lststrInitNonNERWords 
		//and remove the words which have been recognized as proper names
		for (String sWord : lststrInitNonNERWords) {
			sWord = sWord.trim();
			
			if (sWord.length() > 1) {    //If == 1, it might be Punctuation, number of single letter, which could not be valid noun
				if (sWord.charAt(0) >= '0' && sWord.charAt(0) <= '9') {   //Number or xx%
					
				} else if (!lststrNonNERWords.contains(sWord) && !lststrResultNERKeywords.contains(sWord)) {
					//if ((sWord.charAt(0) != '$') && (sWord.charAt(0) != '-') && (isNumeric(sWord) == false)) {
					//if ((sWord.charAt(0) != '$') && (sWord.charAt(0) != '-') && (Utility.isNumeric(sWord) == false) && (Utility.isValidStarted(sWord) == true)) {
					if (Utility.isValidStarted(sWord) == true && Utility.isKeywordBlackListed(sWord) == false) {

						lststrNonNERWords.add(sWord);
					}
				}				
			}
		}
		
		//Check through WordNet for the remained words in lststrInitNonNERWords
		System.setProperty("wordnet.database.dir", sWordNetDict);
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		for (String sCheckedWord : lststrNonNERWords) {
			sCheckedWord = sCheckedWord.trim();
			
			Synset[] synsets = database.getSynsets(sCheckedWord, SynsetType.NOUN);    //, false) gives exact matching
			if (synsets.length > 0) {   //It is a noun
				lststrFinalKeywords.add(sCheckedWord);				
			}
		}
				
		return lststrFinalKeywords;
				
	}
	
	
//	public boolean isNumeric(String str)  
//	{  
//		try {  
//			double d = Double.parseDouble(str);  
//		} catch(NumberFormatException nfe) {  
//			return false;  
//		}  
//	  
//		return true;  
//	}	
	
}
