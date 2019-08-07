package staticanalysis.rigi;

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

/**
 * This class is used to translate all java and sql code into Z3 code
 * 
 * TODO list
 * 	1. we first parse a project and get all paths
 *     -> Done
 *     1.0 remove all redundant paths
 *        -> Partially done, we can eliminate later (only for DeleteReservation)
 *     1.1 get all update queries per path
 *        -> Done
 *     1.2 get all select queries per path
 *        -> Done
 *        -> Why there are some select queries are discarded?
 *  2. we eliminate paths that either contain abort or do not contain any update statement
 *     -> Done
 *  3. we create specifications for database tables
 *  4. we gather all select queries and translate into Z3 code, create a dict for it
 *  5. we gather all update queries and translate into Z3 code, create a dict for it
 *  6. for each path, we gather path condition
 *     -> Aug 7th
 *  7. for each path, we translate side effects
 *  8. the rest of code required by Rigi
 * @author cheng
 *
 */

public class Z3CodeGenerator {
	
	/** Project name*/
	String pjName;
	
	/** Path to the source code*/
	String srcPath;
	
	/** Project parser */
	ProjectParser pjsParser;
	
	/** Tell which set of transactions that we have to analyze*/
	String filterFile;
	
	/** The list of transaction code*/
	List<CodeTransaction> txnCodeList;
	
    private void parseProject() {
		
		//pjsParser.addFileToFileFilter("database");
		pjsParser.buildFileTree();
		//pjsParser.printOutFileTree();
		pjsParser.obtailAllControlFlowGraphs();
		//pjsParser.printOutAllControlFlowGraphs();
		pjsParser.obtainBindingsForAllMethods();
		pjsParser.resolveAllBindings();
				
		System.out.println("Finished the cfg generation");
		

		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, PathAbstraction> pathAbMap = PathAbstractionCreator
				.obtainAllPathAbstractionForWholeProject(pjsParser.getMethodCfgMapping());
				
		PathAbstractionCreator.printOutPathAbstractions(pathAbMap);
				
				
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
			CodeTransaction codeTxn = new CodeTransaction(txnName, reducedCfgList);
			this.txnCodeList.add(codeTxn);
			codeTxn.printInDetails();
		}
		
	}
	
	/**
	 * \brief Create an instance of CodeGenerator
	 * 
	 * @param pName Project name
	 * @param pPath Project sourcecode path
	 * @param fFile Path to the file containing all functions that must be analyzed
	 */
	public Z3CodeGenerator(String pName, String pPath, String fFile)
	{
		this.pjsParser = new ProjectParser(pPath, pName);
		PathAnalyzer.addFunctionMustBeProcessedListFromFile(fFile);
		this.txnCodeList = new ArrayList<CodeTransaction>();
	}
	
	/**
	 * \brief Generate all Z3 code
	 */
	
	public void generateCode() {
		this.parseProject();
	}
	
	
	
	/**
	 * \brief Main function
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 3)
		{
			System.out.println("[Correct Usage:] java -jar z3CodeGen-big.jar projectName pjPath filterFile");
			System.exit(-1);
		}
		
		String projectName = args[0];
		String pjPath = args[1];
		String ffPath = args[2];
		Z3CodeGenerator codeGen = new Z3CodeGenerator(projectName, pjPath, ffPath);
		codeGen.generateCode();
		
		
		
	}
	

}
