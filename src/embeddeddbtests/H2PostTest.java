/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddeddbtests;

import java.sql.Connection;
import java.sql.DriverManager;
import org.h2.tools.Server;

/**
 *
 * @author jorr
 */
public class H2PostTest {
  public static void main(String args[]){
    
  }
  Server server;
  public void init()throws Exception{
    long timeIn = System.nanoTime();
    System.out.println("Starting H2...");
    Class.forName("org.h2.Driver");
    server = Server.createTcpServer();
    Connection conn = conn = DriverManager.getConnection("jdbc:h2:h2test;", "sa", "");;
  }
}
