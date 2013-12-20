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
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		System.out.println("!!!");
		mysql db = new mysql("root","mysql*alan","localhost","menudata");
		ResultSet rs  = db.query("select * from cs_price");
		while (rs.next()){
			System.out.print(rs.getObject("itemid")+" ");
			System.out.println(rs.getObject("price"));

		}
		System.out.println("!!!");
//		db.connection.prepareStatement(sql);
	}
}
