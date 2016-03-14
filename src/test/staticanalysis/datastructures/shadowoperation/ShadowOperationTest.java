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
package test.staticanalysis.datastructures.shadowoperation;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import runtimelogic.shadowoperationcreator.shadowoperation.DBOpEntry;
import runtimelogic.shadowoperationcreator.shadowoperation.ShadowOperation;

import util.crdtlib.datatypes.AppendOnlySet;
import util.crdtlib.datatypes.primitivetypes.LogicalTimestamp;
import util.crdtlib.datatypes.primitivetypes.LwwDateTime;
import util.crdtlib.datatypes.primitivetypes.LwwDouble;
import util.crdtlib.datatypes.primitivetypes.LwwFloat;
import util.crdtlib.datatypes.primitivetypes.LwwInteger;
import util.crdtlib.datatypes.primitivetypes.LwwLogicalTimestamp;
import util.crdtlib.datatypes.primitivetypes.LwwString;
import util.crdtlib.datatypes.primitivetypes.NormalDateTime;
import util.crdtlib.datatypes.primitivetypes.NormalDouble;
import util.crdtlib.datatypes.primitivetypes.NormalFloat;
import util.crdtlib.datatypes.primitivetypes.NormalInteger;
import util.crdtlib.datatypes.primitivetypes.NormalString;
import util.crdtlib.datatypes.primitivetypes.NumberDeltaDateTime;
import util.crdtlib.datatypes.primitivetypes.NumberDeltaDouble;
import util.crdtlib.datatypes.primitivetypes.NumberDeltaFloat;
import util.crdtlib.datatypes.primitivetypes.NumberDeltaInteger;
import util.crdtlib.datatypes.primitivetypes.PrimitiveType;
import util.crdtlib.dbannotationtypes.DatabaseDef;
import util.crdtlib.dbannotationtypes.dbutil.DatabaseFunction;
import util.debug.Debug;

// TODO: Auto-generated Javadoc
/**
 * The Class ShadowOpTemplateTest.
 */
public class ShadowOperationTest {
	
	public static DateFormat dateFormat = DatabaseFunction.getNewDateFormatInstance();
	
	private static DBOpEntry generateInsertDBOpEntry() {
		DBOpEntry dbOpEntry = new DBOpEntry(DatabaseDef.INSERT, "inserttable");
		NormalDateTime nPt1 = new NormalDateTime("time", new java.sql.Timestamp(10000001));
		dbOpEntry.addPrimaryKey(nPt1);
		NormalDouble nPt2 = new NormalDouble("grade", 1.0);
		dbOpEntry.addPrimaryKey(nPt2);
		NormalFloat nPt3 = new NormalFloat("salary", 10000000.0f);
		dbOpEntry.addPrimaryKey(nPt3);
		NormalInteger nPt4 = new NormalInteger("age", 26);
		dbOpEntry.addPrimaryKey(nPt4);
		NormalString nPt6 = new NormalString("name", "Cheng Li");
		dbOpEntry.addPrimaryKey(nPt6);
		LwwDateTime pt1 = new LwwDateTime("time", new java.sql.Timestamp(10000001));
		dbOpEntry.addNormalAttribute(pt1);
		LwwDouble pt2 = new LwwDouble("grade", 1.0);
		dbOpEntry.addNormalAttribute(pt2);
		LwwFloat pt3 = new LwwFloat("salary", 10000000.0f);
		dbOpEntry.addNormalAttribute(pt3);
		LwwInteger pt4 = new LwwInteger("age", 26);
		dbOpEntry.addNormalAttribute(pt4);
		LwwString pt6 = new LwwString("name", "Cheng Li");
		dbOpEntry.addNormalAttribute(pt6);
		NumberDeltaDateTime pt7 = new NumberDeltaDateTime("time2", new java.sql.Timestamp(200000000));
		dbOpEntry.addNormalAttribute(pt7);
		NumberDeltaDouble pt8 = new NumberDeltaDouble("grade2", 1.0);
		dbOpEntry.addNormalAttribute(pt8);
		NumberDeltaFloat pt9 = new NumberDeltaFloat("salary2", 2000000.0f);
		dbOpEntry.addNormalAttribute(pt9);
		NumberDeltaInteger pt10 = new NumberDeltaInteger("age2", 25);
		dbOpEntry.addNormalAttribute(pt10);
		System.out.println(dbOpEntry.getSelectQuery(dateFormat));
		return dbOpEntry;
	}
	
	private static DBOpEntry generateUniqueInsertDBOpEntry() {
		DBOpEntry dbOpEntry = new DBOpEntry(DatabaseDef.UNIQUEINSERT, "uniqueInserttable");
		NormalDateTime nPt1 = new NormalDateTime("time", new java.sql.Timestamp(10000001));
		dbOpEntry.addPrimaryKey(nPt1);
		NormalDouble nPt2 = new NormalDouble("grade", 1.0);
		dbOpEntry.addPrimaryKey(nPt2);
		NormalFloat nPt3 = new NormalFloat("salary", 10000000.0f);
		dbOpEntry.addPrimaryKey(nPt3);
		NormalInteger nPt4 = new NormalInteger("age", 26);
		dbOpEntry.addPrimaryKey(nPt4);
		NormalString nPt6 = new NormalString("name", "Cheng Li");
		dbOpEntry.addPrimaryKey(nPt6);
		LwwDateTime pt1 = new LwwDateTime("time", new java.sql.Timestamp(10000001));
		dbOpEntry.addNormalAttribute(pt1);
		LwwDouble pt2 = new LwwDouble("grade", 1.0);
		dbOpEntry.addNormalAttribute(pt2);
		LwwFloat pt3 = new LwwFloat("salary", 10000000.0f);
		dbOpEntry.addNormalAttribute(pt3);
		LwwInteger pt4 = new LwwInteger("age", 26);
		dbOpEntry.addNormalAttribute(pt4);
		LwwString pt6 = new LwwString("name", "Cheng Li");
		dbOpEntry.addNormalAttribute(pt6);
		NumberDeltaDateTime pt7 = new NumberDeltaDateTime("time2", new java.sql.Timestamp(200000000));
		dbOpEntry.addNormalAttribute(pt7);
		NumberDeltaDouble pt8 = new NumberDeltaDouble("grade2", 1.0);
		dbOpEntry.addNormalAttribute(pt8);
		NumberDeltaFloat pt9 = new NumberDeltaFloat("salary2", 2000000.0f);
		dbOpEntry.addNormalAttribute(pt9);
		NumberDeltaInteger pt10 = new NumberDeltaInteger("age2", 25);
		dbOpEntry.addNormalAttribute(pt10);
		
		LwwInteger ts = new LwwInteger("ts", 5);
		LwwLogicalTimestamp lgs = new LwwLogicalTimestamp("lgs", new LogicalTimestamp(1));
		System.out.println(dbOpEntry.getInsertQuery(dateFormat, ts, lgs));
		return dbOpEntry;
	}
	
	private static DBOpEntry generateUpdateDBOpEntry() {
		DBOpEntry dbOpEntry = new DBOpEntry(DatabaseDef.UPDATE, "updatetable");
		NormalDateTime nPt1 = new NormalDateTime("time", new java.sql.Timestamp(10000001));
		dbOpEntry.addPrimaryKey(nPt1);
		NormalDouble nPt2 = new NormalDouble("grade", 1.0);
		dbOpEntry.addPrimaryKey(nPt2);
		NormalFloat nPt3 = new NormalFloat("salary", 10000000.0f);
		dbOpEntry.addPrimaryKey(nPt3);
		NormalInteger nPt4 = new NormalInteger("age", 26);
		dbOpEntry.addPrimaryKey(nPt4);
		NormalString nPt6 = new NormalString("name", "Cheng Li");
		dbOpEntry.addPrimaryKey(nPt6);
		LwwDateTime pt1 = new LwwDateTime("time", new java.sql.Timestamp(10000001));
		dbOpEntry.addNormalAttribute(pt1);
		LwwDouble pt2 = new LwwDouble("grade", 1.0);
		dbOpEntry.addNormalAttribute(pt2);
		LwwFloat pt3 = new LwwFloat("salary", 10000000.0f);
		dbOpEntry.addNormalAttribute(pt3);
		LwwInteger pt4 = new LwwInteger("age", 26);
		dbOpEntry.addNormalAttribute(pt4);
		LwwString pt6 = new LwwString("name", "Cheng Li");
		dbOpEntry.addNormalAttribute(pt6);
		NumberDeltaDateTime pt7 = new NumberDeltaDateTime("time2", new java.sql.Timestamp(200000000));
		dbOpEntry.addNormalAttribute(pt7);
		NumberDeltaDouble pt8 = new NumberDeltaDouble("grade2", 1.0);
		dbOpEntry.addNormalAttribute(pt8);
		NumberDeltaFloat pt9 = new NumberDeltaFloat("salary2", 2000000.0f);
		dbOpEntry.addNormalAttribute(pt9);
		NumberDeltaInteger pt10 = new NumberDeltaInteger("age2", 25);
		dbOpEntry.addNormalAttribute(pt10);
		
		LwwInteger ts = new LwwInteger("ts", 5);
		LwwLogicalTimestamp lgs = new LwwLogicalTimestamp("lgs", new LogicalTimestamp(1));
		String[] returnStrs = dbOpEntry.getUpdateQuery(dateFormat, ts, lgs);
		for(int i = 0; i < returnStrs.length; i++) {
			System.out.println(returnStrs[i]);
		}
		return dbOpEntry;
	}
	
	private static DBOpEntry generateDeleteDBOpEntry() {
		DBOpEntry dbOpEntry = new DBOpEntry(DatabaseDef.DELETE, "deletetable");
		NormalDateTime nPt1 = new NormalDateTime("time", new java.sql.Timestamp(10000001));
		dbOpEntry.addPrimaryKey(nPt1);
		NormalDouble nPt2 = new NormalDouble("grade", 1.0);
		dbOpEntry.addPrimaryKey(nPt2);
		NormalFloat nPt3 = new NormalFloat("salary", 10000000.0f);
		dbOpEntry.addPrimaryKey(nPt3);
		NormalInteger nPt4 = new NormalInteger("age", 26);
		dbOpEntry.addPrimaryKey(nPt4);
		NormalString nPt6 = new NormalString("name", "Cheng Li");
		dbOpEntry.addPrimaryKey(nPt6);
		LwwDateTime pt1 = new LwwDateTime("time", new java.sql.Timestamp(10000001));
		dbOpEntry.addNormalAttribute(pt1);
		LwwDouble pt2 = new LwwDouble("grade", 1.0);
		dbOpEntry.addNormalAttribute(pt2);
		LwwFloat pt3 = new LwwFloat("salary", 10000000.0f);
		dbOpEntry.addNormalAttribute(pt3);
		LwwInteger pt4 = new LwwInteger("age", 26);
		dbOpEntry.addNormalAttribute(pt4);
		LwwString pt6 = new LwwString("name", "Cheng Li");
		dbOpEntry.addNormalAttribute(pt6);
		NumberDeltaDateTime pt7 = new NumberDeltaDateTime("time2", new java.sql.Timestamp(200000000));
		dbOpEntry.addNormalAttribute(pt7);
		NumberDeltaDouble pt8 = new NumberDeltaDouble("grade2", 1.0);
		dbOpEntry.addNormalAttribute(pt8);
		NumberDeltaFloat pt9 = new NumberDeltaFloat("salary2", 2000000.0f);
		dbOpEntry.addNormalAttribute(pt9);
		NumberDeltaInteger pt10 = new NumberDeltaInteger("age2", 25);
		dbOpEntry.addNormalAttribute(pt10);
		
		LwwInteger ts = new LwwInteger("ts", 5);
		LwwLogicalTimestamp lgs = new LwwLogicalTimestamp("lgs", new LogicalTimestamp(1));
		System.out.println(dbOpEntry.getDeleteQuery(dateFormat, ts, lgs));
		return dbOpEntry;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException 
	 */
	public static void main(String args[]) throws IOException{
		ShadowOperation shdOpT = new ShadowOperation();
		shdOpT.addOperation(generateInsertDBOpEntry());
		shdOpT.addOperation(generateUniqueInsertDBOpEntry());
		shdOpT.addOperation(generateUpdateDBOpEntry());
		shdOpT.addOperation(generateDeleteDBOpEntry());
		String str1 = shdOpT.toString();
		System.out.println(str1);
		
		/*byte[] bArray = shdOpT.encodeShadowOperation();
		ByteArrayInputStream bIn = new ByteArrayInputStream(bArray);
		DataInputStream in = new DataInputStream(bIn);
		
		ShadowOperation shdOpT1 = new ShadowOperation(in);
		String str2 = shdOpT1.toString();
		System.out.println(str2);
		
		assert(str1.equals(str2));*/
	}

}
