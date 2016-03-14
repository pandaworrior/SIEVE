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

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.codeparser.JavaFileParser;
import staticanalysis.codeparser.ProjectParser;
import staticanalysis.pathanalyzer.PathAbstractionCreator;
import staticanalysis.pathanalyzer.PathAnalyzer;
import staticanalysis.templatecreator.SourceCodeGenerator;
import staticanalysis.templatecreator.TemplateCreator;
import util.annotationparser.Invariant;
import util.annotationparser.InvariantParser;
import util.annotationparser.SchemaParser;
import staticanalysis.datastructures.controlflowgraph.CFGGraph;
import staticanalysis.datastructures.path.PathAbstraction;
import staticanalysis.datastructures.path.ReducedPathAbstractionSet;
import util.commonfunc.ExpriementOperations;
import util.commonfunc.FileOperations;
import util.commonfunc.Statistics;
import util.commonfunc.StringOperations;
import util.debug.Debug;
/**
 * The Class TemplateCreatorTest.
 */
public class TPCWTemplateCreator {
	
	public static String[] ATTRIBUTE_NAMES= {"Cfg", "PathAb", "ReducedPathAb", "Templates", "WP", "Total"};
	
	public static String[] STATS_STR = {"Mean", "Median", "Sdev"}; 
	
	public static String outputFile = "ResultOfStaticAnalysisOfTPCW";
	
	public static String outputDirPath = System.getProperty("user.dir") +"/" + SourceCodeGenerator.codeName;
	
	public static int numOfRounds = 0;
	
	public static String srcDirPath = "";
	
	public static void setSrcDirPath(String srcPath) {
		srcDirPath = srcPath;
	}
	
	public static void setNumOfRounds(int nR) {
		numOfRounds = nR;
	}
	
	public static void setOutputCodeDirPath(String projectName) {
		outputDirPath += projectName;
	}
	
	public static void removeAllWPFiles() {
		if(FileOperations.isExisted(outputDirPath)) {
			File dir = new File(outputDirPath);
			File[] files = dir.listFiles();
			for(File f : files) {
				if(f.getName().contains(".vc")) {
					if(!f.delete()) {
						System.out.println("file hasn't been deleted " + f.getName());
						System.exit(-1);
					}
				}
			}
		}else {
			System.out.println("File not existed " + outputDirPath);
		}
	}
	
	public static void removeFiles(String fileName) {
		String fileFullPath = outputDirPath + "/" + fileName;
		if(FileOperations.isExisted(fileFullPath)) {
			File f = new File(fileFullPath);
			if(!f.delete()) {
				System.out.println("file hasn't been deleted " + f.getName());
				System.exit(-1);
			}
		}
	}
	
	public static void cleanUpFiles() {
		removeAllWPFiles();
		removeFiles(TemplateCreator.CLASS_NAME_STR+".java");
		removeFiles("run"+TemplateCreator.CLASS_NAME_STR +".sh");
	}
	
	public static double computeLatency(long startTime) {
		long endTime = System.nanoTime();
		double latency = (endTime - startTime) * 0.000001;
		return latency;
	}
	
	public static String getOutputFileName() {
		return outputFile + numOfRounds + "_times.txt";
	}
	
	public static String getNewOutputFileName(String sizeStr) {
		return outputFile + sizeStr+ "_" +numOfRounds + "_times.txt";
	}
	
	public static void writeResultsToFile(String fileName, String[] resultStrs) {
		FileOperations.createFileByGivenFilePath(fileName);
		FileOperations.writeToFile(fileName, new ArrayList<String>(Arrays.asList(resultStrs)));
	}
	
	public static String[] generateOutputStrings(double[][] outputs) {
		String[] returnStrs = new String[3];
		int numOfRows = outputs.length;
		String topHeader = ATTRIBUTE_NAMES[0];
		for(int i = 1; i < numOfRows; i++) {
			topHeader += " & " + ATTRIBUTE_NAMES[i];
		}
		returnStrs[0] = topHeader + "\n";
		
		String secondHeader = STATS_STR[0];
		for(int i = 1; i < STATS_STR.length; i++) {
			secondHeader += " & " + STATS_STR[i]; 
		}
		
		//generate the results str
		String contentStr = "";
		returnStrs[1] = "";
		for(int i = 0; i < numOfRows; i++) {
			returnStrs[1] += secondHeader + " & "; 
			Statistics stat = new Statistics(outputs[i]);
			contentStr += stat.getMean() + " & " + stat.median()  + " & " + stat.getStdDev() + " & ";
		}
		returnStrs[1] = StringOperations.removeLastAndOperator(returnStrs[1]) + "\n";
		returnStrs[2] = StringOperations.removeLastAndOperator(contentStr);
		return returnStrs;
	}
	
	public static double[] originalTPCWExperiment() {
		double[] output = new double[6];
		cleanUpFiles();
		
		ProjectParser pjsParser = new ProjectParser(srcDirPath,
				"TPCWStaticAnalysis");
		pjsParser.addFileToFileFilter("TPCW_Database.java"); //must exclude since duplicates
		//pjsParser.addFileToFileFilter("TPCW_Populate.java");
		//pjsParser.addFileToFileFilter("TPCW_Database.std.java");
		//pjsParser.addFileToFileFilter("rbe");
		//pjsParser.addFileToFileFilter("args");
		//pjsParser.addFileToFileFilter("util");
		//pjsParser.addFileToFileFilter("servlets");
		pjsParser.addFileToFileFilter("Pad.java");
		
		long startTimeOfBuildingCfg = System.nanoTime();
		pjsParser.buildFileTree();
		//pjsParser.printOutFileTree();
		
		pjsParser.obtailAllControlFlowGraphs();
		//pjsParser.printOutAllControlFlowGraphs();
		pjsParser.obtainBindingsForAllMethods();
		pjsParser.resolveAllBindings();
		//pjsParser.printOutAllControlFlowGraphs();
		double latencyOfBuildingCfg = computeLatency(startTimeOfBuildingCfg);
		
		PathAnalyzer.addFunctionMustBeProcessedListFromFile(srcDirPath + "/transactionMustBeAnalyzed.txt");
		
		long startTimeOfCreatingPathAb = System.nanoTime();
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, PathAbstraction> pathAbMap = PathAbstractionCreator
		.obtainAllPathAbstractionForWholeProject(pjsParser.getMethodCfgMapping());
		double latencyOfCreatingPathAb = computeLatency(startTimeOfCreatingPathAb);
		
		long startTimeOfReducingPathAb = System.nanoTime();
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet> cfgPathAbMapping = 
				PathAnalyzer.obtainAllReducePathAbstractions(pathAbMap);
		double latencyOfReducingPathAb = computeLatency(startTimeOfReducingPathAb);
		
		//define a annotation parser
		String fileName = srcDirPath + "/sqlSchemaTPCW.sql";
		SchemaParser sP = new SchemaParser(fileName);
		sP.parseAnnotations();
		Invariant.setSchemaParser(sP);
		String invariantFileName = srcDirPath + "/tpcwInvariant.txt";
		InvariantParser invP = new InvariantParser(invariantFileName);
		
		long startTimeOfCreatingTemplates = System.nanoTime();
		//get all reduced control flow graph
		List<CFGGraph<CodeNodeIdentifier, Expression>> reducedCfgList = PathAnalyzer.obtainAllReducedCfgGraphs(cfgPathAbMapping);
		
		System.out.println("reduced cfg list size " + reducedCfgList.size());
		//define a template creator
		TemplateCreator tmpCreator = new TemplateCreator(sP,invP, "tpcw", reducedCfgList);
		tmpCreator.generateCode();
		double latencyOfCreatingTemplates = computeLatency(startTimeOfCreatingTemplates);
		
		long startTimeOfGeneratingWP = System.nanoTime();
		tmpCreator.generateAndExecuteJahobCommand();
		double latencyOfGeneratingWP = computeLatency(startTimeOfGeneratingWP);
		
		tmpCreator.printOutStatis();
		
		double totalLatency = latencyOfBuildingCfg + latencyOfCreatingPathAb + latencyOfReducingPathAb + latencyOfCreatingTemplates + latencyOfGeneratingWP;
		
		output[0] = latencyOfBuildingCfg;
		output[1] = latencyOfCreatingPathAb;
		output[2] = latencyOfReducingPathAb;
		output[3] = latencyOfCreatingTemplates;
		output[4] = latencyOfGeneratingWP;
		output[5] = totalLatency;
		
		System.out.println("status: " + latencyOfBuildingCfg + " " + latencyOfCreatingPathAb + " " +
				latencyOfReducingPathAb + " " + latencyOfCreatingTemplates + " " + 
				latencyOfGeneratingWP + " " +
				totalLatency);
		
		return output;
	}
	
	public static double[] doubleSizeTPCWExperiment() {
		double[] output = new double[6];
		cleanUpFiles();
		
		ProjectParser pjsParser = new ProjectParser(srcDirPath,
				"TPCWStaticAnalysis");
		pjsParser.addFileToFileFilter("TPCW_Database.java"); //must exclude since duplicates
		pjsParser.addFileToFileFilter("TPCW_Database1.java"); //must exclude since duplicates
		//pjsParser.addFileToFileFilter("TPCW_Populate.java");
		//pjsParser.addFileToFileFilter("TPCW_Database.std.java");
		//pjsParser.addFileToFileFilter("TPCW_Database.std.size2.java");
		//pjsParser.addFileToFileFilter("TPCW_Database.std.size3.java");
		//pjsParser.addFileToFileFilter("rbe");
		//pjsParser.addFileToFileFilter("args");
		//pjsParser.addFileToFileFilter("util");
		//pjsParser.addFileToFileFilter("servlets");
		pjsParser.addFileToFileFilter("Pad.java");
		pjsParser.addFileToFileFilter("Pad1.java");
		
		long startTimeOfBuildingCfg = System.nanoTime();
		pjsParser.buildFileTree();
		//pjsParser.printOutFileTree();
		
		pjsParser.obtailAllControlFlowGraphs();
		//pjsParser.printOutAllControlFlowGraphs();
		pjsParser.obtainBindingsForAllMethods();
		pjsParser.resolveAllBindings();
		//pjsParser.printOutAllControlFlowGraphs();
		double latencyOfBuildingCfg = computeLatency(startTimeOfBuildingCfg);
		
		PathAnalyzer.addFunctionMustBeProcessedListFromFile(srcDirPath + "/transactionMustBeAnalyzed.txt");
		
		long startTimeOfCreatingPathAb = System.nanoTime();
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, PathAbstraction> pathAbMap = PathAbstractionCreator
		.obtainAllPathAbstractionForWholeProject(pjsParser.getMethodCfgMapping());
		double latencyOfCreatingPathAb = computeLatency(startTimeOfCreatingPathAb);
		
		long startTimeOfReducingPathAb = System.nanoTime();
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet> cfgPathAbMapping = 
				PathAnalyzer.obtainAllReducePathAbstractions(pathAbMap);
		double latencyOfReducingPathAb = computeLatency(startTimeOfReducingPathAb);
		
		//define a annotation parser
		String fileName = srcDirPath + "/sqlSchemaTPCW.sql";
		SchemaParser sP = new SchemaParser(fileName);
		sP.parseAnnotations();
		Invariant.setSchemaParser(sP);
		String invariantFileName = srcDirPath + "/tpcwInvariant.txt";
		InvariantParser invP = new InvariantParser(invariantFileName);
		
		long startTimeOfCreatingTemplates = System.nanoTime();
		//get all reduced control flow graph
		List<CFGGraph<CodeNodeIdentifier, Expression>> reducedCfgList = PathAnalyzer.obtainAllReducedCfgGraphs(cfgPathAbMapping);
		
		System.out.println("reduced cfg list size " + reducedCfgList.size());
		//define a template creator
		TemplateCreator tmpCreator = new TemplateCreator(sP,invP, "tpcw", reducedCfgList);
		tmpCreator.generateCode();
		double latencyOfCreatingTemplates = computeLatency(startTimeOfCreatingTemplates);
		
		long startTimeOfGeneratingWP = System.nanoTime();
		tmpCreator.generateAndExecuteJahobCommand();
		double latencyOfGeneratingWP = computeLatency(startTimeOfGeneratingWP);
		
		tmpCreator.printOutStatis();
		
		double totalLatency = latencyOfBuildingCfg + latencyOfCreatingPathAb + latencyOfReducingPathAb + latencyOfCreatingTemplates + latencyOfGeneratingWP;
		
		output[0] = latencyOfBuildingCfg;
		output[1] = latencyOfCreatingPathAb;
		output[2] = latencyOfReducingPathAb;
		output[3] = latencyOfCreatingTemplates;
		output[4] = latencyOfGeneratingWP;
		output[5] = totalLatency;
		
		System.out.println("status: " + latencyOfBuildingCfg + " " + latencyOfCreatingPathAb + " " +
				latencyOfReducingPathAb + " " + latencyOfCreatingTemplates + " " + 
				latencyOfGeneratingWP + " " +
				totalLatency);
		
		return output;
	}
	
	public static double[] tripleSizeTPCWExperiment() {
		double[] output = new double[6];
		cleanUpFiles();
		
		ProjectParser pjsParser = new ProjectParser(srcDirPath,
				"TPCWStaticAnalysis");
		pjsParser.addFileToFileFilter("TPCW_Database.java"); //must exclude since duplicates
		pjsParser.addFileToFileFilter("TPCW_Database1.java"); //must exclude since duplicates
		pjsParser.addFileToFileFilter("TPCW_Database2.java"); //must exclude since duplicates
		//pjsParser.addFileToFileFilter("TPCW_Populate.java");
		//pjsParser.addFileToFileFilter("TPCW_Database.std.java");
		//pjsParser.addFileToFileFilter("TPCW_Database.std.size2.java");
		//pjsParser.addFileToFileFilter("TPCW_Database.std.size3.java");
		//pjsParser.addFileToFileFilter("rbe");
		//pjsParser.addFileToFileFilter("args");
		//pjsParser.addFileToFileFilter("util");
		//pjsParser.addFileToFileFilter("servlets");
		pjsParser.addFileToFileFilter("Pad.java");
		pjsParser.addFileToFileFilter("Pad1.java");
		pjsParser.addFileToFileFilter("Pad2.java");
		
		long startTimeOfBuildingCfg = System.nanoTime();
		pjsParser.buildFileTree();
		//pjsParser.printOutFileTree();
		
		pjsParser.obtailAllControlFlowGraphs();
		//pjsParser.printOutAllControlFlowGraphs();
		pjsParser.obtainBindingsForAllMethods();
		pjsParser.resolveAllBindings();
		//pjsParser.printOutAllControlFlowGraphs();
		double latencyOfBuildingCfg = computeLatency(startTimeOfBuildingCfg);
		
		PathAnalyzer.addFunctionMustBeProcessedListFromFile(srcDirPath + "/transactionMustBeAnalyzed.txt");
		
		long startTimeOfCreatingPathAb = System.nanoTime();
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, PathAbstraction> pathAbMap = PathAbstractionCreator
		.obtainAllPathAbstractionForWholeProject(pjsParser.getMethodCfgMapping());
		double latencyOfCreatingPathAb = computeLatency(startTimeOfCreatingPathAb);
		
		long startTimeOfReducingPathAb = System.nanoTime();
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, ReducedPathAbstractionSet> cfgPathAbMapping = 
				PathAnalyzer.obtainAllReducePathAbstractions(pathAbMap);
		double latencyOfReducingPathAb = computeLatency(startTimeOfReducingPathAb);
		
		//define a annotation parser
		String fileName = srcDirPath + "/sqlSchemaTPCW.sql";
		SchemaParser sP = new SchemaParser(fileName);
		sP.parseAnnotations();
		Invariant.setSchemaParser(sP);
		String invariantFileName = srcDirPath + "/tpcwInvariant.txt";
		InvariantParser invP = new InvariantParser(invariantFileName);
		
		long startTimeOfCreatingTemplates = System.nanoTime();
		//get all reduced control flow graph
		List<CFGGraph<CodeNodeIdentifier, Expression>> reducedCfgList = PathAnalyzer.obtainAllReducedCfgGraphs(cfgPathAbMapping);
		
		System.out.println("reduced cfg list size " + reducedCfgList.size());
		//define a template creator
		TemplateCreator tmpCreator = new TemplateCreator(sP,invP, "tpcw", reducedCfgList);
		tmpCreator.generateCode();
		double latencyOfCreatingTemplates = computeLatency(startTimeOfCreatingTemplates);
		
		long startTimeOfGeneratingWP = System.nanoTime();
		tmpCreator.generateAndExecuteJahobCommand();
		double latencyOfGeneratingWP = computeLatency(startTimeOfGeneratingWP);
		
		tmpCreator.printOutStatis();
		
		double totalLatency = latencyOfBuildingCfg + latencyOfCreatingPathAb + latencyOfReducingPathAb + latencyOfCreatingTemplates + latencyOfGeneratingWP;
		
		output[0] = latencyOfBuildingCfg;
		output[1] = latencyOfCreatingPathAb;
		output[2] = latencyOfReducingPathAb;
		output[3] = latencyOfCreatingTemplates;
		output[4] = latencyOfGeneratingWP;
		output[5] = totalLatency;
		
		System.out.println("status: " + latencyOfBuildingCfg + " " + latencyOfCreatingPathAb + " " +
				latencyOfReducingPathAb + " " + latencyOfCreatingTemplates + " " + 
				latencyOfGeneratingWP + " " +
				totalLatency);
		
		return output;
	}
	
	public static double[][] removeMaxMin(double[][] latencyMatrix, int indexOfMinLatency, int indexOfMaxLatency){
		double[][] finalLatencyMatrix = null;
		if(indexOfMinLatency != indexOfMaxLatency) {
			finalLatencyMatrix = new double[6][latencyMatrix[0].length - 2];
			for(int i = 0, j = 0; i < latencyMatrix[0].length - 2 && j < latencyMatrix[0].length; i++, j++) {
				if(j == indexOfMinLatency || j == indexOfMaxLatency) {
					j = j + 1;
				}
				finalLatencyMatrix[0][i] = latencyMatrix[0][j];
				finalLatencyMatrix[1][i] = latencyMatrix[1][j];
				finalLatencyMatrix[2][i] = latencyMatrix[2][j];
				finalLatencyMatrix[3][i] = latencyMatrix[3][j];
				finalLatencyMatrix[4][i] = latencyMatrix[4][j];
				finalLatencyMatrix[5][i] = latencyMatrix[5][j];
			}
		}else {
			finalLatencyMatrix = new double[6][latencyMatrix[0].length - 1];
			for(int i = 0, j = 0; i < latencyMatrix[0].length - 1 && j < latencyMatrix[0].length; i++, j++) {
				if(j == indexOfMinLatency) {
					j = j + 1;
				}
				finalLatencyMatrix[0][i] = latencyMatrix[0][j];
				finalLatencyMatrix[1][i] = latencyMatrix[1][j];
				finalLatencyMatrix[2][i] = latencyMatrix[2][j];
				finalLatencyMatrix[3][i] = latencyMatrix[3][j];
				finalLatencyMatrix[4][i] = latencyMatrix[4][j];
				finalLatencyMatrix[5][i] = latencyMatrix[5][j];
			}
		}
		return finalLatencyMatrix;
	}
	
	public static void runOriginalManyTimes(int rounds, String dir) {
		if(rounds <= 0) {
			System.out.println("You specified a wrong round number");
			System.exit(-1);
		}
		
		setNumOfRounds(rounds);
		setSrcDirPath(dir);
		
		rounds = rounds + 2;
		double [][] latencyOfStatistic = new double [6][rounds];
		double minLatency = 0.0;
		int indexOfMinLatency = 0;
		double maxLatency = 0.0;
		int indexOfMaxLatency = 0;
		for(int i = 0 ; i < rounds; i++) {
			double[] output = originalTPCWExperiment();
			latencyOfStatistic[0][i] = output[0];
			latencyOfStatistic[1][i] = output[1];
			latencyOfStatistic[2][i] = output[2];
			latencyOfStatistic[3][i] = output[3];
			latencyOfStatistic[4][i] = output[4];
			latencyOfStatistic[5][i] = output[5];
			if(i == 0) {
				minLatency = latencyOfStatistic[5][i];
				maxLatency = latencyOfStatistic[5][i];
			}else {
				if(latencyOfStatistic[5][i] < minLatency) {
					minLatency = latencyOfStatistic[5][i];
					indexOfMinLatency = i;
				}
				if(latencyOfStatistic[5][i] > maxLatency) {
					maxLatency = latencyOfStatistic[5][i];
					indexOfMaxLatency = i;
				}
			}
			ExpriementOperations.pauseExecution(300000);
		}
		double[][] cleanLatencyOfStatistic = removeMaxMin(latencyOfStatistic, indexOfMinLatency, indexOfMaxLatency);
		String[] resultStrs = generateOutputStrings(cleanLatencyOfStatistic);
		writeResultsToFile(getOutputFileName(),resultStrs);
	}
	
	public static void runDoubleSizeManyTimes(int rounds, String dir) {
		if(rounds <= 0) {
			System.out.println("You specified a wrong round number");
			System.exit(-1);
		}
		
		setNumOfRounds(rounds);
		setSrcDirPath(dir+"-double-size");
		
		rounds = rounds + 2;
		double [][] latencyOfStatistic = new double [6][rounds];
		double minLatency = 0.0;
		int indexOfMinLatency = 0;
		double maxLatency = 0.0;
		int indexOfMaxLatency = 0;
		for(int i = 0 ; i < rounds; i++) {
			double[] output = doubleSizeTPCWExperiment();
			latencyOfStatistic[0][i] = output[0];
			latencyOfStatistic[1][i] = output[1];
			latencyOfStatistic[2][i] = output[2];
			latencyOfStatistic[3][i] = output[3];
			latencyOfStatistic[4][i] = output[4];
			latencyOfStatistic[5][i] = output[5];
			if(i == 0) {
				minLatency = latencyOfStatistic[5][i];
				maxLatency = latencyOfStatistic[5][i];
			}else {
				if(latencyOfStatistic[5][i] < minLatency) {
					minLatency = latencyOfStatistic[5][i];
					indexOfMinLatency = i;
				}
				if(latencyOfStatistic[5][i] > maxLatency) {
					maxLatency = latencyOfStatistic[5][i];
					indexOfMaxLatency = i;
				}
			}
			ExpriementOperations.pauseExecution(300000);
		}
		double[][] cleanLatencyOfStatistic = removeMaxMin(latencyOfStatistic, indexOfMinLatency, indexOfMaxLatency);
		String[] resultStrs = generateOutputStrings(cleanLatencyOfStatistic);
		writeResultsToFile(getNewOutputFileName("doubleSize"),resultStrs);
	}
	
	public static void runTripleSizeManyTimes(int rounds, String dir) {
		if(rounds <= 0) {
			System.out.println("You specified a wrong round number");
			System.exit(-1);
		}
		
		setNumOfRounds(rounds);
		setSrcDirPath(dir+"-triple-size");
		
		rounds = rounds + 2;
		double [][] latencyOfStatistic = new double [6][rounds];
		double minLatency = 0.0;
		int indexOfMinLatency = 0;
		double maxLatency = 0.0;
		int indexOfMaxLatency = 0;
		for(int i = 0 ; i < rounds; i++) {
			double[] output = tripleSizeTPCWExperiment();
			latencyOfStatistic[0][i] = output[0];
			latencyOfStatistic[1][i] = output[1];
			latencyOfStatistic[2][i] = output[2];
			latencyOfStatistic[3][i] = output[3];
			latencyOfStatistic[4][i] = output[4];
			latencyOfStatistic[5][i] = output[5];
			if(i == 0) {
				minLatency = latencyOfStatistic[5][i];
				maxLatency = latencyOfStatistic[5][i];
			}else {
				if(latencyOfStatistic[5][i] < minLatency) {
					minLatency = latencyOfStatistic[5][i];
					indexOfMinLatency = i;
				}
				if(latencyOfStatistic[5][i] > maxLatency) {
					maxLatency = latencyOfStatistic[5][i];
					indexOfMaxLatency = i;
				}
			}
			ExpriementOperations.pauseExecution(300000);
		}
		double[][] cleanLatencyOfStatistic = removeMaxMin(latencyOfStatistic, indexOfMinLatency, indexOfMaxLatency);
		String[] resultStrs = generateOutputStrings(cleanLatencyOfStatistic);
		writeResultsToFile(getNewOutputFileName("tripleSize"),resultStrs);
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		if(args.length != 2) {
			System.err.println("Java -jar JARNAME [args0 : numOfTimes] [args1 : srcDir] ");
			System.exit(-1);
		}
		
		int nT = Integer.parseInt(args[0]);
		String dir = args[1];
		setOutputCodeDirPath("tpcw");
		runOriginalManyTimes(nT, dir);
		ExpriementOperations.pauseExecution(300000);
		runDoubleSizeManyTimes(nT, dir);
		ExpriementOperations.pauseExecution(300000);
		runTripleSizeManyTimes(nT, dir);
	}
}
