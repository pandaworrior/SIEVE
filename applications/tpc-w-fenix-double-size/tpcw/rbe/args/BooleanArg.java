/*-------------------------------------------------------------------------
 * rbe.args.BooleanArg.java
 * Timothy Heil
 * 10/29/99
 *
 * Command line argument for integers.
 *------------------------------------------------------------------------
 *
 * This is part of the the Java TPC-W distribution,
 * written by Harold Cain, Tim Heil, Milo Martin, Eric Weglarz, and Todd
 * Bezenek.  University of Wisconsin - Madison, Computer Sciences
 * Dept. and Dept. of Electrical and Computer Engineering, as a part of
 * Prof. Mikko Lipasti's Fall 1999 ECE 902 course.
 *
 * Copyright (C) 1999, 2000 by Harold Cain, Timothy Heil, Milo Martin, 
 *                             Eric Weglarz, Todd Bezenek.
 *
 * This source code is distributed "as is" in the hope that it will be
 * useful.  It comes with no warranty, and no author or distributor
 * accepts any responsibility for the consequences of its use.
 *
 * Everyone is granted permission to copy, modify and redistribute
 * this code under the following conditions:
 *
 * This code is distributed for non-commercial use only.
 * Please contact the maintainer for restrictions applying to 
 * commercial use of these tools.
 *
 * Permission is granted to anyone to make or distribute copies
 * of this code, either as received or modified, in any
 * medium, provided that all copyright notices, permission and
 * nonwarranty notices are preserved, and that the distributor
 * grants the recipient permission for further redistribution as
 * permitted by this document.
 *
 * Permission is granted to distribute this code in compiled
 * or executable form under the same conditions that apply for
 * source code, provided that either:
 *
 * A. it is accompanied by the corresponding machine-readable
 *    source code,
 * B. it is accompanied by a written offer, with no time limit,
 *    to give anyone a machine-readable copy of the corresponding
 *    source code in return for reimbursement of the cost of
 *    distribution.  This written offer must permit verbatim
 *    duplication by anyone, or
 * C. it is distributed by someone who received only the
 *    executable form, and is accompanied by a copy of the
 *    written offer of source code that they received concurrently.
 *
 * In other words, you are welcome to use, share and improve this codes.
 * You are forbidden to forbid anyone else to use, share and improve what
 * you give them.
 *
 ************************************************************************/

package rbe.args;

public class BooleanArg extends Arg {
  public boolean flag = false;

  public BooleanArg(String arg, String name, String desc) {
	 super(arg, name, desc, false, false);
  }

  public BooleanArg(String arg, String name, String desc, ArgDB db) {
	 super(arg, name, desc, false, false, db);
  }

  public BooleanArg(String arg, String name, String desc, boolean def) {
	 super(arg, name, desc, false, true);
	 flag = def;
  }

  public BooleanArg(String arg, String name, String desc, boolean def, ArgDB db) {
	 super(arg, name, desc, false, true, db);
	 flag = def;
  }

  // Customize to parse arguments.
  protected int parseMatch(String [] args, int a)
		 throws Arg.Exception
  {
	 if (a == args.length) {
		throw new Arg.Exception("Boolean argument missing value.", a);
	 }

	 char ch = args[a].charAt(0);
	 /*switch (ch) {
	 case '0':  // zero
	 case 'F':  // false
	 case 'f':  
	 case 'd':  // disable
	 case 'D':  
	 case 'n':  // no
	 case 'N':
		flag = false;
		break;
	 case '1':  // one
	 case 'T':  // true
	 case 't':  
	 case 'e':  // enable
	 case 'E':  
	 case 'y':  // yes
	 case 'Y':
		flag = true;
		break;
	 default:
		throw new Arg.Exception("Unable to parse flag (" + args[a] + ").", a);
	 }*/

	 if(ch == '0' || ch == 'F' || ch == 'f' || ch == 'd' || ch == 'D' || ch == 'n' ||
			 ch == 'N'){
		 flag = false;
	 }else{
		 if(ch == 'l' || ch == 'T' || ch == 't' || ch == 'e' || ch == 'E' || ch == 'y' || ch == 'Y'){
			 flag = true;
		 }else{
			 throw new Arg.Exception("Unable to parse flag (" + args[a] + ").", a);
		 }
	 }
	 
	 return(a+1);
  }
	
  public String value() { return(""+flag); }
}

