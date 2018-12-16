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
    private final String rijksregisterNummerOuder;
    private final LocalDateTime aanmeldingsTijdstip;
    private int broersOfZussen;
    private Status status;
    private int voorkeur;
    private long preferentie;
    private ArrayList<String> afgewezenScholen;
    
    public ToewijzingsAanvraag(int nummer, String rnstudent, String rnouder) {
        this.toewijzingsAanvraagNummer = nummer;
        this.rijksregisterNummerStudent = rnstudent;
        this.rijksregisterNummerOuder = rnouder;
        this.aanmeldingsTijdstip = LocalDateTime.now();
        this.broersOfZussen = 0;
        this.status = Status.ONTWERP;
        this.voorkeur = 0;
        this.preferentie = 0;
        this.afgewezenScholen = new ArrayList<>();
    }
    
    public ToewijzingsAanvraag(int nummer, String rnstudent, String rnouder,
                                LocalDateTime tijdstip, int broersOfZussen, 
                                Status status, int voorkeur, long preferentie,
                                ArrayList<String> afgewezenScholen){
        this.toewijzingsAanvraagNummer = nummer;
        this.rijksregisterNummerStudent = rnstudent;
        this.rijksregisterNummerOuder = rnouder;
        this.aanmeldingsTijdstip = tijdstip;
        this.broersOfZussen = broersOfZussen;
        this.status = status;
        this.voorkeur = voorkeur;
        this.preferentie = preferentie;
        this.afgewezenScholen = afgewezenScholen;
    }

  public long getPreferentie() {
    return preferentie;
  }

  public void setPreferentie(long preferentie) {
    this.preferentie = preferentie;
  }

    public ArrayList<String> getAfgewezenScholen() {
        return afgewezenScholen;
    }

    public String csvFormatLijst() {
        String csvLijst = "";
        for(String str : afgewezenScholen) {
            csvLijst += str + ";";
        }
        if(csvLijst.equals(""))
            return csvLijst;
        else
            return csvLijst.substring(0, csvLijst.length()-1).replace("[()]", "");
    }
    
    public Integer getVoorkeur() {
        return voorkeur;
    }

    public void setVoorkeur(Integer voorkeur) {
        this.voorkeur = voorkeur;
    }

    public int getToewijzingsAanvraagNummer() {
        return toewijzingsAanvraagNummer;
    }

    public String getRijksregisterNummerStudent() {
        return rijksregisterNummerStudent;
    }

    public String getRijksregisterNummerOuder() {
      return rijksregisterNummerOuder;
    }
    public LocalDateTime getAanmeldingsTijdstip() {
        return aanmeldingsTijdstip;
    }

    public int getBroersOfZussen() {
        return broersOfZussen;
    }

    public void setBroersOfZussen(int broersOfZussen) {
        this.broersOfZussen = broersOfZussen;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
    public void incrementBroersOfZussen() {
	this.broersOfZussen++;
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
               && ta.getBroersOfZussen() == this.broersOfZussen;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(serialVersionUID, toewijzingsAanvraagNummer, 
                            rijksregisterNummerStudent, aanmeldingsTijdstip,
                            status, voorkeur, broersOfZussen);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.getToewijzingsAanvraagNummer());
    }
}
