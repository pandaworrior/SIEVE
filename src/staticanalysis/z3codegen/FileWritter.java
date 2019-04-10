/**
 * 
 */
package staticanalysis.z3codegen;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

/**
 * @author cheng
 *
 */
public class FileWritter {
	
	private String filePath;
	
	File codeFile;
	
	String headerContent = "from z3 import *\n" + 
			"\n" + 
			"def gen_id(total = []):\n" + 
			"    m = 0\n" + 
			"    if total:\n" + 
			"        m = total[-1]\n" + 
			"    total.append(m + 1)\n" + 
			"    return str(total[-1])\n" + 
			"\n" + 
			"debug = True\n" + 
			"\n" + 
			"def Implies2(P,Q):\n" + 
			"    return And(Implies(P,Q),Implies(Q,P))\n" + 
			"\n" + 
			"def z3_list_eq(X,Y):\n" + 
			"    X_eq_Y = [ X[i] == Y[i] for i in range(len(X)) ]\n" + 
			"    return simplify(And(X_eq_Y)) ";
	
	String footContent = "##############################################################################################\n" + 
			"\n" + 
			"def Self_Free(f,c,I):\n" + 
			"    state = GenState(\"\")\n" + 
			"    argv = GenArgv(\"\")\n" + 
			"    return ForAll(list(state)+list(argv), Implies(And(I(state),c(state,argv)),I(f(state,argv))))\n" + 
			"\n" + 
			"def State_Free(f1,c1,f2,c2,I):\n" + 
			"    stateA = GenState(\"A\")\n" + 
			"    argvA = GenArgv(\"A\")\n" + 
			"    stateB = GenState(\"B\")\n" + 
			"    argvB = GenArgv(\"B\")\n" + 
			"    state = GenState(\"\")\n" + 
			"    return ForAll(list(stateA)+list(argvA)+list(stateB)+list(argvB)+list(state),\n" + 
			"                Implies(And(\n" + 
			"                    c1(stateA,argvA),c2(stateB,argvB)\n" + 
			"                    #,c1(f2(state,argvB),argvA),c2(f1(state,argvA),argvB)\n" + 
			"                    )\n" + 
			"                ,z3_list_eq(f2(f1(state,argvA),argvB),\n" + 
			"                           f1(f2(state,argvB),argvA))))\n" + 
			"\n" + 
			"\n" + 
			"def State_Free_Inv(f1,c1,f2,c2,I):\n" + 
			"    stateA = GenState(\"A\")\n" + 
			"    argvA = GenArgv(\"A\")\n" + 
			"    stateB = GenState(\"B\")\n" + 
			"    argvB = GenArgv(\"B\")\n" + 
			"    state = GenState(\"\")\n" + 
			"    return ForAll(list(stateA)+list(argvA)+list(stateB)+list(argvB)+list(state),\n" + 
			"                Implies(And(\n" + 
			"                    c1(stateA,argvA), c2(f1(stateA,argvA),argvB)\n" + 
			"                    )\n" + 
			"                ,z3_list_eq(f2(f1(state,argvA),argvB),\n" + 
			"                           f1(f2(state,argvB),argvA))))\n" + 
			"\n" + 
			"def Invariant_Free(f1,c1,f2,c2,I):\n" + 
			"    stateA = GenState(\"A\")\n" + 
			"    argvA = GenArgv(\"A\")\n" + 
			"    stateB = GenState(\"B\")\n" + 
			"    argvB = GenArgv(\"B\")\n" + 
			"    state = GenState(\"\")\n" + 
			"    return ForAll(list(stateA)+list(argvA)+list(stateB)+list(argvB)+list(state),\n" + 
			"            Implies(And(c1(stateA,argvA),c2(stateB,argvB),c2(f1(state,argvA),argvB),I(state),I(f1(state,argvA)),I(f2(f1(state,argvA),argvB))),\n" + 
			"                And(I(f2(state,argvB)),I(f1(f2(state,argvB),argvA)))))\n" + 
			"\n" + 
			"\n" + 
			"def equal(s1,s2,c):\n" + 
			"    argv = GenArgv(\"\")\n" + 
			"    argv_ = GenArgv(\"_\")\n" + 
			"    return Or(Implies(c(s1,argv),c(s2,argv_)),Implies(c(s1,argv),c(s2,argv)))\n" + 
			"\n" + 
			"def Context_Free(f1,c1,f2,c2,I):\n" + 
			"    state = GenState(\"\")\n" + 
			"    argvA = GenArgv(\"A\")\n" + 
			"    argvB = GenArgv(\"B\")\n" + 
			"    argvB_ = GenArgv(\"B_\")\n" + 
			"    return ForAll(list(argvA)+list(state), Implies(And(c1(state,argvA),I(state),I(f1(state,argvA))),equal(state,f1(state,argvA),c2))) \n" + 
			"\n" + 
			"def isFree2(F,op,I):\n" + 
			"    for c,f in op.ops:\n" + 
			"        solver = Solver()\n" + 
			"        solver.add(Not(F(f,c,I)))\n" + 
			"        if solver.check() == sat:\n" + 
			"            return False\n" + 
			"        elif solver.check() == unknown:\n" + 
			"            print \"unknown!!!\"\n" + 
			"            exit()\n" + 
			"    return True\n" + 
			"\n" + 
			"def isFree(F,op1,op2,I):\n" + 
			"    for c2,f2 in op2.ops:\n" + 
			"        for c1,f1 in op1.ops:\n" + 
			"            solver = Solver()\n" + 
			"            solver.add(Not(F(f1,c1,f2,c2,I)))\n" + 
			"            if solver.check() == sat:\n" + 
			"                return False\n" + 
			"            elif solver.check() == unknown:\n" + 
			"                print \"unknown!!!\"\n" + 
			"                exit()\n" + 
			"    return True\n" + 
			"    \n" + 
			"def sync(op1, op2):\n" + 
			"    return (len(op1.write & op2.sync)) != 0\n" + 
			"\n" + 
			"def depend(op1, op2):\n" + 
			"    return (len(op1.write & op2.depend)) != 0\n" + 
			"\n" + 
			"def SAT_DEBUG():\n" + 
			"    for op in op_list:\n" + 
			"        print \"check \" + op.__class__.__name__\n" + 
			"        if not isFree2(Self_Free,op,Inv):\n" + 
			"            print op.__class__.__name__ + \" not sat\"\n" + 
			"            # return False\n" + 
			"\n" + 
			"    for op1 in op_list:\n" + 
			"        for op2 in op_list:\n" + 
			"            print \"check \" + op2.__class__.__name__ + \" and \" + op1.__class__.__name__\n" + 
			"            if not ((sync(op1, op2) and depend(op1, op2)) or isFree(State_Free,op1,op2,Inv) or (sync(op1, op2) and isFree(State_Free_Inv,op1,op2,Inv))):\n" + 
			"                print op2.__class__.__name__ + \" is not state-free of \" + op1.__class__.__name__\n" + 
			"                # return False\n" + 
			"            if not (sync(op1, op2) or isFree(Context_Free,op1,op2,Inv)):\n" + 
			"                print op2.__class__.__name__ + \" is not context-free of \" + op1.__class__.__name__\n" + 
			"                # return False\n" + 
			"            if not (depend(op1, op2) or isFree(Invariant_Free,op1,op2,Inv)):\n" + 
			"                print op2.__class__.__name__ + \" is not invariant-free of \" + op1.__class__.__name__\n" + 
			"                # return False\n" + 
			"    return True\n" + 
			"\n" + 
			"def SAT():\n" + 
			"    for op1 in op_list:\n" + 
			"        for op2 in op_list:\n" + 
			"            if not ((isFree(State_Free,op1,op2,Inv) or (sync(op1, op2) and depend(op1, op2))) and\n" + 
			"                  (isFree(Context_Free,op1,op2,Inv) or sync(op1, op2)) and\n" + 
			"                  (isFree(Invariant_Free,op1,op2,Inv) or depend(op1, op2))):\n" + 
			"                return False\n" + 
			"    return True\n" + 
			"\n" + 
			"print SAT_DEBUG()";

	/**
	 * 
	 */
	public FileWritter(String fP) {
		this.filePath = "check-"+ fP + ".py";
		try {
			Files.deleteIfExists(Paths.get(this.filePath));
		} catch (IOException e) {
			System.out.println("Failed to delete a file");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void createFileIfNotExist()
	{
		File f = new File(this.filePath);
		if(!f.exists())
		{
			try {
				f.createNewFile();
				this.codeFile = f;
			} catch (IOException e) {
				System.out.println("Failed to create the file " + this.filePath);
				e.printStackTrace();
				System.exit(-1);
			}	
		}
	}
	
	public void headerWrite() {
		this.appendToFile(this.headerContent);
	}
	
	public void tailWrite() {
		this.appendToFile(this.footContent);
	}
	
	public void appendToFile(String formattedStr)
	{
		this.createFileIfNotExist();
		try {
			FileUtils.writeStringToFile(
				      this.codeFile , formattedStr + System.lineSeparator(), StandardCharsets.UTF_8, true);
		} catch (IOException e) {
			System.out.println("Failed to write " + formattedStr + " to file " + this.filePath);
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
