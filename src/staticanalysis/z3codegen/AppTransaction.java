/**
 * 
 */
package staticanalysis.z3codegen;

import java.util.ArrayList;
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
	
	private String codeGenForInitFunctionOps() {
		String codeStr = "self.ops = [";
		for(int i = 0; i < this.shadowOps.size(); i++)
		{
			codeStr += "(self.cond" + i + ", self.sop" + i + "), "; 
		}
		if(codeStr.endsWith(", "))
		{
			codeStr = codeStr.substring(0, codeStr.length() - 2);
		}
		codeStr += "])\n";
		return codeStr;
	}
	
	private String codeGenForInitFunctionSync() {
		String codeStr = "self.sync = set([";
		for(int i = 0; i < this.syncList.size(); i++)
		{
			codeStr += "\"" + this.syncList.get(i) + "\", ";
		}
		if(codeStr.endsWith(", "))
		{
			codeStr = codeStr.substring(0, codeStr.length() - 2);
		}
		codeStr += "])\n";
		return codeStr;
	}
	
	private String codeGenForInitFunctionDepend() {
		String codeStr = "self.depend = set([";
		for(int i = 0; i < this.dependList.size(); i++)
		{
			codeStr += "\"" + this.dependList.get(i) + "\", ";
		}
		if(codeStr.endsWith(", "))
		{
			codeStr = codeStr.substring(0, codeStr.length() - 2);
		}
		codeStr += "])\n";
		return codeStr;
	}
	
	private String codeGenForInitFunctionWset() {
		String codeStr = "self.write = set([";
		for(int i = 0; i < this.writeSet.size(); i++)
		{
			codeStr += "\"" + this.writeSet.get(i) + "\", ";
		}
		if(codeStr.endsWith(", "))
		{
			codeStr = codeStr.substring(0, codeStr.length() - 2);
		}
		codeStr += "])\n";
		return codeStr;
	}
	
	private String codeGenForInitFunction() { 
		String codeStr = CodeGenerator.indentStr + "def __init__(self):\n";
		codeStr += CodeGenerator.indentStr + CodeGenerator.indentStr + this.codeGenForInitFunctionOps();
		codeStr += CodeGenerator.indentStr + CodeGenerator.indentStr + this.codeGenForInitFunctionSync();
		codeStr += CodeGenerator.indentStr + CodeGenerator.indentStr + this.codeGenForInitFunctionDepend();
		codeStr += CodeGenerator.indentStr + CodeGenerator.indentStr + this.codeGenForInitFunctionWset();
		return codeStr;
	}
	
	public String codeGenForTransaction() {
		String codeStr = "class " + this.txnName + "(object):\n";
		codeStr += this.codeGenForInitFunction();
		
		// generate code of conditions and shadow operations
		for(int i = 0; i < this.shadowOps.size(); i++)
		{
			ShadowOp shOp = this.shadowOps.get(i);
			codeStr += shOp.codeGenForShadowOp();
		}
		return codeStr;
	}
	
	public String getTxnName() {
		return this.txnName;
	}

	/**
	 * 
	 */
	public AppTransaction(String tName) {
		this.txnName = tName;
		this.shadowOps = new ArrayList<ShadowOp> ();
		this.syncList = new ArrayList<String>();
		this.dependList = new ArrayList<String>();
		this.writeSet = new ArrayList<String>();
	}

}
