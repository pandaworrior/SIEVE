package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import japa.parser.ast.expr.Expression;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.datastructures.controlflowgraph.CFGGraph;
import util.annotationparser.SchemaParser;
import util.crdtlib.dbannotationtypes.dbutil.DataField;

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
	
	/** The DB Schema Parser. */
	SchemaParser dbSchemaParser;
	
	public CodeTransaction(String tName, List<CFGGraph<CodeNodeIdentifier, Expression>> rList,
			SchemaParser _sp) {
		this.txnName = tName;
		this.reducedCfgList = rList;
		this.codePaths = new ArrayList<CodePath>();
		this.dbSchemaParser = _sp;
		Iterator<CFGGraph<CodeNodeIdentifier, Expression>> pathIt = this.reducedCfgList.iterator();
		while(pathIt.hasNext()) {
			CFGGraph<CodeNodeIdentifier, Expression>  cfg = pathIt.next();
			CodePath cPath = new CodePath(this.txnName, cfg, this.dbSchemaParser);
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
	
	public List<String> genArgvSpecs(){
		List<String> argvSpecs = new ArrayList<String>();
		argvSpecs.add("builder.NewOp(\'" + this.txnName + "\')");
		
		HashMap<String, DataField> allArgvs = new HashMap<String, DataField>();
		
		//remove duplicates
		for(CodePath cP : this.codePaths) {
			HashMap<String, DataField> argvMap = cP.getArgvMap();
			Iterator<Entry<String, DataField>> argvIt = argvMap.entrySet().iterator();
			while(argvIt.hasNext()) {
				Entry<String, DataField> argvEntry = argvIt.next();
				allArgvs.put(argvEntry.getKey(), argvEntry.getValue());
			}
		}
		
		//gen specs
		Iterator<Entry<String, DataField>> argvItForTxn = allArgvs.entrySet().iterator();
		while(argvItForTxn.hasNext()) {
			Entry<String, DataField> argvEntry = argvItForTxn.next();
			allArgvs.put(argvEntry.getKey(), argvEntry.getValue());
			String typeStr = CommonDef.getArgvBuilderType(argvEntry.getValue());
			argvSpecs.add("builder.AddArgv(\'" + argvEntry.getKey() + "\'," + typeStr + ")");
		}
		return argvSpecs;
	}
	
	public List<String> genTxnSpecs(){
		List<String> txnSpecs = new ArrayList<String>();
		txnSpecs.add("class Op_" + this.txnName + "():");
		txnSpecs.add(CommonDef.indentStr + CommonDef.initFuncStr);
		
		//init body str
		String initBodyStr = CommonDef.indentStr + CommonDef.indentStr + "self.ops = [";
		for(int i = 0; i < this.codePaths.size(); i++) {
			String pathTempStr = "(";
			pathTempStr += "self.cond" + i + ", self.csop" + i + ", self.sop" + i + "),";
			initBodyStr += pathTempStr;
		}
		
		if(initBodyStr.endsWith(",")) {
			initBodyStr = initBodyStr.substring(0, initBodyStr.length() - 1);
		}
		txnSpecs.add(initBodyStr + "\n");
		
		//add code for path
		for(int i = 0; i < this.codePaths.size(); i++) {
			List<String> pathSpecs = this.codePaths.get(i).genCodePathSpec(i);
			for(String entry : pathSpecs) {
				txnSpecs.add(CommonDef.indentStr + entry);
			}
			txnSpecs.add("\n");
		}
		return txnSpecs;
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
