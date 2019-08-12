package staticanalysis.rigi;

import util.crdtlib.dbannotationtypes.dbutil.DataField;

public class FieldToFieldPredicate extends Predicate{
	DataField leftDf;
	DataField rightDf;
	
	String expressionStr;
	
	public FieldToFieldPredicate(DataField df1, DataField df2, String _str) {
		this.leftDf = df1;
		this.rightDf = df2;
		this.expressionStr = _str;
	}

	@Override
	String genPredicateSpec(String _str) {
		// TODO Auto-generated method stub
		return "";
	}
}
