/********************************************************************
Copyright (c) 2013 chengli.
All rights reserved. This program and the accompanying materials
are made available under the terms of the GNU Public License v2.0
which accompanies this distribution, and is available at
http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

Contributors:
    chengli - initial API and implementation

Contact:
    To distribute or use this code requires prior specific permission.
    In this case, please contact chengli@mpi-sws.org.
 ********************************************************************/
/**
 * 
 */
package test.staticanalysis.codeparser;

// TODO: Auto-generated Javadoc
/**
 * The Class ExampleFile.
 */
public class ExampleFile {
	
	/** The tmd. */
	Object tmd;
	
	/** The example f. */
	int exampleF;

	/*public void testBreak() {
		for (int i = 0; i <= 10; i++) {
			if (i > 5) {
				break;
			} else {
				i = i + 1;
			}
		}
		// return 0;
	}*/

	/*
	 * public int target(){ int c = 1; return c; }
	 */

	/**
	 * Great computation.
	 *
	 * @param a the a
	 * @return the int
	 */
	public int greatComputation(int a){ 
		//Object c = new Object(a.target());
		//ExampleFile ef = new ExampleFile(); 
		//int w = ef.target() + ef.target();
	 	int b = a; 
	 	b = a; 
	 	if(a > b){ 
	 		b = a; 
	 		int t = 5; 
	 		t = a; 
	 	}
	 	else{ 
	 		a = b; 
	 		int f = 8;
	 	} 
	 	
	 	for(int i = 0; i < 10 ; i++){ 
	 		a = b; 
	 	} 
	 	a = this.greatComputation(b); 
	 	if(a == 1){ 
	 		return -1; 
	 	}else{ 
	 		return 0; 
	 	} 
	 }

}

/*
 * class something{ public void hello(){ System.out.println("hello"); } }
 */
