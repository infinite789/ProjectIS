
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

/*
 * Klasse voor bewerking met de gegevens afkomstig uit de databank
 *
 * @author Masscho Victor, Dragnev Boris
 */
public class DatabaseConnect  {

    private Connection con;
    private Statement st;
    private ResultSet rs;
    private HashMap<String, Ouder> ouders;
    private HashMap<String, Student> studenten;
    private HashMap<Integer, School> scholen;
    private HashMap<Integer, ToewijzingsAanvraag> toewijzingsaanvragen;
    private final ArrayList<Integer> verwijderdeKeys; 
    private Ouder ingelogdeOuder;

    private final int DEFAULT_KEY = 6001; //default key toewijzingsaanvragen
    private final LocalDateTime START_DATUM = LocalDateTime.of(
            2018, Month.DECEMBER, 15, 0, 0, 0
    );
    private LocalDateTime huidigeDeadline = LocalDateTime.of(
            2018, Month.DECEMBER, 30, 0, 0, 0
    ); //deadline eerste voorkeur
    

    /*
     * Default constructor
     * Maakt connectie met de databank en kopieert de gegevens in lokale
     * HashMap objecten
     */
    public DatabaseConnect() throws Exception {
        con = getConnection();
        this.ouders = getOuders();
        this.studenten = getStudenten();
        this.scholen = getScholen();
        this.toewijzingsaanvragen = getToewijzingsAanvragen();
        con.close();
        this.verwijderdeKeys = new ArrayList<>();
    }

    /*
     * Constructor met een object als argument van het type Algoritme
     * Maakt slechts verbinding met de databank
     * Bij gebruik altijd de connectie erna afbreken!
     */
    
    /*
     * Methode voor het maken van verbinding met de databank 
     */
    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection(
                      "jdbc:mysql://157.193.43.67:3306/BINFG22", "BINFG22",
                      "oKdxQaoh");
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return con;
    }

    /*
     * Methode voor het afsluiten van de verbinding met de databank
     */
    public void closeConnection() {
        try {
            con.close();
        } catch (SQLException ex) {
            System.out.println("Error: " + ex);
        }
    }
    
    /*
     * Methode voor het ophalen van de tabel 'ouders' uit de databank 
     */
    public HashMap<String, Ouder> getOuders() {
        HashMap<String, Ouder> oudersHashMap = new HashMap<>();
        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM ouders");
            while (rs.next()) {
                String rijksregisterNummerOuder
                        = rs.getString("ouder_rijksregisternummer");
                String naam = rs.getString("ouder_naam");
                String voornaam = rs.getString("ouder_voornaam");
                String email = rs.getString("ouder_email");
                String adres = rs.getString("ouder_adres");
                String gebruikersnaam = rs.getString("gebruikersnaam");
                String wachtwoord = rs.getString("wachtwoord");
                oudersHashMap.put(rijksregisterNummerOuder,
                        new Ouder(rijksregisterNummerOuder, naam, voornaam,
                                email, adres, gebruikersnaam, wachtwoord));
            }
            this.ouders = oudersHashMap;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return oudersHashMap;
    }

    /*
     * Methode voor het ophalen van de tabel 'studenten' uit de databank 
     */
    public HashMap<String, Student> getStudenten() {
        HashMap<String, Student> studentenHashMap = new HashMap<>();
        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM studenten");
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
            this.studenten = studentenHashMap;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return studentenHashMap;
    }

    /*
     * Methode voor het ophalen van de tabel 'scholen' uit de databank 
     */
    public HashMap<Integer, School> getScholen() {
        HashMap<Integer, School> scholenHashMap = new HashMap<>();
        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM scholen");
            while (rs.next()) {
                int id = rs.getInt("ID");
                String naam = rs.getString("school_naam");
                String adres = rs.getString("school_adres");
                int capaciteit = rs.getInt("capaciteit");
                scholenHashMap.put(id, new School(id, naam, adres, capaciteit));
            }
            this.scholen = scholenHashMap;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return scholenHashMap;
    }

    /*
     * Methode voor het ophalen van de tabel 'toewijzingsaanvragen' uit de 
     * databank 
     */
    public HashMap<Integer, ToewijzingsAanvraag> getToewijzingsAanvragen() {
        HashMap<Integer, ToewijzingsAanvraag> aanvragenHashMap = new HashMap<>();
        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen");
            while (rs.next()) {
                int aanvraagnummer = rs.getInt("toewijzingsaanvraagnummer");
                Status status
                        = Status.valueOf(rs.getString("status"));
                String rijksregisterNummerStudent
                        = rs.getString("student_rijksregisternummer");
                Timestamp ts
                        = rs.getTimestamp("aanmeldingstijdstip");
                LocalDateTime aanmeldingstijdstip = ts.toLocalDateTime();
                boolean heeftBroerOfZus = rs.getBoolean("broer_zus");
                int voorkeur = rs.getInt("voorkeurschool");
                aanvragenHashMap.put(aanvraagnummer,
                        new ToewijzingsAanvraag(aanvraagnummer,
                                rijksregisterNummerStudent,
                                aanmeldingstijdstip, heeftBroerOfZus,
                                status, voorkeur));
            }
            this.toewijzingsaanvragen = aanvragenHashMap;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return aanvragenHashMap;
    }

    /*
     * Methode voor het controleren van de inloggegevens 
     */
    public boolean inloggen(String gebrnaam, char[] wachtwoordCharArray) {
        boolean ingelogd = false;
        String wachtwoord = "";
        for (char c : wachtwoordCharArray) {
            wachtwoord += c;
        }
        for (Ouder o : ouders.values()) {
            if (o.getGebruikersnaam().equals(gebrnaam)
                    && o.getWachtwoord().equals(wachtwoord)) {
                ingelogdeOuder = o;
                ingelogd = true;
            }
        }
        return ingelogd;
    }

    /*
     * Methode voor het activeren van een account
     */
    public boolean activeren(String rnouder) throws Exception {
        boolean geactiveerd = false;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789";
        String wachtwoord = new Random().ints(8, 0, chars.length())
                .mapToObj(i -> "" + chars.charAt(i))
                .collect(Collectors.joining());
        if (ouders.get(rnouder).getWachtwoord().equals("")) {
            geactiveerd = true;
            ingelogdeOuder = getOuder(rnouder);
            ouders.get(rnouder).setWachtwoord(wachtwoord);
            con = getConnection();
            bewaarOuders();
            con.close();
        }
        return geactiveerd;
    }

    /*
     * Methode voor het indienen van een aanvraag 
     */
    public boolean aanvragen(String rnstudent, String rnouder) {
        for (ToewijzingsAanvraag ta : toewijzingsaanvragen.values()) {
            if (ta.getRijksregisterNummerStudent().equals(rnstudent)) {
                return false;
            }
        }
        if (studenten.get(rnstudent)
                .getRijksregisterNummerOuder().equals(rnouder)) {
            int key = keyNieuweAanvraag();
            toewijzingsaanvragen.put(
                key, new ToewijzingsAanvraag(key, rnstudent)
            );
            return true;
        } else {
            return false;
        }
    }
    
    /*
     * Methode voor het verwijderen van een aanvraag 
     */
    public boolean verwijderAanvraag(String rnstudent) {
        ToewijzingsAanvraag ta = getAanvraag(rnstudent);
        if(ta == null)
            return false;
        else
            toewijzingsaanvragen.remove(ta.getToewijzingsAanvraagNummer());
        return verwijderdeKeys.add(ta.getToewijzingsAanvraagNummer());
    }
    
    /*
     * Methode voor het berekenen van de key van een nieuwe aanvraag
     */
    public int keyNieuweAanvraag() {
        ArrayList<Integer> al = new ArrayList<>(toewijzingsaanvragen.keySet());
        int key = DEFAULT_KEY;
        if (!toewijzingsaanvragen.isEmpty()) {
            Collections.sort(al);
            key = al.get(al.size() - 1) + 10;
        }
        return key;
    }

    /*
     * Methode voor het ophalen van een ouder op basis van 
     * zijn rijksregisternummer
     */
    public Ouder getOuder(String rnouder) {
        return ouders.get(rnouder);
    }

    /*
     * Methode voor het ophalen van de ouder van een gegeven student
     */
    public Ouder getOuderVanStudent(String rnstudent) {
        String rnouder = studenten.get(rnstudent).getRijksregisterNummerOuder();
        return ouders.get(rnouder);
    }

    /*
     * Methode voor het ophalen van een ouder op basis van 
     * zijn inloggegevens
     */
    public Ouder getOuder(String gebrnaam, char[] pass) {
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

    /*
     * Methode voor het ophalen van een student op basis van 
     * zijn rijksregisternummer
     */
    public Student getStudent(String rnstudent) {
        return studenten.get(rnstudent);
    }

    /*
     * Methode voor het ophalen van de studenten die bij een ouder 
     * horen en niet op een school zitten op basis van zijn rijksregisternummer
     */
    public ArrayList<Student> getStudentenVanOuder(String rnouder) {
        ArrayList<Student> studentenVanOuder = new ArrayList<>();
        for (Student s : studenten.values()) {
            if (s.getRijksregisterNummerOuder().equals(rnouder)
                && s.getHuidigeSchool() == 0) {
                studentenVanOuder.add(s);
            }
        }
        return studentenVanOuder;
    }

    /*
     * Methode voor het indienen of aanpassen van een voorkeur
     * De methode past zijn gedrag door de datum te vergelelijken
     * met de deadlines voor het indienen 
     */
    public boolean indienenVoorkeur(int aanvraagnummer, String rnstudent,
                                    int school) {
        boolean geldig = false;
        LocalDate dt = LocalDate.now();
        ToewijzingsAanvraag ta = toewijzingsaanvragen.get(aanvraagnummer);
        if (ta == null) {
            return false;
        }
        ta.setVoorkeur(school);
        ta.setStatus(Status.INGEDIEND);
        if (heeftBroerOfZus(rnstudent, school)) {
            ta.setHeeftBroerOfZus(true);
        } else {
            ta.setHeeftBroerOfZus(false);
        }
        return true;

    }

    /*
     * Methode voor het controleren of de student een broer of een zus
     * heeft op de gekozen school
     */
    public boolean heeftBroerOfZus(String rnstudent, int voorkeurschool) {
        boolean controle = false;
        String ouderEersteStudent;
        String ouderTweedeStudent;
        int schoolBroerOfZus;
        ouderEersteStudent = studenten.get(rnstudent)
                .getRijksregisterNummerOuder();
        for (Student s : studenten.values()) {
            ouderTweedeStudent = s.getRijksregisterNummerOuder();
            schoolBroerOfZus = s.getHuidigeSchool();
            if (ouderEersteStudent.equals(ouderTweedeStudent)
                    && !rnstudent.equals(s.getRijksregisterNummerStudent())
                    && schoolBroerOfZus == voorkeurschool) {
                controle = true;
            }
        }
        return controle;
    }

    /*
     * Methode voor het ophalen van een toewijzingsaanvraag
     * op basis van de rijksregisternummer van een kind
     */
    public ToewijzingsAanvraag getAanvraag(String rnkind) {
        for (ToewijzingsAanvraag ta : toewijzingsaanvragen.values()) {
            if (ta.getRijksregisterNummerStudent().equals(rnkind)) {
                return ta;
            }
        }
        return null;
    }

    /*
     * Methode voor het ophalen van een school op basis van
     * zijn ID
     */
    public String getSchool(int ID) {
        for (School s : scholen.values()) {
            if (s.getID() == ID) {
                return s.getNaam();
            }
        }
        return "";
    }

    /*
     * Methode voor het ophalen van de tabel 'scholen' uit de
     * databank als een array van School objecten
     */
    public School[] getScholenArray() {
        School[] scholenArray = new School[scholen.size()];
        int i = 0;
        for (School s : scholen.values()) {
            scholenArray[i] = s;
            i++;
        }
        return scholenArray;
    }

    /*
     * Methode voor het opslaan van de ouders 
     * De methode verwerkt de aangepaste gegevens in de databank
     * Eerst moet de connctie open staan door getConnection() 
     * op te roepen en erna moet die worden afgesloten met
     * closeConnection()
     */
    public void bewaarOuders() {
        try {
            PreparedStatement ps
            = con.prepareStatement("UPDATE ouders SET "
            + "wachtwoord = '" + ingelogdeOuder.getWachtwoord() + "' "
            + "WHERE ouder_rijksregisternummer = '"
            + ingelogdeOuder.getRijksregisterNummerOuder() + "'");
            ps.execute();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    
    /*
     * Methode voor het opslaan van de towijzingsaanvragen 
     * De methode verwerkt de aangepaste gegevens in de databank
     * Eerst connectie openen voor het gebruiken van de methoden 
     * door getConnection() op te roepen en afsluiten met 
     * closeConnection()
     */
    public void bewaarToewijzingsAanvragen() {
        try {
            for (ToewijzingsAanvraag ta : toewijzingsaanvragen.values()) {
                Ouder o = getOuderVanStudent(ta.getRijksregisterNummerStudent());
                if (o.equals(ingelogdeOuder)) {
                    PreparedStatement ps
                    = con.prepareStatement("INSERT INTO toewijzingsaanvragen ("
                    + "toewijzingsaanvraagnummer, status, "
                    + "student_rijksregisternummer, aanmeldingstijdstip, "
                    + "broer_zus, voorkeurschool) "
                    + "VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " 
                    + "status = VALUES(status), broer_zus = VALUES(broer_zus), "
                    + "voorkeurschool = VALUES(voorkeurschool)");
                    ps.setInt(1, ta.getToewijzingsAanvraagNummer());
                    ps.setString(2, ta.getStatus().toString());
                    ps.setString(3, ta.getRijksregisterNummerStudent());
                    DateTimeFormatter df
                        = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    ps.setString(4, df.format(ta.getAanmeldingsTijdstip()));
                    ps.setInt(6, ta.getVoorkeur());
                    ps.setBoolean(5, ta.heeftHeeftBroerOfZus());
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
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
    
    /*
     * Methode voor het opslaan van de towijzingsaanvragen 
     * De methode verwerkt de aangepaste gegevens in de databank
     * Eerst connectie openen voor het gebruiken van de methoden 
     * door getConnection() op te roepen en afsluiten met 
     * closeConnection()
     */
    public void bewaarToewijzingsAanvragen(ArrayList<ToewijzingsAanvraag> lta) {
        try {
            for (ToewijzingsAanvraag ta : lta) {
                PreparedStatement ps
                = con.prepareStatement("INSERT INTO toewijzingsaanvragen ("
                + "toewijzingsaanvraagnummer, status, "
                + "student_rijksregisternummer, aanmeldingstijdstip, "
                + "broer_zus, voorkeurschool) "
                + "VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " 
                + "status = VALUES(status), broer_zus = VALUES(broer_zus), "
                + "voorkeurschool = VALUES(voorkeurschool)");
                ps.setInt(1, ta.getToewijzingsAanvraagNummer());
                ps.setString(2, ta.getStatus().toString());
                ps.setString(3, ta.getRijksregisterNummerStudent());
                DateTimeFormatter df
                    = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                ps.setString(4, df.format(ta.getAanmeldingsTijdstip()));
                ps.setInt(6, ta.getVoorkeur());
                ps.setBoolean(5, ta.heeftHeeftBroerOfZus());
                ps.execute();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
        
    public void exporteerWachtLijsten() throws ToewijzingException {
        for(School s : scholen.values()) {
            String bestandPath = "./lijsten/s" + s.getNaam().substring(0, 5)
                                .replaceAll(" ", "").toLowerCase() + s.getID();
            ObjectOutputStream output;
            try {
                File file = new File(bestandPath);
                if(file.exists())
                    throw new ToewijzingException("De wachtlijsten zijn al geëxporteerd!");
                output = new ObjectOutputStream(new FileOutputStream(
                        bestandPath
                ));
                output.writeObject(getWachtLijst(s.getID()));
                output.close();
            } catch (IOException e) {
                throw new ToewijzingException("Fout bij exporteren: " + e);
            }
        }
    }
    
    public ArrayList<ToewijzingsAanvraag> getWachtLijst(int schoolID) {
            LocalDateTime now = LocalDateTime.now();
            ArrayList<ToewijzingsAanvraag> wachtLijst = new ArrayList<>();
            for(ToewijzingsAanvraag ta : toewijzingsaanvragen.values()) {
                if(ta.getVoorkeur() == schoolID) {
                    wachtLijst.add(ta);
                }
            }
            return wachtLijst;
    }
    public ArrayList<ToewijzingsAanvraag> wachtLijstLaden(School s) {
        ArrayList<ToewijzingsAanvraag> wachtLijst = null;
        try {
            String bestandPath = "./lijsten/s" + s.getNaam().substring(0, 5)
                                .replaceAll(" ", "").toLowerCase() + s.getID();
            ObjectInputStream input;
            input = new ObjectInputStream(new FileInputStream(bestandPath));
            wachtLijst = (ArrayList)input.readObject();
            input.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return wachtLijst;
    }
    
    public void wachtLijstOpslaan(ArrayList<ToewijzingsAanvraag> lijst, String path) {
        ObjectOutputStream output;
        try {
            output = new ObjectOutputStream(new FileOutputStream(
                    path
            ));
            output.writeObject(lijst);
            output.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } 
    }
    
    public ArrayList<ToewijzingsAanvraag> sorteerWachtLijst(ArrayList<ToewijzingsAanvraag> wachtLijst) {
        for(int i = 0; i < wachtLijst.size(); i++) {
            ToewijzingsAanvraag lagerePref = wachtLijst.get(i);
            ToewijzingsAanvraag hogerePref = null;
            int lagePrefIndex = i;
            for(int j = i+1; j < wachtLijst.size(); j++) {
                long preferentie;
                if(hogerePref == null) 
                    preferentie = getPreferentie(wachtLijst.get(i));
                else
                    preferentie = getPreferentie(hogerePref);
                long preferentie2 = getPreferentie(wachtLijst.get(j));
                if(preferentie < preferentie2) {
                    hogerePref = wachtLijst.get(j);
                    lagePrefIndex = j;
                }
            }
            if(hogerePref != null) {
                wachtLijst.set(i, hogerePref);
                wachtLijst.set(lagePrefIndex, lagerePref);
            }
        }  
        return wachtLijst;
    }
    
    public ArrayList<ToewijzingsAanvraag> laadAfgewezenStudenten() throws Exception {
        ArrayList<ToewijzingsAanvraag> afgewezenStudenten;
        File afgStBestand = new File("./lijsten/afgewezenStudenten");
        if(afgStBestand.exists()){
            ObjectInputStream input = new ObjectInputStream(
                                      new FileInputStream(
                        "./lijsten/afgewezenStudenten"
            ));
            afgewezenStudenten = (ArrayList)input.readObject();
            input.close();
        } else {
            afgewezenStudenten = new ArrayList<>();
        }
        return afgewezenStudenten;
    }
    
    public void sorteerAlgoritme() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ArrayList<ToewijzingsAanvraag> afgewezenStudenten;
        afgewezenStudenten = laadAfgewezenStudenten();
        File afgStBestand = new File("./lijsten/afgewezenStudenten");
        for(School s : scholen.values()) {
            String path = "./lijsten/s" + s.getNaam().substring(0, 5)
                          .replaceAll(" ", "").toLowerCase() + s.getID();
            ArrayList<ToewijzingsAanvraag> wachtLijst = wachtLijstLaden(s);
            if(!afgStBestand.exists()) {
                wachtLijst = sorteerWachtLijst(wachtLijst);
            } else {
                for(ToewijzingsAanvraag ta : afgewezenStudenten) {
                    if(ta.getVoorkeur() == s.getID() 
                       && afgewezenStudenten.remove(ta))
                        wachtLijst.add(ta);
                }
                wachtLijst = sorteerWachtLijst(wachtLijst);
            }
            //studenten afwijzen indien de school vol zit
            while(wachtLijst.size() > s.getPlaatsen()) {
                ToewijzingsAanvraag ta = wachtLijst.get(wachtLijst.size()-1);
                wachtLijst.remove(ta);
                afgewezenStudenten.add(ta);
            }
            //wachtlijst van school schrijven naar bestand
            wachtLijstOpslaan(wachtLijst, path);
        }
        wachtLijstOpslaan(afgewezenStudenten, "./lijsten/afgewezenStudenten");
        updateStatus();
    }
    
    public long getPreferentie(ToewijzingsAanvraag ta) {
        long preferentie = huidigeDeadline.toEpochSecond(ZoneOffset.UTC)
                         - ta.getAanmeldingsTijdstip().toEpochSecond(ZoneOffset.UTC);
        long bonusPunten = huidigeDeadline.toEpochSecond(ZoneOffset.UTC)
                         - START_DATUM.toEpochSecond(ZoneOffset.UTC);
        if(ta.heeftHeeftBroerOfZus()) 
            preferentie += bonusPunten;
        return preferentie;
                        
    }
    
    public void updateStatus() {
        getConnection();
        for (ToewijzingsAanvraag ta : toewijzingsaanvragen.values()) {
            if (ta.getStatus().equals(Status.INGEDIEND)) {
                ta.setStatus(Status.VOORLOPIG);
            }
        }
        bewaarToewijzingsAanvragen();
        closeConnection();
    }
    
    public boolean schoolIsAfgewezen(int schoolID, int aanvraagnummer)  {
        try {
            ArrayList<ToewijzingsAanvraag> afgewezenStudenten
                        = laadAfgewezenStudenten();
            for(ToewijzingsAanvraag ta : afgewezenStudenten) {
                if(ta.getVoorkeur() == schoolID
                        && ta.getToewijzingsAanvraagNummer() == aanvraagnummer)
                    return true;
            }
            return false;
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            return false;
        }
    }
    
    public LocalDateTime getHuidigeDeadline() {
        return this.huidigeDeadline;
    }
    /*
     * Methode voor het overschrijven van de lokale gegevens naar
     * de databank
     */
    public void bewarenEnAfsluiten() throws Exception {
        con = getConnection();
        bewaarOuders();
        bewaarToewijzingsAanvragen();
        con.close();
    }
}
