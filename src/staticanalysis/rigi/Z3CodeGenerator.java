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
import util.annotationparser.SchemaParser;

/**
 * This class is used to translate all java and sql code into Z3 code
 * 
 * TODO list
 * 	1. we first parse a project and get all paths
 *     1.0 remove all redundant paths
 *        -> Partially done, we can eliminate later (only for DeleteReservation)
 *     1.1 get all update queries per path
 *        -> Done
 *     1.2 get all select queries per path
 *        -> Done
 *     1.3 test with four applications
 *        -> Done
 *  2. we eliminate paths that either contain abort or do not contain any update statement
 *     -> Done
 *  3. we create specifications for database tables
 *     -> Aug 7th
 *  4. we gather all select queries and translate into Z3 code, create a dict for it
 *  5. we gather all update queries and translate into Z3 code, create a dict for it
 *  6. for each path, we gather path condition
 *     6.1 Propagate the branch condition to if and else paths
 *         -> Done
 *     6.2 gather path conditions
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
	
	/** The content writer */
	ContentWriter conWriter;
	
	/** The database specs */
	DatabaseSpec dbSpec;
	
	static String indentStr = "    ";
	
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
	 * @param sfPath Path to the database schema file
	 */
	public Z3CodeGenerator(String pName, String pPath, String fFile, String sfPath)
	{
		this.conWriter = new ContentWriter(pName);
		this.pjsParser = new ProjectParser(pPath, pName);
		PathAnalyzer.addFunctionMustBeProcessedListFromFile(fFile);
		this.txnCodeList = new ArrayList<CodeTransaction>();
		
		// database specs
		this.dbSpec = new DatabaseSpec(sfPath);
	}
	
	private void writeHeader() {
		this.conWriter.headerWrite();
		this.conWriter.separatorWrite();
	}
	
	private void writeDBSpec() {
		String openerStr = "def GenState():";
		this.conWriter.appendToFile(openerStr);
		List<String> dbSpecs = this.dbSpec.genTableSpecs();
		
		for(String tabSpec : dbSpecs) {
			this.conWriter.appendToFile(indentStr + tabSpec);
		}
	}
	
	/**
	 * \brief Generate all Z3 code
	 */
	
	public void generateCode() {
		this.parseProject();
		this.writeHeader();
		this.writeDBSpec();
	}
	
	
	
	/**
	 * \brief Main function
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 4)
		{
			System.out.println("[Correct Usage:] java -jar z3CodeGen-big.jar projectName pjPath filterFile schemaFile");
			System.exit(-1);
		}
		
		String projectName = args[0];
		String pjPath = args[1];
		String ffPath = args[2];
		String sfPath = args[3];
		Z3CodeGenerator codeGen = new Z3CodeGenerator(projectName, pjPath, ffPath, sfPath);
		codeGen.generateCode();
		
		
		
	}
	

}
