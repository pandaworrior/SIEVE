package replicationlayer.core.txstore.scratchpad;
import replicationlayer.core.util.Debug;

public class ScratchpadException extends Exception
{

	public ScratchpadException() {
	}

	public ScratchpadException(String arg0) {
		super(arg0);
	}

	public ScratchpadException(Throwable arg0) {
		super(arg0);
	}

	public ScratchpadException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
