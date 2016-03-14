package replicationlayer.core.txstore.util;
import replicationlayer.core.util.Debug;

import replicationlayer.core.util.UnsignedTypes;

import java.util.Vector;

import java.util.Set;

public class WriteSet{

    WriteSetEntry writeSet[];

    public WriteSet(WriteSetEntry[] rs){
	writeSet = rs;
    }

    public WriteSet(Set<WriteSetEntry> rs){
	this(rs.toArray(_wse));
    }
    
    static WriteSetEntry[] _wse = new WriteSetEntry[0];
    public WriteSet(Vector<WriteSetEntry> ws){
	this(ws.toArray(_wse));
    }

    public WriteSet(byte b[], int offset){

	int sz = UnsignedTypes.bytesToInt(b, offset);
	offset += UnsignedTypes.uint16Size;

	writeSet = new WriteSetEntry[sz];
	for (int i = 0; i < sz; i++){
	    writeSet[i] = new WriteSetEntry(b, offset);
	    offset += writeSet[i].getByteSize();
	}	
    }

    public void getBytes(byte[] b, int offset){
		UnsignedTypes.intToBytes(writeSet.length, b, offset);
	offset += UnsignedTypes.uint16Size;
	for (int i = 0; i < writeSet.length; i++){
	    writeSet[i].getBytes(b, offset);
	    offset += writeSet[i].getByteSize();
	}


    }

    public final int getByteSize(){
	int sz = UnsignedTypes.uint16Size;
	for (int i  = 0; i < writeSet.length; i++)
	    sz += writeSet[i].getByteSize();
	return sz;
    }
    
    public WriteSetEntry[] getWriteSet(){
	return writeSet;
    }

    public WriteSetEntry getWriteSetEntry(int i ){
	return writeSet[i];
    }

    public boolean isEmpty(){
        return writeSet.length == 0;
        
    }
    
    public boolean isBlue(){
	for (int i = 0; i < writeSet.length; i++)
	    if (writeSet[i].isBlue())
		return true;
	return false;
    }

    public boolean isRed(){
	return !isBlue();
    }
    
        public String toString(){
	String s="<";
	for (int i = 0; i < writeSet.length; i ++)
	    s+=writeSet[i]+(i<writeSet.length-1?", ":"");
	return s+">";
    }
    

}

