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

public class ClusteringWin extends JFrame implements ActionListener {
	JPanel pnlMain;
	JButton btnRun, btnSmart, btnGenerateFrameIndexFile, btnExit;
	
	JButton btnSelectWebDataFolder;
	JFileChooser m_fcSelectWebDataFolder;
	JTextField m_txtSelectWebDataFolder;

	JButton btnSelectCrowdsourcedDataFolder;
	JFileChooser m_fcSelectCrowdsourcedDataFolder;
	JTextField m_txtSelectCrowdsourcedDataFolder;

	JButton btnSelectFrameIndexFileFolder;
	JFileChooser m_fcSelectFrameIndexFileFolder;
	JTextField m_txtSelectFrameIndexFileFolder;
	
	JButton btnSelectResultFolder;
	JFileChooser m_fcSelectResultFolder;
	JTextField m_txtSelectResultFolder;
	
	JCheckBox m_chkUsePlaceName;
	
	JLabel lblTotalFrameCnt;
	JTextField m_txtTotalFrameCnt;
	
	JLabel lblSubsetSize;
	JTextField m_txtSubsetSize;	

	JLabel lblLoopCnt;
	JTextField m_txtLoopCnt;	

	JLabel lblTotalStores;
	JTextField m_txtTotalStores;
	
	JLabel lblPicPerStore;
	JTextField m_txtPicPerStore;	
	
	
	String m_sTotalFrameCnt = "";
	int m_nTotalFrameCnt;
	
	String m_sSubsetSize = "";
	int m_nSubsetSize;
	
	String m_sTotalStoreCnt = "";
	int m_nTotalStoreCnt;
	
	String m_sMaxPicNumPerStore = "";
	int m_nMaxPicNumPerStore;
		
	String m_sFrameIndexFile = "";
	
	JLabel  m_lblRunInfo;
	
	int nScreenWidth, nScreenHeight;
	
	boolean m_blnRunning = false;
	String m_sWebDataFile = "";
	String m_sCrowdsourcedDataFolder = "";
	String m_sResultFolder = "";
	
	String sDefaultWebData = "E:\\UIUC\\Run\\201411_ForPaper_TFIDF\\Web_Street\\PlaceKeywordList_9999.csv";
	String sCrowdsourcedDataFolder = "E:\\UIUC\\Run\\201411_ForPaper_TFIDF\\OCR_Street";
	String sFrameIndexFile = "E:\\UIUC\\Run\\FrameIndex\\FrameIndex_10_5.csv";
	//String sResultFile = "E:\\UIUC\\Run\\ClusteringResultSmart_FilterWithWiFi\\Street";
	String sResultFile = "E:\\UIUC\\Run\\PictureClusteringResult\\ClusteringResultSmart_FilterWithWiFi201508\\Street";
		
	public ClusteringWin() {
		// TODO Auto-generated constructor stub
		super("Clustering");
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
		
		btnSelectFrameIndexFileFolder = new JButton("Frame Index File ...");
		btnSelectFrameIndexFileFolder.addActionListener(this);
		btnSelectFrameIndexFileFolder.setBounds(10, 140, 160, 40);
		btnSelectFrameIndexFileFolder.setSize(160, 40);
		btnSelectFrameIndexFileFolder.setLocation(10, 140);
		
		m_txtSelectFrameIndexFileFolder = new JTextField(120);
		m_txtSelectFrameIndexFileFolder.setBounds(180, 140, 500, 40);
		m_txtSelectFrameIndexFileFolder.setSize(500, 40);
		m_txtSelectFrameIndexFileFolder.setLocation(180, 140);

		btnSelectResultFolder = new JButton("Save Result ...");
		btnSelectResultFolder.addActionListener(this);
		btnSelectResultFolder.setBounds(10, 200, 160, 40);
		btnSelectResultFolder.setSize(160, 40);
		btnSelectResultFolder.setLocation(10, 200);
		
		m_txtSelectResultFolder = new JTextField(120);
		m_txtSelectResultFolder.setBounds(180, 200, 500, 40);
		m_txtSelectResultFolder.setSize(500, 40);
		m_txtSelectResultFolder.setLocation(180, 200);
				
		lblTotalFrameCnt = new JLabel("Total Frames:");
		lblTotalFrameCnt.setBounds(10, 260, 80, 30);
		lblTotalFrameCnt.setSize(80, 30);
		lblTotalFrameCnt.setLocation(10, 260);
		
		m_txtTotalFrameCnt = new JTextField(80);
		m_txtTotalFrameCnt.setBounds(100, 260, 50, 30);
		m_txtTotalFrameCnt.setSize(50, 30);
		m_txtTotalFrameCnt.setLocation(100, 260);
		m_txtTotalFrameCnt.setText("10");
		
		lblSubsetSize = new JLabel("Subset Size:");
		lblSubsetSize.setBounds(180, 260, 80, 30);
		lblSubsetSize.setSize(80, 30);
		lblSubsetSize.setLocation(180, 260);
		
		m_txtSubsetSize = new JTextField(80);
		m_txtSubsetSize.setBounds(270, 260, 40, 30);
		m_txtSubsetSize.setSize(40, 30);
		m_txtSubsetSize.setLocation(270, 260);
		m_txtSubsetSize.setText("5"); 
		
		lblLoopCnt = new JLabel("#Loop:");
		lblLoopCnt.setBounds(340, 260, 40, 30);
		lblLoopCnt.setSize(40, 30);
		lblLoopCnt.setLocation(340, 260);
		
		m_txtLoopCnt = new JTextField(80);
		m_txtLoopCnt.setBounds(390, 260, 50, 30);
		m_txtLoopCnt.setSize(50, 30);
		m_txtLoopCnt.setLocation(390, 260);
		m_txtLoopCnt.setText("1"); 
		
		m_chkUsePlaceName = new JCheckBox("Use PlaceName");
		m_chkUsePlaceName.setBounds(480, 260, 140, 30);
		m_chkUsePlaceName.setSize(140, 30);
		m_chkUsePlaceName.setLocation(480, 260);
		
		m_chkUsePlaceName.setSelected(true);
		
				
		lblTotalStores = new JLabel("#Store:");
		lblTotalStores.setBounds(10, 315, 50, 30);
		lblTotalStores.setSize(50, 30);
		lblTotalStores.setLocation(10, 315);
		
		m_txtTotalStores = new JTextField(80);
		m_txtTotalStores.setBounds(70, 315, 50, 30);
		m_txtTotalStores.setSize(50, 30);
		m_txtTotalStores.setLocation(70, 315);
		m_txtTotalStores.setText("4");
		
		lblPicPerStore = new JLabel("#Max Pic/Store:");
		lblPicPerStore.setBounds(150, 315, 100, 30);
		lblPicPerStore.setSize(100, 30);
		lblPicPerStore.setLocation(150, 315);
		
		m_txtPicPerStore = new JTextField(80);
		m_txtPicPerStore.setBounds(260, 315, 40, 30);
		m_txtPicPerStore.setSize(40, 30);
		m_txtPicPerStore.setLocation(260, 315);
		m_txtPicPerStore.setText("5"); 

		m_lblRunInfo = new JLabel("");
		m_lblRunInfo.setBounds(340, 315, 200, 30);
		m_lblRunInfo.setSize(200, 30);
		m_lblRunInfo.setLocation(340, 315);
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(this);

		btnRun.setBounds(10, 360, 120, 50);
		btnRun.setSize(120, 50);
		btnRun.setLocation(10, 360);

		btnSmart = new JButton("Run Smart");
		btnSmart.addActionListener(this);

		btnSmart.setBounds(160, 360, 150, 50);
		btnSmart.setSize(150, 50);
		btnSmart.setLocation(160, 360);
		
		btnGenerateFrameIndexFile = new JButton("Generate Frame Index");
		btnGenerateFrameIndexFile.addActionListener(this);

		btnGenerateFrameIndexFile.setBounds(340, 360, 180, 50);
		btnGenerateFrameIndexFile.setSize(180,50);
		btnGenerateFrameIndexFile.setLocation(340, 360);
		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(this);

		btnExit.setBounds(550, 360, 120, 50);
		btnExit.setSize(120, 50);
		btnExit.setLocation(550, 360);
		
		pnlMain.add(btnSelectWebDataFolder);
		pnlMain.add(m_txtSelectWebDataFolder);
		
		pnlMain.add(btnSelectCrowdsourcedDataFolder);
		pnlMain.add(m_txtSelectCrowdsourcedDataFolder);

		
		pnlMain.add(btnSelectResultFolder);
		pnlMain.add(m_txtSelectResultFolder);

		pnlMain.add(btnSelectFrameIndexFileFolder);
		pnlMain.add(m_txtSelectFrameIndexFileFolder);
				
		pnlMain.add(lblTotalFrameCnt);
		pnlMain.add(m_txtTotalFrameCnt);
		
		pnlMain.add(m_chkUsePlaceName);
		
		pnlMain.add(lblSubsetSize);
		pnlMain.add(m_txtSubsetSize);
		
		pnlMain.add(lblLoopCnt);
		pnlMain.add(m_txtLoopCnt);
		
		pnlMain.add(lblTotalStores);
		pnlMain.add(m_txtTotalStores);
		pnlMain.add(lblPicPerStore);
		pnlMain.add(m_txtPicPerStore);
			
		pnlMain.add(m_lblRunInfo);
		
		pnlMain.add(btnRun);
		pnlMain.add(btnSmart);
		pnlMain.add(btnGenerateFrameIndexFile);
		pnlMain.add(btnExit);
		
		
		m_txtSelectWebDataFolder.setText(sDefaultWebData);
		m_txtSelectCrowdsourcedDataFolder.setText(sCrowdsourcedDataFolder);
		m_txtSelectFrameIndexFileFolder.setText(sFrameIndexFile);
		m_txtSelectResultFolder.setText(sResultFile);
		
		pack();
		
		setSize(700, 450);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		nScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		nScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation(nScreenWidth / 2 - 300, nScreenHeight / 2 - 175);
				
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

		if (ae.getSource() == btnSelectFrameIndexFileFolder) {
			m_fcSelectFrameIndexFileFolder = new JFileChooser();
			m_fcSelectFrameIndexFileFolder.setCurrentDirectory(new File("E://UIUC//Run//"));
			m_fcSelectFrameIndexFileFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSelectFrameIndexFileFolder.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSelectFrameIndexFileFolder.setText(m_fcSelectFrameIndexFileFolder.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}
		
		if (ae.getSource() == btnSelectResultFolder) {
			m_fcSelectResultFolder = new JFileChooser();
			m_fcSelectResultFolder.setCurrentDirectory(new File("E://UIUC//Run//"));
			m_fcSelectResultFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSelectResultFolder.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSelectResultFolder.setText(m_fcSelectResultFolder.getSelectedFile().getAbsoluteFile().toString());
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

			m_sFrameIndexFile = m_txtSelectFrameIndexFileFolder.getText();
			if (m_sFrameIndexFile.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Frame Index File!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sResultFolder = m_txtSelectResultFolder.getText();
			if (m_sResultFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Folder to Clustering Result!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
						
			m_sTotalFrameCnt = m_txtTotalFrameCnt.getText();
			if (m_sTotalFrameCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Set Total Frame Count!", "Set Frame Count", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_nTotalFrameCnt = Integer.valueOf(m_sTotalFrameCnt).intValue();
			
			
			m_sSubsetSize = m_txtSubsetSize.getText();
			if (m_sSubsetSize.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set subset size!", "Set Subset Size", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_nSubsetSize = Integer.valueOf(m_sSubsetSize).intValue();

			String sLoopCnt = m_txtLoopCnt.getText();
			if (sLoopCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set Loop count!", "Set Loop Count", JOptionPane.WARNING_MESSAGE);
				return;
			}

			int nLoopCnt = Integer.valueOf(sLoopCnt).intValue();
			
			System.out.println("[PlaceApManager DEBUG 1] Start Generating GPS-AP-Place Database file...");
			
			m_lblRunInfo.setText("Processing......");
			m_lblRunInfo.setVisible(true);
			m_lblRunInfo.invalidate();
			
			if (m_chkUsePlaceName.isSelected()) {
				Utility.MATCH_WITH_PLACENAME = true;
			} else {
				Utility.MATCH_WITH_PLACENAME = false;
			}
			
			Utility.USE_FULL_TEXT = false;

			Utility.MATCH_WITH_PLACENAME = false;
			ClusteringProcessor.doCluster(m_sWebDataFile, m_sCrowdsourcedDataFolder, m_sFrameIndexFile, m_sResultFolder, m_nTotalFrameCnt, m_nSubsetSize, nLoopCnt);
			
			//Utility.MATCH_WITH_PLACENAME = true;
			//ClusteringProcessor.doCluster(m_sWebDataFile, m_sCrowdsourcedDataFolder, m_sFrameIndexFile, m_sResultFolder, m_nTotalFrameCnt, m_nSubsetSize, nLoopCnt);
			
			m_lblRunInfo.setText("");
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
			
			return;
		} 
		

		if (ae.getSource() == btnSmart) {
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

			m_sFrameIndexFile = m_txtSelectFrameIndexFileFolder.getText();
			if (m_sFrameIndexFile.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Frame Index File!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sResultFolder = m_txtSelectResultFolder.getText();
			if (m_sResultFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Folder to Clustering Result!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
						
			m_sTotalStoreCnt = m_txtTotalStores.getText();
			if (m_sTotalStoreCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Set Store Number!", "Set Store Count", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_nTotalStoreCnt = Integer.valueOf(m_sTotalStoreCnt).intValue();
					
			m_sMaxPicNumPerStore = m_txtPicPerStore.getText();
			if (m_sMaxPicNumPerStore.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set Max Pic Number per Store!", "Set Max Pic Number", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_nMaxPicNumPerStore = Integer.valueOf(m_sMaxPicNumPerStore).intValue();
			
			String sLoopCnt = m_txtLoopCnt.getText();
			if (sLoopCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set Loop count!", "Set Loop Count", JOptionPane.WARNING_MESSAGE);
				return;
			}

			int nLoopCnt = Integer.valueOf(sLoopCnt).intValue();
			
			System.out.println("[PlaceApManager DEBUG 1] Start Generating GPS-AP-Place Database file...");
			
			m_lblRunInfo.setText("Processing......");
			m_lblRunInfo.setVisible(true);
			m_lblRunInfo.invalidate();
			
			if (m_chkUsePlaceName.isSelected()) {
				Utility.MATCH_WITH_PLACENAME = true;
			} else {
				Utility.MATCH_WITH_PLACENAME = false;
			}
			
			
			ClusteringProcessorSmart.doCluster(m_sWebDataFile, m_sCrowdsourcedDataFolder, m_sFrameIndexFile, m_sResultFolder, m_nTotalStoreCnt, m_nMaxPicNumPerStore, nLoopCnt);
			
			//Utility.MATCH_WITH_PLACENAME = true;
			//ClusteringProcessorSmart.doCluster(m_sWebDataFile, m_sCrowdsourcedDataFolder, m_sFrameIndexFile, m_sResultFolder, m_nTotalFrameCnt, m_nSubsetSize, nLoopCnt);
			
			m_lblRunInfo.setText("");
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
			
			return;
		} 
		
		
		if (ae.getSource() == btnGenerateFrameIndexFile) {
			//Here Result Folder should be the folder which will contain the FrameIndex file, such as "FrameIndex_10_5.csv"
			m_sResultFolder = m_txtSelectResultFolder.getText();
			if (m_sResultFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Select Folder to Store Result Frame Index File!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sTotalFrameCnt = m_txtTotalFrameCnt.getText();
			if (m_sTotalFrameCnt.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please Set Total Frame Count!", "Set Frame Count", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_nTotalFrameCnt = Integer.valueOf(m_sTotalFrameCnt).intValue();
			
			
			m_sSubsetSize = m_txtSubsetSize.getText();
			if (m_sSubsetSize.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set subset size!", "Set Subset Size", JOptionPane.WARNING_MESSAGE);
				return;
			}

			m_nSubsetSize = Integer.valueOf(m_sSubsetSize).intValue();

			Utility.PrintCurrentTime();
			
			System.out.println("Generating...");
			
//			List<List<List<Integer>>> lstlstlstFrameIndex = Utility.getFrameIndexList(m_nTotalFrameCnt, m_nSubsetSize); 
//			List<List<List<Integer>>> lstlstlstFrameIndex = Utility.getFrameIndexList_Special(m_nTotalFrameCnt, m_nSubsetSize); 

			List<List<List<Integer>>> lstlstlstFrameIndex = Utility.getFrameIndexList_Partion(m_nTotalFrameCnt, m_nSubsetSize); 
			
			Utility.writeFrameIndexFile(m_sResultFolder, m_nTotalFrameCnt, m_nSubsetSize, lstlstlstFrameIndex);
			
			Utility.PrintCurrentTime();
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
			
			return;
		} 
		
			
		if (ae.getSource() == btnExit) {
			this.dispose();
		}
	}
	
	
	
}
	
	
