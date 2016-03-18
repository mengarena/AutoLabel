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
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javax.swing.JOptionPane;

public class PlaceApManager extends JFrame implements ActionListener {
	JPanel pnlMain;
	JButton btnRun, btnRunMultipleIndex, btnSubset, btnSubSetMultipleIndex, btnExit;
	
	JButton btnSelectWebDataFolder;
	JFileChooser m_fcSelectWebDataFolder;
	JTextField m_txtSelectWebDataFolder;

	JButton btnSelectCrowdsourcedDataFolder;
	JFileChooser m_fcSelectCrowdsourcedDataFolder;
	JTextField m_txtSelectCrowdsourcedDataFolder;
	
	JButton btnSelectPlaceApDbFileDataFolder;
	JFileChooser m_fcSelectPlaceApDbFileDataFolder;
	JTextField m_txtSelectPlaceApDbFileDataFolder;
	
	JCheckBox m_chkUsePlaceName;
	
	JLabel lblAPNum;
	JTextField m_txtAPNum;

	JLabel lblFrameNum;
	JTextField m_txtFrameNum;
	
	JLabel lblSubsetPlaceCnt;
	JTextField m_txtSubsetPlaceCnt;
	
	JLabel lblSubsetCnt;
	JTextField m_txtSubsetCnt;	
	
	String m_sAPNum;
	int m_nAPNum;
	
	JLabel lblIndexSetNum;
	JTextField m_txtIndexSetNum;
	
	String m_sFrameCnt = "";
	int m_nFrameCnt;
	
	String m_sSubsetPlaceCnt = "";
	int m_nSubsetPlaceCnt;
	
	String m_sSubsetCnt = "";
	int m_nSubsetCnt;
	
	JLabel  m_lblRunInfo;
	
	int nScreenWidth, nScreenHeight;
	
	boolean m_blnRunning = false;
	String m_sWebDataFile = "";
	String m_sCrowdsourcedDataFolder = "";
	String m_sPlaceApDbFileDataFolder = "";
	
	String sDefaultWebDataFile = "E:\\UIUC\\SamsungDemo\\Web\\PlaceKeywordList_9999.csv";
	String sDefaultCrowdsourcedDataFolder = "E:\\UIUC\\SamsungDemo\\Crowdsource";
	String sDefaultSaveResult = "E:\\UIUC\\SamsungDemo\\Matching_Similarity\\KeywordMatchingRet";
	
		
	public PlaceApManager() {
		// TODO Auto-generated constructor stub
		super("Keyword Matcher");
		pnlMain = new JPanel();
		pnlMain.setLayout(null);
		this.getContentPane().add(pnlMain);
				
		btnSelectWebDataFolder = new JButton("Web Data File...");
		btnSelectWebDataFolder.addActionListener(this);
		btnSelectWebDataFolder.setBounds(10, 20, 160, 40);
		btnSelectWebDataFolder.setSize(160, 40);
		btnSelectWebDataFolder.setLocation(10, 20);
		
		m_txtSelectWebDataFolder = new JTextField(120);
		m_txtSelectWebDataFolder.setBounds(180, 20, 500, 40);
		m_txtSelectWebDataFolder.setSize(500, 40);
		m_txtSelectWebDataFolder.setLocation(180, 20);

		
		btnSelectCrowdsourcedDataFolder = new JButton("Crowdsourced Data Folder...");
		btnSelectCrowdsourcedDataFolder.addActionListener(this);
		btnSelectCrowdsourcedDataFolder.setBounds(10, 80, 200, 40);
		btnSelectCrowdsourcedDataFolder.setSize(200, 40);
		btnSelectCrowdsourcedDataFolder.setLocation(10, 80);
		
		m_txtSelectCrowdsourcedDataFolder = new JTextField(120);
		m_txtSelectCrowdsourcedDataFolder.setBounds(220, 80, 460, 40);
		m_txtSelectCrowdsourcedDataFolder.setSize(460, 40);
		m_txtSelectCrowdsourcedDataFolder.setLocation(220, 80);
		

		btnSelectPlaceApDbFileDataFolder = new JButton("Save Result ...");
		btnSelectPlaceApDbFileDataFolder.addActionListener(this);
		btnSelectPlaceApDbFileDataFolder.setBounds(10, 140, 160, 40);
		btnSelectPlaceApDbFileDataFolder.setSize(160, 40);
		btnSelectPlaceApDbFileDataFolder.setLocation(10, 140);
		
		m_txtSelectPlaceApDbFileDataFolder = new JTextField(120);
		m_txtSelectPlaceApDbFileDataFolder.setBounds(180, 140, 500, 40);
		m_txtSelectPlaceApDbFileDataFolder.setSize(500, 40);
		m_txtSelectPlaceApDbFileDataFolder.setLocation(180, 140);

		lblAPNum = new JLabel("Number of AP MACs:");
		lblAPNum.setBounds(10, 200, 140, 40);
		lblAPNum.setSize(140, 40);
		lblAPNum.setLocation(10, 200);
		
		m_txtAPNum = new JTextField(80);
		m_txtAPNum.setBounds(150, 200, 80, 40);
		m_txtAPNum.setSize(80, 40);
		m_txtAPNum.setLocation(150, 200);
		
		lblFrameNum = new JLabel("#Frame:");
		lblFrameNum.setBounds(260, 200, 60, 40);
		lblFrameNum.setSize(60, 40);
		lblFrameNum.setLocation(260, 200);
		
		m_txtFrameNum = new JTextField(80);
		m_txtFrameNum.setBounds(330, 200, 80, 40);
		m_txtFrameNum.setSize(80, 40);
		m_txtFrameNum.setLocation(330, 200);
		
		m_txtFrameNum.setText("" + Utility.NO_FRAME_SAMPLE);  //Utility.NO_FRAME_SAMPLE
		
		m_chkUsePlaceName = new JCheckBox("Use PlaceName");
		m_chkUsePlaceName.setBounds(480, 200, 140, 40);
		m_chkUsePlaceName.setSize(140, 40);
		m_chkUsePlaceName.setLocation(480, 200);
		
		
		lblSubsetPlaceCnt = new JLabel("#Places in Subset:");
		lblSubsetPlaceCnt.setBounds(10, 260, 140, 40);
		lblSubsetPlaceCnt.setSize(140, 40);
		lblSubsetPlaceCnt.setLocation(10, 260);
		
		m_txtSubsetPlaceCnt = new JTextField(80);
		m_txtSubsetPlaceCnt.setBounds(150, 260, 80, 40);
		m_txtSubsetPlaceCnt.setSize(80, 40);
		m_txtSubsetPlaceCnt.setLocation(150, 260);
		m_txtSubsetPlaceCnt.setText("10");
		
		lblSubsetCnt = new JLabel("#Subset:");
		lblSubsetCnt.setBounds(260, 260, 60, 40);
		lblSubsetCnt.setSize(60, 40);
		lblSubsetCnt.setLocation(260, 260);
		
		m_txtSubsetCnt = new JTextField(80);
		m_txtSubsetCnt.setBounds(330, 260, 80, 40);
		m_txtSubsetCnt.setSize(80, 40);
		m_txtSubsetCnt.setLocation(330, 260);
		m_txtSubsetCnt.setText("100"); 
		
		
		lblIndexSetNum = new JLabel("# Index Set:");
		lblIndexSetNum.setBounds(480, 260, 70, 40);
		lblIndexSetNum.setSize(70, 40);
		lblIndexSetNum.setLocation(480, 260);

		m_txtIndexSetNum = new JTextField(30);
		m_txtIndexSetNum.setBounds(560, 260, 50, 40);
		m_txtIndexSetNum.setSize(50, 40);
		m_txtIndexSetNum.setLocation(560, 260);
		
		m_lblRunInfo = new JLabel("");
		m_lblRunInfo.setBounds(250, 315, 200, 30);
		m_lblRunInfo.setSize(200, 30);
		m_lblRunInfo.setLocation(250, 315);
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(this);

		btnRun.setBounds(10, 360, 100, 50);
		btnRun.setSize(100, 50);
		btnRun.setLocation(10, 360);

		btnRunMultipleIndex = new JButton("Run MultiIdx");
		btnRunMultipleIndex.addActionListener(this);

		btnRunMultipleIndex.setBounds(130, 360, 120, 50);
		btnRunMultipleIndex.setSize(120, 50);
		btnRunMultipleIndex.setLocation(130, 360);
		
		btnSubset = new JButton("Subset");
		btnSubset.addActionListener(this);

		btnSubset.setBounds(270, 360, 100, 50);
		btnSubset.setSize(100, 50);
		btnSubset.setLocation(270, 360);

		btnSubSetMultipleIndex = new JButton("MultiIdx Subset");
		btnSubSetMultipleIndex.addActionListener(this);

		btnSubSetMultipleIndex.setBounds(390, 360, 150, 50);
		btnSubSetMultipleIndex.setSize(150, 50);
		btnSubSetMultipleIndex.setLocation(390, 360);
		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(this);

		btnExit.setBounds(560, 360, 100, 50);
		btnExit.setSize(100, 50);
		btnExit.setLocation(560, 360);
		

		pnlMain.add(btnSelectWebDataFolder);
		pnlMain.add(m_txtSelectWebDataFolder);
		
		pnlMain.add(btnSelectCrowdsourcedDataFolder);
		pnlMain.add(m_txtSelectCrowdsourcedDataFolder);

		
		pnlMain.add(btnSelectPlaceApDbFileDataFolder);
		pnlMain.add(m_txtSelectPlaceApDbFileDataFolder);

		pnlMain.add(lblAPNum);
		pnlMain.add(m_txtAPNum);
		
		pnlMain.add(lblFrameNum);
		pnlMain.add(m_txtFrameNum);
		
		pnlMain.add(m_chkUsePlaceName);
		
		pnlMain.add(lblSubsetPlaceCnt);
		pnlMain.add(m_txtSubsetPlaceCnt);
		pnlMain.add(lblSubsetCnt);
		pnlMain.add(m_txtSubsetCnt);
		
		pnlMain.add(lblIndexSetNum);
		pnlMain.add(m_txtIndexSetNum);
		
		pnlMain.add(m_lblRunInfo);
		
		pnlMain.add(btnRun);
		pnlMain.add(btnRunMultipleIndex);
		pnlMain.add(btnSubset);
		pnlMain.add(btnSubSetMultipleIndex);
		pnlMain.add(btnExit);
		
		pack();
		
		setSize(700, 450);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		nScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		nScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation(nScreenWidth / 2 - 300, nScreenHeight / 2 - 175);
		
		m_txtAPNum.setText(""+ Utility.DEFAULT_AP_MAC_CNT);
		
		m_txtSelectWebDataFolder.setText(sDefaultWebDataFile);
		m_txtSelectCrowdsourcedDataFolder.setText(sDefaultCrowdsourcedDataFolder);
		m_txtSelectPlaceApDbFileDataFolder.setText(sDefaultSaveResult);
		
	}
	
	
	public void actionPerformed(ActionEvent ae) {
		int nRetVal;
		
		if (ae.getSource() == btnSelectWebDataFolder) {
			m_fcSelectWebDataFolder = new JFileChooser();
			m_fcSelectWebDataFolder.setCurrentDirectory(new File("E://UIUC//Run//"));
			m_fcSelectWebDataFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSelectWebDataFolder.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSelectWebDataFolder.setText(m_fcSelectWebDataFolder.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}
		
		if (ae.getSource() == btnSelectCrowdsourcedDataFolder) {
			m_fcSelectCrowdsourcedDataFolder = new JFileChooser();
			m_fcSelectCrowdsourcedDataFolder.setCurrentDirectory(new File("E://UIUC//Run//"));
			m_fcSelectCrowdsourcedDataFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSelectCrowdsourcedDataFolder.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSelectCrowdsourcedDataFolder.setText(m_fcSelectCrowdsourcedDataFolder.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}

		if (ae.getSource() == btnSelectPlaceApDbFileDataFolder) {
			m_fcSelectPlaceApDbFileDataFolder = new JFileChooser();
			m_fcSelectPlaceApDbFileDataFolder.setCurrentDirectory(new File("E://UIUC//Run//"));
			m_fcSelectPlaceApDbFileDataFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSelectPlaceApDbFileDataFolder.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSelectPlaceApDbFileDataFolder.setText(m_fcSelectPlaceApDbFileDataFolder.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}
		
		if (ae.getSource() == btnRun) {
			m_sWebDataFile = m_txtSelectWebDataFolder.getText();
			if (m_sWebDataFile.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Web Data Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_sCrowdsourcedDataFolder = m_txtSelectCrowdsourcedDataFolder.getText();
			if (m_sCrowdsourcedDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Crowdsourced Data Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_sPlaceApDbFileDataFolder = m_txtSelectPlaceApDbFileDataFolder.getText();
			if (m_sPlaceApDbFileDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Folder to Store Place AP Db Files!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sAPNum = m_txtAPNum.getText();
			if (m_sAPNum.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Set AP MAC Number!", "Set AP MAC Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_nAPNum = Integer.valueOf(m_sAPNum).intValue();
			
			
			m_sFrameCnt = m_txtFrameNum.getText();
			if (m_sFrameCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set #Frame!", "Set Frane Count", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_nFrameCnt = Integer.valueOf(m_sFrameCnt).intValue();

			System.out.println("[PlaceApManager DEBUG 1] Start Generating GPS-AP-Place Database file...");
			
			m_lblRunInfo.setText("Processing......");
			m_lblRunInfo.setVisible(true);
			m_lblRunInfo.invalidate();
			
			if (m_chkUsePlaceName.isSelected()) {
				Utility.MATCH_WITH_PLACENAME = true;
			} else {
				Utility.MATCH_WITH_PLACENAME = false;
			}
			
//			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, Utility.NO_FRAME_SAMPLE};
//			int nArrFrameCnt[] = {10, 20, 30, Utility.NO_FRAME_SAMPLE};
//			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, Utility.NO_FRAME_SAMPLE};
			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, Utility.NO_FRAME_SAMPLE};
			
			for (int i=0; i<nArrFrameCnt.length; i++) {

				DatabaseDataGenerator locDatabaseDataGenerator = new DatabaseDataGenerator(m_sWebDataFile, m_sCrowdsourcedDataFolder, m_sPlaceApDbFileDataFolder, m_nAPNum, nArrFrameCnt[i]);
			
				//Generate GPS-AP-Place/Store Name Database file
				locDatabaseDataGenerator.GenerateDatabaseData();
			}

			System.out.println("[PlaceApManager DEBUG 2] Finished Generating GPS-AP-Place Database file...");
			
			m_lblRunInfo.setText("");
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
			
			return;
		} 
		
		
		
		if (ae.getSource() == btnRunMultipleIndex) {
			m_sWebDataFile = m_txtSelectWebDataFolder.getText();
			if (m_sWebDataFile.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Web Data Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_sCrowdsourcedDataFolder = m_txtSelectCrowdsourcedDataFolder.getText();
			if (m_sCrowdsourcedDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Crowdsourced Data Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_sPlaceApDbFileDataFolder = m_txtSelectPlaceApDbFileDataFolder.getText();
			if (m_sPlaceApDbFileDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Folder to Store Place AP Db Files!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sAPNum = m_txtAPNum.getText();
			if (m_sAPNum.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Set AP MAC Number!", "Set AP MAC Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_nAPNum = Integer.valueOf(m_sAPNum).intValue();
			
			
			m_sFrameCnt = m_txtFrameNum.getText();
			if (m_sFrameCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set #Frame!", "Set Frane Count", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			String sIndexSetNum = m_txtIndexSetNum.getText().trim();
			if (sIndexSetNum.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set Index Set Number!", "Index Set Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			int nIndexSetNum = Integer.valueOf(sIndexSetNum).intValue();
			
			m_nFrameCnt = Integer.valueOf(m_sFrameCnt).intValue();

			System.out.println("[PlaceApManager DEBUG 1] Start Generating GPS-AP-Place Database file...");
			
			m_lblRunInfo.setText("Processing......");
			m_lblRunInfo.setVisible(true);
			m_lblRunInfo.invalidate();
			
			if (m_chkUsePlaceName.isSelected()) {
				Utility.MATCH_WITH_PLACENAME = true;
			} else {
				Utility.MATCH_WITH_PLACENAME = false;
			}
			
//			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, Utility.NO_FRAME_SAMPLE};
//			int nArrFrameCnt[] = {10, 20, 30, Utility.NO_FRAME_SAMPLE};
//			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, Utility.NO_FRAME_SAMPLE};
			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, Utility.NO_FRAME_SAMPLE};
//			int nArrFrameCnt[] = {5, 10, Utility.NO_FRAME_SAMPLE};
			
			int nSetIndexCnt = 1;
			
			for (int i=0; i<nArrFrameCnt.length; i++) {

				if (nArrFrameCnt[i] == Utility.NO_FRAME_SAMPLE) {
					nSetIndexCnt = 1;
				} else {
					nSetIndexCnt = nIndexSetNum;
				}
				
				DatabaseDataGenerator locDatabaseDataGenerator = new DatabaseDataGenerator(m_sWebDataFile, m_sCrowdsourcedDataFolder, m_sPlaceApDbFileDataFolder, m_nAPNum, nArrFrameCnt[i], nSetIndexCnt);
			
				//Generate GPS-AP-Place/Store Name Database file
				locDatabaseDataGenerator.GenerateDatabaseData_MultipleIndexSet();
			}

			System.out.println("[PlaceApManager DEBUG 2] Finished Generating GPS-AP-Place Database file...");
			
			m_lblRunInfo.setText("");
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
			
			return;
		} 
		
		
		
		if (ae.getSource() == btnSubset) {
			m_sWebDataFile = m_txtSelectWebDataFolder.getText();
			if (m_sWebDataFile.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Web Data Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_sCrowdsourcedDataFolder = m_txtSelectCrowdsourcedDataFolder.getText();
			if (m_sCrowdsourcedDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Crowdsourced Data Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_sPlaceApDbFileDataFolder = m_txtSelectPlaceApDbFileDataFolder.getText();
			if (m_sPlaceApDbFileDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Folder to Store Place AP Db Files!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sAPNum = m_txtAPNum.getText();
			if (m_sAPNum.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Set AP MAC Number!", "Set AP MAC Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_nAPNum = Integer.valueOf(m_sAPNum).intValue();
			
			
			m_sFrameCnt = m_txtFrameNum.getText();
			if (m_sFrameCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set #Frame!", "Set Frame Count", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_nFrameCnt = Integer.valueOf(m_sFrameCnt).intValue();
			
			m_sSubsetPlaceCnt = m_txtSubsetPlaceCnt.getText();
			if (m_sSubsetPlaceCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Set Subset Size!", "Set Subset Place Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_nSubsetPlaceCnt = Integer.valueOf(m_sSubsetPlaceCnt).intValue();

			m_sSubsetCnt = m_txtSubsetCnt.getText();
			if (m_sSubsetCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Set Number of Subsets!", "Set Subset Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_nSubsetCnt = Integer.valueOf(m_sSubsetCnt).intValue();
			
			System.out.println("[PlaceApManager DEBUG 1] Start Generating GPS-AP-Place Database file...");
			
			m_lblRunInfo.setText("Processing......");
			m_lblRunInfo.setVisible(true);
			
			if (m_chkUsePlaceName.isSelected()) {
				Utility.MATCH_WITH_PLACENAME = true;
			} else {
				Utility.MATCH_WITH_PLACENAME = false;
			}
			
//			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, Utility.NO_FRAME_SAMPLE};
			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, Utility.NO_FRAME_SAMPLE};

//			int nArrFrameCnt[] = {5, 10, Utility.NO_FRAME_SAMPLE};

			//			int nArrFrameCnt[] = {10, 20, 30, Utility.NO_FRAME_SAMPLE};
			List<Integer> lstnFrameCnt = new ArrayList<Integer>();
			
			for (int i=0; i<nArrFrameCnt.length; i++) {
				lstnFrameCnt.add(nArrFrameCnt[i]);
			}

			SubsetComparisonProcessor locSubsetComparisonProcessor = new SubsetComparisonProcessor(m_sWebDataFile, m_sCrowdsourcedDataFolder, m_sPlaceApDbFileDataFolder, m_nAPNum, lstnFrameCnt, m_nSubsetPlaceCnt, m_nSubsetCnt);
			
			//Generate GPS-AP-Place/Store Name Database file
			locSubsetComparisonProcessor.compareSubset();
			
			System.out.println("[PlaceApManager DEBUG 2] Finished Generating GPS-AP-Place Database file...");
			
			m_lblRunInfo.setText("");
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
			
			return;
		} 


		if (ae.getSource() == btnSubSetMultipleIndex) {
			m_sWebDataFile = m_txtSelectWebDataFolder.getText();
			if (m_sWebDataFile.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Web Data Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_sCrowdsourcedDataFolder = m_txtSelectCrowdsourcedDataFolder.getText();
			if (m_sCrowdsourcedDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Crowdsourced Data Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_sPlaceApDbFileDataFolder = m_txtSelectPlaceApDbFileDataFolder.getText();
			if (m_sPlaceApDbFileDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Folder to Store Place AP Db Files!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sAPNum = m_txtAPNum.getText();
			if (m_sAPNum.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Set AP MAC Number!", "Set AP MAC Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_nAPNum = Integer.valueOf(m_sAPNum).intValue();
			
			
			m_sFrameCnt = m_txtFrameNum.getText();
			if (m_sFrameCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set #Frame!", "Set Frane Count", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_nFrameCnt = Integer.valueOf(m_sFrameCnt).intValue();
			
			m_sSubsetPlaceCnt = m_txtSubsetPlaceCnt.getText();
			if (m_sSubsetPlaceCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Set Subset Size!", "Set AP MAC Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_nSubsetPlaceCnt = Integer.valueOf(m_sSubsetPlaceCnt).intValue();

			m_sSubsetCnt = m_txtSubsetCnt.getText();
			if (m_sSubsetCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Set Number of Subsets!", "Set AP MAC Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_nSubsetCnt = Integer.valueOf(m_sSubsetCnt).intValue();
			
			String sIndexSetNum = m_txtIndexSetNum.getText().trim();
			if (sIndexSetNum.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set Index Set Number!", "Index Set Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			int nIndexSetNum = Integer.valueOf(sIndexSetNum).intValue();
			
			
			System.out.println("[PlaceApManager DEBUG 1] Start Generating GPS-AP-Place Database file...");
			
			m_lblRunInfo.setText("Processing......");
			m_lblRunInfo.setVisible(true);
			
			if (m_chkUsePlaceName.isSelected()) {
				Utility.MATCH_WITH_PLACENAME = true;
			} else {
				Utility.MATCH_WITH_PLACENAME = false;
			}
			
//			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, Utility.NO_FRAME_SAMPLE};
			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, Utility.NO_FRAME_SAMPLE};

//			int nArrFrameCnt[] = {5, 10, Utility.NO_FRAME_SAMPLE};

			//			int nArrFrameCnt[] = {10, 20, 30, Utility.NO_FRAME_SAMPLE};
			List<Integer> lstnFrameCnt = new ArrayList<Integer>();
			
			for (int i=0; i<nArrFrameCnt.length; i++) {
				lstnFrameCnt.add(nArrFrameCnt[i]);
			}

			SubsetComparisonProcessor locSubsetComparisonProcessor = new SubsetComparisonProcessor(m_sWebDataFile, m_sCrowdsourcedDataFolder, m_sPlaceApDbFileDataFolder, m_nAPNum, lstnFrameCnt, m_nSubsetPlaceCnt, m_nSubsetCnt);
			
			//Generate GPS-AP-Place/Store Name Database file
			locSubsetComparisonProcessor.compareSubset_MultipleIndexSet(nIndexSetNum);
			
			System.out.println("[PlaceApManager DEBUG 2] Finished Generating GPS-AP-Place Database file...");
			
			m_lblRunInfo.setText("");
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
			
			return;
		} 
		
		
		if (ae.getSource() == btnExit) {
			this.dispose();
		}
	}
	
	
	
}
	
	
