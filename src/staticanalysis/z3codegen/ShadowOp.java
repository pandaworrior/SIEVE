/**
 * 
 */
package staticanalysis.z3codegen;

import java.util.List;

import japa.parser.ast.expr.Expression;
import staticanalysis.codeparser.CodeNodeIdentifier;
import staticanalysis.datastructures.controlflowgraph.CFGGraph;
import staticanalysis.templatecreator.TemplateCreator;

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
	
	
	private String codeGenForCondition() {
		String codeStr = pathCond.codeGenforPathCondition();
		return codeStr;
	}
	
	// get Condition Expression from the path
	// please extend the template creator
	private void getAllConditions() {
		
	}
	
	private List<String> getAllUpdateQueries() {
		return tmpCreator.findAllSqlUpdatingStatement(this.cfg);
	}
	
	private String codeGenForOp() {
		// TODO:
		String codeStr = CodeGenerator.indentStr + "def sop" + this.opIndex + "(self, state, argv):\n";
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
	public ShadowOp(int index, CFGGraph<CodeNodeIdentifier, Expression> _cfg) {
		// TODO Auto-generated constructor stub
		this.opIndex = index;
		this.cfg = _cfg;
		this.pathCond = new PathCondition(this.opIndex);
		if(tmpCreator == null)
		{
			tmpCreator = new TemplateCreator();
		}
	}

}
