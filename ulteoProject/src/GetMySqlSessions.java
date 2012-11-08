import java.sql.*;
/**
 * 
 * @author Alon
 *
 */
public final class GetMySqlSessions {
	                                                                
		private static final String  DB_URL = "jdbc:mysql://";
	private static final String  JDBC_DRIVER = "com.mysql.jdbc.Driver";
		
		private GetMySqlSessions() {
			// private constructor to prevent initialization.
		}

   public  static int sessionCount (String dbIp, String userName, String passWord, String hostIp)  throws Exception {
	   Connection conn = null;
	   Statement stmt = null;
       int sessionCount = 0;
       
	   try{
		      Class.forName(JDBC_DRIVER);
		      conn = DriverManager.getConnection(DB_URL + dbIp + "/ovd", userName, passWord);
      
		      // Execute a query
		      stmt = conn.createStatement();
		      String sql = "select count(*) from ovd.ulteo_sessions where status = 'logged' and server = '" + hostIp + "';";

		      ResultSet rs = stmt.executeQuery(sql);
		      
		      // Extract data from result set
		      while(rs.next()){
		         sessionCount = rs.getInt("count(*)");
		         return  sessionCount;
		      }
		      rs.close();

	   		}catch(SQLException se){  //Handle errors for JDBC
	   			System.out.println("Where is your MySQL JDBC Driver?"  + se.getMessage() );
		        se.printStackTrace();
		   }catch(Exception e){ //Handle errors for Class.forName
			   System.out.println("Error on Register Class JDBC Driver?"  + e.getMessage() ); 	
		       e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		   			System.out.println("Error close connection "  + se.getMessage() );
		    	    se.printStackTrace();
		      }//end finally try
		   }//end try
			return sessionCount;
   }
}