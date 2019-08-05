/**
 * 
 */
package staticanalysis.z3codegen;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
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
	
	/** Separator between the query name and query*/
	static char cSeparator = '=';
	
	static String z3Select = "Select";
	
	static String z3Store = "Store";
	
	static String recordPrefix = "rec_";
	
	static String fieldPrefix = "f_";
	
	static String oldPrefix = "o_";
	
	static String newPrefix = "n_";
	
	static String parameterSuffix = "_p";
	
	static String tablePrefix = "table_";
	
	private void setNameAndQuery(String sStr) {
		
		//System.out.println("Set name and query for a qry " + sStr);
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
		
		//System.out.println("Query name is " + this.sqlName + " query is " + this.sqlStr);
	}
	
	private static String getPrimaryKeyList(String tableName) {
		String returnStr = "";
		// get Table instance
		DatabaseTable dTable = QueryParser.dbSchemaParser.getTableByName(tableName);
				
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
				DataField dF = QueryParser.dbSchemaParser.getTableByName(tableName).get_Data_Field(sIt.toString());
				fieldsStr += CodeGenerator.indentStr + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
				fieldsStr += " = " + tableName + "." + dF.get_Data_Field_Name() + "(" + recordPrefix + tableName + ")\n";
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
	
	private String codeGenForUpdate(Update updateStmt)
	{
		String codeStr = "def " + this.sqlName + "(m, ";
		String tableName = updateStmt.getTable().getName();
		
		// function def
		codeStr += this.getPrimaryKeyList(tableName) + ", ";
		
		// create a list of parameters
		List<String> colList = new ArrayList<String>();
		List<String> valList = new ArrayList<String>();
		Iterator colIt = updateStmt.getColumns().iterator();
		while(colIt.hasNext()){
			String colName = colIt.next().toString();
			colList.add(colName);
			codeStr += colName + parameterSuffix + ", ";
		}
		if(codeStr.endsWith(", "))
		{
			codeStr = codeStr.substring(0, codeStr.length() - 2) + "): \n";
		}
		
		// get the old record
		String oldStr = CodeGenerator.indentStr + oldPrefix + recordPrefix + tableName + " = Select(m, ";
		oldStr += this.getPrimaryKeyList(tableName) + ")\n";
		
		codeStr += oldStr;
		
		Iterator valueIt = updateStmt.getExpressions().iterator();
		while(valueIt.hasNext()){
			valList.add(valueIt.next().toString());
		}
		
		for(int i = 0; i < colList.size(); i++) {
			DataField dF = QueryParser.dbSchemaParser.getTableByName(tableName).get_Data_Field(colList.get(i));
			String oldFieldStr = CodeGenerator.indentStr + oldPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
			oldFieldStr += " = " + tableName + "." + dF.get_Data_Field_Name() + "(" + recordPrefix + tableName + ")\n";
			String newFieldStr = CodeGenerator.indentStr + newPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
			
			String valStr = valList.get(i);
			if(valStr.contains("+"))
			{
				newFieldStr += " = " + oldPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
				newFieldStr += " + " + dF.get_Data_Field_Name() + parameterSuffix + "\n";
			}else if(valStr.contains("-")) {
				newFieldStr += " = " + oldPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
				newFieldStr += " - " + dF.get_Data_Field_Name() + parameterSuffix + "\n";
			}else {
				newFieldStr += " = " + dF.get_Data_Field_Name() + parameterSuffix + "\n";
			}
			codeStr += oldFieldStr;
			codeStr += newFieldStr;
		}
		
		// store the new record
		String newStr = CodeGenerator.indentStr + newPrefix + recordPrefix + tableName + " = " + tableName + ".new(";
		for(int i = 0; i < colList.size(); i++)
		{
			newStr += newPrefix + fieldPrefix + recordPrefix + colList.get(i) + ", ";
		}
		if(newStr.endsWith(", ")) {
			newStr = newStr.substring(0, newStr.length() - 2) + ")\n";
		}
		
		newStr += CodeGenerator.indentStr + "return Store(m, " + this.getPrimaryKeyList(tableName) + ", " + newPrefix + recordPrefix + tableName + ")\n";
		codeStr += newStr;
		return codeStr;
	}
	
	private static String getOldTable(String tableName, int tableUsedTime) {
		if(tableUsedTime == 0)
		{
			return tablePrefix + tableName;
		}else {
			return tablePrefix + tableName + "_" + (tableUsedTime - 1);
		}
	}
	
	public static String codeGenForUpdateStmt(Update updateStmt, int tableUsedTime)
	{
		String codeStr = "";
		String tableName = updateStmt.getTable().getName();
		String oldTableName = getOldTable(tableName, tableUsedTime);
		String newTableName = tablePrefix + tableName + "_" + tableUsedTime;
		
		// create a list of parameters
		List<String> colList = new ArrayList<String>();
		List<String> valList = new ArrayList<String>();
		Iterator colIt = updateStmt.getColumns().iterator();
		while(colIt.hasNext()){
			String colName = colIt.next().toString();
			colList.add(colName);
		}
		
		// get the old record
		String oldStr = CodeGenerator.indentStr + CodeGenerator.indentStr + oldPrefix + recordPrefix + tableName + " = Select(" + oldTableName + ", ";
		oldStr += getPrimaryKeyList(tableName) + ")\n";
		
		codeStr += oldStr;
		
		Iterator valueIt = updateStmt.getExpressions().iterator();
		while(valueIt.hasNext()){
			valList.add(valueIt.next().toString());
		}
		
		for(int i = 0; i < colList.size(); i++) {
			DataField dF = QueryParser.dbSchemaParser.getTableByName(tableName).get_Data_Field(colList.get(i));
			String oldFieldStr = CodeGenerator.indentStr + CodeGenerator.indentStr + oldPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
			oldFieldStr += " = " + tableName + "." + dF.get_Data_Field_Name() + "(" + recordPrefix + tableName + ")\n";
			String newFieldStr = CodeGenerator.indentStr + CodeGenerator.indentStr + newPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
			
			String valStr = valList.get(i);
			if(valStr.contains("+"))
			{
				newFieldStr += " = " + oldPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
				newFieldStr += " + " + dF.get_Data_Field_Name() + parameterSuffix + "\n";
			}else if(valStr.contains("-")) {
				newFieldStr += " = " + oldPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
				newFieldStr += " - " + dF.get_Data_Field_Name() + parameterSuffix + "\n";
			}else {
				newFieldStr += " = " + dF.get_Data_Field_Name() + parameterSuffix + "\n";
			}
			codeStr += oldFieldStr;
			codeStr += newFieldStr;
		}
		
		// store the new record
		String newStr = CodeGenerator.indentStr + CodeGenerator.indentStr + newPrefix + recordPrefix + tableName + " = " + tableName + ".new(";
		for(int i = 0; i < colList.size(); i++)
		{
			newStr += newPrefix + fieldPrefix + recordPrefix + colList.get(i) + ", ";
		}
		if(newStr.endsWith(", ")) {
			newStr = newStr.substring(0, newStr.length() - 2) + ")\n";
		}
		
		newStr += CodeGenerator.indentStr + CodeGenerator.indentStr + newTableName + " = " + "Store(" + oldTableName + ", " + getPrimaryKeyList(tableName) + ", " + newPrefix + recordPrefix + tableName + ")\n";
		codeStr += newStr;
		return codeStr;
	}
	
	public static String codeGenForInsertStmt(Insert inStmt, int tableUsedTime)
	{
		String codeStr = "";
		String tableName = inStmt.getTable().getName();
		String oldTableName = getOldTable(tableName, tableUsedTime);
		String newTableName = tablePrefix + tableName + "_" + tableUsedTime;
		
		// create a list of parameters
		List<String> colList = new ArrayList<String>();
		List<String> valList = new ArrayList<String>();
		Iterator colIt = inStmt.getColumns().iterator();
		while(colIt.hasNext()){
			String colName = colIt.next().toString();
			colList.add(colName);
		}
		
		// get the old record
		String oldStr = CodeGenerator.indentStr + CodeGenerator.indentStr + oldPrefix + recordPrefix + tableName + " = Select(" + oldTableName + ", ";
		oldStr += getPrimaryKeyList(tableName) + ")\n";
		
		codeStr += oldStr;
		
		Iterator valueIt = ((ExpressionList)inStmt.getItemsList()).getExpressions().iterator();
		while(valueIt.hasNext()){
			valList.add(valueIt.next().toString());
		}
		
		for(int i = 0; i < colList.size(); i++) {
			DataField dF = QueryParser.dbSchemaParser.getTableByName(tableName).get_Data_Field(colList.get(i));
			String oldFieldStr = CodeGenerator.indentStr + CodeGenerator.indentStr + oldPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
			oldFieldStr += " = " + tableName + "." + dF.get_Data_Field_Name() + "(" + recordPrefix + tableName + ")\n";
			String newFieldStr = CodeGenerator.indentStr + CodeGenerator.indentStr + newPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
			
			String valStr = valList.get(i);
			if(valStr.contains("+"))
			{
				newFieldStr += " = " + oldPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
				newFieldStr += " + " + dF.get_Data_Field_Name() + parameterSuffix + "\n";
			}else if(valStr.contains("-")) {
				newFieldStr += " = " + oldPrefix + fieldPrefix + recordPrefix + dF.get_Data_Field_Name();
				newFieldStr += " - " + dF.get_Data_Field_Name() + parameterSuffix + "\n";
			}else {
				newFieldStr += " = " + dF.get_Data_Field_Name() + parameterSuffix + "\n";
			}
			codeStr += oldFieldStr;
			codeStr += newFieldStr;
		}
		
		// store the new record
		String newStr = CodeGenerator.indentStr + CodeGenerator.indentStr + newPrefix + recordPrefix + tableName + " = " + tableName + ".new(";
		for(int i = 0; i < colList.size(); i++)
		{
			newStr += newPrefix + fieldPrefix + recordPrefix + colList.get(i) + ", ";
		}
		if(newStr.endsWith(", ")) {
			newStr = newStr.substring(0, newStr.length() - 2) + ")\n";
		}
		
		newStr += CodeGenerator.indentStr + CodeGenerator.indentStr + newTableName + " = " + "Store(" + oldTableName + ", " + getPrimaryKeyList(tableName) + ", " + newPrefix + recordPrefix + tableName + ")\n";
		codeStr += newStr;
		return codeStr;
	}
	
	public static String codeGenForDeleteStmt(Delete deStmt, int tableUsedTime)
	{
		String codeStr = "";
		String tableName = deStmt.getTable().getName();
		String oldTableName = getOldTable(tableName, tableUsedTime);
		String newTableName = tablePrefix + tableName + "_" + tableUsedTime;
		
		// create a list of parameters
		List<String> colList = new ArrayList<String>();
		List<String> valList = new ArrayList<String>();
		
		// get the old record
		String oldStr = CodeGenerator.indentStr + CodeGenerator.indentStr + oldPrefix + recordPrefix + tableName + " = Select(" + oldTableName + ", ";
		oldStr += getPrimaryKeyList(tableName) + ")\n";
		
		codeStr += oldStr;
		
		
		// store the new record
		String newStr = CodeGenerator.indentStr + CodeGenerator.indentStr + newPrefix + recordPrefix + tableName + " = " + tableName + ".new(";
		for(int i = 0; i < colList.size(); i++)
		{
			newStr += newPrefix + fieldPrefix + recordPrefix + colList.get(i) + ", ";
		}
		if(newStr.endsWith(", ")) {
			newStr = newStr.substring(0, newStr.length() - 2) + ")\n";
		}
		
		newStr += CodeGenerator.indentStr + CodeGenerator.indentStr + newTableName + " = " + "Store(" + oldTableName + ", " + getPrimaryKeyList(tableName) + ", " + newPrefix + recordPrefix + tableName + ")\n";
		codeStr += newStr;
		return codeStr;
	}
	
	private String codeGenForDelete(Delete delStmt)
	{
		String codeStr = "";
		return codeStr;
	}
	
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
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
