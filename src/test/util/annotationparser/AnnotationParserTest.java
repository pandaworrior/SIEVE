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

		String fileName = "/home/cheng/Code/explicit_tradeoff/code/benchmark/oltpbench/config/olisipo/seats/seats_olisipo.sql";//"/home/cheng/Code/explicit_tradeoff/code/benchmark/oltpbench/config/olisipo/smallbank/smallbank_olisipo.sql";//"/home/cheng/Code/RedBlue_consistency/src/applications/RUBiStxmud/database/rubis_vasco.sql";
		SchemaParser sP = new SchemaParser(fileName);
		sP.parseAnnotations();
		sP.printOut();
	}

}
