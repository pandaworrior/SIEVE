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
import staticanalysis.pathanalyzer.PathAbstractionCreator;
import staticanalysis.pathanalyzer.PathAnalyzer;
import staticanalysis.templatecreator.TemplateCreator;
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
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		JavaFileParser jfParser = new JavaFileParser(
				"/var/tmp/workspace/georeplication/src/test/staticanalysis/templatecreator/DatabaseQueryExample.java");
		jfParser.parseJavaFile();

		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet> cfgPathAbMapping = 
				PathAnalyzer.obtainAllReducePathAbstractions(PathAbstractionCreator
						.obtainAllPathAbstraction(jfParser
								.getClassMethodCFGMappings()));
		
		//get all reduced control flow graph
		
		/*List<CFGGraph<CodeNodeIdentifier, Expression>> reducedCfgList = PathAnalyzer.obtainAllReducedCfgGraphs(cfgPathAbMapping);
		
		//define a annotation parser
		String fileName = "/var/tmp/workspace/georeplication/src/test/util/annotationparser/sqlSchemaTest.sql";
		SchemaParser sP = new SchemaParser(fileName);
		sP.parseAnnotations();
		
		//define a template creator
		TemplateCreator tmpCreator = new TemplateCreator(sP, "test", reducedCfgList);
		tmpCreator.generateCode();*/
		
	}

}
