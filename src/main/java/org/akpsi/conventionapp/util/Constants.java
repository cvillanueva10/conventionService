package org.akpsi.conventionapp.util;

public class Constants {

	public static final String GET_TIMES = "select date, time, activity, description from times order by date, time asc";
	
	public static final String GET_USERS = "SELECT email, salt, password, phone_number, created_on, edited_on, address, city, state, zip FROM users GROUP BY state, city ORDER BY email ASC";
	
	public static final String REGISTER_USER = "INSERT INTO `users` (`first_name`,`last_name`,`email`, `salt`, `password`, `phone_number`, `address`, `city`, `state`, `country`,`allowEmail`,`allowText`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public static final String GET_STATES = "SELECT name from states order by `order`, name";

	public static final String GET_COUNTRIES = "SELECT name from countries order by `order`,name";

	public static final String USER_LOGIN = "SELECT first_name, last_name, userId, salt, password from users where email = ?";

	public static final String CREATE_NEW_SESSION = "insert into sessions (userId, session, expires_on) VALUES (?,?,NOW() + INTERVAL 1 DAY) ";

	public static final String CHECK_FOR_USER = "SELECT count(email) from `users` where email=?";
}
