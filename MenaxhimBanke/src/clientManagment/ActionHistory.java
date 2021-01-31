package clientManagment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Performing different actions concerning the action History tab.
 * 
 * @author neiral
 *
 */

public class ActionHistory {
	private String clientId;
	private String clientName;
	private String clientSurname;
	private String actionName;
	private double sum;
	private String date;
	
	/**
	 * Construct an empty ActionHistory object
	 */
	public ActionHistory(){
		this.clientId = "";
		this.clientName = "";
		this.clientSurname = "";
		this.actionName = "";
		this.sum = 0;
		this.date = "";
	}
	
	/**
	 * Construct an ActionHistory object
	 * @param cId the client id
	 * @param cName the client name 
	 * @param cSurname the client surname
	 * @param action the action made, which could be, withdraw, deposit
	 * taking a loan, paying a loan, registering a new client etc
	 * @param s the sum of the transaction if there be any
	 */
	public ActionHistory(String cId, String cName, String cSurname, String action, double s){
		this.clientId = cId;
		this.clientName = cName;
		this.clientSurname = cSurname;
		this.actionName = action;
		this.sum = s;
		
		Date currDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// Adapt the java format to the mysql one
		date = sdf.format(currDate);
	}
	
	/**
	 * Construct an Action History object, furnishing a date as a parameter as well
	 * @param cId the client id
	 * @param cName the client first name
	 * @param cSurname the client last name
	 * @param action the action performed
	 * @param s the sum of the transaction if it is one
	 * @param date the date when the action was performed
	 */
	public ActionHistory(String cId, String cName, String cSurname, String action, double s, String date){
		this.clientId = cId;
		this.clientName = cName;
		this.clientSurname = cSurname;
		this.actionName = action;
		this.sum = s;
		this.date = date;
	}
	
	/**
	 * get the client id
	 * @return the client id
	 */
	public String getClientId(){return this.clientId;}
	
	/**
	 * get the client name
	 * @return the client name
	 */
	public String getClientName(){return this.clientName;}
	
	/**
	 * get the client surname
	 * @return the client surname
	 */
	public String getClientSurname(){return this.clientSurname;}
	
	/**
	 * Get the action performed by the user
	 * @return the action performed
	 */
	public String getActionPerformed(){return this.actionName;}
	
	/**
	 * Get the sum of the transaction
	 * @return the sum of the transaction
	 */
	public double getSum(){return this.sum;}
	
	public String getDate(){return this.date;}
	
	/**
	 * Save an ActionHistory object to the database
	 * @param action the action performed, which can be a transaction, client or loan registering.
	 */
	public static void saveAction(ActionHistory action)throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			String query = "INSERT INTO action_history"
						+ " VALUES ('"+action.getClientId()+"', '"+action.getClientName()+"', '"+action.getClientSurname()+"', '"+action.getActionPerformed()+"'"
								+ ", "+action.getSum()+", '"+action.getDate()+"');";
			Statement stat = conn.createStatement();
			stat.execute(query);
			
		}finally{
			conn.close();
		}
	}
	
	
	/**
	 * Get all the action history of the bank
	 * @return the action history in an ArrayList
	 */
	public static ArrayList<ActionHistory> getAllHistory() throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			ArrayList<ActionHistory> result = new ArrayList<ActionHistory>();
			String query = "SELECT * FROM action_history ORDER BY date DESC;";
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next())
				result.add(new ActionHistory(rs.getString("client_id"), rs.getString("name"), rs.getString("surname"), rs.getString("action"), rs.getDouble("sum"),
						rs.getString("date")));
			return result;
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Get all the action history of the client with a particular id with the most recent on the top
	 * @param cId the client id
	 * @return the aciton history in an ArrayList
	 */
	public static ArrayList<ActionHistory> getClientHistory(String cId) throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			PreparedStatement stat = conn.prepareStatement("Select * FROM action_history WHERE client_id = ? ORDER BY date DESC");
			stat.setString(1, cId);
			ResultSet rs = stat.executeQuery();
			ArrayList<ActionHistory> result = new ArrayList<>();
			while (rs.next())
				result.add(new ActionHistory (rs.getString("client_id"), rs.getString("name"), rs.getString("surname"), rs.getString("action"), rs.getDouble("sum"),
						rs.getString("date")));
			return result;
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Get all the action history of the client with a particular first name and surname 
	 * with the most recent on the top
	 * @param firstName the client's first name
	 * @param lastName the client's last name
	 * @return an ArrayList containing all the action history of the clients that match
	 * that particular fistName and lastName
	 */
	public static ArrayList<ActionHistory> getClientHistory(String firstName, String lastName)throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			PreparedStatement stat = conn.prepareStatement("SELECT * FROM action_history WHERE name = ? AND surname = ?;");
			stat.setString(1, firstName);
			stat.setString(2, lastName);
			ResultSet rs = stat.executeQuery();

			if (rs.next()){
				ArrayList<ActionHistory> result = new ArrayList<ActionHistory>();
				result.add(new ActionHistory(rs.getString("client_id"), rs.getString("name"), rs.getString("surname"), rs.getString("action"), rs.getDouble("sum"), 
						rs.getString("date")));
				
				while (rs.next())
					result.add(new ActionHistory(rs.getString("client_id"), rs.getString("name"), rs.getString("surname"), rs.getString("action"), rs.getDouble("sum"), 
							rs.getString("date")));
				return result;
			}
			return null;
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Check if a cliend id exists in the ActionHistory database
	 * @param cId the client id
	 * @return whether the id exists or not
	 */
	public static boolean existsId(String cId) throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			
			PreparedStatement stat = conn.prepareStatement("SELECT * FROM action_history WHERE client_id = ?");
			stat.setString(1, cId);
			ResultSet rs = stat.executeQuery();
			if (rs.next())
				return true;
			return false;
			
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Get an ActionHistory object for a specific client id
	 * @return the ActionHistory object
	 */
	public static ActionHistory getActionHistory(String cId) throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			
			PreparedStatement stat = conn.prepareStatement("SELECT * FROM action_history WHERE client_id = ? LIMIT 1");
			stat.setString(1, cId);
			ResultSet rs = stat.executeQuery();
			if (rs.next())
				return new ActionHistory(rs.getString("client_id"), rs.getString("name"), rs.getString("surname"), rs.getString("action"), rs.getDouble("sum"),
						rs.getString("date"));
			return null;
		}finally{
			conn.close();
		}
	}
}