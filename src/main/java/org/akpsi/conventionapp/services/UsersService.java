package org.akpsi.conventionapp.services;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.akpsi.conventionapp.util.ConnectionFactory;
import org.akpsi.conventionapp.util.Constants;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersService {

	// http://stackoverflow.com/questions/18142745/how-do-i-generate-a-salt-in-java-for-salted-hash
	// https://www.owasp.org/index.php/Hashing_Java
	public static String hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) throws InvalidKeySpecException, NoSuchAlgorithmException {

		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
		SecretKey key = skf.generateSecret(spec);
		byte[] res = key.getEncoded( );
//		return res;
		return new String(res, StandardCharsets.UTF_8);
	}

	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public String createUser(
			@RequestParam (value = "email", required = false) String email,
			@RequestParam (value = "password", required = false) String password,
			@RequestParam (value = "phone_number", required = false) String phoneNumber,
			@RequestParam (value = "address", required = false) String address,
			@RequestParam (value = "city", required = false) String city,
			@RequestParam (value = "state", required = false) String state,
			@RequestParam (value = "zip", required = false) String zip
			) throws InvalidKeySpecException, NoSuchAlgorithmException {
		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.REGISTER_USER);
				) {
			String salt = generateSalt();
			ps.setString(1, email);
			ps.setString(2, salt);
			ps.setString(3, hashPassword(password.toCharArray(), salt.getBytes(), 1, 256));
			ps.setString(4, phoneNumber);
			ps.setString(5, address);
			ps.setString(6, city);
			ps.setString(7, state);
			ps.setString(8, zip);
			ps.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return "userCreated";
	}

	private String generateSalt() {
		return KeyGenerators.string().generateKey();
	}

}
