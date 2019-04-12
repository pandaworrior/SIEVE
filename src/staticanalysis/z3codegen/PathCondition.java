package staticanalysis.z3codegen;

import java.util.ArrayList;
import java.util.List;

public class PathCondition {
	
	int opIndex;
	
	List<String> states;
	List<String> argvs;
	List<String> conds;
	
	public String codeGenforPathCondition() {
		// TODO:
		String codeStr = CodeGenerator.indentStr + "def cond" + this.opIndex + "(self, state, argv):\n";
		for (int i = 0; i < this.argvs.size(); i++) {
			String argv = this.argvs.get(i);
			codeStr += CodeGenerator.indentStr + CodeGenerator.indentStr;
			codeStr += argv + " = " + "argv[" + Project.getArgvIndex(argv) + "]\n";
		}
		for (int i = 0; i < this.states.size(); i++) {
			String state = this.states.get(i);
			codeStr += CodeGenerator.indentStr + CodeGenerator.indentStr;
			codeStr += state + " = " + "state[" + Project.getStateIndex(state) + "]\n";
		}
		codeStr += CodeGenerator.indentStr + CodeGenerator.indentStr;
		codeStr += "return And(";
		for (int i = 0; i < this.conds.size(); i++) {
			codeStr += this.conds.get(i) + ",";
		}
		if (codeStr.endsWith(", ")) {
			codeStr = codeStr.substring(0, codeStr.length() - 2);
		}
		codeStr += ")\n";
		return codeStr;
	}
	
	
	public PathCondition(int index) {
		this.opIndex = index;
		this.states = new ArrayList<String>();
		this.argvs = new ArrayList<String>();
		this.conds = new ArrayList<String>();
	}

}
