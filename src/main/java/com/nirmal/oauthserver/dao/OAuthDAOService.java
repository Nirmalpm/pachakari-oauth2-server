package com.nirmal.oauthserver.dao;

import com.nirmal.oauthserver.model.UserEntity;

public interface OAuthDAOService {
	public UserEntity getUserDetails(String emailId);
}
