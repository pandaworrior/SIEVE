/***************************************************************
Project name: georeplication
Class file name: StaticAnalysisEvalTPCW.java
Created at 2:36:49 PM by chengli

Copyright (c) 2014 chengli.
All rights reserved. This program and the accompanying materials
are made available under the terms of the GNU Public License v2.0
which accompanies this distribution, and is available at
http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

Contributors:
    chengli - initial API and implementation

Contact:
    To distribute or use this code requires prior specific permission.
    In this case, please contact chengli@mpi-sws.org.
****************************************************************/

package test.applications;

/**
 * @author chengli
 *
 */
import japa.parser.ast.expr.Expression;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.codeparser.JavaFileParser;
import staticanalysis.codeparser.ProjectParser;
import staticanalysis.pathanalyzer.PathAbstractionCreator;
import staticanalysis.pathanalyzer.PathAnalyzer;
import staticanalysis.templatecreator.DatastructureCreator;
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
public class StaticAnalysisEvalTPCWDoubleSize {
	
	public static String outputFile = "ResultOfStaticAnalysisOfTPCW_Double_Size";
	
	public static String outputDirPath = System.getProperty("user.dir") +"/" + SourceCodeGenerator.codeName;
	
	public static String srcDirPath = "";
	
	public static void setSrcDirPath(String srcPath) {
		srcDirPath = srcPath;
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
	
	public static void removeAllFilesInOutputDir() {
		if(FileOperations.isExisted(outputDirPath)) {
			File dir = new File(outputDirPath);
			File[] files = dir.listFiles();
			if(files == null)
				return;
			for(File f : files) {
				if(!f.delete()) {
						System.out.println("file hasn't been deleted " + f.getName());
						System.exit(-1);
				}
			}
		}else {
			System.out.println("File not existed " + outputDirPath);
		}
		System.out.println("Finish cleaning up the files");
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
		removeAllFilesInOutputDir();
	}
	
	public static double computeLatency(long startTime) {
		long endTime = System.nanoTime();
		double latency = (endTime - startTime) * 0.000001;
		return latency;
	}
	
	public static String getPreciseTimeString() {
	    Calendar cal = Calendar.getInstance();
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH);
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    int minu = cal.get(Calendar.MINUTE);
	    int sec = cal.get(Calendar.SECOND);
	    String timeStr = "" + year + "-" + month + "-" + day + "-" + hour + "-" + minu + "-" + sec;
	    return timeStr;
	}
	
	public static String getOutputFileName() {
		String timeStr = getPreciseTimeString();
		return outputFile + "_" + timeStr + ".txt";
	}
	
	public static void writeResultsToFile(String fileName, String[] resultStrs) {
		FileOperations.createFileByGivenFilePath(fileName);
		FileOperations.writeToFile(fileName, new ArrayList<String>(Arrays.asList(resultStrs)));
	}
	
	public static double createDBClass(String sqlFileName) {
		SchemaParser sP = new SchemaParser(sqlFileName);
		sP.parseAnnotations();	
		DatastructureCreator dsCreator = new DatastructureCreator(sP, "tpcw");
		long startTimeOfCreatingDBClass = System.nanoTime();
		dsCreator.generateCode();
		double latencyOfCreatingDBClass = computeLatency(startTimeOfCreatingDBClass);
		return latencyOfCreatingDBClass;
	}
	
	public static double[] createWP() {
		ProjectParser pjsParser = new ProjectParser(srcDirPath,
				"TPCWStaticAnalysis");
		pjsParser.addFileToFileFilter("TPCW_Database.java"); //must exclude since duplicates
		pjsParser.addFileToFileFilter("TPCW_Database1.java"); //must exclude since duplicates
		pjsParser.addFileToFileFilter("Pad.java");
		pjsParser.addFileToFileFilter("Pad1.java");
		
		long startTimeOfCreatingTemplates = System.nanoTime();
		pjsParser.buildFileTree();
		//pjsParser.printOutFileTree();
		
		pjsParser.obtailAllControlFlowGraphs();
		//pjsParser.printOutAllControlFlowGraphs();
		pjsParser.obtainBindingsForAllMethods();
		pjsParser.resolveAllBindings();
		//pjsParser.printOutAllControlFlowGraphs();
		
		PathAnalyzer.addFunctionMustBeProcessedListFromFile(srcDirPath + "/transactionMustBeAnalyzed.txt");
		
		HashMap<CFGGraph<CodeNodeIdentifier, Expression>, PathAbstraction> pathAbMap = PathAbstractionCreator
		.obtainAllPathAbstractionForWholeProject(pjsParser.getMethodCfgMapping());
		
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
		double latencyOfCreatingTemplates = computeLatency(startTimeOfCreatingTemplates);
		
		long startTimeOfGeneratingWP = System.nanoTime();
		tmpCreator.generateAndExecuteJahobCommand();
		double latencyOfGeneratingWP = computeLatency(startTimeOfGeneratingWP);
		
		tmpCreator.printOutStatis();
		
		double returnLatencyArray[] = new double[2];
		returnLatencyArray[0] = latencyOfCreatingTemplates;
		returnLatencyArray[1] = latencyOfGeneratingWP;
		return returnLatencyArray;
	}
	
	public static double[] doubleSizeTPCWExperiment() {
		double[] output = new double[4];
		cleanUpFiles();
		
		//first create the database class there
		String sqlFileName = "/var/tmp/workspace/georeplication/applications/tpc-w-fenix/sqlSchemaTPCW.sql";
		double latencyOfCreatingDBClass = createDBClass(sqlFileName);
		output[0] = latencyOfCreatingDBClass;
		double[] returnLatencyArray = createWP();
		double latencyOfCreatingTemplates = returnLatencyArray[0];
		output[1] = latencyOfCreatingTemplates;
		double latencyOfGeneratingWP = returnLatencyArray[1];
		output[2] = latencyOfGeneratingWP;
		double totalLatency = latencyOfCreatingDBClass +  latencyOfCreatingTemplates + latencyOfGeneratingWP;
		output[3] = totalLatency;
		
		System.out.println("status: " + latencyOfCreatingDBClass + " " + latencyOfCreatingTemplates + " " + 
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
	
	public static void runDoubleSize(String dir) {
		setSrcDirPath(dir);
		double[] output = doubleSizeTPCWExperiment();
		String[] resultStrs = new String[2];
		resultStrs[0] = "#latencyOfCreatingDBClass latencyOfCreatingTemplates";
		resultStrs[0] +=" latencyOfGeneratingWP totalLatency\n";
		for(int i = 0; i < output.length; i++) {
			if(i == 0) {
				resultStrs[1] = String.valueOf(output[0]);
			}else {
				resultStrs[1] += " " + String.valueOf(output[i]);
			}
		}
		writeResultsToFile(getOutputFileName(),resultStrs);
	}
	
	/*public static void runDoubleSizeManyTimes(int rounds, String dir) {
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
	}*/
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		if(args.length != 1) {
			System.err.println("Java -jar JARNAME [args0 : srcDir] ");
			System.exit(-1);
		}
		
		String dir = args[0];
		setOutputCodeDirPath("tpcw");
		runDoubleSize(dir);
		/*runOriginalManyTimes(nT, dir);
		ExpriementOperations.pauseExecution(300000);
		runDoubleSizeManyTimes(nT, dir);
		ExpriementOperations.pauseExecution(300000);
		runTripleSizeManyTimes(nT, dir);*/
	}
}

