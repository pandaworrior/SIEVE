package staticanalysis.rigi;

import java.util.ArrayList;
import java.util.List;

import util.annotationparser.SchemaParser;
import util.crdtlib.dbannotationtypes.dbutil.DatabaseTable;

/**
 * This function is used to create specifications for database
 * @author cheng
 *
 */

public class DatabaseSpec {
	
	public String dbSchemaFile;
	
	/** The DB Schema Parser. */
	SchemaParser dbSchemaParser;
	
	public List<TableSpecs> schemaSpecs;
	
	public DatabaseSpec(String sfPath) {
		this.dbSchemaFile = sfPath;
		this.dbSchemaParser = new SchemaParser(this.dbSchemaFile);
		this.dbSchemaParser.parseAnnotations();
		this.dbSchemaParser.printOut();
		this.schemaSpecs = new ArrayList<TableSpecs>();
		
		for(DatabaseTable dt : this.dbSchemaParser.getAllTableInstances()) {
			this.schemaSpecs.add(new TableSpecs(dt));
		}
	}
	
	private List<String> genSpecsForTabIns(){
		List<String> tabIns = new ArrayList<String>();
		for(DatabaseTable dt : this.dbSchemaParser.getAllTableInstances()) {
			tabIns.add("TABLE_" + dt.get_Table_Name() + " = TableInstance(" + dt.get_Table_Name() + ")");
		}
		return tabIns;
	}
	
	private String genReturnSpecs() {
		String returnSpec = "return {";
		
		for(DatabaseTable dt : this.dbSchemaParser.getAllTableInstances()) {
			returnSpec += "\'TABLE_" + dt.get_Table_Name() + "':TABLE_"+dt.get_Table_Name() + ",";
		}
		
		// replace the last comma with )
		if(returnSpec.endsWith(","))
		{
			returnSpec = returnSpec.substring(0, returnSpec.length() - 1) + "}";
		}
		return returnSpec;
	}
	
	public List<String> genTableSpecs(){
		List<String> tabsSpecs = new ArrayList<String>();
		for(TableSpecs tabSpec : this.schemaSpecs) {
			tabsSpecs.addAll(tabSpec.genTabSpecs());
			tabsSpecs.add("\n");
		}
		
		tabsSpecs.addAll(this.genSpecsForTabIns());
		tabsSpecs.add(this.genReturnSpecs());
		return tabsSpecs;
	}

}
