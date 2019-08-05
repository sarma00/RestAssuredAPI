package com.sarma00.api.dbUtilities;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection_MYSQL {
	
	 private static DBConnection_MYSQL instance = null;
	    private Connection conn = null;

	    private DBConnection_MYSQL() {
	    }

	    public static DBConnection_MYSQL getInstance() {
	        if(instance == null) {
	            instance = new DBConnection_MYSQL();
	        }

	        return instance;
	    }

	    private void init() throws Exception {
	        String url = "jdbc:mysql://hofdhsmysql3.intra.searshc.com:3372/hs_gift_card_qa";
	        String user = "hs_gift_card_app";
	        String pwd = "HSgiftC1";
	        Class.forName("com.mysql.jdbc.Driver");
	        this.conn = DriverManager.getConnection(url, user, pwd);
	    }

	    public Connection getConectionObject() throws Exception {
	        this.init();
	        return this.conn;
	    }

	    public void closeDBConnection() throws Exception {
	        getInstance().conn.close();
	    }
	}
