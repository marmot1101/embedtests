/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddeddbtests;

import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.h2.tools.RunScript;
import org.h2.tools.Script;
import org.h2.tools.Server;
import org.hsqldb.server.WebServer;

/**
 *
 * @author josh
 */
public class H2test extends AbstractDBTestClass {

  final static String AUTONUM = "AUTO_INCREMENT";

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
    
    long start = threadBean.getCurrentThreadCpuTime();
    
    try {
      H2test obj = new H2test();
      obj.init();
      //obj.test();
      String tableName = obj.createSixWideTable();
      
      obj.bigWriteTest(tableName);
      boolean test=true;
      System.out.println("entering server mode");
//      while(test){
//        Thread.sleep(1000);
//      }
      
      System.out.println("bk");
      obj.backupDB();
      System.out.println("restore");
      obj.restore();
      obj.testRead(tableName);
    //  obj.closeAndStop();
      long finish = threadBean.getCurrentThreadCpuTime();
    System.out.println("start:"+start);
    System.out.println("finish:"+finish);
      
      System.exit(0);

    } catch (Exception e) {
      e.printStackTrace();
    }
    

  }
  Server server;
  WebServer ws;

  public void init() throws Exception {
    //setup server and connection
    long timeIn = System.nanoTime();
    System.out.println("Starting H2...");
    Class.forName("org.h2.Driver");
    String[] options = {"-tcpAllowOthers"};
    server = Server.createTcpServer(options);
    
    
    if(!server.isRunning(true)){
      
        server.start();
      }
//    boolean test=true;
//    while(test){
//      
//    }
    
    conn = DriverManager.getConnection("jdbc:h2:h2test;", "sa", "");
    long tenkseq = System.nanoTime() - timeIn;
    System.out.println("H2 started in: " + TimeUnit.MILLISECONDS.convert(tenkseq, TimeUnit.NANOSECONDS) + "ms");
    PreparedStatement ps = null;
    DatabaseMetaData md = conn.getMetaData();
    String[] type = {"TABLE"};
    ResultSet rs = md.getTables(null, null, "%", type);

    while (rs.next()) {
      ps = conn.prepareStatement("DROP TABLE " + rs.getString("TABLE_NAME"));
      ps.execute();
      ps.close();
    }
    rs.close();
    System.out.println("creating burn in table");
    ps = conn.prepareStatement(
            "CREATE TABLE burnin(ID INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50))");
    ps.execute();
    ps.close();
    System.out.println("write burn in...");
    for (int i = 0; i < 1000; i++) {
      ps = conn.prepareStatement("INSERT INTO burnin(name) VALUES (?)");
      ps.setString(1, "name" + i);
      ps.execute();
      ps.close();
    }
//    System.out.println("read burn in...");
//    ps = conn.prepareStatement("select * from burnin");
//    rs = ps.executeQuery();
//    int printCounter = 0;
//    while (rs.next()) {
//      printCounter++;
//      if (printCounter % 250 == 0) {
//        System.out.println("\tname = " + rs.getString("name"));
//      }
//    }
    rs.close();
    ps.close();
    System.out.println("wiping burn in table");
    ps = conn.prepareStatement("DROP TABLE burnin");
    ps.execute();
    ps.close();
  }

  public void closeAndStop() throws Exception {
    conn.close();
    server.stop();
  }
  public Connection getConnection()throws Exception{
    return DriverManager.getConnection("jdbc:h2:h2test;", "sa", "");
  }
  public String exportTable(String tableName)throws Exception{
    String fileName="export"+new Random().nextInt(999)+".csv";
    String callString = "CALL CSVWRITE('"+fileName+"', 'SELECT * FROM "+tableName+"')";
    PreparedStatement ps = conn.prepareCall(callString);
    ps.execute();
    ps.close();
    return fileName;
  }
  public  void backupDB()throws Exception{
    conn.close();
    Script.execute("jdbc:h2:h2test;", "sa", "", "bk2.sql");
    conn = getConnection();
  }
  
  public void restore() throws Exception{
    clearTables();
    RunScript.execute(conn, new FileReader("bk2.sql"));
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
    ps.close();
  }
  
  public  void importTable(String tableName, String fileName)throws Exception{
    String sql = "CREATE TABLE "+tableName+"re AS SELECT * FROM CSVREAD('"+fileName+"')";
    PreparedStatement ps = conn.prepareCall(sql);
    ps.execute();
    ps.close();
    sql = "SELECT * FROM "+tableName+"re WHERE ID<5";
    ps = conn.prepareStatement(sql);
    ResultSet rs = ps.executeQuery();
    while(rs.next()){
      System.out.println(rs.getString(1));
      System.out.println(rs.getString(2));
      System.out.println(rs.getString(3));
    }
    rs.close();
    ps.close();
  }
}
