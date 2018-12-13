
import java.io.Serializable;
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
public class Student implements Serializable {
   private final String rijksregisterNummerStudent;
   private final String rijksregisterNummerOuder;
   private final String naam;
   private final String voornaam; 
   private final String telefoonnummer;
   private final int huidigeSchool;
   
    public Student(String rijksregisterNummerStudent, 
                   String rijksregisterNummerOuder, String naam, 
                   String voornaam, String telefoonnummer, int huidigeSchool) {
        this.rijksregisterNummerStudent = rijksregisterNummerStudent;
        this.rijksregisterNummerOuder = rijksregisterNummerOuder;
        this.naam = naam;
        this.voornaam = voornaam;
        this.telefoonnummer = telefoonnummer;
        this.huidigeSchool = huidigeSchool;
    }

    public int getHuidigeSchool() {
        return huidigeSchool;
    }

    public String getRijksregisterNummerOuder() {
        return rijksregisterNummerOuder;
    }

    public String getRijksregisterNummerStudent() {
        return rijksregisterNummerStudent;
    }

    public String getNaam() {
        return naam;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public String getTelefoonnummer() {
        return telefoonnummer;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != this.getClass()) return false;
        Student s = (Student)obj;
        return s.getVoornaam().equals(this.getVoornaam()) 
        && s.getHuidigeSchool() == (this.huidigeSchool)
        && s.getNaam().equals(this.naam) 
        && s.getTelefoonnummer().equals(this.telefoonnummer)
        && s.getRijksregisterNummerOuder().equals(this.rijksregisterNummerOuder)
        && s.getRijksregisterNummerStudent().equals(rijksregisterNummerStudent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(huidigeSchool, naam, voornaam, telefoonnummer, 
                            rijksregisterNummerOuder, rijksregisterNummerStudent);
                            
    }
}
