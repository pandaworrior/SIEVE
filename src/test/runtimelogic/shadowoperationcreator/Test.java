/**
 * 
 */
package test.runtimelogic.shadowoperationcreator;

import java.io.IOException;
import java.util.HashMap;

import runtimelogic.shadowoperationcreator.ShadowOperationCreator;

import util.annotationparser.SchemaParser;

import net.sf.jsqlparser.JSQLParserException;

// TODO: Auto-generated Javadoc
/**
 * The Class Test.
 */
public class Test {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JSQLParserException the jSQL parser exception
	 */
	public static void main(String args[]) throws IOException,
			JSQLParserException {

		String fileName = "/home/chengli/workspace/CrdtJDBCDriver/src/test/runtimelogic/shadowoperationcreator/sqlSchemaTest.sql";
		SchemaParser sP = new SchemaParser(fileName);
		HashMap<String, util.crdtlib.dbannotationtypes.dbutil.DatabaseTable> hMp = sP.getTableCrdtFormMap();
		sP.printOut();

		// create a transformer instance here

		//ShadowOperationCreator cTF = new ShadowOperationCreator(hMp);

		// define a few sql queries;

		// insertion

		// String insertQuery =
		// "insert into users ( Id, Name, Age, LastLogin) ";
		// insertQuery += " values (1, 'ChengLi', 25, '1971-01-01 00:00:00');";

		// String transformedQuery = cTF.transformSql(null, insertQuery);

		// System.out.println(transformedQuery);

		// String deleteQuery = "delete from person where Id = 1;";

		// transformedQuery = cTF.transformSql(null, deleteQuery);

		// System.out.println(transformedQuery);

		/*
		 * String insertQuery1 =
		 * "insert into person (pId, UniqueName, Salary,DateTimeLastLogin,";
		 * insertQuery1 +=
		 * "YourAge, Nice) values (1, 'ChengLi', 100000, '2013-06-01 00:00:00', "
		 * ; insertQuery1 += "26, 1.88);";
		 * 
		 * String transformedQuery = cTF.transformSql(null, insertQuery1);
		 */

		String updateQuery = "update best set great = 'aaa',";
		updateQuery += "hah = 10, maybe = '1971-01-01 00:00:00',";
		updateQuery += "go = 0.0 where bId = 10000;";

		/*String[] transformedQuery = cTF.transformSql(null, updateQuery);

		System.out.println(transformedQuery);*/

	}
}
