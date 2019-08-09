package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to represent a 
 * @author cheng
 *
 */

public class UpdateQueryRepr {
	
	net.sf.jsqlparser.statement.Statement sqlStmt;
	
	String tableName;
	
	String txnName;
	
	List<FieldRepr> modifiedFields;
	
	List<FieldRepr> primaryKeyFields;
	
	public UpdateQueryRepr(net.sf.jsqlparser.statement.Statement stmt,
			String tbName, String _txnName) {
		this.sqlStmt = stmt;
		this.tableName = tbName;
		this.txnName = _txnName;
		this.modifiedFields = new ArrayList<FieldRepr>();
		this.primaryKeyFields = new ArrayList<FieldRepr>();
	}
	
	public void addOneModifiedField(FieldRepr fR) {
		this.modifiedFields.add(fR);
	}
	
	public void addOnePrimaryKeyField(FieldRepr fR) {
		this.primaryKeyFields.add(fR);
	}
	
	private List<String> genArgvSpecs(){
		List<String> specs = new ArrayList<String>();
		//iterate params required for modifiedFields
		for(FieldRepr fR : this.modifiedFields) {
			for(String param : fR.params) {
				specs.add(param + " = argv[\'" + this.txnName + "\'][\'" + param + "\']");
			}
		}
		//iterate params required for primarykeyFields
		for(FieldRepr pkFR : this.primaryKeyFields) {
			for(String param : pkFR.params) {
				specs.add(param + " = argv[\'" + this.txnName + "\'][\'" + param + "\']");
			}
		}
		return specs;
	}
	
	/**
	 * Read from database
	 * @return
	 */
	private List<String> genFetchSpecs(){
		List<String> specs = new ArrayList<String>();
		//TODO: please complete this
		return specs;
	}
	
	/**
	 * Specs for performing computations over params and states from db
	 * such as balance = balance + amount
	 * @return
	 */
	private List<String> genCalSpecs(){
		List<String> specs = new ArrayList<String>();
		//TODO: please complete this
		return specs;
	}
	
	/**
	 * Specs for storing new data back to database
	 * @return
	 */
	private List<String> genStoreSpecs(){
		List<String> specs = new ArrayList<String>();
		//TODO: please complete this
		return specs;
	}
	
	public List<String> genUpdateQuerySpecs(){
		List<String> specs = new ArrayList<String>();
		
		//the following is used for update statement
		specs.addAll(this.genArgvSpecs());
		specs.addAll(this.genFetchSpecs());
		specs.addAll(this.genCalSpecs());
		specs.addAll(this.genStoreSpecs());
		return specs;
	}

}
