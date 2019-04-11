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
	
	/** The DB Query Parser. */
	private QueryParser qryParser;
	
	private Project pj;
	
	private FileWritter fWritter;
	
	static int sequencer = 0;
	
	static String tablePrefix = "tab";
	
	static String indentStr = "    ";
	
	static String customizedLineSeparator = "\n##############################################"
			                                 +"################################################\n";
	
	private int getNextSequence() {
		return sequencer++;
	}
	
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
		this.fWritter.appendToFile(this.codeGenForTables(tabList));
	}
	
	private String codeGenForTables(List<DatabaseTable> tabList)
	{
		String tablesStr = "def GenState(label):\n";
		String returnStr = indentStr + "return [";
		for(int i = 0; i < tabList.size(); i++)
		{
			DatabaseTable dt = tabList.get(i);
			tablesStr += indentStr + "table_" + dt.get_Table_Name();
			tablesStr += " = Array(\'" + tablePrefix + Integer.toString(this.getNextSequence()) + "'";
			tablesStr += " + label + gen_id(), ID, " + dt.get_Table_Name() + ")" + "\n";
			returnStr += "table_" + dt.get_Table_Name() + ",";
		}
		// replace the last comma with )
		if(returnStr.endsWith(","))
		{
			returnStr = returnStr.substring(0, returnStr.length() - 1) + "]\n";
		}
		tablesStr += returnStr;
		return tablesStr;
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
		
		// TODO: here please
		// first read every queries
		List<String> qrys = this.qryParser.getQueries();
		// iterate each query, we create a function
		for(int i = 0; i < qrys.size(); i++)
		{
			this.fWritter.appendToFile(this.qryParser.codeGenForOneQuery(qrys.get(i)) + "\n");
		}
	}
	
	private void codeGenForInvariant() {
		// code related to invariant
		this.fWritter.appendToFile(customizedLineSeparator);
	}
	
	private void codeGenForTransactions() {
		// code related to all shadow operations and their
		this.fWritter.appendToFile(customizedLineSeparator);
		
		// get list of transactions
		List<AppTransaction> txnList = this.pj.getTxnList();
		
		for(int i = 0; i < txnList.size(); i++)
		{
			AppTransaction appT = txnList.get(i);
			String codeStr = appT.codeGenForTransaction();
			this.fWritter.appendToFile(codeStr);
		}
	}
	
	private void codeGenForFoot() {
		this.fWritter.tailWrite();
	}
	
	/**
	 * \brief Create an instance of CodeGenerator
	 * 
	 * @param dbSchemaFile Database Schema File
	 */
	public CodeGenerator(String projectName, String dbSchemaFile, String qryFile, String projectPath) {
		this.fWritter = new FileWritter(projectName);
		this.dbSchemaParser = new SchemaParser(dbSchemaFile);
		this.qryParser = new QueryParser(this.dbSchemaParser, qryFile);
		this.pj = new Project();
	}

	/**
	 * \brief Main function
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 4)
		{
			System.out.println("[Correct Usage:] java -jar codeGen-big.jar projectName dbSchema qryFile pjPath");
			System.exit(-1);
		}
		
		String projectName = args[0];
		String dbSchemaFile = args[1];
		String qFile = args[2];
		String pjPath = args[3];
		
		CodeGenerator cGen = new CodeGenerator(projectName, dbSchemaFile, qFile, pjPath);
		cGen.codeGenForHeader();
		cGen.codeGenForDBSchema();
		cGen.codeGenForQueries();
		cGen.codeGenForInvariant();
		cGen.codeGenForTransactions();
		cGen.codeGenForFoot();
		System.out.println("Code genearted is finished for " + projectName);
	}

}
