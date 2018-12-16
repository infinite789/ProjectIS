import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Boris Dragnev, Victor Masscho, Jean Janssens, Edith Lust, Job van Lambalgen
 */
public class Ouder implements Serializable {
    private static final long serialVersionUID = 503;
    private final String rijksregisterNummer;
    private final String naam;
    private final String voornaam;
    private final String email;
    private final String straat;
    private final String gemeente;
    private final String gebruikersnaam;
    private String wachtwoord;
    
    public Ouder(String rijksregisterNummer, String naam, String voornaam, 
                 String email, String straat, String gemeente,
                 String gebruikersnaam, String wachtwoord) {
        this.rijksregisterNummer = rijksregisterNummer;
        this.naam = naam;
        this.voornaam = voornaam;
        this.email = email;
        this.straat = straat;
        this.gemeente = gemeente;
        this.gebruikersnaam = gebruikersnaam;
        this.wachtwoord = wachtwoord;
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

    public String getStraat() {
      return straat;
    }

    public String getGemeente() {
      return gemeente;
    }
    
    public String getGebruikersnaam() {
        return gebruikersnaam;
    }

    public String getWachtwoord() {
        return wachtwoord;
    }

    public void setWachtwoord(String wachtwoord) {
        this.wachtwoord = wachtwoord;
    }

    public String getEmail() {
        return email;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        Ouder o = (Ouder)obj;
        return (o.getNaam().equals(this.getNaam())
                && o.getVoornaam().equals(this.getVoornaam())
                && o.getEmail().equals(this.getEmail())
                && o.getStraat().equals(this.getStraat())
                && o.getGemeente().equals(this.getGemeente())
                && o.getRijksregisterNummer().equals(this.getRijksregisterNummer()));
    }
        
    @Override
    public int hashCode() {
        return Objects.hash(rijksregisterNummer,naam,voornaam,email,straat,gemeente,gebruikersnaam,wachtwoord);
    }
}
