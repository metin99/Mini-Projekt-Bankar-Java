package clientManagment;


import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Loan class contains needed information about loan objects as well as all the 
 * possible operations that can be made with loans.
 * Note: A month has been converted to 1 day for the sake of demonstration 
 * @author neiral
 *
 */
public class Loan {

	private String client_id;
	private double principal_amount;
	private double interest_rate;
	private int no_years;
	private int no_payments_yearly;
	private String start_date;
	
	
	/**
	 * Construct an empty loan object
	 */
	public Loan(){
		this("", 0, 0, 0, 0);
		start_date = null;
	}

	
	/**
	 * Construct a loan object with a client id, principal amount, interest rate 
	 * number of years and no of payments early.
	 * This constructor is used when the loan is first registered
	 * @param cId the id of the client taking the loan
	 * @param pA  the principal amount loaned
	 * @param iR  the interest rate of the loan
	 * @param nY  the number of years for the loan to be paid
	 * @param nPy number of early payments made
	 */
	public Loan(String cId, double pA, double iR, int nY, int nPy){
		client_id = cId;
		principal_amount = pA;
		interest_rate = iR;
		no_years = nY;
		no_payments_yearly = nPy;
		
		Date current_time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Adapt the java data format to the mysql one
		start_date = sdf.format(current_time);
	}
	
	
	/**
	 * Constructs a loan object including a starting date as a parameter.
	 * This constructor is used for different loan opperations
	 * @param cId the client id of the holding client
	 * @param pA  the principal_amount taken as a loan
	 * @param iR  the interest rate of the loan 
	 * @param nY  the number of years for the loan to be paid
	 * @param nPy the number of payments that will be made in a year
	 * @param date the starting date of the loan
	 */
	public Loan(String cId, double pA, double iR, int nY, int nPy, String date){
		client_id = cId;
		principal_amount = pA;
		interest_rate = iR;
		no_years = nY;
		no_payments_yearly = nPy;
		start_date = date;
	}
	
	/**
	 * Constructs a loan object with a client id and a start date only.
	 * Since the bank does not allow loans taken in the same date then this two
	 * parameters alone serve as unique identifier for loans. That means that 
	 * client_id and start_date could serve as a primary key and probably such an 
	 * implementation would be better
	 * @param cId the client id
	 * @param date the starting date of the loan
	 */
	public Loan (String cId, String date){
		this();
		this.client_id = cId;
		this.start_date = date;
	}
	
	/**
	 * Constructs a loan object with a client id, a principal amount and a 
	 * start date. This construction will be used by the superclass OldLoan.
	 * @param cId the client id
	 * @param pAmount the principal amount
	 * @param date the start date
	 */
	public Loan(String cId, double pAmount, String date){
		this();
		this.client_id = cId;
		this.principal_amount = pAmount;
		this.start_date = date;
	}
	
	/**
	 * Get all the loans 
	 * @return an array list containing all the loans
	 */
	public static ArrayList<Loan> getAllLoans() throws SQLException{
		Connection conn = DataSource.getConnection();
		ArrayList<Loan> result = null;
		try{
			
			result = new ArrayList<Loan>();
			String query = "SELECT * FROM loan;";
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()){
				result.add((new Loan(rs.getString("client_id"), rs.getDouble("principal_amount"), rs.getDouble("interest_rate"), rs.getInt("no_years"), 
						rs.getInt("no_payments_yearly"), rs.getString("start_date"))));
			}
			return result;
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Get the number of irregular payments of this particular loan
	 * @return
	 */
	public int getNumberOfIrregularPayments() throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			
			String query = "SELECT no_irregular_payments FROM loan "
					+ "WHERE client_id = '"+this.client_id+"' AND start_date = '"+this.start_date+"';";
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			rs.next();
			int irPayments = rs.getInt(1);
			return irPayments;
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Calculate the amount of fixed payments that will be payed by the customers.
	 * Basically, the way the loan works is that a client will have to pay it in fixed
	 * amounts for x times a year that are choosen by him.
	 * @return the amount of fixed payments
	 */
	public double getAmountOfFixedPayments(){
		double amount =  (principal_amount * (interest_rate / 100)) / (no_payments_yearly * (1 - Math.pow((1+(interest_rate/100)/no_payments_yearly), -no_years*no_payments_yearly)));
		double result = Math.round(amount * 100.0) / 100.0;
		return result;
	}
	
	/**
	 * Calculate the total amount of interest benefited by the bank.
	 * @return the total amount of interest
	 */
	public double getTotalAmountOfInterest(){
		double amount = getAmountOfFixedPayments()*no_years*no_payments_yearly - principal_amount;
		double result = Math.round(amount*100.0) / 100.0;
		return result;
	}
	
	/**
	 * Calculate the total amount paid by the customer
	 * @return the total amount paid by the customer
	 */
	public double getTotalAmountPaid(){
		double amount = getAmountOfFixedPayments()*no_years*no_payments_yearly;
		double result = Math.round(amount*100.0) / 100.0;
		return result;
	}
	
	/**
	 * Get a first basic information on the loan, which will be displayed when
	 * it is first created
	 * @return basic loan information
	 */
	public String getLoanInfo(){
		String info = "Klientit me id "+ client_id + " iu dha nje kredi me nje shume prej " + principal_amount+"€.\n"
				+ "Shuma prej " + getAmountOfFixedPayments()+ " do te paguhet " + no_payments_yearly + " here ne vit pergjate nje periudhe"
						+ " prej \n"+no_years+" vitesh. Interesi eshte " + interest_rate+"%. Kjo do te thote se ne total "+ 
						"pagesa e interesave \neshte"+getTotalAmountOfInterest()+" €. Ne fund te kredise klienti do te kete paguar "+getTotalAmountPaid() + " €.";
		
		return info;
	}
	
	
	
	/**
	 * Get the total amount of the remaining payment of the loan at this point in time
	 * @return the amount of remaining payment
	 */
	public double getRemainingPayment(boolean oldest) throws SQLException{
		
		// Get the current number of payments
		int k;
		if (oldest)
			k = this.getCurrentNumberOfPayments();
		else 
			k = this.getCurrentNumberOfPaymentsOtherLoans();
		
		double result = (this.principal_amount - this.getAmountOfFixedPayments()*no_payments_yearly/(interest_rate/100)) * Math.pow((1+(interest_rate/100)/no_payments_yearly), k) 
				+this.getAmountOfFixedPayments()*no_payments_yearly/(interest_rate/100);
		return Math.round(result*100.0)/100.0;
	}
	
	/**
	 * Get the number of years required to pay the loan
	 * @return the number of years
	 */
	public int getNoYears(){return this.no_years;}
	
	/**
	 * Get the number of yearly payments that the client will make 
	 * @return the number of yearly payments
	 */
	public int getNoPaymentsYearly(){return this.no_payments_yearly;}
	
	/**
	 * get the interest rate
	 * @return the interest rate
	 */
	public double getInterest(){return this.interest_rate;}
	

	/**
	 * Get the client's principal amount
	 * @return the principal amount
	 */
	public double getPrincipalAmount(){return this.principal_amount;}
	
	/**
	 * Get the client id
	 * @return the client id
	 */
	public String getClientId(){ return this.client_id;}
	
	
	/**
	 * Get the number of payments payed up until now by the client. In case of a client having more than one
	 * loan, it will be choosen for the oldest loan.
	 * @return the number of payments payed
	 */
	public int getCurrentNumberOfPayments() throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			
			String query = "SELECT current_payment_counter FROM loan "
						+ " WHERE client_id = '"+this.client_id+"' AND start_date = '"+this.start_date+"';";
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			rs.next();
			return rs.getInt(1);
			
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Get the number of payments payed up until now by the client for this particular loan.
	 * The loan can be uniquely identified using the clien_id and its date.
	 * @return the number of payments payed
	 * @throws SQLException
	 */
	public int getCurrentNumberOfPaymentsOtherLoans() throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			
			String query = "SELECT current_payment_counter FROM loan "
						+ " WHERE client_id = '"+this.client_id+"' AND start_date = '"+this.start_date+"';";
		
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			if (rs.next())
				return rs.getInt(1);
			return 0;
		}finally{
			conn.close();
		}
	}

	/**
	 * Get the number of months that have passed since the client got this loan
	 * For the sake of demonstration 1 real month = 1 day 
	 * @return the number of months till now
	 */
	public int getMonthsPast(){
		Calendar calendar = Calendar.getInstance();
		int c_month = calendar.get(Calendar.MONTH);
		int c_day = calendar.get(Calendar.DAY_OF_MONTH);
		String[] date = start_date.split("-");
		int month = Integer.parseInt(date[1]);
		int day = Integer.parseInt(date[2]);
		
		int current_count = c_day;
		int client_count = day;
		
		switch (c_month - 1){
		case 11:
			current_count += 31;
		case 10: 
			current_count += 30;
		case 9:
			current_count += 31;
		case 8:
			current_count += 30;
		case 7:
			current_count += 31;
		case 6:
			current_count += 31;
		case 5: 
			current_count += 30;
		case 4: 
			current_count += 31;
		case 3:
			current_count += 30;
		case 2: 
			current_count += 31;
		case 1: 
			current_count += 28;
		case 0: 
			current_count += 31;
			break;
			
	}
	
		switch (month - 2){
		case 11:
			client_count += 31;
		case 10: 
			client_count += 30;
		case 9:
			client_count += 31;
		case 8:
			client_count += 30;
		case 7:
			client_count += 31;
		case 6:
			client_count += 31;
		case 5: 
			client_count += 30;
		case 4: 
			client_count += 31;
		case 3:
			client_count += 30;
		case 2: 
			client_count += 31;
		case 1: 
			client_count += 28;
		case 0: 
			client_count += 31;
			break;
	}
		
	return current_count-client_count;
	}
		
	/**
	 * Get the number of payments that are supposed to have been made by the 
	 * client up to this point in time.
	 * NOTE: for the sake of demonstration 1 day = 1 real month
	 * @return the number of total payments
	 */
	public int getNoPaymentsUpToDate(){
		// payment_ratio will check how many payments the client needs to do on average
		double payment_ratio = no_payments_yearly * 1.0 / 12;
		
		// Calculate no. of Payments up to date
		int counter = (int)Math.floor((getMonthsPast()) * payment_ratio);
		return counter;
	}
	
	/**
	 * Get the start date of this loan
	 * @return the start date of the loan
	 */
	public String getDate(){ return this.start_date;}
	
	
	/**
	 * Save a loan into the loan_history table and delete it
	 * @throws SQLException
	 */
	public void deleteLoan() throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			
			String query = "SELECT no_irregular_payments FROM loan"
						+ " WHERE client_id ='"+this.client_id+"' AND start_date ='"+this.start_date+"';";
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			rs.next();
			int irrPayments = rs.getInt(1);
			
			// get the current date that will serve as an end date for the loan
			Date current_time = new Date();
			// MySQL date format
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// Adapt the java data format to the mysql one
			String end_date = sdf.format(current_time);
			
			query = "INSERT INTO loan_history "
					+ "VALUES ('"+this.client_id+"', "+this.principal_amount+", '"+this.start_date+"', '"+end_date+"', "+irrPayments+")";
			stat.executeUpdate(query);
			
			// delete the loan
			query = "DELETE FROM loan "
					+ "WHERE client_id = '"+this.client_id+"' AND start_date = '"+this.start_date+"';";
			stat.executeUpdate(query);
			
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Check if the loan has been completely paid. To do this check if the 
	 * current_payment_counter field in the db is the same as the total
	 * number of payments required to pay off the loan
	 * @param loan the loan
	 * @return true or false depending on whether the loan has been paid
	 */
	public boolean isPaid () throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			
			String date = this.getDate();
			String query = "SELECT current_payment_counter FROM loan"
						+ " WHERE client_id = '"+this.client_id+"' AND start_date = '"+date+"';";
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			rs.next();
			int counter = rs.getInt(1);
			return (counter == no_years*no_payments_yearly);
			
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Update the number of irregular payments for a loan
	 * @param amount the amount of irregular payments
	 * @throws SQLException
	 */
	public void updateIrregularPayments(int amount) throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			
			amount = 1;
			String date = this.getDate();
			String query = "UPDATE loan "
						+ " SET no_irregular_payments = no_irregular_payments + ? "
						+ " WHERE client_id = '"+this.client_id+"' AND start_date = '"+date+"';";
			PreparedStatement stat = conn.prepareStatement(query);
			stat.setInt(1, amount);
			stat.executeUpdate();
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Increases the payment_counter of the loan after a payment has been done
	 * The payment of the oldest loan has precedence, therefore that counter shall be increased
	 * As a side note: There cannot be more than 2 loans taken in the same day as the bank 
	 * policies forbid it. 
	 * @throws SQLException
	 */
	public void increasePaymentCounter() throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
	
			String query = "Update loan "
						+ " SET current_payment_counter = current_payment_counter+1"
						+ " WHERE client_id = '"+this.client_id+"' AND start_date = '"+this.start_date+"';";
			Statement stat = conn.createStatement();
			stat.execute(query);
			
		}finally{
			conn.close();
		}
	}
	
	
	
	/**
	 * Save the loan in the database
	 * @param loan the loan to be saved
	 * @throws SQLException
	 */
	public void saveLoan (Loan loan) throws SQLException{
		Connection conn = DataSource.getConnection();
		
		try{
			String query = "INSERT INTO loan (client_id, principal_amount, interest_rate, no_years, no_payments_yearly, start_date)"
						+ " VALUES ('"+this.client_id+"', "+this.principal_amount+", "+this.interest_rate+", "+this.no_years+", "
								+ ""+this.no_payments_yearly+", '"+this.start_date+"')";
			Statement stat = conn.createStatement();
			stat.execute(query);
			
			// Deposit the money in the client's account
			Client client = Client.getClient(client_id);
			Client.deposit(client, principal_amount);
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Check whether a client with a given id has taken any looan
	 * @param clientId the id of the client
	 * @return whether or not this client has any loans
	 * @throws SQLException
	 */
	public static boolean hasLoan (String clientId) throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			String query = "SELECT * FROM loan "
						+ " WHERE client_id=?";
			PreparedStatement stat = conn.prepareStatement(query);
			stat.setString(1, clientId);
			ResultSet rs = stat.executeQuery();
			if (rs.next())
				return true;
			return false;
			
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Check whether a particular loan from a particular client exists
	 * @return true or false depending on whether the loan exists
	 * @throws SQLException
	 */
	public boolean existsLoan() throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			String query = "SELECT * FROM loan "
						+ " WHERE client_id = '"+this.client_id+"' AND start_date = '"+this.start_date+"';";
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			if (rs.next())
				return true;
			return false;
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Gets all the loans that the client might have
	 * @param clientId the client id
	 * @return AM ArrayList containing all the loans of the client
	 * @throws SQLException
	 */
	public static ArrayList<Loan> getLoans(String clientId)throws SQLException{
		Connection conn = DataSource.getConnection();
		ArrayList<Loan> loans = null;
		try{
			
			PreparedStatement stat = conn.prepareStatement("SELECT * FROM loan WHERE client_id = ?");
			stat.setString(1, clientId);
			ResultSet rs = stat.executeQuery();
			loans = new ArrayList<Loan>();
			while (rs.next()){
				loans.add(new Loan(rs.getString("client_id"), rs.getDouble("principal_amount"), rs.getDouble("interest_rate"), rs.getInt("no_years"), 
						rs.getInt("no_payments_yearly"), rs.getString("start_date")));
			}
			return loans;
		}finally{
			conn.close();
		}
	}
	
	
	/**
	 * Get a particular loan of the client based on the client id and the start
	 * date of that loan.
	 * @param cId the client id
	 * @param sDate the start date
	 * @return the loan
	 */
	public static Loan getLoan(String cId, String sDate) throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("SELECT * FROM loan WHERE client_id = '"+cId+"' AND start_date = '"+sDate+"';");
			if (rs.next())
				return new Loan(rs.getString("client_id"), rs.getDouble("principal_amount"), rs.getDouble("interest_rate"), rs.getInt("no_years"),
						rs.getInt("no_payments_yearly"), rs.getString("start_date"));
			return null;
		}finally{
			conn.close();
		}
	}
	
	/**
	 * Get the oldest loan of the client.
	 * Most calculations will be done on this assumption if the client has 
	 * more than one loan at the same time
	 * @return the oldest loan 
	 * @throws SQLException
	 */
	public Loan getOldestLoan()throws SQLException{
		Connection conn = DataSource.getConnection();
		try{
			String query = "Select * FROM loan WHERE client_id = ? AND start_date = (SELECT MIN(start_date) FROM loan WHERE client_id = ?);";
			PreparedStatement stat = conn.prepareStatement(query);
			stat.setString(1, client_id);
			stat.setString(2, client_id);
			ResultSet rs = stat.executeQuery();
			rs.next();
			return new Loan(rs.getString("client_id"), rs.getDouble("principal_amount"), rs.getDouble("interest_rate"), rs.getInt("no_years"),
					rs.getInt("no_payments_yearly"), rs.getString("start_date"));
		}finally{
			conn.close();
		}
	}
}
