/**
 * 
 */
package staticanalysis.z3codegen;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import japa.parser.ast.expr.Expression;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.update.Update;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.datastructures.controlflowgraph.CFGGraph;
import staticanalysis.templatecreator.TemplateCreator;
import staticanalysis.templatecreator.template.ShadowOperationTemplate;
import util.annotationparser.SchemaParser;
import util.crdtlib.dbannotationtypes.dbutil.DataField;

/**
 * @author cheng
 *
 */
public class ShadowOp {
	
	int opIndex;
	
	/** Conditions*/
	PathCondition pathCond;
	List<String> states;
	List<String> argvs;
	CFGGraph<CodeNodeIdentifier, Expression> cfg;
	static TemplateCreator tmpCreator;
	ShadowOperationTemplate shdOpTemp;
	SchemaParser spParser;
	
	
	private String codeGenForCondition() {
		String codeStr = pathCond.codeGenforPathCondition();
		return codeStr;
	}
	
	// get Condition Expression from the path
	// please extend the template creator
	private void getAllConditions() {
		
	}
	
	private String codeGenForOp() {
		// TODO:
		String codeStr = CodeGenerator.indentStr + "def sop" + this.opIndex + "(self, state, argv):\n";
		// create the shadow operation template first
		this.shdOpTemp = tmpCreator.createShadowOpTemplate(this.cfg);
		
		if(this.shdOpTemp == null) {
			codeStr +=  CodeGenerator.indentStr +  CodeGenerator.indentStr + "return state\n";
			return codeStr;
		}
	    // follow the legacy code to generate new code
		List<String> uQrys = this.shdOpTemp.getAllUpdateQueries();
		HashMap<String, Integer> tableUsedMap = new HashMap<String, Integer>();
		if(uQrys.size() == 0)
		{
			codeStr += CodeGenerator.indentStr + CodeGenerator.indentStr + "This is an no-op and please remove\n";
		}else {
			for(int i = 0; i < uQrys.size() ; i++)
			{
				String uQuery = uQrys.get(i);
				try {
					net.sf.jsqlparser.statement.Statement sqlStmt = QueryParser.cJsqlParser.parse(new StringReader(uQuery));
					Update uStmt = (Update) sqlStmt;
					String tableName = uStmt.getTable().getName();
					Integer usedTimes = tableUsedMap.get(tableName);
					if(usedTimes == null) {
						codeStr += SqlQuery.codeGenForUpdateStmt(uStmt, 0);
						tableUsedMap.put(tableName, 1);
					}else {
						codeStr += SqlQuery.codeGenForUpdateStmt(uStmt, usedTimes.intValue());
						tableUsedMap.put(tableName, usedTimes.intValue() + 1);
					}
					
				} catch (JSQLParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// here we have to generate the return part
			String returnStr = CodeGenerator.indentStr + CodeGenerator.indentStr + "state_0 = [";
			List<String> tableList = this.spParser.getTableList();
			for(int i = 0; i < tableList.size(); i++)
			{
				String tableName = tableList.get(i);
				if(tableUsedMap.containsKey(tableName)) {
					returnStr += SqlQuery.tablePrefix + tableName + "_" + tableUsedMap.get(tableName).intValue() + ", "; 
				}else {
					returnStr += SqlQuery.tablePrefix + tableName + ", ";
				}
			}
			if(returnStr.endsWith(", ")){
				returnStr = returnStr.substring(0, returnStr.length() - 2) + "]\n";
							
			}
			returnStr += CodeGenerator.indentStr + CodeGenerator.indentStr + "return state_0\n";

			codeStr += returnStr;
		}
		return codeStr;
	}
	
	public String codeGenForShadowOp() {
		String codeStr = this.codeGenForCondition();
		codeStr += "\n" + this.codeGenForOp() + "\n";
		return codeStr;
	}

	/**
	 * 
	 */
	public ShadowOp(int index, CFGGraph<CodeNodeIdentifier, Expression> _cfg, SchemaParser _sp) {
		// TODO Auto-generated constructor stub
		this.opIndex = index;
		this.cfg = _cfg;
		this.pathCond = new PathCondition(this.opIndex);
		if(tmpCreator == null)
		{
			tmpCreator = new TemplateCreator();
		}
		this.spParser = _sp;
	}

}
