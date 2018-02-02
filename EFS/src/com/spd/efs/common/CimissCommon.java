package com.spd.efs.common;

import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CimissCommon {
	
	
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
	
	

}
