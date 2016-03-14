
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

/**
 * This servlets displays general information about a user. It must be called
 * this way :
 * 
 * <pre>
 * 
 *  http://..../ViewUserInfo?userId=xx where xx is the id of the user
 *  
 * </pre>
 */

public class ViewUserInfo extends HttpServlet
{

//  public int getPoolSize()
//  {
//    return Config.ViewUserInfoPoolSize;
//  }

  private boolean commentList(Integer userId, PreparedStatement stmt,
      Connection conn, ServletPrinter sp)
  {
    ResultSet rs = null;
    String date, comment;
    int authorId;

    try
    {
      //conn.setAutoCommit(false); // faster if made inside a Tx

      // Try to find the comment corresponding to the user
      try
      {
        stmt = conn
            .prepareStatement("SELECT * FROM comments WHERE to_user_id=?");
        stmt.setInt(1, userId.intValue());
        rs = stmt.executeQuery();
      }
      catch (Exception e)
      {
        sp.printHTML("Failed to execute Query for list of comments: " + e);
        Database.rollback(conn);
        Database.closeConnection(stmt, conn);
        return false;
      }
      if (!rs.first())
      {
        sp.printHTML("<h3>There is no comment yet for this user.</h3><br>");
        Database.commit(conn);
        Database.closeConnection(stmt, conn);
        return false;
      }
      sp.printHTML("<br><hr><br><h3>Comments for this user</h3><br>");

      sp.printCommentHeader();
      // Display each comment and the name of its author
      do
      {
        comment = rs.getString("comment");
        date = rs.getString("date");
        authorId = rs.getInt("from_user_id");

        String authorName = "none";
        ResultSet authorRS = null;
        PreparedStatement authorStmt = null;
        try
        {
          authorStmt = conn
              .prepareStatement("SELECT nickname FROM users WHERE id=?");
          authorStmt.setInt(1, authorId);
          authorRS = authorStmt.executeQuery();
          if (authorRS.first())
            authorName = authorRS.getString("nickname");
          authorStmt.close();
        }
        catch (Exception e)
        {
          sp.printHTML("Failed to execute Query for the comment author: " + e);
          Database.rollback(conn);
          authorStmt.close();
          Database.closeConnection(stmt, conn);
          return false;
        }
        sp.printComment(authorName, authorId, date, comment);
      }
      while (rs.next());
      sp.printCommentFooter();
      Database.commit(conn);
    }
    catch (Exception e)
    {
      sp.printHTML("Exception getting comment list: " + e + "<br>");
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
      return false;
    }
    return true;
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
  {
    doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
  {
    String value = request.getParameter("userId");
    Integer userId;
    ResultSet rs = null;
    ServletPrinter sp = null;
    PreparedStatement stmt = null;
    Connection conn = null;

    sp = new ServletPrinter(response, "ViewUserInfo");

    if ((value == null) || (value.equals("")))
    {
      sp.printHTMLheader("RUBiS ERROR: View user information");
      sp.printHTML("<h3>You must provide a user identifier !<br></h3>");
      sp.printHTMLfooter();
      return;
    }
    else
      userId = new Integer(value);

    sp.printHTMLheader("RUBiS: View user information");

    // Try to find the user corresponding to the userId
    try
    {
      conn = Database.getConnection();
      stmt = conn.prepareStatement("SELECT * FROM users WHERE id=?");
      stmt.setInt(1, userId.intValue());
      rs = stmt.executeQuery();
    }
    catch (Exception e)
    {
      sp.printHTML("Failed to execute Query for user: " + e);
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
      sp.printHTMLfooter();
      return;
    }
    try
    {
      if (!rs.first())
      {
        sp.printHTML("<h2>This user does not exist!</h2>");
        Database.commit(conn);
        Database.closeConnection(stmt, conn);
        sp.printHTMLfooter();
        return;
      }
      String firstname = rs.getString("firstname");
      String lastname = rs.getString("lastname");
      String nickname = rs.getString("nickname");
      String email = rs.getString("email");
      String date = rs.getString("creation_date");
      int rating = rs.getInt("rating");
      stmt.close();

      String result = new String();

      result = result + "<h2>Information about " + nickname + "<br></h2>";
      result = result + "Real life name : " + firstname + " " + lastname
          + "<br>";
      result = result + "Email address  : " + email + "<br>";
      result = result + "User since     : " + date + "<br>";
      result = result + "Current rating : <b>" + rating + "</b><br>";
      sp.printHTML(result);

    }
    catch (SQLException s)
    {
      sp.printHTML("Failed to get general information about the user: " + s);
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
      sp.printHTMLfooter();
      return;
    }
    boolean connAlive = commentList(userId, stmt, conn, sp);
    sp.printHTMLfooter();
    // connAlive means we must close it. Otherwise we must NOT do a
    // double free
    if(connAlive) {
    	Database.commit(conn);
        Database.closeConnection(stmt, conn);
    }
  }

  /**
   * Clean up the connection pool.
   */
  public void destroy()
  {
    super.destroy();
  }

}
