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

import org.akpsi.conventionapp.objects.User;
import org.akpsi.conventionapp.util.ConnectionFactory;
import org.akpsi.conventionapp.util.Constants;
import org.apache.commons.codec.binary.Base64;

public class CommonServices {

	public static String hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) throws InvalidKeySpecException, NoSuchAlgorithmException {

		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
		SecretKey key = skf.generateSecret(spec);
		byte[] res = key.getEncoded( );
		String strPass = new String(res, StandardCharsets.UTF_8);
		byte[] encodedPassword = Base64.encodeBase64(strPass.getBytes());
		return new String(encodedPassword);
	}
	
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
	
	public static boolean userExist(String email){
		try(Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.SQL_CHECK_FOR_USER)){
			int numOfUserWithEmail = Integer.MIN_VALUE;
			ps.setString(1, email);
			try(ResultSet rs = ps.executeQuery()){
				if (rs.next()){
					numOfUserWithEmail = rs.getInt("cnt");
				}
			}
			if (numOfUserWithEmail==1){
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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
