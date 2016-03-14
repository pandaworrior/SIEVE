package edu.rice.rubis.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Add a new item in the database 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public class RegisterItem extends HttpServlet
{
  

//  public int getPoolSize()
//  {
//    return Config.RegisterItemPoolSize;
//  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: Register Item");
    sp.printHTML(
      "<h2>Your registration has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }

  /** Check the values from the html register item form and create a new item */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    String name = null, description = null;
    float initialPrice, buyNow, reservePrice;
    Float stringToFloat;
    int quantity, duration;
    Integer categoryId, userId, stringToInt;
    String startDate, endDate;
    int itemId;
    GregorianCalendar now, later;

    ServletPrinter sp = null;
    sp = new ServletPrinter(response, "RegisterItem");

    String value = request.getParameter("name");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a name!<br>", sp);
      return;
    }
    else
      name = value;

    value = request.getParameter("description");
    if ((value == null) || (value.equals("")))
    {
      description = "No description.";
    }
    else
      description = value;

    value = request.getParameter("initialPrice");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide an initial price!<br>", sp);
      return;
    }
    else
    {
      stringToFloat = new Float(value);
      initialPrice = stringToFloat.floatValue();
    }

    value = request.getParameter("reservePrice");
    if ((value == null) || (value.equals("")))
    {
      reservePrice = 0;
    }
    else
    {
      stringToFloat = new Float(value);
      reservePrice = stringToFloat.floatValue();

    }

    value = request.getParameter("buyNow");
    if ((value == null) || (value.equals("")))
    {
      buyNow = 0;
    }
    else
    {
      stringToFloat = new Float(value);
      buyNow = stringToFloat.floatValue();
    }

    value = request.getParameter("duration");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a duration!<br>", sp);
      return;
    }
    else
    {
      stringToInt = new Integer(value);
      duration = stringToInt.intValue();
      now = new GregorianCalendar();
      //later = TimeManagement.addMinutes(now, duration);
      later = TimeManagement.addDays(now, duration);
      startDate = TimeManagement.dateToString(now);
      endDate = TimeManagement.dateToString(later);
    }

    value = request.getParameter("quantity");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a quantity!<br>", sp);
      return;
    }
    else
    {
      stringToInt = new Integer(value);
      quantity = stringToInt.intValue();
    }

    userId = new Integer(request.getParameter("userId"));
    categoryId = new Integer(request.getParameter("categoryId"));

     PreparedStatement stmt = null;
     Connection conn = null;
     String str="";
    try
    {
      
      conn = Database.getConnection();
      //conn.setAutoCommit(false); // faster if made inside a Tx
      
      // Try to create a new item
      try
      {
    	itemId = Config.ItemIDFactory.addAndGet(Config.TotalProxies);
        str =
        	 "INSERT INTO items" +
        	 "(id,name,description,initial_price,quantity,reserve_price," +
        	 "buy_now,nb_of_bids,max_bid,start_date,end_date,seller,category) " +
        	 "VALUES ("+itemId+", \""
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
              + userId
              + "\", \""
              + categoryId+"\")";
        stmt = conn.prepareStatement(str);

        stmt.executeUpdate();
        stmt.close();
      }
      catch (SQLException e)
      {
        Database.rollback(conn);
        printError(
          "RUBiS internal error: Item registration failed (got exception: "
            + e
            + ")<br>\n"+str+"<br>", sp);
        Database.closeConnection(stmt, conn);
        return;
      }
      // To test if the item was correctly added in the database
      try
      {
        stmt = conn.prepareStatement("SELECT id FROM items WHERE id = ? AND name=?");
        stmt.setInt(1, itemId);
        stmt.setString(2, name);
        ResultSet irs = stmt.executeQuery();
        if (!irs.first() || itemId != irs.getInt("id"))
        {
          Database.commit(conn);
          printError("This item does not exist in the database.", sp);
          Database.closeConnection(stmt, conn);
          return;
        }
        
      }
      catch (SQLException e)
      {
    	Database.rollback(conn);
        printError("Failed to execute Query for the new item: " + e, sp);
        Database.closeConnection(stmt, conn);
        return;
      }
      sp.printHTMLheader("RUBiS: Item to sell " + name);
      sp.printHTML("<h2>Your Item has been successfully registered.</h2><br>");
      sp.printHTML(
        "RUBiS has stored the following information about your item:<br>");
      sp.printHTML("Name         : " + name + "<br>");
      sp.printHTML("Description  : " + description + "<br>");
      sp.printHTML("Initial price: " + initialPrice + "<br>");
      sp.printHTML("ReservePrice : " + reservePrice + "<br>");
      sp.printHTML("Buy Now      : " + buyNow + "<br>");
      sp.printHTML("Quantity     : " + quantity + "<br>");
      sp.printHTML("User id      :" + userId + "<br>");
      sp.printHTML("Category id  :" + categoryId + "<br>");
      sp.printHTML("Duration     : " + duration + "<br>");
      sp.printHTML(
        "<br>The following information has been automatically generated by RUBiS:<br>");
      sp.printHTML("Start date   :" + startDate + "<br>");
      sp.printHTML("End date     :" + endDate + "<br>");
      sp.printHTML("item id      :" + itemId + "<br>");

      Database.commit(conn);
      Database.closeConnection(stmt, conn);
      //AuctionManager.scheduleCloseAuction(later, itemId);
      sp.printHTMLfooter();
    }
    catch (Exception e)
    {
      sp.printHTML("Exception getting comment list: " + e + "<br>");
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
    }
  }

  /** 
   *	Call the doGet method: check the values from the html register item form 
   *	and create a new item 
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doGet(request, response);
  }

  /**
   * Clean up the connection pool.
   */
  public void destroy()
  {
    super.destroy();
  }
}
