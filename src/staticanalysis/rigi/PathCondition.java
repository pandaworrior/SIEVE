package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.crdtlib.dbannotationtypes.dbutil.DataField;


public class PathCondition {
	
	List<Condition> condList;

	
	public PathCondition() {
		this.condList = new ArrayList<Condition>();
	}
	
	public void addPathCondition(Condition cond) {
		this.condList.add(cond);
	}
	
	public List<String> genPathCondSpec(int pathIndex,
			HashMap<String, DataField> aM,
			HashMap<String, SelectQueryRepr> sInfo){
		List<String> condSpecs = new ArrayList<String>();
		
		String defStr = "def cond" + pathIndex + CommonDef.funcParamStr;
		condSpecs.add(defStr);
		
		
		String returnStr = CommonDef.indentStr + "return ";
		if(this.condList.isEmpty()) {
			returnStr += "True";
		}else {
			returnStr += "And(";
			for(Condition cond : this.condList) {
				
				returnStr += cond.genSpec(aM, sInfo) + ",";
				
				//argument get from condition
				condSpecs.addAll(cond.genArgvSpec());
			}
			
			if(returnStr.endsWith(",")) {
				returnStr = returnStr.substring(0, returnStr.length() - 1);
			}
			returnStr += ")";
		}
		condSpecs.add(returnStr);
		return condSpecs;
	}

}
