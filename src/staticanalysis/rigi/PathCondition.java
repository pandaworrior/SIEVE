package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.List;

public class PathCondition {
	
	List<Condition> condList;
	
	public PathCondition() {
		this.condList = new ArrayList<Condition>();
	}
	
	public void addPathCondition(Condition cond) {
		this.condList.add(cond);
	}
	
	public List<String> genPathCondSpec(int pathIndex){
		List<String> condSpecs = new ArrayList<String>();
		
		String defStr = "def cond" + pathIndex + CommonDef.funcParamStr;
		condSpecs.add(defStr);
		
		String returnStr = CommonDef.indentStr + "return ";
		if(this.condList.isEmpty()) {
			returnStr += "True";
		}else {
			returnStr += "And(";
			for(Condition cond : this.condList) {
				condSpecs.add(cond.genSpec());
				
				//TODO: returnStr and conjuction with predicates
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
