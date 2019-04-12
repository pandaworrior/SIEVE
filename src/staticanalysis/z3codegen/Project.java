/**
 * 
 */
package staticanalysis.z3codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
				
		PathAnalyzer.addFunctionMustBeProcessedListFromFile(this.filterFile);
				
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, PathAbstraction> pathAbMap = PathAbstractionCreator
				.obtainAllPathAbstractionForWholeProject(pjsParser.getMethodCfgMapping());
				
		//PathAbstractionCreator.printOutPathAbstractions(pathAbMap);
				
				
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet> cfgPathAbMapping = 
					PathAnalyzer.obtainAllReducePathAbstractions(pathAbMap);
		
		//get all reduced control flow graph
		List<CFGGraph<CodeNodeIdentifier, Expression>> reducedCfgList = PathAnalyzer.obtainAllReducedCfgGraphs(cfgPathAbMapping);
				
		System.out.println("reduced cfg list size " + reducedCfgList.size());
		
	}
	
	private void generateTxnList() {
		this.txnList = new ArrayList<AppTransaction>();
		// iterate all code
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
