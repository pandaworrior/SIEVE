/***************************************************************
Project name: georeplication
Class file name: RuntimeFingerPrintTest.java
Created at 7:18:11 PM by chengli

Copyright (c) 2014 chengli.
All rights reserved. This program and the accompanying materials
are made available under the terms of the GNU Public License v2.0
which accompanies this distribution, and is available at
http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

Contributors:
    chengli - initial API and implementation

Contact:
    To distribute or use this code requires prior specific permission.
    In this case, please contact chengli@mpi-sws.org.
****************************************************************/

package test.runtimelogic.shadowoperationcreator;

import runtimelogic.shadowoperationcreator.shadowoperation.DBOpEntry;
import runtimelogic.shadowoperationcreator.shadowoperation.RuntimeFingerPrintGenerator;
import runtimelogic.shadowoperationcreator.shadowoperation.ShadowOperation;
import util.crdtlib.datatypes.primitivetypes.NormalDateTime;
import util.crdtlib.datatypes.primitivetypes.NormalFloat;
import util.crdtlib.datatypes.primitivetypes.NormalInteger;
import util.crdtlib.dbannotationtypes.DatabaseDef;

/**
 * @author chengli
 *
 */
public class RuntimeFingerPrintTest {
	
	public static void main(String[] args) {
		RuntimeFingerPrintGenerator fpGenerator = new RuntimeFingerPrintGenerator();
		int index = 0;
		while(index < 10000) {
			ShadowOperation op = new ShadowOperation();
			DBOpEntry dbOp = new DBOpEntry(DatabaseDef.UNIQUEINSERT, "bids");
			dbOp.addPrimaryKey(new NormalInteger("id", 314376));
			dbOp.addNormalAttribute(new NormalInteger("user_id", 69457));
			dbOp.addNormalAttribute(new NormalInteger("item_id", 702));
			dbOp.addNormalAttribute(new NormalInteger("qty", 1));
			dbOp.addNormalAttribute(new NormalFloat("bid", 25.0f));
			dbOp.addNormalAttribute(new NormalFloat("max_bid", 33.0f));
			dbOp.addNormalAttribute(new NormalDateTime("date",10));
			
			op.addOperation(dbOp);
			System.out.println(fpGenerator.computeFingerPrint(op));
			index++;
		}
	}

}
