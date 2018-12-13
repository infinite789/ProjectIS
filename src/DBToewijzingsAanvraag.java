
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bddra
 */
public class DBToewijzingsAanvraag {
  
  /*
   * Methode voor het ophalen van de tabel 'toewijzingsaanvragen' uit de 
   * databank 
   */
  public static HashMap<Integer, ToewijzingsAanvraag> getToewijzingsAanvragen() throws DBException {
    HashMap<Integer, ToewijzingsAanvraag> aanvragenHashMap = new HashMap<>();
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen");
      while (rs.next()) {
        int aanvraagnummer = rs.getInt("toewijzingsaanvraagnummer");
        Status status = Status.valueOf(rs.getString("status"));
        String rijksregisterNummerStudent
                = rs.getString("student_rijksregisternummer");
        Timestamp ts = rs.getTimestamp("aanmeldingstijdstip");
        LocalDateTime aanmeldingstijdstip = ts.toLocalDateTime();
        int broersOfZussen = rs.getInt("broer_zus");
        Integer voorkeur = rs.getInt("voorkeurschool");
        String afgewezenScholenCsv = rs.getString("afgewezen_scholen");
        String[] afgSchArray = afgewezenScholenCsv.split(";");
        ArrayList<String> als = new ArrayList<>();
        for(String str : afgSchArray)
            if(!str.equals(""))
                als.add(str);
        aanvragenHashMap.put(aanvraagnummer,
            new ToewijzingsAanvraag(aanvraagnummer,
            rijksregisterNummerStudent,aanmeldingstijdstip, 
            broersOfZussen, status, voorkeur, als)
        );
      }
      DBConnect.closeConnection(con);
      return aanvragenHashMap;
    } catch (DBException dbe) {
      dbe.printStackTrace();
      DBConnect.closeConnection(con);
      throw dbe;
    } catch (Exception e) {
      e.printStackTrace();
      DBConnect.closeConnection(con);
      throw new DBException(e);
    }
  }
  
  
  /*
   * Methode voor het opslaan van de towijzingsaanvragen 
   * De methode verwerkt de aangepaste gegevens in de databank
   * Eerst connectie openen voor het gebruiken van de methoden 
   * door getConnection() op te roepen en afsluiten met 
   * closeConnection()
   */
  public static void bewaarToewijzingsAanvragen(
                     HashMap<Integer, ToewijzingsAanvraag> toewijzingsaanvragen,
                     Ouder ouderVanStudent, Ouder ingelogdeOuder, boolean ingelogdAlsAdmin,
                     ArrayList<Integer> verwijderdeKeys) throws DBException {
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      for (ToewijzingsAanvraag ta : toewijzingsaanvragen.values()) {
        if (ouderVanStudent.equals(ingelogdeOuder) || ingelogdAlsAdmin == true) {
          PreparedStatement ps
          = con.prepareStatement("INSERT INTO toewijzingsaanvragen ("
          + "toewijzingsaanvraagnummer, status, "
          + "student_rijksregisternummer, aanmeldingstijdstip, "
          + "broer_zus, voorkeurschool, afgewezen_scholen) "
          + "VALUES(?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " 
          + "status = VALUES(status), broer_zus = VALUES(broer_zus), "
          + "voorkeurschool = VALUES(voorkeurschool), "
          + "afgewezen_scholen = VALUES(afgewezen_scholen)");
          ps.setInt(1, ta.getToewijzingsAanvraagNummer());
          ps.setString(2, ta.getStatus().toString());
          ps.setString(3, ta.getRijksregisterNummerStudent());
          DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
          ps.setString(4, df.format(ta.getAanmeldingsTijdstip()));
          ps.setInt(5, ta.getBroersOfZussen());
	  if(ta.getVoorkeur() == 0)
	    ps.setNull(6, java.sql.Types.INTEGER);
	  else
	    ps.setInt(6, ta.getVoorkeur());
          ps.setString(7, ta.csvFormatLijst());
          ps.execute();
        }
      }
      for(int i = 0; i < verwijderdeKeys.size(); i++) {
        PreparedStatement ps 
        = con.prepareStatement("DELETE FROM toewijzingsaanvragen "
                             + "WHERE toewijzingsaanvraagnummer = ?");
        ps.setInt(1, verwijderdeKeys.get(i));
        ps.executeUpdate();
      }
      DBConnect.closeConnection(con);
    } catch (DBException dbe) {
      dbe.printStackTrace();
      DBConnect.closeConnection(con);
      throw dbe;
    } catch (Exception e) {
      e.printStackTrace();
      DBConnect.closeConnection(con);
      throw new DBException(e);
    }
  }
  
  public static void verwijder(int aanvraagnummer) throws DBException {
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      PreparedStatement ps 
      = con.prepareStatement("DELETE FROM toewijzingsaanvragen "
			   + "WHERE toewijzingsaanvraagnummer = " + aanvraagnummer);
      ps.executeUpdate();
      DBConnect.closeConnection(con);
    } catch (DBException dbe) {
      dbe.printStackTrace();
      DBConnect.closeConnection(con);
      throw dbe;
    } catch (Exception e) {
      e.printStackTrace();
      DBConnect.closeConnection(con);
      throw new DBException(e);
    }
  }
  
  public static void setAantalBroersOfZussen(int aanvraagnummer, int aantal) throws DBException {
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      PreparedStatement ps 
      = con.prepareStatement("UPDATE toewijzingsaanvragen SET broer_zus = " + aantal
			   + "WHERE toewijzingsaanvraagnummer = " + aanvraagnummer);
      ps.executeUpdate();
      DBConnect.closeConnection(con);
    } catch (DBException dbe) {
      dbe.printStackTrace();
      DBConnect.closeConnection(con);
      throw dbe;
    } catch (Exception e) {
      e.printStackTrace();
      DBConnect.closeConnection(con);
      throw new DBException(e);
    }
  }
}
