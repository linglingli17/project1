/**
 * Created by Administrator on 2017/2/12.
 */
import static spark.Spark.*;
import org.slf4j.spi.*;
import org.slf4j.impl.*;
import org.slf4j.*;
import java.lang.Integer;
public class LUNService {
    public LUNService(final LUNManager lunManager){

        //get the size of a LUN
        get("/luns/:id/size", (request, response) -> {
            Integer idLUN = new Integer(request.params(":id"));
            if (idLUN != null) {
                int size = lunManager.getLUNSize(idLUN);
                return "LUN id: " + idLUN + ", size: " + size;
            } else {
                response.status(404); // 404 Not found
                return "LUN id" + idLUN + "not found";
            }
        });

        //get the exported host id for a LUN
        get("/luns/:id/export", (request, response) -> {
            Integer idLUN = new Integer(request.params(":id"));
            if (idLUN != null) {
                Integer hostID = lunManager.getExportedHostID(idLUN);
                return "LUN id: " + idLUN + ", export to host: " + hostID;
            } else {
                response.status(404); // 404 Not found
                return "LUN id" + idLUN + "not found";
            }
        });

        post("/luns/operation/creation/:size", (request, response) -> {
            Integer size = new Integer(request.params(":size"));
            Integer idLUN= null;
            if(size != null){
                idLUN = lunManager.createLUN(size);
            }
            if(idLUN != null){
                return "create LUN id: " + idLUN ;
            }
            else{
                response.status(401);
                return "LUN node found";
            }
        });

        post("/luns/operation/creation/:size/:count", (request, response) -> {
            Integer size = new Integer(request.params(":size"));
            Integer count = new Integer(request.params(":count"));

            if(size != null && count!= null){
                if(lunManager.createMultipleLUNs(count,size)){
                    response.status(200);
                    return "Action succeeded";
                }
            }else{
                    response.status(401);
                }
            return "Action failed";
        });

        put("/luns/operation/:id/export/:hostid", (request, response) -> {
            Integer idLUN = new Integer(request.params(":id"));
            Integer hostID = new Integer(request.params(":hostid"));
            if (idLUN != null) {
                if(lunManager.exportLUN(idLUN,hostID)){
                response.status(200);
                return "Action succeeded";
            }
        }else{
            response.status(401);
        }
            return "Action failed";
        });

        put("/luns/operation/:id/unexportion", (request, response) -> {
            Integer idLUN = new Integer(request.params(":id"));

            if (idLUN != null) {
                if( lunManager.unexportLUN(idLUN)){
                    response.status(200);
                    return "Action succeeded";
                }
            }else{
                response.status(401);
            }
            return "Action failed";
        });

        delete("/luns/operation/:id/removal", (request, response) -> {
            Integer idLUN = new Integer(request.params(":id"));

            if (idLUN != null) {
                if( lunManager.removeUnexportedLUN(idLUN)){
                    response.status(200);
                    return "Action succeeded";
                }
            }else{
                response.status(401);
            }
            return "Action failed";
        });

    }

    public static void main(String[] args) {
        new LUNService(LUNManager.getInstance());
    }
}
