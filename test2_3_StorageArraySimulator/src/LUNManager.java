
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2017/2/10.
 */
public class LUNManager implements Runnable{
    private static AtomicInteger nextAvailbleId;
    private static LUNManager instance;
    private final Object exportLock;

    private  ConcurrentHashMap<Integer,LUNNode> freeLUNPool;
    private  ConcurrentHashMap<Integer,LUNNode> exportedLUNMap;
    private  ConcurrentHashMap<Integer,LUNNode> unexportedLUNMap;

    public static synchronized LUNManager getInstance()
    {
        if(instance == null){
            instance = new LUNManager();
        }
        return instance;
    }
    
    private LUNManager()
    {
        nextAvailbleId = new AtomicInteger();
        exportLock = new Object();
        freeLUNPool  = new ConcurrentHashMap<Integer,LUNNode>();
        exportedLUNMap = new ConcurrentHashMap<Integer,LUNNode>();
        unexportedLUNMap = new ConcurrentHashMap<Integer,LUNNode>();
    }

    public Integer createLUN(Integer size)
    {
    	Integer id = nextAvailbleId.incrementAndGet();
        LUNNode node = new LUNNode(id, size);
        if(node != null) {
            freeLUNPool.put(id, node);
            return id;
        }
        return null;
    }

    public  boolean createMultipleLUNs(Integer count,Integer nodeSize)
    {
        for (Integer i = 0; i < count; ++i)
        {
            if( createLUN(nodeSize) != null ){
                return false;
            }
        }
        return true;
    }

    public Integer getNextAvailLUN()
    {
        Iterator<Integer> it = freeLUNPool.keySet().iterator();
        if (it.hasNext()){
            return it.next();
        }
        return null;
    }
//    public boolean exportAvaiLUN(String hostID)
//    {
//        if(hostID == null)
//            return false;
//        //LUNNode node = freeLUNPool.poll();
//        //get a node from the free pool
//        Iterator<Integer> it = freeLUNPool.keySet().iterator();
//        if(it.hasNext()) {
//            Integer key = it.next();
//            LUNNode node = freeLUNPool.get(key);
//            it.remove();
//            if (node != null) {
//                //Record the export info in the LUN node after the LUN is exported
//                node.setExportInfo(hostID);
//                exportedLUNMap.put(key, node);
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean exportLUN(Integer idLUN,Integer hostID)
    {
        if(hostID == null)
            return false;

        LUNNode node = null;
        synchronized (exportLock) {
            //get a specific node from the free pool
            node = freeLUNPool.get(idLUN);
            if (node != null) {
                //Record the export info in the LUN node after the LUN is exported
                node.setExportHostID(hostID);
                //remove the node from the free pool
                freeLUNPool.remove(idLUN);
            }
        }
        if (node != null) {
	        exportedLUNMap.put(idLUN, node);
	        return true;
        }
       
        return false;
    }

    public void unexportLUN(Integer idLUN)
    {
    	LUNNode node = null;
        synchronized (exportLock) {
	    	node = exportedLUNMap.get(idLUN);
	        if(node == null)
	            return;
	        //Clear the export info field of the LUN node after unexported
	        node.setExportHostID(null);
	
	        //Remove the node from the exported map, then add to the unexported map
	        exportedLUNMap.remove(idLUN);
    	}
        unexportedLUNMap.put(idLUN, node);
    	
    }

    public void removeUnexportedLUN(Integer idLUN)
    {
        unexportedLUNMap.remove(idLUN);
    }

    public LUNNode getFreeLUNNode(Integer idLUN)
    {
        //Search from the free pool
        LUNNode node = freeLUNPool.get(idLUN);
        if (node != null){
            return node;
        }
        return null;
    }

    public LUNNode getLUNNode(Integer idLUN)
    {
        //Search the exported map
        LUNNode node = exportedLUNMap.get(idLUN);
        if (node != null){
            return node;
        }
        //search the unexported map
        node = unexportedLUNMap.get(idLUN);
        if (node != null){
            return node;
        }
        //search the free pool
        node = freeLUNPool.get(idLUN);
        if(node != null){
            return node;
        }
        return null;
    }
    
    public int getLUNSize(Integer idLUN)
    {
        //Search the exported map
        LUNNode node = getLUNNode(idLUN);
        if (node != null){
            return node.getSize();
        }
        return 0;
    }

    public Integer getExportedHostID(Integer idLUN){
        //Search the exported map
        LUNNode node = getLUNNode(idLUN);
        if (node != null){
            return node.getExportHostID();
        }
        return null;
    }

    public  void Expand(Integer idLUN, int capacity){
        LUNNode node = getLUNNode(idLUN);
        if(node != null){
            node.expand(capacity);
        }
    }

    public void persist(Integer idLUN)
    {
        LUNNode node = getLUNNode(idLUN);
        if(node != null){
            node.persist();
        }
    }

    @Override
    public void run() {
        
    }
}
