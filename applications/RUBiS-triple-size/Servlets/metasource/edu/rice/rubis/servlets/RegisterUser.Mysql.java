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
//import javax.transaction.UserTransaction;

/** 
 * Add a new user in the database 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */
public class RegisterUser extends HttpServlet
{
  //private UserTransaction utx = null;
  

//  public int getPoolSize()
//  {
//    return Config.RegisterUserPoolSize;
//  }

/**
 * Display an error message.
 * @param errorMsg the error message value
 */
  private void printError(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: Register user");
    sp.printHTML(
      "<h2>Your registration has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();


  }
  
  private void printError1(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: Register user");
    sp.printHTML(
      "<h2>Your registration has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();


  }
  
  private void printError2(String errorMsg, ServletPrinter sp)
  {
    sp.printHTMLheader("RUBiS ERROR: Register user");
    sp.printHTML(
      "<h2>Your registration has not been processed due to the following error :</h2><br>");
    sp.printHTML(errorMsg);
    sp.printHTMLfooter();


  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    PreparedStatement stmt = null;
    Connection conn = null;
    
    String firstname = null,
      lastname = null,
      nickname = null,
      email = null,
      password = null;
    int regionId;
    int userId;
    String creationDate, region;

    ServletPrinter sp = null;
    sp = new ServletPrinter(response, "RegisterUser");

    String value = request.getParameter("firstname");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a first name!<br>", sp);
      return;
    }
    else
      firstname = value;

    value = request.getParameter("lastname");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a last name!<br>", sp);
      return;
    }
    else
      lastname = value;

    value = request.getParameter("nickname");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a nick name!<br>", sp);
      return;
    }
    else
      nickname = value;

    value = request.getParameter("email");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide an email address!<br>", sp);
      return;
    }
    else
      email = value;

    value = request.getParameter("password");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a password!<br>", sp);
      return;
    }
    else
      password = value;

    value = request.getParameter("region");
    if ((value == null) || (value.equals("")))
    {
      printError("You must provide a valid region!<br>", sp);
      return;
    }
    else
    {
      region = value;
      
      try
      {
        conn = Database.getConnection();
        stmt = conn.prepareStatement("SELECT id FROM regions WHERE name=?");
        stmt.setString(1, region);
        ResultSet rs = stmt.executeQuery();
        if (!rs.first())
        {
          printError(
            " Region " + value + " does not exist in the database!<br>", sp);
         Database.commit(conn);
         Database.closeConnection(stmt, conn);
          return;
        }
        regionId = rs.getInt("id");
        stmt.close();
      }
      catch (SQLException e)
      {
        printError("Failed to execute Query for region: " + e, sp);
        Database.rollback(conn);
        Database.closeConnection(stmt, conn);
        return;
      }
    }
    // Try to create a new user
    try
    {
      stmt =
        conn.prepareStatement("SELECT nickname FROM users WHERE nickname=?");
      stmt.setString(1, nickname);
      ResultSet rs = stmt.executeQuery();
      if (rs.first())
      {
        printError("The nickname you have choosen is already taken by someone else. Please choose a new nickname.<br>", sp);
        Database.commit(conn);
        Database.closeConnection(stmt, conn);
        return;
      }
      stmt.close();
    }
    catch (SQLException e)
    {
      printError("Failed to execute Query to check the nickname: " + e, sp);
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
      return;
    }
    try
    {
      String now = TimeManagement.currentDateToString();
      stmt =
        conn.prepareStatement(
          //"INSERT INTO users VALUES (NULL, \""
          "INSERT INTO " +
          	"users(id,firstname,lastname,nickname,password,email,rating,balance,creation_date,region) " +
          	"VALUES ("+Config.UserIDFactory.addAndGet(Config.TotalProxies)+", \""
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
            + regionId
            + ")");
      stmt.executeUpdate();
      stmt.close();
    }
    catch (SQLException e)
    {
      printError(
        "RUBiS internal error: User registration failed (got exception: "
          + e
          + ")<br>", sp);
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
      return;
    }
    try
    {
      stmt =
        conn.prepareStatement(
          "SELECT id, creation_date FROM users WHERE nickname=?");
      stmt.setString(1, nickname);
      ResultSet urs = stmt.executeQuery();
      if (!urs.first())
      {
        printError("This user does not exist in the database.", sp);
        Database.commit(conn);
        Database.closeConnection(stmt, conn);
        return;
      }
      userId = urs.getInt("id");
      creationDate = urs.getString("creation_date");
    }
    catch (SQLException e)
    {
      printError("Failed to execute Query for user: " + e, sp);
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
      return;
    }

    Database.commit(conn);
    Database.closeConnection(stmt, conn);
    
    sp.printHTMLheader("RUBiS: Welcome to " + nickname);
    sp.printHTML(
      "<h2>Your registration has been processed successfully</h2><br>");
    sp.printHTML("<h3>Welcome " + nickname + "</h3>");
    sp.printHTML("RUBiS has stored the following information about you:<br>");
    sp.printHTML("First Name : " + firstname + "<br>");
    sp.printHTML("Last Name  : " + lastname + "<br>");
    sp.printHTML("Nick Name  : " + nickname + "<br>");
    sp.printHTML("Email      : " + email + "<br>");
    sp.printHTML("Password   : " + password + "<br>");
    sp.printHTML("Region     : " + region + "<br>");
    sp.printHTML(
      "<br>The following information has been automatically generated by RUBiS:<br>");
    sp.printHTML("User id       :" + userId + "<br>");
    sp.printHTML("Creation date :" + creationDate + "<br>");

    sp.printHTMLfooter();
    
  }
  
  public void doGet1(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
{
  PreparedStatement stmt = null;
  Connection conn = null;
  
  String firstname = null,
    lastname = null,
    nickname = null,
    email = null,
    password = null;
  int regionId;
  int userId;
  String creationDate, region;

  ServletPrinter sp = null;
  sp = new ServletPrinter(response, "RegisterUser");

  String value = request.getParameter("firstname");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a first name!<br>", sp);
    return;
  }
  else
    firstname = value;

  value = request.getParameter("lastname");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a last name!<br>", sp);
    return;
  }
  else
    lastname = value;

  value = request.getParameter("nickname");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a nick name!<br>", sp);
    return;
  }
  else
    nickname = value;

  value = request.getParameter("email");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide an email address!<br>", sp);
    return;
  }
  else
    email = value;

  value = request.getParameter("password");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a password!<br>", sp);
    return;
  }
  else
    password = value;

  value = request.getParameter("region");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a valid region!<br>", sp);
    return;
  }
  else
  {
    region = value;
    
    try
    {
      conn = Database.getConnection();
      stmt = conn.prepareStatement("SELECT id FROM regions WHERE name=?");
      stmt.setString(1, region);
      ResultSet rs = stmt.executeQuery();
      if (!rs.first())
      {
        printError(
          " Region " + value + " does not exist in the database!<br>", sp);
       Database.commit(conn);
       Database.closeConnection(stmt, conn);
        return;
      }
      regionId = rs.getInt("id");
      stmt.close();
    }
    catch (SQLException e)
    {
      printError("Failed to execute Query for region: " + e, sp);
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
      return;
    }
  }
  // Try to create a new user
  try
  {
    stmt =
      conn.prepareStatement("SELECT nickname FROM users WHERE nickname=?");
    stmt.setString(1, nickname);
    ResultSet rs = stmt.executeQuery();
    if (rs.first())
    {
      printError("The nickname you have choosen is already taken by someone else. Please choose a new nickname.<br>", sp);
      Database.commit(conn);
      Database.closeConnection(stmt, conn);
      return;
    }
    stmt.close();
  }
  catch (SQLException e)
  {
    printError("Failed to execute Query to check the nickname: " + e, sp);
    Database.rollback(conn);
    Database.closeConnection(stmt, conn);
    return;
  }
  try
  {
    String now = TimeManagement.currentDateToString();
    stmt =
      conn.prepareStatement(
        //"INSERT INTO users VALUES (NULL, \""
        "INSERT INTO " +
        	"users(id,firstname,lastname,nickname,password,email,rating,balance,creation_date,region) " +
        	"VALUES ("+Config.UserIDFactory.addAndGet(Config.TotalProxies)+", \""
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
          + regionId
          + ")");
    stmt.executeUpdate();
    stmt.close();
  }
  catch (SQLException e)
  {
    printError(
      "RUBiS internal error: User registration failed (got exception: "
        + e
        + ")<br>", sp);
    Database.rollback(conn);
    Database.closeConnection(stmt, conn);
    return;
  }
  try
  {
    stmt =
      conn.prepareStatement(
        "SELECT id, creation_date FROM users WHERE nickname=?");
    stmt.setString(1, nickname);
    ResultSet urs = stmt.executeQuery();
    if (!urs.first())
    {
      printError("This user does not exist in the database.", sp);
      Database.commit(conn);
      Database.closeConnection(stmt, conn);
      return;
    }
    userId = urs.getInt("id");
    creationDate = urs.getString("creation_date");
  }
  catch (SQLException e)
  {
    printError("Failed to execute Query for user: " + e, sp);
    Database.rollback(conn);
    Database.closeConnection(stmt, conn);
    return;
  }

  Database.commit(conn);
  Database.closeConnection(stmt, conn);
  
  sp.printHTMLheader("RUBiS: Welcome to " + nickname);
  sp.printHTML(
    "<h2>Your registration has been processed successfully</h2><br>");
  sp.printHTML("<h3>Welcome " + nickname + "</h3>");
  sp.printHTML("RUBiS has stored the following information about you:<br>");
  sp.printHTML("First Name : " + firstname + "<br>");
  sp.printHTML("Last Name  : " + lastname + "<br>");
  sp.printHTML("Nick Name  : " + nickname + "<br>");
  sp.printHTML("Email      : " + email + "<br>");
  sp.printHTML("Password   : " + password + "<br>");
  sp.printHTML("Region     : " + region + "<br>");
  sp.printHTML(
    "<br>The following information has been automatically generated by RUBiS:<br>");
  sp.printHTML("User id       :" + userId + "<br>");
  sp.printHTML("Creation date :" + creationDate + "<br>");

  sp.printHTMLfooter();
  
}
  
  public void doGet2(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
{
  PreparedStatement stmt = null;
  Connection conn = null;
  
  String firstname = null,
    lastname = null,
    nickname = null,
    email = null,
    password = null;
  int regionId;
  int userId;
  String creationDate, region;

  ServletPrinter sp = null;
  sp = new ServletPrinter(response, "RegisterUser");

  String value = request.getParameter("firstname");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a first name!<br>", sp);
    return;
  }
  else
    firstname = value;

  value = request.getParameter("lastname");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a last name!<br>", sp);
    return;
  }
  else
    lastname = value;

  value = request.getParameter("nickname");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a nick name!<br>", sp);
    return;
  }
  else
    nickname = value;

  value = request.getParameter("email");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide an email address!<br>", sp);
    return;
  }
  else
    email = value;

  value = request.getParameter("password");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a password!<br>", sp);
    return;
  }
  else
    password = value;

  value = request.getParameter("region");
  if ((value == null) || (value.equals("")))
  {
    printError("You must provide a valid region!<br>", sp);
    return;
  }
  else
  {
    region = value;
    
    try
    {
      conn = Database.getConnection();
      stmt = conn.prepareStatement("SELECT id FROM regions WHERE name=?");
      stmt.setString(1, region);
      ResultSet rs = stmt.executeQuery();
      if (!rs.first())
      {
        printError(
          " Region " + value + " does not exist in the database!<br>", sp);
       Database.commit(conn);
       Database.closeConnection(stmt, conn);
        return;
      }
      regionId = rs.getInt("id");
      stmt.close();
    }
    catch (SQLException e)
    {
      printError("Failed to execute Query for region: " + e, sp);
      Database.rollback(conn);
      Database.closeConnection(stmt, conn);
      return;
    }
  }
  // Try to create a new user
  try
  {
    stmt =
      conn.prepareStatement("SELECT nickname FROM users WHERE nickname=?");
    stmt.setString(1, nickname);
    ResultSet rs = stmt.executeQuery();
    if (rs.first())
    {
      printError("The nickname you have choosen is already taken by someone else. Please choose a new nickname.<br>", sp);
      Database.commit(conn);
      Database.closeConnection(stmt, conn);
      return;
    }
    stmt.close();
  }
  catch (SQLException e)
  {
    printError("Failed to execute Query to check the nickname: " + e, sp);
    Database.rollback(conn);
    Database.closeConnection(stmt, conn);
    return;
  }
  try
  {
    String now = TimeManagement.currentDateToString();
    stmt =
      conn.prepareStatement(
        //"INSERT INTO users VALUES (NULL, \""
        "INSERT INTO " +
        	"users(id,firstname,lastname,nickname,password,email,rating,balance,creation_date,region) " +
        	"VALUES ("+Config.UserIDFactory.addAndGet(Config.TotalProxies)+", \""
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
          + regionId
          + ")");
    stmt.executeUpdate();
    stmt.close();
  }
  catch (SQLException e)
  {
    printError(
      "RUBiS internal error: User registration failed (got exception: "
        + e
        + ")<br>", sp);
    Database.rollback(conn);
    Database.closeConnection(stmt, conn);
    return;
  }
  try
  {
    stmt =
      conn.prepareStatement(
        "SELECT id, creation_date FROM users WHERE nickname=?");
    stmt.setString(1, nickname);
    ResultSet urs = stmt.executeQuery();
    if (!urs.first())
    {
      printError("This user does not exist in the database.", sp);
      Database.commit(conn);
      Database.closeConnection(stmt, conn);
      return;
    }
    userId = urs.getInt("id");
    creationDate = urs.getString("creation_date");
  }
  catch (SQLException e)
  {
    printError("Failed to execute Query for user: " + e, sp);
    Database.rollback(conn);
    Database.closeConnection(stmt, conn);
    return;
  }

  Database.commit(conn);
  Database.closeConnection(stmt, conn);
  
  sp.printHTMLheader("RUBiS: Welcome to " + nickname);
  sp.printHTML(
    "<h2>Your registration has been processed successfully</h2><br>");
  sp.printHTML("<h3>Welcome " + nickname + "</h3>");
  sp.printHTML("RUBiS has stored the following information about you:<br>");
  sp.printHTML("First Name : " + firstname + "<br>");
  sp.printHTML("Last Name  : " + lastname + "<br>");
  sp.printHTML("Nick Name  : " + nickname + "<br>");
  sp.printHTML("Email      : " + email + "<br>");
  sp.printHTML("Password   : " + password + "<br>");
  sp.printHTML("Region     : " + region + "<br>");
  sp.printHTML(
    "<br>The following information has been automatically generated by RUBiS:<br>");
  sp.printHTML("User id       :" + userId + "<br>");
  sp.printHTML("Creation date :" + creationDate + "<br>");

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
