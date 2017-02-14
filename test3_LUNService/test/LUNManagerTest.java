import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2017/2/12.
 */
class LUNManagerTest {

    private static LUNManager lunManager;
    private static Integer  nodeIDSimpleTest;
    @BeforeEach
    void setUp() {

        lunManager = LUNManager.getInstance();
        Assertions.assertNotNull(lunManager,"Create single instance LUNManager failed");
        System.out.printf("Get a single instance of LUNManager %s\n",lunManager.toString());
        //nodeIDSimpleTest = lunManager.createLUN(1);
    }

    @AfterEach
    void tearDown() {
        System.out.println("in tearDown");
        lunManager = null;

    }

    @Test
    void createLUN() {
        System.out.println("in createLUN");
        Integer ret = lunManager.createLUN(1);
        Assertions.assertNotNull(ret);
    }

    @Test
    void createMultipleLUNs() {
        System.out.println("in createMultipleLUNs");
        boolean ret = lunManager.createMultipleLUNs(3, 2);
        Assertions.assertTrue(ret);
    }

    @Test
    void exportLUN_unexportLUN() {
        System.out.println("in exportLUN_unexportLUN");
        Integer idLUN = lunManager.createLUN(1);
        if (idLUN != null)
        {
            //Get the original size of the node
            Integer orgSize = lunManager.getLUNSize(idLUN);
            Assertions.assertEquals(orgSize,new Integer(1));

            lunManager.Expand(idLUN, 10);
            Integer newSize = lunManager.getLUNSize(idLUN);
            Assertions.assertEquals(newSize,new Integer(10));

            lunManager.exportLUN(idLUN,1);
            Integer hostID = lunManager.getExportedHostID(idLUN);
            Assertions.assertEquals(new Integer(1),hostID);

            lunManager.unexportLUN(idLUN);
            hostID = lunManager.getExportedHostID(idLUN);
            Assertions.assertEquals(null,hostID);

            LUNNode freenode = lunManager.getFreeLUNNode(idLUN);
            Assertions.assertNull(freenode);

            lunManager.removeUnexportedLUN(idLUN);
            LUNNode node = lunManager.getLUNNode(idLUN);
            Assertions.assertNull(node);
        }
    }


    @Test
    void removeUnexportedLUN() {

    }

    @Test
    void getLUNSize() {

    }


    @Test
    void concurrentCreate() throws InterruptedException{
        int threadNum = 5;
        int lunNum = 3;

        CountDownLatch latch = new CountDownLatch(threadNum);

        Integer totalLun = threadNum * lunNum;

        for ( int i = 0; i < threadNum; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < lunNum; j++) {
                            lunManager.createLUN(1);
                            System.out.println( "created LUN id:" + j);
                        }
                        latch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(100);
                    }
                }
            }).start();
        }
        latch.await();
        Integer totalLUN_inpool = lunManager.getTotalFreeLuns();
        Assertions.assertEquals(totalLun,totalLUN_inpool);
    }
}