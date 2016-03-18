package com.autolabel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javax.swing.JOptionPane;

public class CandidatePlaceAcquisitionMain extends JFrame implements ActionListener {
	JPanel pnlMain;
	JLabel lblAreaInfo, lblGpsLat,lblGpsLong;
	JLabel m_lblRunInfo;
	JTextField m_txtAreaInfo, m_txtGpsLat,m_txtGpsLong, m_txtSaveFolder, m_txtPlaceListFile;
	JButton btnSaveFolder, btnPlaceList, btnRun, btnOnePlace, btnPlaces, btnWeight, btnExit;
	JFileChooser m_fcSaveFolder, m_fcPlaceList;
	
	JLabel lblAreaName;
	JTextField m_txtAreaName;
	
	JLabel lblRadius;
	JTextField m_txtRadius;
	
	JLabel lblTopKeywordNum;
	JTextField m_txtTopKeywordNum;
		
	JLabel lblPlaceName, lblURL;
	JTextField m_txtPlaceName, m_txtURL;
	
	int nScreenWidth, nScreenHeight;
	String m_sSaveFolder = "";
	int m_nRadius;
	int m_nTopNum;   // m_nTopType = 1, top N keywords;   m_nTopType = 2, top N% keywords
	
	boolean m_blnRunning = false;
	
	String sDefaultSaveFolder = "E:\\UIUC\\SamsungDemo\\Web";
	
	public void paint(Graphics g) {
		super.paint(g);

	}
	
	public CandidatePlaceAcquisitionMain() {
		// TODO Auto-generated constructor stub
		super("Place Web Analyzer");
		pnlMain = new JPanel();
		pnlMain.setLayout(null);
		this.getContentPane().add(pnlMain);
		
		lblGpsLat = new JLabel("GPS  Lat:");
		lblGpsLat.setBounds(10, 10, 60, 40);
		lblGpsLat.setSize(60, 40);
		lblGpsLat.setLocation(10, 10);

		m_txtGpsLat = new JTextField(30);
		m_txtGpsLat.setBounds(80, 10, 120, 40);
		m_txtGpsLat.setSize(120, 40);
		m_txtGpsLat.setLocation(80, 10);

		lblGpsLong = new JLabel("Long:");
		lblGpsLong.setBounds(220, 10, 40, 40);
		lblGpsLong.setSize(40, 40);
		lblGpsLong.setLocation(220, 10);

		m_txtGpsLong = new JTextField(30);
		m_txtGpsLong.setBounds(270, 10, 120, 40);
		m_txtGpsLong.setSize(120, 40);
		m_txtGpsLong.setLocation(270, 10);

		lblRadius = new JLabel("Radius (m):");
		lblRadius.setBounds(440, 10, 70, 40);
		lblRadius.setSize(70, 40);
		lblRadius.setLocation(440, 10);

		m_txtRadius = new JTextField(30);
		m_txtRadius.setBounds(520, 10, 60, 40);
		m_txtRadius.setSize(60, 40);
		m_txtRadius.setLocation(520, 10);
		
		btnSaveFolder = new JButton("Save Folder ...");
		btnSaveFolder.addActionListener(this);
		btnSaveFolder.setBounds(10, 65, 140, 40);
		btnSaveFolder.setSize(140, 40);
		btnSaveFolder.setLocation(10, 65);
		
		m_txtSaveFolder = new JTextField(120);
		m_txtSaveFolder.setBounds(160, 65, 520, 40);
		m_txtSaveFolder.setSize(520, 40);
		m_txtSaveFolder.setLocation(160, 65);
		
		lblTopKeywordNum = new JLabel("Top Keyword Num:");
		lblTopKeywordNum.setBounds(10, 120, 120, 40);
		lblTopKeywordNum.setSize(120, 40);
		lblTopKeywordNum.setLocation(10, 120);

		m_txtTopKeywordNum = new JTextField(5);
		m_txtTopKeywordNum.setBounds(140, 120, 60, 40);
		m_txtTopKeywordNum.setSize(60, 40);
		m_txtTopKeywordNum.setLocation(140, 120);
			
		
		lblPlaceName = new JLabel("Place Name:");
		lblPlaceName.setBounds(10, 175, 80, 40);
		lblPlaceName.setSize(80, 40);
		lblPlaceName.setLocation(10, 175);

		m_txtPlaceName = new JTextField(30);
		m_txtPlaceName.setBounds(100, 175, 580, 40);
		m_txtPlaceName.setSize(580, 40);
		m_txtPlaceName.setLocation(100, 175);

		lblURL = new JLabel("URL:");
		lblURL.setBounds(10, 230, 30, 40);
		lblURL.setSize(30, 40);
		lblURL.setLocation(10, 230);

		m_txtURL = new JTextField(30);
		m_txtURL.setBounds(50, 230, 630, 40);
		m_txtURL.setSize(630, 40);
		m_txtURL.setLocation(50, 230);
		
		btnPlaceList = new JButton("Place List File ...");
		btnPlaceList.addActionListener(this);
		btnPlaceList.setBounds(10, 285, 140, 40);
		btnPlaceList.setSize(140, 40);
		btnPlaceList.setLocation(10, 285);
		
		m_txtPlaceListFile = new JTextField(120);
		m_txtPlaceListFile.setBounds(160, 285, 520, 40);
		m_txtPlaceListFile.setSize(520, 40);
		m_txtPlaceListFile.setLocation(160, 285);
		
		m_lblRunInfo = new JLabel("");
		m_lblRunInfo.setBounds(250, 330, 200, 20);
		m_lblRunInfo.setSize(200, 20);
		m_lblRunInfo.setLocation(250, 330);		
		//m_lblRunInfo.setOpaque(true);
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(this);

		btnRun.setBounds(10, 360, 120, 50);
		btnRun.setSize(120, 50);
		btnRun.setLocation(10, 360);
		
		btnOnePlace = new JButton("One Place");
		btnOnePlace.addActionListener(this);

		btnOnePlace.setBounds(145, 360, 120, 50);
		btnOnePlace.setSize(120, 50);
		btnOnePlace.setLocation(145, 360);
		
		
		btnPlaces = new JButton("Place List");
		btnPlaces.addActionListener(this);

		btnPlaces.setBounds(280, 360, 120, 50);
		btnPlaces.setSize(120, 50);
		btnPlaces.setLocation(280, 360);
		
		btnWeight = new JButton("Assign Weight");
		btnWeight.addActionListener(this);

		btnWeight.setBounds(415, 360, 120, 50);
		btnWeight.setSize(120, 50);
		btnWeight.setLocation(415, 360);
		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(this);

		btnExit.setBounds(550, 360, 120, 50);
		btnExit.setSize(120, 50);
		btnExit.setLocation(550, 360);
		
		pnlMain.add(lblGpsLat);
		pnlMain.add(m_txtGpsLat);
		pnlMain.add(lblGpsLong);
		pnlMain.add(m_txtGpsLong);
		
		pnlMain.add(lblRadius);
		pnlMain.add(m_txtRadius);
		
		pnlMain.add(btnSaveFolder);
		pnlMain.add(m_txtSaveFolder);

		pnlMain.add(lblTopKeywordNum);
		pnlMain.add(m_txtTopKeywordNum);
		
		pnlMain.add(lblPlaceName);
		pnlMain.add(m_txtPlaceName);
		pnlMain.add(lblURL);
		pnlMain.add(m_txtURL);
		pnlMain.add(btnPlaceList);
		pnlMain.add(m_txtPlaceListFile);
			
		pnlMain.add(btnOnePlace);
		pnlMain.add(btnPlaces);

		pnlMain.add(btnRun);
		pnlMain.add(btnWeight);
		pnlMain.add(btnExit);
		
		pnlMain.add(m_lblRunInfo);
		pnlMain.validate();
		
		pack();
		
		setSize(700, 450);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		nScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		nScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation(nScreenWidth / 2 - 300, nScreenHeight / 2 - 175);
		
		//m_txtAreaInfo.setText("Champaign IL");
		m_txtGpsLat.setText("40.142192");   //Green street: 40.11035,-88.2298      //Market Place:  40.140842,-88.24363   //Best Buy: 40.142192, -88.259954
		m_txtGpsLong.setText("-88.259954");
		m_txtSaveFolder.setText(sDefaultSaveFolder);

		m_txtRadius.setText("" + Utility.DEFAULT_GOOGLE_PALCE_SEARCH_RADIUS);
		m_txtTopKeywordNum.setText("" + Utility.DEFAULT_TOP_KEYWORD_CNT);
	}
	
	
	public void actionPerformed(ActionEvent ae) {
		int nRetVal;
		String sAreaInfo = "";
		String sGpsCoordinates = "";
		String sGpsLat = "";
		String sGpsLong = "";
		
		int nPlaceSearchType = 0;
		String sRadius;
		String sTopType, sTopNum;
		
		if (ae.getSource() == btnRun) {
			if (m_blnRunning == true) return;
			
			m_sSaveFolder = m_txtSaveFolder.getText();
			if (m_sSaveFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please select folder to store!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			sRadius = m_txtRadius.getText();
			if (sRadius.length() == 0) {
				m_nRadius = Utility.DEFAULT_GOOGLE_PALCE_SEARCH_RADIUS;
			} else {
				m_nRadius = Integer.valueOf(sRadius).intValue();
			}
						
			sTopNum = m_txtTopKeywordNum.getText();
			if (sTopNum.length() == 0) {
				m_nTopNum = Utility.DEFAULT_TOP_KEYWORD_CNT;
			} else {
				m_nTopNum = Integer.valueOf(sTopNum).intValue();
			}
									
			sGpsLat = m_txtGpsLat.getText();
			sGpsLong = m_txtGpsLong.getText();

			if (sGpsLat.length() == 0 || sGpsLong.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set GPS coordinates!", "GPS information", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			sGpsCoordinates = sGpsLat + "," + sGpsLong;
			
//			if (sGpsCoordinates.length() > 0)  {
//				nPlaceSearchType = 1;
//			} else {
//				nPlaceSearchType = 2;
//			}
			
			nPlaceSearchType = 1;
			

			Utility.createFolderContent(m_sSaveFolder, sGpsCoordinates, m_nTopNum);
			
			Date dtStartDate, dtEndDate;
			dtStartDate = new Date();
			SimpleDateFormat spdRecordTime = new SimpleDateFormat("yyMMdd-HH:mm:ss");
			String sStartTime, sEndTime;
			sStartTime = spdRecordTime.format(dtStartDate);

			GoogleSearch myGoogleSearch = new GoogleSearch();
			m_blnRunning = true;
			m_lblRunInfo.setText("Processing...");
			pnlMain.revalidate();
			this.repaint();

			
//			myGoogleSearch.GooglePlaceSearch(nPlaceSearchType, sAreaInfo, sGpsCoordinates, m_sPlaceFile, m_sPlaceGoogleTitleURLFile);
//			myGoogleSearch.GooglePlaceSearch(nPlaceSearchType, sAreaInfo, sGpsCoordinates, m_nRadius, m_sPlaceFile, m_sPlaceGoogleTitleURLFile, m_sTopFrequentWordFilePrefix, m_nTopType, m_nTopNum);
//Previous version			myGoogleSearch.GooglePlaceSearch(nPlaceSearchType, sAreaInfo, sGpsCoordinates, m_nRadius, m_sPlaceFile, m_sPlaceGoogleTitleURLFile, m_sTopFrequentWordFilePrefix, m_nTopType, m_nTopNum, m_sWebImgFolder);
			myGoogleSearch.GooglePlaceSearchByGps(sGpsCoordinates, m_nRadius, m_sSaveFolder, m_nTopNum);

			m_blnRunning = false;
			m_lblRunInfo.setText("");
			//m_lblRunInfo.invalidate();
			
			dtEndDate = new Date();
			sEndTime = spdRecordTime.format(dtEndDate);
			
			System.out.println("=================Run Started at: " + sStartTime + "=======================");
			System.out.println("===================Run Ended at: " + sEndTime + "=======================");
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
		} 
				
		if (ae.getSource() == btnSaveFolder) {
			m_fcSaveFolder = new JFileChooser();
			m_fcSaveFolder.setCurrentDirectory(new File("E://UIUC//"));
			m_fcSaveFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSaveFolder.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSaveFolder.setText(m_fcSaveFolder.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}
	
		
		if (ae.getSource() == btnPlaceList) {
			m_fcPlaceList = new JFileChooser();
			m_fcPlaceList.setCurrentDirectory(new File("E://UIUC//"));
			m_fcPlaceList.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcPlaceList.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtPlaceListFile.setText(m_fcPlaceList.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}
		
		if (ae.getSource() == btnOnePlace) {
			if (m_blnRunning == true) return;
			
			sGpsLat = m_txtGpsLat.getText();
			sGpsLong = m_txtGpsLong.getText();

			if (sGpsLat.length() == 0 || sGpsLong.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set GPS coordinates!", "GPS information", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			sGpsCoordinates = sGpsLat + "," + sGpsLong;
			
			m_sSaveFolder = m_txtSaveFolder.getText();
			if (m_sSaveFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please select folder to store!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
					
			sTopNum = m_txtTopKeywordNum.getText();
			if (sTopNum.length() == 0) {
				m_nTopNum = Utility.DEFAULT_TOP_KEYWORD_CNT;
			} else {
				m_nTopNum = Integer.valueOf(sTopNum).intValue();
			}
			
			String sPlaceName = m_txtPlaceName.getText();
			if (sPlaceName.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please input Place Name!", "Input Place Name", JOptionPane.WARNING_MESSAGE);
				return;
			}
						
			String sURL = m_txtURL.getText();
			if (sURL.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please input URL!", "Input URL", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			Utility.createFolderContent(m_sSaveFolder, sGpsCoordinates, m_nTopNum);
			
			Date dtStartDate, dtEndDate;
			dtStartDate = new Date();
			SimpleDateFormat spdRecordTime = new SimpleDateFormat("yyMMdd-HH:mm:ss");
			String sStartTime, sEndTime;
			sStartTime = spdRecordTime.format(dtStartDate);

			GoogleSearch myGoogleSearch = new GoogleSearch();
			m_blnRunning = true;
			m_lblRunInfo.setText("Processing...");
			pnlMain.revalidate();
			this.repaint();
			
			myGoogleSearch.GooglePlaceSearchByPlace(sGpsCoordinates, sPlaceName, sURL, m_sSaveFolder, m_nTopNum);

			m_blnRunning = false;
			m_lblRunInfo.setText("");
			
			dtEndDate = new Date();
			sEndTime = spdRecordTime.format(dtEndDate);
			
			System.out.println("=================Run Started at: " + sStartTime + "=======================");
			System.out.println("===================Run Ended at: " + sEndTime + "=======================");	
		
		}
		
		if (ae.getSource() == btnPlaces) {
			if (m_blnRunning == true) return;
			
			sGpsLat = m_txtGpsLat.getText();
			sGpsLong = m_txtGpsLong.getText();

			if (sGpsLat.length() == 0 || sGpsLong.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set GPS coordinates!", "GPS information", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			sGpsCoordinates = sGpsLat + "," + sGpsLong;
			
			m_sSaveFolder = m_txtSaveFolder.getText();
			if (m_sSaveFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please select folder to store!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
					
			sTopNum = m_txtTopKeywordNum.getText();
			if (sTopNum.length() == 0) {
				m_nTopNum = Utility.DEFAULT_TOP_KEYWORD_CNT;
			} else {
				m_nTopNum = Integer.valueOf(sTopNum).intValue();
			}
			
						
			String sPlaceListFile = m_txtPlaceListFile.getText();
			if (sPlaceListFile.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please input Place List File!", "Input Place List File", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			Utility.createFolderContent(m_sSaveFolder, sGpsCoordinates, m_nTopNum);
			
			Date dtStartDate, dtEndDate;
			dtStartDate = new Date();
			SimpleDateFormat spdRecordTime = new SimpleDateFormat("yyMMdd-HH:mm:ss");
			String sStartTime, sEndTime;
			sStartTime = spdRecordTime.format(dtStartDate);

			GoogleSearch myGoogleSearch = new GoogleSearch();
			m_blnRunning = true;
			m_lblRunInfo.setText("Processing...");
			pnlMain.revalidate();
			this.repaint();
			
			myGoogleSearch.GooglePlaceSearchByPlaceListFile(sGpsCoordinates, sPlaceListFile, m_sSaveFolder, m_nTopNum);

			m_blnRunning = false;
			m_lblRunInfo.setText("");
			
			dtEndDate = new Date();
			sEndTime = spdRecordTime.format(dtEndDate);
			
			System.out.println("=================Run Started at: " + sStartTime + "=======================");
			System.out.println("===================Run Ended at: " + sEndTime + "=======================");	

		}
	
		
		if (ae.getSource() == btnWeight) {
			m_sSaveFolder = m_txtSaveFolder.getText();
			new WeightAssignmentWin(m_sSaveFolder);
		}
				
	
		
		if (ae.getSource() == btnExit) {
			//System.exit(0);
			this.dispose();
		}
	}
	
	
//	public static void main(String[] args) {
//		new CandidatePlaceAcquisitionMain();
//	}
	
}
	
	
