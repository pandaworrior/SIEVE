package replicationlayer.core.txstore.scratchpad.rdbms.tests;

import java.sql.*;
import java.util.*;
import replicationlayer.core.txstore.scratchpad.*;
import replicationlayer.core.txstore.scratchpad.rdbms.DBScratchpad;
import replicationlayer.core.txstore.scratchpad.rdbms.resolution.AllOpsLockExecution;
import replicationlayer.core.txstore.scratchpad.rdbms.resolution.LWWLockExecution;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBOperation;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBSelectResult;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBSingleOperation;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBUpdateResult;
import replicationlayer.core.txstore.util.LogicalClock;
import replicationlayer.core.txstore.util.ProxyTxnId;
import replicationlayer.core.txstore.util.ReadWriteSet;
import replicationlayer.core.txstore.util.Result;
import replicationlayer.core.txstore.util.TimeStamp;
import replicationlayer.core.util.Debug;

public class Test4
{

	public static void main( String[] args) {
		try {
			Debug.debug = true;
			ScratchpadConfig config = new ScratchpadConfig( "com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/test", "sa", "", "txstore.scratchpad.rdbms.DBScratchpad");
//			ScratchpadConfig config = new ScratchpadConfig( "org.h2.Driver", "jdbc:h2:test", "sa", "", "txstore.scratchpad.rdbms.DBScratchpad");
			config.putPolicy("T1", new LWWLockExecution(false));
			config.putPolicy("T2", new AllOpsLockExecution(false));
			config.putPolicy("T3", new AllOpsLockExecution(true));
			config.putPolicy("T4", new LWWLockExecution(false));
			
			long n = (new java.util.Date().getTime() / 1000) % 100000;
			
			DBScratchpad db = new DBScratchpad( config);
			db.beginTransaction( new ProxyTxnId(0,0,0));

			DBScratchpad db2 = new DBScratchpad( config);
			db2.beginTransaction( new ProxyTxnId(0,1,0));
			
			DBUpdateResult ru;
			DBSelectResult rq;

			ru = (DBUpdateResult)db.execute( new DBSingleOperation( "insert into t4 (a,b,c,d,e) values (" + n + ", " + (n%10) + ",2,3,\'S" + (n%100) + "\');"));
			System.out.println( "result = " + ru);
			ru = (DBUpdateResult)db.execute( new DBSingleOperation( "insert into t4 (a,b,c,d,e) values (" + n + ", " + (n%10) + ",2,3,\'S" + ((n+1)%100) + "\');"));
			System.out.println( "result = " + ru);
		
			rq = (DBSelectResult)db.execute( new DBSingleOperation( "select * from t4 where a > 10000 order by a limit 2;"));
			System.out.println( "query result = \n" + rq);

			ReadWriteSet rwset = db.complete();
			System.out.println( "complete = " + rwset);
			
//			ReadWriteSet rwset2 = db2.complete();
//			System.out.println( "complete = " + rwset);
			
			long []dcs = { 1, 2};
			LogicalClock lc = new LogicalClock( dcs, 1);
			TimeStamp ts = new TimeStamp( 1, n);
			
			db.commit( lc, ts);

			LogicalClock lc2 = new LogicalClock( dcs, 1);
			TimeStamp ts2 = new TimeStamp( 1, n+1);

//			db2.commit( lc2, ts2);

			db.beginTransaction( new ProxyTxnId(0,1,0));
			rq = (DBSelectResult)db.execute( new DBSingleOperation( "select * from t4;"));
			System.out.println( "query result = \n" + rq);
			db.abort();

			

			System.out.println( "Test 4 completed with success");
			
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
}
