/**
 * 
 */
package staticanalysis.z3codegen;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import util.annotationparser.SchemaParser;

/**
 * @author cheng
 *
 */
public class QueryParser {
	
	/** The DB Schema Parser. */
	static SchemaParser dbSchemaParser;
	
	private String qryFile;
	
	/** The c jsql parser. */
	static CCJSqlParserManager cJsqlParser;
	
	public List<String> getQueries(){
		List<String> qrys = new ArrayList<String>();
		//read the file and put the queries into a list
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(qryFile)));
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(!line.contentEquals(""))
				{
					System.out.println("read a line " + line + " size " + line.length());
					qrys.add(line);
				}
			}
			br.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return qrys;
	}
	
	public String codeGenForOneQuery(String qry) {
		SqlQuery sqlQry = new SqlQuery(qry, dbSchemaParser);
		String codeStr = sqlQry.codeGenForQuery();
		return codeStr;
	}
	
	public QueryParser(SchemaParser dbSP, String qFile) {
		dbSchemaParser = dbSP;
		this.qryFile = qFile;
		cJsqlParser = new CCJSqlParserManager();	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
