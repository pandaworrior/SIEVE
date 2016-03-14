package replicationlayer.core.txstore.scratchpad.rdbms.util.micro;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import replicationlayer.core.txstore.scratchpad.rdbms.IDatabase;
import replicationlayer.core.txstore.scratchpad.rdbms.IDefDatabase;
import replicationlayer.core.txstore.scratchpad.rdbms.util.DBShadowOperation;



public class DBMICROEMPTY extends DBShadowOperation{
	
	protected DBMICROEMPTY(byte[] arr) {
		super(arr);
	}
	public static DBMICROEMPTY createOperation(DataInputStream dis) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		return new DBMICROEMPTY( baos.toByteArray());
		
	}
	public static DBMICROEMPTY createOperation() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream( baos);
		dos.writeByte(OP_SHADOWOP);
		dos.writeByte(OP_SHD_NONE);
		return new DBMICROEMPTY( baos.toByteArray());
		
	}
	protected DBMICROEMPTY(byte[] arr, String tN, String pK, String pKv, String fD, String vL) {
		super(arr);
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
	public void executeShadow(IDefDatabase store) {
		// TODO Auto-generated method stub
	}

	@Override
	public void encode(DataOutputStream dos) throws IOException {		
	}
	@Override
	public int execute(IDatabase store) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}

