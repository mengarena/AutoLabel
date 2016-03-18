package com.autolabel;

//This class is doing Keyword Matching.
//It matches a processed Crowdsourced store info with the candidate PlaceWeb Data
//The processed Crowdsourced store info means that this is the result from processing all the crowdsourced data for one store; the GPS, RSS are averaged, MAC are merged, OCRed text is merged
//Candidate PlaceWeb data are the place information obtained through Place Website scan/analyze, the places are possibly close to the expected area.
//


import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ConstructMacPlaceMapping {
	
	public ConstructMacPlaceMapping() {
		// TODO Auto-generated constructor stub
	}

	//crowdSourcedPlaceData is the processed CrowdSourced data of a place
	//lstPlaceWebData is the candidate list of the place web data
	public AL_PlaceAPInfo matchKeywords(List<AL_PlaceWebData> lstPlaceWebData, AL_CrowdSourcedPlaceData crowdSourcedPlaceData) {
		AL_PlaceAPInfo placeAPInfo = null;
		double fMatchingScore = 0.0;
		double fMaxMatchingScore = 0.0;
		double fBaseMatchingScore = 0.0;
		int nMaxMatchingIdx = -1;
		double fCrowdsourcedGpsLat = 0.0f;
		double fCrowdsourceGpsLong = 0.0f;
		List<AL_APInfoForDB> lstAPInfoForDB = null;
		List<String> lstOCRedWords = null;
		int i;
		AL_PlaceWebData placeWebData = null;
		boolean bClose = false;
		
		fCrowdsourcedGpsLat = crowdSourcedPlaceData.getGpsLat();
		fCrowdsourceGpsLong = crowdSourcedPlaceData.getGpsLong();
		lstAPInfoForDB = crowdSourcedPlaceData.getAPInfoForDB();
		lstOCRedWords = crowdSourcedPlaceData.getOCRedWord();
		
		for (i=0; i<lstPlaceWebData.size(); i++) {
			placeWebData = lstPlaceWebData.get(i);
			
			bClose = Utility.isGpsCoordinatesClose(fCrowdsourcedGpsLat, fCrowdsourceGpsLong, placeWebData.getGpsLat(), placeWebData.getGpsLong());
			
			if (bClose == false) {
				continue;
			} else {
				fMatchingScore = Utility.calculateMatchingScoreWithSingleWeight(lstOCRedWords, placeWebData.getKeywordWeight());
				if (fMatchingScore > fMaxMatchingScore) {
					fMaxMatchingScore = fMatchingScore;
					nMaxMatchingIdx = i;
				}
			}
		}
		
//		if (nMaxMatchingIdx != -1 && fMaxMatchingScore >= MATCHED_WEIGHT_THRESHOLD) {
		if (nMaxMatchingIdx != -1) {

			placeAPInfo = new AL_PlaceAPInfo();
			
			placeAPInfo.setPlaceName(lstPlaceWebData.get(nMaxMatchingIdx).getPlaceName());
			placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
			placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
			placeAPInfo.setAPInfo(lstAPInfoForDB);
		}
		
		return placeAPInfo;
	}


	//This function does keyword matching. It compare the OCRed Words in crowdSourcedPlaceData with the Place name and Keywords in lstPlaceWebData for each store,
	//1) It first compares with Place/Store name, 1.1) if only one place name matched, then this is the result.
	//                                            1.2) if more than one place names matched, then compare OCRed words with these places's keywords, the one achieves highest score is the result
	//2) If no place name matched, it compares the OCRed words with Keywords to calculate the weight, the one achieve the highest total weight is the recognized place
	//
	//During Step 1), the place/store names are processed, the words like "a, an, the, is, was, these, this...building, office, mall, shop, store, restaurant, bar..." is not compared. 
	//For example, "Cravings Restaurant", only "Cravings" is meaningful and representative
	//
	//crowdSourcedPlaceData is the processed CrowdSourced data of a place
	//lstPlaceWebData is the candidate list of the place web data
	//fMaxMatchingScore is the matched score
	public AL_PlaceAPInfo matchKeywords(List<AL_PlaceWebData> lstPlaceWebData, AL_CrowdSourcedPlaceData crowdSourcedPlaceData, double fMaxMatchingScore) {
		AL_PlaceAPInfo placeAPInfo = null;
		int nMaxMatchingIdx = -1;
		double fCrowdsourcedGpsLat = 0.0f;
		double fCrowdsourceGpsLong = 0.0f;
		List<AL_APInfoForDB> lstAPInfoForDB = null;
		List<String> lstOCRedWords = null;
		int i;
		AL_PlaceWebData placeWebData = null;
		boolean bClose = false;
		double fMatchingScore = 0.0;
		int nIdx;
		List<String> lstCandidatePlaceNames = new ArrayList<String>();
		List<AL_PlaceWebData> lstCandidatePlaceWebData = new ArrayList<AL_PlaceWebData>();
		List<AL_PlaceWebData> lstFinalCandidatePlaceWebData = new ArrayList<AL_PlaceWebData>();
		
		fMaxMatchingScore = 0.0;
		
		if (lstPlaceWebData == null || lstPlaceWebData.size() == 0 || crowdSourcedPlaceData == null) return null;
		
		fCrowdsourcedGpsLat = crowdSourcedPlaceData.getGpsLat();
		fCrowdsourceGpsLong = crowdSourcedPlaceData.getGpsLong();
		lstAPInfoForDB = crowdSourcedPlaceData.getAPInfoForDB();
		lstOCRedWords = crowdSourcedPlaceData.getOCRedWord();
		
		//Filter out the place which is far from the crowdsourced data 
		for (i=0; i<lstPlaceWebData.size(); i++) {
			placeWebData = lstPlaceWebData.get(i);
			
			bClose = Utility.isGpsCoordinatesClose(fCrowdsourcedGpsLat, fCrowdsourceGpsLong, placeWebData.getGpsLat(), placeWebData.getGpsLong());
			
			if (bClose == false) {
				continue;
			} else {
				lstCandidatePlaceWebData.add(placeWebData);
			}
		}
		
		if (lstCandidatePlaceWebData.size() == 0) return null;
		
		
		for (i=0; i<lstCandidatePlaceWebData.size(); i++) {
			lstCandidatePlaceNames.add(lstCandidatePlaceWebData.get(i).getPlaceName());
		}
		
		//Compare OCRed Words with Place Names
		List<Integer> lstMatchedPlaceIdx = Utility.matchOCRedWordsWithPlaceNames(lstCandidatePlaceNames, lstOCRedWords);
		
		if (lstMatchedPlaceIdx == null) {  //No place name is matched, so need to match every place's keywords
			for (i=0; i<lstCandidatePlaceWebData.size(); i++) {
				placeWebData = lstCandidatePlaceWebData.get(i);
				
				fMatchingScore = Utility.calculateMatchingScoreWithSingleWeight(lstOCRedWords, placeWebData.getKeywordWeight());
				if (fMatchingScore > fMaxMatchingScore) {
					fMaxMatchingScore = fMatchingScore;
					nMaxMatchingIdx = i;
				}				
			}
			
//			if (nMaxMatchingIdx != -1 && fMaxMatchingScore >= MATCHED_WEIGHT_THRESHOLD) {
			if (nMaxMatchingIdx != -1) {

				placeAPInfo = new AL_PlaceAPInfo();
				
				placeAPInfo.setPlaceName(lstCandidatePlaceWebData.get(nMaxMatchingIdx).getPlaceName());
				placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
				placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
				placeAPInfo.setAPInfo(lstAPInfoForDB);
			}
			
			
		} else {  //Place name(s) matched
			if (lstMatchedPlaceIdx.size() == 1) {  //Only one place is matched, then this is the place for the crowdsourced place data
				nIdx = lstMatchedPlaceIdx.get(0).intValue();
				
				placeAPInfo = new AL_PlaceAPInfo();
				placeAPInfo.setPlaceName(lstCandidatePlaceWebData.get(nIdx).getPlaceName());
				placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
				placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
				placeAPInfo.setAPInfo(lstAPInfoForDB);

				fMaxMatchingScore = 1.0;
				
			} else if (lstMatchedPlaceIdx.size() > 1) {  //More than one place is matched, then we need to compare the OCRed words with these places' keywords, the one gives highest score it the target place
				
				for (i=0; i<lstMatchedPlaceIdx.size(); i++) {
					nIdx = lstMatchedPlaceIdx.get(i).intValue();
					lstFinalCandidatePlaceWebData.add(lstCandidatePlaceWebData.get(nIdx));
				}
				
				for (i=0; i<lstFinalCandidatePlaceWebData.size(); i++) {
					placeWebData = lstFinalCandidatePlaceWebData.get(i);
					
					fMatchingScore = Utility.calculateMatchingScoreWithSingleWeight(lstOCRedWords, placeWebData.getKeywordWeight());
					if (fMatchingScore > fMaxMatchingScore) {
						fMaxMatchingScore = fMatchingScore;
						nMaxMatchingIdx = i;
					}				
					
				}
				
//				if (nMaxMatchingIdx != -1 && fMaxMatchingScore >= Utility.MATCHED_WEIGHT_THRESHOLD) {
				if (nMaxMatchingIdx != -1) {

					placeAPInfo = new AL_PlaceAPInfo();
					
					placeAPInfo.setPlaceName(lstFinalCandidatePlaceWebData.get(nMaxMatchingIdx).getPlaceName());
					placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
					placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
					placeAPInfo.setAPInfo(lstAPInfoForDB);
				}
				
				
			}
		}
		
		
		return placeAPInfo;
	}


	
	//#########################################################################
	//
	// This one does keyword matching WITHOUT Weight for OCR words.
	//
	//#########################################################################
	//This function does keyword matching. It compare the OCRed Words in crowdSourcedPlaceData with the Place name and Keywords in lstPlaceWebData for each store,
	//1) It first compares with Place/Store name, 1.1) if only one place name matched, then this is the result.
	//                                            1.2) if more than one place names matched, then compare OCRed words with these places's keywords, the one achieves highest score is the result
	//2) If no place name matched, it compares the OCRed words with Keywords to calculate the weight, the one achieve the highest total weight is the recognized place
	//
	//During Step 1), the place/store names are processed, the words like "a, an, the, is, was, these, this...building, office, mall, shop, store, restaurant, bar..." is not compared. 
	//For example, "Cravings Restaurant", only "Cravings" is meaningful and representative
	//
	//crowdSourcedPlaceData is the processed CrowdSourced data of a place
	//lstPlaceWebData is the candidate list of the place web data
	//fMaxMatchingScore is the matched score
	//lstfMatchingScore will be the list of matching score for each place in lstPlaceWebData
	public AL_PlaceAPInfo matchKeywordsWithSingleWeights(List<AL_PlaceWebData> lstPlaceWebData, AL_CrowdSourcedPlaceData crowdSourcedPlaceData, double fMaxMatchingScore, List<Double> lstfMatchingScore) {
		AL_PlaceAPInfo placeAPInfo = null;
		int nMaxMatchingIdx = -1;
		double fCrowdsourcedGpsLat = 0.0f;
		double fCrowdsourceGpsLong = 0.0f;
		List<AL_APInfoForDB> lstAPInfoForDB = null;
		List<String> lstOCRedWords = null;
		List<String> lstAllOCRedWords = null;
		int i;
		AL_PlaceWebData placeWebData = null;
		boolean bClose = false;
		double fMatchingScore = 0.0;
		double fBaseMatchingScore = 0.0;
		int nIdx;
		List<String> lstCandidatePlaceNames = new ArrayList<String>();
		List<AL_PlaceWebData> lstCandidatePlaceWebData = new ArrayList<AL_PlaceWebData>();
		List<AL_PlaceWebData> lstFinalCandidatePlaceWebData = new ArrayList<AL_PlaceWebData>();
				
		List<Integer> lstCandidatePlaceIdx = new ArrayList<Integer>();
		List<Integer> lstFinalCandidatePlaceIdx = new ArrayList<Integer>();
		
		fMaxMatchingScore = 0.0;
		
		if (lstPlaceWebData == null || lstPlaceWebData.size() == 0 || crowdSourcedPlaceData == null) return null;
		
		fCrowdsourcedGpsLat = crowdSourcedPlaceData.getGpsLat();
		fCrowdsourceGpsLong = crowdSourcedPlaceData.getGpsLong();
		lstAPInfoForDB = crowdSourcedPlaceData.getAPInfoForDB();
		lstOCRedWords = crowdSourcedPlaceData.getOCRedWord();
		lstAllOCRedWords = crowdSourcedPlaceData.getAllOCRedWord();
		
		//Filter out the place which is far from the crowdsourced data 
		for (i=0; i<lstPlaceWebData.size(); i++) {
			lstfMatchingScore.add(0.0);   //Initialize 
			
			placeWebData = lstPlaceWebData.get(i);
			
			bClose = Utility.isGpsCoordinatesClose(fCrowdsourcedGpsLat, fCrowdsourceGpsLong, placeWebData.getGpsLat(), placeWebData.getGpsLong());
			
			if (bClose == false) {
				continue;
			} else {
				lstCandidatePlaceWebData.add(placeWebData);
				lstCandidatePlaceIdx.add(i);
			}
		}
		
		if (lstCandidatePlaceWebData.size() == 0) return null;
		
		
		for (i=0; i<lstCandidatePlaceWebData.size(); i++) {
			lstCandidatePlaceNames.add(lstCandidatePlaceWebData.get(i).getPlaceName());
		}
		
		//Compare OCRed Words with Place Names
		//Should OCRedWordsFreq be involved here???
//		List<Integer> lstMatchedPlaceIdx = Utility.matchOCRedWordsWithPlaceNames(lstCandidatePlaceNames, lstOCRedWords);
		
		//Compare Place name with All OCRed words, to see how many words in place name occurs in OCRed words
		List<Integer> lstMatchedPlaceIdx = Utility.matchOCRedWordsWithPlaceNames(lstCandidatePlaceNames, lstAllOCRedWords);

		if (lstMatchedPlaceIdx == null) {  //No place name is matched, so need to match every place's keywords
			for (i=0; i<lstCandidatePlaceWebData.size(); i++) {
				placeWebData = lstCandidatePlaceWebData.get(i);
				
				//Should OCRedWordsFreq be involved here??????????????????????? How?
				fMatchingScore = Utility.calculateMatchingScoreWithSingleWeight(lstOCRedWords, placeWebData.getKeywordWeight());
				
				lstfMatchingScore.set(lstCandidatePlaceIdx.get(i).intValue(), fMatchingScore);  //Set matching score for this place
				
				if (fMatchingScore > fMaxMatchingScore) {
					fMaxMatchingScore = fMatchingScore;
					nMaxMatchingIdx = i;
				}				
			}
			
//			if (nMaxMatchingIdx != -1 && fMaxMatchingScore >= Utility.MATCHED_WEIGHT_THRESHOLD) {
			if (nMaxMatchingIdx != -1) {

				placeAPInfo = new AL_PlaceAPInfo();
				
				placeAPInfo.setPlaceName(lstCandidatePlaceWebData.get(nMaxMatchingIdx).getPlaceName());
				placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
				placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
				placeAPInfo.setAPInfo(lstAPInfoForDB);
			}
			
			
		} else {  //Place name(s) matched
			
			if (lstMatchedPlaceIdx.size() == 1) {  //Only one place is matched, then this is the place for the crowdsourced place data
				nIdx = lstMatchedPlaceIdx.get(0).intValue();
				
				placeAPInfo = new AL_PlaceAPInfo();
				placeAPInfo.setPlaceName(lstCandidatePlaceWebData.get(nIdx).getPlaceName());
				placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
				placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
				placeAPInfo.setAPInfo(lstAPInfoForDB);

				fMaxMatchingScore = 1.0;
				
				lstfMatchingScore.set(lstCandidatePlaceIdx.get(nIdx), fMaxMatchingScore);  //Set matching score for this place/store
				
			} else if (lstMatchedPlaceIdx.size() > 1) {  //More than one place is matched, then we need to compare the OCRed words with these places' keywords, the one gives highest score it the target place
				//In this case, matching with place name occupies 0.5 total possible matching score;
				//Keywords matching occupies another 0.5 total weight
				
				fBaseMatchingScore = 0.5/lstMatchedPlaceIdx.size();  //Set base matching score
				
				for (i=0; i<lstMatchedPlaceIdx.size(); i++) {
					nIdx = lstMatchedPlaceIdx.get(i).intValue();
					lstFinalCandidatePlaceWebData.add(lstCandidatePlaceWebData.get(nIdx));
					
					lstFinalCandidatePlaceIdx.add(lstCandidatePlaceIdx.get(nIdx));
				}
				
				//In case not matched with keywords, take the first place as the result, because it already matched with the place name
				nMaxMatchingIdx = 0;
				
				for (i=0; i<lstFinalCandidatePlaceWebData.size(); i++) {
					placeWebData = lstFinalCandidatePlaceWebData.get(i);
					
					//Should OCRedWordsFreq be involved here????????????????????? How?
					fMatchingScore = Utility.calculateMatchingScoreWithSingleWeight(lstOCRedWords, placeWebData.getKeywordWeight());
					
					fMatchingScore = fMatchingScore*0.5 + fBaseMatchingScore;
					
					fMatchingScore = Math.round(fMatchingScore*1000)/1000.0;
					
					lstfMatchingScore.set(lstFinalCandidatePlaceIdx.get(i).intValue(), fMatchingScore);
					
					if (fMatchingScore > fMaxMatchingScore) {
						fMaxMatchingScore = fMatchingScore;
						nMaxMatchingIdx = i;
					}				
					
				}
				
//				if (nMaxMatchingIdx != -1 && fMaxMatchingScore >= Utility.MATCHED_WEIGHT_THRESHOLD) {
				if (nMaxMatchingIdx != -1) {

					placeAPInfo = new AL_PlaceAPInfo();
					
					placeAPInfo.setPlaceName(lstFinalCandidatePlaceWebData.get(nMaxMatchingIdx).getPlaceName());
					placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
					placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
					placeAPInfo.setAPInfo(lstAPInfoForDB);
				}
				
				
			}
		}
		
		
		return placeAPInfo;
	}
	

	
	//#########################################################################
	//
	// This one does keyword matching WITH Weight for OCR words.
	//
	//#########################################################################
	//This function does keyword matching. It compare the OCRed Words in crowdSourcedPlaceData with the Place name and Keywords in lstPlaceWebData for each store,
	//1) It first compares with Place/Store name, 1.1) if only one place name matched, then this is the result.
	//                                            1.2) if more than one place names matched, then compare OCRed words with these places's keywords, the one achieves highest score is the result
	//2) If no place name matched, it compares the OCRed words with Keywords to calculate the weight, the one achieve the highest total weight is the recognized place
	//
	// When matching OCRed words with Place Keywords, the weight of OCRed Words are involved.
	//
	//During Step 1), the place/store names are processed, the words like "a, an, the, is, was, these, this...building, office, mall, shop, store, restaurant, bar..." is not compared. 
	//For example, "Cravings Restaurant", only "Cravings" is meaningful and representative
	//
	//crowdSourcedPlaceData is the processed CrowdSourced data of a place
	//lstPlaceWebData is the candidate list of the place web data
	//fMaxMatchingScore is the matched score
	//lstfMatchingScore will be the list of matching score for each place in lstPlaceWebData
//	public AL_PlaceAPInfo matchKeywordsWithDoubleWeights(List<AL_PlaceWebData> lstPlaceWebData, AL_CrowdSourcedPlaceData crowdSourcedPlaceData, double fMaxMatchingScore, List<Double> lstfMatchingScore) {
	public AL_PlaceAPInfo matchKeywordsWithDoubleWeights(List<AL_PlaceWebData> lstPlaceWebData, AL_CrowdSourcedPlaceData crowdSourcedPlaceData, List<Double> lstfMaxMatchingScore, List<Double> lstfMatchingScore) {
		double fMaxMatchingScore = 0.0;
		AL_PlaceAPInfo placeAPInfo = null;
		int nMaxMatchingIdx = -1;
		double fCrowdsourcedGpsLat = 0.0f;
		double fCrowdsourceGpsLong = 0.0f;
		List<AL_APInfoForDB> lstAPInfoForDB = null;
		List<String> lstOCRedWords = null;
		List<Double> lstfOCRedWordsWeight = null;
		List<String> lstAllOCRedWords = null;
		int i;
		AL_PlaceWebData placeWebData = null;
		boolean bClose = false;
		double fMatchingScore = 0.0;
		double fBaseMatchingScore = 0.0;
		int nIdx;
		List<String> lstCandidatePlaceNames = new ArrayList<String>();
		List<AL_PlaceWebData> lstCandidatePlaceWebData = new ArrayList<AL_PlaceWebData>();
		List<AL_PlaceWebData> lstFinalCandidatePlaceWebData = new ArrayList<AL_PlaceWebData>();
				
		List<Integer> lstCandidatePlaceIdx = new ArrayList<Integer>();
		List<Integer> lstFinalCandidatePlaceIdx = new ArrayList<Integer>();
		
		fMaxMatchingScore = 0.0;
		
		if (lstPlaceWebData == null || lstPlaceWebData.size() == 0 || crowdSourcedPlaceData == null) {
			lstfMaxMatchingScore.add(fMaxMatchingScore);
			return null;
		}
		
		fCrowdsourcedGpsLat = crowdSourcedPlaceData.getGpsLat();
		fCrowdsourceGpsLong = crowdSourcedPlaceData.getGpsLong();
		lstAPInfoForDB = crowdSourcedPlaceData.getAPInfoForDB();
		lstOCRedWords = crowdSourcedPlaceData.getOCRedWord();
		lstfOCRedWordsWeight = crowdSourcedPlaceData.getOCRedWordsWeight();
		lstAllOCRedWords = crowdSourcedPlaceData.getAllOCRedWord();
		
		//Filter out the place which is far from the crowdsourced data 
		for (i=0; i<lstPlaceWebData.size(); i++) {
			lstfMatchingScore.add(0.0);   //Initialize 
			
			placeWebData = lstPlaceWebData.get(i);
			
			bClose = Utility.isGpsCoordinatesClose(fCrowdsourcedGpsLat, fCrowdsourceGpsLong, placeWebData.getGpsLat(), placeWebData.getGpsLong());
			
			if (bClose == false) {
				continue;
			} else {
				lstCandidatePlaceWebData.add(placeWebData);
				lstCandidatePlaceIdx.add(i);
			}
		}
		
		if (lstCandidatePlaceWebData.size() == 0) return null;
		
		
		for (i=0; i<lstCandidatePlaceWebData.size(); i++) {
			lstCandidatePlaceNames.add(lstCandidatePlaceWebData.get(i).getPlaceName());
		}
		
		//Compare OCRed Words with Place Names
		//Should OCRedWordsFreq be involved here???
///		List<Integer> lstMatchedPlaceIdx = Utility.matchOCRedWordsWithPlaceNames(lstCandidatePlaceNames, lstOCRedWords);
		//Compare Place name with All OCRed words, to see how many words in place name occurs in OCRed words
		List<Integer> lstMatchedPlaceIdx = Utility.matchOCRedWordsWithPlaceNames(lstCandidatePlaceNames, lstAllOCRedWords);
				
		if (lstMatchedPlaceIdx == null) {  //No place name is matched, so need to match every place's keywords
			for (i=0; i<lstCandidatePlaceWebData.size(); i++) {
				placeWebData = lstCandidatePlaceWebData.get(i);
				
				//fMatchingScore = Utility.calculateMatchingScoreWithSingleWeight(lstOCRedWords, placeWebData.getKeywordWeight());
				fMatchingScore = Utility.calculateMatchingScoreWithDoubleWeight(lstOCRedWords, lstfOCRedWordsWeight, placeWebData.getKeywordWeight());
				
				fMatchingScore = Math.round(fMatchingScore*1000)/1000.0;

				lstfMatchingScore.set(lstCandidatePlaceIdx.get(i).intValue(), fMatchingScore);  //Set matching score for this place
				
				if (fMatchingScore > fMaxMatchingScore) {
					fMaxMatchingScore = fMatchingScore;
					
					nMaxMatchingIdx = i;
				}				
			}
			
//			if (nMaxMatchingIdx != -1 && fMaxMatchingScore >= Utility.MATCHED_WEIGHT_THRESHOLD) {
			if (nMaxMatchingIdx != -1) {

				placeAPInfo = new AL_PlaceAPInfo();
				
				placeAPInfo.setPlaceName(lstCandidatePlaceWebData.get(nMaxMatchingIdx).getPlaceName());
				placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
				placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
				placeAPInfo.setAPInfo(lstAPInfoForDB);
			}
			
		} else {  //Place name(s) matched
			
			if (lstMatchedPlaceIdx.size() == 1) {  //Only one place is matched, then this is the place for the crowdsourced place data
				nIdx = lstMatchedPlaceIdx.get(0).intValue();
				
				placeAPInfo = new AL_PlaceAPInfo();
				placeAPInfo.setPlaceName(lstCandidatePlaceWebData.get(nIdx).getPlaceName());
				placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
				placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
				placeAPInfo.setAPInfo(lstAPInfoForDB);

				fMaxMatchingScore = 1.0;
				
				lstfMatchingScore.set(lstCandidatePlaceIdx.get(nIdx), fMaxMatchingScore);  //Set matching score for this place/store
				
			} else if (lstMatchedPlaceIdx.size() > 1) {  //More than one place is matched, then we need to compare the OCRed words with these places' keywords, the one gives highest score it the target place
				//In this case, matching with place name occupies 0.5 total possible matching score;
				//Keywords matching occupies another 0.5 total weight
				
				fBaseMatchingScore = 0.5/lstMatchedPlaceIdx.size();  //Set base matching score
				
				for (i=0; i<lstMatchedPlaceIdx.size(); i++) {
					nIdx = lstMatchedPlaceIdx.get(i).intValue();
					lstFinalCandidatePlaceWebData.add(lstCandidatePlaceWebData.get(nIdx));
					
					lstFinalCandidatePlaceIdx.add(lstCandidatePlaceIdx.get(nIdx));
				}
				
				//In case not matched with keywords, take the first place as the result, because it already matched with the place name
				nMaxMatchingIdx = 0;
				
				for (i=0; i<lstFinalCandidatePlaceWebData.size(); i++) {
					placeWebData = lstFinalCandidatePlaceWebData.get(i);
					
					//fMatchingScore = Utility.calculateMatchingScoreWithSingleWeight(lstOCRedWords, placeWebData.getKeywordWeight());
					fMatchingScore = Utility.calculateMatchingScoreWithDoubleWeight(lstOCRedWords, lstfOCRedWordsWeight, placeWebData.getKeywordWeight());
					
					fMatchingScore = fMatchingScore*0.5 + fBaseMatchingScore;
					
					fMatchingScore = Math.round(fMatchingScore*1000)/1000.0;

					lstfMatchingScore.set(lstFinalCandidatePlaceIdx.get(i).intValue(), fMatchingScore);
					
					if (fMatchingScore > fMaxMatchingScore) {
						fMaxMatchingScore = fMatchingScore;
						
						nMaxMatchingIdx = i;
					}				
					
				}
				
//				if (nMaxMatchingIdx != -1 && fMaxMatchingScore >= Utility.MATCHED_WEIGHT_THRESHOLD) {
				if (nMaxMatchingIdx != -1) {

					placeAPInfo = new AL_PlaceAPInfo();
					
					placeAPInfo.setPlaceName(lstFinalCandidatePlaceWebData.get(nMaxMatchingIdx).getPlaceName());
					placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
					placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
					placeAPInfo.setAPInfo(lstAPInfoForDB);
				}
				
			}
		}
		
		lstfMaxMatchingScore.add(fMaxMatchingScore);
		
		return placeAPInfo;
	}
	

	
	////////////////////////////////////////////////////////////////////////
	//This function matches keywords with TF-IDF weight
	public AL_PlaceAPInfo matchKeywordsWithTFIDF(List<AL_PlaceWebData> lstPlaceWebData, AL_CrowdSourcedPlaceData crowdSourcedPlaceData, 
												List<Double> lstfMaxMatchingScore, List<Double> lstfMatchingScore, 
												FileWriter fwMatchedCommonWordsFile, String sGroundTruthPlaceName) {
		double fMaxMatchingScore = 0.0;
		AL_PlaceAPInfo placeAPInfo = null;
		int nMaxMatchingIdx = -1;
		double fCrowdsourcedGpsLat = 0.0f;
		double fCrowdsourceGpsLong = 0.0f;
		List<AL_APInfoForDB> lstAPInfoForDB = null;
		List<String> lstOCRedWords = null;
		List<Double> lstfOCRedWordsWeight = null;
		List<String> lstAllOCRedWords = null;
		int i;
		AL_PlaceWebData placeWebData = null;
		boolean bClose = false;
		double fMatchingScore = 0.0;
		double fBaseMatchingScore = 0.0;
		int nIdx;
		List<String> lstCandidatePlaceNames = new ArrayList<String>();
		List<AL_PlaceWebData> lstCandidatePlaceWebData = new ArrayList<AL_PlaceWebData>();
		List<AL_PlaceWebData> lstFinalCandidatePlaceWebData = new ArrayList<AL_PlaceWebData>();
				
		List<Integer> lstCandidatePlaceIdx = new ArrayList<Integer>();
		List<Integer> lstFinalCandidatePlaceIdx = new ArrayList<Integer>();
		
		fMaxMatchingScore = 0.0;
		
		if (lstPlaceWebData == null || lstPlaceWebData.size() == 0 || crowdSourcedPlaceData == null) {
			lstfMaxMatchingScore.add(fMaxMatchingScore);
			return null;
		}
		
		fCrowdsourcedGpsLat = crowdSourcedPlaceData.getGpsLat();
		fCrowdsourceGpsLong = crowdSourcedPlaceData.getGpsLong();
		lstAPInfoForDB = crowdSourcedPlaceData.getAPInfoForDB();
		lstOCRedWords = crowdSourcedPlaceData.getOCRedWord();
		lstfOCRedWordsWeight = crowdSourcedPlaceData.getOCRedWordsWeight();
		lstAllOCRedWords = crowdSourcedPlaceData.getAllOCRedWord();
		
		//Filter out the place which is far from the crowdsourced data 
		for (i=0; i<lstPlaceWebData.size(); i++) {
			lstfMatchingScore.add(0.0);   //Initialize 
			
			placeWebData = lstPlaceWebData.get(i);
			
			bClose = Utility.isGpsCoordinatesClose(fCrowdsourcedGpsLat, fCrowdsourceGpsLong, placeWebData.getGpsLat(), placeWebData.getGpsLong());
			
			if (bClose == false) {
				continue;
			} else {
				lstCandidatePlaceWebData.add(placeWebData);
				lstCandidatePlaceIdx.add(i);
			}
		}
		
		if (lstCandidatePlaceWebData.size() == 0) return null;
		
		
		for (i=0; i<lstCandidatePlaceWebData.size(); i++) {
			lstCandidatePlaceNames.add(lstCandidatePlaceWebData.get(i).getPlaceName());
		}
		
		//Compare OCRed Words with Place Names
		//Should OCRedWordsFreq be involved here???
///		List<Integer> lstMatchedPlaceIdx = Utility.matchOCRedWordsWithPlaceNames(lstCandidatePlaceNames, lstOCRedWords);
		//Compare Place name with All OCRed words, to see how many words in place name occurs in OCRed words
		List<Integer> lstMatchedPlaceIdx = null;
		
		if (Utility.MATCH_WITH_PLACENAME) {
			lstMatchedPlaceIdx = Utility.matchOCRedWordsWithPlaceNames(lstCandidatePlaceNames, lstAllOCRedWords);
		}
				
		if (lstMatchedPlaceIdx == null) {  //No place name is matched, so need to match every place's keywords

			List<String> lstPlaceName = new ArrayList<String>();
			List<Double> lstTmpMatchingScore = new ArrayList<Double>();
//			Utility.CalculateCollectionMatching(lstOCRedWords, lstfOCRedWordsWeight, lstCandidatePlaceWebData, lstPlaceName, lstTmpMatchingScore);
			Utility.CalculateCollectionMatching_withSaving(fwMatchedCommonWordsFile, sGroundTruthPlaceName, lstOCRedWords, lstfOCRedWordsWeight, lstCandidatePlaceWebData, lstPlaceName, lstTmpMatchingScore, -1);
			
			for (i=0; i<lstCandidatePlaceWebData.size(); i++) {
				placeWebData = lstCandidatePlaceWebData.get(i);
				
				//fMatchingScore = Utility.calculateMatchingScoreWithSingleWeight(lstOCRedWords, placeWebData.getKeywordWeight());
				//fMatchingScore = Utility.calculateMatchingScoreWithDoubleWeight(lstOCRedWords, lstfOCRedWordsWeight, placeWebData.getKeywordWeight());
				
				//fMatchingScore = Math.round(fMatchingScore*1000)/1000.0;

				fMatchingScore = lstTmpMatchingScore.get(i).doubleValue();
				
				lstfMatchingScore.set(lstCandidatePlaceIdx.get(i).intValue(), fMatchingScore);  //Set matching score for this place
				
				if (fMatchingScore > fMaxMatchingScore) {
					fMaxMatchingScore = fMatchingScore;
					
					nMaxMatchingIdx = i;
				}				
			}
			
//			if (nMaxMatchingIdx != -1 && fMaxMatchingScore >= Utility.MATCHED_WEIGHT_THRESHOLD) {
			if (nMaxMatchingIdx != -1) {

				placeAPInfo = new AL_PlaceAPInfo();
				
				placeAPInfo.setPlaceName(lstCandidatePlaceWebData.get(nMaxMatchingIdx).getPlaceName());
				placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
				placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
				placeAPInfo.setAPInfo(lstAPInfoForDB);
			}
			
		} else {  //Place name(s) matched
			
			if (lstMatchedPlaceIdx.size() == 1) {  //Only one place is matched, then this is the place for the crowdsourced place data
				nIdx = lstMatchedPlaceIdx.get(0).intValue();
				
				placeAPInfo = new AL_PlaceAPInfo();
				placeAPInfo.setPlaceName(lstCandidatePlaceWebData.get(nIdx).getPlaceName());
				placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
				placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
				placeAPInfo.setAPInfo(lstAPInfoForDB);

				fMaxMatchingScore = 1.0;
				
				lstfMatchingScore.set(lstCandidatePlaceIdx.get(nIdx), fMaxMatchingScore);  //Set matching score for this place/store
				
			} else if (lstMatchedPlaceIdx.size() > 1) {  //More than one place is matched, then we need to compare the OCRed words with these places' keywords, the one gives highest score it the target place
				//In this case, matching with place name occupies 0.5 total possible matching score;
				//Keywords matching occupies another 0.5 total weight
				
				fBaseMatchingScore = 0.5/lstMatchedPlaceIdx.size();  //Set base matching score
				
				for (i=0; i<lstMatchedPlaceIdx.size(); i++) {
					nIdx = lstMatchedPlaceIdx.get(i).intValue();
					lstFinalCandidatePlaceWebData.add(lstCandidatePlaceWebData.get(nIdx));
					
					lstFinalCandidatePlaceIdx.add(lstCandidatePlaceIdx.get(nIdx));
				}
				
				List<String> lstPlaceName = new ArrayList<String>();
				List<Double> lstTmpMatchingScore = new ArrayList<Double>();
	//			Utility.CalculateCollectionMatching(lstOCRedWords, lstfOCRedWordsWeight, lstFinalCandidatePlaceWebData, lstPlaceName, lstTmpMatchingScore);
				Utility.CalculateCollectionMatching_withSaving(fwMatchedCommonWordsFile, sGroundTruthPlaceName, lstOCRedWords, lstfOCRedWordsWeight, lstFinalCandidatePlaceWebData, lstPlaceName, lstTmpMatchingScore, -1);

				//In case not matched with keywords, take the first place as the result, because it already matched with the place name
				nMaxMatchingIdx = 0;
				
				for (i=0; i<lstFinalCandidatePlaceWebData.size(); i++) {
					placeWebData = lstFinalCandidatePlaceWebData.get(i);
					
					//fMatchingScore = Utility.calculateMatchingScoreWithSingleWeight(lstOCRedWords, placeWebData.getKeywordWeight());
					//fMatchingScore = Utility.calculateMatchingScoreWithDoubleWeight(lstOCRedWords, lstfOCRedWordsWeight, placeWebData.getKeywordWeight());
					
					fMatchingScore = lstTmpMatchingScore.get(i).doubleValue();
					fMatchingScore = fMatchingScore*0.5 + fBaseMatchingScore;
					
					fMatchingScore = Math.round(fMatchingScore*1000)/1000.0;

					lstfMatchingScore.set(lstFinalCandidatePlaceIdx.get(i).intValue(), fMatchingScore);
					
					if (fMatchingScore > fMaxMatchingScore) {
						fMaxMatchingScore = fMatchingScore;
						
						nMaxMatchingIdx = i;
					}				
					
				}
				
//				if (nMaxMatchingIdx != -1 && fMaxMatchingScore >= Utility.MATCHED_WEIGHT_THRESHOLD) {
				if (nMaxMatchingIdx != -1) {

					placeAPInfo = new AL_PlaceAPInfo();
					
					placeAPInfo.setPlaceName(lstFinalCandidatePlaceWebData.get(nMaxMatchingIdx).getPlaceName());
					placeAPInfo.setGpsLat(fCrowdsourcedGpsLat);
					placeAPInfo.setGpsLong(fCrowdsourceGpsLong);
					placeAPInfo.setAPInfo(lstAPInfoForDB);
				}
				
			}
		}
		
		lstfMaxMatchingScore.add(fMaxMatchingScore);
		
		return placeAPInfo;
	}
	

	////////////////////////////////////////////////////////////////////////
	//This function matches keywords with TF-IDF weight
	public List<Double> matchKeywordsWithTFIDF_Subset(List<AL_PlaceWebData> lstPlaceWebData, List<List<Double>> lstlstPlaceWebDataIDF, List<String> lstWords, List<Double> lstfTFWeight, List<String> lstWordsRaw) {
		double fBaseMatchingScore = 0.0;
		double fMatchingScore = 0.0;
		int i;
		int nIdx;
		List<String> lstPlaceNames = new ArrayList<String>();
		
		List<AL_PlaceWebData> lstCandidatePlaceWebData = new ArrayList<AL_PlaceWebData>();				
		List<Integer> lstCandidatePlaceIdx = new ArrayList<Integer>();

		List<List<Double>> lstlstCandidatePlaceWebDataIDF = new ArrayList<List<Double>>();
		
		List<Double> lstfMatchingScore = new ArrayList<Double>();

		for (i=0; i<lstPlaceWebData.size(); i++) {
			lstPlaceNames.add(lstPlaceWebData.get(i).getPlaceName());
			lstfMatchingScore.add(0.0);  //Initialize
		}
		
		if (lstWords == null || lstWords.size() == 0) {
			return lstfMatchingScore;
		}
		
		List<Integer> lstMatchedPlaceIdx = null;
		
		if (Utility.MATCH_WITH_PLACENAME) {
			lstMatchedPlaceIdx = Utility.matchOCRedWordsWithPlaceNames(lstPlaceNames, lstWordsRaw);
		}

		if (lstMatchedPlaceIdx == null) {  //No place name is matched, so need to match every place's keywords

			List<Double> lstTmpMatchingScore = new ArrayList<Double>();
			Utility.CalculateCollectionMatching_Clustering(lstWords, lstfTFWeight, lstPlaceWebData, lstlstPlaceWebDataIDF, lstTmpMatchingScore);
			
			for (i=0; i<lstPlaceWebData.size(); i++) {
				fMatchingScore = lstTmpMatchingScore.get(i).doubleValue();
				
				lstfMatchingScore.set(i, fMatchingScore);  //Set matching score for this place
			}
			
		} else {  //Place name(s) matched
			
			if (lstMatchedPlaceIdx.size() == 1) {  //Only one place is matched, then this is the place for the crowdsourced place data
				nIdx = lstMatchedPlaceIdx.get(0).intValue();
								
				lstfMatchingScore.set(nIdx, 1.0);  //Set matching score for this place/store
				
			} else if (lstMatchedPlaceIdx.size() > 1) {  //More than one place is matched, then we need to compare the OCRed words with these places' keywords, the one gives highest score it the target place
				
				fBaseMatchingScore = 0.5/lstMatchedPlaceIdx.size();  //Set base matching score
				
				for (i=0; i<lstMatchedPlaceIdx.size(); i++) {
					nIdx = lstMatchedPlaceIdx.get(i).intValue();
					lstCandidatePlaceWebData.add(lstPlaceWebData.get(nIdx));
					lstCandidatePlaceIdx.add(nIdx);
					
					lstlstCandidatePlaceWebDataIDF.add(lstlstPlaceWebDataIDF.get(nIdx));
				}
				
				List<Double> lstTmpMatchingScore = new ArrayList<Double>();
				Utility.CalculateCollectionMatching_Clustering(lstWords, lstfTFWeight, lstCandidatePlaceWebData, lstlstCandidatePlaceWebDataIDF, lstTmpMatchingScore);

				//In case not matched with keywords, take the first place as the result, because it already matched with the place name
				
				for (i=0; i<lstCandidatePlaceWebData.size(); i++) {										
					fMatchingScore = lstTmpMatchingScore.get(i).doubleValue();
					fMatchingScore = fMatchingScore*0.5 + fBaseMatchingScore;

					lstfMatchingScore.set(lstCandidatePlaceIdx.get(i).intValue(), fMatchingScore);					
				}
				
			}
		}
		
		return lstfMatchingScore;
	}
	
	
	
}
