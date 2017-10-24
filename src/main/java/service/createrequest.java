/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import DBRelated.DBOperation;
import DBRelated.DBPool;
import Support.GlobalClass;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class createrequest extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try  {
            GlobalClass gclass = new GlobalClass();
            String requestString = gclass.getStringFromRequest(request);
            JSONObject jsonReq = new JSONObject(requestString);
            
            DBPool.log("createrequest Req param :: \n" + requestString);
            
            DBOperation db = new DBOperation();
            JSONObject result = new JSONObject();
            db.insertQuarycreaterequest(jsonReq.getString("username"), jsonReq.getString("fromdate"), jsonReq.getString("todate"), 
                    jsonReq.getString("mobile"), jsonReq.getString("leavetype"), jsonReq.getString("reason"), jsonReq.getString("msg"), result);
            result.write(out);
            
        } catch (JSONException ex) {
            DBPool.log(ex);
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
