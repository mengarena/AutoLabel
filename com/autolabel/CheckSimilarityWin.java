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

public class CheckSimilarityWin extends JFrame implements ActionListener {
	JPanel pnlMain;
	JButton btnStoreSimilarity, btnWebSimilarity, btnExit;
	
	JButton btnSelectSrcDataFileFolder;
	JFileChooser m_fcSelectSrcDataFileFolder;
	JTextField m_txtSelectSrcDataFileFolder;

	JButton btnSelectCandidateDataFileFolder;
	JFileChooser m_fcSelectCandidateDataFileFolder;
	JTextField m_txtSelectCandidateDataFileFolder;
	
	JButton btnSelectSimilarityFileDataFolder;
	JFileChooser m_fcSelectSimilarityFileDataFolder;
	JTextField m_txtSelectSimilarityFileDataFolder;
	
	JLabel  m_lblRunInfo;
	
	JLabel lblFrameNum;
	JTextField m_txtFrameNum;
	
	int nScreenWidth, nScreenHeight;
	
	boolean m_blnRunning = false;
	String m_sSrcDataFileFolder = "";
	String m_sCandidateDataFileFolder = "";
	String m_sSimilarityFileDataFolder = "";
	String m_sFrameCnt = "";
	int m_nFrameCnt;
	
//	String sSrcDataFileFolder = "E:\\UIUC\\SamsungDemo\\Web\\PlaceKeywordList_9999.csv";
//	String sCandidateDataFileFolder = "E:\\UIUC\\SamsungDemo\\Web\\PlaceKeywordList_9999.csv";

	String sSrcDataFileFolder = "E:\\UIUC\\SamsungDemo\\Crowdsource";
	String sCandidateDataFileFolder = "E:\\UIUC\\SamsungDemo\\Crowdsource";
	
	String sSaveResult = "E:\\UIUC\\SamsungDemo\\Matching_Similarity\\Similarity";
	
	public CheckSimilarityWin() {
		// TODO Auto-generated constructor stub
		super("Check Similarity");
		pnlMain = new JPanel();
		pnlMain.setLayout(null);
		this.getContentPane().add(pnlMain);
				
		btnSelectSrcDataFileFolder = new JButton("Src Data File/Folder...");
		btnSelectSrcDataFileFolder.addActionListener(this);
		btnSelectSrcDataFileFolder.setBounds(10, 20, 190, 50);
		btnSelectSrcDataFileFolder.setSize(190, 50);
		btnSelectSrcDataFileFolder.setLocation(10, 20);
		
		m_txtSelectSrcDataFileFolder = new JTextField(120);
		m_txtSelectSrcDataFileFolder.setBounds(210, 20, 470, 50);
		m_txtSelectSrcDataFileFolder.setSize(470, 50);
		m_txtSelectSrcDataFileFolder.setLocation(210, 20);

		
		btnSelectCandidateDataFileFolder = new JButton("Candidate Data File/Folder...");
		btnSelectCandidateDataFileFolder.addActionListener(this);
		btnSelectCandidateDataFileFolder.setBounds(10, 90, 190, 50);
		btnSelectCandidateDataFileFolder.setSize(190, 50);
		btnSelectCandidateDataFileFolder.setLocation(10, 90);
		
		m_txtSelectCandidateDataFileFolder = new JTextField(120);
		m_txtSelectCandidateDataFileFolder.setBounds(210, 90, 470, 50);
		m_txtSelectCandidateDataFileFolder.setSize(470, 50);
		m_txtSelectCandidateDataFileFolder.setLocation(210, 90);
		

		btnSelectSimilarityFileDataFolder = new JButton("Save Result ...");
		btnSelectSimilarityFileDataFolder.addActionListener(this);
		btnSelectSimilarityFileDataFolder.setBounds(10, 160, 160, 50);
		btnSelectSimilarityFileDataFolder.setSize(160, 50);
		btnSelectSimilarityFileDataFolder.setLocation(10, 160);
		
		m_txtSelectSimilarityFileDataFolder = new JTextField(120);
		m_txtSelectSimilarityFileDataFolder.setBounds(180, 160, 500, 50);
		m_txtSelectSimilarityFileDataFolder.setSize(500, 50);
		m_txtSelectSimilarityFileDataFolder.setLocation(180, 160);
		
		
		lblFrameNum = new JLabel("#Frame:");
		lblFrameNum.setBounds(10, 230, 60, 50);
		lblFrameNum.setSize(60, 50);
		lblFrameNum.setLocation(10, 230);
		
		m_txtFrameNum = new JTextField(80);
		m_txtFrameNum.setBounds(80, 230, 140, 50);
		m_txtFrameNum.setSize(140, 50);
		m_txtFrameNum.setLocation(80, 230);
		
		m_txtFrameNum.setText("" + Utility.NO_FRAME_SAMPLE);
		
		m_lblRunInfo = new JLabel("");
		m_lblRunInfo.setBounds(250, 300, 200, 30);
		m_lblRunInfo.setSize(200, 30);
		m_lblRunInfo.setLocation(250, 300);
		
		btnWebSimilarity = new JButton("Check Web Similarity");
		btnWebSimilarity.addActionListener(this);

		btnWebSimilarity.setBounds(10, 350, 200, 50);
		btnWebSimilarity.setSize(180, 50);
		btnWebSimilarity.setLocation(10, 350);

		btnStoreSimilarity = new JButton("Check Store Similarity");
		btnStoreSimilarity.addActionListener(this);

		btnStoreSimilarity.setBounds(250, 350, 200, 50);
		btnStoreSimilarity.setSize(180, 50);
		btnStoreSimilarity.setLocation(250, 350);
		
		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(this);

		btnExit.setBounds(480, 350, 200, 50);
		btnExit.setSize(180, 50);
		btnExit.setLocation(480, 350);
		

		pnlMain.add(btnSelectSrcDataFileFolder);
		pnlMain.add(m_txtSelectSrcDataFileFolder);
		
		pnlMain.add(btnSelectCandidateDataFileFolder);
		pnlMain.add(m_txtSelectCandidateDataFileFolder);

		
		pnlMain.add(btnSelectSimilarityFileDataFolder);
		pnlMain.add(m_txtSelectSimilarityFileDataFolder);
		
		pnlMain.add(lblFrameNum);
		pnlMain.add(m_txtFrameNum);
		
		pnlMain.add(m_lblRunInfo);
		
		pnlMain.add(btnWebSimilarity);
		pnlMain.add(btnStoreSimilarity);		
		pnlMain.add(btnExit);
		
		pack();
		
		setSize(700, 450);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		nScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		nScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation(nScreenWidth / 2 - 300, nScreenHeight / 2 - 175);
		
		m_txtSelectSrcDataFileFolder.setText(sSrcDataFileFolder);
		m_txtSelectCandidateDataFileFolder.setText(sCandidateDataFileFolder);
		m_txtSelectSimilarityFileDataFolder.setText(sSaveResult);
		
	}
	
	
	public void actionPerformed(ActionEvent ae) {
		int nRetVal;
		
		if (ae.getSource() == btnSelectSrcDataFileFolder) {
			m_fcSelectSrcDataFileFolder = new JFileChooser();
			m_fcSelectSrcDataFileFolder.setCurrentDirectory(new File("E://UIUC//Run"));
			m_fcSelectSrcDataFileFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSelectSrcDataFileFolder.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSelectSrcDataFileFolder.setText(m_fcSelectSrcDataFileFolder.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}
		
		if (ae.getSource() == btnSelectCandidateDataFileFolder) {
			m_fcSelectCandidateDataFileFolder = new JFileChooser();
			m_fcSelectCandidateDataFileFolder.setCurrentDirectory(new File("E://UIUC//Run"));
			m_fcSelectCandidateDataFileFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSelectCandidateDataFileFolder.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSelectCandidateDataFileFolder.setText(m_fcSelectCandidateDataFileFolder.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}

		if (ae.getSource() == btnSelectSimilarityFileDataFolder) {
			m_fcSelectSimilarityFileDataFolder = new JFileChooser();
			m_fcSelectSimilarityFileDataFolder.setCurrentDirectory(new File("E://UIUC//Run"));
			m_fcSelectSimilarityFileDataFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSelectSimilarityFileDataFolder.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSelectSimilarityFileDataFolder.setText(m_fcSelectSimilarityFileDataFolder.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}
		
		if (ae.getSource() == btnWebSimilarity) {
			m_sSrcDataFileFolder = m_txtSelectSrcDataFileFolder.getText();
			if (m_sSrcDataFileFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Source Data File Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_sCandidateDataFileFolder = m_txtSelectCandidateDataFileFolder.getText();
			if (m_sCandidateDataFileFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Candidate Data File Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sSimilarityFileDataFolder = m_txtSelectSimilarityFileDataFolder.getText();
			if (m_sSimilarityFileDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Folder to Web Similarity Result!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
									
			m_lblRunInfo.setText("Processing......");
			m_lblRunInfo.setVisible(true);
			
			CompareSimilarity.CalSimilarity(m_sSrcDataFileFolder, m_sCandidateDataFileFolder, m_sSimilarityFileDataFolder, Utility.SIMILARITY_WEB, Utility.NO_FRAME_SAMPLE);
						
			m_lblRunInfo.setText("");
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
		} 

		if (ae.getSource() == btnStoreSimilarity) {
			m_sSrcDataFileFolder = m_txtSelectSrcDataFileFolder.getText();
			if (m_sSrcDataFileFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Source Data File Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_sCandidateDataFileFolder = m_txtSelectCandidateDataFileFolder.getText();
			if (m_sCandidateDataFileFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Candidate Data File Folder!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sSimilarityFileDataFolder = m_txtSelectSimilarityFileDataFolder.getText();
			if (m_sSimilarityFileDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Folder to Web Similarity Result!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sFrameCnt = m_txtFrameNum.getText();
			if (m_sFrameCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set #Frame!", "Set Frane Count", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_nFrameCnt = Integer.valueOf(m_sFrameCnt).intValue();
									
			m_lblRunInfo.setText("Processing......");
			m_lblRunInfo.setVisible(true);
			
			int nArrFrameCnt[] = {5, 10, 15, 20, 25, 30, 35, 40, Utility.NO_FRAME_SAMPLE};
			
			for (int i=0; i<nArrFrameCnt.length; i++) {
	
				CompareSimilarity.CalSimilarity(m_sSrcDataFileFolder, m_sCandidateDataFileFolder, m_sSimilarityFileDataFolder, Utility.SIMILARITY_STORE, nArrFrameCnt[i]);
			}
						
			m_lblRunInfo.setText("");
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
		} 
		
		
		if (ae.getSource() == btnExit) {
			this.dispose();
		}
	}
	
	
}
	
	
