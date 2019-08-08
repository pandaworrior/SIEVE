package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.Iterator;
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
	List<CodePath> codePaths;
	
	public CodeTransaction(String tName, List<CFGGraph<CodeNodeIdentifier, Expression>> rList) {
		this.txnName = tName;
		this.reducedCfgList = rList;
		this.codePaths = new ArrayList<CodePath>();
		Iterator<CFGGraph<CodeNodeIdentifier, Expression>> pathIt = this.reducedCfgList.iterator();
		while(pathIt.hasNext()) {
			CFGGraph<CodeNodeIdentifier, Expression>  cfg = pathIt.next();
			CodePath cPath = new CodePath(cfg);
			cPath.findAllSqlStatmentsAndAborts();
			this.codePaths.add(cPath);
		}
		this.eliminatePaths();
	}
	
	/**
	 * \brief Iterate all reduced paths and remove if it contains no update statements
	 */
	public void eliminatePaths() {
		Iterator<CodePath> pathIt = this.codePaths.iterator();
		while(pathIt.hasNext()) {
			CodePath  cPath = pathIt.next();
			if(cPath.isReadOnly()) {
				System.out.println("Hi we remove a path from " + this.txnName);
				pathIt.remove();
			}
		}
	}
	
	
	public boolean isAxiomRequired() {
		for(CodePath cP : this.codePaths) {
			if(cP.isAxiomRequired()) {
				return true;
			}
		}
		return false;
	}
	
	public void printInShort() {
		System.out.println("txnName" + this.txnName + "; reduced cfg list size " + this.reducedCfgList.size() + "; updated path num " + this.codePaths.size());
	}
	
	public void printInDetails() {
		System.out.println("txnName" + this.txnName + "; reduced cfg list size " + this.reducedCfgList.size() + "; updated path num " + this.codePaths.size());
		for(int i = 0; i < this.codePaths.size(); i++) {
			this.codePaths.get(i).printOut();
		}
	}

}
