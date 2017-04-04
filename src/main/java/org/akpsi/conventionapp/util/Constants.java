package org.akpsi.conventionapp.util;

public class Constants {

	public static final String SQL_GET_TIMES = "select time_id, date, time, activity, description from times order by date, time asc";
	
	public static final String SQL_GET_USERS = "SELECT email, salt, password, phone_number, created_on, edited_on, address, city, state, zip FROM users GROUP BY state, city ORDER BY email ASC";
	
	public static final String SQL_REGISTER_USER = "INSERT INTO `users` (`first_name`,`last_name`,`email`, `salt`, `password`, `phone_number`, `address`, `city`, `state`, `country`,`allowEmail`,`allowText`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public static final String SQL_GET_STATES = "SELECT name from states order by `order`, name";

	public static final String SQL_GET_COUNTRIES = "SELECT name from countries order by `order`,name";

	public static final String SQL_USER_LOGIN = "SELECT first_name, last_name, userId, salt, password, role from users where email = ?";

	public static final String SQL_CREATE_NEW_SESSION = "insert into sessions (userId, session, expires_on) VALUES (?,?,NOW() + INTERVAL 1 DAY) ";

	public static final String SQL_CHECK_FOR_USER = "SELECT count(email) from `users` where email=?";
	
	public final static String EMAIL_SERVICE_URL = "http://convention.adrianacala.com:8004/";

	public static final String SQL_USER_LOGOUT = "delete from sessions where session = ?";

	public static final String SQL_CHECK_IF_VALID_SESSION = "select count(session) as `cnt` from sessions where session = ?";
	
	public static final String FORGOT_PASSWORD_ENTRY = "INSERT INTO 'forgot_password' ('user_id','token') VALUES (?, ?)";
}
