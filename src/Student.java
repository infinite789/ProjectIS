
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Boris Dragnev, Victor Masscho, Jean Janssens, Edith Lust, Job van Lambalgen
 */
public class Student implements Serializable {
  private static final long serialVersionUID = 7004;
  private final String rijksregisterNummer;
  private final String rijksregisterNummerOuder;
  private final String naam;
  private final String voornaam;
  private final String telefoonnummer;
  private final int huidigeSchool;
   
    public Student(String rijksregisterNummer, 
                   String rijksregisterNummerOuder, String naam, 
                   String voornaam, String telefoonnummer, int huidigeSchool) {
      this.rijksregisterNummer = rijksregisterNummer;
      this.rijksregisterNummerOuder = rijksregisterNummerOuder;
      this.naam = naam;
      this.voornaam = voornaam;
      this.telefoonnummer = telefoonnummer;
      this.huidigeSchool = huidigeSchool;
    }

    public String getRijksregisterNummer() {
      return rijksregisterNummer;
    }

    public String getNaam() {
      return naam;
    }

    public String getVoornaam() {
      return voornaam;
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
        && s.getRijksregisterNummer().equals(this.getRijksregisterNummer())
        && s.getRijksregisterNummerOuder().equals(this.getRijksregisterNummerOuder())
        && s.getNaam().equals(this.getNaam())
        && s.getVoornaam().equals(this.getVoornaam());
        
    }

    @Override
    public int hashCode() {
        return Objects.hash(rijksregisterNummer,naam,voornaam,huidigeSchool,telefoonnummer,rijksregisterNummerOuder);
                            
    }
}
