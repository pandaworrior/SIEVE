package replicationlayer.core.txstore.scratchpad.rdbms;

import replicationlayer.core.txstore.scratchpad.ScratchpadException;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBSelectResult;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBSingleOperation;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBUpdateResult;

public interface IDatabase
{
	public DBSelectResult executeQuery( String sql) throws ScratchpadException;
	public DBUpdateResult executeUpdate( String sql) throws ScratchpadException;

}
