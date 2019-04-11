/**
 * 
 */
package staticanalysis.z3codegen;

import java.util.ArrayList;
import java.util.List;

import util.annotationparser.SchemaParser;
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
	
	private void setNameAndQuery(String sStr) {
		
		System.out.println("Set name and query for a qry " + sStr);
		// break the sStr into two parts
		int indexOfSeparator = sStr.indexOf(cSeparator);
		this.sqlName = sStr.substring(0, indexOfSeparator).trim();
		this.sqlStr = sStr.substring(indexOfSeparator + 1).trim();
	}
	
	// first get the primary input, select from it
	
	// if select, get attributes
	
	// if update, get old attributes
	
	// if number, get the delta
	
	public String codeGenForQuery() {
		String codeStr = "def " + this.sqlName + "(";
		for(int i = 0; i < this.parameters.size(); i++)
		{
			codeStr += this.parameters.get(i) + ", ";
		}
		if(codeStr.endsWith(", "))
		{
			codeStr = codeStr.substring(0, codeStr.length() - 2) + "):";
		}
		
		// create statements for queries
		// for update, we need to think about the old and new data
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
