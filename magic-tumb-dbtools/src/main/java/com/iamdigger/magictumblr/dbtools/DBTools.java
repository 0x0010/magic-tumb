package com.iamdigger.magictumblr.dbtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.h2.Driver;
import org.h2.tools.DeleteDbFiles;

/**
 * @author Sam
 * @since 3.0.0
 */
public class DBTools {

  private static String dbPath;
  private static String dbName;
  private static String dbUrl;
  private static Properties dbProp = new Properties();

  public static void main(String[] args) throws IOException {
    if (null != args && args.length == 1) {
      dbProp.load(DBTools.class.getResourceAsStream("/db.properties"));
      dbName = dbProp.getProperty("db.name");
      dbPath = dbProp.getProperty("db.path");
      dbUrl = String.format("jdbc:h2:%s/%s;FILE_LOCK=SOCKET", dbPath, dbName);

      Driver.load();
      switch (args[0].toLowerCase()) {
        case "delete":
          delete();
          break;
        case "init":
          init();
          break;
        case "execute":
          execute("");
          execute("1");
          break;
        default:
          System.out.println("Unknown operation specified");
      }
    } else {
      System.out.println("No operation specified");
    }
  }

  private static void delete() {
    DeleteDbFiles.execute(dbPath, dbName, false);
    System.out.println("database deleted.");
  }

  private static void init() {
    delete();
    try {
      create();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private static void create() throws SQLException, IOException {
    Statement statement = null;
    Connection connection = null;
    BufferedReader bufferedReader = null;
    try {
      connection = tryToGetConnection();
      statement = connection.createStatement();

      bufferedReader = new BufferedReader(
          new InputStreamReader(DBTools.class.getResourceAsStream("/schema.sql")));
      String sqlLine;
      while (null != (sqlLine = bufferedReader.readLine())) {
        if (!isEmpty(sqlLine) && !sqlLine.trim().startsWith("--")) {
          statement.execute(sqlLine);
        }
      }
      System.out.println("database created;");
    } finally {
      if (null != statement) {
        statement.close();
      }
      releaseConnection(connection);
      if (null != bufferedReader) {
        bufferedReader.close();
      }
    }
  }

  private static Connection tryToGetConnection() throws SQLException {
    String userName = dbProp.getProperty("db.username");
    String password = dbProp.getProperty("db.password");
    return DriverManager.getConnection(dbUrl, userName, password);
  }

  private static void releaseConnection(Connection connection) throws SQLException {
    if (null != connection) {
      connection.close();
    }
  }

  private static boolean isEmpty(String str) {
    return ((str == null) || (str.length() == 0));
  }

  private static void execute(String sqlFile) {
    System.out.println(sqlFile);
  }
}
