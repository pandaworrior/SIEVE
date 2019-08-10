package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import japa.parser.ast.type.Type;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.Expression;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.datastructures.controlflowgraph.CFGGraph;
import staticanalysis.datastructures.controlflowgraph.CFGNode;
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
	
	/** user inputs*/
	HashMap<String, Type> userInputs;
	
	public CodeTransaction(String tName, List<CFGGraph<CodeNodeIdentifier, Expression>> rList,
			SchemaParser _sp) {
		this.txnName = tName;
		this.reducedCfgList = rList;
		this.codePaths = new ArrayList<CodePath>();
		this.dbSchemaParser = _sp;
		this.userInputs = new HashMap<String, Type>();
		
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
	 * get the user inputs from method delcaration
	 */
	
	private void processUserInputs() {
		HashMap<MethodDeclaration, CFGGraph<CodeNodeIdentifier, Expression>> methodMap = Z3CodeGenerator.pjsParser.getMethodCfgMapping();
		
		CFGGraph<CodeNodeIdentifier, Expression> pathCfg = this.reducedCfgList.get(0);
		if((pathCfg.getNodeList() !=null) && (pathCfg.getNodeList().size() > 0)) {
			CFGNode<CodeNodeIdentifier, Expression> cNode = pathCfg.getNodeList().get(0);
			CodeNodeIdentifier cId = cNode.getNodeId();
			Iterator<Entry<MethodDeclaration, CFGGraph<CodeNodeIdentifier, Expression>>> methodIt = methodMap.entrySet().iterator();
			while(methodIt.hasNext()) {
				Entry<MethodDeclaration, CFGGraph<CodeNodeIdentifier, Expression>> entry = methodIt.next();
				MethodDeclaration meDecl = entry.getKey();
				CFGGraph<CodeNodeIdentifier, Expression> cfg = entry.getValue();
				if(cfg.getCfgIdentifier().getShortName().contentEquals(txnName)) {
					List<Parameter> userDefParams = meDecl.getParameters();
					for(Parameter p : userDefParams) {
						if(!p.getType().toString().contentEquals("Connection") &&
								!p.getType().toString().contentEquals("HttpServletRequest") &&
										!p.getType().toString().contentEquals("HttpServletResponse")) {
							System.out.println(p.toString() + " " + p.getId().toString());
							this.userInputs.put(p.getId().toString(), p.getType());
						}
					}
				}
			}
		}
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
		//first process the user input parameters
		this.processUserInputs();
		
		List<String> argvSpecs = new ArrayList<String>();
		argvSpecs.add("builder.NewOp(\'" + this.txnName + "\')");
		
		//gen specs for user inputs
		Iterator<Entry<String, Type>> userInputIt = this.userInputs.entrySet().iterator();
		while(userInputIt.hasNext()) {
			Entry<String, Type> uInput = userInputIt.next();
			String typeStr = CommonDef.getArgvBuilderType(uInput.getValue());
			argvSpecs.add("builder.AddArgv(\'" + uInput.getKey() + "\'," + typeStr + ")");
		}
		
		HashMap<String, DataField> allArgvs = new HashMap<String, DataField>();
		
		//remove duplicates
		for(CodePath cP : this.codePaths) {
			HashMap<String, DataField> argvMap = cP.getArgvMap();
			Iterator<Entry<String, DataField>> argvIt = argvMap.entrySet().iterator();
			while(argvIt.hasNext()) {
				Entry<String, DataField> argvEntry = argvIt.next();
				if(!this.userInputs.keySet().contains(argvEntry.getKey())) {
					allArgvs.put(argvEntry.getKey(), argvEntry.getValue());
				}
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
		String initBodyStr = CommonDef.indentStr + CommonDef.indentStr + "self.sops = [";
		for(int i = 0; i < this.codePaths.size(); i++) {
			String pathTempStr = "(";
			pathTempStr += "self.cond" + i + ", self.csop" + i + ", self.sop" + i + "),";
			initBodyStr += pathTempStr;
		}
		
		if(initBodyStr.endsWith(",")) {
			initBodyStr = initBodyStr.substring(0, initBodyStr.length() - 1);
		}
		initBodyStr += "]";
		txnSpecs.add(initBodyStr);
		
		//add axiom here
		int num_of_axioms = 0;
		for(int i = 0; i < this.codePaths.size(); i++) {
			List<Axiom> axioms = this.codePaths.get(i).axioms;
			num_of_axioms += axioms.size();
			for(Axiom axiom : axioms) {
				txnSpecs.add(CommonDef.indentStr + CommonDef.indentStr + axiom.genAxiomSpec());
			}
		}
		
		if(num_of_axioms == 0) {
			txnSpecs.add(CommonDef.indentStr + CommonDef.indentStr + "self.axiom = AxiomEmpty()");
		}
		txnSpecs.add("\n");
		
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
