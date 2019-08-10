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
	
	public SelectQueryRepr(net.sf.jsqlparser.statement.Statement stmt,
			String _txnName) {
		this.sqlStmt = stmt;
		this.txnName = _txnName;
		this.keyFields = new ArrayList<FieldRepr>();
	}
	
	public void addOneKeyField(FieldRepr fR) {
		this.keyFields.add(fR);
	}
	
	public String genNotNilSpec() {
		//TODO: across table check
		String specStr = "state[\'TABLE_" + this.keyFields.get(0).df.get_Table_Name() + "\'].notNil(";
		
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
