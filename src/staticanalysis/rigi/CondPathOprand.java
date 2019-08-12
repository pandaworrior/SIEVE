package staticanalysis.rigi;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.NameExpr;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.datastructures.controlflowgraph.CFGNode;
import util.crdtlib.dbannotationtypes.dbutil.DataField;

public class CondPathOprand {
	
	Expression expr;
	
	String txnName;
	
	//cfg
	List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList;
	
	//argv
	List<String> params;
	
	public CondPathOprand(Expression _expr,
			List<CFGNode<CodeNodeIdentifier, Expression>> preList,
			String _txnName) {
		this.expr = _expr;
		this.precedingNodeList = preList;
		this.txnName = _txnName;
		this.params = new ArrayList<String>();
	}
	
	private boolean isParam(Expression _expr, HashMap<String, DataField> aM) {
		return (aM.get(_expr.toString()) != null);
	}
	
	private boolean isUserInput(Expression _expr) {
		
		//get the codetransactoin instance
		
		CodeTransaction codeTxn = Z3CodeGenerator.txnMap.get(txnName);
		if(codeTxn.userInputs.containsKey(_expr.toString())) {
			return true;
		}else {
			return false;
		}
	}
	
	private String genExprSpec(Expression _expr, HashMap<String, DataField> aM,
			HashMap<String, SelectQueryRepr> sInfo) {
		if(this.isParam(_expr, aM) ||
				this.isUserInput(_expr)) {
			// that is a parameter
			this.params.add(_expr.toString());
			return _expr.toString();
		}else {
			//first get the resultset.get expr
			Expression resGetExpr = CommonDef.findGetIndexExpr(precedingNodeList, _expr.toString());
			int getIndex = CommonDef.getIndexFromGetExpr(resGetExpr);
			NameExpr resSetStr = CommonDef.findResultSetExpr(resGetExpr);
			String sqlQuery = CommonDef.findSqlStatementFromContextForAttr(precedingNodeList, resSetStr);
			System.out.println("find a sql query here " + sqlQuery);
			
			//try to find select record
			SelectQueryRepr selRepr = sInfo.get(sqlQuery);
			//we assume right now it is only a table used
			
			//get all parameters
			for(FieldRepr fR : selRepr.keyFields) {
				this.params.addAll(fR.params);
			}
			
			return selRepr.genAttrSpecByIndex(getIndex);
		}
	}
	
	private String genOrderBySpec(HashMap<String, DataField> aM,
			HashMap<String, SelectQueryRepr> sInfo) {
		String spec = "";
		List<String> argsForExpr = CommonDef.getParamStrsFromExpr(expr, null);
		if(argsForExpr != null && argsForExpr.size() > 1) {
			int index = Integer.valueOf(argsForExpr.get(0));
			String param = argsForExpr.get(1);
			String sqlQuery = CommonDef.findSqlStatementFromContextForSetAttr(precedingNodeList, new NameExpr(0,0,0,0, param));
			
			if(sqlQuery.contentEquals("")) {
				System.out.println("That is a user input value");
			}else {
				if(sqlQuery.contains("ORDER BY")) {
					System.out.println("Handle orderby " + sqlQuery + " " + this.expr.toString() + " " + index);
					SelectQueryRepr selInfo = sInfo.get(sqlQuery);
					net.sf.jsqlparser.statement.Statement sqlStmt = null;
					try {
						Expression getIndexExpr = CommonDef.findGetIndexExpr(this.precedingNodeList, param);
						int getIndex = -1;
						if(getIndexExpr != null) {
							System.out.println("index str " + getIndexExpr.toString());
							getIndex = Integer.valueOf(CommonDef.getParamStrsFromExpr(getIndexExpr, null).get(0));
							sqlStmt = Z3CodeGenerator.cJsqlParser.parse(new StringReader(sqlQuery));
							spec = selInfo.genOrderBySpec(((PlainSelect)(((Select)sqlStmt).getSelectBody())).getSelectItems().get(getIndex - 1).toString(), param);
						}
					} catch (JSQLParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new RuntimeException();
					}
				}
			}
		}
		
		return spec;
	}
	
	public String genOprandSpec(HashMap<String, DataField> aM,
			HashMap<String, SelectQueryRepr> sInfo) {
		if(this.expr.toString().contains("next")) {
			String sqlQuery = CommonDef.findSqlStatementFromContext(this.precedingNodeList, 
						this.expr);
			System.out.println("find a sql query here " + sqlQuery);
				
			//try to find select record
			SelectQueryRepr selRepr = sInfo.get(sqlQuery);
			//we assume right now it is only a table used
			
			//get all parameters
			for(FieldRepr fR : selRepr.keyFields) {
				this.params.addAll(fR.params);
			}
			
			return selRepr.genResultSetSpecTogether();
		}else {
			if(this.expr instanceof BooleanLiteralExpr) {
				if(this.expr.toString().contentEquals("false")) {
					return "False";
				}else {
					return "True";
				}
			}else if(this.expr instanceof BinaryExpr) {
				BinaryExpr bExpr = (BinaryExpr) this.expr;
				// for the following expression we need to check if it is param or shared object
				Expression lExpr = bExpr.getLeft();
				Expression rExpr = bExpr.getRight();
				Operator op = bExpr.getOperator();
				if(op.toString().contentEquals("or")) {
					String specStr = "(" + CommonDef.getBinaryOpName(op.toString());
					specStr += this.genExprSpec(lExpr, aM, sInfo);
					specStr += ", " +  this.genExprSpec(rExpr, aM, sInfo) + ")";
					return specStr;
				}else {
					String specStr = "(" + this.genExprSpec(lExpr, aM, sInfo);
					specStr += " " + CommonDef.getBinaryOpName(op.toString()) + " ";
					specStr += this.genExprSpec(rExpr, aM, sInfo) + ")";
					return specStr;
				}
			}else if(this.expr instanceof NameExpr){
				return this.genExprSpec(this.expr, aM, sInfo);
			}else if(this.expr.toString().contains("set")) {
				
				return this.genOrderBySpec(aM, sInfo);
			}else{
				return this.expr.toString();	
			}
		}
	}

}
