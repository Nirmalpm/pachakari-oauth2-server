package com.nirmal.oauthserver.email;

import com.nirmal.oauthserver.model.UserEntity;

public interface EmailService {
	public void sendMessage(String to, String subject, String text);
	public void sendMessageWithAttach(String to, String subject, String text, String filePath);
	public void sendRegisterMessage(String to, String subject, String filePath,UserEntity user, String gregisterUrl); 
}
