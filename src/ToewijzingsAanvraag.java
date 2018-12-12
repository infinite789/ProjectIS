import java.io.Serializable;
import java.time.LocalDateTime;
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
public class ToewijzingsAanvraag implements Serializable {
    private static final long serialVersionUID = 8420;
    private final int toewijzingsAanvraagNummer;
    private final String rijksregisterNummerStudent;
    private final LocalDateTime aanmeldingsTijdstip;
    private boolean heeftBroerOfZus;
    private Status status;
    private Integer voorkeur;
    private final ArrayList<School> afgewezenScholen;
    
    public ToewijzingsAanvraag(int nummer, String rnstudent) {
        this.toewijzingsAanvraagNummer = nummer;
        this.rijksregisterNummerStudent = rnstudent;
        this.aanmeldingsTijdstip = LocalDateTime.now();
        this.heeftBroerOfZus = false;
        this.status = Status.ONTWERP;
        this.voorkeur = 0;
        this.afgewezenScholen = new ArrayList<>();
    }
    
    public ToewijzingsAanvraag(int nummer, String rnstudent,
                                LocalDateTime tijdstip,
                                boolean heeftBroerOfZus, Status status, 
                                Integer voorkeur, ArrayList<School> afgewezenScholen){
        this.toewijzingsAanvraagNummer = nummer;
        this.rijksregisterNummerStudent = rnstudent;
        this.aanmeldingsTijdstip = tijdstip;
        this.heeftBroerOfZus = heeftBroerOfZus;
        this.status = status;
        this.voorkeur = voorkeur;
        this.afgewezenScholen = afgewezenScholen;
    }

    public ArrayList<School> getAfgewezenScholen() {
        return afgewezenScholen;
    }

    public String csvFormatLijst() {
        String csvLijst = "";
        for(School s : afgewezenScholen) {
            csvLijst += s.getID()+ ";";
        }
        if(csvLijst.equals(""))
            return csvLijst;
        else
            return csvLijst.substring(0, csvLijst.length()-1);
    }
    
    public int getVoorkeur() {
        return voorkeur;
    }

    public void setVoorkeur(int voorkeur) {
        this.voorkeur = voorkeur;
    }

    public int getToewijzingsAanvraagNummer() {
        return toewijzingsAanvraagNummer;
    }

    public String getRijksregisterNummerStudent() {
        return rijksregisterNummerStudent;
    }

    public LocalDateTime getAanmeldingsTijdstip() {
        return aanmeldingsTijdstip;
    }

    public boolean heeftHeeftBroerOfZus() {
        return heeftBroerOfZus;
    }

    public void setHeeftBroerOfZus(boolean heeftBroerOfZus) {
        this.heeftBroerOfZus = heeftBroerOfZus;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj.getClass() != this.getClass()) return false;
        ToewijzingsAanvraag ta = (ToewijzingsAanvraag)obj;
        return ta.getAanmeldingsTijdstip().equals(this.aanmeldingsTijdstip)
               && ta.getRijksregisterNummerStudent().equals(this.rijksregisterNummerStudent)
               && ta.getStatus().equals(this.getStatus())
               && ta.getToewijzingsAanvraagNummer() == this.toewijzingsAanvraagNummer
               && ta.getVoorkeur() == this.voorkeur
               && ta.heeftBroerOfZus == this.heeftBroerOfZus;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(serialVersionUID, toewijzingsAanvraagNummer, 
                            rijksregisterNummerStudent, aanmeldingsTijdstip,
                            status, voorkeur, heeftBroerOfZus);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.getToewijzingsAanvraagNummer());
    }
}
