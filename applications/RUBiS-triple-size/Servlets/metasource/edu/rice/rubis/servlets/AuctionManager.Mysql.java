package edu.rice.rubis.servlets;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.*;
import javax.servlet.http.*;

public class AuctionManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//private static ScheduledThreadPoolExecutor closeAuctionPool = new ScheduledThreadPoolExecutor(100);
	//static Hashtable<Integer,CloseAuctionOperation> ActiveItems = new Hashtable<Integer,CloseAuctionOperation>(10000); 
	//static AtomicInteger closedAuctions = new AtomicInteger(0);

	// initialize the database pool upon a loads up
	public void init() throws ServletException{
		// reset values after reading from topology file
		Config.TotalProxies = 1;
		Config.MaxProxyPerDatacenter = 1;
		Config.TotalDatacenters = 1;
		Config.DatacenterID = 0;
		Config.ProxyID = 0; // compute non-overlap proxy id
		ServletContext context = getServletContext();
		String HTMLFilesPath = "";
		String DatabaseProperties = "WEB-INF/classes/mysql.properties";
		Config.HTMLFilesPath = context.getRealPath("") + "/" + HTMLFilesPath;
		Config.DatabaseProperties = context.getRealPath("") + "/"+ DatabaseProperties;
		
		//reading parameters
		Config.TotalProxies = Integer.parseInt(context.getInitParameter("totalproxy"));
		System.out.println("total proxy "+ Config.TotalProxies);
		Config.TotalDatacenters = Integer.parseInt(context.getInitParameter("dcCount"));
		System.out.println("total dc "+ Config.TotalDatacenters);
		Config.DatacenterID = Integer.parseInt(context.getInitParameter("dcId"));
		System.out.println("dcId "+ Config.DatacenterID);
		Config.ProxyID = Integer.parseInt(context.getInitParameter("proxyId"));
		System.out.println("proxyId "+ Config.ProxyID);
		Config.DatabasePool = Integer.parseInt(context.getInitParameter("dbpool"));
		System.out.println("db pool "+ Config.DatabasePool);
		//@initializeBackend@

	}

	public void destroy() {
		//closeAuctionPool.shutdown();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
   
	    int i;
	    PrintWriter out = res.getWriter();
	    long now=0;
	    res.setContentType("text/plain");
	    HttpSession session = req.getSession(false);
	    String command  = req.getParameter("command");
	    if(command.equals("getAborts")){
	    	int aborts= Database.aborts.get();
			out.println("aborts "+aborts);
	    }
	    if(command.equals("setProxyId")){
	    	String proxyid = req.getParameter("proxyid");
	    	Config.ProxyID=Integer.parseInt(proxyid);
	    	String totalproxies= req.getParameter("totalproxies");
	    	Config.TotalProxies=Integer.parseInt(totalproxies);
	    	out.println("proxy_number "+Config.ProxyID);
	    	
	    }
	    if(command.equals("getTransactions")){
	    	int transactions= Database.transactions;
			out.println("transactions "+transactions);
	    }
	    if(command.equals("getCommitedTransactions")){
	    	int transactions= Database.commitedtxn.get();
			out.println("transactions "+transactions);
	    }
	    if(command.equals("configure")){//restart an experiment
	    	Database.transactions=0;
	    	Database.aborts.set(0);
	    	Database.commitedtxn.set(0);
	    	
	    	now = System.currentTimeMillis();
	    	//String totalproxies= req.getParameter("totalproxies");
	    	//TPCW_Database.totalproxies=Integer.parseInt(totalproxies);
	    	String startmi= req.getParameter("startmi");
	    	Database.startmi=now+Long.parseLong(startmi);
	    	String duration= req.getParameter("duration");
	    	Database.endmi=Database.startmi+Long.parseLong(duration);
	    	
//	    	String globalProxyId = req.getParameter("proxyid");
//	    	System.err.println("Proxy global ID"+globalProxyId);
//	    	TPCW_Database.initID(Integer.parseInt(globalProxyId));
	    	
	    	//@txmud.setmeasurementinterval@
	    	
	    	out.println("ok");
	    	out.flush();
	    	System.err.println("Proxy ["+Config.ProxyID+"] configured at "+new Date(now) +
	        		", it will start at "+new Date(Database.startmi)+" and stop at "+new Date(Database.endmi));	
	    }
	    if(command.equals("getTxMudAborts")){
	    	out.println("aborts ");//+@txmud.getaborts@);
	    }if(command.equals("getTxMudRedTransactions")){
	    	out.println("red ");//+@txmud.getredtxn@);
	    }if(command.equals("getTxMudBlueTransactions")){
	    	out.println("blue ");//+@txmud.getbluetxn@);
	    }
		
	}// do get

//	public static void scheduleCloseAuction(GregorianCalendar whenclose, int auctionID){
//		System.out.println("total active threads "+closeAuctionPool.getActiveCount());
//		long delay = (whenclose.getTimeInMillis() - System.currentTimeMillis()) * 1000;
//		if(delay > 0){	//ignore database population
//			CloseAuctionOperation operation = new CloseAuctionOperation(auctionID);
//			closeAuctionPool.schedule(operation, delay, TimeUnit.SECONDS);
//			ActiveItems.put(auctionID, operation);
//		}
//	}
//	public static void cancelScheduledCloseAuction(int auctionID){
//		closeAuctionPool.remove(ActiveItems.remove(auctionID));
//	}
	
	private synchronized void initializeMysqlBackend() throws ServletException{
		Database.init(); //initialize the connection pool
		initializeMysqlIDFactory(Config.MaxProxyPerDatacenter * Config.DatacenterID+ Config.ProxyID);
	}
	private synchronized void initializeTxMudBackend() throws ServletException{
		Database.init(); //initialize the connection pool
		initializeTxMudIDFactory(Config.MaxProxyPerDatacenter * Config.DatacenterID+ Config.ProxyID);
	}
	private synchronized void initializeMysqlIDFactory(int globalProxyId) {
		PreparedStatement stmt = null;
		Connection con = null;
		con = Database.getConnection();

		int n=0;
		try {
			///////////userid
			Statement get_next_usr_id = con.createStatement();
			ResultSet rs = get_next_usr_id.executeQuery("select MAX(id) from users");
			try {
				if (rs.next()) {
					n = rs.getInt(1);
				} else {
					n = 0;
				}
			} catch (NumberFormatException ne) {
				n = 0;
				System.err
				.println("Exception caught while initializing the user id."
						+ " Hopefuly the it is just because the table is empty.");
			}
			
			int keySpace = (Integer.MAX_VALUE - n)/Config.TotalProxies;
			System.err.println("user id keyspace: " + keySpace);
			for(int i = 0; i < globalProxyId; i++){
				n = n + keySpace;
			}
			System.err.println("Proxy[" + globalProxyId
					+ "] set initial USER ID:" + n);
			Config.UserIDFactory.set(n);
			rs.close();
			// /////////itemid
			Statement get_next_item_id = con.createStatement();
			ResultSet rs2 = get_next_item_id.executeQuery("select MAX(id) from items");
			try {
				if (rs2.next()) {
					n = rs2.getInt(1);
				} else {
					n = 0;
				}
			} catch (NumberFormatException ne) {
				n = 0;
				System.err
				.println("Exception caught while initializing the user id."
						+ " Hopefuly the it is just because the table is empty.");
			}
			keySpace = (Integer.MAX_VALUE - n)/Config.TotalProxies;
			System.err.println("item id keyspace: " + keySpace);
			for(int i = 0; i < globalProxyId; i++){
				n = n + keySpace;
			}
			System.err.println("Proxy[" + globalProxyId
					+ "] set initial ITEM ID:" + n);
			Config.ItemIDFactory.set(n);
			rs2.close();
			// //////////bidid
			Statement get_next_bid_id = con.createStatement();
			ResultSet rs3 = get_next_bid_id.executeQuery("select MAX(id) from bids");
			try {
				if (rs3.next()) {
					n = rs3.getInt(1);
				} else {
					n = 0;
				}
			} catch (NumberFormatException ne) {
				n = 0;
				System.err
				.println("Exception caught while initializing the user id."
						+ " Hopefuly the it is just because the table is empty.");
			}
			keySpace = (Integer.MAX_VALUE - n)/Config.TotalProxies;
			System.err.println("bid id keyspace: " + keySpace);
			for(int i = 0; i < globalProxyId; i++){
				n = n + keySpace;
			}
			System.err.println("Proxy[" + globalProxyId
					+ "] set initial BID ID:" + n);
			Config.BidIDFactory.set(n);
			rs3.close();
			// /////////comment id
			Statement get_next_comment_id = con.createStatement();
			ResultSet rs4 = get_next_comment_id.executeQuery("select MAX(id) from comments");
			try {
				if (rs4.next()) {
					n = rs4.getInt(1);
				} else {
					n = 0;
				}
			} catch (NumberFormatException ne) {
				n = 0;
				System.err
				.println("Exception caught while initializing the user id."
						+ " Hopefuly the it is just because the table is empty.");
			}
			keySpace = (Integer.MAX_VALUE - n)/Config.TotalProxies;
			System.err.println("comment id keyspace: " + keySpace);
			for(int i = 0; i < globalProxyId; i++){
				n = n + keySpace;
			}
			System.err.println("Proxy[" + globalProxyId
					+ "] set initial COMMENT ID:" + n);
			Config.CommentIDFactory.set(n);
			rs4.close();
			// /////////buynow
			Statement get_next_buynow_id = con.createStatement();
			ResultSet rs5 = get_next_buynow_id.executeQuery("select MAX(id) from buy_now");
			try {
				if (rs5.next()) {
					n = rs5.getInt(1);
				} else {
					n = 0;
				}
			} catch (NumberFormatException ne) {
				n = 0;
				System.err
				.println("Exception caught while initializing the user id."
						+ " Hopefuly the it is just because the table is empty.");
			}
			keySpace = (Integer.MAX_VALUE - n)/Config.TotalProxies;
			System.err.println("buy now id keyspace: " + keySpace);
			for(int i = 0; i < globalProxyId; i++){
				n = n + keySpace;
			}
			System.err.println("Proxy[" + globalProxyId
					+ "] set initial BUYNOW ID:" + n);
			Config.BuyNowIDFactory.set(n);
			rs5.close();
			// ///////////////
			con.commit();
			Database.releaseConnection(con);
		} catch (Exception e) {
			System.err
			.println("Problem when setting the inital id, system cannot continue");
			e.printStackTrace();
			System.exit(0);
		}
		
	}
	private synchronized void initializeTxMudIDFactory(int globalProxyId) {
		PreparedStatement stmt = null;
		Connection con = null;
		con = Database.getConnection();

		System.out.println("my global proxy id "+ globalProxyId);
		int n;
		try {
			///////////userid
			Statement get_next_usr_id = con.createStatement();
			ResultSet rs = get_next_usr_id.executeQuery("select MAX(id) from users");
			try {
				if (rs.next()) {
					n = rs.getInt(1);
				} else {
					n = 0;
				}
			} catch (NumberFormatException ne) {
				n = 0;
				System.err
				.println("Exception caught while initializing the user id."
						+ " Hopefuly the it is just because the table is empty.");
			}
			n = n - (n % Config.TotalProxies) + Config.TotalProxies
					+ globalProxyId;
			System.err.println("Proxy[" + globalProxyId
					+ "] set initial USER ID:" + n);
			Config.UserIDFactory.set(n);
			rs.close();
			// /////////itemid
			Statement get_next_item_id = con.createStatement();
			ResultSet rs2 = get_next_item_id.executeQuery("select MAX(id) from items");
			try {
				if (rs2.next()) {
					n = rs2.getInt(1);
				} else {
					n = 0;
				}
			} catch (NumberFormatException ne) {
				n = 0;
				System.err
				.println("Exception caught while initializing the user id."
						+ " Hopefuly the it is just because the table is empty.");
			}
			n = n - (n % Config.TotalProxies) + Config.TotalProxies	+ globalProxyId;
			System.err.println("Proxy[" + globalProxyId
					+ "] set initial ITEM ID:" + n);
			Config.ItemIDFactory.set(n);
			rs2.close();
			// //////////bidid
			Statement get_next_bid_id = con.createStatement();
			ResultSet rs3 = get_next_bid_id.executeQuery("select MAX(id) from bids");
			try {
				if (rs3.next()) {
					n = rs3.getInt(1);
				} else {
					n = 0;
				}
			} catch (NumberFormatException ne) {
				n = 0;
				System.err
				.println("Exception caught while initializing the user id."
						+ " Hopefuly the it is just because the table is empty.");
			}
			n = n - (n % Config.TotalProxies) + Config.TotalProxies
					+ globalProxyId;
			System.err.println("Proxy[" + globalProxyId
					+ "] set initial BID ID:" + n);
			Config.BidIDFactory.set(n);
			rs3.close();
			// /////////comment id
			Statement get_next_comment_id = con.createStatement();
			ResultSet rs4 = get_next_comment_id.executeQuery("select MAX(id) from comments");
			try {
				if (rs4.next()) {
					n = rs4.getInt(1);
				} else {
					n = 0;
				}
			} catch (NumberFormatException ne) {
				n = 0;
				System.err
				.println("Exception caught while initializing the user id."
						+ " Hopefuly the it is just because the table is empty.");
			}
			n = n - (n % Config.TotalProxies) + Config.TotalProxies
					+ globalProxyId;
			System.err.println("Proxy[" + globalProxyId
					+ "] set initial COMMENT ID:" + n);
			Config.CommentIDFactory.set(n);
			rs4.close();
			// /////////buynow
			Statement get_next_buynow_id = con.createStatement();
			ResultSet rs5 = get_next_buynow_id.executeQuery("select MAX(id) from buy_now");
			try {
				if (rs5.next()) {
					n = rs5.getInt(1);
				} else {
					n = 0;
				}
			} catch (NumberFormatException ne) {
				n = 0;
				System.err
				.println("Exception caught while initializing the user id."
						+ " Hopefuly the it is just because the table is empty.");
			}
			n = n - (n % Config.TotalProxies) + Config.TotalProxies
					+ globalProxyId;
			System.err.println("Proxy[" + globalProxyId
					+ "] set initial BUYNOW ID:" + n);
			Config.BuyNowIDFactory.set(n);
			rs5.close();
			// ///////////////
			con.commit();
		} catch (Exception e) {
			System.err
			.println("Problem when setting the inital id, system cannot continue");
			e.printStackTrace();
			System.exit(0);
		}
	}

//	/**
//	 * Close both statement and connection.
//	 */
//	protected void closeAuction(int auctionId) {
//		try {
//			Connection conn = Database.getConnection();
//			conn.setTransactionIsolation(conn.TRANSACTION_SERIALIZABLE);
//			String now = TimeManagement.currentDateToString();
//			
//			PreparedStatement stmt =
//		          conn.prepareStatement(
//		            "UPDATE items SET end_date=? WHERE id=?");
//		        stmt.setString(1, now);
//		        stmt.setInt(2, auctionId);
//		        stmt.executeUpdate();
//		        stmt.close();
//		    
//		        
//			conn.commit();
//			conn.setTransactionIsolation(conn.TRANSACTION_REPEATABLE_READ);
//			Database.releaseConnection(conn);
//
//		} catch (Exception e) {
//			System.err.println("exception found during close auction " + e.toString());
//			e.printStackTrace();
//		}
//	}
	
	
//	/**
//	 * Close both statement and connection.
//	 */
//	protected void selectWinner(int auctionId) {
//		try {
//			Connection conn = Database.getConnection();
//			String now = TimeManagement.currentDateToString();
//			
//			PreparedStatement stmt =
//		          conn.prepareStatement(
//		            "SELECT bids.id, bids.qty, bids.bid, bids.max_bid, items.reserve_price, items.quantity from bids, items WHERE bids.item_id = items.id AND items.id=? ORDER BY bids.max_id DESC");
//		        stmt.setInt(1, auctionId);
//		    ResultSet rs = stmt.executeQuery();
//		    int quantity = 0;
//		    float reserve_price;
//		    int count = 0;
//		    PreparedStatement storebidstmt = null;
//		    while(rs.next()){
//		    	if(count == 0){
//		    		quantity = rs.getInt("quantity");
//		    		reserve_price = rs.getFloat("reserve_price");
//		    	}
//		    	if(quantity == 0){
//		    		System.out.println("auction " + auctionId + " assigned up");
//		    	}
//		    	int qty = rs.getInt("qty");
//		    	float price = rs.getFloat("bid");
//		    	int bidId = rs.getInt("id");
//		    	if(qty <= quantity){
//					storebidstmt =
//				          conn.prepareStatement(
//				            "INSERT INTO winners values (?,?,?)");
//					storebidstmt.setInt(1, bidId);
//					storebidstmt.setInt(2, qty);
//					storebidstmt.setFloat(3, price);
//					quantity = quantity - qty;
//					storebidstmt.executeUpdate();
//		    	}else{
//		    		storebidstmt =
//				          conn.prepareStatement(
//				            "INSERT INTO winners values (?,?,?)");
//					storebidstmt.setInt(1, bidId);
//					storebidstmt.setInt(2, quantity);
//					storebidstmt.setFloat(3, price);
//					quantity = 0;
//					storebidstmt.executeUpdate();
//		    	}
//		    }
//		        stmt.close();
//		        storebidstmt.close();
//		        rs.close();
//		        
//			Database.commit(conn);
//			//conn.setTransactionIsolation(conn.TRANSACTION_REPEATABLE_READ);
//			Database.releaseConnection(conn);
//
//		} catch (Exception e) {
//			System.err.println("exception found during close auction " + e.toString());
//			e.printStackTrace();
//		}
//	}
	
}
//class CloseAuctionOperation implements Runnable {
//	private int auctionID;
//	AuctionManager proxy; 
//	public CloseAuctionOperation(int id){
//		auctionID = id;
//	}
//	
//	@Override
//	public void run() {
//		//issue the sql statement to set the auction closer and find the winner
//		proxy.closeAuction(auctionID);
//		System.out.println("Finishing auction "+auctionID);
//		System.out.println("Total closed auctions so far"+AuctionManager.closedAuctions.incrementAndGet());
//		proxy.selectWinner(auctionID);
//	}
//	
//	
//}
