
import java.util.Objects;

/**
 *
 * @author Boris Dragnev, Victor Masscho, Jean Janssens, Edith Lust, Job van Lambalgen
 */
public class Student extends Persoon {
   private final String rijksregisterNummerOuder;
   private final String telefoonnummer;
   private final int huidigeSchool;
   
    public Student(String rijksregisterNummer, 
                   String rijksregisterNummerOuder, String naam, 
                   String voornaam, String telefoonnummer, int huidigeSchool) {
      super(rijksregisterNummer, naam, voornaam);
      this.rijksregisterNummerOuder = rijksregisterNummerOuder;
      this.telefoonnummer = telefoonnummer;
      this.huidigeSchool = huidigeSchool;
    }

    public int getHuidigeSchool() {
        return huidigeSchool;
    }

    public String getRijksregisterNummerOuder() {
        return rijksregisterNummerOuder;
    }

    public String getTelefoonnummer() {
        return telefoonnummer;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != this.getClass()) return false;
        Student s = (Student)obj;
        return  s.getHuidigeSchool() == (this.huidigeSchool)
        && s.getTelefoonnummer().equals(this.telefoonnummer)
        && s.getRijksregisterNummer().equals(this.getRijksregisterNummer());
    }

    @Override
    public int hashCode() {
        return super.hashCode()+Objects.hash(huidigeSchool,telefoonnummer,rijksregisterNummerOuder);
                            
    }
}
