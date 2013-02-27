/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddeddbtests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jorr
 */
public abstract class AbstractDBTestClass {
  String AUTONUM="";
  String DEFAULTTS="NOW()";
  final int TESTCOUNT = 10000;
  Connection conn;
  String name1 = "Widgit";
  String name2 = "flashyThing";
  String status1 = "Good";
  String status2 = "Warning";
  String status3 = "Problem";
  String statusDetail1 = "Partial";
  String statusDetail2 = "WindowsME_BROKE";
  public abstract void init() throws Exception;
  public abstract void closeAndStop()throws Exception;
  public void test()throws Exception{
    test(DEFAULTTS);
  }
  public void test(String defaultTimeStamp)throws Exception{
       String tableName = createFiveWideTable(defaultTimeStamp);
       System.out.println("Starting "+TESTCOUNT+" sequential writes");
       long timeIn = System.nanoTime();
       sequentialWriteTest(tableName);
       long tenkseq = System.nanoTime()-timeIn;
       System.out.println(TESTCOUNT+" writes done, time to complete: " + TimeUnit.MILLISECONDS.convert(tenkseq, TimeUnit.NANOSECONDS)+ "ms");     
       System.out.println("Starting "+TESTCOUNT+" sequential reads");
       timeIn=System.nanoTime();
       sequentialReadTest(tableName);
       tenkseq = System.nanoTime()-timeIn;
       System.out.println(TESTCOUNT+" reads done, time to complete: " + TimeUnit.MILLISECONDS.convert(tenkseq, TimeUnit.NANOSECONDS)+ "ms");     
       System.out.println("Starting "+TESTCOUNT+" sequential selects");
       timeIn=System.nanoTime();
       sequentialSelectTest(tableName);
       tenkseq = System.nanoTime()-timeIn;
       System.out.println(TESTCOUNT+" selects done("+TESTCOUNT/2+" per select) ,time to complete: " + TimeUnit.MILLISECONDS.convert(tenkseq, TimeUnit.NANOSECONDS)+ "ms");     
    }
  public void sequentialWriteTest(String tableName)throws Exception{
    for(int i=0;i<TESTCOUNT;i++){
        String sql = "INSERT INTO "+tableName+"(ID, name,status,statusDetail) VALUES (?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, i);
        ps.setString(2, i%2==0 ? name1 : name2);
        if(i%3==0){
          ps.setString(3, status1);
          ps.setString(4, "");
        }else{
          ps.setString(3, i%3==1 ? status2 : status3);
          ps.setString(4, i%2==0 ? statusDetail1 : statusDetail2);
        }
        ps.execute();
        ps.close();
      }
    }
    
    public String createFiveWideTable()throws Exception{
      return createFiveWideTable(DEFAULTTS);
    }
    public String createFiveWideTable(String defaultTimeStamp)throws Exception{
        //5 wide table
        String tableName = "fiveWideTable";
        //ID Field
        HashMap<String,String[]> fields = new HashMap<String,String[]>();
        String fieldName = "ID";
        String[] mods = {"INT","PRIMARY KEY"};
        fields.put(fieldName,mods);
        //TimeStamp Field
        fieldName="writetime";
        String[] mods1 = {"TIMESTAMP", "DEFAULT", defaultTimeStamp};
        fields.put(fieldName, mods1);
        //Name Field
        fieldName="name";
        String[] mods2 = {"VARCHAR(50)"};
        fields.put(fieldName,mods2);
        //Status 1 field
        fieldName="status";
        String[] mods3 = {"VARCHAR(50)"};
        fields.put(fieldName,mods3);
        //Status 2 field
        fieldName="statusDetail";
        String[] mods4 = {"VARCHAR(50)"};
        fields.put(fieldName, mods4);
        
        createTable(tableName,fields);
        return tableName;
    }
    public void createTable(String name, HashMap<String,String[]> fields) throws Exception{
        String sql= "CREATE TABLE "+name+"(";
        for(String fieldName:fields.keySet()){
          
            if(!sql.endsWith("("))sql+=",";
            sql+=fieldName + " ";
            String mods[] = fields.get(fieldName);
            if(mods==null){
              throw new Exception("null mod field name:"+fieldName);
            } else {
            for (String mod : mods) {
              sql += mod + " ";
            }
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
    
    public void testRead(String tableName)throws Exception{
      PreparedStatement ps = conn.prepareStatement("SELECT * from " + tableName + " WHERE ID>3000 AND ID<3015");
      ResultSet rs = ps.executeQuery();
      while(rs.next()){
        System.out.println("Rec " + rs.getInt("ID")+": " +rs.getString("name")+", "+rs.getString("status")+"--"+rs.getString("statusDetail"));
      }
      rs.close();
      ps.close();
    }
    
    public void sequentialReadTest(String tableName) throws Exception{
      for(int i=0;i<TESTCOUNT;i++){
        PreparedStatement ps = conn.prepareStatement("SELECT ID,name,writetime,status,statusDetail FROM "+tableName+" WHERE ID=?");
        ps.setInt(1, i);
        ResultSet rs = ps.executeQuery();
        rs.close();
        ps.close();        
      }
    }
    
    public void sequentialSelectTest(String tableName) throws Exception{
      for(int i=0;i<TESTCOUNT;i++){
        PreparedStatement ps = conn.prepareStatement("SELECT ID,name,writetime,status,statusDetail FROM "+tableName+ " WHERE name=?");
        ps.setString(1, i%2==1 ? name1 : name2);
        ResultSet rs = ps.executeQuery();
        rs.close();
        ps.close();
      }
      
    }
  
}
