package com.nirmal.oauthserver.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nirmal.oauthserver.dao.OAuthDAOService;
import com.nirmal.oauthserver.email.EmailService;
import com.nirmal.oauthserver.model.UserEntity;

@RestController
@RequestMapping("/register")
public class UserRegisterController {	
	
	@Autowired
	PasswordEncoder encoder;
	@Autowired
	OAuthDAOService oAuthDAOService;
	@Autowired
	EmailService emailService;
	
	@PostMapping(value="/user", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> user(@RequestBody Map<String, Object> userMap){
		try {
			JsonParser parser = new Jackson2JsonParser();
			String userJson = parser.formatMap((Map<String, ?>) userMap.get("user"));
			String registerUrl = (String)userMap.get("registerUrl");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
			UserEntity user =  mapper.readValue(userJson, UserEntity.class);
			System.out.println(user);
			user.setIsActive(0);
			user.setPassword("-NOT SET-");
			UserEntity registeredUser = oAuthDAOService.registerUser(user);
			registerUrl = registerUrl + "?id="+ user.getId();
			this.emailService.sendRegisterMessage(user.getEmailId(), "User Registered", null,user,registerUrl);
			return new ResponseEntity<>(userMap,HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	@GetMapping("/user")
	public ResponseEntity<UserEntity> user(@RequestParam("id") Integer id){
		UserEntity user = oAuthDAOService.getUser(id);
		return new ResponseEntity<>(user,HttpStatus.OK);
	}
	
	@PostMapping("/confirmuser/{role}")
	public ResponseEntity<?> confirmUser(@RequestBody UserEntity user, @PathVariable("role") Integer role ){
		user.setIsActive(0);
		user.setPassword(encoder.encode(user.getPassword()));
		//List roles = new ArrayList();
		//roles.add(role);
		user.setRole(role);
		UserEntity registeredUser = oAuthDAOService.registerCompletion(user);	
		this.emailService.sendRegisterMessage(user.getEmailId(), "User Registration completed. Thank you", null,user,"google.com");
		return new ResponseEntity<UserEntity>(registeredUser,HttpStatus.OK);
	}
}
