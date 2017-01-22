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
import org.akpsi.conventionapp.util.ConnectionFactory;
import org.akpsi.conventionapp.util.Constants;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterUserService {

	public static String hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) throws InvalidKeySpecException, NoSuchAlgorithmException {

		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
		SecretKey key = skf.generateSecret(spec);
		byte[] res = key.getEncoded( );
		return new String(res, StandardCharsets.UTF_8);
	}

	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public Response createUser(
			@RequestParam (value = "email", required = false) String email,
			@RequestParam (value = "password", required = false) String password,
			@RequestParam (value = "cell", required = false) String cell,
			@RequestParam (value = "address", required = false) String address,
			@RequestParam (value = "city", required = false) String city,
			@RequestParam (value = "state", required = false) String state,
			@RequestParam (value = "zip", required = false) String zip
			) {
		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.REGISTER_USER);
				) {
			String salt = generateSalt();
			ps.setString(1, email);
			ps.setString(2, salt);
			ps.setString(3, hashPassword(password.toCharArray(), salt.getBytes(), 1, 256));
			ps.setString(4, cell);
			ps.setString(5, address);
			ps.setString(6, city);
			ps.setString(7, state);
			ps.setString(8, zip);
			ps.execute();
		}
		catch(Exception e) {
			return new Response(false, "Error creating user");
		}
		return new Response(true);
	}

	private String generateSalt() {
		return KeyGenerators.string().generateKey();
	}
	
	@RequestMapping("/GetStates")
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
	
	@RequestMapping("/GetCountries")
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
