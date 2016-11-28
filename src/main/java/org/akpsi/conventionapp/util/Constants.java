package org.akpsi.conventionapp.util;

public class Constants {

	public static final String GET_TIMES = "select date, time, activity, description from times order by date, time asc";
	
	public static final String GET_USERS = "SELECT email, salt, password, phone_number, created_on, edited_on, address, city, state, zip FROM users GROUP BY state, city ORDER BY email ASC"; 
	
}
