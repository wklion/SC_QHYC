package com.spd.common;
/**
 * 对应数据库t_metadict的实体
 * @author xianchao
 *
 */
public class ResourceItem {

		private int id;
		
		private String URL;
		
		private String fileTimeStr;
		
		private boolean isUTC;

		private String storePath;
		
		private String productCode;
		
		private String interfaceAddress;
		
		private String savePath;
		
		private String username;

		private String password;
		
		private String timeFormat;
		
		public String getTimeFormat() {
			return timeFormat;
		}

		public void setTimeFormat(String timeFormat) {
			this.timeFormat = timeFormat;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getSavePath() {
			return savePath;
		}

		public void setSavePath(String savePath) {
			this.savePath = savePath;
		}

		public String getInterfaceAddress() {
			return interfaceAddress;
		}

		public void setInterfaceAddress(String interfaceAddress) {
			this.interfaceAddress = interfaceAddress;
		}

		public String getProductCode() {
			return productCode;
		}

		public void setProductCode(String productCode) {
			this.productCode = productCode;
		}

		public String getStorePath() {
			return storePath;
		}

		public void setStorePath(String storePath) {
			this.storePath = storePath;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getURL() {
			return URL;
		}

		public void setURL(String uRL) {
			URL = uRL;
		}

		public String getFileTimeStr() {
			return fileTimeStr;
		}

		public void setFileTimeStr(String fileTimeStr) {
			this.fileTimeStr = fileTimeStr;
		}

		public boolean isUTC() {
			return isUTC;
		}

		public void setUTC(boolean isUTC) {
			this.isUTC = isUTC;
		}
		
}
