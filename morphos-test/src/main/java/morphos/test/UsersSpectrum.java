package morphos.test;

import morphos.api.interfaces.Table;
import spectra.interfaces.Band;
import spectra.interfaces.Ray;
import spectra.interfaces.SpectrumSpecification;

import static morphos.api.interfaces.Column.Type.*;
import static org.junit.jupiter.api.Assertions.*;

@SpectrumSpecification
public class UsersSpectrum extends MorphosSpectrum {

  @Ray
  void givenTablesWhenGetFromCacheTableThenReturnDataModel() {
    assertUsersTable(cache().getTable("users").orElse(null));
    assertRolesTable(cache().getTable("roles").orElse(null));
    assertUserRolesTable(cache().getTable("user_roles").orElse(null));
  }

  private void assertUsersTable(Table table) {
    assertTable("users", table, 5, 1, 0);

    assertPrimaryKey(table, "id", UUID, CHAR);

    // username (VARCHAR 50 NOT NULL UNIQUE, CHECK)
    assertColumn(table, "username",
      attributes -> {
        assertFalse(attributes.getNullable());
        assertTrue(attributes.getUnique());
      }, VARCHAR, NVARCHAR);

    // email (domain core.email → VARCHAR(255))
    assertColumn(table, "email",
      attributes -> {
        assertFalse(attributes.getNullable());
      }, VARCHAR, NVARCHAR);

    // created_at (timestamp)
    assertColumn(table, "created_at",
      attributes -> {
        assertTrue(attributes.getDefault().isPresent());
      }, TIMESTAMP, TIMESTAMP_WITH_TIMEZONE);

    // updated_at (timestamp nullable)
    assertColumn(table, "updated_at",
      attributes -> {
        assertTrue(attributes.getNullable());
      }, TIMESTAMP, TIMESTAMP_WITH_TIMEZONE);

    // active (boolean default true)
    assertColumn(table, "active",
      attributes -> {
        assertTrue(attributes.getDefault().isPresent());
        assertNotNull(attributes.getDefault().get());
      }, BOOLEAN, BIT);
  }

  private void assertRolesTable(Table table) {
    assertTable("roles", table, 2, 1, 0);

    assertPrimaryKey(table, "id", BIGINT);

    assertColumn(table, "name",
      attributes -> {
        assertFalse(attributes.getNullable());
        assertTrue(attributes.getUnique());
      }, VARCHAR, NVARCHAR);

    assertColumn(table, "description",
      attributes -> {
        assertTrue(attributes.getNullable());
      }, VARCHAR, NVARCHAR, LONGVARCHAR);
  }

  private void assertUserRolesTable(Table table) {
    assertTable("user_roles", table, 1, 2, 2);

    assertPrimaryKey(table, "role_id", BIGINT);
    assertPrimaryKey(table, "user_id", UUID, CHAR);

    assertForeignKey(table, "role_id", BIGINT);
    assertForeignKey(table, "user_id", UUID, CHAR);

    assertColumn(table, "assigned_at",
      attributes -> {
        assertTrue(attributes.getDefault().isPresent());
      }, TIMESTAMP_WITH_TIMEZONE, TIMESTAMP);
  }

  @Override
  protected Band band() {
    return Bands.USERS;
  }
}