package org.akpsi.conventionapp.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.akpsi.conventionapp.objects.Times;
import org.akpsi.conventionapp.util.ConnectionFactory;
import org.akpsi.conventionapp.util.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimesService {

	
	@RequestMapping(value = "/listTimes", method = RequestMethod.POST)
	public List<Times> getTime(@RequestBody(required=false) String sessionId){
		
		List<Times> times = new LinkedList<Times>();
		
		try(
				Connection conn = ConnectionFactory.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(Constants.GET_TIMES);
				){
			while (rs.next()){
				Times time = new Times();
				time.setActivity(rs.getString("activity"));
				time.setDate(rs.getString("date"));
				time.setDescription(rs.getString("description"));
				time.setTime(rs.getString("time"));
				if (sessionId!=null && !"{}".equals(sessionId)){
					time.setCanRegister(true);
				}else{
					time.setCanRegister(false);
				}
				times.add(time);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return times;
	}
	
}
