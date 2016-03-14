package replicationlayer.core.txstore.scratchpad.rdbms;

import replicationlayer.core.txstore.util.Result;

public interface IPrimaryExec
{
	public void addResult( Result r); 
	public Result getResult( int pos);
}
