package com.autolabel;

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
import java.util.List;
 
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javax.swing.JOptionPane;

public class CrowdsourceWin extends JFrame implements ActionListener {
	JPanel pnlMain;
	JButton btnRun, btnRunIndexSet, btnFrameStat, btnExit;
	
	JButton btnSelectDataFolder;
	JFileChooser m_fcSelectDataFolder;
	JTextField m_txtSelectDataFolder;
	
	JLabel lblFinalTopAPNum;
	JTextField m_txtFinalTopAPNum;

	JLabel lblFrameNum;
	JTextField m_txtFrameNum;

	JLabel lblGpsLat,lblGpsLong;
	JTextField m_txtGpsLat,m_txtGpsLong;
	
	String m_sTopFinalAPNum;
	int m_nTopFinalAPNum;
	
	JLabel lblIndexSetNum;
	JTextField m_txtIndexSetNum;
	
	JButton btnSelectFrameCountFolder;
	JFileChooser m_fcSelectFrameCountFolder;
	JTextField m_txtSelectFrameCountFolder;

	int nScreenWidth, nScreenHeight;
	
	boolean m_blnRunning = false;
	String m_sDataFolder = "";
	String m_sFrameStatFolder = "";
	String m_sApCnt = "";
	int m_nApCnt;
	String m_sFrameCnt = "";
	int m_nFrameCnt;
	
	String sDefaultOCRFolder = "E:\\UIUC\\SamsungDemo\\Crowdsource";
	String sDefaultFrameStatFolder = "E:\\UIUC\\SamsungDemo\\FrameStat";
	
	
	public CrowdsourceWin() {
		// TODO Auto-generated constructor stub
		super("Crowdsource Data Processor");
		pnlMain = new JPanel();
		pnlMain.setLayout(null);
		this.getContentPane().add(pnlMain);
				
		btnSelectDataFolder = new JButton("Data Folder ...");
		btnSelectDataFolder.addActionListener(this);
		btnSelectDataFolder.setBounds(10, 30, 140, 50);
		btnSelectDataFolder.setSize(140, 50);
		btnSelectDataFolder.setLocation(10, 30);
		
		m_txtSelectDataFolder = new JTextField(120);
		m_txtSelectDataFolder.setBounds(160, 30, 520, 50);
		m_txtSelectDataFolder.setSize(520, 50);
		m_txtSelectDataFolder.setLocation(160, 30);
		
		lblFinalTopAPNum = new JLabel("Number of Top APs:");
		lblFinalTopAPNum.setBounds(10, 120, 120, 50);
		lblFinalTopAPNum.setSize(120, 50);
		lblFinalTopAPNum.setLocation(10, 120);
		
		m_txtFinalTopAPNum = new JTextField(80);
		m_txtFinalTopAPNum.setBounds(140, 120, 140, 50);
		m_txtFinalTopAPNum.setSize(140, 50);
		m_txtFinalTopAPNum.setLocation(140, 120);
		
		lblFrameNum = new JLabel("#Frame:");
		lblFrameNum.setBounds(320, 120, 60, 50);
		lblFrameNum.setSize(60, 50);
		lblFrameNum.setLocation(320, 120);
		
		m_txtFrameNum = new JTextField(80);
		m_txtFrameNum.setBounds(390, 120, 140, 50);
		m_txtFrameNum.setSize(140, 50);
		m_txtFrameNum.setLocation(390, 120);
		
		m_txtFrameNum.setText("" + Utility.NO_FRAME_SAMPLE);
		
		lblGpsLat = new JLabel("Default GPS  Lat:");
		lblGpsLat.setBounds(10, 200, 100, 50);
		lblGpsLat.setSize(100, 50);
		lblGpsLat.setLocation(10, 200);

		m_txtGpsLat = new JTextField(30);
		m_txtGpsLat.setBounds(120, 200, 120, 50);
		m_txtGpsLat.setSize(120, 50);
		m_txtGpsLat.setLocation(120, 200);

		lblGpsLong = new JLabel("Long:");
		lblGpsLong.setBounds(260, 200, 40, 50);
		lblGpsLong.setSize(40, 50);
		lblGpsLong.setLocation(260, 200);

		m_txtGpsLong = new JTextField(30);
		m_txtGpsLong.setBounds(300, 200, 120, 50);
		m_txtGpsLong.setSize(120, 50);
		m_txtGpsLong.setLocation(300, 200);
		

		lblIndexSetNum = new JLabel("# Index Set:");
		lblIndexSetNum.setBounds(440, 200, 70, 50);
		lblIndexSetNum.setSize(70, 50);
		lblIndexSetNum.setLocation(440, 200);

		m_txtIndexSetNum = new JTextField(30);
		m_txtIndexSetNum.setBounds(520, 200, 50, 50);
		m_txtIndexSetNum.setSize(50, 50);
		m_txtIndexSetNum.setLocation(520, 200);
		
		
		btnSelectFrameCountFolder = new JButton("Frame Stat Folder ...");
		btnSelectFrameCountFolder.addActionListener(this);
		btnSelectFrameCountFolder.setBounds(10, 280, 160, 50);
		btnSelectFrameCountFolder.setSize(160, 50);
		btnSelectFrameCountFolder.setLocation(10, 280);
		
		m_txtSelectFrameCountFolder = new JTextField(120);
		m_txtSelectFrameCountFolder.setBounds(180, 280, 500, 50);
		m_txtSelectFrameCountFolder.setSize(500, 50);
		m_txtSelectFrameCountFolder.setLocation(180, 280);
		
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(this);

		btnRun.setBounds(20, 350, 150, 50);
		btnRun.setSize(150, 50);
		btnRun.setLocation(20, 350);
		
		btnRunIndexSet = new JButton("Run Idx Set");
		btnRunIndexSet.addActionListener(this);

		btnRunIndexSet.setBounds(190, 350, 150, 50);
		btnRunIndexSet.setSize(150, 50);
		btnRunIndexSet.setLocation(190, 350);
		
		
		btnFrameStat = new JButton("Frame Stat");
		btnFrameStat.addActionListener(this);

		btnFrameStat.setBounds(360, 350, 150, 50);
		btnFrameStat.setSize(150, 50);
		btnFrameStat.setLocation(360, 350);

		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(this);

		btnExit.setBounds(530, 350, 150, 50);
		btnExit.setSize(150, 50);
		btnExit.setLocation(530, 350);
		
		pnlMain.add(btnSelectDataFolder);
		pnlMain.add(m_txtSelectDataFolder);
		
		pnlMain.add(lblFinalTopAPNum);
		pnlMain.add(m_txtFinalTopAPNum);

		pnlMain.add(lblFrameNum);
		pnlMain.add(m_txtFrameNum);
				
		pnlMain.add(lblGpsLat);
		pnlMain.add(m_txtGpsLat);
		pnlMain.add(lblGpsLong);
		pnlMain.add(m_txtGpsLong);
		
		pnlMain.add(lblIndexSetNum);
		pnlMain.add(m_txtIndexSetNum);
		
		pnlMain.add(btnSelectFrameCountFolder);
		pnlMain.add(m_txtSelectFrameCountFolder);
		
		pnlMain.add(btnRun);
		pnlMain.add(btnRunIndexSet);
		pnlMain.add(btnFrameStat);		
		pnlMain.add(btnExit);
		
		setSize(700, 450);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		nScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		nScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation(nScreenWidth / 2 - 300, nScreenHeight / 2 - 175);
		
		m_txtGpsLat.setText("40.142192");   //Green street: 40.11035,-88.2298      //Market Place:  40.140842,-88.24363;    40.141843, -88.242750   //Best Buy: 40.142192, -88.259954
		m_txtGpsLong.setText("-88.259954");
		m_txtSelectDataFolder.setText(sDefaultOCRFolder);
		m_txtSelectFrameCountFolder.setText(sDefaultFrameStatFolder);
		
		m_txtFinalTopAPNum.setText("" + Utility.DEFAULT_AP_MAC_CNT);
		
	}
	
	
	public void actionPerformed(ActionEvent ae) {
		int nRetVal;
		String sGpsLat = "";
		String sGpsLong = "";
		String sGpsCoordinates = "";
		
		if (ae.getSource() == btnSelectDataFolder) {
			m_fcSelectDataFolder = new JFileChooser();
			m_fcSelectDataFolder.setCurrentDirectory(new File("E://UIUC//"));
			m_fcSelectDataFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSelectDataFolder.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSelectDataFolder.setText(m_fcSelectDataFolder.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}
		
		
		if (ae.getSource() == btnRun) {
			m_sDataFolder = m_txtSelectDataFolder.getText();
			if (m_sDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please select folder to store!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sApCnt = m_txtFinalTopAPNum.getText();
			if (m_sApCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set AP Number!", "Set AP Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sFrameCnt = m_txtFrameNum.getText();
			if (m_sFrameCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set #Frame!", "Set Frane Count", JOptionPane.WARNING_MESSAGE);
				return;
			}
						
			sGpsLat = m_txtGpsLat.getText();
			sGpsLong = m_txtGpsLong.getText();

			if (sGpsLat.length() == 0 || sGpsLong.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set default GPS coordinates!", "GPS information", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			
			
			sGpsCoordinates = sGpsLat + "," + sGpsLong;
			
			m_nApCnt = Integer.valueOf(m_sApCnt).intValue();
			
						
			
			//m_nFrameCnt = Integer.valueOf(m_sFrameCnt).intValue();
			
//			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, Utility.NO_FRAME_SAMPLE};
			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, Utility.NO_FRAME_SAMPLE};

//			int nArrFrameCnt[] = {5, 10, 15, 20, 25, Utility.NO_FRAME_SAMPLE};
//			int nArrFrameCnt[] = {35, 40, 45, 50};

			
			for (int i=0; i<nArrFrameCnt.length; i++) {
			
				CrowdsourcedDataProcessor locCrowdsourcedDataProcessor = new CrowdsourcedDataProcessor(m_sDataFolder, m_nApCnt, sGpsCoordinates, nArrFrameCnt[i]);
			
				locCrowdsourcedDataProcessor.MergeCrowdsourcedData();
			}
			
			System.out.println("Done!!!!!!!!!!!!!!!!");
			
			return;
		} 
		

		if (ae.getSource() == btnRunIndexSet) {
			
			m_sDataFolder = m_txtSelectDataFolder.getText();
			if (m_sDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please select folder to store!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sApCnt = m_txtFinalTopAPNum.getText();
			if (m_sApCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set AP Number!", "Set AP Number", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sFrameCnt = m_txtFrameNum.getText();
			if (m_sFrameCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set #Frame!", "Set Frane Count", JOptionPane.WARNING_MESSAGE);
				return;
			}
						
			sGpsLat = m_txtGpsLat.getText();
			sGpsLong = m_txtGpsLong.getText();

			if (sGpsLat.length() == 0 || sGpsLong.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set default GPS coordinates!", "GPS information", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			
			String sIndexSetNum = m_txtIndexSetNum.getText().trim();
			if (sIndexSetNum.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set Index Set Number!", "Index Set Number", JOptionPane.WARNING_MESSAGE);
				return;
			}

			
			
			sGpsCoordinates = sGpsLat + "," + sGpsLong;
			
			m_nApCnt = Integer.valueOf(m_sApCnt).intValue();
			
			System.out.println("Running...........");			
			
			//m_nFrameCnt = Integer.valueOf(m_sFrameCnt).intValue();
			
//			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, Utility.NO_FRAME_SAMPLE};
			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, Utility.NO_FRAME_SAMPLE};
//			int nArrFrameCnt[] = {35, 40, 45, 50};
//			int nArrFrameCnt[] = {5, 10, 15, 20, 25};

			
			for (int i=0; i<nArrFrameCnt.length; i++) {
			
				CrowdsourcedDataProcessor locCrowdsourcedDataProcessor = new CrowdsourcedDataProcessor(m_sDataFolder, m_nApCnt, sGpsCoordinates, nArrFrameCnt[i], Integer.valueOf(sIndexSetNum).intValue());
			
				locCrowdsourcedDataProcessor.MergeCrowdsourcedData_MultipleIndexSet();
			}
			
			System.out.println("Done!!!!!!!!!!!!!!!!");
			
			return;
		} 
		
		
		
		if (ae.getSource() == btnFrameStat) {
			m_sDataFolder = m_txtSelectDataFolder.getText().trim();
			if (m_sDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please select OCR folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sFrameStatFolder = m_txtSelectFrameCountFolder.getText().trim();
			if (m_sFrameStatFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please select folder to store!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			System.out.println("Running...........");
			
			String sIndexSetNum = m_txtIndexSetNum.getText().trim();
			if (sIndexSetNum.length() == 0) {
				FrameStat.calculateFrameStat(m_sDataFolder, m_sFrameStatFolder);
			} else {
				FrameStat.calculateFrameStat_MultiIndexSet(m_sDataFolder, m_sFrameStatFolder, Integer.valueOf(sIndexSetNum).intValue());
			}
			
		
			//FrameStat.calculateFrameStat_FromExisting(m_sDataFolder, m_sFrameStatFolder);
			
			System.out.println("Done!!!!!!!!!!!!!!!!");
			
			return;
		} 
		
		
		
		if (ae.getSource() == btnExit) {
			this.dispose();
		}
	}
	
}
	
	
