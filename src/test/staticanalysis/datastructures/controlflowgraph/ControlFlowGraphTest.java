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
package test.staticanalysis.datastructures.controlflowgraph;

import staticanalysis.datastructures.controlflowgraph.CFGGraph;
import staticanalysis.datastructures.controlflowgraph.CFGNode;


// TODO: Auto-generated Javadoc
/**
 * The Class ControlFlowGraphTest.
 */
public class ControlFlowGraphTest {

	// simple test for testing the control flow graph functionalities
	/**
	 * Test1.
	 */
	public static void test1() {
		CFGNode<String, Integer> node1 = new CFGNode<String, Integer>("A", 0);
		CFGNode<String, Integer> node2 = new CFGNode<String, Integer>("B", 1);
		CFGNode<String, Integer> node3 = new CFGNode<String, Integer>("C", 2);
		CFGNode<String, Integer> node4 = new CFGNode<String, Integer>("D", 3);
		CFGNode<String, Integer> node5 = new CFGNode<String, Integer>("E", 4);
		CFGNode<String, Integer> node6 = new CFGNode<String, Integer>("G", 5);
		CFGNode<String, Integer> node7 = new CFGNode<String, Integer>("F", 6);
		CFGNode<String, Integer> node8 = new CFGNode<String, Integer>("H", 7);

		CFGGraph<String, Integer> cfg = new CFGGraph<String, Integer>();
		cfg.addEdge(node1, node2);
		cfg.addEdge(node1, node3);
		cfg.addEdge(node2, node7);
		cfg.addEdge(node3, node4);
		cfg.addEdge(node3, node5);
		cfg.addEdge(node4, node7);
		cfg.addEdge(node5, node6);
		cfg.addEdge(node7, node8);
		cfg.addEdge(node6, node5);
		cfg.addEdge(node6, node8);
		cfg.addEntryNode(node1);
		cfg.addReturnNode(node8);

		cfg.printOut();
		// cfg.computeRegularExpression();
		// System.out.println(cfg.getRegularExpression());

		// RegularExpressionParser regexParser = new
		// RegularExpressionParser(cfg.getRegularExpression());
		// regexParser.breakIntoPieces();

		// regexParser.reduceRegularExpression();
	}

	// complex example that tests the regular expression functionalities
	/**
	 * Test2.
	 */
	public static void test2() {
		CFGNode<String, Integer> node1 = new CFGNode<String, Integer>("A", 0);
		CFGNode<String, Integer> node2 = new CFGNode<String, Integer>("B", 1);
		CFGNode<String, Integer> node3 = new CFGNode<String, Integer>("C", 2);
		CFGNode<String, Integer> node4 = new CFGNode<String, Integer>("D", 3);
		CFGNode<String, Integer> node5 = new CFGNode<String, Integer>("E", 4);
		CFGNode<String, Integer> node6 = new CFGNode<String, Integer>("F", 5);
		CFGNode<String, Integer> node7 = new CFGNode<String, Integer>("G", 6);
		CFGNode<String, Integer> node8 = new CFGNode<String, Integer>("H", 7);
		CFGNode<String, Integer> node9 = new CFGNode<String, Integer>("I", 8);
		CFGNode<String, Integer> node10 = new CFGNode<String, Integer>("G", 9);
		CFGNode<String, Integer> node11 = new CFGNode<String, Integer>("K", 10);
		CFGNode<String, Integer> node12 = new CFGNode<String, Integer>("L", 11);

		CFGGraph<String, Integer> cfg = new CFGGraph<String, Integer>();
		cfg.addEdge(node1, node2);
		cfg.addEdge(node1, node3);
		cfg.addEdge(node2, node4);
		cfg.addEdge(node2, node5);
		cfg.addEdge(node3, node6);
		cfg.addEdge(node4, node5);
		cfg.addEdge(node5, node8);
		cfg.addEdge(node6, node3);
		cfg.addEdge(node6, node7);
		cfg.addEdge(node6, node8);
		cfg.addEdge(node7, node10);
		cfg.addEdge(node7, node11);
		cfg.addEdge(node8, node4);
		cfg.addEdge(node8, node9);
		cfg.addEdge(node9, node10);
		cfg.addEdge(node9, node12);
		cfg.addEdge(node10, node9);
		cfg.addEdge(node10, node12);
		cfg.addEdge(node11, node10);
		cfg.addEdge(node11, node12);
		cfg.addEntryNode(node1);
		cfg.addReturnNode(node12);

		cfg.printOut();

		// cfg.computeRegularExpression();
		// System.out.println(cfg.getRegularExpression());
		// RegularExpressionParser regexParser = new
		// RegularExpressionParser(cfg.getRegularExpression());
		// String randStr = "(1+(2+(3+(5+6))))";
		// RegularExpressionParser regexParser = new
		// RegularExpressionParser(randStr);
		// regexParser.breakIntoPieces();

		// regexParser.reduceRegularExpression();
	}

	// test control flow graph merging
	/**
	 * Test3.
	 */
	public static void test3() {
		CFGNode<String, Integer> node1 = new CFGNode<String, Integer>("A", 0);
		CFGNode<String, Integer> node2 = new CFGNode<String, Integer>("B", 1);
		CFGNode<String, Integer> node3 = new CFGNode<String, Integer>("C", 2);
		CFGNode<String, Integer> node4 = new CFGNode<String, Integer>("D", 3);
		CFGNode<String, Integer> node5 = new CFGNode<String, Integer>("E", 4);
		CFGNode<String, Integer> node6 = new CFGNode<String, Integer>("F", 5);
		CFGNode<String, Integer> node7 = new CFGNode<String, Integer>("G", 6);
		CFGNode<String, Integer> node8 = new CFGNode<String, Integer>("H", 7);
		CFGNode<String, Integer> node9 = new CFGNode<String, Integer>("I", 8);

		CFGGraph<String, Integer> cfg1 = new CFGGraph<String, Integer>();
		CFGGraph<String, Integer> cfg2 = new CFGGraph<String, Integer>();
		cfg1.addEdge(node1, node2);
		cfg1.addEdge(node1, node3);
		cfg1.addEdge(node3, node4);
		cfg1.addEdge(node3, node5);
		cfg1.addEdge(node3, node6);
		cfg1.addEntryNode(node1);
		cfg1.addExitNode(node2);
		cfg1.addExitNode(node4);
		cfg1.addExitNode(node5);
		cfg1.addExitNode(node6);

		cfg2.addEdge(node7, node8);
		cfg2.addEdge(node7, node9);
		// cfg2.addEdge(node8, node9);
		cfg2.addEntryNode(node7);
		cfg2.addReturnNode(node8);
		cfg2.addReturnNode(node9);

		cfg1.printOut();
		cfg2.printOut();

		cfg1.mergeWithOtherControlFlowGraph(cfg2);
		cfg1.printOut();
		// cfg1.computeRegularExpression();
		// System.out.println(cfg1.getRegularExpression());
		// RegularExpressionParser regexParser = new
		// RegularExpressionParser(cfg1.getRegularExpression());
		// regexParser.breakIntoPieces();

		// regexParser.reduceRegularExpression();

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		// test1();
		// test2();
		test3();
	}

}
