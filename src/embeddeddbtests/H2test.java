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
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.h2.tools.Server;

/**
 *
 * @author josh
 */
public class H2test extends AbstractDBTestClass{
    final static String AUTONUM = "AUTO_INCREMENT";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            H2test obj = new H2test();
            obj.init();
            obj.test();
            obj.closeAndStop();
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    Server server;
    public void init()throws Exception{
        //setup server and connection
        server = Server.createTcpServer();
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:~/test;", "sa", "");
        PreparedStatement ps = null;
        DatabaseMetaData md = conn.getMetaData();
        String[] type = {"TABLE"};
        ResultSet rs = md.getTables(null, null, "%", type);
        
        while (rs.next()) {
            ps = conn.prepareStatement("DROP TABLE "+rs.getString("TABLE_NAME"));
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
            ps.setString(1, "name"+i);
            ps.execute();
            ps.close();
        }
        System.out.println("read burn in...");
        ps = conn.prepareStatement("select * from burnin");
        rs = ps.executeQuery();
        int printCounter=0;
        while(rs.next()){
            printCounter++;
            if(printCounter % 250==0){
                System.out.println("\tname = " + rs.getString("name"));
            }
        }
        rs.close();
        ps.close();
        System.out.println("wiping burn in table");
        ps = conn.prepareStatement("DROP TABLE burnin");
        ps.execute();
        ps.close();
    }
    public void closeAndStop()throws Exception{
        conn.close();
        server.stop();
    }
}
