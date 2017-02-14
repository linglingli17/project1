/**
 * Created by Administrator on 2017/2/13.
 */
import static spark.Spark.*;

public class LUNWebservice {
    final static LUNManager lunManager = LUNManager.getInstance();
    public static void main(String[] args){

        //Request to create a LUN, will return the ID , the size for the LUN
        //is sent in the post body e.g. size=4
        post("/lun", (request, response) -> {

            String sizestr = request.queryParams("size");
            System.out.print(sizestr);
            Integer size = new Integer(sizestr);
            Integer idLUN= null;
            if(size != null){
                idLUN = lunManager.createLUN(size);
            }
            if(idLUN != null){
                response.status(201); // 201 Created
                return "create LUN id: " + idLUN + " with size:" + size;
            }
            else{
                response.status(401);
                return "Action failed";
            }
        });

        //Request to create the number of count LUNs, each has initial size
        //The param is sent in the post body e.g. count=5&size=10
        post("/luns", (request, response) -> {
            Integer size = new Integer(request.queryParams("size"));
            Integer count = new Integer(request.queryParams("count"));
            System.out.println(size);
            System.out.println(count);
            if(size != null && count!= null){
                System.out.println("start to create multiple");
                if(lunManager.createMultipleLUNs(count,size)){
                    response.status(201);
                    return "Action succeeded";
                }
            }else{
                response.status(401);
            }
            return "Action failed";
        });

        //get the information of a LUN
        get("/luns/:id", (request, response) -> {
            Integer idLUN = new Integer(request.params(":id"));
            System.out.println("To retrieve the export informaton of LUN id=" + idLUN);
            if (idLUN != null) {
                int size = lunManager.getLUNSize(idLUN);
                Integer hostID = lunManager.getExportedHostID(idLUN);
                response.status(201);
                return "LUN id: " + idLUN + ", size: " + size + ",export host:" + hostID;
            } else {
                response.status(404); // 404 Not found
                return "LUN id" + idLUN + "not found";
            }
        });

        get("/luns", (request, response) -> {
            String type = request.queryParams("type");
            int num = 0;
            if(type == "free") {
                num = lunManager.getTotalFreeLuns();
                response.status(201);
                return "Total number of free LUNs: " + num;
            }
            else if(type=="exported")
            {
                num = lunManager.getTotalExportedLuns();
                response.status(201);
                return "Total number of exported LUNs: " + num;
            }
            else
            {
                num = lunManager.getTotalUnexportedLuns();
                response.status(201);
                return "Total number of unexported LUNs: " + num;
            }
        });

        //export the LUN to a specific host, the request body eg. id=5&hostid=10
        put("/luns", (request, response) -> {
            Integer idLUN = new Integer(request.queryParams("id"));
            Integer hostID = new Integer(request.queryParams("hostid"));
            if (idLUN != null) {
                if(lunManager.exportLUN(idLUN,hostID)){
                    response.status(201);
                    return "Action succeeded";
                }
            }else{
                response.status(401);
            }
            return "Action failed";
        });

        //unexport the LUN,the request body eg. id=5&hostid=0
        put("/luns/unexport/:id", (request, response) -> {
            Integer idLUN = new Integer(request.params(":id"));

            if (idLUN != null) {
                if( lunManager.unexportLUN(idLUN)){
                    response.status(201);
                    return "Action succeeded";
                }
            }else{
                response.status(401);
            }
            return "Action failed";
        });

        delete("/luns/:id", (request, response) -> {
            Integer idLUN = new Integer(request.params(":id"));

            if (idLUN != null) {
                if( lunManager.removeUnexportedLUN(idLUN)){
                    response.status(201);
                    return "Action succeeded";
                }
            }else{
                response.status(401);
            }
            return "Action failed";
        });

    }
}
