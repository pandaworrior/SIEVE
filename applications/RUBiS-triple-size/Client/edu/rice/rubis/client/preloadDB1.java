/*
 * RUBiS
 * Copyright (C) 2002, 2003, 2004 French National Institute For Research In Computer
 * Science And Control (INRIA).
 * Contact: jmob@objectweb.org
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 *
 * Initial developer(s): Emmanuel Cecchet, Julie Marguerite
 * Contributor(s): 
 */
 package edu.rice.rubis.client;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.Thread;
import java.lang.reflect.Array;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.mysql.jdbc.CallableStatement;
import com.mysql.jdbc.Statement;

import edu.rice.rubis.beans.TimeManagement;

/**
 * This program initializes the RUBiS database according to the rubis.properties file
 * found in the classpath.
 *
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class preloadDB 
{
  Connection c;
  java.sql.Statement stat;

  /**
   * Creates a new <code>InitDBParallel</code> instance.
   *
   */
  public preloadDB()
  {
    try{
    Class.forName("com.mysql.jdbc.Driver");
    c = DriverManager.getConnection("jdbc:mysql://localhost:50000/rubis?",
			"root",
			"101010");
	c.setAutoCommit(true);
	stat = c.createStatement();
   }catch(Exception e){
	e.printStackTrace();	
   }
  }
  
  public void commitTx(){
	  try{
		    c.commit();
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
  }
  
  public void preload(){
      /*try {
              stat.execute("DROP TABLE IF EXISTS BLACKHOLEbids;");
              stat.execute("CREATE TABLE BLACKHOLEbids LIKE bids;");
              stat.execute("ALTER TABLE BLACKHOLEbids ENGINE = BLACKHOLE;");
              stat.execute("INSERT INTO BLACKHOLEbids SELECT * FROM bids ORDER BY id;");
              stat.close();
              c.close();
      }catch(Exception e){
    	  e.printStackTrace();
      }*/
	  java.sql.CallableStatement cstmt = null;
	  try {
	     String SQL = "{call preload ()}";
	     cstmt = c.prepareCall (SQL);
	     cstmt.execute();
	  }
	  catch (SQLException e) {
	     e.printStackTrace();
	  }
  }


  /**
   * Main program accepts any combination of the following arguments: <pre>
   * all: generate the complete database
   * users: generate only users
   * items: generate only items
   * bids: generate bids and items (it is not possible to create bids without creating the related items)
   * comments: generate comments and items (it is not possible to create comments without creating the related items)
   *
   * @param args all|users|items|bids|comments
   */
  public static void main(String[] args) throws InterruptedException
  {
    System.out.println("RUBiS database initialization - (C) Rice University/INRIA 2001\n");

    preloadDB pDB = new preloadDB();
    pDB.preload();
    }
    	
   
  }
