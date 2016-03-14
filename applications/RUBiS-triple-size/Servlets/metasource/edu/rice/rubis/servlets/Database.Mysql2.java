package edu.rice.rubis.servlets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EmptyStackException;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import edu.rice.rubis.servlets.Config;

/** This class contains the configuration for the servlets
 * like the path of HTML files, etc ...
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class Database2
{

  /** Controls connection pooling */
	private static final boolean enablePooling = true;
	/** Stack of available connections (pool) */
	private static Stack<Connection> freeConnections = null;
	private static Properties dbProperties = null;

	static AtomicInteger aborts =new AtomicInteger(0);
	static AtomicInteger commitedtxn =new AtomicInteger(0);
	static int transactions = 0;
	static long startmi=0;
	static long endmi=0;
	
	
	public static void init2() throws ServletException {
		System.out.println("======HTML FILES:" + Config.HTMLFilesPath);
		System.out.println("======Database FILES:" + Config.DatabaseProperties);
		InputStream in = null;
		
		try {
			// Get the properties for the database connection
			dbProperties = new Properties();
			in = new FileInputStream(Config.DatabaseProperties);
			dbProperties.load(in);
			// load the driver
			Class.forName(dbProperties.getProperty("datasource.classname"));

			freeConnections = new Stack<Connection>();
			initializeConnections();
		} catch (FileNotFoundException f) {
			throw new UnavailableException(
					"Couldn't find file mysql.properties: " + f + "<br>");
		} catch (IOException io) {
			throw new UnavailableException(
					"Cannot open read mysql.properties: " + io + "<br>");
		} catch (ClassNotFoundException c) {
			throw new UnavailableException("Couldn't load database driver: "
					+ c + "<br>");
		} catch (SQLException s) {
			throw new UnavailableException("Couldn't get database connection: "
					+ s + "<br>");
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}
	}//method
	/**
	 * Initialize the pool of connections to the database. The caller must
	 * ensure that the driver has already been loaded else an exception will be
	 * thrown.
	 * 
	 * @exception SQLException
	 *                if an error occurs
	 */
	public static synchronized void initializeConnections2() throws SQLException {
		if (enablePooling){
			for (int i = 0; i < Config.DatabasePool; i++) {
				// Get connections to the database
				Connection c = DriverManager.getConnection(
						dbProperties.getProperty("datasource.url"),
						dbProperties.getProperty("datasource.username"),
						dbProperties.getProperty("datasource.password"));
				c.setAutoCommit(false);
                System.out.println("Initializing database pool " + i);
				freeConnections.push(c);
			}
		System.out.println("Database pool was initialized. #connections:"+Config.DatabasePool);	
		}
	}
	/**
	 * Closes a <code>Connection</code>.
	 * 
	 * @param connection
	 *            to close
	 */
	public static void closeConnection2(Connection connection) {
		try {
			connection.close();
		} catch (Exception e) {

		}
	}

	/**
	 * Gets a connection from the pool (round-robin)
	 * 
	 * @return a <code>Connection</code> or null if no connection is available
	 */
	public static synchronized Connection getConnection2() {
		long time = System.currentTimeMillis();
		boolean isMeasurementInterval=(time > startmi && time < endmi);
		
		if (enablePooling) {
			try {
				// Wait for a connection to be available
				Connection c = (Connection) freeConnections.pop();
		    	if(isMeasurementInterval) transactions++;
				return c;
			} catch (EmptyStackException e) {
				System.out.println("Connection pool Out of connections.");
				return null;
			}
		} else {
			try {
				Connection c = DriverManager.getConnection(
						dbProperties.getProperty("datasource.url"),
						dbProperties.getProperty("datasource.username"),
						dbProperties.getProperty("datasource.password"));
				c.setAutoCommit(false);//disable autocommit
				if(isMeasurementInterval) transactions++;
				return c;
			} catch (SQLException ex) {
				System.out.println("Error when connecting to the database. pw:"
						+ dbProperties.getProperty("datasource.password")
						+ " url:" + dbProperties.getProperty("datasource.url")
						+ " username:"
						+ dbProperties.getProperty("datasource.username"));
				ex.printStackTrace();
				return null;
			}
		}
	}
	
	
	/**
	 * Release a connection to the pool. Changing individual method in each
	 * servelet to one
	 */
	
	public static void closeConnection2(PreparedStatement stmt, Connection conn){
		try
	    {
	      if (stmt != null)
	        stmt.close(); // close statement
	      if (conn != null)
	        releaseConnection(conn);
	    }
	    catch (Exception ignore)
	    {
	    }
	}
	
	/**
	 * commit a transaction
	 */
	
	public static void commit2(Connection conn){
		long time = System.currentTimeMillis();
		boolean isMeasurementInterval=(time > startmi && time < endmi);
		try{
			conn.commit();
			if(isMeasurementInterval)
				commitedtxn.incrementAndGet();
		}catch (SQLException e){
			if(isMeasurementInterval)
				aborts.incrementAndGet();
			System.err.println("Restarting TX because of a database problem (hopefully just a conflict) total aborts within the MI so far:"+aborts);
			//e.printStackTrace();
		}
	}
	
	/**
	 * rollback a transaction
	 */
	
	public static void rollback2(Connection conn){
		try{
			conn.rollback();
		}catch (SQLException e){
			e.printStackTrace();
		}
	}

	/**
	 * Releases a connection to the pool.
	 * 
	 * @param c
	 *            the connection to release
	 */
	public static synchronized void releaseConnection2(Connection c) {
		if (enablePooling) {
			boolean mustNotify = freeConnections.isEmpty();
			freeConnections.push(c);
			// Wake up one servlet waiting for a connection (if any)
//			if (mustNotify)
//				notifyAll();
		} else {
			closeConnection(c);
		}

	}

	/**
	 * Release all the connections to the database.
	 * 
	 * @exception SQLException
	 *                if an error occurs
	 */
	public synchronized void finalizeConnections2() throws SQLException {
		if (enablePooling) {
			Connection c = null;
			while (!freeConnections.isEmpty()) {
				c = (Connection) freeConnections.pop();
				c.close();
			}
		}
	}
}

