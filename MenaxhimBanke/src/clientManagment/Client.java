package clientManagment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * The client class represents a client. It provides different constructs in order to fit the program's needs.
 * It provides differents methods with which one can manipulate a client such as:
 * deleteAccount to delete an account; isFrozen to check whether the account is frozen;
 * freeze to prevent any further operations on the account; unfreeze to unfreeze the account;
 * deposit and withdraw for different transactions; saveClient to save the client in the database;
 * various getClient methods to fit different purposes of the program; 
 * and lastly two clientExists methods checking whether a client exits
 * @author neiral
 *
 */
public class Client {
	private String id, name, surname, phone;
	private double balance;
	
	/**
	 * Constructs a client with an id, name, surname, phone and initial balance
	 * @param id the id of the client
	 * @param name the name of the client
	 * @param surname the surname of the client
	 * @param phone the phone of the client
	 * @param balance the initial balance of the client
	 */
	public Client (String id, String name, String surname, String phone, double balance){
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.phone = phone;
		this.balance = balance;
	}
	
	/**
	 * Constructs a client with a given id
	 * @param id the id of the client that uniquely 
	 */
	public Client (String id){
		this();
		this.id = id;
	}
	
	/**
	 * Constructs a client with no attributes whatsoever
	 */
	public Client(){
		this("", "", "", "", 0);
	}
	
	/**
	 * Sets the new balance of the bank account
	 * @param newBalance the new balance of the bank account
	 */
	public void setBalance(double newBalance){this.balance = newBalance;}
	
	/**
	 * Returns the id of the client account
	 * @return the id of the client
	 */
	public String getId(){return this.id;}
	
	/**
	 * Returns the name of the client account
	 * @return the name of the client
	 */
	public String getName(){return this.name;}
	
	/**
	 * Returns the surname of the client account
	 * @return the name of the client
	 */
	public String getSurname(){return this.surname;}
	
	/**
	 * Returns the balance of the client account
	 * @return the balance of the client
	 */
	public double getBalance(){return this.balance;}
	
	/**
	 * Returns the phone number of the client account
	 * @return the phone number
	 */
	public String getPhone(){return this.phone;}
	
	
	/**
	 * Delete a client account
	 * @param id the id of the client
	 */
	public static void deleteAccount (String id) throws SQLException{
		Connection conn = DataSource.getConnection();
		
		try{
			PreparedStatement pStat = conn.prepareStatement("DELETE FROM client WHERE id = ?");
			pStat.setString(1, id);
			pStat.executeUpdate();
		}finally{
			conn.close();
		}
	}
	
	
	/**
	 * Check if a client account is frozen. If a client account is frozen no further 
	 * transactions can be made.
	 * @param cId the client id
	 * @return whether the account is frozen
	 */
	public static boolean isFrozen (String cId) throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			
			PreparedStatement stat = conn.prepareStatement("SELECT freezed from client WHERE id=?;");
			stat.setString(1, cId);
			ResultSet rs = stat.executeQuery();
			if (rs.next())
				if (rs.getInt(1) == 0)
					return false;
			return true;
			
		}finally{
			conn.close();
		}
	}
	
	
	/**
	 * Freeze a particular account preventing any further operations on it
	 * @param cId the client id holding the account
	 */
	public static void freeze(String cId) throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			
			PreparedStatement stat = conn.prepareStatement("UPDATE client SET freezed = 1  WHERE id = ?;");
			stat.setString(1, cId);
			stat.execute();
			
		}finally{
			conn.close();
		}
	}
	
	
	/**
	 * Unfreeze a particular account returning to it its normal functionalities
	 * @param cId the client id holding the account
	 */
	public static void unFreeze(String cId) throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			
			PreparedStatement stat = conn.prepareStatement("UPDATE client SET freezed = 0 WHERE id = ?;");
			stat.setString(1, cId);
			stat.execute();
			
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Deposits money to a client
	 * @param client the client
	 * @param amount the amount to deposit
	 */
	public static void deposit (Client client, double amount)throws SQLException{
		Connection conn = DataSource.getConnection();
		
		try{
			String query = "UPDATE client "
						+ " SET balance = balance + ?"
						+ " WHERE id = ?;";
			PreparedStatement stat = conn.prepareStatement(query);
			stat.setDouble(1, amount);
			stat.setString(2, client.getId());
			stat.executeUpdate();
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Withdraws money from a client
	 * @param client the client 
	 * @param amount the amount to be withdrawn
	 */
	public static void withdraw (Client client, double amount) throws SQLException{
		Connection conn = DataSource.getConnection();
		
		try{
			double result = client.getBalance() - amount;
			double result2 = Math.round(result * 100.0) / 100.0;
			String query = "UPDATE client "
						+ " SET balance = "+result2 
						+ " WHERE id = ?;";
			PreparedStatement stat = conn.prepareStatement(query);
			stat.setString(1, client.getId());
			stat.executeUpdate();
		}finally{
			conn.close();
		}
	}
	
	
	/**
	 * Saves the client in the database
	 * @param client that will be saved
	 * @throws SQLException possible database errors that may occur
	 */
	public static void saveClient(Client client) throws SQLException{
		Connection conn = DataSource.getConnection();
		
		try{
			
			String query = "INSERT INTO client VALUES ('"+client.getId()+"', '"
					+ ""+ client.getName()+"', '"+client.getSurname()+"', "+client.getBalance()+", '"+client.getPhone()+"', 0);";
			Statement st = conn.createStatement();
			st.execute(query);

		}finally{
			conn.close();
		}
	}
	
	
	/**
	 * Gets all the clients
	 * @return all the clients as an arraylist
	 */
	public static ArrayList<Client> getClients() throws SQLException{
		ArrayList<Client> result = null;
		
		Connection conn = DataSource.getConnection();
		
		try{
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM client;");
			
			result = new ArrayList<Client>();
			while(rs.next()){
				result.add(new Client(rs.getString("id"), rs.getString("name"), rs.getString("surname"), rs.getString("phone"), rs.getDouble("balance")));
			}
			return result;	
		}finally{
			conn.close();
		}
	}
	
	
	/**
	 * Check whether a client with a particular id exists in the database
	 * @param id of the client
	 * @return true or false depending on whether the client exists
	 * @throws SQLException possible database errors that may occur
	 */
	public static boolean clientExists (String id) throws SQLException{
		
		Connection conn = DataSource.getConnection();
		
		try{
			Statement stat = conn.createStatement();
			String query = "SELECT * FROM client "
						+ "WHERE id='"+id+"';";
			ResultSet rs = stat.executeQuery(query);
			if (rs.next())
				return true;
			return false;
			
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Check whether a client with a particular name and surname exists in the database
	 * @param firstName the first name of the client
	 * @param lastName the last name of the client
	 * @return true or false depending on whether the client exists
	 */
	public static boolean clientExists(String firstName, String lastName) throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			PreparedStatement stat = conn.prepareStatement("SELECT * FROM client WHERE name = ? AND surname = ?");
			stat.setString(1, firstName);
			stat.setString(2, lastName);
			ResultSet rs = stat.executeQuery();
			if (rs.next())
				return true;
			return false;
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Returns a client with the particular id. 
	 * @param id of the client to be returned
	 * @return the client
	 */
	public static Client getClient(String id) throws SQLException{
		Connection conn = DataSource.getConnection();
		Client client = null;
		
		try{
			
			String query = "SELECT * FROM client WHERE id = '"+id+"';";
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			if (rs.next())
				client = new Client(rs.getString("id"), rs.getString("name"), rs.getString("surname"), rs.getString("phone"), rs.getDouble("balance"));
			return client;
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Returns a client with a particular name and id. If there is more than one match,
	 * then it will return all the possible matches.
	 * @param firstName the first name of the client
	 * @param lastName the last name of the client
	 * @return ArrayList containing the clients
	 */
	public static ArrayList<Client> getClient(String firstName, String lastName)throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			PreparedStatement stat = conn.prepareStatement("SELECT * FROM client WHERE name = ? AND surname = ?;");
			stat.setString(1, firstName);
			stat.setString(2, lastName);
			ResultSet rs = stat.executeQuery();
			
			if (rs.next()){
				ArrayList<Client> result = new ArrayList<Client>();
				result.add(new Client(rs.getString("id"), rs.getString("name"), rs.getString("surname"), rs.getString("phone"), rs.getDouble("balance")));
				while (rs.next()){
					result.add(new Client(rs.getString("id"), rs.getString("name"), rs.getString("surname"), rs.getString("phone"), rs.getDouble("balance")));
				}
				return result;
			}
			return null;
		}finally{
			conn.close();
		}
	}
}