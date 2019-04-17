/********************************************************************
Copyright (c) 2013 chengli.
All rights reserved. This program and the accompanying materials
are made available under the terms of the GNU Public License v2.0
which accompanies this distribution, and is available at
http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

Contributors:
    chengli - initial API and implementation

Contact:
    To distribute or use this code requires prior specific permission.
    In this case, please contact chengli@mpi-sws.org.
********************************************************************/
/**
 * 
 */
package test.staticanalysis.templatecreator;

import japa.parser.ast.expr.Expression;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.codeparser.JavaFileParser;
import staticanalysis.codeparser.ProjectParser;
import staticanalysis.pathanalyzer.PathAbstractionCreator;
import staticanalysis.pathanalyzer.PathAnalyzer;
import staticanalysis.templatecreator.TemplateCreator;
import util.annotationparser.Invariant;
import util.annotationparser.InvariantParser;
import util.annotationparser.SchemaParser;
import staticanalysis.datastructures.controlflowgraph.CFGGraph;
import staticanalysis.datastructures.path.PathAbstraction;
import staticanalysis.datastructures.path.ReducedPathAbstractionSet;
import util.debug.Debug;

// TODO: Auto-generated Javadoc
/**
 * The Class TemplateCreatorTest.
 */
public class TemplateCreatorTest {
	
	static String projectPath = "";
	static String projectName = "";
	static String schemaPath = "";
	static String txnMustedProcessFile = "";
	static String invariantFile = "";
	
	public static void config(String pName) {
		
		projectName = pName;
		if(projectName.contentEquals("smallbank")) {
			projectPath = "/home/cheng/Code/explicit_tradeoff/code/benchmark/oltpbench/src/com/oltpbenchmark/benchmarks/smallbank";
			schemaPath = "/home/cheng/Code/explicit_tradeoff/code/benchmark/oltpbench/config/olisipo/smallbank/smallbank_olisipo.sql";
			txnMustedProcessFile = "/home/cheng/Code/explicit_tradeoff/code/benchmark/oltpbench/config/olisipo/smallbank/smallbank_txn_mustprocess.txt";
			invariantFile = "/home/cheng/Code/explicit_tradeoff/code/benchmark/oltpbench/config/olisipo/smallbank/smallbank_inv.txt";
		}else if (projectName.contentEquals("seats")) {
			projectPath = "/home/cheng/Code/explicit_tradeoff/code/benchmark/oltpbench/src/com/oltpbenchmark/benchmarks/seats";
			schemaPath = "/home/cheng/Code/explicit_tradeoff/code/benchmark/oltpbench/config/olisipo/seats/seats_olisipo.sql";
			txnMustedProcessFile = "/home/cheng/Code/explicit_tradeoff/code/benchmark/oltpbench/config/olisipo/seats/seats_txn_mustprocess.txt";
			invariantFile = "/home/cheng/Code/explicit_tradeoff/code/benchmark/oltpbench/config/olisipo/seats/seats_inv.txt";
		}else {
			throw new RuntimeException("No support for this project");
		}
		
	}
	
	public static void generateCode() {
		ProjectParser pjsParser = new ProjectParser(projectPath,
				projectName);
		// pjsParser.addFileToFileFilter("TPCW_Database.java"); //must exclude since duplicates
		
		pjsParser.buildFileTree();
		//pjsParser.printOutFileTree();
		
		pjsParser.obtailAllControlFlowGraphs();
		//pjsParser.printOutAllControlFlowGraphs();
		pjsParser.obtainBindingsForAllMethods();
		pjsParser.resolveAllBindings();
		//pjsParser.printOutAllControlFlowGraphs();
		
		PathAnalyzer.addFunctionMustBeProcessedListFromFile(txnMustedProcessFile);
		
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, PathAbstraction> pathAbMap = PathAbstractionCreator
		.obtainAllPathAbstractionForWholeProject(pjsParser.getMethodCfgMapping());
		
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet> cfgPathAbMapping = 
				PathAnalyzer.obtainAllReducePathAbstractions(pathAbMap);
		
		//define a annotation parser
		SchemaParser sP = new SchemaParser(schemaPath);
		sP.parseAnnotations();
		Invariant.setSchemaParser(sP);
		InvariantParser invP = new InvariantParser(invariantFile);
		
		//get all reduced control flow graph
		List<CFGGraph<CodeNodeIdentifier, Expression>> reducedCfgList = PathAnalyzer.obtainAllReducedCfgGraphs(cfgPathAbMapping);
		
		System.out.println("reduced cfg list size " + reducedCfgList.size());
		//define a template creator
		TemplateCreator tmpCreator = new TemplateCreator(sP,invP, projectName, reducedCfgList);
		tmpCreator.generateCode();
		
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		
		if(args.length != 1) {
			System.out.println("[Correct Usage: ] java -jar .jar projectName");
		}
		
		config(args[0]);
		generateCode();
		
	}

}
