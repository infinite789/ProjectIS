

/**
 *
 * @author Boris Dragnev, Victor Masscho, Jean Janssens, Edith Lust, Job van Lambalgen
 */
public class ToewijzingException extends Exception {
    public ToewijzingException(String boodschap) {
        super("Fout toewijzing: " + boodschap);
    }
    
}