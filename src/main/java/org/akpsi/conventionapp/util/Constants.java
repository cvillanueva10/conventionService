package org.akpsi.conventionapp.util;

public class Constants {

	public static final String SQL_GET_TIMES = "select time_id, date, time, activity, description from times order by date, time asc";
	
	public static final String SQL_GET_USERS = "SELECT email, salt, password, phone_number, created_on, edited_on, address, city, state, zip FROM users GROUP BY state, city ORDER BY email ASC";
	
	public static final String SQL_REGISTER_USER = "INSERT INTO `users` (`first_name`,`last_name`,`email`, `salt`, `password`, `phone_number`, `address`, `city`, `state`, `country`,`allowEmail`,`allowText`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public static final String SQL_GET_STATES = "SELECT name from states order by `order`, name";

	public static final String SQL_GET_COUNTRIES = "SELECT name from countries order by `order`,name";

	public static final String SQL_USER_LOGIN = "SELECT first_name, last_name, userId, salt, password, role from users where email = ?";

	public static final String SQL_CREATE_NEW_SESSION = "insert into sessions (userId, session, expires_on) VALUES (?,?,NOW() + INTERVAL 1 DAY) ";

	public static final String SQL_CHECK_FOR_USER = "SELECT count(email) as `cnt` from `users` where email=?";
	
	public final static String EMAIL_SERVICE_URL = "http://convention.adrianacala.com:8004/";

	public static final String SQL_USER_LOGOUT = "delete from sessions where session = ?";

	public static final String SQL_CHECK_IF_VALID_SESSION = "select count(session) as `cnt` from sessions where session = ?";
	
	public static final String EMAIL_API_KEY = "4hJctCGcfUNkEkJh9mH42yQ3Q9WHQhLv";
	
	public static final String EMAIL_REGISTRATION_SUBJECT = "AKPsi Convention Volunteering Registration Complete";
	
	public static final String EMAIL_REGISTRATION_BODY = "Congratulations! You have successfully registered to volunteer for Convention!";
	
	public static final String SQL_FORGOT_PASSWORD_ENTRY = "INSERT INTO `forgot_password` (`user_id`,`token`) VALUES ((SELECT `userId` FROM `users` WHERE email = ?), ?)";
	
	public static final String SQL_CHECK_RESET_PASSWORD_SESSION = "select count(token) as 'cnt' from forgot_password where token = ? AND NOW() <= date_expiration";
	
	public static final String SQL_RESET_PASSWORD = "UPDATE users SET password = ? WHERE userId = ?";
	
	public static final String SQL_INSERT_OLD_PASSWORD = "INSERT INTO 'old_passwords' ('userId, salt, password) VALUES (?,?,?)";
	
	public static final String SQL_CHECK_OLD_PASSWORDS = "select count(password) as 'cnt' from old_passwords where userId = ? and password - ?";

	public static final String EMAIL_FORGOT_PASSWORD_SUBJECT = "Forgot Password - Link to reset password";
	
	public static final String EMAIL_FORGOT_PASSWORD_MESSAGE = "<a href=\"http://localhost:8080/Convention/forgot_password?token=" + /*token */  "\"> Click here to reset your password </a>";
}
