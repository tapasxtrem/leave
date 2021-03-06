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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Goutam
 */
public class ipinsertnew extends HttpServlet {

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            GlobalClass gclass = new GlobalClass();
            String requestString = gclass.getStringFromRequest(request);
            DBPool.log("ipinsertNEW Req param :: \n" + requestString);
            JSONObject jsonReq = new JSONObject(requestString);            
            DBOperation db = new DBOperation();
            JSONObject result = new JSONObject();
            db.ipinsertMeth(jsonReq.getString("username"), jsonReq.getString("ip"), jsonReq.getString("datetime"),
                    jsonReq.getString("imei"),jsonReq.getString("mobile"),jsonReq.optString("totaluses"),jsonReq.optString("sl"),result);
            result.write(out);
            
        } catch (JSONException ex) {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
