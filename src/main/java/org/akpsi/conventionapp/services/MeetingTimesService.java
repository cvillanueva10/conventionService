package org.akpsi.conventionapp.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

import org.akpsi.conventionapp.util.ConnectionFactory;
import org.akpsi.conventionapp.util.Constants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



public class MeetingTimesService {

	@RequestMapping(value = "/CreateMeeting", method = RequestMethod.POST)
	public String createMeeting(
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "date", required = false) Date date,
			@RequestParam(value = "time", required = false) Time time
			){
		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.CREATE_MEETING);
				){
					ps.setString(1, name);
					ps.setString(2, description);
					ps.setString(3, location);
					ps.setDate(4, date);
					ps.setTime(5, time);
					ps.execute();
					
				}catch(SQLException e){
					e.printStackTrace();
				}
		
		return "Meeting Created";
	}
	
}
