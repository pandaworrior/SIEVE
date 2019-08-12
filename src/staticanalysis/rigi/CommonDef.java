package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.type.Type;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.codeparser.ExpressionParser;
import staticanalysis.datastructures.controlflowgraph.CFGNode;
import util.commonfunc.StringOperations;
import util.crdtlib.dbannotationtypes.dbutil.DataField;
import util.debug.Debug;

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
	
	static String getArgvBuilderType(Type type) {
		
		String z3Type = "";
		if(type.toString().contentEquals("long") || 
				type.toString().contentEquals("int")) {
			z3Type = "ArgvBuilder.Type.INT";
		}else if(type.toString().contentEquals("String")) {
			//System.out.println(df.toString());
			z3Type = "ArgvBuilder.Type.STRING";
		}else if(type.toString().contentEquals("float") ||
				type.toString().contains("double")) {
			z3Type = "ArgvBuilder.Type.REAL";
		}else {
			System.out.println("Undefined data type " + type.toString());
			System.exit(-1);
		}
		return z3Type;
	}
	
	static String trimQuotes(String _str) {
		//System.out.println("Trim before " + _str);
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
	static List<String> getParamStrsFromExpr(Expression expr, FieldRepr fR) {
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
				
				if(fR != null) {
					fR.setExpression(binaryExpr);
				}
			}else {
				oprStrs.add(e.toString());
			}
		}
		return oprStrs;
	}
	
	static String getPrimaryKeyString(List<FieldRepr> pkFPs) {
		String specStr = "{";
		for(FieldRepr pk : pkFPs) {
			if(pk.df.is_Primary_Key()) {
				specStr += "\'" + pk.df.get_Data_Field_Name() + "\' : " + pk.params.get(0) + ",";
			}
		}
		
		if(specStr.endsWith(",")) {
			specStr = specStr.substring(0, specStr.length() - 1);
		}
		specStr += "}";
		return specStr;
	}
	
	static String getModifiedKeyString(List<FieldRepr> fRs) {
		String specStr = "{";
		
		for(FieldRepr fR : fRs) {
			specStr += "\'" + fR.df.get_Data_Field_Name() + "\' : " + fR.df.get_Data_Field_Name() + ",";
		}
		
		if(specStr.endsWith(",")) {
			specStr = specStr.substring(0, specStr.length() - 1);
		}
		specStr += "}";
		return specStr;
	}
	
	static boolean isDBCal(String dbExprStr) {
		if(dbExprStr.contains("+") ||
				dbExprStr.contains("-") || 
				dbExprStr.contains("*") ||
				dbExprStr.contains("/")) {
			return true;
		}
		return false;
	}
	
	static String getDBCalSpec(FieldRepr fR) {
		String returnStr = fR.df.get_Data_Field_Name() + " = " + fR.df.get_Data_Field_Name() ;
		if(fR.dbExprStr.contains("+")) {
			returnStr += " + ";
		}else if(fR.dbExprStr.contains("-")){
			returnStr += " - ";
		}else if(fR.dbExprStr.contains("*")){
			returnStr += " * ";
		}else if(fR.dbExprStr.contains("/")){
			returnStr += " / ";
		}
		returnStr += fR.params.get(0);
		return returnStr;
	}
	
	static boolean isConditionExpression(Expression exp) {
		if (ExpressionParser.isMethodCallExpression(exp)) {
			MethodCallExpr methodCallExpr = (MethodCallExpr) exp;
			if (methodCallExpr.getName().equals("executeUpdate")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	static String getBinaryOpName(String _str) {
		if(_str.contentEquals("equals")) {
			return "==";
		}else if(_str.contentEquals("less")) {
			return "<";
		}else if(_str.contentEquals("greaterEquals")) {
			return ">=";
		}else if(_str.contentEquals("notEquals")){
			return "!=";
		}else if(_str.contentEquals("minus")){
			return "-";
		}else if (_str.contentEquals("or")) {
			return "Or";
		}else if (_str.contentEquals("greater")) {
			return ">";
		}else if(_str.contentEquals("lessEquals")){
			return "<=";
		}else {
			System.out.println("Not support yet " + _str);
			System.exit(-1);
		}
		return null;
	}
	
	static String assembleStringForBinaryExpr(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			BinaryExpr binExpr) {
		List<Expression> operandList = ExpressionParser.getOperands((BinaryExpr) binExpr);
		String varValueString = "";
		for (Expression operand : operandList) {
			Debug.println("we get an operand: " + operand.toString());
			if (ExpressionParser.isLiteralExpression(operand)) {
				varValueString += StringOperations.trimDoubleQuotesHeadTail(operand.toString());
			} else {
				if (ExpressionParser.isNameExpr(operand)) {
					varValueString += assembleStringForNameExpr(precedingNodeList, (NameExpr) operand);
				} else {
					varValueString += "?";
				}
			}
		}
		return StringOperations.trimDoubleQuotesHeadTail(varValueString);
	}
	
	static String assembleStringForNameExpr(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			NameExpr nameExpr) {
		Debug.println("Hello, you have a variable: " + nameExpr.getName());
		int index = precedingNodeList.size() - 1;
		while (index >= 0) {
			Expression expr = precedingNodeList.get(index).getNodeData();
			Debug.println("currently we work with expr: " + expr.toString());
			if (ExpressionParser.isAssignmentExpr(expr)) {
				// if the leftside is nameExpr, then check the right side is the string literal
				// or some literal , return
				AssignExpr assignExpr = (AssignExpr) expr;
				assert (assignExpr.getOperator() == AssignExpr.Operator.assign);
				Expression target = assignExpr.getTarget();
				Expression value = assignExpr.getValue();
				Debug.println("target: " + target.toString());
				Debug.println("value: " + value.toString());
				// if the right side is not a string literal, then it must be a plus operator
				if (target.toString().equals(nameExpr.getName())) {
					Debug.println("the assignment target is the one we want: " + target.toString());
					if (ExpressionParser.isLiteralExpression(value)) {
						return StringOperations.trimDoubleQuotesHeadTail(value.toString());
					} else {
						// it must be a binaryExpr
						if (ExpressionParser.isBinaryExpr(value)) {
							return assembleStringForBinaryExpr(precedingNodeList.subList(0, index),
									(BinaryExpr) value);
						} else {
							return "?";
						}
					}
				}
			} else if (ExpressionParser.isVariableDeclarationExpr(expr)) {
				// if it has initializer, then check whether it is string or not
				VariableDeclarationExpr varDeclExpr = (VariableDeclarationExpr) expr;
				// get the name of the variable
				Expression targetVarExpr = ExpressionParser.getMatchedVarDeclaration(varDeclExpr, nameExpr.getName());
				// if it doesn't have initializer return ""
				if (targetVarExpr != null) {
					// if the targetVarExpr is literal then return;
					if (ExpressionParser.isLiteralExpression(targetVarExpr)) {
						return StringOperations.trimDoubleQuotesHeadTail(targetVarExpr.toString());
					} else {
						// it must be a binaryExpr
						if (ExpressionParser.isBinaryExpr(targetVarExpr)) {
							return assembleStringForBinaryExpr(precedingNodeList.subList(0, index),
									(BinaryExpr) targetVarExpr);
						} else {
							return "?";
						}
					}
				}
			}
			index--;
		}
		return "";
	}
	
	static NameExpr findPrepareStmtNameExpr(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			NameExpr resExpr) {
		int index = precedingNodeList.size() - 1;
		while (index >= 0) {
			Expression expr = precedingNodeList.get(index).getNodeData();
			if (ExpressionParser.isAssignmentExpr(expr)) {
				AssignExpr asExp = (AssignExpr) expr;
				Expression target = asExp.getTarget();
				if (target instanceof NameExpr) {
					if (((NameExpr) target).getName().equals(resExpr.getName())) {
						// you find this
						Expression val = asExp.getValue();
						if(val instanceof MethodCallExpr) {
							NameExpr callScope = (NameExpr) ((MethodCallExpr) val).getScope();
							return callScope;
						}
					}
				}
			} else if (ExpressionParser.isVariableDeclarationExpr(expr)) {
				VariableDeclarationExpr vdExpr = (VariableDeclarationExpr) expr;
				List<VariableDeclarator> vars = vdExpr.getVars();
				if (vars != null) {
					if (vars.size() == 1) {
						VariableDeclarator varDec = vars.get(0);
						if(varDec.getId().getName().equals(resExpr.getName()))
						{
							Expression val = varDec.getInit();
							if(val instanceof MethodCallExpr) {
								NameExpr callScope = (NameExpr) ((MethodCallExpr) val).getScope();
								return callScope;
							}
						}
					}
				}
			}
			index--;
		}
		return null;
	}
	
	static int getIndexFromGetExpr(Expression expr) {
		List<Expression> args = ((MethodCallExpr)expr).getArgs();
		if(args.size() > 0) {
			Expression firstArg = args.get(0);
			if(firstArg instanceof IntegerLiteralExpr) {
				return Integer.valueOf(firstArg.toString());
			}
		}
		throw new RuntimeException("You cannot find number");
	}
	
	static NameExpr findResultSetExpr(Expression expr) {
		if(expr instanceof MethodCallExpr) {
			return (NameExpr)((MethodCallExpr)expr).getScope();
		}
		throw new RuntimeException("You cannot find resultset str");
	}
	
	/* think about balance -> balance = rs.getDouble(1) return rs.getDouble(1)
	 */
	static Expression findGetIndexExpr(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			String targetAttr) {
		int index = precedingNodeList.size() - 1;
		while (index >= 0) {
			Expression expr = precedingNodeList.get(index).getNodeData();
			if (ExpressionParser.isAssignmentExpr(expr)) {
				AssignExpr asExp = (AssignExpr) expr;
				Expression target = asExp.getTarget();
				if (target instanceof NameExpr) {
					if (((NameExpr) target).getName().equals(targetAttr)) {
						// you find this
						Expression val = asExp.getValue();
						if(val instanceof MethodCallExpr && val.toString().contains("get")) {
							return val;
							/*List<Expression> args = ((MethodCallExpr)val).getArgs();
							if(args.size() > 0) {
								Expression firstArg = args.get(0);
								if(firstArg instanceof IntegerLiteralExpr) {
									return Integer.valueOf(firstArg.toString());
								}
							}*/
						}
					}
				}
			} else if (ExpressionParser.isVariableDeclarationExpr(expr)) {
				VariableDeclarationExpr vdExpr = (VariableDeclarationExpr) expr;
				List<VariableDeclarator> vars = vdExpr.getVars();
				if (vars != null) {
					if (vars.size() == 1) {
						VariableDeclarator varDec = vars.get(0);
						if(varDec.getId().getName().equals(targetAttr))
						{
							Expression val = varDec.getInit();
							if(val instanceof MethodCallExpr) {
								return val;
							}
						}
					}
				}
			}
			index--;
		}
		return null;
	}
	
	/* think about balance -> balance = rs.getDouble(1) -> rs = stmt.execute ->
	 * input is the resultset expr
	 */
	
	static String findSqlStatementFromContextForAttr(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			Expression expr) {
		
		String sqlStr = "";
		
		NameExpr stmtStr = findPrepareStmtNameExpr(precedingNodeList, (NameExpr)expr);
		
		if(stmtStr != null) {
			sqlStr = getSqlStatementFromPrepareStatement(precedingNodeList, stmtStr);
			
			sqlStr = CommonDef.trimQuotes(sqlStr);
			//System.out.println("Trim after " + sqlStr);
		}
		return sqlStr;
	}
	
	// from setlong(1, name) -> name = rs.getInt => rs => rs = stmt.executeQuery => stmt
	static String findSqlStatementFromContextForSetAttr(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			Expression expr) {
		
 		String sqlStr = "";
		
		Expression resStr = findGetIndexExpr(precedingNodeList, expr.toString());
		if(resStr != null) {
			
			MethodCallExpr callExpr = (MethodCallExpr)resStr;
		
			NameExpr stmtStr = findPrepareStmtNameExpr(precedingNodeList, (NameExpr)callExpr.getScope());
			
			if(stmtStr != null) {
				sqlStr = getSqlStatementFromPrepareStatement(precedingNodeList, stmtStr);
				
				sqlStr = CommonDef.trimQuotes(sqlStr);
				//System.out.println("Trim after " + sqlStr);
			}
		}
		return sqlStr;
	}
	
	static String findSqlStatementFromContext(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			Expression expr) {
		
		String sqlStr = "";
		
		/* think about two cases: 
		 * first r0.next() -> r0 = stmt.execute() -> prestmt = "select" 
		 * second balance -> balance = rs.getDouble(1) -> rs = stmt.execute ->
		 */
		MethodCallExpr callExpr = (MethodCallExpr)expr;
		
		//result set
		NameExpr resStr = (NameExpr) callExpr.getScope();
		
		NameExpr stmtStr = findPrepareStmtNameExpr(precedingNodeList, resStr);
		
		sqlStr = getSqlStatementFromPrepareStatement(precedingNodeList, stmtStr);
		
		sqlStr = CommonDef.trimQuotes(sqlStr);
		//System.out.println("Trim after " + sqlStr);
		return sqlStr;
	}
	
	static NameExpr getNameExpr(Expression scope) {
		if (scope instanceof NameExpr) {
			return (NameExpr) scope;
		} else {
			throw new RuntimeException("this expression not implemented yet " + scope.toString());
		}
	}

	/**
	 * Gets the sql statement from prepare statement.
	 *
	 * @param precedingNodeList the preceding node list
	 * @param preStat           the pre stat
	 * @return the sql statement from prepare statement
	 */
	static String getSqlStatementFromPrepareStatement(
			List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList, NameExpr preStat) {
		int index = precedingNodeList.size() - 1;
		while (index >= 0) {
			Expression expr = precedingNodeList.get(index).getNodeData();
			if (ExpressionParser.isAssignmentExpr(expr)) {
				AssignExpr asExp = (AssignExpr) expr;
				Expression target = asExp.getTarget();
				if (target instanceof NameExpr) {
					if (((NameExpr) target).getName().equals(preStat.getName())) {
						// you find this
						Expression value = asExp.getValue();
						MethodCallExpr preDef = (MethodCallExpr) value;
						return getSqlStringFromMethodCallExpr(precedingNodeList.subList(0, index), preDef);
					}
				}
			} else if (ExpressionParser.isVariableDeclarationExpr(expr)) {
				VariableDeclarationExpr vdExpr = (VariableDeclarationExpr) expr;
				List<VariableDeclarator> vars = vdExpr.getVars();
				if (vars != null) {
					if (vars.size() == 1) {
						VariableDeclarator varDec = vars.get(0);
						VariableDeclaratorId varId = varDec.getId();
						if (varId.toString().equals(preStat.getName())) {
							// you find it
							Expression varInit = varDec.getInit();
							if (varInit instanceof MethodCallExpr) {
								MethodCallExpr methodCExpr = (MethodCallExpr) varInit;
								return getSqlStringFromMethodCallExpr(precedingNodeList.subList(0, index),
										methodCExpr);
							}
						}
					}
				}
			}
			index--;
		}
		throw new RuntimeException("You cannot find this sql update statement");
	}

	/**
	 * Gets the sql string from method call expr.
	 *
	 * @param precedingNodeList the preceding node list
	 * @param methodCExpr       the method c expr
	 * @return the sql string from method call expr
	 */
	static String getSqlStringFromMethodCallExpr(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			MethodCallExpr methodCExpr) {
		List<Expression> args = methodCExpr.getArgs();
		assert (args != null);
		assert (args.size() == 1);
		Expression arg = args.get(0);
		if (arg instanceof NameExpr) {
			return assembleStringForNameExpr(precedingNodeList, (NameExpr) arg);
		} else {
			if (arg instanceof BinaryExpr) {
				return assembleStringForBinaryExpr(precedingNodeList, (BinaryExpr) arg);
			} else {
				return arg.toString();
			}
		}
	}
	
	static int lastIndexOfSQLBinaryOperator(String _str) {
		if(_str.contains(">=")) {
			return _str.lastIndexOf(">=");
		}else if(_str.contains("<=")) {
			return _str.lastIndexOf("<=");
		}else if(_str.contains(">")) {
			return _str.lastIndexOf(">");
		}else if(_str.contains("<")) {
			return _str.lastIndexOf("<");
		}else if(_str.contains("=")) {
			return _str.lastIndexOf("=");
		}else {
			throw new RuntimeException("Not such sql binary operator " + _str);
		}
		
	}
	
	static String getSQLBinaryOperator(String _str) {
		if(_str.contains(">=")) {
			return ">=";
		}else if(_str.contains("<=")) {
			return "<=";
		}else if(_str.contains(">")) {
			return ">";
		}else if(_str.contains("<")) {
			return "<";
		}else if(_str.contains("=")) {
			return "==";
		}else {
			throw new RuntimeException("Not such sql binary operator " + _str);
		}
		
	}
	
}
