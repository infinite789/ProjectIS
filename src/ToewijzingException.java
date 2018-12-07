

/**
 *
 * @author Boris Dragnev
 */
public class ToewijzingException extends Exception {
    public ToewijzingException(String boodschap) {
        super("Fout toewijzing: " + boodschap);
    }
    
}