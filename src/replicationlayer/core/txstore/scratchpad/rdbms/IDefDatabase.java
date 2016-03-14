package replicationlayer.core.txstore.scratchpad.rdbms;

import java.sql.ResultSet;
import java.sql.SQLException;

import replicationlayer.core.txstore.scratchpad.ScratchpadException;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBSelectResult;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBSingleOperation;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBUpdateResult;

public interface IDefDatabase
{
	public ResultSet executeQuery( String sql) throws SQLException;
	public int executeUpdate( String sql) throws SQLException, ScratchpadException;
	public int executeOp( String sql) throws SQLException, ScratchpadException;
	public void addCleanUpToBatch(String sql) throws SQLException;

}
