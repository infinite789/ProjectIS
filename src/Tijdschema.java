
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
public class Tijdschema {
  private Year jaar;
  private LocalDateTime startDatum;
  private LocalDateTime inschrijvingenDeadline;
  private LocalDateTime capaciteitDeadline;
  private LocalDateTime voorkeurDeadline;
  private LocalDateTime datum;
  
  public void veranderTijd(){
      try {
          DBTijdschema.setDatum(jaar,startDatum,inschrijvingenDeadline, capaciteitDeadline, voorkeurDeadline, datum);
      } catch (DBException ex) {
          ex.getMessage();
      }
  }
  
}
