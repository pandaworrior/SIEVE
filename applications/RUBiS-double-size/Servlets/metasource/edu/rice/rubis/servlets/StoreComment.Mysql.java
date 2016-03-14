package edu.rice.rubis.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** This servlets records a comment in the database and display
 * the result of the transaction.
 * It must be called this way :
 * <pre>
 * http://..../StoreComment?itemId=aa&userId=bb&minComment=cc&maxQty=dd&comment=ee&maxComment=ff&qty=gg 
 *   where: aa is the item id 
 *          bb is the user id
 *          cc is the minimum acceptable comment for this item
 *          dd is the maximum quantity available for this item
 *          ee is the user comment
 *          ff is the maximum comment the user wants
 *          gg is the quantity asked by the user
 * /<pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class StoreComment extends HttpServlet
{


//  public int getPoolSize()
//  {
//    return Config.StoreCommentPoolSize;
//  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: StoreComment");
    sp.printHTML(
      "<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
   
  }
  
  private void printError1(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: StoreComment");
    sp.printHTML(
      "<h2>Your request has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();
   
  }

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

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    Integer toId; // to user id
    Integer fromId; // from user id
    Integer itemId; // item id
    String comment; // user comment
    Integer rating; // user rating
    ServletPrinter sp = null;
    PreparedStatement stmt = null;
    Connection conn = null;

    sp = new ServletPrinter(response, "StoreComment");

    /* Get and check all parameters */

    String value = request.getParameter("to");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a 'to user' identifier !<br></h3>", sp);
      return;
    }
    else
      toId = new Integer(value);

    value = request.getParameter("from");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a 'from user' identifier !<br></h3>", sp);
      return;
    }
    else
      fromId = new Integer(value);

    value = request.getParameter("itemId");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide an item identifier !<br></h3>", sp);
      return;
    }
    else
      itemId = new Integer(value);

    value = request.getParameter("rating");
    if ((value == null) || (value.equals("")))
    {
      printError("<h3>You must provide a rating !<br></h3>", sp);
      return;
    }
    else
      rating = new Integer(value);

    comment = request.getParameter("comment");
    if ((comment == null) || (comment.equals("")))
    {
      printError("<h3>You must provide a comment !<br></h3>", sp);
      return;
    }

    try
    {
      conn = Database.getConnection();
      //conn.setAutoCommit(false); // faster if made inside a Tx
      // Try to create a new comment
      try
      {
        String now = TimeManagement.currentDateToString();
        stmt =
          conn.prepareStatement(
            //"INSERT INTO comments VALUES (NULL, \""
            "INSERT INTO comments(id,from_user_id,to_user_id,item_id,rating,date,comment)" +
            " VALUES ("+Config.CommentIDFactory.addAndGet(Config.TotalProxies)+", \""  
              + fromId
              + "\", \""
              + toId
              + "\", \""
              + itemId
              + "\", \""
              + rating
              + "\", \""
              + now
              + "\",\""
              + comment
              + "\")");

        stmt.executeUpdate();
        stmt.close();
      }
      catch (SQLException e)
      {
    	Database.rollback(conn);
        printError(
          "Error while storing the comment (got exception: " + e + ")<br>", sp);
         Database.closeConnection(stmt, conn);
        return;
      }
      // Try to find the user corresponding to the 'to' ID
      try
      {
        ResultSet urs;
        stmt = conn.prepareStatement("SELECT rating FROM users WHERE id=?");
        stmt.setInt(1, toId.intValue());
        urs = stmt.executeQuery();
        if (urs.first())
        {
          int userRating = urs.getInt("rating");
          userRating = userRating + rating.intValue();

          stmt = conn.prepareStatement("UPDATE users SET rating=? WHERE id=?");
          stmt.setInt(1, userRating);
          stmt.setInt(2, toId.intValue());
          stmt.executeUpdate();
        }
      }
      catch (SQLException e)
      {
    	Database.rollback(conn);
        printError(
          "Error while updating user's rating (got exception: " + e + ")<br>", sp);
         Database.closeConnection(stmt, conn);
        return;
      }
      sp.printHTMLheader("RUBiS: Comment posting");
      sp.printHTML(
        "<center><h2>Your comment has been successfully posted.</h2></center>");

      sp.printHTMLfooter();
      Database.commit(conn);
      Database.closeConnection(stmt, conn);
    }
    catch (Exception e)
    {
      sp.printHTML("Exception getting comment list: " + e + "<br>");
      try
      {
    	Database.rollback(conn);
        Database.closeConnection(stmt, conn);
      }
      catch (Exception se)
      {
        sp.printHTML("Transaction rollback failed: " + e + "<br>");
        Database.rollback(conn);
        Database.closeConnection(stmt, conn);
      }
    }
  }
  
  public void doPost1(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
{
  Integer toId; // to user id
  Integer fromId; // from user id
  Integer itemId; // item id
  String comment; // user comment
  Integer rating; // user rating
  ServletPrinter sp = null;
  PreparedStatement stmt = null;
  Connection conn = null;

  sp = new ServletPrinter(response, "StoreComment");

  /* Get and check all parameters */

  String value = request.getParameter("to");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a 'to user' identifier !<br></h3>", sp);
    return;
  }
  else
    toId = new Integer(value);

  value = request.getParameter("from");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a 'from user' identifier !<br></h3>", sp);
    return;
  }
  else
    fromId = new Integer(value);

  value = request.getParameter("itemId");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide an item identifier !<br></h3>", sp);
    return;
  }
  else
    itemId = new Integer(value);

  value = request.getParameter("rating");
  if ((value == null) || (value.equals("")))
  {
    printError("<h3>You must provide a rating !<br></h3>", sp);
    return;
  }
  else
    rating = new Integer(value);

  comment = request.getParameter("comment");
  if ((comment == null) || (comment.equals("")))
  {
    printError("<h3>You must provide a comment !<br></h3>", sp);
    return;
  }

  try
  {
    conn = Database.getConnection();
    //conn.setAutoCommit(false); // faster if made inside a Tx
    // Try to create a new comment
    try
    {
      String now = TimeManagement.currentDateToString();
      stmt =
        conn.prepareStatement(
          //"INSERT INTO comments VALUES (NULL, \""
          "INSERT INTO comments(id,from_user_id,to_user_id,item_id,rating,date,comment)" +
          " VALUES ("+Config.CommentIDFactory.addAndGet(Config.TotalProxies)+", \""  
            + fromId
            + "\", \""
            + toId
            + "\", \""
            + itemId
            + "\", \""
            + rating
            + "\", \""
            + now
            + "\",\""
            + comment
            + "\")");

      stmt.executeUpdate();
      stmt.close();
    }
    catch (SQLException e)
    {
  	Database.rollback(conn);
      printError(
        "Error while storing the comment (got exception: " + e + ")<br>", sp);
       Database.closeConnection(stmt, conn);
      return;
    }
    // Try to find the user corresponding to the 'to' ID
    try
    {
      ResultSet urs;
      stmt = conn.prepareStatement("SELECT rating FROM users WHERE id=?");
      stmt.setInt(1, toId.intValue());
      urs = stmt.executeQuery();
      if (urs.first())
      {
        int userRating = urs.getInt("rating");
        userRating = userRating + rating.intValue();

        stmt = conn.prepareStatement("UPDATE users SET rating=? WHERE id=?");
        stmt.setInt(1, userRating);
        stmt.setInt(2, toId.intValue());
        stmt.executeUpdate();
      }
    }
    catch (SQLException e)
    {
  	Database.rollback(conn);
      printError(
        "Error while updating user's rating (got exception: " + e + ")<br>", sp);
       Database.closeConnection(stmt, conn);
      return;
    }
    sp.printHTMLheader("RUBiS: Comment posting");
    sp.printHTML(
      "<center><h2>Your comment has been successfully posted.</h2></center>");

    sp.printHTMLfooter();
    Database.commit(conn);
    Database.closeConnection(stmt, conn);
  }
  catch (Exception e)
  {
    sp.printHTML("Exception getting comment list: " + e + "<br>");
    try
    {
  	Database.rollback(conn);
      Database.closeConnection(stmt, conn);
    }
    catch (Exception se)
    {
      sp.printHTML("Transaction rollback failed: " + e + "<br>");
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
    }
  }
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
}
