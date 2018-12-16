
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author bddra
 */
public class DBSchool {

  /*
   * Methode voor het ophalen van de tabel 'scholen' uit de databank 
   */

  public static HashMap<Integer, School> getScholen() throws DBException {
    Connection con = null;
    HashMap<Integer, School> scholenHashMap = new HashMap();
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM scholen");
      while (rs.next()) {
        int id = rs.getInt("ID");
        String naam = rs.getString("school_naam");
        String adres = rs.getString("school_adres");
        int capaciteit = rs.getInt("capaciteit");
        ArrayList<ToewijzingsAanvraag> wachtLijst = new ArrayList();
        scholenHashMap.put(id, new School(id, naam,
                adres, capaciteit, wachtLijst));
      }
      for(School s : scholenHashMap.values()) {
	rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen "
                           + "WHERE voorkeurschool = " + s.getID());
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
	  String afgewezenScholenCsv = rs.getString("afgewezen_scholen");
	  ArrayList<String> als = CSV.toList(afgewezenScholenCsv);
	  s.getWachtLijst().add(new ToewijzingsAanvraag(aanvraagnummer,
		  rijksregisterNummerStudent, rijksregisterNummerOuder, aanmeldingstijdstip,
		  broersOfZussen, status, voorkeur, preferentie, als));
	}
      }
      DBConnect.closeConnection(con);
      return scholenHashMap;
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
  
  public static School getSchool(int ID) throws DBException {
    Connection con = null;
    School school = null;
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM scholen WHERE ID = " + ID);
      if (rs.next()) {
        int schoolID = rs.getInt("ID");
        String naam = rs.getString("school_naam");
        String adres = rs.getString("school_adres");
        int capaciteit = rs.getInt("capaciteit");
        ArrayList<ToewijzingsAanvraag> wachtLijst = new ArrayList<>();
        school = new School(schoolID, naam, adres, capaciteit, wachtLijst);
        rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen "
                           + "WHERE voorkeurschool = " + school.getID());
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
	  String afgewezenScholenCsv = rs.getString("afgewezen_scholen");
	  ArrayList<String> als = CSV.toList(afgewezenScholenCsv);
	  school.getWachtLijst().add(new ToewijzingsAanvraag(aanvraagnummer,
		  rijksregisterNummerStudent, rijksregisterNummerOuder, aanmeldingstijdstip,
		  broersOfZussen, status, voorkeur, preferentie, als));
	}
      }
      DBConnect.closeConnection(con);
      return school;
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
  
  
  public static void addWachtLijsten() {
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      
      DBConnect.closeConnection(con);
    } catch (Exception e) {
      
    }
  }
  
  public static ArrayList<ToewijzingsAanvraag> getWachtLijst(School s) throws DBException {
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs= st.executeQuery("SELECT * FROM toewijzingsaanvragen "
                                  + "WHERE voorkeurschool = " + s.getID());
      ArrayList<ToewijzingsAanvraag> wachtLijst = new ArrayList();
      while(rs.next()) {
        int aanvraagnummer = rs.getInt("toewijzingsaanvraagnummer");
        Status status = Status.valueOf(rs.getString("status"));
        String rijksregisterNummerStudent = rs.getString("student_rijksregisternummer");
        String rijksregisterNummerOuder = rs.getString("ouder_rijksregisternummer");
        Timestamp ts = rs.getTimestamp("aanmeldingstijdstip");
        LocalDateTime aanmeldingstijdstip = ts.toLocalDateTime();
        int broersOfZussen = rs.getInt("broer_zus");
        int voorkeur = rs.getInt("voorkeurschool");
        long preferentie = rs.getLong("preferentie");
        ArrayList<String> afgScholen = CSV.toList(rs.getString("afgewezen_scholen"));
        wachtLijst.add(new ToewijzingsAanvraag(aanvraagnummer, rijksregisterNummerStudent, 
                                    rijksregisterNummerOuder, aanmeldingstijdstip, 
                                    broersOfZussen, status, voorkeur, preferentie, afgScholen)
        );
      }
      DBConnect.closeConnection(con);
      return wachtLijst;
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
  
  public static void setCapaciteit(int ID, int nieuwAantal) throws DBException{
      Connection con = null;
      try{
          con = DBConnect.getConnection();
          PreparedStatement ps = con.prepareStatement("UPDATE scholen SET capaciteit = ? WHERE ID = ?");
          ps.setInt(1,nieuwAantal);
          ps.setInt(2, ID);
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

