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
import org.h2.tools.Server;

/**
 *
 * @author jorr
 */
public class H2PostTest {
  public Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");
    }
    public static void main(String args[])throws Exception{
        H2PostTest hpt = new H2PostTest();
        hpt.test();
    }
    public void test() throws Exception {
        Connection conn = getConnection();
        DatabaseMetaData md = conn.getMetaData();
        System.out.println(md.getDatabaseMajorVersion());
        System.out.println(md.getDatabaseMinorVersion());
        String[] type = {"TABLE"};
        ResultSet rs = md.getTables(null, null, "%", type);
        String tableName="";
        while(rs.next()){
         tableName=rs.getString("TABLE_NAME");
        }
        rs.close();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM "+tableName+" WHERE ID<10");
        rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println("ID = " + rs.getInt("ID") + "name = " + rs.getString("name"));
        }
        rs.close();
        ps.close();
        
        conn.close();
    }
}
