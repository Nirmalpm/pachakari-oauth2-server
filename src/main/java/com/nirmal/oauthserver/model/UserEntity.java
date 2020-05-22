package com.nirmal.oauthserver.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Data
public class UserEntity {
	private int id;
	private String userName;
	private String firstName;
	private String lastName;
	private String emailId;
	private String password;
	private Integer isActive;
	private Timestamp createDate;
	private Timestamp modifiedDate;
	private int role;
	private Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList();
	
	
}
