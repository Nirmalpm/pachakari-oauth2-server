package com.nirmal.oauthserver.dao;

import com.nirmal.oauthserver.model.UserEntity;

public interface OAuthDAOService {
	UserEntity getUserDetails(String emailId);
	UserEntity registerUser(UserEntity user);
	UserEntity registerCompletion(UserEntity user);
	UserEntity getUser(int id); 
}
