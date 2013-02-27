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
import org.h2.tools.Server;

/**
 *
 * @author josh
 */
public class H2test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            H2test obj = new H2test();
            obj.init();
            obj.closeAndStop();
            obj.test();
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    Connection conn;
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
    public void createTable(String name, HashMap<String,String[]> fields) throws Exception{
        String sql= "CREATE TABLE "+name+"(";
        for(String fieldName:fields.keySet()){
            if(!sql.endsWith("("))sql+=",";
            sql+=fieldName = " ";
            String mods[] = fields.get(fieldName);
            for(String mod:mods){
                sql+=mod+" ";
            }
        }
        sql+=")";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.execute();
        ps.close();
    }
    public void dropTable(String name)throws Exception{
        PreparedStatement ps = conn.prepareStatement("DROP TABLE "+name);
        ps.execute();
        ps.close();
    }
    public void test()throws Exception{
       String tableName = createFiveWideTable();
               
    }
    public void tenThousandSequentialWrites(String tableName){
        
    }
    
    public String createFiveWideTable()throws Exception{
        //5 wide table
        String tableName = "fiveWideTable";
        //ID Field
        HashMap<String,String[]> fields = new HashMap<String,String[]>();
        String fieldName = "ID";
        String[] mods = {"INT","PRIMARY KEY", "AUTO_INCREMENT"};
        fields.put(fieldName,mods);
        //TimeStamp Field
        fieldName="writetime";
        String[] mods1 = {"TIMESTAMP", "DEFAULT", "NOW()"};
        fields.put(fieldName, mods1);
        //Name Field
        fieldName="name";
        String[] mods2 = {"VARCHAR(50)"};
        fields.put(fieldName,mods2);
        //Status 1 field
        fieldName="status1";
        String[] mods3 = {"VARCHAR(50)"};
        fields.put(fieldName,mods3);
        //Status 2 field
        fieldName="status2";
        String[] mods4 = {"VARCHAR(50)"};
        fields.put(fieldName, mods4);
        
        createTable(tableName,fields);
        return tableName;
    }
}
