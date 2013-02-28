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

/**
 *
 * @author jorr
 */
public class DerbyTest extends AbstractDBTestClass{
  String AUTONUM="GENERATED ALWAYS AS IDENTITY";
  static String DEFAULTTS="current_timestamp";
  public static void main(String[] args) {
    // TODO code application logic here
    try {
      DerbyTest obj = new DerbyTest();
      obj.init();
      //obj.test(DEFAULTTS);
      String tableName = obj.createSixWideTable(DEFAULTTS);
      obj.concurrentWriteTest(tableName);
      obj.closeAndStop();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  public void init() throws Exception {
    String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    Class.forName(driver).newInstance();
    long timeIn = System.nanoTime();
    System.out.println("Starting Derby...");
    conn = DriverManager.getConnection("jdbc:derby:testDerby;create=true", "sa", "");
    long tenkseq = System.nanoTime()-timeIn;
    System.out.println("Derby started in: " + TimeUnit.MILLISECONDS.convert(tenkseq, TimeUnit.NANOSECONDS)+ "ms");     
    
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
    try{
      conn = DriverManager.getConnection("jdbc:derby:;shutdown=true");
    }catch(Exception e){
      
    }
    conn.close();
  }
  public Connection getConnection()throws Exception{
      return DriverManager.getConnection("jdbc:derby:testDerby", "sa", "");
    }
  
}
