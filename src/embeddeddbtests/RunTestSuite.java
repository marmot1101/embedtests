/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddeddbtests;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author jorr
 */
public class RunTestSuite {
  public static void main(String args[])throws Exception{
    //h2
    long startTime = System.nanoTime();
    AbstractDBTestClass obj = new H2test();
    obj.init();
    obj.test();
    obj.closeAndStop();
    long timeOut = System.nanoTime() - startTime;
    System.out.println("Total H2 Time: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS) + "ms");
    System.gc();
    //hsql
    startTime = System.nanoTime();
    obj = new HSQLtest();
    obj.init();
    obj.test();
//            String tableName = obj.createSixWideTable(DEFAULTTS);
//            obj.concurrentWriteTest(tableName);
    obj.closeAndStop();
    timeOut = System.nanoTime() - startTime;
    System.out.println("Total HSQL Time: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS) + "ms");
    System.gc();
    //derby
    startTime=System.nanoTime();
    obj = new DerbyTest();
      obj.init();
      obj.test(DerbyTest.DEFAULTTS);
//      String tableName = obj.createSixWideTable(DEFAULTTS);
//      obj.concurrentWriteTest(tableName);
      obj.closeAndStop();
      timeOut = System.nanoTime() - startTime;
    System.out.println("Total Derby Time: " + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.NANOSECONDS) + "ms");
    System.gc();
      System.exit(0);
  }
}
