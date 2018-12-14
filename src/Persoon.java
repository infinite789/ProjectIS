
import java.io.Serializable;
import java.util.Objects;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bddra
 */
public class Persoon implements Serializable {
  private final String rijksregisterNummer;
  private final String naam;
  private final String voornaam;

  public Persoon(String rijksregisternummer, String naam, String voornaam) {
    this.rijksregisterNummer = rijksregisternummer;
    this.naam = naam;
    this.voornaam = voornaam;
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

  @Override
  public int hashCode() {
    int hash = 7;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj.getClass() != this.getClass()) return false;
    Persoon p = (Persoon)obj;
    return(p.getNaam().equals(this.getNaam()) 
            && p.getVoornaam().equals(this.getVoornaam())
            && p.getRijksregisterNummer().equals(this.getRijksregisterNummer()));
  }
  
}
