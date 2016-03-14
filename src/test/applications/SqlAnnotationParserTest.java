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
package test.applications;

import java.io.IOException;

import util.annotationparser.SchemaParser;

// TODO: Auto-generated Javadoc
/**
 * The Class SqlAnnotationParserTest.
 *
 * @author chengli
 */
public class SqlAnnotationParserTest {
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String args[]) throws IOException {

		String fileName = "/var/tmp/workspace1/georeplication/applications/tpc-w-fenix/sqlSchemaTPCW.sql";
		SchemaParser sP = new SchemaParser(fileName);
		sP.parseAnnotations();
		sP.printOut();
	}
}
