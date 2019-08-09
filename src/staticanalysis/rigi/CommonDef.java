package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
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
		System.out.println("Trim before " + _str);
		if(_str.indexOf('\"') != -1 && _str.lastIndexOf('\"') != -1)
			return _str.substring(_str.indexOf('\"') + 1, _str.lastIndexOf('\"'));
		else
			return _str;
	}
	
	static boolean isNumeric(String strNum) {
	    return strNum.matches("-?\\d+(\\.\\d+)?");
	}
	
	/**
	 * Get param string from Expression
	 * @param expr
	 * @return
	 */
	static List<String> getParamStrsFromExpr(Expression expr) {
		List<String> oprStrs = new ArrayList<String>();
		MethodCallExpr methodCallExp = (MethodCallExpr) expr;
		List<Expression> args = methodCallExp.getArgs();
		for(Expression e : args) {
			if(e instanceof BinaryExpr) {
				BinaryExpr binaryExpr = (BinaryExpr) e;
				Expression leftExpr = binaryExpr.getLeft();
				Expression rightExpr = binaryExpr.getRight();
				if(!isNumeric(leftExpr.toString())) {
					oprStrs.add(leftExpr.toString());
				}
				if(!isNumeric(rightExpr.toString())) {
					oprStrs.add(rightExpr.toString());
				}
			}else {
				oprStrs.add(e.toString());
			}
		}
		return oprStrs;
	}
}
