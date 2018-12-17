
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.mail.internet.ParseException;
import javax.swing.JFormattedTextField.AbstractFormatter;

public class DateLabelFormatter extends AbstractFormatter {

    private String datePattern = "yyyy-MM-dd";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws java.text.ParseException  {
      Object o = dateFormatter.parseObject(text);
      return o;
    }

    @Override
    public String valueToString(Object value){
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }

        return "";
    }

}