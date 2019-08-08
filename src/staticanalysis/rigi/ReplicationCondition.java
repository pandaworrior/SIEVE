package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.List;

public class ReplicationCondition {
	
	List<Condition> condList;
	
	public ReplicationCondition() {
		this.condList = new ArrayList<Condition>();
	}
	
	public void addReplicationCondition(Condition cond) {
		this.condList.add(cond);
	}
	
	public List<String> genReplicationCondSpec(){
		List<String> condSpecs = new ArrayList<String>();
		String returnStr = "return ";
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
