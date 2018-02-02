package com.spd.hmsas;

import java.math.BigInteger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * 
 */
@WebService(name = "hmsasserverPortType", targetNamespace = "urn:hmsas")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface HmsasserverPortType {

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param abnormalflag
	 * @param username
	 * @param userpass
	 * @param quyunamestr0
	 * @param stanumstr0
	 * @param elementstr0
	 * @param enddate0
	 * @param begdate0
	 * @return returns java.lang.Object
	 */
	@WebMethod(operationName = "get_awsqcdata_toarray")
	@WebResult(name = "get_awsqcdata_toarrayReturn", partName = "get_awsqcdata_toarrayReturn")
	public Object getAwsqcdataToarray(
			@WebParam(name = "begdate0", partName = "begdate0") String begdate0,
			@WebParam(name = "enddate0", partName = "enddate0") String enddate0,
			@WebParam(name = "quyunamestr0", partName = "quyunamestr0") String quyunamestr0,
			@WebParam(name = "elementstr0", partName = "elementstr0") String elementstr0,
			@WebParam(name = "stanumstr0", partName = "stanumstr0") String stanumstr0,
			@WebParam(name = "abnormalflag", partName = "abnormalflag") String abnormalflag,
			@WebParam(name = "username", partName = "username") String username,
			@WebParam(name = "userpass", partName = "userpass") String userpass);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param username
	 * @param datatable0
	 * @param userpass
	 * @param stanumstr0
	 * @param elementstr0
	 * @param enddate0
	 * @param begdate0
	 * @return returns java.lang.Object
	 */
	@WebMethod(operationName = "getdatatoarray_addqc")
	@WebResult(name = "getdatatoarray_addqcReturn", partName = "getdatatoarray_addqcReturn")
	public Object getdatatoarrayAddqc(
			@WebParam(name = "datatable0", partName = "datatable0") String datatable0,
			@WebParam(name = "begdate0", partName = "begdate0") String begdate0,
			@WebParam(name = "enddate0", partName = "enddate0") String enddate0,
			@WebParam(name = "elementstr0", partName = "elementstr0") String elementstr0,
			@WebParam(name = "stanumstr0", partName = "stanumstr0") String stanumstr0,
			@WebParam(name = "username", partName = "username") String username,
			@WebParam(name = "userpass", partName = "userpass") String userpass);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param username
	 * @param datatable0
	 * @param userpass
	 * @param stanumstr0
	 * @param elementstr0
	 * @param enddate0
	 * @param begdate0
	 * @return returns java.lang.Object
	 */
	@WebMethod
	@WebResult(name = "getdatatoarrayReturn", partName = "getdatatoarrayReturn")
	public Object getdatatoarray(
			@WebParam(name = "datatable0", partName = "datatable0") String datatable0,
			@WebParam(name = "begdate0", partName = "begdate0") String begdate0,
			@WebParam(name = "enddate0", partName = "enddate0") String enddate0,
			@WebParam(name = "elementstr0", partName = "elementstr0") String elementstr0,
			@WebParam(name = "stanumstr0", partName = "stanumstr0") String stanumstr0,
			@WebParam(name = "username", partName = "username") String username,
			@WebParam(name = "userpass", partName = "userpass") String userpass);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param username
	 * @param orderbystr
	 * @param datatable0
	 * @param userpass
	 * @param stanumstr0
	 * @param elementstr0
	 * @param enddate0
	 * @param conditionstr
	 * @param begdate0
	 * @return returns java.lang.Object
	 */
	@WebMethod(operationName = "getdatatoarray_for_condition")
	@WebResult(name = "getdatatoarray_for_conditionReturn", partName = "getdatatoarray_for_conditionReturn")
	public Object getdatatoarrayForCondition(
			@WebParam(name = "datatable0", partName = "datatable0") String datatable0,
			@WebParam(name = "begdate0", partName = "begdate0") String begdate0,
			@WebParam(name = "enddate0", partName = "enddate0") String enddate0,
			@WebParam(name = "elementstr0", partName = "elementstr0") String elementstr0,
			@WebParam(name = "stanumstr0", partName = "stanumstr0") String stanumstr0,
			@WebParam(name = "conditionstr", partName = "conditionstr") String conditionstr,
			@WebParam(name = "orderbystr", partName = "orderbystr") String orderbystr,
			@WebParam(name = "username", partName = "username") String username,
			@WebParam(name = "userpass", partName = "userpass") String userpass);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param rainelement
	 * @param raindatatable
	 * @param rainuserpass
	 * @param rainusername
	 * @param rainbegdate
	 * @param rainenddate
	 * @param rainstanum
	 * @return returns java.lang.Object
	 */
	@WebMethod
	@WebResult(name = "rainaddupdataReturn", partName = "rainaddupdataReturn")
	public Object rainaddupdata(
			@WebParam(name = "raindatatable", partName = "raindatatable") String raindatatable,
			@WebParam(name = "rainbegdate", partName = "rainbegdate") String rainbegdate,
			@WebParam(name = "rainenddate", partName = "rainenddate") String rainenddate,
			@WebParam(name = "rainelement", partName = "rainelement") String rainelement,
			@WebParam(name = "rainstanum", partName = "rainstanum") String rainstanum,
			@WebParam(name = "rainusername", partName = "rainusername") String rainusername,
			@WebParam(name = "rainuserpass", partName = "rainuserpass") String rainuserpass);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param minusername
	 * @param mindatatable
	 * @param minstanum
	 * @param minbegdate
	 * @param minelement
	 * @param minenddate
	 * @param minuserpass
	 * @return returns java.lang.Object
	 */
	@WebMethod(operationName = "minvalue_elementdata")
	@WebResult(name = "minvalue_elementdataReturn", partName = "minvalue_elementdataReturn")
	public Object minvalueElementdata(
			@WebParam(name = "mindatatable", partName = "mindatatable") String mindatatable,
			@WebParam(name = "minbegdate", partName = "minbegdate") String minbegdate,
			@WebParam(name = "minenddate", partName = "minenddate") String minenddate,
			@WebParam(name = "minelement", partName = "minelement") String minelement,
			@WebParam(name = "minstanum", partName = "minstanum") String minstanum,
			@WebParam(name = "minusername", partName = "minusername") String minusername,
			@WebParam(name = "minuserpass", partName = "minuserpass") String minuserpass);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param maxelement
	 * @param maxstanum
	 * @param maxusername
	 * @param maxbegdate
	 * @param maxdatatable
	 * @param maxenddate
	 * @param maxuserpass
	 * @return returns java.lang.Object
	 */
	@WebMethod(operationName = "maxvalue_elementdata")
	@WebResult(name = "maxvalue_elementdataReturn", partName = "maxvalue_elementdataReturn")
	public Object maxvalueElementdata(
			@WebParam(name = "maxdatatable", partName = "maxdatatable") String maxdatatable,
			@WebParam(name = "maxbegdate", partName = "maxbegdate") String maxbegdate,
			@WebParam(name = "maxenddate", partName = "maxenddate") String maxenddate,
			@WebParam(name = "maxelement", partName = "maxelement") String maxelement,
			@WebParam(name = "maxstanum", partName = "maxstanum") String maxstanum,
			@WebParam(name = "maxusername", partName = "maxusername") String maxusername,
			@WebParam(name = "maxuserpass", partName = "maxuserpass") String maxuserpass);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param avgstanum
	 * @param avgenddate
	 * @param avgbegdate
	 * @param avgdatatable
	 * @param avguserpass
	 * @param avgelement
	 * @param avgusername
	 * @return returns java.lang.Object
	 */
	@WebMethod(operationName = "avgvalue_elementdata")
	@WebResult(name = "avgvalue_elementdataReturn", partName = "avgvalue_elementdataReturn")
	public Object avgvalueElementdata(
			@WebParam(name = "avgdatatable", partName = "avgdatatable") String avgdatatable,
			@WebParam(name = "avgbegdate", partName = "avgbegdate") String avgbegdate,
			@WebParam(name = "avgenddate", partName = "avgenddate") String avgenddate,
			@WebParam(name = "avgelement", partName = "avgelement") String avgelement,
			@WebParam(name = "avgstanum", partName = "avgstanum") String avgstanum,
			@WebParam(name = "avgusername", partName = "avgusername") String avgusername,
			@WebParam(name = "avguserpass", partName = "avguserpass") String avguserpass);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param downfilename1
	 * @param enddate1
	 * @param fileuserpassweb
	 * @param datatable1
	 * @param fileusernameweb
	 * @param elementstr1
	 * @param begdate1
	 * @param stanumstr1
	 * @return returns java.lang.String
	 */
	@WebMethod
	@WebResult(name = "getdatatofileforwebReturn", partName = "getdatatofileforwebReturn")
	public String getdatatofileforweb(
			@WebParam(name = "datatable1", partName = "datatable1") String datatable1,
			@WebParam(name = "begdate1", partName = "begdate1") String begdate1,
			@WebParam(name = "enddate1", partName = "enddate1") String enddate1,
			@WebParam(name = "elementstr1", partName = "elementstr1") String elementstr1,
			@WebParam(name = "stanumstr1", partName = "stanumstr1") String stanumstr1,
			@WebParam(name = "downfilename1", partName = "downfilename1") String downfilename1,
			@WebParam(name = "fileusernameweb", partName = "fileusernameweb") String fileusernameweb,
			@WebParam(name = "fileuserpassweb", partName = "fileuserpassweb") String fileuserpassweb);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param endtime
	 * @param limitnum
	 * @param flashpassword
	 * @param elefields
	 * @param begintime
	 * @param flashuser
	 * @param datatable
	 * @return returns java.lang.Object
	 */
	@WebMethod
	@WebResult(name = "getflashdataReturn", partName = "getflashdataReturn")
	public Object getflashdata(
			@WebParam(name = "datatable", partName = "datatable") String datatable,
			@WebParam(name = "begintime", partName = "begintime") String begintime,
			@WebParam(name = "endtime", partName = "endtime") String endtime,
			@WebParam(name = "elefields", partName = "elefields") String elefields,
			@WebParam(name = "limitnum", partName = "limitnum") String limitnum,
			@WebParam(name = "flashuser", partName = "flashuser") String flashuser,
			@WebParam(name = "flashpassword", partName = "flashpassword") String flashpassword);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param stanumstr
	 * @param minlat
	 * @param quyustr
	 * @param elenumstr
	 * @param provincestr
	 * @param maxlat
	 * @param statypestr
	 * @param maxlon
	 * @param minlon
	 * @param fieldstr
	 * @param updatatime
	 * @return returns java.lang.Object
	 */
	@WebMethod
	@WebResult(name = "getstationinfoReturn", partName = "getstationinfoReturn")
	public Object getstationinfo(
			@WebParam(name = "provincestr", partName = "provincestr") String provincestr,
			@WebParam(name = "statypestr", partName = "statypestr") String statypestr,
			@WebParam(name = "quyustr", partName = "quyustr") String quyustr,
			@WebParam(name = "elenumstr", partName = "elenumstr") String elenumstr,
			@WebParam(name = "stanumstr", partName = "stanumstr") String stanumstr,
			@WebParam(name = "fieldstr", partName = "fieldstr") String fieldstr,
			@WebParam(name = "updatatime", partName = "updatatime") String updatatime,
			@WebParam(name = "minlon", partName = "minlon") String minlon,
			@WebParam(name = "maxlon", partName = "maxlon") String maxlon,
			@WebParam(name = "minlat", partName = "minlat") String minlat,
			@WebParam(name = "maxlat", partName = "maxlat") String maxlat);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param warnpasswd
	 * @param warnelementstr
	 * @param elementvalue
	 * @param warnstationnum
	 * @param warnbegintime
	 * @param warnuser
	 * @param warncode
	 * @param warnlevel
	 * @param warnendtime
	 * @return returns java.lang.Object
	 */
	@WebMethod
	@WebResult(name = "getwarninfoReturn", partName = "getwarninfoReturn")
	public Object getwarninfo(
			@WebParam(name = "warncode", partName = "warncode") String warncode,
			@WebParam(name = "warnbegintime", partName = "warnbegintime") String warnbegintime,
			@WebParam(name = "warnendtime", partName = "warnendtime") String warnendtime,
			@WebParam(name = "warnstationnum", partName = "warnstationnum") String warnstationnum,
			@WebParam(name = "elementvalue", partName = "elementvalue") String elementvalue,
			@WebParam(name = "warnlevel", partName = "warnlevel") String warnlevel,
			@WebParam(name = "warnelementstr", partName = "warnelementstr") String warnelementstr,
			@WebParam(name = "warnuser", partName = "warnuser") String warnuser,
			@WebParam(name = "warnpasswd", partName = "warnpasswd") String warnpasswd);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param vaporDensityflag
	 * @param stanumstr
	 * @param username
	 * @param temperatureflag
	 * @param cengstr
	 * @param begdate
	 * @param userpass
	 * @param liquidflag
	 * @param relativeHumidityflag
	 * @param enddate
	 * @return returns java.lang.Object
	 */
	@WebMethod(operationName = "getradiometricsdata_fenceng")
	@WebResult(name = "getradiometricsdata_fencengReturn", partName = "getradiometricsdata_fencengReturn")
	public Object getradiometricsdataFenceng(
			@WebParam(name = "stanumstr", partName = "stanumstr") String stanumstr,
			@WebParam(name = "begdate", partName = "begdate") String begdate,
			@WebParam(name = "enddate", partName = "enddate") String enddate,
			@WebParam(name = "cengstr", partName = "cengstr") String cengstr,
			@WebParam(name = "Temperatureflag", partName = "Temperatureflag") BigInteger temperatureflag,
			@WebParam(name = "VaporDensityflag", partName = "VaporDensityflag") BigInteger vaporDensityflag,
			@WebParam(name = "Liquidflag", partName = "Liquidflag") BigInteger liquidflag,
			@WebParam(name = "RelativeHumidityflag", partName = "RelativeHumidityflag") BigInteger relativeHumidityflag,
			@WebParam(name = "username", partName = "username") String username,
			@WebParam(name = "userpass", partName = "userpass") String userpass);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param datatable
	 * @return returns java.lang.Object
	 */
	@WebMethod
	@WebResult(name = "getlatestdataReturn", partName = "getlatestdataReturn")
	public Object getlatestdata(
			@WebParam(name = "datatable", partName = "datatable") String datatable);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param fieldtable
	 * @param fieldstr
	 * @return returns java.lang.Object
	 */
	@WebMethod
	@WebResult(name = "gettablefieldsinfoReturn", partName = "gettablefieldsinfoReturn")
	public Object gettablefieldsinfo(
			@WebParam(name = "fieldtable", partName = "fieldtable") String fieldtable,
			@WebParam(name = "fieldstr", partName = "fieldstr") String fieldstr);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param username
	 * @param datatable0
	 * @param userpass
	 * @param enddate0
	 * @param begdate0
	 * @return returns java.lang.String
	 */
	@WebMethod(operationName = "check_user_and_type_and_date_beg_end_legalornot")
	@WebResult(name = "check_user_and_type_and_date_beg_end_legalornotReturn", partName = "check_user_and_type_and_date_beg_end_legalornotReturn")
	public String checkUserAndTypeAndDateBegEndLegalornot(
			@WebParam(name = "datatable0", partName = "datatable0") String datatable0,
			@WebParam(name = "begdate0", partName = "begdate0") String begdate0,
			@WebParam(name = "enddate0", partName = "enddate0") String enddate0,
			@WebParam(name = "username", partName = "username") String username,
			@WebParam(name = "userpass", partName = "userpass") String userpass);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param nowdatestorelimit
	 * @param alldbtbnamestr
	 * @param datatable0
	 * @param dbnamedatadb
	 * @param enddate0
	 * @param begdate0
	 * @return returns java.lang.String
	 */
	@WebMethod(operationName = "combination_dbname_tbname_to_string")
	@WebResult(name = "combination_dbname_tbname_to_stringReturn", partName = "combination_dbname_tbname_to_stringReturn")
	public String combinationDbnameTbnameToString(
			@WebParam(name = "datatable0", partName = "datatable0") String datatable0,
			@WebParam(name = "begdate0", partName = "begdate0") String begdate0,
			@WebParam(name = "enddate0", partName = "enddate0") String enddate0,
			@WebParam(name = "dbnamedatadb", partName = "dbnamedatadb") String dbnamedatadb,
			@WebParam(name = "nowdatestorelimit", partName = "nowdatestorelimit") String nowdatestorelimit,
			@WebParam(name = "alldbtbnamestr", partName = "alldbtbnamestr") String alldbtbnamestr);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param staname1Str
	 * @param quyuname1Str
	 * @param monnum1Str
	 * @param field1Str
	 * @param stanum1Str
	 * @return returns java.lang.Object
	 */
	@WebMethod(operationName = "get_mon_extremevalue")
	@WebResult(name = "get_mon_extremevalueReturn", partName = "get_mon_extremevalueReturn")
	public Object getMonExtremevalue(
			@WebParam(name = "quyuname1str", partName = "quyuname1str") String quyuname1Str,
			@WebParam(name = "staname1str", partName = "staname1str") String staname1Str,
			@WebParam(name = "stanum1str", partName = "stanum1str") String stanum1Str,
			@WebParam(name = "monnum1str", partName = "monnum1str") String monnum1Str,
			@WebParam(name = "field1str", partName = "field1str") String field1Str);

	/**
	 * 
	 * Enter description here...
	 * 
	 * 
	 * @param fieldsstr
	 * @param searchconditionstr
	 * @param provcodestr
	 * @param countynamestr
	 * @param hdangerdnamestr
	 * @return returns java.lang.Object
	 */
	@WebMethod(operationName = "get_sx_dflow_hdanger")
	@WebResult(name = "get_sx_dflow_hdangerReturn", partName = "get_sx_dflow_hdangerReturn")
	public Object getSxDflowHdanger(
			@WebParam(name = "provcodestr", partName = "provcodestr") String provcodestr,
			@WebParam(name = "hdangerdnamestr", partName = "hdangerdnamestr") String hdangerdnamestr,
			@WebParam(name = "countynamestr", partName = "countynamestr") String countynamestr,
			@WebParam(name = "searchconditionstr", partName = "searchconditionstr") String searchconditionstr,
			@WebParam(name = "fieldsstr", partName = "fieldsstr") String fieldsstr);

}