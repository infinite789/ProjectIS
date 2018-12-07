import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Masscho Victor, Boris Dragnev
 */
public class Ouder implements Serializable {
    private final static long serialVersionUID = 453;
    private final String rijksregisterNummerOuder;
    private final String naam;
    private final String voornaam;
    private final String email;
    private final String adres;
    private final String gebruikersnaam;
    private String wachtwoord;
    
    public Ouder(String rijksregisterNummerOuder, String naam,
                 String voornaam, String email, String adres,
                 String gebruikersnaam, String wachtwoord) {
        this.rijksregisterNummerOuder = rijksregisterNummerOuder;
        this.naam = naam;
        this.voornaam = voornaam;
        this.email = email;
        this.adres = adres;
        this.gebruikersnaam = gebruikersnaam;
        this.wachtwoord = wachtwoord;
    }
    
    public String getGebruikersnaam() {
        return gebruikersnaam;
    }

    public String getWachtwoord() {
        return wachtwoord;
    }

    public String getRijksregisterNummerOuder() {
        return rijksregisterNummerOuder;
    }

    public void setWachtwoord(String wachtwoord) {
        this.wachtwoord = wachtwoord;
    }

    public String getNaam() {
        return naam;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public String getEmail() {
        return email;
    }

    public String getAdres() {
        return adres;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        Ouder o = (Ouder)obj;
        return(o.getNaam().equals(this.getNaam()) 
                && o.getVoornaam().equals(this.getVoornaam())
                && o.getEmail().equals(this.getEmail())
                && o.getAdres().equals(this.getAdres())
                && o.getRijksregisterNummerOuder()
                   .equals(this.getRijksregisterNummerOuder()));
    }
        
    @Override
    public int hashCode() {
        return Objects.hash(serialVersionUID, rijksregisterNummerOuder, naam,
                            voornaam, email, adres, gebruikersnaam, wachtwoord);
    }
}
