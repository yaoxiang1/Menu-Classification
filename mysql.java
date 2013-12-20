import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class mysql {
	public String host;
	public Connection conn;
	
	public mysql(String user, String password, String host, String database) throws ClassNotFoundException, SQLException{	
		try{		
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database , user , password);	
		}
		catch(SQLException se){   
			System.out.println("database connection failed!");   
			se.printStackTrace() ;   
	    }  
	}


	
	public ResultSet query (String query) throws SQLException{
		Statement stmt = this.conn.createStatement() ;
		ResultSet rs = stmt.executeQuery(query) ;
		return rs;
	}
	
}
