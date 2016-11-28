package org.akpsi.conventionapp.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.akpsi.conventionapp.objects.Users;
import org.akpsi.conventionapp.util.ConnectionFactory;
import org.akpsi.conventionapp.util.Constants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersService {
	
	// http://stackoverflow.com/questions/18142745/how-do-i-generate-a-salt-in-java-for-salted-hash
	// https://www.owasp.org/index.php/Hashing_Java
	public static byte[] hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) throws InvalidKeySpecException, NoSuchAlgorithmException {
	   
	   SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
	   PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
	   SecretKey key = skf.generateSecret(spec);
	   byte[] res = key.getEncoded( );
	   return res;
   }
	
	@RequestMapping("/listUsers")
	public List<Users> getUser() {
		
		// LinkedList of User objects named 'usersList'
		List<Users> usersList = new LinkedList<Users>();
		
		try(
				Connection conn = ConnectionFactory.getConnection();
				Statement stmt = conn.createStatement();
				// calls query in string-form (GET_USERS) from Constants class
				ResultSet rs = stmt.executeQuery(Constants.GET_USERS);
				) {
			while (rs.next()) {
				Users user = new Users();
				user.setEmail(rs.getString("email"));
				//user.setSalt(rs.getString("salt"));
				user.setSalt(rs.getString(hashPassword("password", "salt", 1, 256));
				user.setPassword(rs.getString("password"));
				user.setPhone_number(rs.getString("phone_number"));
				user.setCreated_on(rs.getString("created_on"));
				user.setEdited_on(rs.getString("edited_on"));
				user.setAddress(rs.getString("address"));
				user.setCity(rs.getString("city"));
				user.setState(rs.getString("state"));
				user.setZip(rs.getString("zip"));
				// add new user info to list
				usersList.add(user);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return usersList;
	}

}
