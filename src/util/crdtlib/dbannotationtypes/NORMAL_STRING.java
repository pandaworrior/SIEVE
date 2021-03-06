package util.crdtlib.dbannotationtypes;

import java.sql.ResultSet;

import util.crdtlib.dbannotationtypes.dbutil.CrdtDataFieldType;
import util.crdtlib.dbannotationtypes.dbutil.DataField;

// TODO: Auto-generated Javadoc
/**
 * The Class LWW_STRING.
 */
public class NORMAL_STRING extends DataField {

	/**
	 * Instantiates a new lww string.
	 *
	 * @param dFN the d fn
	 * @param tN the t n
	 * @param dT the d t
	 * @param iPK the i pk
	 * @param iFK the i fk
	 * @param iAIC the i aic
	 * @param position the position
	 */
	public NORMAL_STRING(String dFN, String tN, String dT, boolean iPK,
			boolean iFK, boolean iAIC, int position) {
		super(CrdtDataFieldType.NORMALSTRING, dFN, tN, dT, iPK, iFK, iAIC,
				position);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see util.crdtlib.dbannotationtypes.dbutil.DataField#get_Crdt_Form(java.sql.ResultSet, java.lang.String)
	 */
	/**
	 * @see util.crdtlib.dbannotationtypes.dbutil.DataField#get_Crdt_Form(java.sql.ResultSet, java.lang.String)
	 * @param rs
	 * @param Value
	 * @return
	 */
	@Override
	public String get_Crdt_Form(ResultSet rs, String Value) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see crdts.basics.Data_Field#get_Crdt_Form(java.lang.String)
	 */
	/**
	 * @see util.crdtlib.dbannotationtypes.dbutil.DataField#get_Crdt_Form(java.lang.String)
	 * @param Value
	 * @return
	 */
	@Override
	public String get_Crdt_Form(String Value) {
		// TODO Auto-generated method stub
		Value = Value.trim();
		if (Value.indexOf("'") == 0
				&& Value.lastIndexOf("'") == (Value.length() - 1))
			Value = Value.substring(1, Value.length() - 1);
		return this.get_Data_Field_Name() + " = '" + Value + "'";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * crdts.basics.Data_Field#get_Value_In_Correct_Format(java.lang.String)
	 */
	/**
	 * @see util.crdtlib.dbannotationtypes.dbutil.DataField#get_Value_In_Correct_Format(java.lang.String)
	 * @param Value
	 * @return
	 */
	@Override
	public String get_Value_In_Correct_Format(String Value) {
		if ((Value.indexOf("'") == 0
				&& Value.lastIndexOf("'") == Value.length() - 1) ||
			(Value.indexOf("\"") == 0
				&& Value.lastIndexOf("\"") == Value.length() - 1) 	)
			return Value;
		return "'" + Value + "'";
	}
}
