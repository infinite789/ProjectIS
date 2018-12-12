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

/**
 *
 * @author Masscho Victor, aangevuld door Dragnev Boris
 */
public class Algoritme extends DatabaseConnect {

    private final LocalDateTime START_DATUM = LocalDateTime.of(
            2018, Month.DECEMBER, 1, 0, 0, 0
    );//start inschrijvingen 
    private LocalDateTime huidigeDeadline = LocalDateTime.of(
            2018, Month.DECEMBER, 30, 0, 0, 0
    );//dynamische deadline, die na elke 'sorteerronde' een andere waarde aanneemt 

    public Algoritme() throws Exception {
        super();
    }

    public void exporteerWachtLijsten() throws ToewijzingException {
        for (School s : getScholen().values()) {
            String bestandPath = "./lijsten/s" + s.getNaam().substring(0, 5)
                    .replaceAll(" ", "").toLowerCase() + s.getID();
            ObjectOutputStream output;
            try {
                output = new ObjectOutputStream(new FileOutputStream(
                        bestandPath
                ));
                output.writeObject(s.getWachtLijst());
                output.close();
            } catch (IOException e) {
                throw new ToewijzingException("Fout bij exporteren: " + e);
            }
        }
    }

    public ArrayList<ToewijzingsAanvraag> getWachtLijst(int schoolID) {
        ArrayList<ToewijzingsAanvraag> wachtLijst = new ArrayList<>();
        for (ToewijzingsAanvraag ta : getToewijzingsaanvragen().values()) {
            if (ta.getVoorkeur() == schoolID) {
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
            wachtLijst = (ArrayList) input.readObject();
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

    public ArrayList<ToewijzingsAanvraag> laadAfgewezenStudenten() throws Exception {
        ArrayList<ToewijzingsAanvraag> afgewezenStudenten;
        File afgStBestand = new File("./lijsten/afgewezenStudenten");
        if (afgStBestand.exists()) {
            ObjectInputStream input = new ObjectInputStream(
                    new FileInputStream(
                            "./lijsten/afgewezenStudenten"
                    ));
            afgewezenStudenten = (ArrayList) input.readObject();
            input.close();
        } else {
            afgewezenStudenten = new ArrayList<>();
        }
        return afgewezenStudenten;
    }

    public void sorteerAlgoritme() throws Exception {
      int afgewezenStudenten = 0;
        for (School s : getScholen().values()) {
          String bestandPath = "./lijsten/s" + s.getNaam().substring(0, 5)
                    .replaceAll(" ", "").toLowerCase() + s.getID();
          ArrayList<ToewijzingsAanvraag> wachtLijst = s.getWachtLijst();
          sorteerWachtLijst(wachtLijst);
          //studenten afwijzen indien de school vol zit
          while (wachtLijst.size() > s.getPlaatsen()) {
              ToewijzingsAanvraag ta = wachtLijst.get(wachtLijst.size() - 1);
              wachtLijst.remove(ta);
              getAanvraag(String.valueOf(ta.getToewijzingsAanvraagNummer())).setStatus(Status.ONTWERP);
              ta.getAfgewezenScholen().add(s);
              afgewezenStudenten++;
          }
          //wachtlijst van school schrijven naar bestand
          wachtLijstOpslaan(wachtLijst, bestandPath);
        }
        if(afgewezenStudenten > 0)
          updateStatus(Status.VOORLOPIG);
        else
          updateStatus(Status.DEFINITIEF);
    }

    public long getPreferentie(ToewijzingsAanvraag ta) {
        long preferentie = huidigeDeadline.toEpochSecond(ZoneOffset.UTC)
                - ta.getAanmeldingsTijdstip().toEpochSecond(ZoneOffset.UTC);
        long bonusPunten = huidigeDeadline.toEpochSecond(ZoneOffset.UTC)
                - START_DATUM.toEpochSecond(ZoneOffset.UTC);
        if (ta.heeftHeeftBroerOfZus()) {
            preferentie += bonusPunten;
        }
        return preferentie;

    }

    public void updateStatus(Status s) {
        getConnection();
        for (ToewijzingsAanvraag ta : getToewijzingsaanvragen().values()) {
            if (ta.getStatus().equals(Status.INGEDIEND) && s.equals(Status.VOORLOPIG)) {
              ta.setStatus(s);
            } else if (!ta.getStatus().equals(Status.ONTWERP) && s.equals(Status.DEFINITIEF)){
              ta.setStatus(s);
            }
        }
        bewaarToewijzingsAanvragen();
        closeConnection();
    }

    public boolean schoolIsAfgewezen(int schoolID, int aanvraagnummer) {
        try {
            ArrayList<ToewijzingsAanvraag> afgewezenStudenten
                    = laadAfgewezenStudenten();
            for (ToewijzingsAanvraag ta : afgewezenStudenten) {
                if (ta.getVoorkeur() == schoolID
                        && ta.getToewijzingsAanvraagNummer() == aanvraagnummer) {
                    return true;
                }
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
