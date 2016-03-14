/***************************************************************
Project name: georeplication
Class file name: CheckPath.java
Created at 10:04:49 PM by chengli

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

package test.staticanalysis.pathanalyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author chengli
 *
 */
public class CheckPath {
	static String[] keyWords = {"1855","1856","1879","1882","1790","1791","1885","1888","1889","1892"};
	public static void main(String[] args) throws Exception {
		
		String filePath = "/var/tmp/workspace/georeplication/dist/exec/doBuyConfirmReduction.txt";
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath)));
			String line;
			HashMap<Integer, String> records = new HashMap<Integer, String>();
			while ((line = br.readLine()) != null) {
				for(int i = 0; i < keyWords.length; i++) {
					int index = line.indexOf(keyWords[i]);
					if(index != -1) {
						records.put(index, keyWords[i]);
					}
				}
				if(records.size() == 0) {
					System.out.println("you are wrong");
				}else {
					List<Integer> sortedKeys=new ArrayList<Integer>(records.keySet());
					Collections.sort(sortedKeys);
					String output = "";
					for(Integer index : sortedKeys) {
						output += records.get(index) + ".";
					}
					System.out.println(output);
				}
				records.clear();
			}
			br.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
