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
import staticanalysis.templatecreator.DatastructureCreator;
import util.annotationparser.SchemaParser;

// TODO: Auto-generated Javadoc
/**
 * The Class DatabaseClassCreatorTest.
 */
public class RUBiSDatabaseClassCreator {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		
		if(args.length != 1)
		{
			System.exit(-1);
		}

		String fileName = args[0];//"/var/tmp/workspace/georeplication/applications/RUBiS/database/rubis_sieve.sql";
		SchemaParser sP = new SchemaParser(fileName);
		sP.parseAnnotations();
		sP.printOut();
		
		DatastructureCreator dsCreator = new DatastructureCreator(sP, "rubis");
		dsCreator.generateCode();
	}

}
