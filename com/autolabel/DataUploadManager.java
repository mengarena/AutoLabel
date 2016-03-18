package com.autolabel;

import java.util.List;

public class DataUploadManager {

	private int m_nDataType;
	private String m_sDataFileFolder;
	private DatabaseOperation m_DatabaseOperation = new DatabaseOperation();
	
	public DataUploadManager() {
		// TODO Auto-generated constructor stub
	}

	
	public DataUploadManager(int nDataType, String sDataFileFolder) {
		m_nDataType = nDataType;
		m_sDataFileFolder = sDataFileFolder;
	}
	
	
	public void UploadData() {
		if (m_nDataType == Utility.DB_SOURCE_DATA_TYPE_FILE) {
			m_DatabaseOperation.uploadPlaceAPInfo(m_sDataFileFolder);
			return;
		}
		
		if (m_nDataType == Utility.DB_SOURCE_DATA_TYPE_FOLDER) {
			List<String> lstDBFileList = Utility.getPlaceApDatabaseFileList(m_sDataFileFolder);
			
			if (lstDBFileList == null || lstDBFileList.size() == 0) {
				return;
			}
			
			m_DatabaseOperation.uploadPlaceAPInfo(lstDBFileList);
			return;
		}
	}
	
}

