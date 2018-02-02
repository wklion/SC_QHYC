package com.spd.common;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 对应t_doc表
 * @author xianchao
 *
 */
public class TDoc {

	private String savepath;
	
	private String website;
	
	private int typeid;
	
	private String md5;
	
	private String filename;
	
	private String intime;
	
	private String filetime;

	public String getSavepath() {
		return savepath;
	}

	public void setSavepath(String savepath) {
		this.savepath = savepath;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public int getTypeid() {
		return typeid;
	}

	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getIntime() {
		return intime;
	}

	public void setIntime(String intime) {
		this.intime = intime;
	}

	public String getFiletime() {
		return filetime;
	}

	public void setFiletime(String filetime) {
		this.filetime = filetime;
	}

	
}
