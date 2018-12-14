
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
  
  public static int getBroersEnZussen(int aanvraagnummer, Student s, int voorkeurschool) throws DBException {
    Connection con = null;
    int aantal = 0;
    try {
      con = DBConnect.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM studenten");
      while (rs.next()) {
        String rijksregisterNummerOuder = rs.getString("ouder_rijksregisternummer");
        String rijksregisterNummerStudent = rs.getString("student_rijksregisternummer");
        Integer huidigeSchool = rs.getInt("huidige_school");
        if(!rijksregisterNummerStudent.equals(s.getRijksregisterNummer()) 
           && rijksregisterNummerOuder.equals(s.getRijksregisterNummerOuder())
           && voorkeurschool == huidigeSchool) {
          aantal++;
        }
      }
      rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen");
      while(rs.next()) {
      String rijksnumStudent = rs.getString("student_rijksregisternummer");
      String rijksnumOuder = rs.getString("ouder_rijksregisternummer");
      int voorkeur = rs.getInt("voorkeurschool");
      if(!rijksnumStudent.equals(s.getRijksregisterNummer()) 
         &&  rijksnumOuder.equals(s.getRijksregisterNummerOuder())
         && voorkeur == voorkeurschool) {
        aantal++;
      }
      }
      DBConnect.closeConnection(con);
      return aantal;
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
  
  public static ArrayList<String> afgScholenCsvOmzetten(String csv) {
    String[] afgSchArray = csv.split(";");
    ArrayList<String> als = new ArrayList<>();
    for(String str : afgSchArray)
	if(!str.equals(""))
	    als.add(str);
    return als;
  }
}
