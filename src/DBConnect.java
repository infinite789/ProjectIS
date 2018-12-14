
import java.sql.*;

/*
 * Klasse voor bewerking met de gegevens afkomstig uit de databank
 *
 * @author Boris Dragnev, Victor Masscho, Jean Janssens, Edith Lust, Job van Lambalgen
 */
public class DBConnect  {
    
    private static final String DB_NAME = "BINFG22";//vul hier uw databank naam in
    private static final String DB_PASS = "oKdxQaoh";//vul hier uw databank paswoord in
    
    /*
     * Methode voor het maken van een verbinding met de databank
     */
    public static Connection getConnection() throws DBException {
      Connection con = null;
      try {
          Class.forName("com.mysql.jdbc.Driver");
          con = (Connection)DriverManager.getConnection(
                    "jdbc:mysql://mysqlha2.ugent.be:3306/" + DB_NAME, 
                    DB_NAME, DB_PASS);
      } catch (ClassNotFoundException | SQLException ex) {
          ex.printStackTrace();
          closeConnection(con);
          throw new DBException(ex);
      } catch (Exception ex) {
          ex.printStackTrace();
          closeConnection(con);
          throw new DBException(ex);
      }
      return con;
    }
    
    /*
     * Methode voor het afsluiten van de verbinding met de databank
     */
    public static void closeConnection(Connection con) {
      try {
        if(con != null)
          con.close();
      } catch (SQLException ex) {
          System.out.println("Error: " + ex);
      }
    }
}
