

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
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
public class DBTijdSchema{
  
    public static TijdSchema getTijdSchema(Year jaar) throws DBException {
      Connection con = null;
        try{
            con = DBConnect.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tijdschema WHERE jaar = " + jaar);
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter dfy = DateTimeFormatter.ofPattern("yyyy");
            LocalDateTime startDatum = null, inschrDL = null, capDL = null, huidigDL = null, eindDatum = null;
            TijdSchema ts = null;
            if(rs.next()) {
              Timestamp start = rs.getTimestamp("startdatum");
              Timestamp inschr = rs.getTimestamp("inschrijvingen_deadline");
              Timestamp cap = rs.getTimestamp("capaciteit_deadline");
              Timestamp huid = rs.getTimestamp("huidig_deadline");
              Timestamp eind = rs.getTimestamp("einddatum");
              startDatum = start.toLocalDateTime();
              inschrDL = inschr.toLocalDateTime();
              capDL = cap.toLocalDateTime();
              huidigDL = huid.toLocalDateTime();
              eindDatum = eind.toLocalDateTime();
              ts = new TijdSchema(jaar, startDatum, inschrDL, capDL, huidigDL, eindDatum);
            }
            DBConnect.closeConnection(con);
            return ts;
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
    
    public static void setTijdSchema(TijdSchema ts) throws DBException{
        Connection con = null;
        try{
            con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO tijdschema ("
          + "jaar, startdatum, inschrijvingen_deadline, "
          + "capaciteit_deadline, huidig_deadline, einddatum) "
          + "VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " 
          + "startdatum = VALUES(startdatum), inschrijvingen_deadline = VALUES(inschrijvingen_deadline), "
          + "capaciteit_deadline = VALUES(capaciteit_deadline), huidig_deadline = VALUES(huidig_deadline), "
          + "einddatum = VALUES(einddatum)");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter dfy = DateTimeFormatter.ofPattern("yyyy");
            ps.setString(1,dfy.format(ts.getJaar()));
            ps.setString(2, df.format(ts.getStartDatum()));
            ps.setString(3,df.format (ts.getInschrijvingenDeadline()));
            ps.setString(4, df.format(ts.getCapaciteitDeadline()));
            ps.setString(5, df.format(ts.getHuidigDeadline()));
            ps.setString(6, df.format(ts.getEindDatum()));
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
