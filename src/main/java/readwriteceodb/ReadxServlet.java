/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package readwriteceodb;

import DBRelated.DBOperation;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author DELL
 */
public class ReadxServlet extends HttpServlet {

    
    
    private static final String DESTINATION_DIR_PATH = "info.conf";
    byte[] keyBytes = "8AEE9DF494A23A36".getBytes();  //16 character len key
    final byte[] ivBytes = "QH7Yz5363CE98U22".getBytes(); // must be 16 bytes
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try  {
            System.out.println(11);
            PrintWriter out = response.getWriter();
            String imeiCode = request.getHeader("XTREM");
            System.out.println(imeiCode);
            if(imeiCode.equals("xtremceodbxHawkeye")){
                //DBRelated.DBPool.log("Path :: "+ DESTINATION_DIR_PATH + " : Real : " + getServletContext().getContextPath());
                //File file = new File(DESTINATION_DIR_PATH);
            
                /*final SecretKey key = new SecretKeySpec(keyBytes, "AES");
                final IvParameterSpec IV = new IvParameterSpec(ivBytes);
                final Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, key, IV);
                // creting output stream for file and ciher
                FileInputStream fin = new FileInputStream(file);
                CipherInputStream cfin = new CipherInputStream(fin, cipher);
            // read file and start update
                String s = "";
                while (fin.available() > 0) {
                    byte buffer[] = new byte[1024];
                    int len = cfin.read(buffer);
                    s += new java.lang.String(buffer, "UTF-8");
                }
                
                cfin.close();
                s = s.trim();*/
               //DBOperation db = new DBOperation();
                //String s = db.getCEODBIPPORT();
                String s = "\"CEO11#219.64.39.25:5054#ven<>CEO12#115.113.49.8:51527#trust<>CEO13#61.12.61.163:51527#bp\" +\n" +
"\"<>CEO14#180.179.74.213:51527#jm<>CEO15#182.73.217.6:51527#elite\" +\n" +
"\"<>CEO16#220.224.237.68:51527#pravin<>CEOLEQ1#180.179.110.105:51527#lkp\" +\n" +
"\"<>CEO18#117.239.28.218:51527#9star<>CEO19#110.173.180.67:51527#choice\" +\n" +
"\"<>CEO20#220.227.52.156:51527#emkay<>CEO25#111.93.167.222:51527#proficient\" +\n" +
"\"<>CEO26#220.226.199.246:51527#reliance<>CEO27#14.141.196.190:51527#networth\" +\n" +
"\"<>CEO28#220.226.199.246:51527#reliance<>CEO29#59.163.57.102:51527#wellworth\" +\n" +
"\"<>ceoleq1#180.179.110.105:51527#lkp<>ceoleq2#180.179.110.105:51527#lkp\" +\n" +
"\"<>CEOREQ9#220.226.199.246:51526#reliance<>CEO30#59.163.57.102:51527#wellworth\" +\n" +
"\"<>KOTDEL1#mhawkeye.kotakcommodities.com#kotak<>KOTHYD1#mhawkeye.kotakcommodities.com#kotak\" +\n" +
"\"<>XTRAGS2#103.17.48.35:51527#agshares<>KOTMUM#mhawkeye.kotakcommodities.com#kotak\"";
                out.print(s);
            }
            else{
                out.print("Please contact admin...");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
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