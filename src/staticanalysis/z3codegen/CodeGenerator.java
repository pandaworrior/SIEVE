/**
 * 
 */
package staticanalysis.z3codegen;

import util.annotationparser.SchemaParser;

/**
 * This class is used to statically analyze web applications written in Java and
 * translate the applications into Z3 code with conditions and side effects.
 * @author cheng
 *
 */
public class CodeGenerator {
	
	/** The DB Schema Parser. */
	private SchemaParser dbSchemaParser;
	
	private FileWritter fWritter;
	
	static String customizedLineSeparator = "\n##############################################"
			                                 +"################################################\n";
	
	
	private void parseDBSchema() {
		this.dbSchemaParser.parseAnnotations();
		this.dbSchemaParser.printOut();
	}
	
	private void codeGenForHeader() {
		this.fWritter.headerWrite();
	}
	
	private void codeGenForDBSchema() {
		//TODO complete first
		this.fWritter.appendToFile(customizedLineSeparator);
		// code related to tables and records
	}
	
	private void codeGenForQueries() {
		// code related to queries 
		this.fWritter.appendToFile(customizedLineSeparator);
	}
	
	private void codeGenForInvariant() {
		// code related to invariant
		this.fWritter.appendToFile(customizedLineSeparator);
	}
	
	private void codeGenForTransactions() {
		// code related to all shadow operations and their
		this.fWritter.appendToFile(customizedLineSeparator);
	}
	
	private void codeGenForFoot() {
		this.fWritter.tailWrite();
	}
	
	/**
	 * \brief Create an instance of CodeGenerator
	 * 
	 * @param dbSchemaFile Database Schema File
	 */
	public CodeGenerator(String projectName, String dbSchemaFile) {
		this.fWritter = new FileWritter(projectName);
		this.dbSchemaParser = new SchemaParser(dbSchemaFile);
	}

	/**
	 * \brief Main function
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 2)
		{
			System.out.println("[Correct Usage:] java -jar codeGen-big.jar projectName dbSchema");
			System.exit(-1);
		}
		
		String projectName = args[0];
		String dbSchemaFile = args[1];
		
		CodeGenerator cGen = new CodeGenerator(projectName, dbSchemaFile);
		cGen.codeGenForHeader();
		cGen.parseDBSchema();
		cGen.codeGenForDBSchema();
		cGen.codeGenForQueries();
		cGen.codeGenForInvariant();
		cGen.codeGenForTransactions();
		cGen.codeGenForFoot();
		System.out.println("Code genearted is finished for " + projectName);
	}

}
