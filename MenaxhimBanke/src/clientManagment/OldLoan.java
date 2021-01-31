package clientManagment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Contains relevant information about old loans, i.e. loans that have been 
 * paid by the client. 
 * @author neiral
 *
 */
public class OldLoan extends Loan{
	private String endDate;
	private int noIrregularPayments;
	
	public OldLoan(String cId, double pAmount, String sDate, String eDate, int nPayments){
		super(cId, pAmount, sDate);
		this.endDate = eDate;
		this.noIrregularPayments = nPayments;
	}
	
	/**
	 * Get the number of irregular payments
	 * @return the number of irregular payments
	 */
	public int getIrregularPayments(){return this.noIrregularPayments;}

	/**
	 * Get the end date of the loan
	 * @return the end date
	 */
	public String getEndDate(){return this.endDate;}

	/**
	 * Get the start date of the loan
	 * @return the start date
	 */
	public String getStartDate(){return super.getDate();}
	
	/**
	 * Get the principal amount
	 */
	public double getPrincipalAmount(){return super.getPrincipalAmount();}
	
	/**
	 * Get the client id
	 */
	public String getClientId(){return super.getClientId();} 
	
	/**
	 * Get former loans that the client might have had
	 * @param clientId the client id
	 * @return an ArrayList containing former loans
	 */
	public static ArrayList<OldLoan> getFormerLoans(String clientId) throws SQLException{
		Connection conn = DataSource.getConnection();
		ArrayList<OldLoan> loans = null;
		try{
			
			PreparedStatement stat = conn.prepareStatement("SELECT * FROM loan_history WHERE client_id = ?");
			stat.setString(1, clientId);
			ResultSet rs = stat.executeQuery();
			
			if (rs.next()){
				loans = new ArrayList<OldLoan>();
				loans.add(new OldLoan(rs.getString("client_id"), rs.getDouble("principal_amount"), rs.getString("start_date"), rs.getString("end_date"),
						rs.getInt("no_irregular_payments")));
			}
			
			while (rs.next()){
				loans.add(new OldLoan(rs.getString("client_id"), rs.getDouble("principal_amount"), rs.getString("start_date"), rs.getString("end_date"),
						rs.getInt("no_irregular_payments")));
			}
			return loans;
		}finally{
			conn.close();
		}
	}
}
