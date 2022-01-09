package com.xadmin.usermanagement.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.xadmin.usermanagement.bean.User;

public class UserDao {
	
	private String jdbcURL = "jdbc:mysql://localhost:3306/userdb";
	private String jdbcUsername = "root";
	private String jdbcPassword = "1234";
	private String jdbcDriver = "com.mysql.jdbc.Driver";

	private static final String INSERT_USERS_SQL = "INSERT INTO users" + "(name,email,country)VALUES "+" (?,?,?);";
	private static final String SELECT_USER_BY_ID = "select id,name,email,country from users where id=?";
	private static final String SELECT_ALL_USERS = "select * from users";
	private static final String DELETE_USERS_SQL = "delete from users where id = ?;";
	private static final String UPDATE_USERS_SQL = "update users set name = ?, email=?,country=? where id=?;";

	public UserDao(){
		
	}
	
	protected Connection getConnection() {
		Connection con = null;
		try {
			Class.forName("jdbcDriver");
			con = DriverManager.getConnection(jdbcURL,jdbcUsername,jdbcPassword);
			
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return con;
		
	}
	
	//insert user
	public void insertUser(User user) throws SQLException{
		System.out.println(INSERT_USERS_SQL);
		try (Connection con = getConnection();
				PreparedStatement prepStmt = con.prepareStatement(INSERT_USERS_SQL)) {
			prepStmt.setString(1, user.getName());
			prepStmt.setString(2, user.getEmail());
			prepStmt.setString(3,  user.getCountry());
			System.out.println(prepStmt);
			prepStmt.executeUpdate();
		}
		catch (SQLException e) {
			printSQLException(e);
		}
	}

	
	
	//select user by id
	public User SelectUser(int id) {
		User user = null;
		//Step 1: Establishing a Connection
		try(Connection con=getConnection();
				//Step 2: Create a statement using connection object
				PreparedStatement prepStmt = con.prepareStatement(SELECT_USER_BY_ID);){
			prepStmt.setInt(1, id);
			System.out.println(prepStmt);
			//Step 3: Execute the query or update query
			ResultSet rs = prepStmt.executeQuery();
			
			//Step 4: Process the Resultset object.
			while(rs.next()) {
				String name = rs.getString("name");
				String email = rs.getString("email");
				String country = rs.getString("country");
				user = new User(id,name,email,country);
			}
		}catch(SQLException e) {
			printSQLException(e);
		}
		return user;
		
	}
	
	//select all users
	public List<User> selectAllUsers(){
		List<User> users = new ArrayList<>();
		
		try (Connection con = getConnection();
				PreparedStatement prepStmt = con.prepareStatement(SELECT_ALL_USERS);){
			System.out.println(prepStmt);
			ResultSet rs = prepStmt.executeQuery();
			
			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String country = rs.getString("country");
				users.add(new User(id,name,email,country));
			}
		}catch(SQLException e) {
			printSQLException(e);
		}
		return users;
	}
	
	//update user
	public boolean updateUser(User user) throws SQLException{
		boolean rowUpdated;
		try (Connection con = getConnection();
				PreparedStatement stmt = con.prepareStatement(UPDATE_USERS_SQL);){
			System.out.println("updated user: "+stmt);
			stmt.setString(1, user.getName());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, user.getCountry());
			stmt.setInt(4, user.getId());
			
			rowUpdated = stmt.executeUpdate() > 0;
		}
		return rowUpdated;
	}
	
	//delete user
	public boolean deleteUser(int id) throws SQLException{
		boolean rowDeleted;
		try (Connection con = getConnection();
				PreparedStatement stmt = con.prepareStatement(DELETE_USERS_SQL);){
			stmt.setInt(1, id);
			rowDeleted = stmt.executeUpdate() > 0;    
		}
		return rowDeleted;
		
	}
	
	
	private void printSQLException(SQLException ex) {
		for (Throwable e:ex) {
			if(e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: "+((SQLException)e).getSQLState());
				System.err.println("Error Code: "+((SQLException)e).getSQLState());
				System.err.println("Message: "+e.getMessage());
				Throwable t = ex.getCause();
				while (t != null) {
					System.out.println("Cause: "+t);
					t=t.getCause();
				}
			}
		}
	}
	


}
