package org.akpsi.conventionapp.util;

public class Constants {

	public static final String GET_TIMES = "select date, time, activity, description from times order by date, time asc";
	
	public static final String GET_USERS = "SELECT email, salt, password, phone_number, created_on, edited_on, address, city, state, zip FROM users GROUP BY state, city ORDER BY email ASC";
	
	public static final String REGISTER_USER = "INSERT INTO `users` (`email`, `salt`, `password`, `phone_number`, `address`, `city`, `state`, `country`) VALUES (?,?,?,?,?,?,?,?)";
	
	public static final String GET_STATES = "SELECT name from states order by `order`, name";

	public static final String GET_COUNTRIES = "SELECT name from countries order by `order`,name";
}
