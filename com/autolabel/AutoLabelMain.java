package com.autolabel;

//////////////////////////////////////////////////////////////////////////////////////////////
//This is the start window of the whole application.
//It has three main functions (corresponding to 3 buttons: Web Data, Crowdsourced Data, Manage DB)
//Web Data: It process the web data, to get the Store Names--Gps Coordinates---Representative Keywords
//Crowdsourced Data: It processed the crowdsource data, for each place/store, it get a file which contains GPS, <MAC, LbRss, UbRss, MeanRss, Order>s, OCRed Words List
//Mange DB: It does keyword matching between processed result from Web Data and Crowdsourced Data, it generated: GPS coordinates---AP List-----Place/Store Name,  which could be uploaded to server
//////////////////////////////////////////////////////////////////////////////////////////////


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javax.swing.JOptionPane;

public class AutoLabelMain extends JFrame implements ActionListener {
	JPanel pnlMain;
	JButton btnWeb, btnCrowdsource, btnKeywordMatcher, btnUploadDB, btnSimilarity, btnProcessEvaluation, btnClustering, btnExit;
	int nScreenWidth, nScreenHeight;

		
	public AutoLabelMain() {
		// TODO Auto-generated constructor stub
		super("AutoLabel Data Processor");
		pnlMain = new JPanel();
		pnlMain.setLayout(null);
		this.getContentPane().add(pnlMain);
				
		btnWeb = new JButton("Web Data");
		btnWeb.addActionListener(this);
		btnWeb.setBounds(120, 30, 160, 60);
		btnWeb.setSize(160, 60);
		btnWeb.setLocation(120, 30);
		
		btnCrowdsource = new JButton("Crowdsourced Data");
		btnCrowdsource.addActionListener(this);
		btnCrowdsource.setBounds(400, 30, 160, 60);
		btnCrowdsource.setSize(160, 60);
		btnCrowdsource.setLocation(400, 30);
		
		btnKeywordMatcher = new JButton("Keyword Matcher");
		btnKeywordMatcher.addActionListener(this);
		btnKeywordMatcher.setBounds(120, 140, 160, 60);
		btnKeywordMatcher.setSize(160, 60);
		btnKeywordMatcher.setLocation(120, 140);

		btnUploadDB = new JButton("Data Upload");
		btnUploadDB.addActionListener(this);
		btnUploadDB.setBounds(400, 140, 160, 60);
		btnUploadDB.setSize(160, 60);
		btnUploadDB.setLocation(400, 140);
	
		btnSimilarity = new JButton("Similarity");
		btnSimilarity.addActionListener(this);
		btnSimilarity.setBounds(120, 250, 160, 60);
		btnSimilarity.setSize(160, 60);
		btnSimilarity.setLocation(120, 250);

		btnProcessEvaluation = new JButton("Evaluate");
		btnProcessEvaluation.addActionListener(this);
		btnProcessEvaluation.setBounds(400, 250, 160, 60);
		btnProcessEvaluation.setSize(160, 60);
		btnProcessEvaluation.setLocation(400, 250);

		btnClustering = new JButton("Clustering");
		btnClustering.addActionListener(this);
		btnClustering.setBounds(120, 340, 160, 60);
		btnClustering.setSize(160, 60);
		btnClustering.setLocation(120, 340);
		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(this);

		btnExit.setBounds(400, 340, 160, 60);
		btnExit.setSize(160, 60);
		btnExit.setLocation(400, 340);
		
		pnlMain.add(btnWeb);
		pnlMain.add(btnCrowdsource);
		pnlMain.add(btnKeywordMatcher);	
		pnlMain.add(btnUploadDB);
		pnlMain.add(btnSimilarity);
		pnlMain.add(btnProcessEvaluation);
		pnlMain.add(btnClustering);
		pnlMain.add(btnExit);
		
		setSize(700, 450);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		nScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		nScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation(nScreenWidth / 2 - 300, nScreenHeight / 2 - 175);
				
	}
	
	
	public void actionPerformed(ActionEvent ae) {		
		if (ae.getSource() == btnWeb) {
			//Utility.testFrameSubset();
			
			//Utility.testFrameSubsetA();
		
			new CandidatePlaceAcquisitionMain();
		}
				
		if (ae.getSource() == btnCrowdsource) {
			new CrowdsourceWin();
		}

		if (ae.getSource() == btnKeywordMatcher) {			
			new PlaceApManager();			
		}

		if (ae.getSource() == btnUploadDB) {			
			new DataUploadWin();
		}

		if (ae.getSource() == btnSimilarity) {
			new CheckSimilarityWin();
		}

		if (ae.getSource() == btnProcessEvaluation) {
			new ProcessEvaluationWin();
		}

		if (ae.getSource() == btnClustering) {
			new ClusteringWin();
		}
		
		if (ae.getSource() == btnExit) {
			System.exit(0);
		}
	}
		
		
	public static void main(String[] args) {
		new AutoLabelMain();
	}
	
	
}
	
	
