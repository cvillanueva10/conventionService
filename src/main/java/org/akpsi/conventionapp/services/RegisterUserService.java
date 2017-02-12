package org.akpsi.conventionapp.services;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.akpsi.conventionapp.objects.Country;
import org.akpsi.conventionapp.objects.Response;
import org.akpsi.conventionapp.objects.State;
import org.akpsi.conventionapp.objects.User;
import org.akpsi.conventionapp.util.ConnectionFactory;
import org.akpsi.conventionapp.util.Constants;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterUserService {

	public static String hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) throws InvalidKeySpecException, NoSuchAlgorithmException {

		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
		SecretKey key = skf.generateSecret(spec);
		byte[] res = key.getEncoded( );
		String strPass = new String(res, StandardCharsets.UTF_8);
		byte[] encodedPassword = Base64.encodeBase64(strPass.getBytes());
		return new String(encodedPassword);
	}

	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public Response createUser(@RequestBody User user) {
		
		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.REGISTER_USER);
				) {
			String salt = generateSalt();
			ps.setString(1, user.getFirstName());
			ps.setString(2, user.getLastName());
			ps.setString(3, user.getEmail());
			ps.setString(4, salt);
			ps.setString(5, hashPassword(user.getPassword().toCharArray(), salt.getBytes(), 1, 256));
			ps.setString(6, user.getPhoneNumber());
			ps.setString(7, user.getAddress());
			ps.setString(8, user.getCity());
			ps.setString(9, user.getState());
			ps.setString(10, user.getCountry());
			ps.setInt(11, Integer.parseInt(user.getAllowEmail()));
			ps.setInt(12, Integer.parseInt(user.getAllowText()));
			ps.execute();
		}
		catch(Exception e) {
			return new Response(false, "Error creating user");
		}
		return new Response(true);
	}

	private String generateSalt() {
		return new String(KeyGenerators.string().generateKey());
	}
	
	@RequestMapping("/getStates")
	public List<State> getStates(){
		List<State> states = new LinkedList<State>();
		try(
				Connection conn = ConnectionFactory.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(Constants.GET_STATES);
				){
			while (rs.next()){
				states.add(new State(rs.getString("name")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return states;
	}
	
	@RequestMapping("/getCountries")
	public List<Country> getCountries(){
		List<Country> countries = new LinkedList<Country>();
		try(
				Connection conn = ConnectionFactory.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(Constants.GET_COUNTRIES);
				){
			while (rs.next()){
				countries.add(new Country(rs.getString("name")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return countries;
	}

}
