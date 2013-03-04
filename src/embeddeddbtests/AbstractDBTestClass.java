/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddeddbtests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jorr
 */
public abstract class AbstractDBTestClass {
  String AUTONUM="";
  String DEFAULTTS="NOW()";
  final int BIGTESTCOUNT = 1000000;
  final int TESTCOUNT = 1000;
  final int SMALLTESTCOUNT=5;
  int coreCount = Runtime.getRuntime().availableProcessors();
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
       String tableName = createSixWideTable(defaultTimeStamp);
       String refTableName = createRefTable(defaultTimeStamp);
       System.out.println("Starting "+TESTCOUNT+" sequential writes");
       long timeIn = System.nanoTime();
       sequentialWriteTest(tableName);
       long timeOut = System.nanoTime()-timeIn;
       System.out.println(TESTCOUNT+" writes done, time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");
       System.out.println("Starting table export");
       timeIn = System.nanoTime();
       String bkFileName = exportTable(tableName);
       timeOut = System.nanoTime()-timeIn;
       System.out.println("Export done, time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");     
       System.out.println("Starting table import");
       timeIn = System.nanoTime();
       importTable(tableName,bkFileName);
       timeOut = System.nanoTime()-timeIn;
       System.out.println("Import done, time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");     
//       dropTable(tableName);
//       tableName=createSixWideTable(defaultTimeStamp);
//       System.out.println("Starting "+TESTCOUNT+" sequential writes committing on 50");
//       timeIn = System.nanoTime();
//       sequentialWriteTestAutoCommitOff(tableName);
//       timeOut = System.nanoTime()-timeIn;
//       System.out.println(TESTCOUNT+" writes done, time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");     
       System.out.println("Starting "+TESTCOUNT+" sequential reads");
       timeIn=System.nanoTime();
       sequentialReadTest(tableName);
       timeOut = System.nanoTime()-timeIn;
       System.out.println(TESTCOUNT+" reads done, time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");     
       System.out.println("Starting "+TESTCOUNT+" sequential selects");
       timeIn=System.nanoTime();
       sequentialSelectTest(tableName);
       timeOut = System.nanoTime()-timeIn;
       System.out.println(TESTCOUNT+" selects done("+TESTCOUNT/2+" per select) ,time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");     
       System.out.println("Starting "+SMALLTESTCOUNT+" sequential join selects");
       timeIn=System.nanoTime();
       sequentialSelectJoinTest(tableName,refTableName);
       timeOut = System.nanoTime()-timeIn;
       System.out.println(SMALLTESTCOUNT+" join selects done("+TESTCOUNT/2+" per select) ,time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");     
      
       dropTable(tableName);
       tableName = createSixWideTable(defaultTimeStamp);
       System.out.println("Starting "+TESTCOUNT+" concurrent writes");
       timeIn=System.nanoTime();
       concurrentWriteTest(tableName);
       timeOut = System.nanoTime()-timeIn;
       System.out.println(TESTCOUNT+" concurrent writes done("+(coreCount+2) +" concurrency) ,time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");     
       System.out.println("Starting "+TESTCOUNT+" concurrent reads");
       timeIn=System.nanoTime();
       concurrentReadTest(tableName);
       timeOut = System.nanoTime()-timeIn;
       System.out.println(TESTCOUNT+" concurrent reads done("+(coreCount+2) +" concurrency) ,time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");     
       System.out.println("sleeping to allow concurrent writes to finish");
       dropTable(tableName);
       tableName=createSixWideTable(defaultTimeStamp);
       System.out.println("Starting "+BIGTESTCOUNT+" writes");
       timeIn=System.nanoTime();
       bigWriteTest(tableName);
       timeOut = System.nanoTime()-timeIn;
       System.out.println(BIGTESTCOUNT+" sequential writes, time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");     
       System.out.println("Starting "+BIGTESTCOUNT+" sequential reads");
       timeIn=System.nanoTime();
       bigSequentialReadTest(tableName);
       timeOut = System.nanoTime()-timeIn;
       System.out.println(BIGTESTCOUNT+" sequential reads, time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");     
       
       System.out.println("Starting "+BIGTESTCOUNT+" sequential selects, capped at 250 records per");
       timeIn=System.nanoTime();
       bigSequentialSelectTest(tableName);
       timeOut = System.nanoTime()-timeIn;
       System.out.println(BIGTESTCOUNT+" sequential selects, time to complete: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS)+ "ms");     
       
       
    }
  
  public void sequentialWriteTest(String tableName)throws Exception{
    for(int i=0;i<TESTCOUNT;i++){
        String sql = "INSERT INTO "+tableName+"(ID, name,status,statusDetail,vltraderid) VALUES (?,?,?,?,?)";
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
        ps.setInt(5, i%2==0 ? 1 : 777);
        ps.execute();
        ps.close();
      }
    }
  public void sequentialWriteTestAutoCommitOff(String tableName)throws Exception{
    conn.setAutoCommit(false);
    for(int i=0;i<TESTCOUNT;i++){
        String sql = "INSERT INTO "+tableName+"(ID, name,status,statusDetail,vltraderid) VALUES (?,?,?,?,?)";
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
        ps.setInt(5, i%2==0 ? 1 : 777);
        ps.execute();
        ps.close();
        if(i%5==0){
          conn.commit();
        }
      }
    }
    
    public String createSixWideTable()throws Exception{
      return createSixWideTable(DEFAULTTS);
    }
    public String createSixWideTable(String defaultTimeStamp)throws Exception{
        //5 wide table
        String tableName = "sixWideTable";
        //ID Field
        HashMap<String,String[]> fields = new HashMap<String,String[]>();
        String fieldName = "ID";
        String[] mods = {"INT","PRIMARY KEY"};
        fields.put(fieldName,mods);
        fieldName = "vltraderID";
        String[] mods5 = {"INT", "NOT NULL"};
        fields.put(fieldName,mods5);
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
    public String createRefTable(String defaultTimeStamp)throws Exception{
      String tableName = "refTable";
      HashMap<String,String[]> fields = new HashMap<String,String[]>();
        String fieldName = "ID";
        String[] mods = {"INT","PRIMARY KEY"};
        fields.put(fieldName,mods);
        fieldName="vltradername";
        String[] mods2 = {"VARCHAR(50)"};
        fields.put(fieldName,mods2);
        createTable(tableName, fields);
        String sql = "INSERT INTO refTable(ID,vltradername) VALUES(?,?)";
        for(int i=0;i<1000;i++){
          PreparedStatement ps = conn.prepareStatement(sql);
          ps.setInt(1, i);
          if(i==1){
            ps.setString(2,"vltrader1");
          }else if(i==777){
            ps.setString(2, "vltrader2");
          }else{
            ps.setString(2, UUID.randomUUID().toString().substring(1, 15));
          }
          ps.execute();
        }
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
    public void sequentialSelectJoinTest(String tableName, String refTableName) throws Exception{
      String sql = "SELECT ";
      sql += tableName + ".ID,";
      sql += tableName + ".name,";
      sql += tableName + ".writetime,";
      sql += tableName + ".status,";
      sql += tableName + ".statusDetail,";
      sql += refTableName+".vltradername";
      sql += " FROM " + tableName+","+refTableName + " WHERE "+tableName+".name=? ";
      for(int i=0;i<SMALLTESTCOUNT;i++){
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, i%2==1 ? name1 : name2);
        ResultSet rs = ps.executeQuery();
        rs.close();
        ps.close();
      }
    }
    
    public void concurrentWriteTest(String tableName) throws Exception {
    ExecutorService pool = Executors.newFixedThreadPool(coreCount);
    for (int i = 0; i < TESTCOUNT; i++) {
      pool.submit(new DBWriteTask( i, tableName));
      
      System.gc();
    }
    
    pool.shutdown();
    System.out.println("Waiting for write threads to finish...");
      pool.awaitTermination(2, TimeUnit.MINUTES);
    
  }
    public void concurrentReadTest(String tableName) throws Exception {
    ExecutorService pool = Executors.newFixedThreadPool(coreCount);
    for (int i = 0; i < TESTCOUNT; i++) {
      pool.submit(new DBReadTask(i, tableName));
      
    }    
    pool.shutdown();
    System.out.println("Waiting for write threads to finish...");
      pool.awaitTermination(2, TimeUnit.MINUTES);
  }
    public void bigWriteTest(String tableName) throws Exception {
    for (int i = 0; i < BIGTESTCOUNT; i++) {
      String sql = "INSERT INTO " + tableName + "(ID, name,status,statusDetail,vltraderid) VALUES (?,?,?,?,?)";
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setInt(1, i);
      ps.setString(2, i % 2 == 0 ? name1 : name2);
      if (i % 3 == 0) {
        ps.setString(3, status1);
        ps.setString(4, "");
      } else {
        ps.setString(3, i % 3 == 1 ? status2 : status3);
        ps.setString(4, i % 2 == 0 ? statusDetail1 : statusDetail2);
      }
      ps.setInt(5, i % 2 == 0 ? 1 : 777);
      ps.execute();
      ps.close();
    }
  }  
    public void bigSequentialReadTest(String tableName) throws Exception{
      for(int i=0;i<BIGTESTCOUNT;i++){
        PreparedStatement ps = conn.prepareStatement("SELECT ID,name,writetime,status,statusDetail FROM "+tableName+" WHERE ID=?");
        ps.setInt(1, i);
        ResultSet rs = ps.executeQuery();
        rs.close();
        ps.close();        
      }
    }
    public void bigSequentialSelectTest(String tableName) throws Exception{
      for(int i=0;i<BIGTESTCOUNT;i++){
        PreparedStatement ps = conn.prepareStatement("SELECT ID,name,writetime,status,statusDetail FROM "+tableName+ " WHERE name=? AND ID<500");
        ps.setString(1, i%2==1 ? name1 : name2);
        ResultSet rs = ps.executeQuery();
        rs.close();
        ps.close();
      }
    }

  public abstract Connection getConnection() throws Exception;

  private final class DBWriteTask implements Callable {

    Connection conn;
    int count;
    String tableName;

    public DBWriteTask( int count, String tableName) {
      
      this.count = count;
      this.tableName = tableName;
    }

    public Object call() {
      try {
        conn = getConnection();
        String sql = "INSERT INTO " + tableName + "(ID, name,status,statusDetail,vltraderid) VALUES (?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, count);
        ps.setString(2, count % 2 == 0 ? name1 : name2);
        if (count % 3 == 0) {
          ps.setString(3, status1);
          ps.setString(4, "");
        } else {
          ps.setString(3, count % 3 == 1 ? status2 : status3);
          ps.setString(4, count % 2 == 0 ? statusDetail1 : statusDetail2);
        }
        ps.setInt(5, count % 2 == 0 ? 1 : 777);
        ps.execute();
        ps.close();
        conn.close();
        System.gc();
        

      } catch (Exception e) {
        System.out.println("DB WRITE NUM:" + count);
        e.printStackTrace();
      }

      return null;
    }
  }

  private final class DBReadTask implements Callable {

    Connection conn;
    int count;
    String tableName;

    public DBReadTask( int count, String tableName) {
      
      this.count = count;
      this.tableName = tableName;
    }

    public Object call() {
      try {
        conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT ID,name,writetime,status,statusDetail FROM " + tableName + " WHERE ID=?");
        ps.setInt(1, count);
        ResultSet rs = ps.executeQuery();
        rs.close();
        ps.close();
        conn.close();
        System.gc();

      } catch (Exception e) {
        System.out.println("DB WRITE NUM:" + count);
        e.printStackTrace();
      }

      return null;
    }
  }
  public abstract void backupDB()throws Exception;
  public abstract void restoreDB() throws Exception;
  public abstract String exportTable(String tableName)throws Exception;
  public abstract void importTable(String tableName, String fileName)throws Exception;

}
