/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddeddbtests;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.h2.tools.RunScript;
import org.h2.tools.Server;

/**
 *
 * @author jorr
 */
public class H2LoadLive {
  Server server;
  Connection conn;
  
  public static void main(String args[])throws Exception{
    H2LoadLive h2ll= new H2LoadLive();
    h2ll.run();
    System.exit(0);
  }
  public Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:h2:tcp://localhost/~/vlmetrics2", "josh", "cleo");
        
    }
  public void startServer()throws Exception{
    String [] serverArgs = {"-tcpAllowOthers"};
    server = Server.createTcpServer(serverArgs);
    if (!server.isRunning(true)) {
      server.start();
    }
  }
  public void stopServer()throws Exception{
    if(conn!=null){
      conn.close();
    }
    server.stop();
    server.shutdown();
  }
  public void restore() throws Exception{
    clearTables();
    RunScript.execute(conn, new FileReader("hybriddb.sql"));
  }
  
  public void clearTables()throws Exception{
    PreparedStatement ps = null;
    DatabaseMetaData md = conn.getMetaData();
    System.out.println(md.getDatabaseProductVersion());
    String[] type = {"TABLE"};
    ResultSet rs = md.getTables(null, null, "%", type);
    while (rs.next()) {
      ps = conn.prepareStatement("DROP TABLE " + rs.getString("TABLE_NAME"));
      ps.execute();
      ps.close();
    }
    rs.close();
    if(ps!=null){
      ps.close();
    }
  }
  public void showTables()throws Exception{
    DatabaseMetaData md = conn.getMetaData();
    String[] type = {"TABLE"};
    ResultSet rs = md.getTables(null, null, "%", type);
    while (rs.next()) {
      System.out.println("Table Name:"+rs.getString("TABLE_NAME"));
    }
  }
  public void run()throws Exception{
    startServer();
    conn = getConnection();
    restore();
    showTables();
    stopServer();
    
  }
  
  
  
  
}
