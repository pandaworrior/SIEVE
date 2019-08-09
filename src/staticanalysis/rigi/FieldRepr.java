package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.Expression;
import util.crdtlib.dbannotationtypes.dbutil.DataField;

/**
 * This class is used to represent a database field during transalation
 * @author cheng
 *
 */
public class FieldRepr {
	
	/** DataField from Schema */
	DataField df = null;
	
	/** Value set by a expression*/
	Expression expr = null;
	
	/** Database expression, just like update set s = s + ?*/
	String dbExprStr = null;
	
	/** list of parameters*/
	List<String> params;

	public FieldRepr() {
		this.params = new ArrayList<String>();
	}
	
	public void setDataField(DataField _df) {
		this.df = _df;
	}
	
	public void setExpression(Expression _expr) {
		this.expr = _expr;
	}
	
	public void setDBExpr(String _dbExprStr) {
		this.dbExprStr = _dbExprStr;
	}
	
	public void addParams(List<String> _params) {
		this.params.addAll(_params);
	}
	
}
