package com.spd.grid.domain;

import java.io.Serializable;

import com.spd.grid.annotation.Column;
import com.spd.grid.annotation.Entity;
import com.spd.grid.annotation.Id;


@Entity("t_areaCustom")
public class Area implements Serializable{
	@Id("id")
	private int id;
	@Column("name")
	private String name;
	@Column("centerX")
	private Double centerX;
	@Column("centerY")
	private Double centerY;
	@Column("coordinates")
	private String coordinates;
	@Column("createDate")
	private String createDate;
	@Column("createUser")
	private String createUser;
	@Column("departCode")
	private String departCode;
	@Column("type")
	private int type; 
	@Column("stationCode")
	private String stationCode;
	@Column("stationName")
	private String stationName;
	@Column("stationX")
	private double stationX;
	@Column("stationY")
	private double stationY;
	@Column("status")
	private int status;
	
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getStationCode() {
		return stationCode;
	}
	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public double getStationX() {
		return stationX;
	}
	public void setStationX(double stationX) {
		this.stationX = stationX;
	}
	public double getStationY() {
		return stationY;
	}
	public void setStationY(double stationY) {
		this.stationY = stationY;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	
	public Area(String name, Double centerX, Double centerY,
			String coordinates, String createDate, String createUser,
			String departCode, int type, String stationCode,
			String stationName, double stationX, double stationY, int status) {
		super();
		this.name = name;
		this.centerX = centerX;
		this.centerY = centerY;
		this.coordinates = coordinates;
		this.createDate = createDate;
		this.createUser = createUser;
		this.departCode = departCode;
		this.type = type;
		this.stationCode = stationCode;
		this.stationName = stationName;
		this.stationX = stationX;
		this.stationY = stationY;
		this.status = status;
	}
	
	
	
	
	
	
	
	
	public Area(int id,String name,Double centerX,Double centerY,String coordinates,String createDate,String createUser,String departCode,int status)
	{
		this.id = id;
		this.name = name;
		this.centerX = centerX;
		this.centerY = centerY;
		this.coordinates = coordinates;
		this.createDate = createDate;
		this.createUser = createUser;
		this.departCode = departCode;
		this.status = status;
	}
	public Area(){
		
	}
	public int getId()
	{
		return this.id;
	}
	public void setId(Integer value)
	{
		this.id = value;
	}
	
	public String getName()
	{
		return this.name;
	}
	public void setName(String value)
	{
		this.name = value;
	}
	
	public Double getCenterX()
	{
		return this.centerX;
	}
	public void setCenterX(Double value)
	{
		this.centerX = value;
	}
	
	public Double getCenterY()
	{
		return this.centerY;
	}
	public void setCenterY(Double value)
	{
		this.centerY = value;
	}
	
	public String getCoordinates()
	{
		return this.coordinates;
	}
	public void setCoordinates(String value)
	{
		this.coordinates = value;
	}
	
	public String getCreateDate()
	{
		return this.createDate;
	}
	public void setCreateDate(String value)
	{
		this.createDate = value;
	}
	
	public String getCreateUser()
	{
		return this.createUser;
	}
	public void setCreateUser(String value)
	{
		this.createUser = value;
	}
	
	public String getDepartCode()
	{
		return this.departCode;
	}
	public void setDepartCode(String value)
	{
		this.departCode = value;
	}
	public int getStatus() {
		return status;
	}	
	
	
	
}
