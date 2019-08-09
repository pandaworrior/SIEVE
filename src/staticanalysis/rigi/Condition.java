package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.datastructures.controlflowgraph.CFGNode;
import japa.parser.ast.expr.Expression;

public class Condition {
	
	//we assume that is binary expression
	//Expression
	Expression condExpr;
	boolean isNegation;
	CondPathOprand leftExpr;
	Operator op;
	CondPathOprand rightExpr;
	
	//cfg
	List<CFGNode<CodeNodeIdentifier, Expression>> precedingNodeList;
	
	//argv
	
	//predicate
	
	//state and argv
	public Condition(Expression expr, boolean negative,
			List<CFGNode<CodeNodeIdentifier, Expression>> preList) {
		this.condExpr = expr;
		this.isNegation = negative;
		this.precedingNodeList = new ArrayList<CFGNode<CodeNodeIdentifier, Expression>>();
		this.precedingNodeList.addAll(preList);
		if(this.condExpr instanceof BinaryExpr) {
			this.leftExpr = new CondPathOprand(((BinaryExpr)this.condExpr).getLeft(),
					this.precedingNodeList);
			this.rightExpr = new CondPathOprand(((BinaryExpr)this.condExpr).getRight(),
					this.precedingNodeList);
			this.op = ((BinaryExpr)this.condExpr).getOperator();
		}else {
			System.out.println("Rightnow, we do not support non-binary conditional expression");
			System.exit(-1);
		}
	}
	
	public String genSpec() {
		String specStr = "(";
		if(this.isNegation) {
			specStr += "Not";
		}
		specStr += "(" + this.leftExpr.genOprandSpec();
		specStr += " " + CommonDef.getBinaryOpName(this.op.toString()) + " ";
		specStr += this.rightExpr.genOprandSpec() + "))";
		return specStr;
	}

}
