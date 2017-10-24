/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DBRelated;

import Support.DateUtil;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelStructure.reportRowDetails;
import org.json.simple.parser.JSONParser;


/**
 *
 * @author XTREMSOFT
 */
public class DBOperation {
    
     Connection conn = null;
     public final String AUTH_KEY_FCM = "AIzaSyAgDiYKYn0En5VUvJf-MlobdBHYy8wa0aA";
     public final String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    
    

    
    public DBOperation(){
        conn = (Connection) DBPool.get();
        DBPool.log("Connection establish");
    }
    public CallableStatement callSP(String spDetail) throws SQLException{//Proc_GuestUsers(?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        return conn.prepareCall("{call "+spDetail+"}");
    }
    
    
    public void getNetworkReport(String userId,JSONObject respObj) throws SQLException{
        try {
            
        } catch (Exception e) {
        }
    }
    
    
    public void insertTest(String name,String mobile,JSONObject obj) throws JSONException{
        try {                       
            CallableStatement stmt = conn.prepareCall("call procedure_test(?,?)");
            stmt.setString(1, name);
            stmt.setString(2,mobile);
             ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                obj.put("status", "success");
            }
        } catch (SQLException e) {
            DBRelated.DBPool.log("SQLException: "+e);
            obj.put("status", "fail");
        }catch(Exception e){
            DBRelated.DBPool.log("Exception: "+e);  
            obj.put("status", "fail");
        }        
    }
    
   
    public void insertQuaryclient(String username,String phone, String imei, 
                                        String regID,String model,String os,JSONObject obj) throws JSONException{
        try {
            int noOfUpdate = 0;
            CallableStatement  stmt = conn.prepareCall("call proc_clientlogin(?,?,?,?,?,?)");
                stmt.setString(1, imei);
                stmt.setString(2, username);
                stmt.setString(3, phone);
                stmt.setString(4, model);
                stmt.setString(5, os);
                stmt.setString(6, regID);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                noOfUpdate = rs.getInt(1);
            }
            conn.close();
            obj.put("updatecount", noOfUpdate);
            if(noOfUpdate>0) {
                obj.put("msg", "Welcome " + username);
                obj.put("status", "success");
            }
            else{
                obj.put("msg", "There is some issue please try after some times.");
                obj.put("status", "failure");
            }
        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        } catch (JSONException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        }
    }
    
    
    
    public void selectQuaryclienttype(String username,String mobile, String imei,JSONObject obj) throws JSONException{
        try {
            PreparedStatement  stmt = conn.prepareStatement("select clienttype from client where username='" + username + "' and phone='"+mobile + "' and imei='"+imei + "';");
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()){
                int clType = resultSet.getInt("clienttype");
                if(clType == 1){
                    obj.put("clienttype", "admin");
                }
                else{
                    obj.put("clienttype", "client");
                }
            }
            obj.put("status", "success");
            conn.close();
            
        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        } catch (JSONException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        }
    }
    
    public void selectQuaryclientIPlist(String username,JSONObject obj) throws JSONException{
        try {
            PreparedStatement  stmt = conn.prepareStatement("select ip, datetime from clientip where username='" + username + "' order by datetime desc LIMIT 0,20;");
            ResultSet resultSet = stmt.executeQuery();
            JSONArray jsonArr = new JSONArray();
            while (resultSet.next()) {
                String ip = resultSet.getString("ip");
                String dateTime = resultSet.getString("datetime");
                JSONObject row = new JSONObject();
                row.put("ip", ip);
                row.put("datetime", DateUtil.ConvertDataFormat1(dateTime));
                
                jsonArr.put(row);
            }
            obj.put("data", jsonArr);
            obj.put("status", "success");
            conn.close();
            
        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        }
    }
    
    public void insertQuarycreaterequest(String username, String fromdate, String todate,
            String mobile, String leavetype, String reason, String msg, JSONObject obj) throws JSONException, Exception {
        try {
            PreparedStatement stmt = conn.prepareStatement(""
                    + "insert into createrequest (username, fromdate,todate,mobile,leavetype,"
                    + "reason,msg) values (?,?,?,?,?,?,?);");
            stmt.setString(1, username);
            stmt.setString(2, DateUtil.NToDT(Long.parseLong(fromdate)));
            stmt.setString(3, DateUtil.NToDT(Long.parseLong(todate)));
            stmt.setString(4, mobile);
            stmt.setString(5, leavetype);
            stmt.setString(6, reason);
            stmt.setString(7, msg);
            int noOfUpdate = stmt.executeUpdate();
            obj.put("updatecount", noOfUpdate);
            if (noOfUpdate > 0) {
                obj.put("msg", "Leave Request added successfully.");
                obj.put("status", "success");
                CallableStatement callableStatement = conn.prepareCall("call proc_get_adminregid()");
                ResultSet rSet = callableStatement.executeQuery();
                while (rSet.next()) {
                    String adminRegId = rSet.getString("regid");
                    if (!adminRegId.equalsIgnoreCase("")) {   
                        DBPool.log("RegID : " + adminRegId);
                        senNotificationToAdmin(adminRegId, username, leavetype, reason, fromdate, todate);
                    }
                }
            } else {
                obj.put("msg", "There is some issue please try after some times.");
                obj.put("status", "failure");
            }
            conn.close();

        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        } catch (JSONException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        }
    }
     
     public void getIPUsers(JSONObject obj) throws JSONException{
          try {
            PreparedStatement  stmt = conn.prepareStatement("SELECT DISTINCT username FROM  `client`");
            ResultSet resultSet = stmt.executeQuery();
            JSONArray jsonArr = new JSONArray();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                if (!username.trim().equalsIgnoreCase("")) {
                    JSONObject row = new JSONObject();
                    row.put("username", username);
                    jsonArr.put(row);
                }
            }
            
            stmt = conn.prepareStatement("SELECT ip FROM  `staticip`");
            resultSet = stmt.executeQuery();
            if(resultSet.next()){
                String staticIp = resultSet.getString("ip");
                obj.put("staticip", staticIp);
            }
            else{
                obj.put("staticip", "");
            }
            obj.put("data", jsonArr);
            obj.put("status", "success");
            conn.close();
            
        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        } catch (JSONException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
         }
     }
    public void UsersList(JSONObject obj) throws JSONException{
        try {
            PreparedStatement  stmt = conn.prepareStatement("select username, mobile from createrequest");
            ResultSet resultSet = stmt.executeQuery();
            JSONArray jsonArr = new JSONArray();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String mobile = resultSet.getString("mobile");
                JSONObject row = new JSONObject();
                row.put("username", username);
                row.put("mobile", mobile);
                
                jsonArr.put(row);
            }
            obj.put("data", jsonArr);
            obj.put("status", "success");
            conn.close();
            
        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        }
    }
    public void approvedrequest(JSONObject obj, int status) throws JSONException{
        try {
            PreparedStatement  stmt = conn.prepareStatement("select sl, username,fromdate, todate,leavetype,reason,msg from createrequest where status = " +status+";");
            ResultSet resultSet = stmt.executeQuery();
            JSONArray jsonArr = new JSONArray();
            while (resultSet.next()) {
                String slN = resultSet.getString("sl");
                String name = resultSet.getString("username");
                String fromdate = resultSet.getString("fromdate");
                String todate = resultSet.getString("todate");
                String leavetype = resultSet.getString("leavetype");
                String reason = resultSet.getString("reason");
                String msg = resultSet.getString("msg");
                JSONObject row = new JSONObject();
                row.put("sl", slN);
                row.put("username", name);
                row.put("fromdate", fromdate);
                row.put("todate", todate);
                row.put("leavetype", leavetype);
                row.put("reason", reason);
                row.put("msg", msg);
                
                jsonArr.put(row);
            }
            obj.put("data", jsonArr);
            obj.put("status", "success");
            conn.close();
            
        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        }
   }
    public void createviewrequest(String username,JSONObject obj) throws JSONException{
        try {
            
            PreparedStatement  stmt;
            if(!username.equalsIgnoreCase("")){
                stmt = conn.prepareStatement("select sl,fromdate, todate,leavetype,reason,status from createrequest where username='" + username + "';");
            }
            else{
                stmt = conn.prepareStatement("select sl,fromdate, todate,leavetype,reason,status from createrequest where status = 0;");
            }
            ResultSet resultSet = stmt.executeQuery();
            JSONArray jsonArr = new JSONArray();
            while (resultSet.next()) {
                String slNo = resultSet.getString("sl");
                String fromdate = resultSet.getString("fromdate");
                String todate = resultSet.getString("todate");
                String leavetype = resultSet.getString("leavetype");
                String reason = resultSet.getString("reason");
                String status = resultSet.getString("status");
                JSONObject row = new JSONObject();
                row.put("sl", slNo);// change in 180217
                row.put("fromdate", fromdate);
                row.put("todate", todate);
                row.put("leavetype", leavetype);
                row.put("reason", reason);
                row.put("status", status);
                
                jsonArr.put(row);
            }
            obj.put("data", jsonArr);
            conn.close();
            
        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        }
   }
    public void approvealeave(String sl,String status,JSONObject obj) throws JSONException{
        try {
            PreparedStatement stmt = conn.prepareStatement("update createrequest set status="+status+" where sl = " + sl + ";");
            int rowCount = stmt.executeUpdate();
            
            if(rowCount > 0) {
                obj.put("status", "success");
                obj.put("msg", "Successfully updated");
            }
            
            conn.close();

        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        }
   }
    public void leavebalance(String username,JSONObject obj) throws JSONException{
        try {
            
            CallableStatement   stmt;
            
             stmt = conn.prepareCall("call balanceforleave(?)");
             stmt.setString(1, username);
           
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String username1 = resultSet.getString("username");
                String totalLeave = resultSet.getString("totalleave");
                String balanceleave = resultSet.getString("balanceleave");
                
                obj.put("username", username1);
                obj.put("totalleave", totalLeave);
                obj.put("balanceleave", balanceleave);
                obj.put("status", "success");
            }
            conn.close();
            
        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        }
   }
   public void ipinsertMeth(String username,String ip, String datetime, String imei,
            String mobile,String totaluses,String sl,JSONObject obj) throws JSONException{
        try {
            if(sl.equalsIgnoreCase("")){
                sl = "0";
            }
            if(totaluses.equalsIgnoreCase("")){
                totaluses = "0";
            }
            CallableStatement  stmt = conn.prepareCall("call PROC_IPINSERT(?,?,?,?,?,?,?)");
            stmt.setString(1, username);
            stmt.setString(2, ip);
            stmt.setString(3, DateUtil.NToDT(Long.parseLong(datetime)));
            stmt.setString(4, mobile);
            stmt.setString(5, imei);
            stmt.setInt(6, Integer.parseInt(totaluses));
            stmt.setInt(7, Integer.parseInt(sl));

            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                obj.put("sl", resultSet.getInt("sl") +"");
                obj.put("status", "success");
               // obj.put(mobile, clctn);
            }
            else{
                obj.put("sl",0 + "");
                obj.put("status", "failure");
            }
            conn.close();
            
        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("sl",0 + "");
            obj.put("status", "failure");
        } catch (JSONException ex) {
            DBPool.log(ex);
            obj.put("sl",0+"");
            obj.put("status", "failure");
        }
    }
   public void ipUsesMeth(String username,String fromDate, String toDate, String imei,JSONObject obj) throws JSONException{
        try {
            
            CallableStatement  stmt = conn.prepareCall("call PROC_USERIPFORZERO(?,?,?)");
            stmt.setString(1, username);
            stmt.setString(2, DateUtil.NToDTFromDate(Long.parseLong(fromDate)));
            stmt.setString(3, DateUtil.NToDTToDate(Long.parseLong(toDate)));
            ResultSet resultSet = stmt.executeQuery();
            
            String zeroIp = "";
            String totSec = "";
            if(resultSet.next()){
                zeroIp = resultSet.getString("ip");
                totSec = resultSet.getString("Total");
            }
            DBPool.log("zeroipUses : " + zeroIp + " sec: " + totSec);
            
            stmt = conn.prepareCall("call PROC_USERIPGET(?,?,?,?)");
            stmt.setString(1, username);
            stmt.setString(2, DateUtil.NToDTFromDate(Long.parseLong(fromDate)));
            stmt.setString(3, DateUtil.NToDTToDate(Long.parseLong(toDate)));
            stmt.setString(4, imei);
            
            resultSet = stmt.executeQuery();
            JSONArray jsonArr = new JSONArray();
            while (resultSet.next()) {
                String slNo = resultSet.getString("ip");
                String fromdate = resultSet.getString("datetime");
                String total = resultSet.getString("Total");
                
                JSONObject row = new JSONObject();
                row.put("ip", slNo);
                if(zeroIp.equalsIgnoreCase(slNo)){
                    long totalsecond = Long.parseLong(total) + (long)(Long.parseLong(totSec)/1000);
                    row.put("total", totalsecond + "");
                }
                else{
                    row.put("total", total);
                }
                row.put("datetime", fromdate);
                DBPool.log("ipUses : " + row.toString());
                jsonArr.put(row);
            }
            obj.put("data", jsonArr);
            obj.put("status", "success");
            conn.close();
            
        } catch (SQLException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        } catch (JSONException ex) {
            DBPool.log(ex);
            obj.put("status", "failure");
        }
    }

    public void MarathonRegister(String fname, String lname, String email, String password,String mobile, JSONObject obj) {
        try {
            PreparedStatement stmnt = conn.prepareStatement("insert into marathonApp(fname,lname,email,password,mobile) values(?,?,?,?,?)");
            stmnt.setString(1, fname);
            stmnt.setString(2, lname);
            stmnt.setString(3, email);
            stmnt.setString(4, password);
            stmnt.setString(5, mobile);

            int noOfRows = stmnt.executeUpdate();
            if (noOfRows > 0) {
                obj.put("updatedCount", noOfRows);
                obj.put("status", "success");
            } else {
                obj.put("updatedCount", noOfRows);
                obj.put("status", "failure");
            }

        } catch (SQLException ex) {
            DBPool.log(ex);
        } catch (JSONException ex) {
            DBPool.log(ex);
        }
    }
    public void MarathonLogin(String email, String password, JSONObject obj) {
        try {
            PreparedStatement stmnt = conn.prepareStatement("select * from marathonApp where email = ? and password = ?");
            stmnt.setString(1, email);
            stmnt.setString(2, password);
            
            ResultSet rs = stmnt.executeQuery();              
            if(rs.next()){
                String dbemail = rs.getString("email");
                obj.put("status", "success");
                obj.put("msg","Login Successful");
            }else{
                obj.put("status", "failure");  
                obj.put("msg","Invalid Login Credential");

            }               
            
        } catch (SQLException ex) {
            DBPool.log(ex);
        }  catch (JSONException ex) {
            DBPool.log(ex);
        }
    }
    public  void RetrievePasswordChange(String email, JSONObject obj){
        
            try {
            PreparedStatement stmnt = conn.prepareStatement("select * from marathonApp where email = ?");
            stmnt.setString(1, email);
            
            
            ResultSet rs = stmnt.executeQuery();              
            if(rs.next()){
                String dbemail = rs.getString("email");
                String dbpass = rs.getString("password");
                obj.put("status", "success");
                obj.put("msg","An email was successfully sent");
            }else{
                obj.put("status", "failure");
                obj.put("msg", "You are not a valid user");
            }               
            
        } catch (SQLException ex) {
            DBPool.log(ex);
        }  catch (JSONException ex) {
            DBPool.log(ex);
        }

    }
    public void PasswordChange(String email,String password,String newPassword, JSONObject obj){
        try {
            PreparedStatement stmnt = conn.prepareStatement("update marathonApp set password = ? where email = ? and password = ?");
            stmnt.setString(1, newPassword);
            stmnt.setString(2, email);
            stmnt.setString(3, password);
            
            int rs = stmnt.executeUpdate();
            if(rs>0){
                obj.put("status", "success");
                obj.put("msg", "password changed successfully");
            }else{
                obj.put("status", "failure");
                obj.put("msg", "Please try again later");
            }               
            
        } catch (SQLException ex) {
            DBPool.log(ex);
        }  catch (JSONException ex) {
            DBPool.log(ex);
        }
    }
    public void leavedetailsondate(String fromdate,JSONObject obj) throws JSONException{
        try {
            String fromdatefrom = DateUtil.NToDTLeaveDate(Long.parseLong(fromdate)) + " 00:00:01";
            String fromdateto = DateUtil.NToDTLeaveDate(Long.parseLong(fromdate)) + " 23:59:59";
            
            DBPool.log("from Date : " + fromdatefrom  + " toDate : " + fromdateto );
            CallableStatement stmnt = conn.prepareCall("call leavedetails(?,?)");
            stmnt.setString(1, fromdatefrom);
            stmnt.setString(2, fromdateto);
            
            ResultSet rs = stmnt.executeQuery();   
          
            JSONArray jsonArr = new JSONArray();
            
            while(rs.next()){
                String users = rs.getString("username");
                JSONObject row = new JSONObject();
                row.put("username", users);
                jsonArr.put(row);
                DBPool.log("leavedetails : " + row.toString());
            }
            obj.put("data", jsonArr);
            obj.put("status", "success");
            conn.close();
            
        } catch (SQLException ex) {
            obj.put("status", "failure");
            DBPool.log(ex);
        }  catch (JSONException ex) {
            obj.put("status", "failure");
            DBPool.log(ex);
        }
   }
    public void leavenetworkreports(String username, long fromDate, long toDate, JSONObject obj) throws JSONException, ParseException {
        try {
            long today = DateUtil.secondsTODate(toDate);
            long fromday = DateUtil.secondsFROMDate(fromDate);
            DBPool.log("" + username + "     " + fromday + "   " + today);
            CallableStatement stmnt = null;
            stmnt = conn.prepareCall("call network_report(?,?,?)");
            stmnt.setString(1, username);
            stmnt.setLong(2, fromday);
            stmnt.setLong(3, today);

            ResultSet rs = stmnt.executeQuery();
            long totalUsesTime = 0;
            long indateTime = 0;

            //FOR MULTIDATE
            String prevDATE = "";
            //String dbDATE = "";
            JSONArray jsonArray = new JSONArray();

            ArrayList<reportRowDetails> allRow = new ArrayList<reportRowDetails>();
            while (rs.next()) {
                long dbDateTime = rs.getLong("datetime");
                short enterExit = rs.getShort("enterexit");
                //FOR MULTIDATE
                reportRowDetails rd = new reportRowDetails(dbDateTime, enterExit);
                DBPool.log(rd.strDate + " | " + dbDateTime + " | " + enterExit);
                allRow.add(rd);
            }
            conn.close();
            
            for(int i=0;i<allRow.size();i++){
                reportRowDetails row = allRow.get(i);
                if(i == 0 && row.enterExit == 2){    
                    String formattedTime = row.strDate + " 00:00:01";
                    SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMMyy HH:mm:ss");
                    sdf1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                    Date sourceDate = sdf1.parse(formattedTime);

                    totalUsesTime = totalUsesTime + (row.dbDateTime - sourceDate.getTime());
                    indateTime = 0;
                }
                else{
                    if(row.enterExit == 2 && indateTime > 0){
                        if (prevDATE.equalsIgnoreCase(row.strDate)) {
                            totalUsesTime = totalUsesTime + (row.dbDateTime - indateTime);
                        } else {
                            if(i<(allRow.size()-1)){
                                String formattedTime = prevDATE + " 23:59:59";
                                SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMMyy HH:mm:ss");
                                sdf1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                                Date sourceDate = sdf1.parse(formattedTime);

                                totalUsesTime = totalUsesTime + (sourceDate.getTime() - indateTime);
                                
                                indateTime = sourceDate.getTime();
                            }
                            JSONObject jObj = new JSONObject();
                            jObj.put("date", prevDATE);
                            jObj.put("totaluses", DateUtil.LtoGetHours(totalUsesTime));
                            jsonArray.put(jObj);
                            totalUsesTime = 0;
                            totalUsesTime = totalUsesTime + (row.dbDateTime - indateTime);
                        }
                    }
                    else{
                        indateTime = row.dbDateTime;
                    }
                    prevDATE = row.strDate;
                }
            }
            
            reportRowDetails rowD = allRow.get(allRow.size()-1);
            if (rowD.enterExit == 1) {
                totalUsesTime = totalUsesTime + (System.currentTimeMillis() - rowD.dbDateTime);
            }
            if (prevDATE.equalsIgnoreCase("") || prevDATE.equalsIgnoreCase(rowD.strDate)) {
                JSONObject jObj = new JSONObject();
                jObj.put("date", rowD.strDate);
                jObj.put("totaluses", DateUtil.LtoGetHours(totalUsesTime));
                jsonArray.put(jObj);
            }
            
            /*
            while (rs.next()) {
                long dbDateTime = rs.getLong("datetime");
                short enterExit = rs.getShort("enterexit");
                //FOR MULTIDATE
                dbDATE = DateUtil.LtoddMMMyy(dbDateTime);
                DBPool.log(dbDATE + " | " + dbDateTime + " | " + enterExit);
                
                if (enterExit == 2 && indateTime > 0) {
                    if (dbDateTime > indateTime) {
                        if (prevDATE.equalsIgnoreCase(dbDATE)) {
                            totalUsesTime = totalUsesTime + (dbDateTime - indateTime);
                        } else {
                            if(!prevDATE.equalsIgnoreCase("")){
                                JSONObject jObj = new JSONObject();
                                jObj.put("date", prevDATE);
                                jObj.put("totaluses", DateUtil.LtoGetHours(totalUsesTime));
                                jsonArray.put(jObj);
                            }
                            prevDATE = dbDATE;
                            totalUsesTime = 0;
                            totalUsesTime = totalUsesTime + (dbDateTime - indateTime);
                        }
                    }
                    indateTime = 0;
                } else {
                    indateTime = dbDateTime;
                }
            }
            if (indateTime > 0) {
                totalUsesTime = totalUsesTime + (System.currentTimeMillis() - indateTime);
            }
            if (prevDATE.equalsIgnoreCase("") || prevDATE.equalsIgnoreCase(dbDATE)) {
                JSONObject jObj = new JSONObject();
                jObj.put("date", dbDATE);
                jObj.put("totaluses", DateUtil.LtoGetHours(totalUsesTime));
                jsonArray.put(jObj);
            }*/
            DBPool.log("THREE: " + jsonArray);
            obj.put("status", "success");
            obj.put("data", jsonArray);
            
        } catch (SQLException ex) {
            obj.put("status", "failure");
            DBPool.log(ex);
        } catch (JSONException ex) {
            obj.put("status", "failure");
            DBPool.log(ex);
        }
    }
    
    
     public void leavenetworkreportslocal(String username, long fromDate, long toDate, JSONObject obj) throws JSONException, ParseException {
        try {
            long today = DateUtil.secondsTODate(toDate);
            long fromday = DateUtil.secondsFROMDate(fromDate);
            
            DBPool.log("" + username + "   " + fromday + "   " + today);
            CallableStatement stmnt = null;
            stmnt = conn.prepareCall("call network_report_localip(?,?,?)");
            stmnt.setString(1, username);
            stmnt.setLong(2, fromday);
            stmnt.setLong(3, today);

            ResultSet rs = stmnt.executeQuery();
            long totalUsesTime = 0;
            long indateTime = 0;
          
            String prevDATE = "";
            JSONArray jsonArray = new JSONArray();

            ArrayList<reportRowDetails> allRow = new ArrayList<reportRowDetails>();
            while (rs.next()) {
                long dbDateTime = rs.getLong("datetime");
                short enterExit = rs.getShort("enterexit");
                
                reportRowDetails rd = new reportRowDetails(dbDateTime, enterExit);
                DBPool.log(rd.strDate + " | " + dbDateTime + " | " + enterExit);
                allRow.add(rd);
            }
            conn.close();
            
            for(int i=0;i<allRow.size();i++){
                reportRowDetails row = allRow.get(i);
                if(i == 0 && row.enterExit == 2){    
                    String formattedTime = row.strDate + " 00:00:01";
                    SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMMyy HH:mm:ss");
                    sdf1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                    Date sourceDate = sdf1.parse(formattedTime);

                    totalUsesTime = totalUsesTime + (row.dbDateTime - sourceDate.getTime());
                    indateTime = 0;
                }
                else{
                    if(row.enterExit == 2 && indateTime > 0){
                        if (prevDATE.equalsIgnoreCase(row.strDate)) {
                            totalUsesTime = totalUsesTime + (row.dbDateTime - indateTime);
                        } else {
                            if(i<(allRow.size()-1)){
                                String formattedTime = prevDATE + " 23:59:59";
                                SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMMyy HH:mm:ss");
                                sdf1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                                Date sourceDate = sdf1.parse(formattedTime);

                                totalUsesTime = totalUsesTime + (sourceDate.getTime() - indateTime);
                                
                                indateTime = sourceDate.getTime();
                            }
                            JSONObject jObj = new JSONObject();
                            jObj.put("date", prevDATE);
                            jObj.put("totaluses", DateUtil.LtoGetHours(totalUsesTime));
                            jsonArray.put(jObj);
                            totalUsesTime = 0;
                            totalUsesTime = totalUsesTime + (row.dbDateTime - indateTime);
                        }
                    }
                    else{
                        indateTime = row.dbDateTime;
                    }
                    prevDATE = row.strDate;
                }
            }
            
            reportRowDetails rowD = allRow.get(allRow.size()-1);
            if (rowD.enterExit == 1) {
                totalUsesTime = totalUsesTime + (System.currentTimeMillis() - rowD.dbDateTime);
                
            }
            if (prevDATE.equalsIgnoreCase("") || prevDATE.equalsIgnoreCase(rowD.strDate)) {
                JSONObject jObj = new JSONObject();
                jObj.put("date", rowD.strDate);
                jObj.put("totaluses", DateUtil.LtoGetHours(totalUsesTime));
                jsonArray.put(jObj);
            }
            
            DBPool.log("THREE: " + jsonArray);
            obj.put("status", "success");
            obj.put("data", jsonArray);
            
        } catch (SQLException ex) {
            obj.put("status", "failure");
            DBPool.log(ex);
        } catch (JSONException ex) {
            obj.put("status", "failure");
            DBPool.log(ex);
        }
    }

    public void getIpDetails(String username,long date,JSONObject obj) throws JSONException{
        try {
            long today = DateUtil.secondsTODate(date);
            long fromday = DateUtil.secondsFROMDate(date);
            CallableStatement stmnt = null;
            stmnt = conn.prepareCall("call proc_details(?,?,?)");
            stmnt.setString(1, username);
            stmnt.setLong(2, fromday);
            stmnt.setLong(3, today);
            JSONArray jsonArray = new JSONArray();
            ResultSet rs = stmnt.executeQuery();
            long entryTime = 0;
            ArrayList<reportRowDetails> allRow = new ArrayList<reportRowDetails>();
            
            
            while (rs.next()) {
                long dbDateTime = rs.getLong("datetime");
                short enterExit = rs.getShort("enterexit");
                String ip = rs.getString("ip");
                
                reportRowDetails row = new reportRowDetails(dbDateTime, enterExit);
                row.ip = ip;
                allRow.add(row);
                String dbDATE = DateUtil.LtoddMMMyy(dbDateTime);
                DBPool.log(dbDATE + " | " + dbDateTime + " | " + enterExit + " | "+ip);
            }
            for(int i=0;i<allRow.size();i++){
                reportRowDetails row = allRow.get(i);
                if(i==0 && row.enterExit == 2){
                    
                    entryTime = 0;
                    String entryTimeDate = DateUtil.LtoddMMMyy(row.dbDateTime);
                    String formattedTime = entryTimeDate + " 00:00:01";
                    SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMMyy HH:mm:ss");
                    sdf1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                    Date sourceDate = sdf1.parse(formattedTime);
                    entryTime = sourceDate.getTime();
                    
                    JSONObject jObj = new JSONObject();
                    jObj.put("intime", DateUtil.LtoHHMMSS(entryTime));
                    jObj.put("outtime", DateUtil.LtoHHMMSS(row.dbDateTime));
                    jObj.put("total", DateUtil.LtoGetHours(row.dbDateTime - entryTime));
                    jObj.put("ip", row.ip);
                    jObj.put("order", row.enterExit);
                    jsonArray.put(jObj);
                }
                else{
                    if (row.enterExit == 2 && entryTime > 0) {
                        JSONObject jObj = new JSONObject();
                        jObj.put("intime", DateUtil.LtoHHMMSS(entryTime));
                        jObj.put("outtime", DateUtil.LtoHHMMSS(row.dbDateTime));
                        jObj.put("total", DateUtil.LtoGetHours(row.dbDateTime - entryTime));
                        jObj.put("ip", row.ip);
                        jObj.put("order", row.enterExit);
                        jsonArray.put(jObj);
                        entryTime = 0;
                    } else {
                        entryTime = row.dbDateTime;
                    }
                }
                
            }
            reportRowDetails rowD = allRow.get(allRow.size()-1);
            if (rowD.enterExit == 1) {
             
                String strCurrentDate = DateUtil.LtoddMMMyy(System.currentTimeMillis());
                String entryTimeDate = DateUtil.LtoddMMMyy(rowD.dbDateTime);
                String outTime = "";
                long totalUses = 0;
                if(strCurrentDate.equalsIgnoreCase(entryTimeDate)){
                    totalUses = System.currentTimeMillis() - rowD.dbDateTime;
                }
                else{
                    String formattedTime = entryTimeDate + " 23:59:59";
                    SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMMyy HH:mm:ss");
                    sdf1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                    Date sourceDate = sdf1.parse(formattedTime);
                    
                    totalUses = sourceDate.getTime() - rowD.dbDateTime;
                    outTime = DateUtil.LtoHHMMSS(sourceDate.getTime());
                }
                
                
                JSONObject jObj = new JSONObject();
                jObj.put("intime", DateUtil.LtoHHMMSS(rowD.dbDateTime));
                jObj.put("outtime", outTime);
                jObj.put("total", DateUtil.LtoGetHours(totalUses));
                jObj.put("ip", rowD.ip);
                jObj.put("order", rowD.enterExit);
                jsonArray.put(jObj);
            }
            obj.put("status", "success");
            obj.put("data", jsonArray);
            conn.close();            
        } catch (SQLException e) {
            DBRelated.DBPool.log("SQLException: "+e);
            obj.put("status", "fail");
        }catch(Exception e){
            DBRelated.DBPool.log("Exception: "+e);  
            obj.put("status", "fail");
        }        
    }
        
        
    public void getAllStaticIP(JSONObject result) throws JSONException {
        try {
            CallableStatement stmnt = null;
            stmnt = conn.prepareCall("call PROC_GETALLSTATICIP()");//this sp should be changed
            ResultSet rs = stmnt.executeQuery();   
          
            JSONArray jsonArr = new JSONArray();
            while(rs.next()){
                String staticIP = rs.getString("ip");
                JSONObject row = new JSONObject();
                row.put("ip", staticIP);
                jsonArr.put(row);
            }
            DBPool.log("ALLStaticIP : " + jsonArr.toString());

            result.put("data", jsonArr);
            result.put("status", "success");
            conn.close();
            
        } catch (SQLException ex) {
            try {
                result.put("status", "failure");
            } catch (JSONException ex1) {
                Logger.getLogger(DBOperation.class.getName()).log(Level.SEVERE, null, ex1);
            }
            DBPool.log(ex);
        }  catch (JSONException ex) {
            result.put("status", "failure");
            DBPool.log(ex);
        }
    }

    public void inserStaticIP(JSONObject result,String inserIp) throws JSONException {
        try {
            CallableStatement stmnt = null;
            stmnt = conn.prepareCall("call PROC_INSERTSTATICIP(?)");//this sp should be changed
            stmnt.setString(1, inserIp);
            ResultSet rs = stmnt.executeQuery();   
            result.put("status", "success");
            result.put("data", "Successfully added");
            conn.close();
            
        } catch (SQLException ex) {
            try {
                result.put("status", "failure");
            } catch (JSONException ex1) {
                Logger.getLogger(DBOperation.class.getName()).log(Level.SEVERE, null, ex1);
            }
            DBPool.log(ex);
        }  catch (JSONException ex) {
            result.put("status", "failure");
            DBPool.log(ex);
        }
    }

    public void deleteStaticIP(JSONObject result, String string) throws JSONException {
        try {
            CallableStatement stmnt = null;
            stmnt = conn.prepareCall("call PROC_DELETESTATICIP(?)");//this sp should be changed
            stmnt.setString(1, string);
            ResultSet rs = stmnt.executeQuery();   
            result.put("status", "success");
            result.put("data", "Successfully removed");
            conn.close();
            
        } catch (SQLException ex) {
            try {
                result.put("status", "failure");
            } catch (JSONException ex1) {
                Logger.getLogger(DBOperation.class.getName()).log(Level.SEVERE, null, ex1);
            }
            DBPool.log(ex);
        }  catch (JSONException ex) {
            result.put("status", "failure");
            DBPool.log(ex);
        }
    }

    public void insertQuaryclientip(JSONObject jsonReq, JSONObject result) {
        try {
            String userName = jsonReq.getString("username");
            String imei = jsonReq.getString("imei");
            String mobile = jsonReq.getString("mobile");
            String inIP = jsonReq.getString("inip");
            String inTime = jsonReq.getString("intime");
            String outIP = jsonReq.getString("outip");
            String outTime = jsonReq.getString("outtime");

            long longINTime = 0;
            long longOUTTime = 0;

            if (!inTime.equalsIgnoreCase("")) {
                longINTime = Long.parseLong(inTime);
            }
            if (!outTime.equalsIgnoreCase("")) {
                longOUTTime = Long.parseLong(outTime);
            }
            CallableStatement stmt = conn.prepareCall("call PROC_IPINSERTNEW(?,?,?,?,?,?,?)");
            stmt.setString(1, userName);
            stmt.setString(2, mobile);
            stmt.setString(3, imei);
            stmt.setString(4, inIP);
            stmt.setLong(5, longINTime);
            stmt.setString(6, outIP);
            stmt.setLong(7, longOUTTime);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                result.put("sl", resultSet.getInt("sl") + "");
                result.put("status", "success");
            } else {
                result.put("sl", 0 + "");
                result.put("status", "failure");
            }
            conn.close();

        } catch (SQLException ex) {
            try {
                DBPool.log(ex);
                result.put("sl", 0 + "");
                result.put("status", "failure");
            } catch (JSONException ex1) {
                Logger.getLogger(DBOperation.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (JSONException ex) {
            try {
                DBPool.log(ex);
                result.put("sl", 0 + "");
                result.put("status", "failure");
            } catch (JSONException ex1) {
                Logger.getLogger(DBOperation.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    private void senNotificationToAdmin(String regId,String username, String leavetype, String reason, String fromdate, String todate){  
        try {
         String authKey = AUTH_KEY_FCM;   // You FCM AUTH key
        String FMCurl = API_URL_FCM;

        URL url = new URL(FMCurl);
        HttpURLConnection HttpCon = (HttpURLConnection) url.openConnection();

        HttpCon.setUseCaches(false);
        HttpCon.setDoInput(true);
        HttpCon.setDoOutput(true);

        HttpCon.setRequestMethod("POST");
        HttpCon.setRequestProperty("Authorization", "key=" + authKey);
        HttpCon.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject();
        
        json.put("to", regId);
        JSONObject info = new JSONObject();
        info.put("title", username);   // Notification title
        info.put("body",reason); // Notification body
        info.put("fromdate",DateUtil.LtoddMMMyy(Long.parseLong(fromdate)) );// Notification fromdate
        info.put("todate", DateUtil.LtoddMMMyy(Long.parseLong(todate)));// Notification todate
        json.put("notification", info);
        json.put("data", info);
        
        OutputStreamWriter writer = new OutputStreamWriter(HttpCon.getOutputStream());
          int status = 0;
          String errMsg = "";
        try  {
            writer.write(json.toString());
            writer.flush();
            int responseCode = HttpCon.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = HttpCon.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                JSONParser perser = new JSONParser();
                org.json.simple.JSONObject responseData = (org.json.simple.JSONObject)perser.parse(in);
                in.close();
                inputStream.close();
                
                DBPool.log("Notification Res : " + responseData.toString());
                
                int failureC = Integer.parseInt(responseData.get("failure").toString());
                if(failureC == 0){
                    status = 2;
                    errMsg = "msg send";
                }
                else{
                    status = 3;
                    //JSONArray array = (JSONArray)responseData.get("results");
                    //JSONObject singleObj = (JSONObject)array.get(0);
                    errMsg = "Error";
                }
                
            } else {
                StringBuilder errorresponse = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(HttpCon.getErrorStream()));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    errorresponse.append(inputLine);
                }
                reader.close();
                status = 1;
                errMsg = "Error code : " + responseCode;
            }
        }catch(Exception ex){
            status = 1;
            errMsg = ex.getMessage();
            
        }
        HttpCon.disconnect();   
        } catch (Exception e) {
           DBPool.log("E:::: "+e);
        }
    }

    public String getCEODBIPPORT(){
        try {
            CallableStatement stmnt = null;
            stmnt = conn.prepareCall("call PROC_GETCEODBIPPORT()");//this sp should be changed
            ResultSet rs = stmnt.executeQuery();   
            String staticIPs = "";
            if(rs.next()){
                staticIPs = rs.getString("IPPORT");
                
            }
            DBPool.log("ALLStaticIPs : " + staticIPs);
            conn.close();
            return staticIPs;
        } catch (SQLException ex) {
            DBPool.log(ex);
        } 
        return "";
    }
  
    public void travelprofile(String fname, String lname, String mobile, String email,String gender, JSONObject obj) {
        try {
            CallableStatement stmnt = conn.prepareCall("call proc_travelprofile(?,?,?,?,?)");
            stmnt.setString(1, fname);
            stmnt.setString(2, lname);
            stmnt.setString(3, mobile);
            stmnt.setString(4, email);
            stmnt.setString(5, gender);
            int resultSet = stmnt.executeUpdate();
            if (resultSet>0) {
                obj.put("status", "success");
            } else {
                obj.put("status", "failure");
            }
        } catch (SQLException ex) {
            DBPool.log(ex);
        } catch (JSONException ex) {
            DBPool.log(ex);
        }
    }
     public void travelconfirmbooking(String mobile, String mdate, String mtime, String location,
             String sericetype, String cartype,String duration,String fare, JSONObject obj) {
        try {
            CallableStatement stmnt = conn.prepareCall("call proc_travelconfirmbooking(?,?,?,?,?,?,?,?)");
            stmnt.setString(1, mobile);
            stmnt.setString(2, mdate);
            stmnt.setString(3, mtime);
            stmnt.setString(4, location);
            stmnt.setString(5, sericetype);
            stmnt.setString(6, cartype);
            stmnt.setString(7, duration);
            stmnt.setString(8, fare);
            int resultSet = stmnt.executeUpdate();
            if (resultSet>0) {
                obj.put("status", "success");
            } else {
                obj.put("status", "failure");
            }
        } catch (SQLException ex) {
            DBPool.log(ex);
        } catch (JSONException ex) {
            DBPool.log(ex);
        }
    }
     public void travelallrides(String mobile,JSONObject obj) throws JSONException{
         try {
            CallableStatement stmnt = conn.prepareCall("call proc_travelallrides(?)");
            stmnt.setString(1, mobile);
            JSONArray jsonArr = new JSONArray();
            ResultSet resultSet = stmnt.executeQuery();
            
             while(resultSet.next()){
                String mdate = resultSet.getString("date");
                String mtime = resultSet.getString("time");
                String pickuplocation = resultSet.getString("pickuplocation");
                String servicetype = resultSet.getString("servicetype");
                String fare = resultSet.getString("fare");
                String duration = resultSet.getString("duration");
                String cartype = resultSet.getString("cartype");
                
                JSONObject row = new JSONObject();
                row.put("mdate", mdate);
                row.put("mtime", mtime);
                row.put("pickuplocation", pickuplocation);
                row.put("servicetype", servicetype);
                row.put("fare", fare);
                row.put("duration", duration);
                row.put("cartype", cartype);
                jsonArr.put(row);
            }
            DBPool.log("travelconfirmbooking : " + jsonArr.toString());
            obj.put("data", jsonArr);
            obj.put("status", "success");
            conn.close();
            
        } catch (SQLException ex) {
            try {
                obj.put("status", "failure");
            } catch (JSONException ex1) {
                Logger.getLogger(DBOperation.class.getName()).log(Level.SEVERE, null, ex1);
            }
            DBPool.log(ex);
        }  catch (JSONException ex) {
            obj.put("status", "failure");
            DBPool.log(ex);
        }
     }
     public void travelupcomingrides(String mobile,JSONObject obj) throws JSONException, ParseException{
         try {
             DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
             Calendar today = Calendar.getInstance();
             Date todayDate = today.getTime();
             
             CallableStatement stmnt = conn.prepareCall("call proc_travelallrides(?)");
             stmnt.setString(1, mobile);
             JSONArray jsonArr = new JSONArray();
             ResultSet resultSet = stmnt.executeQuery();
            
             while(resultSet.next()){
                String mdate = resultSet.getString("date");
                String mtime = resultSet.getString("time");
                String pickuplocation = resultSet.getString("pickuplocation");
                String servicetype = resultSet.getString("servicetype");
                String fare = resultSet.getString("fare");
                String duration = resultSet.getString("duration");
                String cartype = resultSet.getString("cartype");
                
                Date getdate = formatter.parse(mdate);
                 JSONObject row = new JSONObject();
                     row.put("mdate", mdate);
                     row.put("mtime", mtime);
                     row.put("pickuplocation", pickuplocation);
                     row.put("servicetype", servicetype);
                     row.put("fare", fare);
                     row.put("duration", duration);
                     row.put("cartype", cartype);
                     jsonArr.put(row);
//                 if (todayDate.compareTo(getdate) >= 0) {
//                     JSONObject row = new JSONObject();
//                     row.put("mdate", mdate);
//                     row.put("mtime", mtime);
//                     row.put("pickuplocation", pickuplocation);
//                     row.put("servicetype", servicetype);
//                     row.put("fare", fare);
//                     row.put("duration", duration);
//                     row.put("cartype", cartype);
//                     jsonArr.put(row);
//                 }
            }
            DBPool.log("travelconfirmbooking : " + jsonArr.toString());
            obj.put("data", jsonArr);
            obj.put("status", "success");
            conn.close();
            
        } catch (SQLException ex) {
            try {
                obj.put("status", "failure");
            } catch (JSONException ex1) {
                Logger.getLogger(DBOperation.class.getName()).log(Level.SEVERE, null, ex1);
            }
            DBPool.log(ex);
        }  catch (JSONException ex) {
            obj.put("status", "failure");
            DBPool.log(ex);
        }
     }
     public void travelmailsend(String name, String email, String mobile, String currentDate, String Message, JSONObject obj) {
        try {
            CallableStatement stmnt = conn.prepareCall("call proc_travelmail(?,?,?,?,?)");
            stmnt.setString(1, name);
            stmnt.setString(2, email);
            stmnt.setString(3, mobile);
            stmnt.setString(4, currentDate);
            stmnt.setString(5, Message);
           
            int resultSet = stmnt.executeUpdate();
            if (resultSet>0) {
                obj.put("status", "success");
            } else {
                obj.put("status", "failure");
            }
        } catch (SQLException ex) {
            DBPool.log(ex);
        } catch (JSONException ex) {
            DBPool.log(ex);
        }
    }
}
