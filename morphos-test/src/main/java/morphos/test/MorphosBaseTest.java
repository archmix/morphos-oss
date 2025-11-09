package morphos.test;

import legolas.runtime.core.interfaces.RunningEnvironment;
import morphos.api.interfaces.MorphosReflector;
import morphos.api.interfaces.Schema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import legolas.provided.infra.LegolasExtension;
import spectra.interfaces.Band;
import spectra.interfaces.Spectra;

import java.sql.SQLException;
import java.util.Collection;

@ExtendWith(LegolasExtension.class)
public abstract class MorphosBaseTest {
  public static final String MORPHOS_CACHE_CONTEXT_KEY = "morphos-cache";

  @TestFactory
  Collection<DynamicTest> testUsersSchema(RunningEnvironment environment) throws SQLException {
    return testSchema(environment, Bands.USERS);
  }

  @TestFactory
  Collection<DynamicTest> testSalesSchema(RunningEnvironment environment) throws SQLException {
    return testSchema(environment, Bands.SALES);
  }

  @TestFactory
  Collection<DynamicTest> testAuditSchema(RunningEnvironment environment) throws SQLException {
    return testSchema(environment, Bands.AUDIT);
  }

  @TestFactory
  Collection<DynamicTest> testDemoSchema(RunningEnvironment environment) throws SQLException {
    return testSchema(environment, Bands.DEMO);
  }

  Collection<DynamicTest> testSchema(RunningEnvironment environment, Band band) throws SQLException {
    try (var connection = this.getConnectionProperties(environment).openConnection()) {
      var cache = MorphosReflector.reflect(connection, Schema.defaultSchema());
      return Spectra.of().context(ctx -> {
        ctx.put(MORPHOS_CACHE_CONTEXT_KEY, cache);
      }).reveal(band);
    }
  }

  protected abstract ConnectionProperties getConnectionProperties(RunningEnvironment environment);
}
