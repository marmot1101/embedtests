/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddeddbtests;

import java.sql.Connection;
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
        return DriverManager.getConnection("jdbc:h2:h2test;", "sa", "");
    }
    public static void main(String args[])throws Exception{
        HSQLPostTest hpt = new HSQLPostTest();
        hpt.test();
    }
    public void test() throws Exception {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM sixWideTable WHERE ID<10");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println("ID = " + rs.getInt("ID") + "name = " + rs.getString("name"));
        }
        rs.close();
        ps.close();
        conn.close();
    }
}
