package org.akpsi.conventionapp.util;

public class Constants {

	public static final String GET_TIMES = "select date, time, activity, description from times order by date, time asc";

	public static final String GET_USERS = "SELECT email, salt, password, phone_number, created_on, edited_on, address, city, state, zip FROM users GROUP BY state, city ORDER BY email ASC";

	public static final String REGISTER_USER = "INSERT INTO `users` (`email`, `salt`, `password`, `phone_number`, `address`, `city`, `state`, `zip`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	
	public static final String CREATE_MEETING = "INSERT INTO 'meetingtime' ('name', 'description', 'location', 'date', 'time') VALUES (?, ?, ?, ?, ?)";

}
