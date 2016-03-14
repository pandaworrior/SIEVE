package replicationlayer.core.txstore.scratchpad.rdbms.util.rubis;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;

import replicationlayer.core.txstore.scratchpad.rdbms.IDatabase;
import replicationlayer.core.txstore.scratchpad.rdbms.IDefDatabase;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBShadowOperation;
import replicationlayer.core.util.Debug;

public class DBRUBISShdStoreComment2 extends DBShadowOperation{
	private int id;
	private int from_user_id;
	private int to_user_id;
	private int item_id;
	private int rating;
	private String date;
	private String comment;
	

	public static DBRUBISShdStoreComment2 createOperation(DataInputStream dis) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream( baos);
		int id = dis.readInt();
		dos.writeInt(id);
		int from_user_id = dis.readInt();
		dos.writeInt(from_user_id);
		int to_user_id = dis.readInt();
		dos.writeInt(to_user_id);
		int item_id = dis.readInt();
		dos.writeInt(item_id);
		int rating = dis.readInt();
		dos.writeInt(rating);
		String date = dis.readUTF();
		dos.writeUTF(date);
		String comment = dis.readUTF();
		dos.writeUTF(comment);
		return new DBRUBISShdStoreComment2( baos.toByteArray(), id,from_user_id,to_user_id,item_id,rating,date,comment);
		
	}
	public static DBRUBISShdStoreComment2 createOperation(int id, int from_user_id, int to_user_id, int item_id,int rating, String date, String comment) throws IOException {
		Debug.println("store comment 2 operation\n");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream( baos);
		dos.writeByte(OP_SHADOWOP);
		dos.writeByte(OP_SHD_RUBIS_STORECOMMENT2);
		dos.writeInt(id);
		dos.writeInt(from_user_id);
		dos.writeInt(to_user_id);
		dos.writeInt(item_id);
		dos.writeInt(rating);
		dos.writeUTF(date);
		dos.writeUTF(comment);
		Debug.println("store comment 2 operation done\n");
		return new DBRUBISShdStoreComment2( baos.toByteArray(), id,from_user_id,to_user_id,item_id,rating,date,comment);
		
	}
	
	protected DBRUBISShdStoreComment2(byte[] arr) {
		super(arr);
	}
	
	protected DBRUBISShdStoreComment2(byte[] arr, int id, int from_user_id, int to_user_id, int item_id,int rating, String date, String comment) {
		super(arr);
		this.id = id;
		this.from_user_id = from_user_id;
		this.to_user_id = to_user_id;
		this.item_id = item_id;
		this.rating = rating;
		this.date = date;
		this.comment = comment;
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
		return 0;
	}

	@Override
	public void encode(DataOutputStream dos) throws IOException {
		Debug.print("store comment 2 encode\n");
		dos.writeInt(id);
		dos.writeInt(from_user_id);
		dos.writeInt(to_user_id);
		dos.writeInt(item_id);
		dos.writeInt(rating);
		dos.writeUTF(date);
		dos.writeUTF(comment);
	}
	
	
	@Override
	public void executeShadow(IDefDatabase iDefDatabase) {
		// TODO Auto-generated method stub
		try {
			Debug.println("Shadow store comment 2 execution " + id);
			iDefDatabase.executeUpdate("INSERT INTO comments(id,from_user_id,to_user_id,item_id,rating,date,comment)" +
		            " VALUES ("+id+", \""  
		              + this.from_user_id
		              + "\", \""
		              + this.to_user_id
		              + "\", \""
		              + this.item_id
		              + "\", \""
		              + this.rating
		              + "\", \""
		              + this.date
		              + "\",\""
		              + comment
		              + "\")");
		    String updateSql = "UPDATE users SET rating=rating+"+this.rating+" WHERE id="+this.to_user_id;
		    iDefDatabase.executeUpdate(updateSql);			
		} catch( Exception e) {
			System.err.println("There was an exception when performing the shadow store comment 2 operation");
			e.printStackTrace();
		}
	}

}

