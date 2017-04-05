package org.akpsi.conventionapp.services;

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

	public static boolean userExist(String email){
		try(Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(Constants.CHECK_FOR_USER)){
			int numOfUserWithEmail = Integer.MIN_VALUE;
			ps.setString(1, email);
			try(ResultSet rs = ps.executeQuery()){
				if (rs.next()){
					numOfUserWithEmail = rs.getInt(1);
				}
			}
			if (numOfUserWithEmail==1){
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
public Response forgotPassword(@RequestBody User user, HttpServletResponse response) {
	
	if (!userExist(user.getEmail())){
		return new Response(false,"No user is registered under this email.");
	}
	
	try(
			Connection conn = ConnectionFactory.getConnection();
			PreparedStatement ps = conn.prepareStatement(Constants.FORGOT_PASSWORD_ENTRY);
			){
		String token = generateToken();
		ps.setString(1,user.getEmail());
		ps.setString(2, token);
		
		if(Integer.parseInt(user.getAllowEmail()) == 1){
			HttpHeaders headers = new HttpHeaders();
			RestTemplate restTemplate = new RestTemplate();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("apiKey", "4hJctCGcfUNkEkJh9mH42yQ3Q9WHQhLv");
			map.add("toAddress", user.getEmail());
			map.add("subject", "Forgot Password - Link to reset password");
			map.add("message", " (Insert message and link to reset user password)");

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

			restTemplate.postForEntity(Constants.EMAIL_SERVICE_URL , request , String.class );
		}
		
		
		
		
		return new Response(true, "Forgot Password Session Started");
		
	}catch(Exception e) {
		return new Response(false, "Error creating session");
	}
	
}
private String generateToken() {
	return new String(KeyGenerators.string().generateKey());
}
}
