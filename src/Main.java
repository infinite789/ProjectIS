
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.mail.MessagingException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bddra
 */
public class Main {

  private HashMap<String, Ouder> ouders;
  private HashMap<String, Student> studenten;
  private HashMap<Integer, School> scholen;
  private HashMap<Integer, ToewijzingsAanvraag> toewijzingsaanvragen;
  private Ouder ingelogdeOuder;
  private TypeGebruiker typeGebruiker;

  private final String ADMIN_ACCOUNT = "admin";
  private final String ADMIN_PASS = "admin";
  private final LocalDateTime START_DATUM = LocalDateTime.of(
          2018, Month.DECEMBER, 1, 0, 0, 0);//start inschrijvingen 
  private LocalDateTime huidigeDeadline = LocalDateTime.of(
          2018, Month.DECEMBER, 30, 0, 0, 0);//dynamische deadline, die na elke 'sorteerronde' een andere waarde aanneemt 

  public Main() {
    try {
      studenten = DBStudent.getStudenten();
      ouders = DBOuder.getOuders();
      scholen = DBSchool.getScholen();
      toewijzingsaanvragen = DBToewijzingsAanvraag.getToewijzingsAanvragen();
    } catch(DBException dbe) {
      
    } finally {
      this.typeGebruiker = TypeGebruiker.NULL;
      this.ingelogdeOuder = null;
    }
  }

  public static void main(String[] args) throws Exception {
    UI ui = new UI(new Main());
    ui.setVisible(true);
  }  
    
  public TypeGebruiker inloggen(String gebrnaam, char[] wachtwoord) {
    Ouder ouder = null;
    String wwoord = "";
    for(char c : wachtwoord) {
      wwoord += c;
    }
    for(Ouder o : ouders.values()) {
      if(o.getGebruikersnaam().equals(gebrnaam) && o.getWachtwoord().equals(wwoord)) {
        ouder = o;
        break;
      }
    }
    try {
      if(ouder != null) {
        ingelogdeOuder = DBOuder.getOuder(ouder.getRijksregisterNummer());
        typeGebruiker = TypeGebruiker.OUDER;
      }
      if(gebrnaam.equals(ADMIN_ACCOUNT) && wwoord.equals(ADMIN_PASS)) 
	typeGebruiker = TypeGebruiker.ADMIN;
    } catch (DBException dbe) {
      typeGebruiker = TypeGebruiker.NULL;
      dbe.getMessage();
      return typeGebruiker;
    } 
    return typeGebruiker;
  }

  /*
   * Methode voor het activeren van een account
   */
  public boolean activeren(String rnouder) {
    boolean geactiveerd = false;
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                 + "abcdefghijklmnopqrstuvwxyz"
                 + "0123456789";
    String wachtwoord = new Random().ints(8, 0, chars.length())
                .mapToObj(i -> "" + chars.charAt(i))
                .collect(Collectors.joining());
    try {
      Ouder ouder = DBOuder.getOuder(rnouder);
      ouders.put(ouder.getRijksregisterNummer(), ouder);
      if (ouder.getWachtwoord().equals("")) {
        ouder.setWachtwoord(wachtwoord);
        String[] ontvangers = {ouder.getEmail()};
        Email email = new Email(ouder.getVoornaam(), ouder.getGebruikersnaam(),
                                ouder.getWachtwoord(), ontvangers[0], TypeBericht.ACTIVATIE
        );
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executor.execute(() -> {
          try {
            DBOuder.bewaarOuder(ouders.get(rnouder));
            email.send();
          } catch (DBException | MessagingException e) {
            e.getMessage();
          }
        });
        geactiveerd = true;
      }
    } catch (DBException dbe) {
      dbe.getMessage();
    } catch (Exception e) {
      e.getMessage();
    }
    return geactiveerd;
  }

  /*
   * Methode voor het indienen van een aanvraag 
   */
  public boolean addAanvraag(ToewijzingsAanvraag ta) {
    try {
      if(DBToewijzingsAanvraag.voegNieuweAanvraagToe(ta)) {
        toewijzingsaanvragen.put(ta.getToewijzingsAanvraagNummer(), ta);
        return true;
      } else {
        return false;
      }
    } catch(DBException dbe) {
      dbe.getMessage();
      return false;
    }
  }
  
  /*
   * Methode voor het verwijderen van een aanvraag 
   */
  public boolean verwijderAanvraag(int nummer) {
    ToewijzingsAanvraag ta = toewijzingsaanvragen.remove(nummer);
    if(ta != null) {
      try {
	DBToewijzingsAanvraag.verwijder(nummer);
      } catch (DBException dbe) {
	dbe.getMessage();
      }
    }
    return ta != null;
  }

  
  /*
   * Methode voor het berekenen van de key van een nieuwe aanvraag
   */
  public int keyNieuweAanvraag() {
    try {
      return DBToewijzingsAanvraag.getNextKey();
    } catch (DBException dbe) {
      dbe.getMessage();
      return 0;
    }
  }

 

  /*
     * Methode voor het ophalen van een ouder op basis van 
     * zijn inloggegevens
   */
  public Ouder ophalenOuder(String gebrnaam, char[] pass) {
    Ouder o = null;
    String wachtwoord = "";
    for (char c : pass) {
      wachtwoord += c;
    }
    for (Ouder ouder : ouders.values()) {
      if (ouder.getGebruikersnaam().equals(gebrnaam)
              && wachtwoord.equals(ouder.getWachtwoord())) {
        o = ouder;
      }
    }
    return o;
  }

  public Ouder ophalenOuder(String rnouder) {
    try {
      return DBOuder.getOuder(rnouder);
    } catch (DBException dbe) {
      dbe.getMessage();
      return null;
    }
  }
  /*
     * Methode voor het ophalen van een student op basis van 
     * zijn rijksregisternummer
   */
  public Student ophalenStudent(String rnstudent) {
    Student s;
    try {
      s = DBStudent.getStudent(rnstudent);
      return s;
    } catch (DBException dbe) {
      dbe.getMessage();
      return null;
    }
  }

  /*
     * Methode voor het ophalen van de studenten die bij een ouder 
     * horen en niet op een school zitten op basis van zijn rijksregisternummer
   */
  public ArrayList<Student> ophalenKinderen(String rnouder) {
    ArrayList<Student> studentenVanOuder;
    try {
      studentenVanOuder = DBOuder.getStudentenVanOuder(rnouder);
      return studentenVanOuder;
    } catch (DBException dbe) {
      dbe.getMessage();
      return null;
    }
  }
  
  /*
   * Methode voor het ophalen van een school op basis van
   * zijn ID
   */
  public School ophalenSchool(int ID) {
    School s = null;
    try {
      s = DBSchool.getSchool(ID);
      return s;
    } catch (DBException dbe) {
      dbe.getMessage();
      return s;     
    }
  }
  
  public School[] ophalenScholen() {
    try {
      scholen = DBSchool.getScholen();
      School[] scholenArray = new School[scholen.size()];
      int i = 0;
      for (School s : scholen.values()) {
        scholenArray[i] = s;
        i++;
      }
      return scholenArray;
    } catch (DBException dbe) {
      dbe.getMessage();
      return null;
    }
  }
  
  public ToewijzingsAanvraag ophalenAanvraag(String rnstudent) {
    try {
      return DBToewijzingsAanvraag.getAanvraag(rnstudent);
    } catch (DBException dbe) {
      dbe.getMessage();
    }
    return null;
  }
  
  /*
     * Methode voor het indienen of aanpassen van een voorkeur
     * De methode past zijn gedrag door de datum te vergelelijken
     * met de deadlines voor het indienen 
   */
   public boolean indienenVoorkeur(int aanvraagnummer, Student student, int schoolID)
          throws DBException {
    try {
      ToewijzingsAanvraag ta = DBToewijzingsAanvraag.getAanvraag(student.getRijksregisterNummer());
      int vorigVoorkeur = ta.getVoorkeur();
      if(DBToewijzingsAanvraag.bewaarVoorkeur(aanvraagnummer, schoolID)) {
        toewijzingsaanvragen.get(aanvraagnummer).setVoorkeur(schoolID);
        toewijzingsaanvragen.get(aanvraagnummer).setStatus(Status.INGEDIEND);
        scholen.get(schoolID).getWachtLijst().add(ta);
        if(vorigVoorkeur != 0) 
          scholen.get(vorigVoorkeur).getWachtLijst().remove(ta);
        toewijzingsaanvragen.get(aanvraagnummer).setBroersOfZussen(DBStudent.getBroersEnZussen(aanvraagnummer, student, schoolID));
      }
    } catch (DBException dbe) {
      dbe.getMessage();
    } catch (Exception e) {
      e.getMessage();
    }
    return true;
  }
  /*
  public ArrayList<ToewijzingsAanvraag> laadAfgewezenStudenten() throws Exception {
      
  }
  */
  
  
  public void sorteerWachtLijst(ArrayList<ToewijzingsAanvraag> wachtLijst) {
      for (int i = 0; i < wachtLijst.size(); i++) {
	  ToewijzingsAanvraag lagerePref = wachtLijst.get(i);
	  ToewijzingsAanvraag hogerePref = null;
	  int lagePrefIndex = i;
	  for (int j = i + 1; j < wachtLijst.size(); j++) {
	      long preferentie;
	      if (hogerePref == null) {
		  preferentie = getPreferentie(wachtLijst.get(i));
	      } else {
		  preferentie = getPreferentie(hogerePref);
	      }
	      long preferentie2 = getPreferentie(wachtLijst.get(j));
	      if (preferentie < preferentie2) {
		  hogerePref = wachtLijst.get(j);
		  lagePrefIndex = j;
	      }
	  }
	  if (hogerePref != null) {
	      wachtLijst.set(i, hogerePref);
	      wachtLijst.set(lagePrefIndex, lagerePref);
	  }
      }
  }
  
  public void sorteerAlgoritme() {
    int afgewezenStudenten = 0;
    try {
      toewijzingsaanvragen = DBToewijzingsAanvraag.getToewijzingsAanvragen();
      scholen = DBSchool.getScholen();
      for (School s : scholen.values()) {
	ArrayList<ToewijzingsAanvraag> wachtLijst = s.getWachtLijst();
	sorteerWachtLijst(wachtLijst);
	//studenten afwijzen indien de school vol zit
	while (wachtLijst.size() > s.getPlaatsen()) {
          ToewijzingsAanvraag ta = wachtLijst.get(wachtLijst.size() - 1);
          wachtLijst.remove(ta);
          ta.getAfgewezenScholen().add(String.valueOf(s.getID()));
          ta.setStatus(Status.ONTWERP);
          afgewezenStudenten++;
	}
      } 
      if(afgewezenStudenten > 0)
        updateStatus(Status.VOORLOPIG);
      else
        updateStatus(Status.DEFINITIEF);
      DBToewijzingsAanvraag.bewaarToewijzingsAanvragen(toewijzingsaanvragen);
    } catch(DBException dbe) {
      dbe.getMessage();
    }
  }

  public void updateStatus(Status s) {
    for (ToewijzingsAanvraag ta : toewijzingsaanvragen.values()) {
	  if (!ta.getStatus().equals(Status.ONTWERP)){
	    ta.setStatus(s);
	  }
    }
  }
  
  public long getPreferentie(ToewijzingsAanvraag ta) {
      long preferentie = huidigeDeadline.toEpochSecond(ZoneOffset.UTC)
	      - ta.getAanmeldingsTijdstip().toEpochSecond(ZoneOffset.UTC);
      long bonusPunten = huidigeDeadline.toEpochSecond(ZoneOffset.UTC)
	      - START_DATUM.toEpochSecond(ZoneOffset.UTC);
      if (ta.getBroersOfZussen() > 0) {
	  preferentie += bonusPunten*ta.getBroersOfZussen();
      }
      return preferentie;

  }

  public boolean schoolIsAfgewezen(int schoolID, int nummer) {
      try {
        toewijzingsaanvragen = DBToewijzingsAanvraag.getToewijzingsAanvragen();
        ArrayList<String> afgScholen = toewijzingsaanvragen.get(nummer).getAfgewezenScholen();
        if(afgScholen.contains(schoolID))
          return true;
      } catch (Exception ex) {
        System.out.println("Error: " + ex);
        return false;
      }
      return false;
  }

  public LocalDateTime getHuidigeDeadline() {
      return this.huidigeDeadline;
  }
  
  public void veranderCapaciteit(int ID, int nieuweCap){
      try {
          DBSchool.setCapaciteit(ID, nieuweCap);
      }
      catch (DBException dbe){
          dbe.getMessage();
      }
  }
}

