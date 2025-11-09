package morphosReflectorTest;

import legolas.runtime.core.interfaces.RunningEnvironment;
import legolas.sqlserver.interfaces.SQLServerEntry;
import legolas.sqlserver.interfaces.SQLServerServiceId;
import morphos.test.ConnectionProperties;
import morphos.test.MorphosBaseTest;

public class MorphosReflectorSQLServerTest extends MorphosBaseTest {

  @Override
  protected ConnectionProperties getConnectionProperties(RunningEnvironment environment) {
    var config = environment.get(SQLServerServiceId.INSTANCE).get().configuration();

    var url = config.getString(SQLServerEntry.URL).get();
    var driver = config.getString(SQLServerEntry.DRIVER).get();
    var username = config.getString(SQLServerEntry.USERNAME).get();
    var password = config.getString(SQLServerEntry.PASSWORD).get();

    return new ConnectionProperties(url, driver, username, password);
  }
}
