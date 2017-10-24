/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelStructure;

import Support.DateUtil;

/**
 *
 * @author DELL
 */
public class reportRowDetails {
    public long dbDateTime;
    public short enterExit;
    public  String ip;
    public String strDate;
    
    public reportRowDetails(long dbDateTime, short enterExit) {
        this.dbDateTime = dbDateTime;
        this.enterExit = enterExit;
        
        strDate = DateUtil.LtoddMMMyy(dbDateTime);
    }
}
