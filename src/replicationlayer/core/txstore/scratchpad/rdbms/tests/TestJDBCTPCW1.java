package replicationlayer.core.txstore.scratchpad.rdbms.tests;
import replicationlayer.core.util.Debug;

import java.sql.*;
import java.util.*;
import replicationlayer.core.txstore.scratchpad.*;
import replicationlayer.core.txstore.scratchpad.rdbms.DBScratchpad;
import replicationlayer.core.txstore.scratchpad.rdbms.jdbc.PassThroughProxy;
import replicationlayer.core.txstore.scratchpad.rdbms.resolution.AllOpsLockExecution;
import replicationlayer.core.txstore.scratchpad.rdbms.resolution.LWWLockExecution;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBOperation;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBSelectResult;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBUpdateResult;
import replicationlayer.core.txstore.util.LogicalClock;
import replicationlayer.core.txstore.util.ProxyTxnId;
import replicationlayer.core.txstore.util.ReadWriteSet;
import replicationlayer.core.txstore.util.Result;
import replicationlayer.core.txstore.util.TimeStamp;

public class TestJDBCTPCW1
{

	public static void main( String[] args) {
		try {
			// setup proxy
			ScratchpadConfig config = new ScratchpadConfig( "com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/tpcwdb_mysql_scratchpad", "sa", "", "txstore.scratchpad.rdbms.DBScratchpad");
//			ScratchpadConfig config = new ScratchpadConfig( "org.h2.Driver", "jdbc:h2:tpcwdb_mysql_scratchpad", "sa", "", "txstore.scratchpad.rdbms.DBScratchpad");
			config.putPolicy("address", new LWWLockExecution(false));
			config.putPolicy("author", new LWWLockExecution(false));
			config.putPolicy("cc_xacts", new LWWLockExecution(false));
			config.putPolicy("country", new LWWLockExecution(false));
			config.putPolicy("customer", new LWWLockExecution(false));
			config.putPolicy("item", new LWWLockExecution(false));
			config.putPolicy("order_line", new LWWLockExecution(false));
			config.putPolicy("orders", new LWWLockExecution(false));
			config.putPolicy("shopping_cart", new LWWLockExecution(false));
			config.putPolicy("shopping_cart_line", new LWWLockExecution(false));

			PassThroughProxy.config = config;
			
			long n = (new java.util.Date().getTime() / 1000) % 100000;


			Class.forName("txstore.scratchpad.rdbms.jdbc.TxMudDriver");
			Connection con = DriverManager.getConnection( "jdbc:txmud:test");
			
			Statement stat = con.createStatement();
			
			
			
/*			int ru = stat.executeUpdate("insert into t1 (a,b,c,d,e) values (" + n + ", " + (n%10) + ",2,3,\'S" + (n%100) + "\');");
			Debug.println( "result = " + ru);
*/
			ResultSet rq = stat.executeQuery( "SELECT c_fname,c_lname FROM customer WHERE c_id = 9974;");
			Debug.println( "query result = \n" + rq);
			
			con.commit();

			Debug.println( "Test completed with success");
			
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
}
