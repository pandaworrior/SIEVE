/**
 * 
 */
package staticanalysis.z3codegen;

import java.util.List;

/**
 * @author cheng
 *
 */
public class AppTransaction {
	
	private String txnName;
	
	List<ShadowOp> shadowOps;
	List<String> syncList;
	List<String> dependList;
	List<String> writeSet;
	
	
	public String codeGenForTransaction() {
		String codeStr = "class " + this.txnName + "(object):\n";
		
		return codeStr;
	}

	/**
	 * 
	 */
	public AppTransaction(String tName) {
		this.txnName = tName;
	}

}
