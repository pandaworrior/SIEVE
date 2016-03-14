/********************************************************************
Copyright (c) 2013 chengli.
All rights reserved. This program and the accompanying materials
are made available under the terms of the GNU Public License v2.0
which accompanies this distribution, and is available at
http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

Contributors:
    chengli - initial API and implementation

Contact:
    To distribute or use this code requires prior specific permission.
    In this case, please contact chengli@mpi-sws.org.
********************************************************************/
/**
 * 
 */
package test.staticanalysis.templatecreator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import util.debug.Debug;

// TODO: Auto-generated Javadoc
/**
 * The Class DatabaseQueryExample.
 */
public class DatabaseQueryExample {
	
	/** The con. */
	Connection con = null;
	
	/**
	 * Instantiates a new database query example.
	 */
	public DatabaseQueryExample(){
        String url = "jdbc:mysql://localhost:3306/testdb";
        String user = "testuser";
        String password = "test623";
        try {
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test func1.
	 */
	public void testFunc1(){
		Statement st = null;
		try {
			st = this.con.createStatement();
			/*st.executeUpdate("UPDATE Authors SET Name = 'Leo Tolstoy' WHERE Id = 1");
			
			int i = 0;
			String updateQuery =  "UPDATE Users SET Time = 'Leo Tolstoy' "
                    + "WHERE uId = " + i;
			st.executeUpdate(updateQuery);
			
			String insertQuery;
			
			insertQuery = "insert into table a ";
			insertQuery = insertQuery + "values (";
			String personName = "Cheng Li";
			insertQuery = insertQuery + personName + "," + "niubi) where Id = " + 1000;*/
			
			
			Debug.println("unique insert test");
			String uniqueInsertQuery = "insert into users (Id, UniqueName, NoTimesLoggedIn, DateTimeLastLogin, TimeZoneId, CultureInfoId, DateLastUpdated)";
			uniqueInsertQuery = uniqueInsertQuery + "values (";
			uniqueInsertQuery = uniqueInsertQuery + "a, b, c, d, e, f";
			uniqueInsertQuery = uniqueInsertQuery + ");";		
			st.executeUpdate(uniqueInsertQuery);
			
			Debug.println("insert test");
			String insertQuery = "insert into person values (a, b, c, d)";
			st.executeUpdate(insertQuery);
			
			Debug.println("delete test");
			String deleteQuery = "delete from person where Id = 1 and UUId = 2";
			st.executeUpdate(deleteQuery);
			
			Debug.println("update test1");
			String updateQuery = "update person set NoTimesLoggedIn =  NoTimesLoggedIn + 1 ";
			updateQuery = updateQuery + "where Id = 3 and UUId = 5";
			st.executeUpdate(updateQuery);
			
			Debug.println("update test2");
			String updateQuery1 = "update person set UniqueName = a, NoTimesLoggedIn =  NoTimesLoggedIn + 1 ";
			updateQuery1 = updateQuery1 + "where Id = 3 and UUId = 5";
			st.executeUpdate(updateQuery1);
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*int index = 0;
		while(index < 10){
			String deleteQuery = "DELETE FROM Person WHERE id = " + index;
			try {
				st.executeUpdate(deleteQuery);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			index++;
		}*/
		
	}
	
	/**
	 * Test func2.
	 */
	public void testFunc2(){
		Statement st = null;
		try {
			st = this.con.createStatement();
			
			
			Debug.println("unique insert test");
			String uniqueInsertQuery = "insert into users (Id, UniqueName, NoTimesLoggedIn, DateTimeLastLogin, TimeZoneId, CultureInfoId, DateLastUpdated)";
			uniqueInsertQuery = uniqueInsertQuery + "values (";
			uniqueInsertQuery = uniqueInsertQuery + "a, b, c, d, e, f";
			uniqueInsertQuery = uniqueInsertQuery + ");";		
			st.executeUpdate(uniqueInsertQuery);
			
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test func3.
	 */
	public void testFunc3(){
		Statement st = null;
		try {
			st = this.con.createStatement();
			Debug.println("update test2");
			String updateQuery1 = "update person set UniqueName = a, NoTimesLoggedIn =  NoTimesLoggedIn + 1 ";
			updateQuery1 = updateQuery1 + " where Id = 3 and UUId = 5";
			st.executeUpdate(updateQuery1);		
			
			int index = 0;
			while(index < 10){
				String deleteQuery = "DELETE FROM person WHERE Id = " + index + " and UUId = " + index;
				st.executeUpdate(deleteQuery);
				index++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
		/**
		 * Test func4.
		 */
		public void testFunc4(){
			Statement st = null;
			try {
				st = this.con.createStatement();
				Debug.println("update test2");
				
				int i = 0;
				
				if(i > 0){
					String updateQuery1 = "update person set NoTimesLoggedIn =  NoTimesLoggedIn + 1 ";
					updateQuery1 = updateQuery1 + " where Id = 3 and UUId = 5";
					st.executeUpdate(updateQuery1);		
				}else{
					String updateQuery2 = "update person set UniqueName = a";
					updateQuery2 = updateQuery2 + " where Id = 3 and UUId = 5";
					st.executeUpdate(updateQuery2);	
				}
				con.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

}
