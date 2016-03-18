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

public class ProcessEvaluationWin extends JFrame implements ActionListener {
	JPanel pnlMain;
	JButton btnRun, btnRunMultipleIndex, btnRunGroup, btnExit;
	
	JButton btnSelectDataFolder;
	JFileChooser m_fcSelectDataFolder;
	JTextField m_txtSelectDataFolder;
		
	
	int nScreenWidth, nScreenHeight;
	
	boolean m_blnRunning = false;
	String m_sDataFolder = "";
	
	
	public ProcessEvaluationWin() {
		// TODO Auto-generated constructor stub
		super("Evaluation Data Processor");
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
		
			
		
		btnRun = new JButton("Calculate Rank");
		btnRun.addActionListener(this);

		btnRun.setBounds(10, 350, 130, 50);
		btnRun.setSize(130, 50);
		btnRun.setLocation(10, 350);
		
		btnRunGroup = new JButton("Cal Rank (Group)");
		btnRunGroup.addActionListener(this);

		btnRunGroup.setBounds(160, 350, 170, 50);
		btnRunGroup.setSize(170, 50);
		btnRunGroup.setLocation(160, 350);

		
		btnRunMultipleIndex = new JButton("Rank MultiIdx (Group)");
		btnRunMultipleIndex.addActionListener(this);

		btnRunMultipleIndex.setBounds(350, 350, 170, 50);
		btnRunMultipleIndex.setSize(170, 50);
		btnRunMultipleIndex.setLocation(350, 350);
		
		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(this);

		btnExit.setBounds(540, 350, 130, 50);
		btnExit.setSize(130, 50);
		btnExit.setLocation(540, 350);
		
		pnlMain.add(btnSelectDataFolder);
		pnlMain.add(m_txtSelectDataFolder);

		
		pnlMain.add(btnRun);
		pnlMain.add(btnRunMultipleIndex);
		pnlMain.add(btnRunGroup);
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
			m_sDataFolder = m_txtSelectDataFolder.getText();
			if (m_sDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please select folder of evaluation result data!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			ProcessEvaluationData.CalculateRank(m_sDataFolder);
			
			System.out.println("Done!!!!!!!!!!!!!!!!");
			
			return;
		} 
		
		
		if (ae.getSource() == btnRunGroup) {
			m_sDataFolder = m_txtSelectDataFolder.getText();
			if (m_sDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please select parent folder of evaluation result data!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			ProcessEvaluationData.CalculateRankAllSub(m_sDataFolder);
			
			System.out.println("Done!!!!!!!!!!!!!!!!");
			
			return;
		} 
		

		if (ae.getSource() == btnRunMultipleIndex) {
			m_sDataFolder = m_txtSelectDataFolder.getText();
			if (m_sDataFolder.length() == 0) {
				JOptionPane.showMessageDialog(null, "Please select parent folder of evaluation result data!", "Select Folder", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
						
			ProcessEvaluationData.CalculateRankAllSub_MultipleIndexSet(m_sDataFolder);
			
			System.out.println("Done!!!!!!!!!!!!!!!!");
			
			return;
		} 
		
		
		if (ae.getSource() == btnExit) {
			this.dispose();
		}
	}
	
}
	
	
