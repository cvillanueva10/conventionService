package org.akpsi.conventionapp.services;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.akpsi.conventionapp.objects.User;
import org.akpsi.conventionapp.util.ConnectionFactory;
import org.akpsi.conventionapp.util.Constants;

public class CommonServices {

	public static void clearSensitiveInformation(User actualUser) {
		actualUser.setUserId(null);
		actualUser.setPassword(null);
		actualUser.setEmail(null);
		actualUser.setPhoneNumber(null);
		actualUser.setAddress(null);
		actualUser.setCity(null);
		actualUser.setState(null);
		actualUser.setCountry(null);
		actualUser.setAllowEmail(null);
		actualUser.setAllowText(null);
		actualUser.setSalt(null);
	}
	
	public static boolean isValidSessionId(String sessionId){
		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.SQL_CHECK_IF_VALID_SESSION);
				){
			ps.setString(1, sessionId);
			try(ResultSet rs = ps.executeQuery()){
				while (rs.next()){
					int value = rs.getInt("cnt");
					if (value>0){
						return true;
					}else{
						return false;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String generateSession(User user) {
		SecureRandom random = new SecureRandom();
		String hash = new BigInteger(255, random).toString(32);

		try (
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.SQL_CREATE_NEW_SESSION);
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
