/***************************************************************
Project name: georeplication
Class file name: WPChecker.java
Created at 12:46:41 PM by chengli

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
****************************************************************/

package test.runtimelogic.weakestpreconditionchecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import runtimelogic.staticinformation.StaticFPtoWPsStore;
import runtimelogic.weakestpreconditionchecker.WeakestPreconditionChecker;
/**
 * @author chengli
 *
 */
public class WPChecker {
	
	public static List<String> getSignatureList(String path){
		String[] signatures = path.split("\\.");
		List<String> signatureList = new ArrayList<String>();
		for(int i = 0; i < signatures.length; i++) {
			signatureList.add(signatures[i]);
		}
		return signatureList;
	}
	
	public static void main(String[] args) throws Exception {
		
		//String filePath = "/var/tmp/workspace/georeplication/src/test/runtimelogic/weakestpreconditionchecker/wpconditiontest.txt";
		//String filePath = "/var/tmp/workspace/georeplication/dist/exec/transformedcodetpcw/tpcw_sieve_input.txt";
		//String filePath = "/var/tmp/workspace/georeplication/src/test/runtimelogic/weakestpreconditionchecker/tpcw_sieve_input.txt";
		String filePath = "/var/tmp/workspace/MPI-txmud-java/src/applications/tpc-w-fenix/tpcw_sieve_input.txt";
		StaticFPtoWPsStore sFpStore = new StaticFPtoWPsStore(filePath);
		sFpStore.loadAllStaticOutput();
		//WeakestPreconditionChecker.setStaticFPtoWPsStore(sFpStore);
		
		String fgPrints = "A023B77E65493711AD9DBC0D4911FC32C7FBA029.6F56748984208AD77F406CA0F1766328E6E10FEB.54446CAEE1A17C0FFB46B4820EFCAF32DBE96E88.6F56748984208AD77F406CA0F1766328E6E10FEB.54446CAEE1A17C0FFB46B4820EFCAF32DBE96E88.6F56748984208AD77F406CA0F1766328E6E10FEB.54446CAEE1A17C0FFB46B4820EFCAF32DBE96E88.6F56748984208AD77F406CA0F1766328E6E10FEB.54446CAEE1A17C0FFB46B4820EFCAF32DBE96E88.6F56748984208AD77F406CA0F1766328E6E10FEB.54446CAEE1A17C0FFB46B4820EFCAF32DBE96E88.6F56748984208AD77F406CA0F1766328E6E10FEB.54446CAEE1A17C0FFB46B4820EFCAF32DBE96E88.6F56748984208AD77F406CA0F1766328E6E10FEB.54446CAEE1A17C0FFB46B4820EFCAF32DBE96E88.6F56748984208AD77F406CA0F1766328E6E10FEB.6F56748984208AD77F406CA0F1766328E6E10FEB.54446CAEE1A17C0FFB46B4820EFCAF32DBE96E88.6F56748984208AD77F406CA0F1766328E6E10FEB.54446CAEE1A17C0FFB46B4820EFCAF32DBE96E88.6F56748984208AD77F406CA0F1766328E6E10FEB.54446CAEE1A17C0FFB46B4820EFCAF32DBE96E88.6A2F49780972BF5757EE1AAC7B676A1F87AA8637.54446CAEE1A17C0FFB46B4820EFCAF32DBE96E88.29A4387C75B4A9C7B4681695995F5F90DF22821C.29A4387C75B4A9C7B4681695995F5F90DF22821C.29A4387C75B4A9C7B4681695995F5F90DF22821C.29A4387C75B4A9C7B4681695995F5F90DF22821C.29A4387C75B4A9C7B4681695995F5F90DF22821C.29A4387C75B4A9C7B4681695995F5F90DF22821C.29A4387C75B4A9C7B4681695995F5F90DF22821C.29A4387C75B4A9C7B4681695995F5F90DF22821C.29A4387C75B4A9C7B4681695995F5F90DF22821C.29A4387C75B4A9C7B4681695995F5F90DF22821C.29A4387C75B4A9C7B4681695995F5F90DF22821C";
		List<String> fgList = getSignatureList(fgPrints);
		
		sFpStore.fetchWeakestPreconditionByGivenSequenceOfOperations(fgList);
		//WeakestPreconditionChecker.getColor(null);
		/*String path1= "1.2.3.4.2.3.4.3.4.3.4";
		List<String> signatureList = getSignatureList(path1);
		System.out.println("runtime signature: " + path1);
		sFpStore.fetchWeakestPreconditionByGivenSequenceOfOperations(signatureList);
		
		path1= "1.2.3.4.2.3.4.3.4.3";
		signatureList = getSignatureList(path1);
		System.out.println("runtime signature: " + path1);
		sFpStore.fetchWeakestPreconditionByGivenSequenceOfOperations(signatureList);
		
		path1= "1.2.3";
		signatureList = getSignatureList(path1);
		System.out.println("runtime signature: " + path1);
		sFpStore.fetchWeakestPreconditionByGivenSequenceOfOperations(signatureList);
		
		path1= "1.4";
		signatureList = getSignatureList(path1);
		System.out.println("runtime signature: " + path1);
		sFpStore.fetchWeakestPreconditionByGivenSequenceOfOperations(signatureList);*/
		
	}

}
