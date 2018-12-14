
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bddra
 
public class DataBestand {
  private static ObjectInputStream ois;
  
  public static void schrijfBestand(String path) {
    try {
      ois = new ObjectInputStream(new FileInputStream(path));
    } catch (Exception e) {
      e.printStackTrace();
      ois.close();
      throw new CorruptBestandException(e);
    }
  }
  
}
*/