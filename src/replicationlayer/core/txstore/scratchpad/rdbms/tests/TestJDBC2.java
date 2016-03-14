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

public class TestJDBC2
{

	public static void main( String[] args) {
		try {
			Debug.debug = true;
			// setup proxy
			ScratchpadConfig config = new ScratchpadConfig( "org.h2.Driver", "jdbc:h2:test", "sa", "", "txstore.scratchpad.rdbms.DBScratchpad");
			config.putPolicy("T1", new LWWLockExecution(false));
			config.putPolicy("T2", new AllOpsLockExecution(false));
			config.putPolicy("T3", new AllOpsLockExecution(true));
			config.putPolicy("T4", new LWWLockExecution(false));
			PassThroughProxy.config = config;
			
			long n = (new java.util.Date().getTime() / 1000) % 100000;


			Class.forName("txstore.scratchpad.rdbms.jdbc.TxMudDriver");
			Connection con = DriverManager.getConnection( "jdbc:txmud:test");
			
			Statement stat = con.createStatement();
			int k = 14;
			
			PreparedStatement ps1 = con.prepareStatement( "insert into t1 values( ?, 10, 10, 10, 'a');");
			ps1.setInt(1, k);
			ps1.executeUpdate();
			ps1.close();
			
			PreparedStatement ps2 = con.prepareStatement( "insert into t1 values( ?, 10, 10, 10, 'a');");
			ps2.setInt(1, k+1);
			ps2.executeUpdate();
			ps2.close();

			ResultSet rq = stat.executeQuery( "select * from t1;");
			Debug.println( "query result = \n" + rq);
			con.rollback();			
			
			Debug.println( "Test 2 completed with success");
			
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
}
