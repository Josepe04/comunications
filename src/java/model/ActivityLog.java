/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import Controllers.Homepage;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chema
 */
public class ActivityLog {
    public static void nuevaEntrada(String user,String username,String personinfo,
            String type,String note){
        String consulta = "insert into activitylog(username,personinfo," +
            "type,note,date,userid) values('"+username+"','"+personinfo+"','"+type+"','"+note+"',now(),"+user+")";
        try {
            Homepage.st.execute(consulta);
        } catch (SQLException ex) {
            Logger.getLogger(ActivityLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
