package org.akpsi.conventionapp.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import org.akpsi.conventionapp.objects.Response;
import org.akpsi.conventionapp.objects.User;
import org.akpsi.conventionapp.util.ConnectionFactory;
import org.akpsi.conventionapp.util.Constants;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ForgotPasswordService {


	@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
	public Response forgotPassword(@RequestBody User user, HttpServletResponse response) {

		if (!CommonServices.userExist(user.getEmail())){
			return new Response(false,"No user is registered under this email.");
		}

		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.SQL_FORGOT_PASSWORD_ENTRY);
				){
			String token = generateToken();
			ps.setString(1,user.getEmail());
			ps.setString(2, token);
			ps.execute();

			insertOldPassword(user);

			HttpHeaders headers = new HttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("apiKey", "4hJctCGcfUNkEkJh9mH42yQ3Q9WHQhLv");
			map.add("toAddress", user.getEmail());
			map.add("subject", "Forgot Password - Link to reset password");
			map.add("message", "<a href=\"http://localhost:8080/Convention/forgot_password?token=" + token + "\"> Click here to reset your password </a>");

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

			restTemplate.postForEntity(Constants.EMAIL_SERVICE_URL , request , String.class );

			return new Response(true, "Forgot Password Session Started");

		}catch(Exception e) {
			return new Response(false, "Error creating session");
		}

	}

	@RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
	public Response resetPassword(@RequestBody User user, HttpServletResponse response){
		boolean isOldPassword = false;
		boolean valid = isValidResetToken(user.getSessionId());
		if(!valid){
			return new Response(false, "Session has expired");
		}

		String salt = user.getSalt();
		try {
			String hashedNewPassword = RegisterUserService.hashPassword(user.getPassword().toCharArray(), salt.getBytes(), 1, 256);
			isOldPassword = checkIfOldPassword(user, hashedNewPassword);
		} catch (Exception e){
			return new Response(false, "Error checking old passwords");
		}
		if(isOldPassword){
			return new Response(false, "This is an old password");
		}
		 	
		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.SQL_RESET_PASSWORD);
				){
			ps.setString(1, RegisterUserService.hashPassword(user.getPassword().toCharArray(), salt.getBytes(), 1, 256));
			ps.setString(2, user.getUserId());
			ps.execute();

			return new Response(true);
		}catch(Exception e){
			return new Response(false, "Error resetting password");
		}

	}
	private boolean isValidResetToken(String token){
		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.SQL_CHECK_RESET_PASSWORD_SESSION);
				){
			ps.setString(1, token);
			try(ResultSet rs = ps.executeQuery()){
				while (rs.next()){
					int value = rs.getInt("cnt");
					if (value > 0){
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
	private boolean checkIfOldPassword(User user, String newPassword){
		int numOfSameOldPasswords = 0;
		try(		
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.SQL_CHECK_OLD_PASSWORDS);
				){
			ps.setString(1, user.getEmail());
			ps.setString(2, newPassword);
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()){
					numOfSameOldPasswords = rs.getInt("cnt");
				}
			}
			if(numOfSameOldPasswords > 0){
				return true;
			}
			return false;
		} catch (Exception e){
			return false;
		}
	}
	private boolean insertOldPassword(User user){
		try(
				Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.SQL_INSERT_OLD_PASSWORD);
				){
			ps.setString(1, user.getUserId());
			ps.setString(2, user.getSalt());
			ps.setString(3, user.getPassword());
			ps.execute();

			return true;
		}catch(Exception e){
			return false;
		}
	}

	private String generateToken() {
		return new String(KeyGenerators.string().generateKey());
	}
}
