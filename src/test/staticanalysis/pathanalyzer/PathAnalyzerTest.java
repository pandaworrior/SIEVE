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
package test.staticanalysis.pathanalyzer;

import japa.parser.ast.expr.Expression;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import staticanalysis.datastructures.controlflowgraph.CFGGraph;
import staticanalysis.datastructures.path.PathAbstraction;
import staticanalysis.datastructures.path.ReducedPathAbstractionSet;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.codeparser.JavaFileParser;
import staticanalysis.pathanalyzer.PathAbstractionCreator;
import staticanalysis.pathanalyzer.PathAnalyzer;

// TODO: Auto-generated Javadoc
/**
 * The Class PathAnalyzerTest.
 */
public class PathAnalyzerTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		JavaFileParser jfParser = new JavaFileParser(
				"/var/tmp/workspace1/georeplication/src/test/staticanalysis/codeparser/ExampleFile.java");
		jfParser.parseJavaFile();

		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet> cfgPathAbMapping = 
				PathAnalyzer.obtainAllReducePathAbstractions(PathAbstractionCreator
						.obtainAllPathAbstraction(jfParser
								.getClassMethodCFGMappings()));
		
		Iterator<Entry<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet>> it = cfgPathAbMapping.entrySet().iterator();
		while(it.hasNext()){
			Entry<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet> itEntry = it.next();
			CFGGraph<CodeNodeIdentifier, Expression> cfg = itEntry.getKey();
			ReducedPathAbstractionSet rPathAbSet = itEntry.getValue();
			List<PathAbstraction> reducedPathAbList = rPathAbSet.getReducedPathAbstractionSet();
			for(PathAbstraction reducedPathAb : reducedPathAbList){
				reducedPathAb.printOutInPlainText();
				PathAnalyzer.obtainReducedControlFlowGraph(cfg, reducedPathAb);
			}
		}

	}

}
