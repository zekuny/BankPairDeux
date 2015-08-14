
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;



public class DBConnection {
	public static Connection connectDB() throws SQLException{
		//URL of Oracle database server
        String url = "jdbc:oracle:thin:testuser/password@localhost"; 
        //properties for creating connection to Oracle database
        Properties props = new Properties();
        props.setProperty("user", "testdb");
        props.setProperty("password", "password");
      
        //creating connection to Oracle database using JDBC
        return DriverManager.getConnection(url,props);
	}
}
