/********************************************************************
Copyright (c) 2013 chengli.
All rights reserved. This program and the accompanying materials
are made available under the terms of the GNU Public License v2.0
which accompanies this distribution, and is available at
http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

Contributors:
    chengli - initial API and implementation

Contact:
    To distribute or use this code requires prior specific permission.
    In this case, please contact chengli@mpi-sws.org.
********************************************************************/
/**
 * 
 */
package util.crdtlib.datatypes.primitivetypes;

// TODO: Auto-generated Javadoc
/**
 * The Class LwwDouble.
 */
public class LwwDouble extends PrimitiveType{
	
	/** The value. */
	double value;
	
	/** The lww logical ts. */
	LwwLogicalTimestamp lwwLogicalTs;
	
	/**
	 * Instantiates a new lww double.
	 *
	 * @param dataName the data name
	 * @param v the v
	 */
	public LwwDouble(String dataName, double v){
		super(dataName);
		this.value = v;
	}
	
	/**
	 * Instantiates a new lww double.
	 *
	 * @param dataName the data name
	 * @param v the v
	 * @param ts the ts
	 */
	public LwwDouble(String dataName, double v, LwwLogicalTimestamp ts){
		super(dataName);
		this.value = v;
		this.lwwLogicalTs = ts;
	}
	
	/**
	 * Sets the logical timestamp.
	 *
	 * @param ts the new logical timestamp
	 */
	public void setLogicalTimestamp(LwwLogicalTimestamp ts){
		this.lwwLogicalTs = ts;
	}
	
	/**
	 * Update.
	 *
	 * @param v the v
	 * @param lts the lts
	 */
	public void update(double v, LwwLogicalTimestamp lts){
		if(this.lwwLogicalTs.isSmallerThan(lts)){
			this.value = v;
		}
	}

	/* (non-Javadoc)
	 * @see util.crdtlib.datatypes.primitivetypes.PrimitiveType#toString()
	 */
	/**
	 * @see util.crdtlib.datatypes.primitivetypes.PrimitiveType#toString()
	 * @return
	 */
	@Override
	public String toString() {
		String str = "LwwDouble" + " value " + this.value;
		return str;
	}


	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public double getValue() {
		// TODO Auto-generated method stub
		return this.value;
	}

	/* (non-Javadoc)
	 * @see util.crdtlib.datatypes.primitivetypes.PrimitiveType#equalTo(util.crdtlib.datatypes.primitivetypes.PrimitiveType)
	 */
	/**
	 * @see util.crdtlib.datatypes.primitivetypes.PrimitiveType#equalTo(util.crdtlib.datatypes.primitivetypes.PrimitiveType)
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equalTo(PrimitiveType obj) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see util.crdtlib.datatypes.primitivetypes.PrimitiveType#getValueByName(java.lang.String)
	 */
	@Override
	public String getValueByName(String name) {
		if(name.equals("value")) {
			return Double.toString(this.getValue());
		}else {
			System.out.println(this.getClass().toString() + " name is not specified correctly " + name);
			System.exit(-1);
		}
		return null;
	}
	
}
