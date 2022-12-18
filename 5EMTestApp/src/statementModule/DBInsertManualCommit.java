package statementModule;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cez.dbUtil.DBUtil;
import com.cez.dbUtil.TimeMark;
import logModule.WriteLog;

public class DBInsertManualCommit extends DBUtil{
	
	private static String tmStamp;
	private static Connection conn;
	private static int i =0;
	private static Statement stmt;
	private static List<String> tempStamps =  new ArrayList<>();

	public static void insertDBRecords() throws Exception {
		
		conn=getConnection();
		
		// if cezdb table doesn't exists create one
		createTable("cezdb",conn);
		
		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		// set a number of records to be inserted 
		while(i<10) {
				
		// add time stamp to the table		
			tmStamp = new TimeMark().getTimeStamp();
			tempStamps.add(tmStamp);
			
			System.out.println("Record No. "+ (i+1) +" Timestamp is: "+tmStamp);
			
			
			String insertCommand = "INSERT INTO cezdb (timemark) VALUES('" + tmStamp + "') ";;

			stmt.addBatch(insertCommand);
			
		// add time stamp to local text file
			WriteLog.controlLog(tmStamp, statLog);
			
		// set a time between insertion of records - ms
			Thread.sleep(6000);
			i++;			
		} 
		i=0;
		// set for manual commit
		conn.setAutoCommit(false);
		int[] count = stmt.executeBatch();
		try {
			conn.commit();
			System.out.println("The batch of records was inserted");
		} catch (SQLException e) {
			doRollback(conn);
			System.out.println("The batch was cleared");
		}		
		conn.setAutoCommit(true);		
		DBUtil.close(conn);
	}
}






