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
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import javax.swing.JOptionPane;

public class DataUploadWin extends JFrame implements ActionListener {
	JPanel pnlMain;
	JButton btnUpload, btnExit;
	
	JRadioButton jrFile, jrFolder;
	JButton btnSelectDataFile;
	JFileChooser m_fcSelectDataFile;
	JTextField m_txtSelectDataFile;

	JButton btnSelectDataFolder;
	JFileChooser m_fcSelectDataFolder;
	JTextField m_txtSelectDataFolder;
	
	JLabel lblRunInfo;
	
	int nScreenWidth, nScreenHeight;
	
	boolean m_blnRunning = false;
	String m_sDataFileFolder = "";
	
		
	public DataUploadWin() {
		// TODO Auto-generated constructor stub
		super("Data Upload");
		pnlMain = new JPanel();
		pnlMain.setLayout(null);
		this.getContentPane().add(pnlMain);
		
		jrFile = new JRadioButton("");
		jrFile.addActionListener(this);
		jrFile.setBounds(10, 50, 20, 50);
		jrFile.setSize(20, 50);
		jrFile.setLocation(10, 50);

		btnSelectDataFile = new JButton("Place AP DB File ...");
		btnSelectDataFile.addActionListener(this);
		btnSelectDataFile.setBounds(40, 50, 160, 50);
		btnSelectDataFile.setSize(160, 50);
		btnSelectDataFile.setLocation(40, 50);
		
		m_txtSelectDataFile = new JTextField(120);
		m_txtSelectDataFile.setBounds(210, 50, 470, 50);
		m_txtSelectDataFile.setSize(470, 50);
		m_txtSelectDataFile.setLocation(210, 50);

		jrFolder = new JRadioButton("");
		jrFolder.addActionListener(this);
		jrFolder.setBounds(10, 150, 20, 50);
		jrFolder.setSize(20, 50);
		jrFolder.setLocation(10, 150);
		
		btnSelectDataFolder = new JButton("Place AP DB Folder ...");
		btnSelectDataFolder.addActionListener(this);
		btnSelectDataFolder.setBounds(40, 150, 160, 50);
		btnSelectDataFolder.setSize(160, 50);
		btnSelectDataFolder.setLocation(40, 150);
		
		m_txtSelectDataFolder = new JTextField(120);
		m_txtSelectDataFolder.setBounds(210, 150, 470, 50);
		m_txtSelectDataFolder.setSize(470, 50);
		m_txtSelectDataFolder.setLocation(210, 150);
		
		lblRunInfo = new JLabel("");
		lblRunInfo.setBounds(150, 250, 360, 50);
		lblRunInfo.setSize(360, 50);
		lblRunInfo.setLocation(150, 250);

		
		btnUpload = new JButton("Upload");
		btnUpload.addActionListener(this);

		btnUpload.setBounds(120, 350, 200, 50);
		btnUpload.setSize(180, 50);
		btnUpload.setLocation(120, 350);
		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(this);

		btnExit.setBounds(380, 350, 200, 50);
		btnExit.setSize(180, 50);
		btnExit.setLocation(380, 350);
		
		pnlMain.add(jrFile);
		pnlMain.add(btnSelectDataFile);
		pnlMain.add(m_txtSelectDataFile);

		pnlMain.add(jrFolder);
		pnlMain.add(btnSelectDataFolder);
		pnlMain.add(m_txtSelectDataFolder);
		
		pnlMain.add(lblRunInfo);
		pnlMain.add(btnUpload);
		pnlMain.add(btnExit);
		
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
		int nDataType = 0;
		
		if (ae.getSource() == btnSelectDataFile) {
			m_fcSelectDataFile = new JFileChooser();
			m_fcSelectDataFile.setCurrentDirectory(new File("E://"));
			m_fcSelectDataFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSelectDataFile.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSelectDataFile.setText(m_fcSelectDataFile.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}
		
		if (ae.getSource() == btnSelectDataFolder) {
			m_fcSelectDataFolder = new JFileChooser();
			m_fcSelectDataFolder.setCurrentDirectory(new File("E://"));
			m_fcSelectDataFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			nRetVal = m_fcSelectDataFolder.showSaveDialog(null);
			if (nRetVal == JFileChooser.APPROVE_OPTION) { 
				m_txtSelectDataFolder.setText(m_fcSelectDataFolder.getSelectedFile().getAbsoluteFile().toString());
			}
			return;
		}

		
		if (ae.getSource() == jrFile) {
			if (jrFolder.isSelected() == true) jrFolder.setSelected(false);
			return;
		}

		if (ae.getSource() == jrFolder) {
			if (jrFile.isSelected() == true) jrFile.setSelected(false);
			return;
		}
		
		if (ae.getSource() == btnUpload) {
						
			if (jrFile.isSelected() == false && jrFolder.isSelected() == false) {
				JOptionPane.showMessageDialog(null, "Please choose data source type!", "Select Data Source Type", JOptionPane.WARNING_MESSAGE);
				return;				
			}
			
			if (jrFile.isSelected()) {
				m_sDataFileFolder = m_txtSelectDataFile.getText();
				if (m_sDataFileFolder.length() == 0) {
					JOptionPane.showMessageDialog(null, "Please select file to upload!", "Select File", JOptionPane.WARNING_MESSAGE);
					return;
				}
			
				nDataType = Utility.DB_SOURCE_DATA_TYPE_FILE;

			}
			
			
			if (jrFolder.isSelected()) {
				m_sDataFileFolder = m_txtSelectDataFolder.getText();
				if (m_sDataFileFolder.length() == 0) {
					JOptionPane.showMessageDialog(null, "Please select folder to upload!", "Select Folder", JOptionPane.WARNING_MESSAGE);
					return;
				}
			
				
				nDataType = Utility.DB_SOURCE_DATA_TYPE_FOLDER;
			}
			
			lblRunInfo.setText("Uploading data ...");
			lblRunInfo.setVisible(true);
			DataUploadManager locDataUploadManager = new DataUploadManager(nDataType, m_sDataFileFolder);
			locDataUploadManager.UploadData();
			
			lblRunInfo.setText("");
			
			System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!");
			
			return;
		} 
		
		
		if (ae.getSource() == btnExit) {
			this.dispose();
		}
	}
	
	
	
}
	
	
