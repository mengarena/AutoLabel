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

public class WeightAssignmentWin extends JFrame implements ActionListener {
	JPanel pnlMain;
	JLabel lblWeight, lblMetaWeight, lblWebContentWeight, lblWebImageWeight, m_lblRunInfo;
	JTextField m_txtMetaWeight, m_txtWebContentWeight, m_txtWebImageWeight;
	JButton btnRun, btnExit;
	
	JButton btnSelectDataFolder;
	JFileChooser m_fcSelectDataFolder;
	JTextField m_txtSelectDataFolder;

	JButton btnResultFile;
	JFileChooser m_fcResult;
	JTextField m_txtResult;
	
	JLabel lblFinalTopKeywordNum;
	JTextField m_txtFinalTopKeywordNum;
	
	String m_sWeightMeta, m_sWeightContent, m_sWeightImage;
	double m_fTotalWeightMeta, m_fTotalWeightContent, m_fTotalWeightImage;
	
	String m_sTopFinalKeywordNum;
	int m_nTopFinalKeywordNum;
	
	int nScreenWidth, nScreenHeight;
	
	boolean m_blnRunning = false;
	String m_sDataFolder = "";
	
	public WeightAssignmentWin() {
		
	}
	
	public WeightAssignmentWin(String sDataFolder) {
		// TODO Auto-generated constructor stub
		super("Keyword Weight Assignment");
		pnlMain = new JPanel();
		pnlMain.setLayout(null);
		this.getContentPane().add(pnlMain);
				
		btnSelectDataFolder = new JButton("Data Folder ...");
		btnSelectDataFolder.addActionListener(this);
		btnSelectDataFolder.setBounds(10, 20, 140, 50);
		btnSelectDataFolder.setSize(140, 50);
		btnSelectDataFolder.setLocation(10, 20);
		
		m_txtSelectDataFolder = new JTextField(120);
		m_txtSelectDataFolder.setBounds(160, 20, 520, 50);
		m_txtSelectDataFolder.setSize(520, 50);
		m_txtSelectDataFolder.setLocation(160, 20);
		
		
		lblWeight = new JLabel("Weight Portion:");
		lblWeight.setBounds(10, 90, 90, 50);
		lblWeight.setSize(90, 50);
		lblWeight.setLocation(10, 90);

		lblMetaWeight = new JLabel("Meta");
		lblMetaWeight.setBounds(110, 90, 30, 50);
		lblMetaWeight.setSize(30, 50);
		lblMetaWeight.setLocation(110, 90);
		
		m_txtMetaWeight = new JTextField(30);
		m_txtMetaWeight.setBounds(145, 90, 100, 50);
		m_txtMetaWeight.setSize(100, 50);
		m_txtMetaWeight.setLocation(145, 90);

		lblWebContentWeight = new JLabel("Content");
		lblWebContentWeight.setBounds(300, 90, 50, 50);
		lblWebContentWeight.setSize(50, 50);
		lblWebContentWeight.setLocation(300, 90);
		
		m_txtWebContentWeight = new JTextField(30);
		m_txtWebContentWeight.setBounds(355, 90, 100, 50);
		m_txtWebContentWeight.setSize(100, 50);
		m_txtWebContentWeight.setLocation(355, 90);
		
		lblWebImageWeight = new JLabel("Image");
		lblWebImageWeight.setBounds(495, 90, 40, 50);
		lblWebImageWeight.setSize(40, 50);
		lblWebImageWeight.setLocation(495, 90);
		
		m_txtWebImageWeight = new JTextField(30);
		m_txtWebImageWeight.setBounds(540, 90, 100, 50);
		m_txtWebImageWeight.setSize(100, 50);
		m_txtWebImageWeight.setLocation(540, 90);

		
		lblFinalTopKeywordNum = new JLabel("Number of Top Final Keywords:");
		lblFinalTopKeywordNum.setBounds(10, 160, 180, 50);
		lblFinalTopKeywordNum.setSize(180,50);
		lblFinalTopKeywordNum.setLocation(10, 160);
		
		m_txtFinalTopKeywordNum = new JTextField(80);
		m_txtFinalTopKeywordNum.setBounds(200, 160, 140, 50);
		m_txtFinalTopKeywordNum.setSize(140, 50);
		m_txtFinalTopKeywordNum.setLocation(200, 160);

//		btnResultFile = new JButton("Result File ...");
//		btnResultFile.addActionListener(this);
//		btnResultFile.setBounds(10, 230, 140, 50);
//		btnResultFile.setSize(140, 50);
//		btnResultFile.setLocation(10, 230);
//		
//		m_txtResult = new JTextField(120);
//		m_txtResult.setBounds(160, 230, 520, 50);
//		m_txtResult.setSize(520, 50);
//		m_txtResult.setLocation(160, 230);
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(this);

		btnRun.setBounds(120, 350, 200, 50);
		btnRun.setSize(180, 50);
		btnRun.setLocation(120, 350);
		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(this);

		btnExit.setBounds(360, 350, 200, 50);
		btnExit.setSize(180, 50);
		btnExit.setLocation(360, 350);
		
		m_lblRunInfo = new JLabel("");
		m_lblRunInfo.setBounds(250, 290, 200, 30);
		m_lblRunInfo.setSize(200, 30);
		m_lblRunInfo.setLocation(250, 290);

		pnlMain.add(btnSelectDataFolder);
		pnlMain.add(m_txtSelectDataFolder);
		
		pnlMain.add(lblWeight);
		pnlMain.add(lblMetaWeight);
		pnlMain.add(m_txtMetaWeight);
		pnlMain.add(lblWebContentWeight);
		pnlMain.add(m_txtWebContentWeight);
		pnlMain.add(lblWebImageWeight);
		pnlMain.add(m_txtWebImageWeight);
		
		pnlMain.add(lblFinalTopKeywordNum);
		pnlMain.add(m_txtFinalTopKeywordNum);
		
//		pnlMain.add(btnResultFile);
//		pnlMain.add(m_txtResult);
		
		pnlMain.add(btnRun);
		pnlMain.add(btnExit);
		
		pnlMain.add(m_lblRunInfo);
		
		pack();
		
		setSize(700, 450);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		nScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		nScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation(nScreenWidth / 2 - 300, nScreenHeight / 2 - 175);
		
		m_sDataFolder = sDataFolder;
		m_txtSelectDataFolder.setText(m_sDataFolder);
		
		m_txtMetaWeight.setText("" + Utility.DEFAULT_TOTAL_WEIGHT_META);
		m_txtWebContentWeight.setText("" + Utility.DEFAULT_TOTAL_WEIGHT_WEB_CONTENT);
		m_txtWebImageWeight.setText("" + Utility.DEFAULT_TOTAL_WEIGHT_WEB_IMG);
		
		m_txtFinalTopKeywordNum.setText("" + Utility.DEFAULT_TOP_KEYWORD_CNT);
		
	}
	
	
	public void actionPerformed(ActionEvent ae) {
		int nRetVal;
		
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
			m_sWeightMeta = m_txtMetaWeight.getText();
			m_sWeightContent = m_txtWebContentWeight.getText();
			m_sWeightImage = m_txtWebImageWeight.getText();
			
			m_sDataFolder = m_txtSelectDataFolder.getText();
			
			if (m_sDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please select data folder!", "Weight Assignment", JOptionPane.WARNING_MESSAGE);
				return;				
			}
			
			if (m_sWeightMeta.length() == 0 || m_sWeightContent.length() == 0 || m_sWeightImage.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set weight!", "Weight Assignment", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			m_sTopFinalKeywordNum = m_txtFinalTopKeywordNum.getText();
			
			if (m_sTopFinalKeywordNum.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please set Top Final Keyword Number", "Weight Assignment", JOptionPane.WARNING_MESSAGE);
				return;				
			}
					
			m_lblRunInfo.setText("Processing....");
			m_lblRunInfo.setVisible(true);
			
			m_fTotalWeightMeta = Double.valueOf(m_sWeightMeta).doubleValue();
			m_fTotalWeightContent = Double.valueOf(m_sWeightContent).doubleValue();
			m_fTotalWeightImage = Double.valueOf(m_sWeightImage).doubleValue();
			
			m_nTopFinalKeywordNum = Integer.valueOf(m_sTopFinalKeywordNum).intValue();
						
			ProcessAllKeywords m_ProcessAllKeywords = new ProcessAllKeywords(m_sDataFolder, m_fTotalWeightMeta, m_fTotalWeightContent, m_fTotalWeightImage, m_nTopFinalKeywordNum);
			
			m_ProcessAllKeywords.ProcessPlaceKeyword();
			m_lblRunInfo.setText("");
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
		} 
					
		if (ae.getSource() == btnExit) {
			this.dispose();
		}
	}
	
	
	
}
	
	
