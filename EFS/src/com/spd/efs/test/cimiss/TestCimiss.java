package com.spd.efs.test.cimiss;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class TestCimiss {
	
	private static List<Client> pool = new ArrayList<Client>();
	
	static {
		for(int i=0; i<10; i++) {
			pool.add(Client.create());
		}
	}
	
	
   
	
	
	
	public static String callCIMISS(String url) {
		Client client = Client.create();
	    WebResource webResource = client.resource(url); 
	    ClientResponse response = webResource.type("application/x-www-form-urlencoded").post(ClientResponse.class);
	    int status = response.getStatus();
	    if (status == 200) {
	    	return response.getEntity(String.class);
	    }
    	String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
	    return "";
	}
	
	

	
    /**
     * Cimiss 中国地面日值资料
     * @param url
     * @param paramName
     * @param param
     * @return
     */ 
      @Test
	  public void testCimiss(){
		  String url = "http://10.194.89.55/cimiss-web/api?userId=BCCD_QHZX_PJS&pwd=pjs20160106";
		  String param = "&interfaceId=getSurfEleByTime" +  
				         "&dataCode=SURF_CHN_MUL_HOR"+"&elements=Station_ID_C,PRE_1h,PRS,RHU,VIS,WIN_S_Avg_2mi,WIN_D_Avg_2mi,Q_PRS"+ "&times=20160317000000" + "&limitCnt=10" + "&dataFormat=json" ; /* 1.4 序列化格式 */
		  System.out.println(callCIMISS(url+param));
		  
	  }
	
	
	
	
	
}
