/**
 * Created by Administrator on 2017/2/10.
 */


import java.io.*;
import java.util.ArrayList;

public class LUNNode implements Serializable {
    private Integer ID;
    private Integer size;
    private Integer exportHostID;
    private transient ArrayList<Byte> storage;

    public LUNNode(Integer ID, Integer size)
    {
    	this.ID = ID;
		this.size = size;
        storage = new ArrayList<Byte>(size);
        //for(Integer i = 0; i < size; ++i) {
           // storage.add((byte)0);
        //}
    }
    public int getID(){
        return ID;
    }

    public Integer getExportHostID(){
        return exportHostID;
    }
    public void setExportHostID(Integer exportHostID){
        this.exportHostID = exportHostID;
    }
    public int getSize(){
        //return storage.size();
        return size;
    }
    
    public synchronized void expand(int capacity){
       	storage.ensureCapacity(capacity);
       	this.size = capacity;
    }

    //@Override
    //public String toString(){
        //return "[LUN ID " + ID + ",export Host ID " + exportHostID + ",size " + storage.size() + "]";
    //}

    private void writeObject( ObjectOutputStream oos) throws IOException{
        //oos.defaultWriteObject();
        String str = new String("LUN ID " + ID + ",export Host ID " + exportHostID + ",size " + size);
        oos.writeBytes(str);
    }
    public boolean persist(){
        String fileName = ID + ".txt";
        File file = new File(fileName);
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(file));
            outStream.writeObject(this);
            outStream.close();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
