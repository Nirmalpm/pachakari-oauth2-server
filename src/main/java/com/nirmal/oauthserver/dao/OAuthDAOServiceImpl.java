package com.nirmal.oauthserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nirmal.oauthserver.model.UserEntity;

@Repository
public class OAuthDAOServiceImpl implements OAuthDAOService {

	@Autowired
	private JdbcTemplate jdbcTemplate; 
	
	@Override
	public UserEntity getUserDetails(String emailId) {
		Collection<GrantedAuthority> grantedAuthorities = new ArrayList();
		
		List<UserEntity> list = jdbcTemplate.query("SELECT * FROM USER WHERE EMAIL_ID=?", 
				new String[] {emailId},(ResultSet rs, int rowNum) ->{
					UserEntity user = new UserEntity();
					user.setEmailId(emailId);
					user.setId(rs.getInt("ID"));
					user.setUserName(rs.getString("USERNAME"));
					user.setFirstName(rs.getString("FIRSTNAME"));
					user.setLastName(rs.getString("LASTNAME"));
					user.setPassword(rs.getString("PASSWORD"));
					user.setIsActive(rs.getInt("ISACTIVE"));
					return user;
				});
		if (!list.isEmpty()) {
			UserEntity userEntity = list.get(0);
			List<String> permissionList = jdbcTemplate.query("SELECT DISTINCT P.PERMISSION_NAME FROM PERMISSION P\r\n" + 
					"INNER JOIN ASSIGN_PERMISSION_TO_ROLE P_R ON P.ID=P_R.PERMISSION_ID\r\n" + 
					"INNER JOIN ROLE R ON R.ID = P_R.ROLE_ID\r\n" + 
					"INNER JOIN ASSIGN_USER_TO_ROLE U_R ON U_R.ROLE_ID=R.ID\r\n" + 
					"INNER JOIN USER U ON U.ID=U_R.USER_ID\r\n" + 
					"WHERE U.EMAIL_ID=?", new String[] {emailId},
					(ResultSet rs, int rowNum)->{
						return "ROLE_"+rs.getString("PERMISSION_NAME");
					});
			
			if(permissionList != null && !permissionList.isEmpty()) {
				for(String permission: permissionList) {
					GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission);
					grantedAuthorities.add(grantedAuthority);
				}
				userEntity.setGrantedAuthoritiesList(grantedAuthorities);
			}
			return userEntity;	
		}
		return null;
	}

	@Override
	public UserEntity registerUser(UserEntity user) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		jdbcTemplate.update(new PreparedStatementCreator() {
			String insertSql = "INSERT INTO USER(USERNAME,FIRSTNAME,LASTNAME,EMAIL_ID,PASSWORD,ISACTIVE,CREATED_DATE) VALUES(?,?,?,?,?,?,?)";
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(insertSql,Statement.RETURN_GENERATED_KEYS);
				pst.setString(1, user.getUserName());
				pst.setString(2, user.getFirstName());
				pst.setString(3, user.getLastName());
				pst.setString(4, user.getEmailId());
				pst.setString(5, user.getPassword());
				pst.setInt(6, user.getIsActive());
				pst.setTimestamp(7, user.getCreateDate());
				return pst;
			}}, keyHolder);
		int id = keyHolder.getKey().intValue();
		user.setId(id);
		return user;	
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public UserEntity getUser(int id) {
		String query = "select * from user where id=?";
		
		List<UserEntity> users = jdbcTemplate.query(query,new Integer[] {id} ,(ResultSet rs, int rowNum)->{
			UserEntity user = new UserEntity();
			System.out.println(rs.getString("USERNAME"));
			user.setId(rs.getInt("ID"));
			user.setUserName(rs.getString("USERNAME"));
			user.setFirstName(rs.getString("FIRSTNAME"));
			user.setLastName(rs.getString("LASTNAME"));
			user.setEmailId(rs.getString("EMAIL_ID"));
			return user;
		});
								
		return users.get(0);
	}

	@Override
	@Transactional
	public UserEntity registerCompletion(UserEntity user) {
		String sql1 = "update user set password='"+user.getPassword()+"' where id="+user.getId();
		String sql2 = "insert into assign_user_to_role(user_id,role_id) values (?,?)";
		int up = jdbcTemplate.update(sql1);
		jdbcTemplate.update(new PreparedStatementCreator() {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql2);
				pst.setInt(1, user.getId());
				pst.setInt(2, user.getRole());
				return pst;
			}
		});		
		return user;
	}
		
		

}
