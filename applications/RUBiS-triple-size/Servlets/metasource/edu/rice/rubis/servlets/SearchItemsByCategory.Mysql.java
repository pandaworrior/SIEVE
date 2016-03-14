package edu.rice.rubis.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** This servlets displays a list of items belonging to a specific category.
 * It must be called this way :
 * <pre>
 * http://..../SearchItemsByCategory?category=xx&categoryName=yy 
 *    where xx is the category id
 *      and yy is the category name
 * /<pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class SearchItemsByCategory extends HttpServlet
{


//  public int getPoolSize()
//  {
//    return Config.SearchItemsByCategoryPoolSize;
//  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: Search Items By Category");
    sp.printHTML(
      "<h2>We cannot process your request due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }
  
  private void printError1(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: Search Items By Category");
    sp.printHTML(
      "<h2>We cannot process your request due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }
  
  private void printError2(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: Search Items By Category");
    sp.printHTML(
      "<h2>We cannot process your request due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
    
  }

  private void itemList(
    Integer categoryId,
    String categoryName,
    int page,
    int nbOfItems,
    ServletPrinter sp)
  {
    
    PreparedStatement stmt = null;
    Connection conn = null;
    
    String itemName, endDate;
    int itemId;
    float maxBid;
    int nbOfBids = 0;
    ResultSet rs = null;

    // get the list of items
    try
    {
      conn = Database.getConnection();
      //conn.setAutoCommit(false);

      String now = TimeManagement.currentDateToString();
      stmt =
          conn.prepareStatement(
            "SELECT items.name, items.id, items.end_date, items.max_bid, items.nb_of_bids, items.initial_price FROM items WHERE items.category=? AND end_date>= '"+now+"' ORDER BY items.end_date ASC LIMIT ?,?");
      stmt.setInt(1, categoryId.intValue());
      stmt.setInt(2, page * nbOfItems);
      stmt.setInt(3, nbOfItems);
      rs = stmt.executeQuery();
    }
    catch (Exception e)
    {
      sp.printHTML("Failed to executeQuery for item: " + e);
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
      return;
    }
    try
    {
      if (!rs.first())
      {
        if (page == 0)
        {
          sp.printHTML(
            "<h2>Sorry, but there are no items available in this category !</h2>");
        }
        else
        {
          sp.printHTML(
            "<h2>Sorry, but there are no more items available in this category !</h2>");
          sp.printItemHeader();
          sp.printItemFooter(
            "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
              + categoryId
              + "&categoryName="
              + URLEncoder.encode(categoryName,"UTF-8")
              + "&page="
              + (page - 1)
              + "&nbOfItems="
              + nbOfItems
              + "\">Previous page</a>",
            "");
        }
        Database.commit(conn);
        Database.closeConnection(stmt, conn);
        return;
      }

      sp.printItemHeader();
      do
      {
        itemName = rs.getString("name");
        itemId = rs.getInt("id");
        endDate = rs.getString("end_date");
        maxBid = rs.getFloat("max_bid");
        nbOfBids = rs.getInt("nb_of_bids");
        float initialPrice = rs.getFloat("initial_price");
        if (maxBid < initialPrice)
          maxBid = initialPrice;
        sp.printItem(itemName, itemId, maxBid, nbOfBids, endDate);
      }
      while (rs.next());
      if (page == 0)
      {
        sp.printItemFooter(
          "",
          "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
            + categoryId
            + "&categoryName="
            + URLEncoder.encode(categoryName,"UTF-8")
            + "&page="
            + (page + 1)
            + "&nbOfItems="
            + nbOfItems
            + "\">Next page</a>");
      }
      else
      {
        sp.printItemFooter(
          "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
            + categoryId
            + "&categoryName="
            + URLEncoder.encode(categoryName,"UTF-8")
            + "&page="
            + (page - 1)
            + "&nbOfItems="
            + nbOfItems
            + "\">Previous page</a>",
          "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
            + categoryId
            + "&categoryName="
            + URLEncoder.encode(categoryName,"UTF-8")
            + "&page="
            + (page + 1)
            + "&nbOfItems="
            + nbOfItems
            + "\">Next page</a>");
      }
      //conn.commit();
      Database.commit(conn);
      Database.closeConnection(stmt, conn);
    }
    catch (Exception e)
    {
      printError("Exception getting item list: " + e + "<br>", sp);
      //       try
      //       {
      //         conn.rollback();
      //       }
      //       catch (Exception se) 
      //       {
      //         printError("Transaction rollback failed: " + e +"<br>");
      //       }
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
    }
  }
  
  private void itemList1(
		    Integer categoryId,
		    String categoryName,
		    int page,
		    int nbOfItems,
		    ServletPrinter sp)
		  {
		    
		    PreparedStatement stmt = null;
		    Connection conn = null;
		    
		    String itemName, endDate;
		    int itemId;
		    float maxBid;
		    int nbOfBids = 0;
		    ResultSet rs = null;

		    // get the list of items
		    try
		    {
		      conn = Database.getConnection();
		      //conn.setAutoCommit(false);

		      String now = TimeManagement.currentDateToString();
		      stmt =
		          conn.prepareStatement(
		            "SELECT items.name, items.id, items.end_date, items.max_bid, items.nb_of_bids, items.initial_price FROM items WHERE items.category=? AND end_date>= '"+now+"' ORDER BY items.end_date ASC LIMIT ?,?");
		      stmt.setInt(1, categoryId.intValue());
		      stmt.setInt(2, page * nbOfItems);
		      stmt.setInt(3, nbOfItems);
		      rs = stmt.executeQuery();
		    }
		    catch (Exception e)
		    {
		      sp.printHTML("Failed to executeQuery for item: " + e);
		      Database.rollback(conn);
		      Database.closeConnection(stmt, conn);
		      return;
		    }
		    try
		    {
		      if (!rs.first())
		      {
		        if (page == 0)
		        {
		          sp.printHTML(
		            "<h2>Sorry, but there are no items available in this category !</h2>");
		        }
		        else
		        {
		          sp.printHTML(
		            "<h2>Sorry, but there are no more items available in this category !</h2>");
		          sp.printItemHeader();
		          sp.printItemFooter(
		            "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
		              + categoryId
		              + "&categoryName="
		              + URLEncoder.encode(categoryName,"UTF-8")
		              + "&page="
		              + (page - 1)
		              + "&nbOfItems="
		              + nbOfItems
		              + "\">Previous page</a>",
		            "");
		        }
		        Database.commit(conn);
		        Database.closeConnection(stmt, conn);
		        return;
		      }

		      sp.printItemHeader();
		      do
		      {
		        itemName = rs.getString("name");
		        itemId = rs.getInt("id");
		        endDate = rs.getString("end_date");
		        maxBid = rs.getFloat("max_bid");
		        nbOfBids = rs.getInt("nb_of_bids");
		        float initialPrice = rs.getFloat("initial_price");
		        if (maxBid < initialPrice)
		          maxBid = initialPrice;
		        sp.printItem(itemName, itemId, maxBid, nbOfBids, endDate);
		      }
		      while (rs.next());
		      if (page == 0)
		      {
		        sp.printItemFooter(
		          "",
		          "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
		            + categoryId
		            + "&categoryName="
		            + URLEncoder.encode(categoryName,"UTF-8")
		            + "&page="
		            + (page + 1)
		            + "&nbOfItems="
		            + nbOfItems
		            + "\">Next page</a>");
		      }
		      else
		      {
		        sp.printItemFooter(
		          "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
		            + categoryId
		            + "&categoryName="
		            + URLEncoder.encode(categoryName,"UTF-8")
		            + "&page="
		            + (page - 1)
		            + "&nbOfItems="
		            + nbOfItems
		            + "\">Previous page</a>",
		          "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
		            + categoryId
		            + "&categoryName="
		            + URLEncoder.encode(categoryName,"UTF-8")
		            + "&page="
		            + (page + 1)
		            + "&nbOfItems="
		            + nbOfItems
		            + "\">Next page</a>");
		      }
		      //conn.commit();
		      Database.commit(conn);
		      Database.closeConnection(stmt, conn);
		    }
		    catch (Exception e)
		    {
		      printError("Exception getting item list: " + e + "<br>", sp);
		      //       try
		      //       {
		      //         conn.rollback();
		      //       }
		      //       catch (Exception se) 
		      //       {
		      //         printError("Transaction rollback failed: " + e +"<br>");
		      //       }
		      Database.rollback(conn);
		      Database.closeConnection(stmt, conn);
		    }
		  }
  
  private void itemList2(
		    Integer categoryId,
		    String categoryName,
		    int page,
		    int nbOfItems,
		    ServletPrinter sp)
		  {
		    
		    PreparedStatement stmt = null;
		    Connection conn = null;
		    
		    String itemName, endDate;
		    int itemId;
		    float maxBid;
		    int nbOfBids = 0;
		    ResultSet rs = null;

		    // get the list of items
		    try
		    {
		      conn = Database.getConnection();
		      //conn.setAutoCommit(false);

		      String now = TimeManagement.currentDateToString();
		      stmt =
		          conn.prepareStatement(
		            "SELECT items.name, items.id, items.end_date, items.max_bid, items.nb_of_bids, items.initial_price FROM items WHERE items.category=? AND end_date>= '"+now+"' ORDER BY items.end_date ASC LIMIT ?,?");
		      stmt.setInt(1, categoryId.intValue());
		      stmt.setInt(2, page * nbOfItems);
		      stmt.setInt(3, nbOfItems);
		      rs = stmt.executeQuery();
		    }
		    catch (Exception e)
		    {
		      sp.printHTML("Failed to executeQuery for item: " + e);
		      Database.rollback(conn);
		      Database.closeConnection(stmt, conn);
		      return;
		    }
		    try
		    {
		      if (!rs.first())
		      {
		        if (page == 0)
		        {
		          sp.printHTML(
		            "<h2>Sorry, but there are no items available in this category !</h2>");
		        }
		        else
		        {
		          sp.printHTML(
		            "<h2>Sorry, but there are no more items available in this category !</h2>");
		          sp.printItemHeader();
		          sp.printItemFooter(
		            "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
		              + categoryId
		              + "&categoryName="
		              + URLEncoder.encode(categoryName,"UTF-8")
		              + "&page="
		              + (page - 1)
		              + "&nbOfItems="
		              + nbOfItems
		              + "\">Previous page</a>",
		            "");
		        }
		        Database.commit(conn);
		        Database.closeConnection(stmt, conn);
		        return;
		      }

		      sp.printItemHeader();
		      do
		      {
		        itemName = rs.getString("name");
		        itemId = rs.getInt("id");
		        endDate = rs.getString("end_date");
		        maxBid = rs.getFloat("max_bid");
		        nbOfBids = rs.getInt("nb_of_bids");
		        float initialPrice = rs.getFloat("initial_price");
		        if (maxBid < initialPrice)
		          maxBid = initialPrice;
		        sp.printItem(itemName, itemId, maxBid, nbOfBids, endDate);
		      }
		      while (rs.next());
		      if (page == 0)
		      {
		        sp.printItemFooter(
		          "",
		          "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
		            + categoryId
		            + "&categoryName="
		            + URLEncoder.encode(categoryName,"UTF-8")
		            + "&page="
		            + (page + 1)
		            + "&nbOfItems="
		            + nbOfItems
		            + "\">Next page</a>");
		      }
		      else
		      {
		        sp.printItemFooter(
		          "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
		            + categoryId
		            + "&categoryName="
		            + URLEncoder.encode(categoryName,"UTF-8")
		            + "&page="
		            + (page - 1)
		            + "&nbOfItems="
		            + nbOfItems
		            + "\">Previous page</a>",
		          "<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.SearchItemsByCategory?category="
		            + categoryId
		            + "&categoryName="
		            + URLEncoder.encode(categoryName,"UTF-8")
		            + "&page="
		            + (page + 1)
		            + "&nbOfItems="
		            + nbOfItems
		            + "\">Next page</a>");
		      }
		      //conn.commit();
		      Database.commit(conn);
		      Database.closeConnection(stmt, conn);
		    }
		    catch (Exception e)
		    {
		      printError("Exception getting item list: " + e + "<br>", sp);
		      //       try
		      //       {
		      //         conn.rollback();
		      //       }
		      //       catch (Exception se) 
		      //       {
		      //         printError("Transaction rollback failed: " + e +"<br>");
		      //       }
		      Database.rollback(conn);
		      Database.closeConnection(stmt, conn);
		    }
		  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Integer page;
    Integer nbOfItems;
    String value = request.getParameter("category");
    ;
    Integer categoryId;
    String categoryName = request.getParameter("categoryName");

    ServletPrinter sp = null;
    sp = new ServletPrinter(response, "SearchItemsByCategory");

    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a category identifier!<br>", sp);
      return;
    }
    else
      categoryId = new Integer(value);

    value = request.getParameter("page");
    if ((value == null) || (value.equals("")))
      page = new Integer(0);
    else
      page = new Integer(value);

    value = request.getParameter("nbOfItems");
    if ((value == null) || (value.equals("")))
      nbOfItems = new Integer(25);
    else
      nbOfItems = new Integer(value);

    if (categoryName == null)
    {
      sp.printHTMLheader("RUBiS: Missing category name");
      sp.printHTML("<h2>Items in this category</h2><br><br>");
    }
    else
    {
      sp.printHTMLheader("RUBiS: Items in category " + categoryName);
      sp.printHTML("<h2>Items in category " + categoryName + "</h2><br><br>");
    }

    itemList(categoryId, categoryName, page.intValue(), nbOfItems.intValue(), sp);
    sp.printHTMLfooter();
  }
  
  public void doGet1(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
{
  Integer page;
  Integer nbOfItems;
  String value = request.getParameter("category");
  ;
  Integer categoryId;
  String categoryName = request.getParameter("categoryName");

  ServletPrinter sp = null;
  sp = new ServletPrinter(response, "SearchItemsByCategory");

  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a category identifier!<br>", sp);
    return;
  }
  else
    categoryId = new Integer(value);

  value = request.getParameter("page");
  if ((value == null) || (value.equals("")))
    page = new Integer(0);
  else
    page = new Integer(value);

  value = request.getParameter("nbOfItems");
  if ((value == null) || (value.equals("")))
    nbOfItems = new Integer(25);
  else
    nbOfItems = new Integer(value);

  if (categoryName == null)
  {
    sp.printHTMLheader("RUBiS: Missing category name");
    sp.printHTML("<h2>Items in this category</h2><br><br>");
  }
  else
  {
    sp.printHTMLheader("RUBiS: Items in category " + categoryName);
    sp.printHTML("<h2>Items in category " + categoryName + "</h2><br><br>");
  }

  itemList1(categoryId, categoryName, page.intValue(), nbOfItems.intValue(), sp);
  sp.printHTMLfooter();
}
  
  public void doGet2(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
{
  Integer page;
  Integer nbOfItems;
  String value = request.getParameter("category");
  ;
  Integer categoryId;
  String categoryName = request.getParameter("categoryName");

  ServletPrinter sp = null;
  sp = new ServletPrinter(response, "SearchItemsByCategory");

  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a category identifier!<br>", sp);
    return;
  }
  else
    categoryId = new Integer(value);

  value = request.getParameter("page");
  if ((value == null) || (value.equals("")))
    page = new Integer(0);
  else
    page = new Integer(value);

  value = request.getParameter("nbOfItems");
  if ((value == null) || (value.equals("")))
    nbOfItems = new Integer(25);
  else
    nbOfItems = new Integer(value);

  if (categoryName == null)
  {
    sp.printHTMLheader("RUBiS: Missing category name");
    sp.printHTML("<h2>Items in this category</h2><br><br>");
  }
  else
  {
    sp.printHTMLheader("RUBiS: Items in category " + categoryName);
    sp.printHTML("<h2>Items in category " + categoryName + "</h2><br><br>");
  }

  itemList2(categoryId, categoryName, page.intValue(), nbOfItems.intValue(), sp);
  sp.printHTMLfooter();
}

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doGet(request, response);
  }
  
  public void doPost1(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
{
  doGet1(request, response);
}
  
  public void doPost2(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
{
  doGet2(request, response);
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
