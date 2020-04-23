package com.smarttools.database;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

	private static final Logger logger = Logger.getLogger(DBConnection.class);

	private static DBConnection instance;

    private Connection connection;

    private DBConnection(){

    }
    public static DBConnection getInstance(){
        if(instance==null){
            instance=new DBConnection();
        }
        return instance;
    }


    public String readProperties(String prop) {
        try {
            Properties p = new Properties();
            p.load(DBConnection.class.getResourceAsStream("/dbconf.properties"));
            return p.getProperty(prop);
        }  catch (IOException e) {
            logger.fatal(e);
        }
        return "";
    }




    public void closeConnection() {
        try {
            if (this.connection != null)
                this.connection.close();
        } catch (SQLException e) {
            logger.error(e);

        }
    }

    public void setAutoCommit(boolean ac) {
        try {
            this.connection.setAutoCommit(ac);
        } catch (SQLException e) {
            logger.error(e);
        }
    }
    public Connection getConnection(){
        return connection;
    }


}
