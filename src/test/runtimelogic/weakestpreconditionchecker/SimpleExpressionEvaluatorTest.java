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
package test.runtimelogic.weakestpreconditionchecker;

import runtimelogic.weakestpreconditionchecker.SimpleExpressionEvaluator;
import runtimelogic.weakestpreconditionchecker.SimpleInequalityExpressionEvaluator;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleExpressionEvaluatorTest.
 */
public class SimpleExpressionEvaluatorTest {
	
	public static double computeLatency(long startTime) {
		long endTime = System.nanoTime();
		double latency = (endTime - startTime) * 0.000001;
		return latency;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		
		/*for(int i = 0; i < 100; i++) {
			String formulaStr = "true && true && true && 1>2";
			long startTime = System.nanoTime();
			boolean trueOrFalse = SimpleExpressionEvaluator.evalBoolExpression(formulaStr);
			System.out.println("Latency to evaluate wp is " + computeLatency(startTime) + " ms");
			System.out.println("True or false: " + trueOrFalse);
		}*/
		
		for(int i = 0; i < 100; i++) {
			boolean value = false;
			String str1 = "10 >= 9";
			long startTime = System.nanoTime();
			value = SimpleInequalityExpressionEvaluator.evalBoolExpression(str1);
			System.out.println("Latency to evaluate wp is " + computeLatency(startTime) + " ms");
			System.out.println("True or false: " + value);
			String str2 = "10 >= 11";
			startTime = System.nanoTime();
			value = SimpleInequalityExpressionEvaluator.evalBoolExpression(str2);
			System.out.println("Latency to evaluate wp is " + computeLatency(startTime) + " ms");
			System.out.println("True or false: " + value);
			String str3 = "10 > 9";
			startTime = System.nanoTime();
			value = SimpleInequalityExpressionEvaluator.evalBoolExpression(str3);
			System.out.println("Latency to evaluate wp is " + computeLatency(startTime) + " ms");
			System.out.println("True or false: " + value);
			String str4 = "10 > 11";
			startTime = System.nanoTime();
			value = SimpleInequalityExpressionEvaluator.evalBoolExpression(str4);
			System.out.println("Latency to evaluate wp is " + computeLatency(startTime) + " ms");
			System.out.println("True or false: " + value);
			String str5 = "9<= 10";
			startTime = System.nanoTime();
			value = SimpleInequalityExpressionEvaluator.evalBoolExpression(str5);
			System.out.println("Latency to evaluate wp is " + computeLatency(startTime) + " ms");
			System.out.println("True or false: " + value);
			String str6 = "11<=10";
			startTime = System.nanoTime();
			value = SimpleInequalityExpressionEvaluator.evalBoolExpression(str6);
			System.out.println("Latency to evaluate wp is " + computeLatency(startTime) + " ms");
			System.out.println("True or false: " + value);
			String str7 = "9<10";
			startTime = System.nanoTime();
			value = SimpleInequalityExpressionEvaluator.evalBoolExpression(str7);
			System.out.println("Latency to evaluate wp is " + computeLatency(startTime) + " ms");
			System.out.println("True or false: " + value);
			String str8 = "11 < 10";
			startTime = System.nanoTime();
			value = SimpleInequalityExpressionEvaluator.evalBoolExpression(str8);
			System.out.println("Latency to evaluate wp is " + computeLatency(startTime) + " ms");
			System.out.println("True or false: " + value);
			String str9 = "1 <>3";
			startTime = System.nanoTime();
			value = SimpleInequalityExpressionEvaluator.evalBoolExpression(str9);
			System.out.println("Latency to evaluate wp is " + computeLatency(startTime) + " ms");
			System.out.println("True or false: " + value);
			String str10 = "1 <> 1";
			startTime = System.nanoTime();
			value = SimpleInequalityExpressionEvaluator.evalBoolExpression(str10);
			System.out.println("Latency to evaluate wp is " + computeLatency(startTime) + " ms");
			System.out.println("True or false: " + value);
		}
	}
}
