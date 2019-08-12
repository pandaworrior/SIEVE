package staticanalysis.rigi;

import util.crdtlib.dbannotationtypes.dbutil.DataField;

public class OrderByPredicate extends Predicate{
	
	DataField df;
	
	boolean isDesc;
	
	String expressionStr;
	
	String orderByAttr;
	
	public OrderByPredicate(DataField _df, String _expr) {
		this.df = _df;
		if(_expr.contains("DESC")) {
			this.isDesc = true;
		}else {
			this.isDesc = false;
		}
		this.expressionStr = _expr;
		this.setOrderByAttr();
	}
	
	public String getRelString() {
		if(this.isDesc) {
			return "RelLessOrEqual()";
		}else {
			throw new RuntimeException("Not support something different from desc");
		}
	}
	
	private void setOrderByAttr() {
		String tempStr = this.expressionStr.substring(this.expressionStr.indexOf("BY") + 2);
		this.orderByAttr = tempStr.substring(0, tempStr.indexOf(" "));
		System.out.println("Set order by str " + this.orderByAttr);
	}

	@Override
	String genPredicateSpec(String _str) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
