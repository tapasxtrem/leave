package service;

import DBRelated.DBOperation;
import com.google.appengine.repackaged.org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class details extends HttpServlet{
    
   @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processReq(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         processReq(req,resp);
    }

    private void processReq(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {      
            String reqOBJ = org.apache.commons.io.IOUtils.toString(req.getInputStream());
            JSONObject jsonReq = new JSONObject(reqOBJ); 
            
            DBOperation db = new DBOperation();
            JSONObject result = new JSONObject();
            db.getIpDetails(jsonReq.getString("username"), jsonReq.getLong("date"),result);
            
           
            
            resp.setContentType("text/html;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            result.write(out);
            
        } catch (Exception e) {
           DBRelated.DBPool.log("Exception: "+e);
        }
    } 
    
}
