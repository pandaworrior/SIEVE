/**
 * 
 */
package test.util.annotationparser;

import java.io.IOException;
import java.util.HashMap;

import util.annotationparser.SchemaParser;
import util.crdtlib.dbannotationtypes.dbutil.DatabaseTable;

// TODO: Auto-generated Javadoc
/**
 * The Class AnnotationParserTest.
 */
public class AnnotationParserTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String args[]) throws IOException {

		String fileName = "/var/tmp/workspace1/georeplication/src/test/util/annotationparser/sqlSchemaTest.sql";
		SchemaParser sP = new SchemaParser(fileName);
		sP.parseAnnotations();
		sP.printOut();
	}

}
