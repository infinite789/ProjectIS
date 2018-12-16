
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
public class DBOuder {
  /*
   * Methode voor het ophalen van de tabel 'ouders' uit de databank 
   */
  public static HashMap<String, Ouder> getOuders() throws DBException {
    Connection con = null;
    HashMap<String, Ouder> oudersHashMap = new HashMap<>();
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM ouders");
      while (rs.next()) {
        String rijksregisterNummerOuder
                = rs.getString("ouder_rijksregisternummer");
        String naam = rs.getString("ouder_naam");
        String voornaam = rs.getString("ouder_voornaam");
        String email = rs.getString("ouder_email");
        String straat = rs.getString("adres_straat");
        String gemeente = rs.getString("adres_gemeente");
        String gebruikersnaam = rs.getString("gebruikersnaam");
        String wachtwoord = rs.getString("wachtwoord");
        oudersHashMap.put(rijksregisterNummerOuder,
                          new Ouder(rijksregisterNummerOuder, naam, voornaam,
                          email, straat, gemeente, gebruikersnaam, wachtwoord));
      }
      DBConnect.closeConnection(con);
      return oudersHashMap;
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
   * Methode voor het opslaan van de ouders 
   * De methode verwerkt de aangepaste gegevens in de databank
   * Eerst moet de connctie open staan door getConnection() 
   * op te roepen en erna moet die worden afgesloten met
   * closeConnection()
   */
  public static void bewaarOuder(Ouder o) throws DBException {
    Connection con = null;
    try {
      con = DBConnect.getConnection();
      PreparedStatement ps = con.prepareStatement("UPDATE ouders SET " 
                           + "wachtwoord = '" + o.getWachtwoord() + "' "
                           + "WHERE ouder_rijksregisternummer = '"
                           + o.getRijksregisterNummer()+ "'");
      ps.execute(); 
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
  
  /*
   * Methode voor het controleren van de inloggegevens, 
   * retourneert -1 als de gegevens fout zijn, 0 als de ingelogde gebruiker
   * een ouder is, 1 als de ingelogde gebruiker een administrator is
   */
  public static Ouder getOuder(String rnouder) throws DBException {
    Connection con = null;
    Ouder o = null;
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM ouders "
				   + "WHERE ouder_rijksregisternummer = '" + rnouder + "';");
      if(rs.next()) {
	String rijksregisterNummerOuder = rs.getString("ouder_rijksregisternummer");
	String naam = rs.getString("ouder_naam");
	String voornaam = rs.getString("ouder_voornaam");
	String email = rs.getString("ouder_email");
	String straat = rs.getString("adres_straat");
	String gemeente = rs.getString("adres_gemeente");
	String gebruikersnaam = rs.getString("gebruikersnaam");
	String wwoord = rs.getString("wachtwoord");
	o = new Ouder(rijksregisterNummerOuder, naam, voornaam,
                      email, straat, gemeente, gebruikersnaam, wwoord);
      }
      DBConnect.closeConnection(con);
      return o;
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
  
  public static ArrayList<Student> getStudentenVanOuder(String rnouder) throws DBException {
    Connection con = null;
    ArrayList<Student> studentenVanOuder = new ArrayList<>();
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM studenten "
				   + "WHERE ouder_rijksregisternummer = '" + rnouder + "';");
      while(rs.next()) {
        String rijksregisterNummerOuder = rs.getString("ouder_rijksregisternummer");
        String rijksregisterNummerStudent = rs.getString("student_rijksregisternummer");
        String naam = rs.getString("student_naam");
        String voornaam = rs.getString("student_voornaam");
        String telefoonnummer = rs.getString("student_telefoonnummer");
        Integer huidigeSchool = rs.getInt("huidige_school");
        if(huidigeSchool == 0) {
          studentenVanOuder.add(new Student(rijksregisterNummerStudent,
                                rijksregisterNummerOuder, naam, voornaam,
                                telefoonnummer, huidigeSchool));
        }
      }
      DBConnect.closeConnection(con);
      return studentenVanOuder;
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
