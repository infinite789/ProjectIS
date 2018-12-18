
import java.time.LocalDateTime;
import java.time.Year;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bddra
 */
public class TijdSchema {
  private Year jaar;
  private LocalDateTime startDatum;
  private LocalDateTime inschrijvingenDeadline;
  private LocalDateTime capaciteitDeadline;
  private LocalDateTime huidigDeadline;
  private LocalDateTime eindDatum;
  
  public TijdSchema(Year jaar, LocalDateTime startDatum, LocalDateTime inschrijvingenDL, 
                    LocalDateTime capaciteitDL, LocalDateTime huidigDeadline, LocalDateTime eindDatum) {
    this.jaar = jaar;
    this.startDatum = startDatum;
    this.inschrijvingenDeadline = inschrijvingenDL;
    this.capaciteitDeadline = capaciteitDL;
    this.huidigDeadline = huidigDeadline;
    this.eindDatum = eindDatum;
  }

  public Year getJaar() {
    return jaar;
  }

  public LocalDateTime getStartDatum() {
    return startDatum;
  }

  public void setStartDatum(LocalDateTime startDatum) {
    this.startDatum = startDatum;
  }

  public LocalDateTime getInschrijvingenDeadline() {
    return inschrijvingenDeadline;
  }

  public void setInschrijvingenDeadline(LocalDateTime inschrijvingenDeadline) {
    this.inschrijvingenDeadline = inschrijvingenDeadline;
  }

  public LocalDateTime getCapaciteitDeadline() {
    return capaciteitDeadline;
  }

  public void setCapaciteitDeadline(LocalDateTime capaciteitDeadline) {
    this.capaciteitDeadline = capaciteitDeadline;
  }

  public LocalDateTime getHuidigDeadline() {
    return huidigDeadline;
  }

  public void setHuidigDeadline(LocalDateTime huidigDeadline) {
    this.huidigDeadline = huidigDeadline;
  }
  
  public LocalDateTime getEindDatum() {
    return eindDatum;
  }

  public void setEindDatum(LocalDateTime eindDatum) {
    this.eindDatum = eindDatum;
  }
}
