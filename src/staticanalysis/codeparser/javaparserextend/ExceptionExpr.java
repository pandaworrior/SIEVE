package staticanalysis.codeparser.javaparserextend;

import japa.parser.ast.expr.Expression;
import japa.parser.ast.visitor.GenericVisitor;
import japa.parser.ast.visitor.VoidVisitor;

public final class ExceptionExpr extends Expression{
	
	public Expression expr;
	
	public ExceptionExpr(int beginLine, int beginColumn, int endLine, int endColumn, Expression e) {
		super(beginLine, beginColumn, endLine, endColumn);
		this.expr = e;
	}

	@Override
	public <R, A> R accept(GenericVisitor<R, A> arg0, A arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A> void accept(VoidVisitor<A> arg0, A arg1) {
		// TODO Auto-generated method stub
		
	}

}
