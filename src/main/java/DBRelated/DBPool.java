package DBRelated;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import static sun.management.jmxremote.ConnectorBootstrap.DefaultValues.PORT;

/**
 *
 * @author XTREMSOFT
 */
public class DBPool {

    //public static String DATABASE_IP = "172.16.14.106";  //Ventura
    //public static String DATABASE_IP = "xtrembonbon-xtremsofts3.rhcloud.com";  //Openshift
    public static String DATABASE_IP = "127.11.90.130";  //Openshift
    //public static String DATABASE_IP = "XPC-17\\SQLEXPRESS"; //LOCAL

////    /**
    /*PORT NO
     */
    //public static String DATABASE_PORT = "1433";
    public static String DATABASE_PORT = "3306";//openshift

    /**
     * DATABASE NAME
     */
    public static String DATABASE_NAME = "leave"; //OpenShift
    //public static String DATABASE_NAME = "notification";  //SF

    /**
     * USER NAME
     */
    //public static String DATABASE_USERNAME = "test";
    public static String DATABASE_USERNAME = "adminVLFB3HU";// opensiftBonBon;
    //  public static String DATABASE_USERNAME = "xtremsoft";  //Ventura

    /**
     * PASSWORD FOR DATABASE
     */
    public static String DATABASE_PWD = "ZKp5BKCvjW2D";
    public static final String DATABASE_CLASSNAME = "com.mysql.jdbc.Driver";

    /**
     * DATABASE URL
     */
    //public static final String DATABASE_URL = "jdbc:mysql://" + DATABASE_IP +":" +DATABASE_PORT +","  + "database=" + DATABASE_NAME;
    public static final String DATABASE_URL = "jdbc:mysql://" + DATABASE_IP + ":" + DATABASE_PORT + "/" + DATABASE_NAME;

    /**
     * Log
     */
    private static ArrayList<String> log = new ArrayList(1024);

//no of shared Connections
    static {
        try {
            Class.forName(DATABASE_CLASSNAME);
            log = new ArrayList(1024);

        } catch (Exception ex) {
            System.out.println(ex.toString());

        }
    }

    /**
     *
     * @return
     */
    public static Connection get() {
        try {
            return DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PWD);
        } catch (SQLException ex) {
            System.out.println("Cannot get connection");
            System.out.println(ex.toString());
            log(ex);
        }
        return get();

    }

    /**
     *
     * @param s
     */
    public static void log(String s) {
        log.add(s);
    }

    /**
     *
     * @param s
     */
    public static void log(Exception s) {
        log.add((s.toString() + "," + Arrays.toString(s.getStackTrace())).replaceAll(",", "<br />"));
    }

    /**
     *
     * @param out
     * @throws IOException
     */
    public static void printLog(Writer out) throws IOException {
        log.add("Testing");
        log.add("xtrem");
        for (String s : log) {
            out.write(s + "<br />");
        }
        log.clear();
    }
}
