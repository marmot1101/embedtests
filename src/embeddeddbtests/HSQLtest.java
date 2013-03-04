/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddeddbtests;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;
import org.hsqldb.Server;

/**
 *
 * @author jorr
 */
public class HSQLtest extends AbstractDBTestClass{
  final static String AUTONUM = "GENERATED ALWAYS AS IDENTITY";
  static String DEFAULTTS="NOW()";
  public static void main(String[] args) {
        // TODO code application logic here
        try{
            HSQLtest obj = new HSQLtest();
            obj.init();
            //obj.test();
//            String tableName = obj.createSixWideTable(DEFAULTTS);
//            obj.concurrentWriteTest(tableName);
            obj.closeAndStop();
            System.exit(0);
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
  Server server;
  public void init()throws Exception{
    server = new Server();
    server.start();
    Class.forName("org.hsqldb.jdbc.JDBCDriver");
    long timeIn = System.nanoTime();
    System.out.println("Starting HSQLDB...");
    conn = DriverManager.getConnection("jdbc:hsqldb:file:hsqltest", "sa", "");
    long tenkseq = System.nanoTime()-timeIn;
    System.out.println("HSQLDB started in: " + TimeUnit.MILLISECONDS.convert(tenkseq, TimeUnit.NANOSECONDS)+ "ms");     
    
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
            "CREATE TABLE burnin(ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY , name VARCHAR(50))");
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
  public void closeAndStop()throws Exception{
    
      server.stop();
      conn.close();
  }
    public Connection getConnection()throws Exception{
      return DriverManager.getConnection("jdbc:hsqldb:file:hsqltest", "sa", "");
    }
    
    public  void backupDB()throws Exception{}
  public  void restoreDB() throws Exception{}
  public  void importTable(String tableName)throws Exception{}
  public  void exportTable(String tableName)throws Exception{}
}
