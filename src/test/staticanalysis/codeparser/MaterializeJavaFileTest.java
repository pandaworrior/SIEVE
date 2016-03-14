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
package test.staticanalysis.codeparser;

import staticanalysis.codeparser.jdbcextend.PrepareStatementTagMaterializer;

// TODO: Auto-generated Javadoc
/**
 * The Class MaterializeJavaFileTest.
 *
 * @author chengli
 */
public class MaterializeJavaFileTest {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		// creates an project parser for the tree
		String dirPath = "/var/tmp/workspace1/georeplication/applications/tpc-w-fenix";
		String javaFileName = "TPCW_Database.std.java";
		String sqlPropertiesFileName = "sql-mysql.properties";
		
		PrepareStatementTagMaterializer ptM = new PrepareStatementTagMaterializer(dirPath, javaFileName, sqlPropertiesFileName);
		ptM.replaceTagStrInJavaFileWithSql();
	}

}
