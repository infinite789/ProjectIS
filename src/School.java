
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Boris Dragnev, Victor Masscho, Jean Janssens, Edith Lust, Job van Lambalgen
 */
public class School implements Serializable {
    private static final long serialVersionUID = 1032;
    private final int ID;
    private final String naam;
    private final String adres;
    private int plaatsen;
    private final String email;
    private ArrayList<ToewijzingsAanvraag> wachtLijst;
    
    public School(int ID, String naam, String adres,
                  int plaatsen, String email){
        this.ID = ID;
         this.naam = naam;
        this.adres = adres;
        this.plaatsen = plaatsen;
        this.email = email;
        this.wachtLijst = new ArrayList();
    }

    public int getID() {
        return ID;
    }

    public String getNaam() {
        return naam;
    }

    public String getAdres() {
        return adres;
    }

    public int getPlaatsen() {
        return plaatsen;
    }

    public void setPlaatsen(int plaatsen) {
        this.plaatsen = plaatsen;
    }

    public String getEmail() {
        return email;
    }
    
    public ArrayList<ToewijzingsAanvraag> getWachtLijst() {
        return this.wachtLijst;
    }
     
    public void setWachtLijst(ArrayList<ToewijzingsAanvraag> wachtLijst) {
        this.wachtLijst = wachtLijst;
    }
    
    public String csvFormatLijst() {
        String csvLijst = "";
        for(ToewijzingsAanvraag ta : wachtLijst) {
            csvLijst += ta.getToewijzingsAanvraagNummer() + ";";
        }
        if(csvLijst.equals(""))
            return csvLijst;
        else
            return csvLijst.substring(0, csvLijst.length()-1);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != this.getClass()) return false;
        School s = (School)obj;
        return s.getAdres().equals(this.adres) && s.getID() == this.ID
           && s.getNaam().equals(this.naam) && s.getPlaatsen() == this.plaatsen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialVersionUID, naam, adres, ID, plaatsen);
    }
    
    public String toString() {
        return this.getNaam() + "";
    }
}
