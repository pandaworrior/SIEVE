/**
 * 
 */
package staticanalysis.z3codegen;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBSingleOperation;
import util.annotationparser.SchemaParser;
import util.crdtlib.dbannotationtypes.dbutil.DataField;
import util.crdtlib.dbannotationtypes.dbutil.DatabaseTable;

/**
 * @author cheng
 *
 */
public class SqlQuery {
	
	private String sqlName;
	
	private String sqlStr;
	
	private List<String> parameters;
	
	/** The DB Schema Parser. */
	static SchemaParser dbSchemaParser;
	
	/** Separator between the query name and query*/
	static char cSeparator = '=';
	
	static String z3Select = "Select";
	
	static String z3Store = "Store";
	
	static String recordPrefix = "rec_";
	
	static String fieldPrefix = "f_";
	
	static String oldPrefix = "o_";
	
	static String newPrefix = "n_";
	
	private void setNameAndQuery(String sStr) {
		
		System.out.println("Set name and query for a qry " + sStr);
		// break the sStr into two parts
		int indexOfSeparator = sStr.indexOf(cSeparator);
		this.sqlName = sStr.substring(0, indexOfSeparator).trim();
		this.sqlStr = sStr.substring(indexOfSeparator + 1).trim();
		if(sqlStr.startsWith("\"")) {
			sqlStr = sqlStr.substring(1);
		}
		if(sqlStr.endsWith("\""))
		{
			sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
		}
		
		System.out.println("Query name is " + this.sqlName + " query is " + this.sqlStr);
	}
	
	private String getPrimaryKeyList(String tableName) {
		String returnStr = "";
		// get Table instance
		DatabaseTable dTable = dbSchemaParser.getTableByName(tableName);
				
		// get primary key
		List<DataField> pkDFs = dTable.getPrimaryKeyDataFieldList();
		for(int i = 0; i < pkDFs.size(); i++)
		{
			DataField dF = pkDFs.get(i);
			returnStr += dF.get_Data_Field_Name() + ", ";
		}
		if(returnStr.endsWith(", "))
		{
			returnStr = returnStr.substring(0, returnStr.length() - 2);
		}
		return returnStr;
	}
	
	private String codeGenForSelectQuery(Select sqlStmt) {
		String codeStr = "def " + this.sqlName + "(m, ";
		
		SelectBody sb = sqlStmt.getSelectBody();
		if( ! (sb instanceof PlainSelect))
			throw new RuntimeException( "Cannot process select : " + sqlStmt);
		PlainSelect psb = (PlainSelect)sb;
		
		FromItem fi = psb.getFromItem();
		// get the table name;
		String tableName = ((Table)fi).getName();
		
		// function def
		codeStr += this.getPrimaryKeyList(tableName) + "): \n";
		
		// get the corresponding record first
		codeStr += CodeGenerator.indentStr + "rec_" + tableName + " = Select(m, ";
		codeStr += this.getPrimaryKeyList(tableName) + ")\n";
		
		
		// check if * or individual attributes
		List<SelectItem> selItems = psb.getSelectItems();
		if(selItems.size() == 1 && selItems.get(0).toString().contains("*")) {
			// return the whole record
			codeStr += CodeGenerator.indentStr + "return " + "rec_" + tableName;
		}else
		{
			String returnStr = CodeGenerator.indentStr + "return [";
			String fieldsStr = "";
			for(int i = 0; i < selItems.size(); i++) {
				SelectItem sIt = selItems.get(i);
				DataField dF = dbSchemaParser.getTableByName(tableName).get_Data_Field(sIt.toString());
				fieldsStr += CodeGenerator.indentStr + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
				fieldsStr += " = " + tableName + "." + dF.get_Data_Field_Name() + "(" + "rec_" + tableName + ")\n";
				returnStr += fieldPrefix + recordPrefix + dF.get_Data_Field_Name() + ", ";
			}
			if(returnStr.endsWith(", "))
			{
				returnStr = returnStr.substring(0, returnStr.length() - 2) + "]\n";
			}
			codeStr += fieldsStr;
			codeStr += returnStr;
		}
		// generate the return str
		
		return codeStr;
	}
	
	private String codeGenForInsert(Insert insStmt)
	{
		String codeStr = "";
		return codeStr;
	}
	
	private String codeGenForUpdate(Update updStmt)
	{
		String codeStr = "";
		return codeStr;
	}
	
	private String codeGenForDelete(Delete delStmt)
	{
		String codeStr = "";
		return codeStr;
	}
	
	// first get the primary input, select from it
	
	// if select, get attributes
	
	// if update, get old attributes
	
	// if number, get the delta
	
	public String codeGenForQuery() {
		String codeStr = "";
		// create a statement for this query
		try {
			net.sf.jsqlparser.statement.Statement sqlStmt = QueryParser.cJsqlParser.parse(new StringReader(this.sqlStr));
		
			if (sqlStmt instanceof Select) {
				codeStr = this.codeGenForSelectQuery((Select)sqlStmt);
			}else if(sqlStmt instanceof Insert) {
				Insert insStmt = (Insert) sqlStmt;
				String tableName = insStmt.getTable().getName();
				codeStr = this.codeGenForInsert((Insert) sqlStmt);
			}else if(sqlStmt instanceof Update) {
				Update insStmt = (Update) sqlStmt;
				String tableName = insStmt.getTable().getName();
				codeStr = this.codeGenForUpdate((Update) sqlStmt);
			}else if(sqlStmt instanceof Delete) {
				// for update, we need to think about the old and new data
				Delete insStmt = (Delete) sqlStmt;
				String tableName = insStmt.getTable().getName();
				throw new RuntimeException( "Delete not support yet " + sqlStmt);
			}
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return codeStr;
	}

	/**
	 * 
	 */
	public SqlQuery(String sStr, SchemaParser sP) {
		this.setNameAndQuery(sStr);
		this.parameters = new ArrayList<String>();
		this.parameters.add("m"); // table
		dbSchemaParser = sP;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}