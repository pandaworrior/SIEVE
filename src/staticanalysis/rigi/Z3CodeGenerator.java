package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import japa.parser.ast.expr.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
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
 *     -> Done
 *  4. for each path, we gather path condition
 *     4.1 Propagate the branch condition to if and else paths
 *         -> Done
 *     4.2 gather path conditions
 *         -> Done
 *     4.3 translate select and include select into path condition
 *         -> Done
 *  5. side effects collection
 *     5.1 create functions for all paths
 *         -> Done
 *     5.2 for each path, we translate side effects
 *         -> Update Done
 *         -> Insert
 *         -> Delete
 *  6. Applying Rigi to apps
 *     6.1 SmallBank
 *         -> Done
 *     6.2 Seats
 *         -> UpdateReservation Done
 *     6.3 RUBiS
 *         
 *  7. make courseware run with vasco
 *     -> Aug 9th
 * @author cheng
 *
 */

public class Z3CodeGenerator {
	
	/** Project name*/
	String pjName;
	
	/** Path to the source code*/
	String srcPath;
	
	/** Project parser */
	static ProjectParser pjsParser;
	
	/** Tell which set of transactions that we have to analyze*/
	String filterFile;
	
	/** The list of transaction code*/
	List<CodeTransaction> txnCodeList;
	
	/** static hash information*/
	static HashMap<String, CodeTransaction> txnMap;
	
	/** The content writer */
	ContentWriter conWriter;
	
	/** The database specs */
	DatabaseSpec dbSpec;
	
	/** The sql parser*/
	static CCJSqlParserManager cJsqlParser;
	
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
			CodeTransaction codeTxn = new CodeTransaction(txnName, reducedCfgList, this.dbSpec.dbSchemaParser);
			if(!codeTxn.codePaths.isEmpty()) {
				this.txnCodeList.add(codeTxn);
				txnMap.put(txnName, codeTxn);
			}
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
		this.pjName = pName;
		//create sql parser
		cJsqlParser = new CCJSqlParserManager();
		
		this.conWriter = new ContentWriter(pName);
		this.pjsParser = new ProjectParser(pPath, pName);
		PathAnalyzer.addFunctionMustBeProcessedListFromFile(fFile);
		this.txnCodeList = new ArrayList<CodeTransaction>();
		
		// database specs
		this.dbSpec = new DatabaseSpec(sfPath);
		txnMap = new HashMap<String, CodeTransaction>();
	}
	
	private void writeHeader() {
		this.conWriter.headerWrite();
		this.conWriter.separatorWrite();
	}
	
	private void writeFooter() {
		this.conWriter.appendToFile("\ncheck(" + this.pjName + "())");
	}
	
	private void writeDBSpec() {
		
		//table definition
		List<String> dbSpecs = this.dbSpec.genTableSpecs();
		
		for(String tabSpec : dbSpecs) {
			this.conWriter.appendToFile(tabSpec);
		}
		
		//instance definition and states
		
		String openerStr = "def GenState():";
		this.conWriter.appendToFile(openerStr);
		
		List<String> stateSpecs = this.dbSpec.genStateSpecs();
		for(String insSpec : stateSpecs) {
			this.conWriter.appendToFile(CommonDef.indentStr + insSpec);
		}
	}
	
	private void writeArgvSpec() {
		this.conWriter.appendToFile("\ndef GenArgv():");
		this.conWriter.appendToFile(CommonDef.indentStr + "builder = ArgvBuilder()");
		for(CodeTransaction cTxn : this.txnCodeList) {
			List<String> argvSpecs = cTxn.genArgvSpecs();
			for(String argvSp : argvSpecs) {
				this.conWriter.appendToFile(CommonDef.indentStr + argvSp);
			}
			this.conWriter.appendToFile("\n");
		}
		
		this.conWriter.appendToFile(CommonDef.indentStr + "return builder.Build()\n");
	}
	
	private void writeTxnSpec() {
		for(CodeTransaction cTxn : this.txnCodeList) {
			List<String> txnSpecs = cTxn.genTxnSpecs();
			for(String entry : txnSpecs) {
				this.conWriter.appendToFile(entry);
			}
			this.conWriter.appendToFile("\n");
		}
	}
	
	private void writeAppSpec() {
		this.conWriter.appendToFile("\nclass " + this.pjName + "():");
		this.conWriter.appendToFile(CommonDef.indentStr + CommonDef.initFuncStr);
		
		// for ops
		String opsStr = "self.ops = [";
		for(CodeTransaction cTxn : this.txnCodeList) {
			opsStr += "Op_" + cTxn.txnName + "(),";
		}
		
		if(opsStr.endsWith(",")) {
			opsStr = opsStr.substring(0, opsStr.length() - 1);
		}
		opsStr += "]";
		this.conWriter.appendToFile(CommonDef.indentStr + CommonDef.indentStr + opsStr);
		
		//for tables
		this.conWriter.appendToFile(CommonDef.indentStr + CommonDef.indentStr + "self.tables = " + this.dbSpec.genTableNames());
		
		// for states
		this.conWriter.appendToFile(CommonDef.indentStr + CommonDef.indentStr + "self.state = GenState");
		
		// for argv
		this.conWriter.appendToFile(CommonDef.indentStr + CommonDef.indentStr + "self.argv = GenArgv");
		
		// for axiom
		this.conWriter.appendToFile(CommonDef.indentStr + CommonDef.indentStr + "self.axiom = BuildArgvAxiom(self.ops)");
	}
	
	/**
	 * \brief Generate all Z3 code
	 */
	
	public void generateCode() {
		this.parseProject();
		this.writeHeader();
		this.writeDBSpec();
		this.writeArgvSpec();
		this.writeTxnSpec();
		this.writeAppSpec();
		this.writeFooter();
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
