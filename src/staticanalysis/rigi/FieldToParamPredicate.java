package staticanalysis.rigi;

import util.crdtlib.dbannotationtypes.dbutil.DataField;

public class FieldToParamPredicate extends Predicate{
	
	DataField df;
	
	String paramStr;
	
	int questionMarkIndex;
	
	String expressionStr;
	
	private String getOperationStr() {
		System.out.println("Predicate expression str: " + this.expressionStr);
		return CommonDef.getSQLBinaryOperator(this.expressionStr);
	}
	
	public FieldToParamPredicate(DataField _df,
			String _paramStr, int _index, String _exprStr) {
		this.df = _df;
		this.paramStr = _paramStr;
		this.questionMarkIndex = _index;
		this.expressionStr = _exprStr;
	}

	/**
	 * _str: primary key string
	 */
	@Override
	String genPredicateSpec(String _str) {
		String predStr = _str;
		predStr += ", \'" + df.get_Data_Field_Name() + "\')";
		predStr += this.getOperationStr();
		predStr += " " + this.paramStr;
		return predStr;
	}

}
