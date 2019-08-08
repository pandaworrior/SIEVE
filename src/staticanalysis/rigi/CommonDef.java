package staticanalysis.rigi;

import util.crdtlib.dbannotationtypes.dbutil.DataField;

public class CommonDef {
	
	static String indentStr = "    ";
	
	static String initFuncStr = "def __init__(self):";
	
	static String funcParamStr = "(self, state, argv):";
	
	
	static String getArgvBuilderType(DataField df) {
		
		String z3Type = "";
		if(df.get_Data_Type().contentEquals("INTEGER") || 
				df.get_Data_Type().contentEquals("INT") || 
				df.get_Data_Type().contentEquals("BIGINT") || 
				df.get_Data_Type().contentEquals("TIMESTAMP") ||
				df.get_Data_Type().contentEquals("DATE") ||
				df.get_Data_Type().contentEquals("DATETIME")) {
			z3Type = "ArgvBuilder.Type.INT";
		}else if(df.get_Data_Type().contentEquals("VARCHAR") ||
				df.get_Data_Type().contentEquals("TEXT") ||
				df.get_Data_Type().contentEquals("String")) {
			//System.out.println(df.toString());
			z3Type = "ArgvBuilder.Type.STRING";
		}else if(df.get_Data_Type().contentEquals("FLOAT")) {
			z3Type = "ArgvBuilder.Type.REAL";
		}else {
			System.out.println("Undefined data type " + df.get_Data_Type() + " " + df.toString());
			System.exit(-1);
		}
		return z3Type;
	}
	
	static String trimQuotes(String _str) {
		return _str.substring(_str.indexOf('\"') + 1, _str.lastIndexOf('\"'));
	}
}
