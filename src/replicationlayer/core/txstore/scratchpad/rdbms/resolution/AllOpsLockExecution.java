package replicationlayer.core.txstore.scratchpad.rdbms.resolution;
import replicationlayer.core.util.Debug;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import replicationlayer.core.txstore.scratchpad.rdbms.DBScratchpad;
import replicationlayer.core.txstore.scratchpad.rdbms.IDBScratchpad;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBSingleOpPair;
import replicationlayer.core.txstore.scratchpad.resolution.ExecutionPolicy;
import replicationlayer.core.txstore.util.LogicalClock;
import replicationlayer.core.txstore.util.TimeStamp;

public class AllOpsLockExecution
	extends AbstractDBLockExecution
{

	public AllOpsLockExecution(boolean blue) {
		super(blue);
	}
	/**
	 * Returns an unitialized fresh copy of this execution policy
	 */
	public ExecutionPolicy duplicate() {
		return new AllOpsLockExecution( super.blue);
	}

	@Override
	protected void executeDefOpInsert(DBSingleOpPair op, Insert dbOp, IDBScratchpad db, LogicalClock lc, TimeStamp ts, boolean b)
			throws SQLException {
		Debug.println( "DEF-ALL>>>>" + op.op);

		if( b && DBScratchpad.SQL_ENGINE == DBScratchpad.RDBMS_MYSQL)
			db.addToBatchUpdate( "delete from " + super.tempTableName + ";");
			
		StringBuffer buffer = new StringBuffer();
		buffer.append( "insert into ");
		buffer.append( def.name);
		List s = dbOp.getColumns();
		if( s == null) {
			buffer.append( "(");
			buffer.append(def.getPlainFullColumnList());
			buffer.append( ")");
		} else {
			buffer.append( "(");
			Iterator it = s.iterator();
			while( it.hasNext()) {
				buffer.append(it.next());
				buffer.append(",");
			}
			buffer.append( DBScratchpad.SCRATCHPAD_COL_DELETED);
			buffer.append(",");
			buffer.append( DBScratchpad.SCRATCHPAD_COL_TS);
			buffer.append(",");
			buffer.append( DBScratchpad.SCRATCHPAD_COL_VV);
			buffer.append( ")");
		}
		buffer.append( " values (");
		Iterator it = ((ExpressionList)dbOp.getItemsList()).getExpressions().iterator();
		while( it.hasNext()) {
			buffer.append( it.next());
			buffer.append(",");
		}
		buffer.append(" FALSE, ");
		buffer.append( ts.toIntString());
		buffer.append(",'");
		buffer.append( lc.toString());
		buffer.append( "');");

		try {
			db.executeBatch();
			Debug.println( "DEF:" + buffer.toString());
			db.executeUpdate( buffer.toString());
			return;
		} catch( SQLException e) {
			// do nothing
			e.printStackTrace();
		}
		buffer = new StringBuffer();
		buffer.append( "update ");
		buffer.append( def.name);
		buffer.append( " set ");
		s = dbOp.getColumns();
		if( s == null) {
			Iterator expIt = ((ExpressionList)dbOp.getItemsList()).getExpressions().iterator();
			for( int i = 0; i < def.colsPlain.length; i++) {
				if( def.colsPlain[i].startsWith( DBScratchpad.SCRATCHPAD_COL_PREFIX))
					continue;
				buffer.append( def.colsPlain[i]);
				buffer.append( " = ");
				buffer.append( expIt.next());
				buffer.append( " , ");
			}
		} else {
			Iterator colIt = s.iterator();
			Iterator expIt = ((ExpressionList)dbOp.getItemsList()).getExpressions().iterator();
			while( colIt.hasNext()) {
				buffer.append( colIt.next());
				buffer.append( " = ");
				buffer.append( expIt.next());
				buffer.append( " , ");
			}
		}
		buffer.append( DBScratchpad.SCRATCHPAD_COL_DELETED);
		buffer.append(" = FALSE, ");
		buffer.append( DBScratchpad.SCRATCHPAD_COL_TS);
		buffer.append(" = ");
		buffer.append( ts.toIntString());
		buffer.append(", ");
		buffer.append( DBScratchpad.SCRATCHPAD_COL_VV);
		buffer.append(" = '");
		buffer.append( lc.toString());
		buffer.append("' where ");
		String[] pkNames = def.getPksPlain();
		for( int i = 0; i < pkNames.length; i++) {
			if( i > 0)
				buffer.append(" AND ");
			buffer.append( pkNames[i]);
			buffer.append(" = ");
			buffer.append( op.pk[i]);
		}
		buffer.append(";");
		
		Debug.println( "DEF2:" + buffer.toString());
		db.addToBatchUpdate( buffer.toString());
		
	}

	@Override
	protected void executeDefOpDelete(DBSingleOpPair op, Delete dbOp, IDBScratchpad db, LogicalClock lc, TimeStamp ts, boolean b)
			throws SQLException {
		Debug.println( "DEF-ALL>>>>" + op.op);
		
		if( b && DBScratchpad.SQL_ENGINE == DBScratchpad.RDBMS_MYSQL)
			db.addToBatchUpdate( "delete from " + super.tempTableName + ";");
		StringBuffer buffer = new StringBuffer();
		buffer.append( "update ");
		buffer.append( def.name);
		buffer.append( " set ");
		buffer.append( DBScratchpad.SCRATCHPAD_COL_DELETED);
		buffer.append( "  = TRUE, ");
		buffer.append( DBScratchpad.SCRATCHPAD_COL_TS);
		buffer.append( " = ");
		buffer.append( ts.toIntString());
		buffer.append( " , ");
		buffer.append( DBScratchpad.SCRATCHPAD_COL_VV);
		buffer.append( " = ");
		buffer.append( lc.toString());
		buffer.append( " where ");
		String[] pkNames = def.getPksPlain();
		for( int i = 0; i < pkNames.length; i++) {
			if( i > 0)
				buffer.append(" AND ");
			buffer.append( pkNames[i]);
			buffer.append(" = ");
			buffer.append( op.pk[i]);
		}
		buffer.append( ";");
		
		Debug.println( "DEF:" + buffer.toString());
		db.addToBatchUpdate( buffer.toString());
	}

	/**
	 * Execute update operation in the final table
	 * @param op
	 * @param dbOp
	 * @param db
	 * @return
	 * @throws SQLException
	 */
	protected void executeDefOpUpdate(DBSingleOpPair op, Update dbOp, IDBScratchpad db, LogicalClock lc, TimeStamp ts, boolean b) throws SQLException {
		Debug.println( "DEF-ALL>>>>" + op.op);
		
		if( b && DBScratchpad.SQL_ENGINE == DBScratchpad.RDBMS_MYSQL)
			db.addToBatchUpdate( "delete from " + super.tempTableName + ";");
		StringBuffer buffer = new StringBuffer();
		buffer.append( "update ");
		buffer.append( def.name);
		buffer.append( " set ");
		Iterator colIt = dbOp.getColumns().iterator();
		Iterator expIt = dbOp.getExpressions().iterator();
		while( colIt.hasNext()) {
			buffer.append( colIt.next());
			buffer.append( " = ");
			buffer.append( expIt.next());
			buffer.append( " , ");
		}
		buffer.append( DBScratchpad.SCRATCHPAD_COL_TS);
		buffer.append( " = ");
		buffer.append( ts.toIntString());
		buffer.append( " , ");
		buffer.append( DBScratchpad.SCRATCHPAD_COL_VV);
		buffer.append( " = '");
		buffer.append( lc.toString());
		buffer.append( "' where ");
		String[] pkNames = def.getPksPlain();
		for( int i = 0; i < pkNames.length; i++) {
			if( i > 0)
				buffer.append(" AND ");
			buffer.append( pkNames[i]);
			buffer.append(" = ");
			buffer.append( op.pk[i]);
		}
		buffer.append( ";");
		
		Debug.println( "DEF:" + buffer.toString());
		db.addToBatchUpdate( buffer.toString());
		Debug.println( "add batch update finish");
	}

}
