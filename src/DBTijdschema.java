

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;




/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jeanjanssens
 */
public class DBTijdschema{
    public static void setDatum(Year jaar, LocalDateTime stdtm, LocalDateTime inschrDL, 
            LocalDateTime capDL, LocalDateTime vkDL, LocalDateTime EDL) throws DBException{
        Connection con = null;
        try{
            con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE tijdschema SET jaar = ?, "+ 
                    "start_datum = ?, inschrijvingen_deadline = ?, capaciteit_deadline = ?, " +
                    "voorkeur_deadline = ?, eind_datum = ?");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter dfy = DateTimeFormatter.ofPattern("yyyy");
            ps.setString(1,dfy.format(jaar));
            ps.setString(2, df.format(stdtm));
            ps.setString(3,df.format (inschrDL));
            ps.setString(4, df.format(capDL));
            ps.setString(5, df.format(vkDL));
            ps.setString(6, df.format(EDL));
            ps.execute();
            DBConnect.closeConnection(con);
        }
       catch (DBException dbe) {
        dbe.printStackTrace();
        DBConnect.closeConnection(con);
        throw dbe;
        }
      catch (Exception e) {
        e.printStackTrace();
        DBConnect.closeConnection(con);
        throw new DBException(e);
        }  
    }
}
