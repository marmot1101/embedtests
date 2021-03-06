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
import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.hsqldb.Server;
import org.hsqldb.lib.tar.DbBackup;

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
            String tableName = obj.createSixWideTable();
            obj.bigWriteTest(tableName);
            //obj.test();
            obj.backupDB();
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
    
    public String exportTable(String tableName)throws Exception{
//    String fileName="export"+new Random().nextInt(999)+".csv";
//    String callString = "CALL CSVWRITE('"+fileName+"', 'SELECT * FROM "+tableName+"')";
//    PreparedStatement ps = conn.prepareCall(callString);
//    ps.execute();
//    ps.close();
//    return fileName;
        return "";
  }
  public  void backupDB()throws Exception{
      String sql = "BACKUP DATABASE TO 'bk.tgz'";
      Statement s = conn.createStatement();
      s.execute(sql);
      s.close();
    
    
      
  }
  
  public  void importTable(String tableName, String fileName)throws Exception{
//    String sql = "CREATE TABLE "+tableName+"re AS SELECT * FROM CSVREAD('"+fileName+"')";
//    PreparedStatement ps = conn.prepareCall(sql);
//    ps.execute();
//    ps.close();
//    sql = "SELECT * FROM "+tableName+"re WHERE ID<5";
//    ps = conn.prepareStatement(sql);
//    ResultSet rs = ps.executeQuery();
//    while(rs.next()){
//      System.out.println(rs.getString(1));
//      System.out.println(rs.getString(2));
//      System.out.println(rs.getString(3));
//    }
//    rs.close();
//    ps.close();
  }
}
