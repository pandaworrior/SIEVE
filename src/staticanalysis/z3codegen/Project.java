/**
 * 
 */
package staticanalysis.z3codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import japa.parser.ast.expr.Expression;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.codeparser.ProjectParser;
import staticanalysis.datastructures.controlflowgraph.CFGGraph;
import staticanalysis.datastructures.path.PathAbstraction;
import staticanalysis.datastructures.path.ReducedPathAbstractionSet;
import staticanalysis.pathanalyzer.PathAbstractionCreator;
import staticanalysis.pathanalyzer.PathAnalyzer;
import util.annotationparser.SchemaParser;

/**
 * @author cheng
 *
 */
public class Project {
	
	/** Project parser */
	ProjectParser pjsParser;
	
	String srcPath;
	
	String pjName;
	
	/** Tell which set of transactions that we have to analyze*/
	String filterFile;
	
	SchemaParser spParser;
	
	QueryParser qryParser;
	
	List<AppTransaction> txnList;
	
	static List<String> allStates;
	
	static List<String> allArgvs;
	
	public static int getStateIndex(String stateStr) {
		int index = -1;
		index = allStates.indexOf(stateStr);
		return index;
	}
	
	public static int getArgvIndex(String argvStr) {
		int index = -1;
		index = allStates.indexOf(argvStr);
		return index;
	}
	
	public List<AppTransaction> getTxnList(){
		return this.txnList;
	}
	
	private void parseProject() {
		
		//pjsParser.addFileToFileFilter("database");
		pjsParser.buildFileTree();
		//pjsParser.printOutFileTree();
		pjsParser.obtailAllControlFlowGraphs();
		//pjsParser.printOutAllControlFlowGraphs();
		pjsParser.obtainBindingsForAllMethods();
		pjsParser.resolveAllBindings();
				
		System.out.println("Finished the cfg generation");
				
		
	}
	
	private void generateTxnList() {
		this.txnList = new ArrayList<AppTransaction>();
		allStates = new ArrayList<String>();
		allArgvs = new ArrayList<String>();
		// iterate all code and create shadow and conditions
		
		PathAnalyzer.addFunctionMustBeProcessedListFromFile(this.filterFile);
		
		
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, PathAbstraction> pathAbMap = PathAbstractionCreator
				.obtainAllPathAbstractionForWholeProject(pjsParser.getMethodCfgMapping());
				
		//PathAbstractionCreator.printOutPathAbstractions(pathAbMap);
				
				
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet> cfgPathAbMapping = 
					PathAnalyzer.obtainAllReducePathAbstractions(pathAbMap);
		
		// iterate all transactions
		
		Iterator<Entry<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet>> it = cfgPathAbMapping.entrySet().iterator();
		while(it.hasNext()){
			Entry<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet> itEntry = it.next();
			CFGGraph<CodeNodeIdentifier, Expression> cfg = itEntry.getKey();
			ReducedPathAbstractionSet rPathAbSet = itEntry.getValue();
			String txnName = cfg.getCfgIdentifier().getShortName();
			List<CFGGraph<CodeNodeIdentifier, Expression>> reducedCfgList = PathAnalyzer.obtainAllReducedControlFlowGraphs(cfg, rPathAbSet);
			System.out.println("reduced cfg list size " + reducedCfgList.size());
			
			AppTransaction appT = new AppTransaction(txnName, reducedCfgList, this.spParser);
			this.txnList.add(appT);
		}
	}

	/**
	 * 
	 */
	public Project(String name, String path, String fFile, SchemaParser _spParser, QueryParser _qryParser) {
		this.pjName = name;
		this.srcPath = path;
		this.filterFile = fFile;
		this.spParser = _spParser;
		this.qryParser = _qryParser;
		this.pjsParser = new ProjectParser(this.srcPath,
				this.pjName);	
		this.parseProject();
		this.generateTxnList();
	}

}
