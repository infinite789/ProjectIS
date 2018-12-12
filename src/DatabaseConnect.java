
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/*
 * Klasse voor bewerking met de gegevens afkomstig uit de databank
 *
 * @author Boris Dragnev, Victor Masscho, Jean Janssens, Edith Lust, Job van Lambalgen
 */
public class DatabaseConnect  {

    private Connection con;
    private final HashMap<String, Ouder> ouders;
    private final HashMap<String, Student> studenten;
    private final HashMap<Integer, School> scholen;
    private final HashMap<Integer, ToewijzingsAanvraag> toewijzingsaanvragen;
    private final ArrayList<Integer> verwijderdeKeys; 
    private Ouder ingelogdeOuder;
    private boolean ingelogdAlsAdmin;

    
    private final String ADMIN_ACCOUNT = "admin";
    private final String ADMIN_WACHTWOORD = "admin";
    private final int DEFAULT_KEY = 6001; //default key toewijzingsaanvragen
    
    /*
     * Default constructor
     * Maakt connectie met de databank en kopieert de gegevens in lokale
     * HashMap objecten
     */
    public DatabaseConnect() {
        getConnection();
        this.ouders = laadOuders();
        this.studenten = laadStudenten();
        this.scholen = laadScholen();
        this.toewijzingsaanvragen = laadToewijzingsAanvragen();
        for(School s : scholen.values()) {
            laadWachtLijst(s);
        }
        closeConnection();
        this.verwijderdeKeys = new ArrayList<>();
        this.ingelogdAlsAdmin = false;
    }

    public HashMap<String, Ouder> getOuders() {
        return ouders;
    }

    public HashMap<String, Student> getStudenten() {
        return studenten;
    }

    public HashMap<Integer, School> getScholen() {
        return scholen;
    }

    public HashMap<Integer, ToewijzingsAanvraag> getToewijzingsaanvragen() {
        return toewijzingsaanvragen;
    }

    public ArrayList<Integer> getVerwijderdeKeys() {
        return verwijderdeKeys;
    }

    public Ouder getIngelogdeOuder() {
        return ingelogdeOuder;
    }
     
    /*
     * Methode voor het ophalen van de tabel 'ouders' uit de databank 
     */
    public final HashMap<String, Ouder> laadOuders() {
        HashMap<String, Ouder> oudersHashMap = new HashMap<>();
        try {
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
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return oudersHashMap;
    }

    /*
     * Methode voor het ophalen van de tabel 'studenten' uit de databank 
     */
    public final HashMap<String, Student> laadStudenten() {
        HashMap<String, Student> studentenHashMap = new HashMap<>();
        try {
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
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return studentenHashMap;
    }

    /*
     * Methode voor het ophalen van de tabel 'scholen' uit de databank 
     */
    public final HashMap<Integer, School> laadScholen() {
        HashMap<Integer, School> scholenHashMap = new HashMap<>();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM scholen");
            while (rs.next()) {
                int id = rs.getInt("ID");
                String naam = rs.getString("school_naam");
                String adres = rs.getString("school_adres");
                int capaciteit = rs.getInt("capaciteit");
                scholenHashMap.put(id, new School(id, naam, 
                                   adres, capaciteit, new ArrayList<>()));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return scholenHashMap;
    }

    /*
     * Methode voor het ophalen van de tabel 'toewijzingsaanvragen' uit de 
     * databank 
     */
    public final HashMap<Integer, ToewijzingsAanvraag> laadToewijzingsAanvragen() {
        HashMap<Integer, ToewijzingsAanvraag> aanvragenHashMap = new HashMap<>();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen");
            while (rs.next()) {
                int aanvraagnummer = rs.getInt("toewijzingsaanvraagnummer");
                Status status = Status.valueOf(rs.getString("status"));
                String rijksregisterNummerStudent
                        = rs.getString("student_rijksregisternummer");
                Timestamp ts = rs.getTimestamp("aanmeldingstijdstip");
                LocalDateTime aanmeldingstijdstip = ts.toLocalDateTime();
                boolean heeftBroerOfZus = rs.getBoolean("broer_zus");
                int voorkeur = rs.getInt("voorkeurschool");
                String afgewezenScholenCsv = rs.getString("afgewezen_scholen");
                String[] afgSchArray = afgewezenScholenCsv.split(";");
                ArrayList<School> als = new ArrayList<>();
                for(String str : afgSchArray)
                    if(!str.equals(""))
                        als.add(getSchool(Integer.parseInt(str)));
                aanvragenHashMap.put(aanvraagnummer,
                    new ToewijzingsAanvraag(aanvraagnummer,
                    rijksregisterNummerStudent,aanmeldingstijdstip, 
                    heeftBroerOfZus, status, voorkeur, als)
                );
            }
        } catch (NumberFormatException | SQLException e) {
            System.out.println("Error: " + e);
        }
        return aanvragenHashMap;
    }

    /*
     * Methode voor het ophalen van een wachtlijst van de School meegegeven
     * als argument 
     */
    public final void laadWachtLijst(School s) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM toewijzingsaanvragen "
                    + "WHERE voorkeurschool = " + s.getID());
            while (rs.next()) {
                int aanvraagnummer = rs.getInt("toewijzingsaanvraagnummer");
                Status status = Status.valueOf(rs.getString("status"));
                String rijksregisterNummerStudent
                        = rs.getString("student_rijksregisternummer");
                Timestamp ts = rs.getTimestamp("aanmeldingstijdstip");
                LocalDateTime aanmeldingstijdstip = ts.toLocalDateTime();
                boolean heeftBroerOfZus = rs.getBoolean("broer_zus");
                int voorkeur = rs.getInt("voorkeurschool");
                String afgewezenScholenCsv = rs.getString("afgewezen_scholen");
                String[] afgScholenArray = afgewezenScholenCsv.split(";");
                ArrayList<School> als = new ArrayList<>();
                for(String str : afgScholenArray) 
                    if(!str.equals(""))
                        als.add(scholen.get(Integer.parseInt(str)));
                s.getWachtLijst().add(new ToewijzingsAanvraag(aanvraagnummer,
                    rijksregisterNummerStudent,aanmeldingstijdstip, 
                    heeftBroerOfZus, status, voorkeur, als));
            }
        } catch (NumberFormatException | SQLException e) {
            System.out.println("Error: " + e);
        }
    }
    
    /*
     * Methode voor het controleren van de inloggegevens, 
     * retourneert -1 als de gegevens fout zijn, 0 als de ingelogde gebruiker
     * een ouder is, 1 als de ingelogde gebruiker een administrator is
     */
    public int inloggen(String gebrnaam, char[] wachtwoordCharArray) {
        String wachtwoord = "";
        int inlogStatus = -1;
        for (char c : wachtwoordCharArray) {
            wachtwoord += c;
        }
        for (Ouder o : ouders.values()) {
            if (o.getGebruikersnaam().equals(gebrnaam)
                    && o.getWachtwoord().equals(wachtwoord)) {
                ingelogdeOuder = o;
                inlogStatus = 0;
                break;
            }
        }
        if(gebrnaam.equals(ADMIN_ACCOUNT) && wachtwoord.equals(ADMIN_WACHTWOORD)) {
          ingelogdAlsAdmin = true;
          inlogStatus = 1;
        } 
        return inlogStatus;
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
            ingelogdeOuder = ouders.get(rnouder);
            ingelogdeOuder.setWachtwoord(wachtwoord);
            con = getConnection();
            bewaarOuder(getOuder(rnouder));
            con.close();
            String[] ontvangers = {ingelogdeOuder.getEmail()};
            Email email = new Email();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
            executor.execute(() -> {
              try {
                email.sendFromGMail(
                        "klantendienstsct@gmail.com", "centraletoewijzing", ontvangers,
                        "Inloggegevens voor de dienst centrale toewijzing",
                        "Beste " + ingelogdeOuder.getVoornaam() + ", \n"
                                + "\nJe kan vanaf nu inloggen op onze website met de volgende gegevens:\n"
                                + "\nGebruikersnaam: " + ingelogdeOuder.getGebruikersnaam()
                                + "\nWachtwoord: " + ingelogdeOuder.getWachtwoord() + "\n"
                                        + "\nMet vriendelijke groeten,\n"
                                        + "\nDienst Centrale Toewijzing"
                                        + "\nSecundair onderwijs");
              } catch (Exception e) {
                System.out.println("Error: " + e);
              }
            });
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
    public boolean indienenVoorkeur (int aanvraagnummer, String rnstudent, int schoolID) 
    throws ToewijzingException {
        ToewijzingsAanvraag ta = toewijzingsaanvragen.get(aanvraagnummer);
        if(schoolID == ta.getVoorkeur())
            throw new ToewijzingException("U heeft al voor deze school gekozen!");
        if(ta.getVoorkeur() != 0)
            scholen.get(ta.getVoorkeur()).getWachtLijst().remove(ta);
        ta.setVoorkeur(schoolID);
        ta.setStatus(Status.INGEDIEND);
        scholen.get(schoolID).getWachtLijst().add(ta);
        if (heeftBroerOfZus(rnstudent, schoolID)) {
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
    public School getSchool(int ID) {
        for (School s : scholen.values()) {
            if (s.getID() == ID) {
                return s;
            }
        }
        return null;
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
    public void bewaarOuder(Ouder o) {
        try {
            PreparedStatement ps
            = con.prepareStatement("UPDATE ouders SET "
            + "wachtwoord = '" + ingelogdeOuder.getWachtwoord() + "' "
            + "WHERE ouder_rijksregisternummer = '"
            + o.getRijksregisterNummerOuder() + "'");
            ps.execute();
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    public void bewaarScholen() {
        try {
            for(School s : scholen.values()) {
                PreparedStatement ps
                = con.prepareStatement("UPDATE scholen SET "
                + "wachtlijst = ? WHERE id = ?");
                ps.setString(1, s.csvFormatLijst());
                ps.setInt(2, s.getID());
                ps.execute();
            }
        } catch (SQLException e) {
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
                if (o.equals(ingelogdeOuder) || ingelogdAlsAdmin == true) {
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
                    DateTimeFormatter df
                        = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    ps.setString(4, df.format(ta.getAanmeldingsTijdstip()));
                    ps.setBoolean(5, ta.heeftHeeftBroerOfZus());
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
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }
    
    /*
     * Methode voor het overschrijven van de lokale gegevens naar
     * de databank
     */
    public void bewarenEnAfsluiten() throws Exception {
        con = getConnection();
        if(ingelogdeOuder != null)
          bewaarOuder(ingelogdeOuder);
        bewaarScholen();
        bewaarToewijzingsAanvragen();
        con.close();
    }

    /*
     * Methode voor het maken van verbinding met de databank 
     */
    public final Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection(
                      "jdbc:mysql://157.193.43.67:3306/BINFG22", "BINFG22",
                      "oKdxQaoh");
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Error: " + ex);
        }
        return con;
    }

    /*
     * Methode voor het afsluiten van de verbinding met de databank
     */
    public final void closeConnection() {
        try {
            con.close();
        } catch (SQLException ex) {
            System.out.println("Error: " + ex);
        }
    }
}
