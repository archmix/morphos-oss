package morphosReflectorTest;

import legolas.mysql.interfaces.MySQLEntry;
import legolas.mysql.interfaces.MySQLServiceId;
import legolas.runtime.core.interfaces.RunningEnvironment;
import morphos.test.ConnectionProperties;
import morphos.test.MorphosBaseTest;

public class MorphosReflectorMySQLTest extends MorphosBaseTest {

  @Override
  protected ConnectionProperties getConnectionProperties(RunningEnvironment environment) {
    var config = environment.get(MySQLServiceId.INSTANCE).get().configuration();

    var url = config.getString(MySQLEntry.URL).get();
    var driver = config.getString(MySQLEntry.DRIVER).get();
    var username = config.getString(MySQLEntry.USERNAME).get();
    var password = config.getString(MySQLEntry.PASSWORD).get();

    return new ConnectionProperties(url, driver, username, password);
  }
}
