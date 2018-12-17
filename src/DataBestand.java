
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bddra
 */
public class DataBestand {
  public static void opslaanWachtLijst (School s, ArrayList<ToewijzingsAanvraag> wachtLijst) 
          throws DBException {
            ObjectOutputStream ois = null;
            try {
              String path = ".lijsten/school" + s.getID();
              File file = new File(".lijsten/school" + s.getID());
              if(!file.exists())
                file.createNewFile();
              ois = new ObjectOutputStream(new FileOutputStream(path));
              ois.writeObject(wachtLijst);
              ois.close();
            } catch (Exception e) {
              e.printStackTrace();
              throw new DBException(e);
            }
  }
}
