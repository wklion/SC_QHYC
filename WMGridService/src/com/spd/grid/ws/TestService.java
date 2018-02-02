package com.spd.grid.ws;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.spd.grid.jdbc.DataSource;
import com.spd.grid.model.CommonResult;

@Stateless
@Path("TestService")
public class TestService {
	@POST
   	@Path("getZsCsLastDate")
   	@Produces("application/json")
	public Object getZsCsLastDate(@FormParam("para") String para) throws Exception{
		CommonResult cr = new CommonResult();
		DataSource dataSource=DataSource.getBaseInstance();
		Connection conn=dataSource.getBaseConnection();
		String sql = "select max(publictime) from t_zscs";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			String strDate = rs.getString(1);
			cr.setSuc(strDate);
			break;
		}
		ps.close();
		conn.close();
		return cr;
	}
}
