package staticanalysis.rigi;

import java.util.List;

import japa.parser.ast.expr.Expression;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.datastructures.controlflowgraph.CFGGraph;

/**
 * This class is used to represent transaction code
 * 
 * @author cheng
 *
 */

public class CodeTransaction {
	
	String txnName;
	List<CFGGraph<CodeNodeIdentifier, Expression>> reducedCfgList;
	
	public CodeTransaction(String tName, List<CFGGraph<CodeNodeIdentifier, Expression>> rList) {
		this.txnName = tName;
		this.reducedCfgList = rList;
	}
	
	public void printInShort() {
		System.out.println("txnName" + this.txnName + "; reduced cfg list size " + this.reducedCfgList.size());
	}
	
	public void printInDetails() {
		
	}

}
