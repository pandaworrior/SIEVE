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

import edu.rice.rubis.beans.TimeManagement;

/**
 * This program initializes the RUBiS database according to the rubis.properties file
 * found in the classpath.
 *
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class InitDBParallel implements Runnable
{
  private URLGenerator    urlGen = null;
  private Random          rand = new Random();
  private RUBiSProperties rubis = null;
  private static int[]           itemsPerCategory;
  private static AtomicInteger commentId =new AtomicInteger(0);
  private static AtomicInteger bidId = new AtomicInteger(0);
  private static AtomicInteger availableBid;
  
  static boolean generateBids;
  static boolean generateComments;
  
  int startItem = 0;
  int totalItemNum = 0;
  Connection c;

  /**
   * Creates a new <code>InitDBParallel</code> instance.
   *
   */
  public InitDBParallel(int sU, int tU)
  {
    rubis = new RUBiSProperties();
    urlGen = rubis.checkPropertiesFileAndGetURLGenerator();
    if (urlGen == null)
      Runtime.getRuntime().exit(1);
    itemsPerCategory = rubis.getItemsPerCategory();
    startItem = sU;
    totalItemNum = tU+sU;
    availableBid = new AtomicInteger(rubis.getTotalActiveItems());
    try{
    Class.forName("com.mysql.jdbc.Driver");
    c = DriverManager.getConnection("jdbc:mysql://localhost:50000/rubis?",
			"root",
			"101010");
	c.setAutoCommit(false);
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

    InitDBParallel initDB = new InitDBParallel(0,0);
    int    argc = Array.getLength(args);
    String params = "";

    if (argc == 0)
    {
      System.out.println("Command line  : java -classpath .:./database edu.rice.rubis.client.InitDBParallel parameters");
      System.out.println("Using Makefile: make initDB PARAM=\"parameters\"");
      System.out.println("where parameter is one or any combination of the following arguments:");
      System.out.println(" all: generate the complete database");
      System.out.println(" users: generate only users");
      System.out.println(" items: generate only items");
      System.out.println(" bids: generate bids and items (it is not possible to create bids without creating the related items)");
      System.out.println(" comments: generate comments and items (it is not possible to create comments without creating the related items)");
      Runtime.getRuntime().exit(1);
    }    

    for (int i = 0 ; i < argc ; i++)
     params = params +" "+ args[i];
    
    if ((params.indexOf("users") != -1) || (params.indexOf("all") != -1))
      initDB.generateUsers();
    
    if ((params.indexOf("items") != -1) || (params.indexOf("bids") != -1) || 
        (params.indexOf("comments") != -1) || (params.indexOf("all") != -1)){
    	initDB.generateBids = (params.indexOf("bids") != -1) || (params.indexOf("all") != -1);
        initDB.generateComments = (params.indexOf("comments") != -1) || (params.indexOf("all") != -1);
        initDB.startItem = 0;
        initDB.totalItemNum = initDB.rubis.getNbOfOldItems() + initDB.rubis.getTotalActiveItems();
        System.out.println("start " + initDB.startItem + " total " + initDB.totalItemNum);
        initDB.generateItems(initDB.generateBids, initDB.generateComments);
        //start to generate items
        /*BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100, true);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
          12, // core size
          20, // max size
          5, // keep alive time
          TimeUnit.HOURS, // keep alive time units
          queue// the queue to use
        );
        
        System.out.println("coolsize " + executor.getCorePoolSize() + " max size " +executor.getMaximumPoolSize() );
        int keyspace =     initDB.rubis.getNbOfOldItems() + initDB.rubis.getTotalActiveItems();
        int eachPiece = keyspace/8;
        
        for(long i = 0; i<8; i++) {
        	System.out.println("start " + i + " thread start at " +(int) (i*eachPiece) + " total " + eachPiece);
            InitDBParallel t = new InitDBParallel((int) (i*eachPiece),eachPiece);
            System.out.println(Thread.currentThread().getName() + " submitted " + t + ", queue size = " + executor.getQueue().size());
            try {
              executor.execute(t);
            } catch (RejectedExecutionException e) {
              // will be thrown if rejected execution handler
              // is not set with executor.setRejectedExecutionHandler
              e.printStackTrace();
            }
            Thread.sleep(1);
          }
        while (executor.getTaskCount()!=executor.getCompletedTaskCount()){
            System.err.println("count="+executor.getTaskCount()+","+executor.getCompletedTaskCount());
            Thread.sleep(20000);
        }
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);*/

        System.exit(-1);
    }
    	
   
  }


  /**
   * This method add users to the database according to the parameters
   * given in the database.properties file.
   */
  public void generateUsers()
  {
    String firstname;
    String lastname;
    String nickname;
    String email;
    String password;
    String regionName;
    String HTTPreply;
    int    i;
    URL    url;

    // Cache variables
    int getNbOfUsers = rubis.getNbOfUsers();
    int getNbOfRegions = rubis.getNbOfRegions();

    System.out.print("Generating "+getNbOfUsers+" users ");
    for (i = 0 ; i < getNbOfUsers ; i++)
    {
      firstname = "Great"+(i+1);
      lastname = "User"+(i+1);
      nickname = "user"+(i+1);
      email = firstname+"."+lastname+"@rubis.com";
      password = "password"+(i+1);
      regionName = (String)rubis.getRegions().elementAt(i % getNbOfRegions);

      PreparedStatement updatestatement = null;
      try
      {
    	  String now = TimeManagement.currentDateToString();
    	  updatestatement =c.prepareStatement(
    			  "INSERT INTO " +
    	          	"users(id,firstname,lastname,nickname,password,email,rating,balance,creation_date,region) " +
    	          	"VALUES ("+(i+1)+", \""
    	            + firstname
    	            + "\", \""
    	            + lastname
    	            + "\", \""
    	            + nickname
    	            + "\", \""
    	            + password
    	            + "\", \""
    	            + email
    	            + "\", 0, 0,\""
    	            + now
    	            + "\", "
    	            + "(select id from regions where name='"+regionName+"')"
    	            + ")");
    	  updatestatement.executeUpdate();
      }catch(SQLException e){
    	  e.printStackTrace();
      }
      if (i % 100 == 0){
        System.out.print(".");
        commitTx();
      }
    }
   commitTx();
    System.out.println(" Done!");
  }
  
  public synchronized void updateItemIdCategory(int categoryId){
	  itemsPerCategory[categoryId]--;
  }
  
  public synchronized int getItemIdCategory(int categoryId){
	  return itemsPerCategory[categoryId];
  }

  /**
   * This method add items to the database according to the parameters
   * given in the database.properties file.
   */
  public void generateItems(boolean generateBids, boolean generateComments)
  {
    // Items specific variables
    String name;
    String description;
    float  initialPrice; 
    float  reservePrice;
    float  buyNow;
    int    duration;
    int    quantity;
    int    categoryId;
    int    sellerId;
    int    oldItems = rubis.getNbOfOldItems();
    int    activeItems = rubis.getTotalActiveItems();
    int    totalItems = oldItems + activeItems;
    String staticDescription = "This incredible item is exactly what you need !<br>It has a lot of very nice features including "+
      "a coffee option.<p>It comes with a free license for the free RUBiS software, that's really cool. But RUBiS even if it "+
      "is free, is <B>(C) Rice University/INRIA 2001</B>. It is really hard to write an interesting generic description for "+
      "automatically generated items, but who will really read this ?<p>You can also check some cool software available on "+
      "http://sci-serv.inrialpes.fr. There is a very cool DSM system called SciFS for SCI clusters, but you will need some "+
      "SCI adapters to be able to run it ! Else you can still try CART, the amazing 'Cluster Administration and Reservation "+
      "Tool'. All those software are open source, so don't hesitate ! If you have a SCI Cluster you can also try the Whoops! "+
      "clustered web server. Actually Whoops! stands for something ! Yes, it is a Web cache with tcp Handoff, On the fly "+
      "cOmpression, parallel Pull-based lru for Sci clusters !! Ok, that was a lot of fun but now it is starting to be quite late "+
      "and I'll have to go to bed very soon, so I think if you need more information, just go on <h1>http://sci-serv.inrialpes.fr</h1> "+
      "or you can even try http://www.cs.rice.edu and try to find where Emmanuel Cecchet or Julie Marguerite are and you will "+
      "maybe get fresh news about all that !!<p>";

    // Comments specific variables
    int      staticDescriptionLength = staticDescription.length();
    String[] staticComment = { "This is a very bad comment. Stay away from this seller !!<p>",
                               "This is a comment below average. I don't recommend this user !!<p>",
                               "This is a neutral comment. It is neither a good or a bad seller !!<p>",
                               "This is a comment above average. You can trust this seller even if it is not the best deal !!<p>",
                               "This is an excellent comment. You can make really great deals with this seller !!<p>" };
    int[]    staticCommentLength = { staticComment[0].length(), staticComment[1].length(), staticComment[2].length(),
                                     staticComment[3].length(), staticComment[4].length()};
    int[]    ratingValue = { -5, -3, 0, 3, 5 };
    int      rating; 
    String   comment;

    // Bids specific variables
    int    nbBids;

    // All purpose variables
    int    i, j;
    URL    url;
    String HTTPreply;

    // Cache variables
    int   getItemDescriptionLength = rubis.getItemDescriptionLength();
    float getPercentReservePrice = rubis.getPercentReservePrice();
    float getPercentBuyNow = rubis.getPercentBuyNow();
    float getPercentUniqueItems = rubis.getPercentUniqueItems();
    int   getMaxItemQty = rubis.getMaxItemQty();
    int   getCommentMaxLength = rubis.getCommentMaxLength();
    int   getNbOfCategories = rubis.getNbOfCategories();
    int   getNbOfUsers = rubis.getNbOfUsers();
    int   getMaxBidsPerItem = rubis.getMaxBidsPerItem();

    System.out.println("Generating "+oldItems+" old items and "+activeItems+" active items.");
    if (generateBids)
      System.out.println("Generating up to "+getMaxBidsPerItem+" bids per item.");
    if (generateComments)
      System.out.println("Generating 1 comment per item");
    int counter = 0;
   while(startItem < totalItemNum){
      // Generate the item
	   i = startItem;
      name = "RUBiS automatically generated item #"+(i+1);
      int descriptionLength = rand.nextInt(getItemDescriptionLength-(int)(getItemDescriptionLength*0.2) )+1;
      description = "";
      while (staticDescriptionLength < descriptionLength)
      {
        description = description+staticDescription;
        descriptionLength -= staticDescriptionLength;
      }
      description += staticDescription.substring(0, descriptionLength);
      initialPrice = rand.nextInt(5000)+1;
      duration = rand.nextInt(7)+1;
      if (i < oldItems)
      { // This is an old item
        duration = -duration; // give a negative auction duration so that auction will be over
        if (i < getPercentReservePrice*oldItems/100)
          reservePrice = rand.nextInt(1000)+initialPrice;
        else
          reservePrice = 0;
        if (i < getPercentBuyNow*oldItems/100)
          buyNow = rand.nextInt(1000)+initialPrice+reservePrice;
        else
          buyNow = 0;
        if (i < getPercentUniqueItems*oldItems/100)
          quantity = 1;
        else
          quantity = rand.nextInt(getMaxItemQty)+1;
      }
      else
      {
        if (i < getPercentReservePrice*activeItems/100)
          reservePrice = rand.nextInt(1000)+initialPrice;
        else
          reservePrice = 0;
        if (i < getPercentBuyNow*activeItems/100)
          buyNow = rand.nextInt(1000)+initialPrice+reservePrice;
        else
          buyNow = 0;
        if (i < getPercentUniqueItems*activeItems/100)
          quantity = 1;
        else
          quantity = rand.nextInt(getMaxItemQty)+1;
      }
      categoryId =  i % getNbOfCategories;
      // Hopefully everything is ok and we will not have a deadlock here
      while (getItemIdCategory(categoryId) == 0)
        categoryId = (categoryId + 1) % getNbOfCategories;
      if (i >= oldItems)
    	  updateItemIdCategory(categoryId);
      sellerId = rand.nextInt(getNbOfUsers) + 1;
      
      GregorianCalendar now = new GregorianCalendar();
      //later = TimeManagement.addMinutes(now, duration);
      GregorianCalendar later = TimeManagement.addDays(now, duration);
      String startDate = TimeManagement.dateToString(now);
      String endDate = TimeManagement.dateToString(later);
      PreparedStatement stmt = null;
      String str =
     	 "INSERT INTO items" +
     	 "(id,name,description,initial_price,quantity,reserve_price," +
     	 "buy_now,nb_of_bids,max_bid,start_date,end_date,seller,category) " +
     	 "VALUES ("+(i+1)+", \""
           + name
           + "\", \""
           + description
           + "\", \""
           + initialPrice
           + "\", \""
           + quantity
           + "\", \""
           + reservePrice
           + "\", \""
           + buyNow
           + "\", 0, 0, \""
           + startDate
           + "\", \""
           + endDate
           + "\", \""
           + sellerId
           + "\", \""
           + (categoryId+1)+"\")";
      try{
    	  stmt = c.prepareStatement(str);
    	  stmt.executeUpdate();
    	  stmt.close();
      }catch(Exception e){
    	  e.printStackTrace();
      }

      double availBid = rand.nextDouble();
      if (generateBids && (availBid<=0.062))
      { // Now deal with the bids
        nbBids = rand.nextInt(getMaxBidsPerItem);
        for (j = 0 ; j < nbBids ; j++)
        {
          int addBid = rand.nextInt(10)+1;
          String currentTS = TimeManagement.currentDateToString();
          try{
          stmt =
            c.prepareStatement(
              //"INSERT INTO bids VALUES (NULL, \""
           	  "INSERT INTO bids(id,user_id,item_id,qty,bid,max_bid,date)" +
           	  	" VALUES ("+bidId.addAndGet(1)+", \""
                + (rand.nextInt(getNbOfUsers)+1)
                + "\", \""
                + (i+1)
                + "\", \""
                + (rand.nextInt(quantity)+1)
                + "\", \""
                + (initialPrice+addBid)
                + "\", \""
                + (initialPrice+addBid*2)
                + "\", \""
                + currentTS
                + "\")");
          stmt.executeUpdate();
          stmt.close();
          PreparedStatement update =
              c.prepareStatement(
                "UPDATE items SET max_bid="+(initialPrice+addBid*2)+", nb_of_bids=nb_of_bids+1 WHERE id="+(i+1));
          update.executeUpdate();
          update.close();
          
          }catch(Exception e){
        	  e.printStackTrace();
          }
          initialPrice += addBid; // We use initialPrice as minimum bid
        }
      }

      double availComment = rand.nextDouble();
      if (generateComments && (availComment <= 0.95))
      { // Generate the comment
        rating = rand.nextInt(5);
        int commentLength = rand.nextInt(getCommentMaxLength)+1;
        comment = "";
        while (staticCommentLength[rating] < commentLength)
        {
          comment = comment+staticComment[rating];
          commentLength -= staticCommentLength[rating];
        }
        comment += staticComment[rating].substring(0, commentLength);
        
        
        String currentTS = TimeManagement.currentDateToString();
        try{
        	int toId = (rand.nextInt(getNbOfUsers)+1);
        stmt =
          c.prepareStatement(
            "INSERT INTO comments(id,from_user_id,to_user_id,item_id,rating,date,comment)" +
            " VALUES ("+(i+1)+", \""  
              + sellerId
              + "\", \""
              + toId
              + "\", \""
              + commentId.addAndGet(1)
              + "\", \""
              + ratingValue[rating]
              + "\", \""
              + currentTS
              + "\",\""
              + comment
              + "\")");

        stmt.executeUpdate();
        stmt.close();
        /*stmt = c.prepareStatement("UPDATE users SET rating=rating+"+ratingValue[rating]+" WHERE id=" +toId );
        stmt.executeUpdate();
        stmt.close();*/
        }catch(Exception e){
        	e.printStackTrace();
        }
      }

      if (counter % 100 == 0){
        System.out.print(".");
       commitTx();
      }
      startItem++;
      counter++;
    }
   commitTx();
    System.out.println(" Done!" + startItem + " " + counter);
  }



  /**
   * Call the HTTP Server according to the given URL and get the reply
   *
   * @param url URL to access
   * @return <code>String</code> containing the web server reply (HTML file)
   */
  private String callHTTPServer(URL url)
  {
    String              HTMLReply = "";
    BufferedInputStream in = null;
    int                 retry = 0;
        
    while (retry < 5)
    {
      // Open the connexion
      try
      {
        in = new BufferedInputStream(url.openStream(), 4096);
      }
      catch (IOException ioe) 
      {
        System.err.println("Unable to open URL "+url+" ("+ioe.getMessage()+")");
        retry++;
        try
        {
          Thread.currentThread().sleep(1000L);
        }
        catch (InterruptedException i) 
        {
          System.err.println("Interrupted in callHTTPServer()");
          return null;
        }
        continue;
      }

      // Get the data
      try 
      {
        byte[] buffer = new byte[4096];
        int    read;

        while ((read = in.read(buffer, 0, buffer.length)) != -1)
        {
          if (read > 0) 
            HTMLReply = HTMLReply + new String(buffer, 0, read);
        }
      }
      catch (IOException ioe) 
      {
        System.err.println("Unable to read from URL "+url+" ("+ioe.getMessage()+")");
        return null;
      }

      // No retry at this point
      break;
    }        
        
    try
    {
      if (in != null)
        in.close();
    } 
    catch (IOException ioe) 
    {
        System.err.println("Unable to close URL "+url+" ("+ioe.getMessage()+")");
    }
    return HTMLReply;
  }


@Override
public void run() {
	// TODO Auto-generated method stub
	this.generateItems(generateBids, generateComments);
}

}
