/**
 * 
 */
package test.runtimelogic.shadowoperationcreator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import runtimelogic.shadowoperationcreator.ShadowOperationCreator;

import util.debug.Debug;

import net.sf.jsqlparser.JSQLParserException;
import util.annotationparser.SchemaParser;
import util.crdtlib.dbannotationtypes.dbutil.DatabaseTable;

// TODO: Auto-generated Javadoc
/**
 * The Class ResultSetTest.
 */
public class ResultSetTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws SQLException the sQL exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JSQLParserException the jSQL parser exception
	 */
	public static void main(String args[]) throws SQLException, IOException,
			JSQLParserException {
		System.out
				.println("-------- MySQL JDBC Connection Testing ------------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}

		System.out.println("MySQL JDBC Driver Registered!");
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://srv-76-12.mpi-sws.org:50000/deltaTest",
					"root", "101010");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}

		/*
		 * String query = "SHOW COLUMNS FROM users;";
		 * 
		 * Statement stmt = connection.createStatement(); ResultSet rs =
		 * stmt.executeQuery(query); while(rs.next()){
		 * Debug.println(rs.getString(1)); Debug.println(rs.getString(2));
		 * Debug.println(rs.getString(3)); Debug.println(rs.getString(4));
		 * Debug.println(rs.getString(5)); } rs.close(); DatabaseMetaData meta =
		 * connection.getMetaData(); boolean listUniqueIndex = true; ResultSet
		 * rs = meta.getIndexInfo(null, null, "t1", listUniqueIndex, true);
		 * while(rs.next()) { String indexName = rs.getString("INDEX_NAME");
		 * String table = rs.getString("TABLE_NAME"); String schema =
		 * rs.getString("TABLE_SCHEM"); String columnName =
		 * rs.getString("COLUMN_NAME"); if(indexName == null) { continue; }
		 * System.out.println("****************************************");
		 * System.out.println("Table: " + schema + "." + table);
		 * System.out.println("Index Name: " + indexName);
		 * System.out.println("Column Name: " + columnName); }
		 */

		// create parser here

		String fileName = "/home/chengli/workspace/CrdtJDBCDriver/src/test/runtimelogic/shadowoperationcreator/sqlSchemaDeltaTest.sql";
		SchemaParser sP = new SchemaParser(fileName);
		HashMap<String, DatabaseTable> hMp = sP.getTableCrdtFormMap();
		sP.printOut();

		// create a transformer instance here

		//ShadowOperationCreator cTF = new ShadowOperationCreator(hMp);

		// put your test case here

		String selectQuery = "select * from person where pId = 10;";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(selectQuery);
		if (rs.next()) {
			Debug.println("pId " + rs.getInt("pId"));
			Debug.println("UniqueName " + rs.getString("UniqueName"));
			Debug.println("Salary " + rs.getInt("Salary"));
			Debug.println("DateTimeLastLogin "
					+ rs.getTimestamp("DateTimeLastLogin"));
			Debug.println("YourAge " + rs.getInt("YourAge"));
			Debug.println("Nice " + rs.getFloat("Nice"));
		}
		rs.close();

		String insertQuery = "insert into person(pId, UniqueName, Salary, DateTimeLastLogin, YourAge, Nice) values(10, 'ChengLi', 20000, '2013-01-05 00:00:00', 26, 1.8);";

		/*String[] transformedQuery = cTF.transformSql(null, insertQuery);

		System.out.println(transformedQuery);

		Statement st = connection.createStatement();
		for (int i = 0; i < transformedQuery.length; i++) {
			PreparedStatement updateStmt = connection
					.prepareStatement(transformedQuery[i]);
			int parameterCount = updateStmt.getParameterMetaData()
					.getParameterCount();
			if (parameterCount == 0) {
				st.addBatch(transformedQuery[i]);
				Debug.println("The " + i + " th query " + transformedQuery[i]);
			} else if (parameterCount == 2) {
				updateStmt.setString(1, "2-2");
				updateStmt.setInt(2, 15);
				String generatedQuery = updateStmt.toString().substring(
						updateStmt.toString().indexOf(":") + 1);
				st.addBatch(generatedQuery);
				Debug.println("The " + i + " th query " + generatedQuery);
			} else if (parameterCount == 3) {
				updateStmt.setString(1, "3-3");
				updateStmt.setInt(2, 16);
				updateStmt.setInt(3, 16);
				String generatedQuery = updateStmt.toString().substring(
						updateStmt.toString().indexOf(":") + 1);
				st.addBatch(generatedQuery);
				Debug.println("The " + i + " th query " + generatedQuery);
			}
			updateStmt.close();
		}

		st.executeBatch();
		st.clearBatch();

		rs = stmt.executeQuery(selectQuery);
		if (rs.next()) {
			Debug.println("pId " + rs.getInt("pId"));
			Debug.println("UniqueName " + rs.getString("UniqueName"));
			Debug.println("Salary " + rs.getInt("Salary"));
			Debug.println("DateTimeLastLogin "
					+ rs.getTimestamp("DateTimeLastLogin"));
			Debug.println("YourAge " + rs.getInt("YourAge"));
			Debug.println("Nice " + rs.getFloat("Nice"));
		}
		rs.close();

		// delete

		String deleteQuery = "delete from person where pId = 10";

		transformedQuery = cTF.transformSql(null, deleteQuery);

		System.out.println(transformedQuery);
		for (int i = 0; i < transformedQuery.length; i++) {
			PreparedStatement updateStmt = connection
					.prepareStatement(transformedQuery[i]);
			int parameterCount = updateStmt.getParameterMetaData()
					.getParameterCount();
			if (parameterCount == 0) {
				st.addBatch(transformedQuery[i]);
				Debug.println("The " + i + " th query " + transformedQuery[i]);
			} else if (parameterCount == 2) {
				updateStmt.setString(1, "2-2");
				updateStmt.setInt(2, 15);
				String generatedQuery = updateStmt.toString().substring(
						updateStmt.toString().indexOf(":") + 1);
				st.addBatch(generatedQuery);
				Debug.println("The " + i + " th query " + generatedQuery);
			} else if (parameterCount == 3) {
				updateStmt.setString(1, "3-3");
				updateStmt.setInt(2, 16);
				updateStmt.setInt(3, 16);
				String generatedQuery = updateStmt.toString().substring(
						updateStmt.toString().indexOf(":") + 1);
				st.addBatch(generatedQuery);
				Debug.println("The " + i + " th query " + generatedQuery);
			}
			updateStmt.close();
		}

		st.executeBatch();
		st.clearBatch();

		// first select and then update

		selectQuery = "select YourAge, Nice from person where pId = 10;";
		stmt = connection.createStatement();
		rs = stmt.executeQuery(selectQuery);
		if (rs.next()) {
			Debug.println("YourAge " + rs.getInt("YourAge"));
			Debug.println("Nice " + rs.getFloat("Nice"));

			int yourAge = rs.getInt("YourAge") - 15;
			float nice = (float) (rs.getFloat("Nice") - 2.9);

			String updateQuery = "update person set YourAge = " + yourAge + ",";
			updateQuery += " Nice = " + nice + " where pId = 10;";

			rs.beforeFirst();

			transformedQuery = cTF.transformSql(rs, updateQuery);

			System.out.println(transformedQuery);

			st = connection.createStatement();
			for (int i = 0; i < transformedQuery.length; i++) {
				PreparedStatement updateStmt = connection
						.prepareStatement(transformedQuery[i]);
				int parameterCount = updateStmt.getParameterMetaData()
						.getParameterCount();
				if (parameterCount == 0) {
					st.addBatch(transformedQuery[i]);
					Debug.println("The " + i + " th query "
							+ transformedQuery[i]);
				} else if (parameterCount == 3) {
					updateStmt.setString(1, "1-1");
					updateStmt.setInt(2, 18);
					updateStmt.setInt(3, 18);
					String generatedQuery = updateStmt.toString().substring(
							updateStmt.toString().indexOf(":") + 1);
					st.addBatch(generatedQuery);
					Debug.println("The " + i + " th query " + generatedQuery);
				}
				updateStmt.close();
			}

			st.executeBatch();

		}

		rs.close();
		stmt.close();*/
	}

}
