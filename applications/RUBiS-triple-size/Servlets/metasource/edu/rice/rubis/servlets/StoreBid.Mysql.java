package edu.rice.rubis.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** This servlet records a bid in the database and display
 * the result of the transaction.
 * It must be called this way :
 * <pre>
 * http://..../StoreBid?itemId=aa&userId=bb&minBid=cc&maxQty=dd&bid=ee&maxBid=ff&qty=gg 
 *   where: aa is the item id 
 *          bb is the user id
 *          cc is the minimum acceptable bid for this item
 *          dd is the maximum quantity available for this item
 *          ee is the user bid
 *          ff is the maximum bid the user wants
 *          gg is the quantity asked by the user
 * </pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class StoreBid extends HttpServlet
{


//  public int getPoolSize()
//  {
//    return Config.StoreBidPoolSize;
//  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: StoreBid");
    sp.printHTML(
      "<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
  }
  
  private void printError1(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: StoreBid");
    sp.printHTML(
      "<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
  }
  
  private void printError2(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: StoreBid");
    sp.printHTML(
      "<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
  }

  /**
   * Call the <code>doPost</code> method.
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doPost(request, response);
  }
  
  public void doGet1(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
{
  doPost1(request, response);
}
  
  public void doGet2(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
{
  doPost2(request, response);
}

  /**
   * Store the bid to the database and display resulting message.
   *
   * @param request a <code>HttpServletRequest</code> value
   * @param response a <code>HttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Integer userId; // item id
    Integer itemId; // user id
    float minBid; // minimum acceptable bid for this item
    float bid; // user bid
    float maxBid; // maximum bid the user wants
    int maxQty; // maximum quantity available for this item
    int qty; // quantity asked by the user
    ServletPrinter sp = null;
    PreparedStatement stmt = null;
    Connection conn = null;

    sp = new ServletPrinter(response, "StoreBid");

    /* Get and check all parameters */

    String value = request.getParameter("userId");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a user identifier !<br></h3>", sp);
      return;
    }
    else
      userId = new Integer(value);

    value = request.getParameter("itemId");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide an item identifier !<br></h3>", sp);
      return;
    }
    else
      itemId = new Integer(value);

    value = request.getParameter("minBid");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a minimum bid !<br></h3>", sp);
      return;
    }
    else
    {
      Float foo = new Float(value);
      minBid = foo.floatValue();
    }

    value = request.getParameter("bid");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a bid !<br></h3>", sp);
      return;
    }
    else
    {
      Float foo = new Float(value);
      bid = foo.floatValue();
    }

    value = request.getParameter("maxBid");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a maximum bid !<br></h3>", sp);
      return;
    }
    else
    {
      Float foo = new Float(value);
      maxBid = foo.floatValue();
    }

    value = request.getParameter("maxQty");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a maximum quantity !<br></h3>", sp);
      return;
    }
    else
    {
      Integer foo = new Integer(value);
      maxQty = foo.intValue();
    }

    value = request.getParameter("qty");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a quantity !<br></h3>", sp);
      return;
    }
    else
    {
      Integer foo = new Integer(value);
      qty = foo.intValue();
    }

    /* Check for invalid values */

    if (qty > maxQty)
    {
      printError(
        "<h3>You cannot request "
          + qty
          + " items because only "
          + maxQty
          + " are proposed !<br></h3>", sp);
      return;
    }
    if (bid < minBid)
    {
      printError(
        "<h3>Your bid of $"
          + bid
          + " is not acceptable because it is below the $"
          + minBid
          + " minimum bid !<br></h3>", sp);
      return;
    }
    if (maxBid < minBid)
    {
      printError(
        "<h3>Your maximum bid of $"
          + maxBid
          + " is not acceptable because it is below the $"
          + minBid
          + " minimum bid !<br></h3>", sp);
      return;
    }
    if (maxBid < bid)
    {
      printError(
        "<h3>Your maximum bid of $"
          + maxBid
          + " is not acceptable because it is below your current bid of $"
          + bid
          + " !<br></h3>", sp);
      return;
    }
    try
    {
      conn = Database.getConnection();
      String now = TimeManagement.currentDateToString();
      stmt =
        conn.prepareStatement(
          //"INSERT INTO bids VALUES (NULL, \""
       	  "INSERT INTO bids(id,user_id,item_id,qty,bid,max_bid,date)" +
       	  	" VALUES ("+Config.BidIDFactory.addAndGet(Config.TotalProxies)+", \""
            + userId
            + "\", \""
            + itemId
            + "\", \""
            + qty
            + "\", \""
            + bid
            + "\", \""
            + maxBid
            + "\", \""
            + now
            + "\")");
      stmt.executeUpdate();
      stmt.close();
      // update the number of bids and the max bid for the item
      PreparedStatement update = null;
      try
      {
        stmt =
          conn.prepareStatement(
            "SELECT nb_of_bids, max_bid FROM items WHERE id=?");
        stmt.setInt(1, itemId.intValue());
        ResultSet rs = stmt.executeQuery();
        if (rs.first())
        {
          
          int nbOfBids = rs.getInt("nb_of_bids");
          nbOfBids++;
          float oldMaxBid = rs.getFloat("max_bid");
          if (bid > oldMaxBid)
          {
            oldMaxBid = bid;
            update =
              conn.prepareStatement(
                "UPDATE items SET max_bid=?, nb_of_bids=? WHERE id=?");
            update.setFloat(1, maxBid);
            update.setInt(2, nbOfBids);
            update.setInt(3, itemId.intValue());
            update.executeUpdate();
            update.close();
          }
          else
          {
            update =
              conn.prepareStatement("UPDATE items SET nb_of_bids=? WHERE id=?");
            update.setInt(1, nbOfBids);
            update.setInt(2, itemId.intValue());
            update.executeUpdate();
            update.close();
          }

        }
        else
        {
          Database.commit(conn);
          printError("Couldn't find the item.", sp);
          Database.closeConnection(stmt, conn);
          return;
        }
      }
      catch (Exception ex)
      {
    	Database.rollback(conn);
        printError("Failed to update nb of bids and max bid: " + ex, sp);
        if (update != null) 
          update.close();
        Database.closeConnection(stmt, conn);
        return;
      }
      sp.printHTMLheader("RUBiS: Bidding result");
      sp.printHTML(
        "<center><h2>Your bid has been successfully processed.</h2></center>\n");
      Database.commit(conn);
      Database.closeConnection(stmt, conn);
    }
    catch (Exception e)
    {
      sp.printHTML(
        "Error while storing the bid (got exception: " + e + ")<br>");
    	Database.rollback(conn);
        Database.closeConnection(stmt, conn);
      return;
    }
    sp.printHTMLfooter();
  }
  
  public void doPost1(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
{
  Integer userId; // item id
  Integer itemId; // user id
  float minBid; // minimum acceptable bid for this item
  float bid; // user bid
  float maxBid; // maximum bid the user wants
  int maxQty; // maximum quantity available for this item
  int qty; // quantity asked by the user
  ServletPrinter sp = null;
  PreparedStatement stmt = null;
  Connection conn = null;

  sp = new ServletPrinter(response, "StoreBid");

  /* Get and check all parameters */

  String value = request.getParameter("userId");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a user identifier !<br></h3>", sp);
    return;
  }
  else
    userId = new Integer(value);

  value = request.getParameter("itemId");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide an item identifier !<br></h3>", sp);
    return;
  }
  else
    itemId = new Integer(value);

  value = request.getParameter("minBid");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a minimum bid !<br></h3>", sp);
    return;
  }
  else
  {
    Float foo = new Float(value);
    minBid = foo.floatValue();
  }

  value = request.getParameter("bid");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a bid !<br></h3>", sp);
    return;
  }
  else
  {
    Float foo = new Float(value);
    bid = foo.floatValue();
  }

  value = request.getParameter("maxBid");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a maximum bid !<br></h3>", sp);
    return;
  }
  else
  {
    Float foo = new Float(value);
    maxBid = foo.floatValue();
  }

  value = request.getParameter("maxQty");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a maximum quantity !<br></h3>", sp);
    return;
  }
  else
  {
    Integer foo = new Integer(value);
    maxQty = foo.intValue();
  }

  value = request.getParameter("qty");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a quantity !<br></h3>", sp);
    return;
  }
  else
  {
    Integer foo = new Integer(value);
    qty = foo.intValue();
  }

  /* Check for invalid values */

  if (qty > maxQty)
  {
    printError(
      "<h3>You cannot request "
        + qty
        + " items because only "
        + maxQty
        + " are proposed !<br></h3>", sp);
    return;
  }
  if (bid < minBid)
  {
    printError(
      "<h3>Your bid of $"
        + bid
        + " is not acceptable because it is below the $"
        + minBid
        + " minimum bid !<br></h3>", sp);
    return;
  }
  if (maxBid < minBid)
  {
    printError(
      "<h3>Your maximum bid of $"
        + maxBid
        + " is not acceptable because it is below the $"
        + minBid
        + " minimum bid !<br></h3>", sp);
    return;
  }
  if (maxBid < bid)
  {
    printError(
      "<h3>Your maximum bid of $"
        + maxBid
        + " is not acceptable because it is below your current bid of $"
        + bid
        + " !<br></h3>", sp);
    return;
  }
  try
  {
    conn = Database.getConnection();
    String now = TimeManagement.currentDateToString();
    stmt =
      conn.prepareStatement(
        //"INSERT INTO bids VALUES (NULL, \""
     	  "INSERT INTO bids(id,user_id,item_id,qty,bid,max_bid,date)" +
     	  	" VALUES ("+Config.BidIDFactory.addAndGet(Config.TotalProxies)+", \""
          + userId
          + "\", \""
          + itemId
          + "\", \""
          + qty
          + "\", \""
          + bid
          + "\", \""
          + maxBid
          + "\", \""
          + now
          + "\")");
    stmt.executeUpdate();
    stmt.close();
    // update the number of bids and the max bid for the item
    PreparedStatement update = null;
    try
    {
      stmt =
        conn.prepareStatement(
          "SELECT nb_of_bids, max_bid FROM items WHERE id=?");
      stmt.setInt(1, itemId.intValue());
      ResultSet rs = stmt.executeQuery();
      if (rs.first())
      {
        
        int nbOfBids = rs.getInt("nb_of_bids");
        nbOfBids++;
        float oldMaxBid = rs.getFloat("max_bid");
        if (bid > oldMaxBid)
        {
          oldMaxBid = bid;
          update =
            conn.prepareStatement(
              "UPDATE items SET max_bid=?, nb_of_bids=? WHERE id=?");
          update.setFloat(1, maxBid);
          update.setInt(2, nbOfBids);
          update.setInt(3, itemId.intValue());
          update.executeUpdate();
          update.close();
        }
        else
        {
          update =
            conn.prepareStatement("UPDATE items SET nb_of_bids=? WHERE id=?");
          update.setInt(1, nbOfBids);
          update.setInt(2, itemId.intValue());
          update.executeUpdate();
          update.close();
        }

      }
      else
      {
        Database.commit(conn);
        printError("Couldn't find the item.", sp);
        Database.closeConnection(stmt, conn);
        return;
      }
    }
    catch (Exception ex)
    {
  	Database.rollback(conn);
      printError("Failed to update nb of bids and max bid: " + ex, sp);
      if (update != null) 
        update.close();
      Database.closeConnection(stmt, conn);
      return;
    }
    sp.printHTMLheader("RUBiS: Bidding result");
    sp.printHTML(
      "<center><h2>Your bid has been successfully processed.</h2></center>\n");
    Database.commit(conn);
    Database.closeConnection(stmt, conn);
  }
  catch (Exception e)
  {
    sp.printHTML(
      "Error while storing the bid (got exception: " + e + ")<br>");
  	Database.rollback(conn);
      Database.closeConnection(stmt, conn);
    return;
  }
  sp.printHTMLfooter();
}
  
  public void doPost2(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
{
  Integer userId; // item id
  Integer itemId; // user id
  float minBid; // minimum acceptable bid for this item
  float bid; // user bid
  float maxBid; // maximum bid the user wants
  int maxQty; // maximum quantity available for this item
  int qty; // quantity asked by the user
  ServletPrinter sp = null;
  PreparedStatement stmt = null;
  Connection conn = null;

  sp = new ServletPrinter(response, "StoreBid");

  /* Get and check all parameters */

  String value = request.getParameter("userId");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a user identifier !<br></h3>", sp);
    return;
  }
  else
    userId = new Integer(value);

  value = request.getParameter("itemId");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide an item identifier !<br></h3>", sp);
    return;
  }
  else
    itemId = new Integer(value);

  value = request.getParameter("minBid");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a minimum bid !<br></h3>", sp);
    return;
  }
  else
  {
    Float foo = new Float(value);
    minBid = foo.floatValue();
  }

  value = request.getParameter("bid");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a bid !<br></h3>", sp);
    return;
  }
  else
  {
    Float foo = new Float(value);
    bid = foo.floatValue();
  }

  value = request.getParameter("maxBid");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a maximum bid !<br></h3>", sp);
    return;
  }
  else
  {
    Float foo = new Float(value);
    maxBid = foo.floatValue();
  }

  value = request.getParameter("maxQty");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a maximum quantity !<br></h3>", sp);
    return;
  }
  else
  {
    Integer foo = new Integer(value);
    maxQty = foo.intValue();
  }

  value = request.getParameter("qty");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a quantity !<br></h3>", sp);
    return;
  }
  else
  {
    Integer foo = new Integer(value);
    qty = foo.intValue();
  }

  /* Check for invalid values */

  if (qty > maxQty)
  {
    printError(
      "<h3>You cannot request "
        + qty
        + " items because only "
        + maxQty
        + " are proposed !<br></h3>", sp);
    return;
  }
  if (bid < minBid)
  {
    printError(
      "<h3>Your bid of $"
        + bid
        + " is not acceptable because it is below the $"
        + minBid
        + " minimum bid !<br></h3>", sp);
    return;
  }
  if (maxBid < minBid)
  {
    printError(
      "<h3>Your maximum bid of $"
        + maxBid
        + " is not acceptable because it is below the $"
        + minBid
        + " minimum bid !<br></h3>", sp);
    return;
  }
  if (maxBid < bid)
  {
    printError(
      "<h3>Your maximum bid of $"
        + maxBid
        + " is not acceptable because it is below your current bid of $"
        + bid
        + " !<br></h3>", sp);
    return;
  }
  try
  {
    conn = Database.getConnection();
    String now = TimeManagement.currentDateToString();
    stmt =
      conn.prepareStatement(
        //"INSERT INTO bids VALUES (NULL, \""
     	  "INSERT INTO bids(id,user_id,item_id,qty,bid,max_bid,date)" +
     	  	" VALUES ("+Config.BidIDFactory.addAndGet(Config.TotalProxies)+", \""
          + userId
          + "\", \""
          + itemId
          + "\", \""
          + qty
          + "\", \""
          + bid
          + "\", \""
          + maxBid
          + "\", \""
          + now
          + "\")");
    stmt.executeUpdate();
    stmt.close();
    // update the number of bids and the max bid for the item
    PreparedStatement update = null;
    try
    {
      stmt =
        conn.prepareStatement(
          "SELECT nb_of_bids, max_bid FROM items WHERE id=?");
      stmt.setInt(1, itemId.intValue());
      ResultSet rs = stmt.executeQuery();
      if (rs.first())
      {
        
        int nbOfBids = rs.getInt("nb_of_bids");
        nbOfBids++;
        float oldMaxBid = rs.getFloat("max_bid");
        if (bid > oldMaxBid)
        {
          oldMaxBid = bid;
          update =
            conn.prepareStatement(
              "UPDATE items SET max_bid=?, nb_of_bids=? WHERE id=?");
          update.setFloat(1, maxBid);
          update.setInt(2, nbOfBids);
          update.setInt(3, itemId.intValue());
          update.executeUpdate();
          update.close();
        }
        else
        {
          update =
            conn.prepareStatement("UPDATE items SET nb_of_bids=? WHERE id=?");
          update.setInt(1, nbOfBids);
          update.setInt(2, itemId.intValue());
          update.executeUpdate();
          update.close();
        }

      }
      else
      {
        Database.commit(conn);
        printError("Couldn't find the item.", sp);
        Database.closeConnection(stmt, conn);
        return;
      }
    }
    catch (Exception ex)
    {
  	Database.rollback(conn);
      printError("Failed to update nb of bids and max bid: " + ex, sp);
      if (update != null) 
        update.close();
      Database.closeConnection(stmt, conn);
      return;
    }
    sp.printHTMLheader("RUBiS: Bidding result");
    sp.printHTML(
      "<center><h2>Your bid has been successfully processed.</h2></center>\n");
    Database.commit(conn);
    Database.closeConnection(stmt, conn);
  }
  catch (Exception e)
  {
    sp.printHTML(
      "Error while storing the bid (got exception: " + e + ")<br>");
  	Database.rollback(conn);
      Database.closeConnection(stmt, conn);
    return;
  }
  sp.printHTMLfooter();
}

  /**
  * Clean up the connection pool.
  */
  public void destroy()
  {
    super.destroy();
  }

  public void destroy1()
  {
    super.destroy1();
  }
  
  public void destroy2()
  {
    super.destroy2();
  }
  
}
