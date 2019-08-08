package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.List;

import util.crdtlib.dbannotationtypes.dbutil.DataField;
import util.crdtlib.dbannotationtypes.dbutil.DatabaseTable;

/**
 * This is used to generate specs for a table
 * @author cheng
 *
 */

public class TableSpecs {
	
	
	DatabaseTable dbTable;
	
	
	public TableSpecs(DatabaseTable dt) {
		this.dbTable = dt;
	}
	
	private String getAttrZ3Type(DataField df) {
		String z3Type = "";
		if(df.get_Data_Type().contentEquals("INTEGER") || 
				df.get_Data_Type().contentEquals("INT") || 
				df.get_Data_Type().contentEquals("BIGINT") || 
				df.get_Data_Type().contentEquals("TIMESTAMP") ||
				df.get_Data_Type().contentEquals("DATE") ||
				df.get_Data_Type().contentEquals("DATETIME")) {
			z3Type = "Table.Type.INT";
		}else if(df.get_Data_Type().contentEquals("VARCHAR") ||
				df.get_Data_Type().contentEquals("TEXT") ||
				df.get_Data_Type().contentEquals("String")) {
			//System.out.println(df.toString());
			z3Type = "Table.Type.STRING";
		}else if(df.get_Data_Type().contentEquals("FLOAT")) {
			z3Type = "Table.Type.REAL";
		}else {
			System.out.println("Undefined data type " + df.get_Data_Type() + " " + df.toString());
			System.exit(-1);
		}
		
		return z3Type;
	}
	
	private List<String> addKeySpecs(){
		List<String> keySpec = new ArrayList<String>();
		for(DataField df : this.dbTable.getPrimaryKeyDataFieldList()) {
			String z3Type = this.getAttrZ3Type(df);
			keySpec.add(this.dbTable.get_Table_Name() + ".addKey(\'" + df.get_Data_Field_Name() 
					+ "\', " + z3Type + ")");
		}
		return keySpec;
	}
	
	private List<String> addAttrSpecs(){
		List<String> attrSpec = new ArrayList<String>();
		for(DataField df : this.dbTable.getDataFieldList()) {
			if(df.is_Primary_Key() || df.get_Data_Field_Name().contains("_SP_clock") ||
					df.get_Data_Field_Name().contains("_SP_ts") ||
					df.get_Data_Field_Name().contains("_SP_del")) {
				continue;
			}
			
			String z3Type = this.getAttrZ3Type(df);
			attrSpec.add(this.dbTable.get_Table_Name() + ".addValue(\'" + df.get_Data_Field_Name() 
					+ "\', " + z3Type + ")");
		}
		return attrSpec;
	}
	
	public List<String> genTabSpecs(){
		List<String> tabSpec = new ArrayList<String>();
		tabSpec.add(this.dbTable.get_Table_Name() + " = Table(\'" + this.dbTable.get_Table_Name() + "\')");
		// keys
		tabSpec.addAll(this.addKeySpecs());
		// attributes
		tabSpec.addAll(this.addAttrSpecs());
		return tabSpec;
	}

}
