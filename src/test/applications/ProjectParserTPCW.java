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
package test.applications;

import japa.parser.ast.expr.Expression;

import java.util.HashMap;
import java.util.List;

import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.codeparser.ProjectParser;
import staticanalysis.datastructures.controlflowgraph.CFGGraph;
import staticanalysis.datastructures.path.PathAbstraction;
import staticanalysis.datastructures.path.ReducedPathAbstractionSet;
import staticanalysis.pathanalyzer.PathAbstractionCreator;
import staticanalysis.pathanalyzer.PathAnalyzer;
import staticanalysis.templatecreator.TemplateCreator;
import util.annotationparser.Invariant;
import util.annotationparser.InvariantParser;
import util.annotationparser.SchemaParser;

// TODO: Auto-generated Javadoc
/**
 * The Class ProjectParserTest.
 */
public class ProjectParserTPCW {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		// creates an project parser for the tree
		String srcDirPath = "/var/tmp/workspace/georeplication/applications/tpc-w-fenix";
		ProjectParser pjsParser = new ProjectParser(srcDirPath,
				"TPCWStaticAnalysis");
		//pjsParser.addFileToFileFilter("TPCW_Populate.java");
		//pjsParser.addFileToFileFilter("rbe");
		//pjsParser.addFileToFileFilter("servlets");
		pjsParser.addFileToFileFilter("TPCW_Database.java"); //must exclude since duplicates
		pjsParser.addFileToFileFilter("Pad.java");
		pjsParser.buildFileTree();
		//pjsParser.printOutFileTree();
		pjsParser.obtailAllControlFlowGraphs();
		//pjsParser.printOutAllControlFlowGraphs();
		pjsParser.obtainBindingsForAllMethods();
		pjsParser.resolveAllBindings();
		
		System.out.println("Finished the cfg generation");
		
		PathAnalyzer.addFunctionMustBeProcessedListFromFile(srcDirPath + "/transactionMustBeAnalyzed.txt");
		
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, PathAbstraction> pathAbMap = PathAbstractionCreator
		.obtainAllPathAbstractionForWholeProject(pjsParser.getMethodCfgMapping());
		
		//PathAbstractionCreator.printOutPathAbstractions(pathAbMap);
		
		//System.exit(-1);
		
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet> cfgPathAbMapping = 
			PathAnalyzer.obtainAllReducePathAbstractions(pathAbMap);
		
		//define a annotation parser
		String fileName = srcDirPath + "/sqlSchemaTPCW.sql";
		SchemaParser sP = new SchemaParser(fileName);
		sP.parseAnnotations();
		Invariant.setSchemaParser(sP);
		String invariantFileName = srcDirPath + "/tpcwInvariant.txt";
		InvariantParser invP = new InvariantParser(invariantFileName);
		
		//get all reduced control flow graph
		List<CFGGraph<CodeNodeIdentifier, Expression>> reducedCfgList = PathAnalyzer.obtainAllReducedCfgGraphs(cfgPathAbMapping);
		
		System.out.println("reduced cfg list size " + reducedCfgList.size());
		//define a template creator
		TemplateCreator tmpCreator = new TemplateCreator(sP,invP, "tpcw", reducedCfgList);
		tmpCreator.generateCode();
		
		tmpCreator.printOutStatis();
		
		tmpCreator.generateAndExecuteJahobCommand();
	}

}
