
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2017/2/10.
 * LUNManager is a singleton class instance which functions as a simulator
 * to manage a pool of LUNs.
 *
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

    /**
     *  Create a single LUN that will allocate a unique id as a key to put to the free LUN pool
     * @param size   the initial size of a LUN to create
     * @return     the id of the newly created LUN
     */
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

    /**
     * Create the number of Count LUN, each has an initial size of nodeSize.Each
     * LUN will be put to the free LUN pool
     * @param count   The number of LUN to create
     * @param nodeSize  The initial size for each LUN
     * @return true if all required LUN created successfully, false if any is failed
     */
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

    /**
     * Export a specific LUN to the specified host. The LUN exported will be removed from the free pool
     * and add to the map of exportLUN for manangement.
     * @param idLUN   The id of the LUN, which is the unique key for this LUN when initially created
     * @param hostID  The host ID to export this LUN to
     * @return   true if the specified LUN is free for use, and successfully exported to a host.
     */
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

    /**
     * Unexport a specific LUN, which will cleared of the export host information, remmoved from
     * the export map, and add to the un-exported map for management
     * @param idLUN   The id of the LUN
     */
    public boolean unexportLUN(Integer idLUN)
    {
    	LUNNode node = null;
        synchronized (exportLock) {
	    	node = exportedLUNMap.get(idLUN);
	        if(node == null)
	            return false;
	        //Clear the export info field of the LUN node after unexported
	        node.setExportHostID(null);
	
	        //Remove the node from the exported map, then add to the unexported map
	        exportedLUNMap.remove(idLUN);
    	}
        unexportedLUNMap.put(idLUN, node);
    	return true;
    }

    public boolean removeUnexportedLUN(Integer idLUN)
    {
        LUNNode node = null;
        synchronized (exportLock) {
            node = exportedLUNMap.get(idLUN);
            if(node == null)
                return false;
            unexportedLUNMap.remove(idLUN);
        }
        return true;
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

    /**
     * Get the size of the LUN specified by the LUN ID
     * @param idLUN   the ID of the LUN
     * @return   the size of the LUN
     */
    public int getLUNSize(Integer idLUN)
    {
        //Search the exported map
        LUNNode node = getLUNNode(idLUN);
        if (node != null){
            return node.getSize();
        }
        return 0;
    }

    /**
     * Get the exported hostID of the LUN
     * @param idLUN  the ID of the LUN
     * @return  the hostID of the LUN exported to
     */
    public Integer getExportedHostID(Integer idLUN){
        //Search the exported map
        LUNNode node = getLUNNode(idLUN);
        if (node != null){
            return node.getExportHostID();
        }
        return null;
    }

    /**
     *  Expand the size of the LUN
     * @param idLUN   the ID of the LUN
     * @param capacity  the size to expand to
     */
    public  void Expand(Integer idLUN, int capacity){
        LUNNode node = getLUNNode(idLUN);
        if(node != null){
            node.expand(capacity);
        }
    }

    /**
     * Get the total number of LUNs created and exist in the LUN pool
     * @return the number of LUNs exist in the pool
     */
    public Integer getTotalNumLuns(){
        return freeLUNPool.size() + exportedLUNMap.size() + unexportedLUNMap.size();
    }

    /**
     * Get the total number of LUNs exist in the free LUN pool
     * @return the number of free LUNs
     */
    public Integer getTotalFreeLuns(){
        return freeLUNPool.size();
    }

    /**
     * Get the number of exported LUNs
     * @return the number of exported LUNs
     */
    public Integer getTotalExportedLuns(){
        return exportedLUNMap.size();
    }

    /**
     * Get the number of unexported LUNs
     * @return the number of unexported LUNS
     */
    public Integer getTotalUnexportedLuns(){
        return unexportedLUNMap.size();
    }
    /**
     * Persist the LUN info to file
     * @param idLUN
     */
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
