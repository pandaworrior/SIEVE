@AOSETTABLE CREATE TABLE address(
	@NORMALINTEGER addr_id int not null, 
	@NORMALSTRING addr_street1 varchar(40), 
	@NORMALSTRING addr_street2 varchar(40), 
	@NORMALSTRING addr_city varchar(30), 
	@NORMALSTRING addr_state varchar(20), 
	@NORMALSTRING addr_zip varchar(10), 
	@NORMALINTEGER addr_co_id int, 
	PRIMARY KEY(addr_id)
	);

CREATE TABLE author ( 
	@NORMALINTEGER a_id int not null, 
	@NORMALSTRING a_fname varchar(20), 
	@NORMALSTRING a_lname varchar(20), 
	@NORMALSTRING a_mname varchar(20), 
	@NORMALDATETIME a_dob date, 
	@NORMALSTRING a_bio varchar(1500), 
	PRIMARY KEY(a_id)
	);
	    
@AOSETTABLE CREATE TABLE cc_xacts ( 
	@NORMALINTEGER cx_o_id int not null, 
	@NORMALSTRING cx_type varchar(10), 
	@NORMALSTRING cx_num varchar(20), 
	@NORMALSTRING cx_name varchar(30), 
	@NORMALDATETIME cx_expire date, 
	@NORMALSTRING cx_auth_id char(15), 
	@NORMALDOUBLE cx_xact_amt double, 
	@NORMALDATETIME cx_xact_date date, 
	@NORMALINTEGER cx_co_id int, 
	PRIMARY KEY(cx_o_id)
	);

CREATE TABLE country ( 
	@NORMALINTEGER co_id int not null, 
	@NORMALSTRING co_name varchar(50), 
	@NORMALDOUBLE co_exchange double, 
	@NORMALSTRING co_currency varchar(18), 
	PRIMARY KEY(co_id)
	);

@AUSETTABLE CREATE TABLE customer ( 
	@NORMALINTEGER c_id int not null, 
	@NORMALSTRING c_uname varchar(20), 
	@NORMALSTRING c_passwd varchar(20), 
	@NORMALSTRING c_fname varchar(17), 
	@NORMALSTRING c_lname varchar(17), 
	@NORMALSTRING c_addr_id int, 
	@NORMALSTRING c_phone varchar(18), 
	@NORMALSTRING c_email varchar(50), 
	@LWWDATETIME c_since date, 
	@LWWDATETIME c_last_login date, 
	@LWWDATETIME c_login timestamp, 
	@LWWDATETIME c_expiration timestamp, 
	@NORMALDOUBLE c_discount real, 
	@NORMALDOUBLE c_balance double, 
	@NORMALDOUBLE c_ytd_pmt double, 
	@NORMALDATETIME c_birthdate date, 
	@NORMALSTRING c_data varchar(1500), 
	PRIMARY KEY(c_id)
	);

@UOSETTABLE CREATE TABLE item ( 
	@NORMALINTEGER i_id int not null, 
	@NORMALSTRING i_title varchar(60), 
	@NORMALINTEGER i_a_id int, 
	@LWWDATETIME i_pub_date date, 
	@NORMALSTRING i_publisher varchar(60), 
	@NORMALSTRING i_subject varchar(60), 
	@NORMALSTRING i_desc varchar(1500), 
	@LWWINTEGER i_related1 int, 
	@LWWINTEGER i_related2 int, 
	@LWWINTEGER i_related3 int, 
	@LWWINTEGER i_related4 int, 
	@LWWINTEGER i_related5 int, 
	@LWWSTRING i_thumbnail varchar(40), 
	@LWWSTRING i_image varchar(40), 
	@NORMALDOUBLE i_srp double, 
	@LWWDOUBLE i_cost double, 
	@NORMALDATETIME i_avail date, 
	@NUMDELTAINTEGER i_stock int, 
	@NORMALSTRING i_isbn char(13), 
	@NORMALINTEGER i_page int, 
	@NORMALSTRING i_backing varchar(15), 
	@NORMALSTRING i_dimensions varchar(25), 
	PRIMARY KEY(i_id)
	);

@AOSETTABLE CREATE TABLE order_line ( 
	@NORMALINTEGER ol_id int not null, 
	@NORMALINTEGER ol_o_id int not null, 
	@NORMALINTEGER ol_i_id int, 
	@NORMALINTEGER ol_qty int, 
	@NORMALDOUBLE ol_discount double, 
	@NORMALSTRING ol_comments varchar(110), 
	PRIMARY KEY(ol_id, ol_o_id)
	);

@AOSETTABLE CREATE TABLE orders ( 
	@NORMALINTEGER o_id int not null, 
	@NORMALINTEGER o_c_id int, 
	@NORMALDATETIME o_date date, 
	@NORMALDOUBLE o_sub_total double, 
	@NORMALDOUBLE o_tax double, 
	@NORMALDOUBLE o_total double, 
	@NORMALSTRING o_ship_type varchar(10), 
	@NORMALDATETIME o_ship_date date, 
	@NORMALINTEGER o_bill_addr_id int, 
	@NORMALINTEGER o_ship_addr_id int, 
	@NORMALSTRING o_status varchar(15), 
	PRIMARY KEY(o_id)
	);

@AUSETTABLE CREATE TABLE shopping_cart ( 
	@NORMALINTEGER sc_id int not null, 
	@LWWDATETIME sc_time timestamp, 
	PRIMARY KEY(sc_id)
	);

@ARSETTABLE CREATE TABLE shopping_cart_line ( 
	@NORMALINTEGER scl_sc_id int not null, 
	@LWWINTEGER scl_qty int, 
	@NORMALINTEGER scl_i_id int not null, 
	PRIMARY KEY(scl_sc_id, scl_i_id)
	);