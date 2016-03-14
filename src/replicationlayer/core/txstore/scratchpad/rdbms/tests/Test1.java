package replicationlayer.core.txstore.scratchpad.rdbms.tests;
import replicationlayer.core.util.Debug;

import java.sql.*;
import java.util.*;
import replicationlayer.core.txstore.scratchpad.*;
import replicationlayer.core.txstore.scratchpad.rdbms.DBScratchpad;
import replicationlayer.core.txstore.scratchpad.rdbms.resolution.AbstractDBLockExecution;
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

public class Test1
{

	public static void main( String[] args) {
		try {
			Debug.debug = true;
			
//			ScratchpadConfig config = new ScratchpadConfig( "com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/test", "sa", "", "txstore.scratchpad.rdbms.DBScratchpad");
			ScratchpadConfig config = new ScratchpadConfig( "org.h2.Driver", "jdbc:h2:test", "sa", "", "txstore.scratchpad.rdbms.DBScratchpad");
			config.putPolicy("T1", new LWWLockExecution(false));
			config.putPolicy("T2", new AllOpsLockExecution(false));
			config.putPolicy("T3", new AllOpsLockExecution(true));
			config.putPolicy("T4", new LWWLockExecution(false));
			
			long n = (new java.util.Date().getTime() / 1000) % 100000;
			
			DBScratchpad db = new DBScratchpad( config);
			
			DBUpdateResult ru;
			DBSelectResult rq;
			
			db.beginTransaction( new ProxyTxnId(0,0,0));

			ru = (DBUpdateResult)db.execute( new DBSingleOperation( "insert into t1 (a,b,c,d,e) values (" + n + ", " + (n%10) + ",2,3,\'S" + (n%100) + "\');"));
			Debug.println( "result = " + ru);

			ru = (DBUpdateResult)db.execute( new DBSingleOperation( "insert into t1 (a,b,c,d,e) values (" + (n+1) + ", " + ((n+1)%10) + ",2,3,\'S" + ((n+1)%100) + "\');"));
			Debug.println( "result = " + ru);

			ru = (DBUpdateResult)db.execute( new DBSingleOperation( "insert into t2 values (" + n + ", " + (n%5) + ",2,3,\'T" + (n%100) + "\');"));
			Debug.println( "result = " + ru);

			rq = (DBSelectResult)db.execute( new DBSingleOperation( "select * from t1 where a > 10000 limit 2;"));
			Debug.println( "query result = \n" + rq);
			
			if( rq.next()) {
				ru =  (DBUpdateResult)db.execute( new DBSingleOperation( "delete from t1 where a=" + rq.getInt(1) + ";"));
				Debug.println( "result = " + ru);
				if( rq.next()) {
					ru =  (DBUpdateResult)db.execute( new DBSingleOperation( "update t1 set c = c + 1 where a =" + rq.getInt(1) + ";"));
					Debug.println( "result = " + ru);
				}
			}

			ReadWriteSet rwset = db.complete();
			Debug.println( "complete = " + rwset);
			
			long []dcs = { 1, 2};
			LogicalClock lc = new LogicalClock( dcs, 1);
			TimeStamp ts = new TimeStamp( 1, n);
			
			db.commit( lc, ts);

			db.beginTransaction( new ProxyTxnId(0,0,1));
			rq = (DBSelectResult)db.execute( new DBSingleOperation( "select * from t1;"));
			Debug.println( "query result = \n" + rq);
			rq = (DBSelectResult)db.execute( new DBSingleOperation( "select * from t2;"));
			Debug.println( "query result = \n" + rq);
			rq = (DBSelectResult)db.execute( new DBSingleOperation( "select * from t2 where a = 99999;"));
			Debug.println( "query result = \n" + rq);
			db.abort();

			

			Debug.println( "Test 1 completed with success");
			
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
}
