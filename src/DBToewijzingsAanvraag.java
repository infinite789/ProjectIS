
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
      ResultSet rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen ORDER BY preferentie DESC");
      while (rs.next()) {
        int aanvraagnummer = rs.getInt("toewijzingsaanvraagnummer");
        Status status = Status.valueOf(rs.getString("status"));
        String rijksregisterNummerStudent = rs.getString("student_rijksregisternummer");
        String rijksregisterNummerOuder = rs.getString("ouder_rijksregisternummer");
        Timestamp ts = rs.getTimestamp("aanmeldingstijdstip");
        LocalDateTime aanmeldingstijdstip = ts.toLocalDateTime();
        int broersOfZussen = rs.getInt("broer_zus");
        int voorkeur = rs.getInt("voorkeurschool");
        long preferentie = rs.getLong("preferentie");
        String csvAfgScholen = rs.getString("afgewezen_scholen");
        if(csvAfgScholen == null)
          csvAfgScholen = "";
        aanvragenHashMap.put(aanvraagnummer,
            new ToewijzingsAanvraag(aanvraagnummer, rijksregisterNummerStudent, 
                                    rijksregisterNummerOuder, aanmeldingstijdstip, 
                                    broersOfZussen, status, voorkeur, preferentie, csvAfgScholen)
        );
      }
      for(ToewijzingsAanvraag ta : aanvragenHashMap.values()) {
      int broersOfZussen = 0;
      st = con.createStatement();
      rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen "
              + "WHERE ouder_rijksregisternummer = '" + ta.getRijksregisterNummerOuder() + "' "
              + "AND student_rijksregisternummer <> '" + ta.getRijksregisterNummerStudent() + "' "
              + "AND voorkeurschool = " + ta.getVoorkeur() + ";");
      while(rs.next()) {
        broersOfZussen++;
      }
      st = con.createStatement();
      rs = st.executeQuery("SELECT * FROM studenten "
              + "WHERE (ouder_rijksregisternummer = '" + ta.getRijksregisterNummerOuder() + "' "
              + "AND student_rijksregisternummer <> '" + ta.getRijksregisterNummerStudent() + "' "
              + "AND huidige_school = " + ta.getVoorkeur() + ");");
      while(rs.next()) {
        broersOfZussen++;
      }
      aanvragenHashMap.get(ta.getToewijzingsAanvraagNummer()).setBroersOfZussen(broersOfZussen);
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
  
  public static HashMap<Integer, ToewijzingsAanvraag> getToewijzingsAanvragen(Status s) throws DBException {
    HashMap<Integer, ToewijzingsAanvraag> aanvragenHashMap = new HashMap<>();
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen "
                                   + "WHERE status = '" + s + "';");
      while (rs.next()) {
        int aanvraagnummer = rs.getInt("toewijzingsaanvraagnummer");
        Status status = Status.valueOf(rs.getString("status"));
        String rijksregisterNummerStudent = rs.getString("student_rijksregisternummer");
        String rijksregisterNummerOuder = rs.getString("ouder_rijksregisternummer");
        Timestamp ts = rs.getTimestamp("aanmeldingstijdstip");
        LocalDateTime aanmeldingstijdstip = ts.toLocalDateTime();
        int broersOfZussen = rs.getInt("broer_zus");
        int voorkeur = rs.getInt("voorkeurschool");
        long preferentie = rs.getLong("preferentie");
        String csvAfgScholen = rs.getString("afgewezen_scholen");
        if(csvAfgScholen == null)
          csvAfgScholen = "";
        aanvragenHashMap.put(aanvraagnummer,
            new ToewijzingsAanvraag(aanvraagnummer, rijksregisterNummerStudent, 
                                    rijksregisterNummerOuder, aanmeldingstijdstip, 
                                    broersOfZussen, status, voorkeur, preferentie, csvAfgScholen)
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
  
  public static ToewijzingsAanvraag getAanvraag(String rnstudent) throws DBException {
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen WHERE student_rijksregisternummer = '" + rnstudent + "'");
      int nummer; Status status; String rijksnumStudent; String rijksnumOuder; ArrayList<String> afgScholen;
      LocalDateTime aanmeldingstijdstip; int voorkeur; int broersOfZussen;
      ToewijzingsAanvraag ta = null;
      if(rs.next()) {
	nummer = rs.getInt("toewijzingsaanvraagnummer");
	status = Status.valueOf(rs.getString("status"));
        rijksnumStudent = rs.getString("student_rijksregisternummer");
        rijksnumOuder = rs.getString("ouder_rijksregisternummer");
        Timestamp ts = rs.getTimestamp("aanmeldingstijdstip");
        aanmeldingstijdstip = ts.toLocalDateTime();
        broersOfZussen = rs.getInt("broer_zus");
        voorkeur = rs.getInt("voorkeurschool");
        long preferentie = rs.getLong("preferentie");
        String csvAfgScholen = rs.getString("afgewezen_scholen");
        if(csvAfgScholen == null)
          csvAfgScholen = "";
        ta = new ToewijzingsAanvraag(nummer,
	     rijksnumStudent, rijksnumOuder, aanmeldingstijdstip, 
	      broersOfZussen, status, voorkeur, preferentie, csvAfgScholen);
      }
      DBConnect.closeConnection(con);
      return ta;
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
  public static void bewaarToewijzingsAanvragen(HashMap<Integer, ToewijzingsAanvraag> toewijzingsaanvragen) throws DBException {
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      for (ToewijzingsAanvraag ta : toewijzingsaanvragen.values()) {
          PreparedStatement ps
          = con.prepareStatement("INSERT INTO toewijzingsaanvragen ("
          + "toewijzingsaanvraagnummer, status, student_rijksregisternummer, "
          + "ouder_rijksregisternummer, aanmeldingstijdstip, broer_zus, "
          + "voorkeurschool, preferentie, afgewezen_scholen) "
          + "VALUES(?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " 
          + "status = VALUES(status), broer_zus = VALUES(broer_zus), "
          + "voorkeurschool = VALUES(voorkeurschool), preferentie = VALUES(preferentie), "
          + "afgewezen_scholen = VALUES(afgewezen_scholen)");
          ps.setInt(1, ta.getToewijzingsAanvraagNummer());
          ps.setString(2, ta.getStatus().toString());
          ps.setString(3, ta.getRijksregisterNummerStudent());
          ps.setString(4, ta.getRijksregisterNummerOuder());
          DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
          ps.setString(5, df.format(ta.getAanmeldingsTijdstip()));
          ps.setInt(6, ta.getBroersOfZussen());
	  if(ta.getVoorkeur() == 0)
	    ps.setNull(7, java.sql.Types.INTEGER);
	  else
	    ps.setInt(7, ta.getVoorkeur());
          ps.setLong(8, ta.getPreferentie());
          ps.setString(9, ta.getAfgewezenScholen());
          ps.execute();
    }
    for(ToewijzingsAanvraag ta : toewijzingsaanvragen.values()) {
      int broersOfZussen = 0;
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen "
              + "WHERE ouder_rijksregisternummer = '" + ta.getRijksregisterNummerOuder() + "' "
              + "AND student_rijksregisternummer != '" + ta.getRijksregisterNummerStudent() + "' "
              + "AND voorkeurschool = " + ta.getVoorkeur() + ";");
      while(rs.next()) {
        broersOfZussen++;
      }
      
      rs = st.executeQuery("SELECT * FROM studenten "
              + "WHERE ouder_rijksregisternummer = '" + ta.getRijksregisterNummerOuder() + "' "
              + "AND student_rijksregisternummer != '" + ta.getRijksregisterNummerStudent() + "' "
              + "AND huidige_school = " + ta.getVoorkeur() + ";");
      while(rs.next()) {
        broersOfZussen++;
      }
      
      PreparedStatement ps
      = con.prepareStatement("UPDATE toewijzingsaanvragen "
                           + "SET broer_zus = " + broersOfZussen + " "
                           + "WHERE toewijzingsaanvraagnummer = " 
                           + ta.getToewijzingsAanvraagNummer() + ";");
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
  
  
    
  public static boolean voegNieuweAanvraagToe(ToewijzingsAanvraag ta) throws DBException {
    Connection con = null;
    boolean toevoegen = true;
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen "
                                   + "WHERE student_rijksregisternummer = " 
                                   + ta.getRijksregisterNummerStudent());
      if(rs.next()) {
	toevoegen = false;
      } else {
        PreparedStatement ps = con.prepareStatement(
                             "INSERT INTO toewijzingsaanvragen ("
                           + "toewijzingsaanvraagnummer, status, "
                           + "student_rijksregisternummer, "
                           + "ouder_rijksregisternummer, aanmeldingstijdstip, "
                           + "broer_zus) "
                           + "VALUES('" + ta.getToewijzingsAanvraagNummer() + "',"
                           + "'" + ta.getStatus() + "',"
                           + "'" + ta.getRijksregisterNummerStudent() + "',"
                           + "'" + ta.getRijksregisterNummerOuder() + "',"
                           + "'" + ta.getAanmeldingsTijdstip() + "',"
                           + "'" + ta.getBroersOfZussen() + "')");
        ps.executeUpdate();
      }
      DBConnect.closeConnection(con);
      return toevoegen;
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
  
  public static void bewaarAantalBroersOfZussen(int aanvraagnummer, int aantal) throws DBException {
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      PreparedStatement ps 
      = con.prepareStatement("UPDATE toewijzingsaanvragen SET broer_zus = " + aantal + " "
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
  
  public static int getNextKey() throws DBException {
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen");
      int nextKey = 1;
      while(rs.next()) {
        int key = rs.getInt("toewijzingsaanvraagnummer");
        if(key >= nextKey)
          nextKey = key + 10;
      }
      DBConnect.closeConnection(con);
      return nextKey;
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
  
  public static boolean bewaarVoorkeur(int aanvraagnummer, int voorkeurschool, long preferentie) throws DBException {
    boolean veranderd = false;
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen "
                                   + "WHERE toewijzingsaanvraagnummer = " + aanvraagnummer);
      int voorkeur = 0;
      if(rs.next()) {
        voorkeur = rs.getInt("voorkeurschool");
      }
      if(voorkeur != voorkeurschool) {
        PreparedStatement ps = con.prepareStatement("UPDATE toewijzingsaanvragen "
                                                  + "SET voorkeurschool = " + voorkeurschool + ", "
                                                  + "status = '" + Status.INGEDIEND  + ", "
                                                  + "preferentie = " + preferentie + " "
                                                  + "WHERE toewijzingsaanvraagnummer = " + aanvraagnummer);
        ps.executeUpdate();
        veranderd = true;
      }
      DBConnect.closeConnection(con);
      return veranderd;
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
