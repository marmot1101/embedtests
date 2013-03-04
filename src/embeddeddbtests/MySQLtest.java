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
public class MySQLtest extends AbstractDBTestClass{
  
  public static void main(String args[])throws Exception{
    MySQLtest obj = new MySQLtest();
    obj.init();
    obj.test();
    System.exit(0);
    
  }
  
  public void init()throws Exception{
    Class.forName("com.mysql.jdbc.Driver");
    conn = getConnection();
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

    rs.close();
    ps.close();
    System.out.println("wiping burn in table");
    ps = conn.prepareStatement("DROP TABLE burnin");
    ps.execute();
    ps.close();
  }
  
  
  public void closeAndStop()throws Exception{
    
  }
  public Connection getConnection()throws Exception{
    
    return DriverManager.getConnection("jdbc:mysql://localhost/embedtest?user=embed&password=embed");
  
  }
  public  void backupDB()throws Exception{}
  public  void importTable(String tableName,String fileName)throws Exception{}
  public  String exportTable(String tableName)throws Exception{return null;}
  
}
