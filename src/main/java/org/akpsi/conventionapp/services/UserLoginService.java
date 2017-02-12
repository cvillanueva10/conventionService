package org.akpsi.conventionapp.services;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akpsi.conventionapp.objects.Response;
import org.akpsi.conventionapp.objects.User;
import org.akpsi.conventionapp.util.ConnectionFactory;
import org.akpsi.conventionapp.util.Constants;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserLoginService {

	private final int SECONDS_IN_A_DAY = 86400;
	
	public static String hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) throws InvalidKeySpecException, NoSuchAlgorithmException {

		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
		SecretKey key = skf.generateSecret(spec);
		byte[] res = key.getEncoded( );
		String strPass =  new String(res, StandardCharsets.UTF_8);
		byte[] encodedPassword = Base64.encodeBase64(strPass.getBytes());
		return new String(encodedPassword);
	}
	
	@RequestMapping(value = "/auth/login", method = RequestMethod.GET)
	public Response getUser(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies!=null){
			for (Cookie cookie : cookies){
				System.out.println(cookie.toString());
			}
		}
		Response resp = new Response(true);
		return resp;
	}
	
	@RequestMapping(value = "/auth/login", method = RequestMethod.POST)
	public Response createUser(@RequestBody User user, HttpServletResponse response) {
		User actualUser = new User();
		String actualPassword = null;
		String actualSalt = null;
		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.USER_LOGIN);
				){
			ps.setString(1, user.getEmail());
			try(ResultSet rs = ps.executeQuery()){
				while (rs.next()){
					actualUser.setFirstName(rs.getString("first_name"));
					actualUser.setLastName(rs.getString("last_name"));
					actualUser.setUserId(rs.getString("userId"));
					actualPassword = new String(rs.getBytes("password"), StandardCharsets.UTF_8);
					actualSalt = rs.getString("salt");
					break;
				}
			}
			
		} catch (SQLException e) {
			return new Response(false);
		}
		if (actualSalt==null) return new Response(false);
		String hashedUserPassword = null;
		try {
			hashedUserPassword = hashPassword(user.getPassword().toCharArray(), actualSalt.getBytes(), 1, 256);
		} catch (Exception e){
			return new Response(false);
		}
		if (actualPassword.equals(hashedUserPassword)){
			String sessionId = generateSession(actualUser);
			Cookie cookie = new Cookie("JSESSIONID", sessionId);
			cookie.setMaxAge(SECONDS_IN_A_DAY);
			cookie.setSecure(true);
			cookie.setPath("/");
			response.addCookie(cookie);
			return new Response(true, actualUser.serialize(false));
		}
		return new Response(false, "The username and/or password is incorrect.");
	}

	private String generateSession(User user) {
		SecureRandom random = new SecureRandom();
		String hash = new BigInteger(255, random).toString(32);

		try (
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.CREATE_NEW_SESSION);
				){
			ps.setString(1, user.getUserId());
			ps.setString(2, hash);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hash;
	}

}
