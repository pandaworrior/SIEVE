package replicationlayer.core.txstore.scratchpad.rdbms.util.tpcw;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import replicationlayer.core.txstore.scratchpad.rdbms.IDatabase;
import replicationlayer.core.txstore.scratchpad.rdbms.IDefDatabase;
import replicationlayer.core.txstore.scratchpad.rdbms.IPrimaryExec;
import replicationlayer.core.txstore.scratchpad.rdbms.tests.DBTestCountOperation;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBGenericOperation;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBSelectResult;
import replicationlayer.core.util.Debug;

import java.sql.Date;



public class DBTPCWResetCartTime extends DBGenericOperation{
	int cartID;
	Date timestamp;
	protected DBTPCWResetCartTime(byte[] arr) {
		super(arr);
	}
	public static DBTPCWResetCartTime createOperation(DataInputStream dis) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream( baos);
		int shopping_id = dis.readInt();
		dos.writeInt(shopping_id);
		long timestamp = dis.readLong();
		dos.writeLong(timestamp);
		return new DBTPCWResetCartTime( baos.toByteArray(), shopping_id, timestamp);
		
	}
	public static DBTPCWResetCartTime createOperation(int shopping_id, long timestamp) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream( baos);
		dos.writeInt(shopping_id);
		dos.writeLong(timestamp);
		return new DBTPCWResetCartTime( baos.toByteArray(), shopping_id, timestamp);
		
	}
	protected DBTPCWResetCartTime(byte[] arr, int shopping_id, long timestamp) {
		super(arr);
		this.cartID = shopping_id;
		this.timestamp = new Date(timestamp);
	}
	
	
	@Override
	public boolean isQuery() {
		return false;
	}

	@Override
	public boolean registerIndividualOperations() {
		return false;
	}

	@Override
	public int execute(IDatabase store) {
		try {
			Debug.print("Primary execution");
			store.executeUpdate("UPDATE shopping_cart SET sc_time = "+this.timestamp+" WHERE sc_id = "+this.cartID);
			return 1;
		} catch( Exception e) {
			System.err.println("There was an exception in the primary datacenter!!!");
			e.printStackTrace();
			return 0;
		}
		
		
	}

	@Override
	public void executeShadow(IDefDatabase store, IPrimaryExec exec) {
		try {
			Debug.print("Shadow execution");
			store.executeUpdate("UPDATE shopping_cart SET sc_time = "+this.timestamp+" WHERE sc_id = "+this.cartID);
		} catch( Exception e) {
			System.err.println("There was an exception when performing the shadow operation");
			Debug.kill(e);
		}
	}

	@Override
	public void encode(DataOutputStream dos) throws IOException {
		dos.writeInt(cartID);
		dos.writeLong(timestamp.getTime());
	}

}
