/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Masscho Victor, aangevuld door Dragnev Boris
 */
public class Algoritme {
    private final DatabaseConnect dbconnect;
    private final HashMap<Integer, ToewijzingsAanvraag> toewijzingsaanvragen;
    private final HashMap<Integer, School> scholen;
    
    private final LocalDateTime START_DATUM = LocalDateTime.of(
            2018, Month.DECEMBER, 1, 0, 0, 0
    );//start inschrijvingen 
    private LocalDateTime huidigeDeadline = LocalDateTime.of(
            2018, Month.DECEMBER, 30, 0, 0, 0
    );//dynamische deadline, die na elke 'sorteerronde' een andere waarde aanneemt 
    
    public Algoritme() throws Exception {
        this.dbconnect = new DatabaseConnect(this);
        this.toewijzingsaanvragen = dbconnect.getToewijzingsAanvragen();
        this.scholen = dbconnect.getScholen();
        this.dbconnect.closeConnection();
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
        dbconnect.getConnection();
        ArrayList<ToewijzingsAanvraag> al = new ArrayList<>();
        al.addAll(dbconnect.getToewijzingsAanvragen().values());
        for (ToewijzingsAanvraag ta : al) {
            if (ta.getStatus().equals(Status.INGEDIEND)) {
                ta.setStatus(Status.VOORLOPIG);
            }
        }
        dbconnect.bewaarToewijzingsAanvragen(al);
        dbconnect.closeConnection();
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
}
