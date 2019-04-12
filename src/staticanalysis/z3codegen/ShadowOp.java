/**
 * 
 */
package staticanalysis.z3codegen;

import java.util.List;

/**
 * @author cheng
 *
 */
public class ShadowOp {
	
	/** Conditions*/
	List<String> conds;
	List<String> states;
	List<String> argvs;
	
	
	private String codeGenForCondition() {
		// TODO:
		String codeStr = "\n";
		return codeStr;
	}
	
	private String codeGenForOp() {
		// TODO:
		String codeStr = "\n";
		return codeStr;
	}
	
	public String codeGenForShadowOp() {
		String codeStr = this.codeGenForCondition();
		codeStr = "\n" + this.codeGenForOp() + "\n";
		return codeStr;
	}

	/**
	 * 
	 */
	public ShadowOp() {
		// TODO Auto-generated constructor stub
	}

}
