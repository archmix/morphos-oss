package morphos.test;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionProperties {
  private final String url;
  private final String driver;
  private final String username;
  private final String password;

  public ConnectionProperties(String url, String driver, String username, String password) {
    this.url = url;
    this.driver = driver;
    this.username = username;
    this.password = password;
  }

  protected Connection openConnection() {
    try {
      Class.forName(this.driver);
      return DriverManager.getConnection(this.url, this.username, this.password);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
