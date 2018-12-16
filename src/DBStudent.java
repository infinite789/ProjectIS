
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
public class DBStudent {

  /*
   * Methode voor het ophalen van de tabel 'studenten' uit de databank 
   */
  public static HashMap<String, Student> getStudenten() throws DBException {
    Connection con = null;
    HashMap<String, Student> studentenHashMap = new HashMap<>();
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM studenten");
      while (rs.next()) {
        String rijksregisterNummerOuder
                = rs.getString("ouder_rijksregisternummer");
        String rijksregisterNummerStudent
                = rs.getString("student_rijksregisternummer");
        String naam = rs.getString("student_naam");
        String voornaam = rs.getString("student_voornaam");
        String telefoonnummer = rs.getString("student_telefoonnummer");
        Integer huidigeSchool = rs.getInt("huidige_school");
        studentenHashMap.put(rijksregisterNummerStudent,
                new Student(rijksregisterNummerStudent,
                        rijksregisterNummerOuder, naam, voornaam,
                        telefoonnummer, huidigeSchool));
      }
      DBConnect.closeConnection(con);
      return studentenHashMap;
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
   * Methode voor het ophalen van de tabel 'studenten' uit de databank 
   */
  public static Student getStudent(String rnstudent) throws DBException {
    Connection con = null;
    Student student = null;
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM studenten "
                                   + "WHERE student_rijksregisternummer = '" + rnstudent + "'");
      if (rs.next()) {
        String rijksregisterNummerOuder
                = rs.getString("ouder_rijksregisternummer");
        String rijksregisterNummerStudent
                = rs.getString("student_rijksregisternummer");
        String naam = rs.getString("student_naam");
        String voornaam = rs.getString("student_voornaam");
        String telefoonnummer = rs.getString("student_telefoonnummer");
        Integer huidigeSchool = rs.getInt("huidige_school");
        student = new Student(rijksregisterNummerStudent,rijksregisterNummerOuder, 
                           naam, voornaam, telefoonnummer, huidigeSchool);
      }
      DBConnect.closeConnection(con);
      return student;
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
