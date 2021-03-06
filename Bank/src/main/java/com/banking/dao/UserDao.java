package com.banking.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.banking.pojos.User;
import com.banking.util.ConnectionFactory;

import oracle.jdbc.OracleTypes;

public class UserDao implements Dao<User, Integer>{
	
	@Override
	public List<User> findAll() {

		List<User> users = new ArrayList<User>();
		
		try(Connection conn = ConnectionFactory.getInstance().getConnection()){
			
			String sql = "{ call GET_ALL_BK_USER(?) }";
			
			CallableStatement cs = conn.prepareCall(sql);
			
			cs.registerOutParameter(1, OracleTypes.CURSOR);
			cs.execute();
			
			ResultSet rs = (ResultSet) cs.getObject(1);
			
			while(rs.next()) {
				
				User temp = new User();
				temp.setId(rs.getInt("USER_ID"));
				temp.setFirstName(rs.getString(2));
				temp.setLastName(rs.getString(3));
				temp.setUsername(rs.getString(4));
				temp.setPassword(rs.getString(5));
				users.add(temp);
				
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		
		}
		return users;

	}

	@Override
	public User findById(Integer id) {
		
		User user = null;
		
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			
			String sql = "SELECT * FROM BK_USERS WHERE USER_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1,  id);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				
				user = new User();
				user.setId(rs.getInt(1));
				user.setFirstName(rs.getString(2));
				user.setLastName(rs.getString(3));
				user.setUsername(rs.getString(4));
				user.setPassword(rs.getString(5));
				
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		
		}
		
		return user;
	}

	@Override
	public User save(User obj) {
		
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			
			conn.setAutoCommit(false);
			
			String sql = "INSERT INTO BK_USERS (FIRSTNAME, LASTNAME, USERNAME, USER_PASSWORD)"
					+ " VALUES(?, ?, ?, ?)";
			
			String[] keyNames = {"USER_ID"};
			
			PreparedStatement ps = conn.prepareStatement(sql, keyNames);
			ps.setString(1, obj.getFirstName());
			ps.setString(2, obj.getLastName());
			ps.setString(3, obj.getUsername());
			ps.setString(4, obj.getPassword());
			
			
			int numRows = ps.executeUpdate();
			if(numRows > 0) {
				
				ResultSet pk = ps.getGeneratedKeys();
				
				while(pk.next()) {
					
					obj.setId(pk.getInt(1));
					
				}
				
				conn.commit();
				
			}	
			
		} catch (SQLException e) {

			e.printStackTrace();
		
		}
		
		return obj;

	}

	@Override
	public User update(User obj) {
	
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			
			String sql = "UPDATE BK_USERS SET FIRSTNAME = ?, LASTNAME = ?, USERNAME = ?, USER_PASSWORD = ? WHERE USER_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,  obj.getFirstName());
			ps.setString(2, obj.getLastName());
			ps.setString(3,  obj.getUsername());
			ps.setString(4, obj.getPassword());
			ps.setInt(5, obj.getId());
			ps.executeUpdate();
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		
		}
		
		return null;
	}

	@Override
	public void delete(User obj) {
		// TODO Auto-generated method stub
		
	}
    
}