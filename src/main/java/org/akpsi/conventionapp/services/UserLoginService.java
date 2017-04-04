package org.akpsi.conventionapp.services;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.akpsi.conventionapp.objects.Response;
import org.akpsi.conventionapp.objects.Session;
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
	
	public static String hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) throws InvalidKeySpecException, NoSuchAlgorithmException {

		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
		SecretKey key = skf.generateSecret(spec);
		byte[] res = key.getEncoded( );
		String strPass =  new String(res, StandardCharsets.UTF_8);
		byte[] encodedPassword = Base64.encodeBase64(strPass.getBytes());
		return new String(encodedPassword);
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public Response logout(@RequestBody Session session) {
		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.SQL_USER_LOGOUT)
				){
			ps.setString(1, session.getSession());
			ps.execute();
		} catch (SQLException e) {
			return new Response(false);
		}
		return new Response(true);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Response userLogin(@RequestBody User user) {
		User actualUser = new User();
		String actualPassword = null;
		String actualSalt = null;
		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.SQL_USER_LOGIN);
				){
			ps.setString(1, user.getEmail());
			try(ResultSet rs = ps.executeQuery()){
				while (rs.next()){
					actualUser.setFirstName(rs.getString("first_name"));
					actualUser.setLastName(rs.getString("last_name"));
					actualUser.setUserId(rs.getString("userId"));
					actualPassword = new String(rs.getBytes("password"), StandardCharsets.UTF_8);
					actualSalt = rs.getString("salt");
					actualUser.setUserRole(rs.getInt("role"));
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
			actualUser.setSessionId(CommonServices.generateSession(actualUser));
			CommonServices.clearSensitiveInformation(actualUser);
			return new Response(true, actualUser.serialize(false));
		}
		return new Response(false, "The username and/or password is incorrect.");
	}	
}