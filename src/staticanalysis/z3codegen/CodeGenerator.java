/**
 * 
 */
package staticanalysis.z3codegen;

import java.util.List;

import util.annotationparser.SchemaParser;
import util.crdtlib.dbannotationtypes.dbutil.DataField;
import util.crdtlib.dbannotationtypes.dbutil.DatabaseTable;

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
		this.fWritter.appendToFile(customizedLineSeparator);
		
		// ID 
		this.fWritter.appendToFile("ID = IntSort()\n");
		
		// parse the schema
		this.parseDBSchema();
		
		// code related to tables and records
		List<DatabaseTable> tabList = this.dbSchemaParser.getAllTableInstances();
		for(int i = 0; i < tabList.size(); i++) {
			DatabaseTable tab = tabList.get(i);
			this.fWritter.appendToFile(this.codeGenForRecord(tab));
		}
	}
	
	private String codeGenForRecord(DatabaseTable tabIns) {
		String recordStr = tabIns.get_Table_Name() + " = Datatype(\'"+tabIns.get_Table_Name()+"\')" ;
		// Iterate all attributes
		List<DataField> dfList = tabIns.getDataFieldList();
		recordStr += "\n" + tabIns.get_Table_Name()+".declare(\'new\',";
		for(int i = 0; i < dfList.size(); i++)
		{
			DataField df = dfList.get(i);
			if(df.is_Primary_Key()) {
				continue;
			}
			recordStr += "(\'" + df.get_Data_Field_Name() + "\', ";
			// TODO: check how to handle timestamp
			if(df.get_Data_Type().contentEquals("INTEGER") || 
					df.get_Data_Type().contentEquals("INT") || 
					df.get_Data_Type().contentEquals("BIGINT") || 
					df.get_Data_Type().contentEquals("TIMESTAMP") ||
					df.get_Data_Type().contentEquals("DATE")) {
				recordStr += "IntSort()),";
			}else if(df.get_Data_Type().contentEquals("VARCHAR") ||
					df.get_Data_Type().contentEquals("TEXT")) {
				recordStr += "StringSort()),";
			}else if(df.get_Data_Type().contentEquals("FLOAT")) {
				recordStr += "RealSort()),";
			}else {
				System.out.println("Undefined data type " + df.get_Data_Type());
				System.exit(-1);
			}
		}
		
		// replace the last comma with )
		if(recordStr.endsWith(","))
		{
		  recordStr = recordStr.substring(0,recordStr.length() - 1) + ")";
		}
		recordStr += "\n" + tabIns.get_Table_Name() + " = "+tabIns.get_Table_Name()+".create()\n" ;
		return recordStr;
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
		cGen.codeGenForDBSchema();
		cGen.codeGenForQueries();
		cGen.codeGenForInvariant();
		cGen.codeGenForTransactions();
		cGen.codeGenForFoot();
		System.out.println("Code genearted is finished for " + projectName);
	}

}
