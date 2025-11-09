package morphosReflectorTest;

import legolas.postgre.interfaces.PostgreSQLEntry;
import legolas.postgre.interfaces.PostgreSQLServiceId;
import legolas.runtime.core.interfaces.RunningEnvironment;
import morphos.test.ConnectionProperties;
import morphos.test.MorphosBaseTest;

public class MorphosReflectorPostgresTest extends MorphosBaseTest {

  @Override
  protected ConnectionProperties getConnectionProperties(RunningEnvironment environment) {
    var config = environment.get(PostgreSQLServiceId.INSTANCE).get().configuration();

    var url = config.getString(PostgreSQLEntry.URL).get();
    var driver = config.getString(PostgreSQLEntry.DRIVER).get();
    var username = config.getString(PostgreSQLEntry.USERNAME).get();
    var password = config.getString(PostgreSQLEntry.PASSWORD).get();

    return new ConnectionProperties(url, driver, username, password);
  }
}
