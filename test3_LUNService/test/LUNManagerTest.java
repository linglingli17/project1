import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import sun.rmi.server.InactiveGroupException;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/12.
 */
class LUNManagerTest {

    private static LUNManager lunManager;
    private static Integer  nodeIDSimpleTest;
    @BeforeEach
    void setUp() {
        //vector
        //System.out.println("Run setUp");
        //ArrayList<Byte> storage = new ArrayList<Byte>(3);
        //for(Integer i = 0; i < 3; ++i) {
         //   storage.add((byte)0);
        //}
        lunManager = LUNManager.getInstance();
        Assertions.assertNotNull(lunManager,"Create single instance LUNManager failed");
        //System.out.printf("Get a single instance of LUNManager %s\n",lunManager.toString());
        //nodeIDSimpleTest = lunManager.createLUN(1);
    }

    @AfterEach
    void tearDown() {
        //System.out.println("in tearDown");

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
        //Assertions.assertTrue(ret);
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
    void persist() {

    }

}