package staticanalysis.rigi;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.codeparser.ExpressionParser;
import staticanalysis.codeparser.javaparserextend.ExceptionExpr;
import staticanalysis.datastructures.controlflowgraph.CFGGraph;
import staticanalysis.datastructures.controlflowgraph.CFGNode;
import staticanalysis.z3codegen.QueryParser;
import util.annotationparser.SchemaParser;
import util.commonfunc.StringOperations;
import util.crdtlib.dbannotationtypes.dbutil.DataField;
import util.debug.Debug;

/**
 * This class is used to represent a path in a transaction
 **/
public class CodePath {

	CFGGraph<CodeNodeIdentifier, Expression> pathCfg;
	List<String> selectQueries;
	List<String> updateQueries;
	boolean aborted = false;
	
	/** for specs */
	PathCondition pCond;
	
	ReplicationCondition rCond;
	
	List<Axiom> axioms;
	
	/** arguments */
	HashMap<String, DataField> argvsMap;
	
	private boolean isArgvProcessed = false;
	
	/** The DB Schema Parser. */
	SchemaParser dbSchemaParser;

	public CodePath(CFGGraph<CodeNodeIdentifier, Expression> cfg,
			SchemaParser _sp) {
		this.pathCfg = cfg;
		this.selectQueries = new ArrayList<String>();
		this.updateQueries = new ArrayList<String>();
		this.pCond = new PathCondition();
		this.rCond = new ReplicationCondition();
		this.axioms = new ArrayList<Axiom>();
		this.argvsMap = new HashMap<String, DataField>();
		this.dbSchemaParser = _sp;
	}

	private void addOneSelectQuery(String _str) {
		this.selectQueries.add(_str);
	}

	private void addOneUpdateQuery(String _str) {
		this.updateQueries.add(_str);
	}

	/**
	 * Checks if is execute update method call expression.
	 *
	 * @param exp the exp
	 * @return true, if is execute update method call expression
	 */
	private boolean isExecuteUpdateMethodCallExpression(Expression exp) {
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
	
	/**
	 * Checks if is execute query method call expression.
	 *
	 * @param exp the exp
	 * @return true, if is execute query method call expression
	 */
	private boolean isExecuteQueryMethodCallExpression(Expression exp) {
		if (ExpressionParser.isMethodCallExpression(exp)) {
			MethodCallExpr methodCallExpr = (MethodCallExpr) exp;
			if (methodCallExpr.getName().equals("executeQuery")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private boolean isSetParamMethodCallExpression(Expression exp) {
		if (ExpressionParser.isMethodCallExpression(exp)) {
			MethodCallExpr methodCallExpr = (MethodCallExpr) exp;
			if (methodCallExpr.getName().contains("set")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if is abort method call expression.
	 *
	 * @param exp the exp
	 * @return true, if is execute query method call expression
	 */
	private boolean isAbortMethodCallExpression(Expression exp) {
		if (exp instanceof ExceptionExpr) {
			return true;
		} else {
			return false;
		}
	}

	private String assembleStringForBinaryExpr(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			BinaryExpr binExpr) {
		List<Expression> operandList = ExpressionParser.getOperands((BinaryExpr) binExpr);
		String varValueString = "";
		for (Expression operand : operandList) {
			Debug.println("we get an operand: " + operand.toString());
			if (ExpressionParser.isLiteralExpression(operand)) {
				varValueString += StringOperations.trimDoubleQuotesHeadTail(operand.toString());
			} else {
				if (ExpressionParser.isNameExpr(operand)) {
					varValueString += this.assembleStringForNameExpr(precedingNodeList, (NameExpr) operand);
				} else {
					varValueString += "?";
				}
			}
		}
		return StringOperations.trimDoubleQuotesHeadTail(varValueString);
	}

	// find one update/insert/delete statement

	/**
	 * Assemble string for name expr.
	 *
	 * @param precedingNodeList the preceding node list
	 * @param nameExpr          the name expr
	 * @return the string
	 */
	// TODO: currently we only support = or +=
	private String assembleStringForNameExpr(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
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
							return this.assembleStringForBinaryExpr(precedingNodeList.subList(0, index),
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
							return this.assembleStringForBinaryExpr(precedingNodeList.subList(0, index),
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

	/**
	 * Gets the name expr.
	 *
	 * @param scope the scope
	 * @return the name expr
	 */
	private NameExpr getNameExpr(Expression scope) {
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
	private String getSqlStatementFromPrepareStatement(
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
						return this.getSqlStringFromMethodCallExpr(precedingNodeList.subList(0, index), preDef);
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
								return this.getSqlStringFromMethodCallExpr(precedingNodeList.subList(0, index),
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
	private String getSqlStringFromMethodCallExpr(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			MethodCallExpr methodCExpr) {
		List<Expression> args = methodCExpr.getArgs();
		assert (args != null);
		assert (args.size() == 1);
		Expression arg = args.get(0);
		if (arg instanceof NameExpr) {
			return this.assembleStringForNameExpr(precedingNodeList, (NameExpr) arg);
		} else {
			if (arg instanceof BinaryExpr) {
				return this.assembleStringForBinaryExpr(precedingNodeList, (BinaryExpr) arg);
			} else {
				return arg.toString();
			}
		}
	}

	/**
	 * Find sql updating statement.
	 *
	 * @param precedingNodeList the preceding node list
	 * @param cfgNode           the cfg node
	 * @return the string
	 */
	private String findSqlStatementFromContext(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			CFGNode<CodeNodeIdentifier, Expression> cfgNode) {
		// get the argument from the argument of the executeUpdate function
		MethodCallExpr methodCallExpr = (MethodCallExpr) cfgNode.getNodeData();
		List<Expression> args = methodCallExpr.getArgs();
		
		String sqlStr = "";
		/*
		 * Fork into two branches: one for connection.execute() one for
		 * preparestatement.executeUpdate()
		 */
		if (args != null) {
			assert (args.size() == 1);
			Expression argExpr = args.get(0);
			// if this argument is a string, then please return this string
			if (ExpressionParser.isStringLiteralExpression(argExpr)) {
				sqlStr = argExpr.toString();
			} else if (ExpressionParser.isNameExpr(argExpr)) {
				// if the argument is a variable, please find it along the path back the start
				// of the function
				// * find the assignment expression
				sqlStr = this.assembleStringForNameExpr(precedingNodeList, (NameExpr) argExpr);
			} else {
				System.err.println("This method has not been implemented!");
				return null;
			}
		} else {
			Expression scope = methodCallExpr.getScope();
			NameExpr namExpr = this.getNameExpr(scope);
			// find create preparestatement for this name expression
			sqlStr = this.getSqlStatementFromPrepareStatement(precedingNodeList, namExpr);
		}
		
		sqlStr = CommonDef.trimQuotes(sqlStr);
		System.out.println("Trim after " + sqlStr);
		return sqlStr;
	}

	/**
	 * Find all sql statements and aborts
	 */
	public void findAllSqlStatmentsAndAborts() {
		System.out.println("------------>Analyze Path Starts------------>");
		List<CFGNode<CodeNodeIdentifier, Expression>> nodeList = this.pathCfg.getNodeListViaBFS();
		List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList = new ArrayList<CFGNode<CodeNodeIdentifier, Expression>>();
		for (CFGNode<CodeNodeIdentifier, Expression> cfgNode : nodeList) {
			precedingNodeList.add(cfgNode);
			Expression expr = cfgNode.getNodeData();
			
			
			if(expr == null) {
				System.out.println("Expr: null probably it is return");
			}else {
				if(cfgNode.isCondExpr()) {
					if(cfgNode.isIfPath()) {
						System.out.println("Branch boolean expr: " + expr.toString());
					}else {
						System.out.println("Branch boolean expr: not (" + expr.toString() + ")");
					}
				}else {
					System.out.println("Expr: " + expr.toString());
				}
			}
			if (this.isExecuteUpdateMethodCallExpression(expr)) {
				Debug.println("Expr: " + expr.toString());
				String e = this.findSqlStatementFromContext(precedingNodeList, cfgNode);
				if (e != null) {
					//System.out.println("I found a string: " + e);
					this.addOneUpdateQuery(e);
				}
			}else if(this.isExecuteQueryMethodCallExpression(expr)) {
				Debug.println("Expr: " + expr.toString());
				String e = this.findSqlStatementFromContext(precedingNodeList, cfgNode);
				if (e != null) {
					//System.out.println("I found a string: " + e);
					this.addOneSelectQuery(e);
				}
			}else if(this.isAbortMethodCallExpression(expr)) {
				//System.out.println("I found an abort or exception string: ");
				this.aborted = true;
				break;
			}
		}
		System.out.println("<------------Analyze Path Ends<------------");
	}
	
	private List<String> getParamInQueriesByIndex(List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList,
			int paramIndex) {
		List<String> paramStrs = new ArrayList<String>();
		for(int i = precedingNodeList.size() - 1; i >= 0; i--) {
			Expression expr = precedingNodeList.get(i).getNodeData();
			if(this.isSetParamMethodCallExpression(expr)) {
				System.out.println("set " + expr.toString());
				List<String> argsForExpr = CommonDef.getParamStrsFromExpr(expr);
				if(argsForExpr != null && argsForExpr.size() > 1) {
					int index = Integer.valueOf(argsForExpr.get(0));
					if(index == paramIndex) {
						for(int j = 1; j < argsForExpr.size(); j++) {
							paramStrs.add(argsForExpr.get(j));
						}
						break;
					}
				}
			}
		}
		
		return paramStrs;
	}
	
	/**
	 * process select query
	 * - get all parameters and add into the argv hashmap
	 * @param sqlQuery
	 * @param precedingNodeList
	 */
	private void processSelectQuery(String sqlQuery, 
			List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList) {
		try {
			//first parse the select query
			net.sf.jsqlparser.statement.Statement sqlStmt = Z3CodeGenerator.cJsqlParser.parse(new StringReader(sqlQuery));
		
			PlainSelect selectStmt = (PlainSelect) ((Select) sqlStmt).getSelectBody();

			//get the primary key and question mark
			String whereClauseStr = selectStmt.getWhere().toString();
			
			String[] subExpressionStrs = null;
			if(whereClauseStr.contains("AND")) {
				subExpressionStrs = whereClauseStr.split("AND");
			}else if(whereClauseStr.contains("and")) {
				subExpressionStrs = whereClauseStr.split("and");
			}else {
				subExpressionStrs = new String[1];
				subExpressionStrs[0] = whereClauseStr;
			}
			
			// get question mark
			List<String> questionMarkStrs = new ArrayList<String>();
			for (int i = 0; i < subExpressionStrs.length; i++) {
				if (subExpressionStrs[i].contains("?")) {
					String tempStr = subExpressionStrs[i].substring(0, subExpressionStrs[i].lastIndexOf('='));
					questionMarkStrs.add(tempStr.trim());
				}
			}

			// get back from the precedingNodeList
			for (int i = 0; i < questionMarkStrs.size(); i++) {
				List<String> paramStrs = this.getParamInQueriesByIndex(precedingNodeList, i + 1);

				// find datafield
				DataField df = this.dbSchemaParser.getDataFieldByName(questionMarkStrs.get(i));

				for(String paramStr : paramStrs) {
					this.argvsMap.put(paramStr, df);
				}
			}
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * process update statement
	 * @param uStmt
	 * @param precedingNodeList
	 */
	private void processUpdateStmt(Update updateStmt, 
			List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList) {
		String tableName = updateStmt.getTable().getName();
		
		//get whereclause
		String whereClauseStr = updateStmt.getWhere().toString();
		String[] subExpressionStrs = null;
		if(whereClauseStr.contains("AND")) {
			subExpressionStrs = whereClauseStr.split("AND");
		}else if(whereClauseStr.contains("and")){
			subExpressionStrs = whereClauseStr.split("and");
		}else {
			subExpressionStrs = new String[1];
			subExpressionStrs[0] = whereClauseStr;
		}
		
		//get update column
		Iterator colIt = updateStmt.getColumns().iterator();
		Iterator valueIt = updateStmt.getExpressions().iterator();
		int paramIndex = 0;
		while(colIt.hasNext() && valueIt.hasNext()) {
			String columnName = colIt.next().toString();
			String valueStr = valueIt.next().toString();
			List<String> paramStrs = this.getParamInQueriesByIndex(precedingNodeList, paramIndex + 1);
			
			//need to process paramStr to get right str from -1 * price
			
			//find datafield
			DataField df = this.dbSchemaParser.getTableByName(tableName).get_Data_Field(columnName);
			for(String paramStr : paramStrs) {
				this.argvsMap.put(paramStr, df);
			}
			paramIndex++;
		}
		
		// get question mark
		List<String> questionMarkStrs = new ArrayList<String>();
		for (int i = 0; i < subExpressionStrs.length; i++) {
			if (subExpressionStrs[i].contains("?")) {
				String tempStr = subExpressionStrs[i].substring(0, subExpressionStrs[i].lastIndexOf('='));
				questionMarkStrs.add(tempStr.trim());
			}
		}

		// get back from the precedingNodeList
		for (int i = 0; i < questionMarkStrs.size(); i++) {
			List<String> paramStrs = this.getParamInQueriesByIndex(precedingNodeList, paramIndex + 1);

			// find datafield
			DataField df = this.dbSchemaParser.getTableByName(tableName).get_Data_Field(questionMarkStrs.get(i));

			for(String paramStr : paramStrs) {
				this.argvsMap.put(paramStr, df);
			}
			
			paramIndex++;
		}
		
	}
	
	private void processInsertStmt(Insert inStmt, 
			List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList) {
		String tableName = inStmt.getTable().getName();
		
		Iterator colIt = inStmt.getColumns().iterator();
		int paramIndex = 0;
		while(colIt.hasNext()) {
			String columnName = colIt.next().toString();
			List<String> paramStrs = this.getParamInQueriesByIndex(precedingNodeList, paramIndex + 1);
			
			//find datafield
			DataField df = this.dbSchemaParser.getTableByName(tableName).get_Data_Field(columnName);
			for(String paramStr : paramStrs) {
				this.argvsMap.put(paramStr, df);
			}
			paramIndex++;
		}
	}
	
	private void processDeleteStmt(Delete deStmt, 
			List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList) {
		String tableName = deStmt.getTable().getName();
		
		//get the primary key and question mark
		String whereClauseStr = deStmt.getWhere().toString();
		
		String[] subExpressionStrs = null;
		if(whereClauseStr.contains("AND")) {
			subExpressionStrs = whereClauseStr.split("AND");
		}else if(whereClauseStr.contains("and")) {
			subExpressionStrs = whereClauseStr.split("and");
		}else {
			subExpressionStrs = new String[1];
			subExpressionStrs[0] = whereClauseStr;
		}
		
		// get question mark
		List<String> questionMarkStrs = new ArrayList<String>();
		for (int i = 0; i < subExpressionStrs.length; i++) {
			if (subExpressionStrs[i].contains("?")) {
				String tempStr = subExpressionStrs[i].substring(0, subExpressionStrs[i].lastIndexOf('='));
				questionMarkStrs.add(tempStr.trim());
			}
		}

		// get back from the precedingNodeList
		for (int i = 0; i < questionMarkStrs.size(); i++) {
			List<String> paramStrs = this.getParamInQueriesByIndex(precedingNodeList, i + 1);

			// find datafield
			DataField df = this.dbSchemaParser.getTableByName(tableName).get_Data_Field(questionMarkStrs.get(i));

			for(String paramStr : paramStrs) {
				this.argvsMap.put(paramStr, df);
			}
		}
		
	}
	
	/**
	 * process update query such as insert, update, delete
	 * - get all parameters and add into the argv hashmap
	 * @param sqlQuery
	 * @param precedingNodeList
	 */
	private void processUpdatingQuery(String sqlQuery, 
			List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList) {
		//parse the update query
		try {
			net.sf.jsqlparser.statement.Statement sqlStmt = Z3CodeGenerator.cJsqlParser.parse(new StringReader(sqlQuery));
			
			if(sqlStmt instanceof Insert) {
				this.processInsertStmt((Insert) sqlStmt, precedingNodeList);
			}else if(sqlStmt instanceof Update){
				this.processUpdateStmt((Update) sqlStmt, precedingNodeList);
			}else if(sqlStmt instanceof Delete) {
				this.processDeleteStmt((Delete) sqlStmt, precedingNodeList);
			}
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Traverse the code path
	 * - get parameters from select
	 * - get parameters from update
	 *   - consider two cases
	 *     - overwrite
	 *     - binary
	 */
	private void processArgvs() {
		List<CFGNode<CodeNodeIdentifier, Expression>> nodeList = this.pathCfg.getNodeListViaBFS();
		List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList = new ArrayList<CFGNode<CodeNodeIdentifier, Expression>>();
		for (CFGNode<CodeNodeIdentifier, Expression> cfgNode : nodeList) {
			precedingNodeList.add(cfgNode);
			Expression expr = cfgNode.getNodeData();
			
			if(expr != null) {
				if (this.isExecuteUpdateMethodCallExpression(expr)) {
					Debug.println("Expr: " + expr.toString());
					String e = this.findSqlStatementFromContext(precedingNodeList, cfgNode);
					System.out.println("Found : " + e);
					//process this update query
					this.processUpdatingQuery(e, precedingNodeList);
				}else if(this.isExecuteQueryMethodCallExpression(expr)) {
					Debug.println("Expr: " + expr.toString());
					String e = this.findSqlStatementFromContext(precedingNodeList, cfgNode);
					//process this select query
					this.processSelectQuery(e, precedingNodeList);
				}
			}
		}
	}
	
	public HashMap<String, DataField> getArgvMap(){
		
		if(!this.isArgvProcessed) {
			this.processArgvs();
			this.isArgvProcessed = true;
		}
		
		return this.argvsMap;
	}
	
	private List<String> genSideEffectSpecs(int pathIndex) {
		List<String> sEffectSpecs = new ArrayList<String>();
		
		String defStr = "def sop" + pathIndex + CommonDef.funcParamStr;
		sEffectSpecs.add(defStr);
		
		//TODO: translate here
		
		sEffectSpecs.add(CommonDef.indentStr + "return state");
		return sEffectSpecs;
	}
	
	public List<String> genCodePathSpec(int pathIndex){
		List<String> codePathSpecs = new ArrayList<String>();
		codePathSpecs.addAll(this.pCond.genPathCondSpec(pathIndex));
		codePathSpecs.add("\n");
		
		codePathSpecs.addAll(this.rCond.genReplicationCondSpec(pathIndex));
		codePathSpecs.add("\n");
		
		codePathSpecs.addAll(this.genSideEffectSpecs(pathIndex));
		return codePathSpecs;
	}

	public boolean isReadOnly() {
		return (this.aborted || this.updateQueries.isEmpty());
	}
	
	public boolean isAxiomRequired() {
		return !this.axioms.isEmpty();
	}
	
	public void printOut() {
		System.out.println("------------>Code Path------------>");
		for(int i = 0 ; i < this.selectQueries.size(); i++) {
			System.out.println("Select: " + this.selectQueries.get(i));
		}
		for(int i = 0 ; i < this.updateQueries.size(); i++) {
			System.out.println("Update: " + this.updateQueries.get(i));
		}
		System.out.println("<------------Code Path<------------");
	}

}
