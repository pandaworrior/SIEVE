package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.datastructures.controlflowgraph.CFGNode;
import util.crdtlib.dbannotationtypes.dbutil.DataField;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;

public class Condition {
	
	//we assume that is binary expression
	//Expression
	Expression condExpr;
	boolean isNegation;
	CondPathOprand leftExpr;
	Operator op;
	CondPathOprand rightExpr;
	
	String txnName;
	
	//cfg
	List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList;
	
	//argv
	
	//predicate
	
	//state and argv
	public Condition(Expression expr, boolean negative,
			List<CFGNode<CodeNodeIdentifier, Expression>> preList, String _txnName) {
		this.condExpr = expr;
		this.isNegation = negative;
		this.txnName = _txnName;
		this.precedingNodeList = new ArrayList<CFGNode<CodeNodeIdentifier, Expression>>();
		this.precedingNodeList.addAll(preList);
		if(this.condExpr instanceof BinaryExpr) {
			this.leftExpr = new CondPathOprand(((BinaryExpr)this.condExpr).getLeft(),
					this.precedingNodeList, this.txnName);
			this.rightExpr = new CondPathOprand(((BinaryExpr)this.condExpr).getRight(),
					this.precedingNodeList, this.txnName);
			this.op = ((BinaryExpr)this.condExpr).getOperator();
		}else if(this.condExpr instanceof MethodCallExpr){
			//handle orderby
			this.leftExpr = new CondPathOprand(expr,
					this.precedingNodeList, this.txnName);
		}else{
			throw new RuntimeException("Rightnow, we do not support non-binary conditional expression " + this.condExpr.toString());
		}
	}
	
	public String genSpec(HashMap<String, DataField> aM,
			HashMap<String, SelectQueryRepr> sInfo) {
		String specStr = "(";
		if(this.isNegation) {
			specStr += "Not";
		}
		if(this.condExpr instanceof BinaryExpr) {
			specStr += "(" + this.leftExpr.genOprandSpec(aM,
					sInfo);
			specStr += " " + CommonDef.getBinaryOpName(this.op.toString()) + " ";
			specStr += this.rightExpr.genOprandSpec(aM,
					sInfo) + ")";
		}else if(this.condExpr instanceof MethodCallExpr){
			
			//here we check orderby
			specStr += this.leftExpr.genOprandSpec(aM, sInfo);
			
		}else {
			specStr += this.condExpr.toString();
		}
		specStr += ")";
		return specStr;
	}
	
	public List<String> genArgvSpec(){
		List<String> argSpecs = new ArrayList<String>();
		if(this.condExpr instanceof BinaryExpr) {
			for(String e : this.leftExpr.params) {
				argSpecs.add(CommonDef.indentStr + e + " = argv[\'" + this.txnName + "\'][\'" + e + "\']");
			}
			for(String e : this.rightExpr.params) {
				argSpecs.add(CommonDef.indentStr + e + " = argv[\'" + this.txnName + "\'][\'" + e + "\']");
			}
		}
		return argSpecs;
	}

}
