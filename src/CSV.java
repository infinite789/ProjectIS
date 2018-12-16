
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
public class CSV {
  public static ArrayList<String> toList(String csv) {
    if(csv.equals("[]")) {
      return new ArrayList();
    } else {
      String[] strArray = csv.split(";");
      ArrayList<String> strList = new ArrayList();
      for(String str : strArray)
          if(!str.equals(""))
              strList.add(str);
      return strList;
    }
  }
}
