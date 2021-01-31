package clientManagment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A simple data source for getting database connections
 * @author neiral
 *
 */
public class DataSource {
	
	private static String url = "jdbc:mysql://localhost:3306/bank_management?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	private static String username = "root";
	private static String password = "";
	
	
	/**
	 * Gets a connection to the database
	 * @return the database connection
	 */
	public static Connection getConnection() throws SQLException{
		return DriverManager.getConnection(url, username, password);
	}
}