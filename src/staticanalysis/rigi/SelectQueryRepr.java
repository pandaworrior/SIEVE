package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class SelectQueryRepr {
	net.sf.jsqlparser.statement.Statement sqlStmt;
	
	String txnName;
	
	/** using to search*/
	List<FieldRepr> keyFields;
	
	/** predicates*/
	List<Predicate> predicates;
	
	public SelectQueryRepr(net.sf.jsqlparser.statement.Statement stmt,
			String _txnName) {
		this.sqlStmt = stmt;
		this.txnName = _txnName;
		this.keyFields = new ArrayList<FieldRepr>();
		this.predicates = new ArrayList<Predicate>();
	}
	
	public void addOnePredict(Predicate _pred) {
		this.predicates.add(_pred);
	}
	
	public void addOneKeyField(FieldRepr fR) {
		this.keyFields.add(fR);
	}
	
	private boolean isPrimaryKeyMissing() {
		boolean missing = true;
		
		for(FieldRepr fR : this.keyFields) {
			if(fR.df.is_Primary_Key()) {
				missing = false;
			}
		}
		
		return missing;
	}
	
	private List<String> genResultSetSpec() {
		List<String> resultSetSpecs = new ArrayList<String>();
		if(this.isPrimaryKeyMissing()) {
			// TODO: Please complete this
			
		}else {
			resultSetSpecs.add(this.genNotNilSpec());
			
			//first get the primary key string
			String pkStr = this.genPrimaryKeyGetSpec();
					
			//iterate all predicate and get the right form
			
			for(Predicate pred : this.predicates) {
				resultSetSpecs.add(pred.genPredicateSpec(pkStr));
			}
		}
		return resultSetSpecs;
	}
	
	public String genResultSetSpecTogether() {
		String specStr = "";
		List<String> rsSpecs = this.genResultSetSpec();
		if(!rsSpecs.isEmpty()){
			specStr = "And (";
			for(String e : rsSpecs) {
				specStr += e + ",";
			}
			if(specStr.endsWith(",")) {
				specStr = specStr.substring(0, specStr.lastIndexOf(","));
			}
			specStr +=")";
		}
		return specStr;
	}
	
	private String genPrimaryKeyGetSpec() {
		String specStr = "state[\'TABLE_" + ((PlainSelect)((Select)sqlStmt).getSelectBody()).getFromItem().toString() + "\'].get(";
		
		specStr += CommonDef.getPrimaryKeyString(this.keyFields);
		return specStr;
	}
	
	public String genNotNilSpec() {
		//TODO: across table check
		String specStr = "state[\'TABLE_" + ((PlainSelect)((Select)sqlStmt).getSelectBody()).getFromItem().toString() + "\'].notNil(";
		
		specStr += CommonDef.getPrimaryKeyString(this.keyFields);
		specStr += ")";
		return specStr;
	}
	
	/**
	 * get spec for balance = rs.getLong(1);
	 * @param index
	 * @return
	 */
	public String genAttrSpecByIndex(int index) {
		PlainSelect selectStmt = (PlainSelect) ((Select) sqlStmt).getSelectBody();
		
		//getLong(1) starts with 1
		String attrName = selectStmt.getSelectItems().get(index - 1).toString();
		
		//TODO: across table check
		String specStr = "state[\'TABLE_" + selectStmt.getFromItem().toString() + "\'].get(";
		
		specStr += CommonDef.getPrimaryKeyString(this.keyFields);
		specStr += ", \'" + attrName + "\')";
		return specStr;
	}
}
