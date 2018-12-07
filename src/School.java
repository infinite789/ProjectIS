
import java.io.Serializable;
import java.util.Objects;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Masscho Victor, Dragnev Boris
 */
public class School implements Serializable {
    private static final long serialVersionUID = 1032;
    private final int ID;
    private final String naam;
    private final String adres;
    private int plaatsen;
    
    public School(int ID, String naam, String adres, int plaatsen){
        this.ID = ID;
         this.naam = naam;
        this.adres = adres;
        this.plaatsen = plaatsen;
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
}
