package morphos.test;

import morphos.api.interfaces.Table;
import spectra.interfaces.Band;
import spectra.interfaces.Ray;
import spectra.interfaces.SpectrumSpecification;

import static morphos.api.interfaces.Column.Type.*;
import static org.junit.jupiter.api.Assertions.*;

@SpectrumSpecification
public class DataTypesSpectrum extends MorphosSpectrum {

  @Ray
  public void givenTablesWhenGetFromCacheTableThenReturnDataModel() {
    assertDataTypesDemoTable(cache().getTable("data_types_demo").orElse(null));
  }

  private void assertDataTypesDemoTable(Table table) {
    assertTable("data_types_demo", table, 28, 1, 0);

    assertPrimaryKey(table, "id", BIGINT);

    assertColumn(table, "small_id",
      attributes -> {
        assertTrue(attributes.getUnique());
      }, SMALLINT);

    assertColumn(table, "regular_id", INTEGER);

    assertColumn(table, "notes",
      attributes -> {
        assertTrue(attributes.getNullable());
      }, NCHAR, CHAR);

    assertColumn(table, "code",
      attributes -> {
        assertTrue(attributes.getNullable());
      }, NVARCHAR, VARCHAR);

    assertColumn(table, "amount",
      column -> {
        assertType(column, NUMERIC, DECIMAL);
        assertEquals(12, column.getSize());
        assertEquals(2, column.getDecimalDigits());
      },
      attributes -> assertTrue(attributes.getNullable()));

    assertColumn(table, "price", DECIMAL, NUMERIC, DOUBLE);

    assertColumn(table, "ratio",REAL);

    assertColumn(table, "precision_value", DOUBLE);

    assertColumn(table, "active",
      attributes -> {
        assertTrue(attributes.getDefault().isPresent());
      }, BIT, BOOLEAN);

    assertColumn(table, "bitmask",
      attributes -> assertTrue(attributes.getNullable()),
      BIT, BINARY);

    assertColumn(table, "binary_data", BLOB, LONGVARBINARY, BINARY, VARBINARY);

    assertColumn(table, "created_at",
      attributes -> {
        assertTrue(attributes.getDefault().isPresent());
      }, TIMESTAMP);

    assertColumn(table, "updated_at",
      attributes -> assertTrue(attributes.getNullable()),
      TIMESTAMP_WITH_TIMEZONE, TIMESTAMP);

    assertColumn(table, "birth_date",
      attributes -> assertTrue(attributes.getNullable()),
      DATE);

    assertColumn(table, "wake_time",
      attributes -> assertTrue(attributes.getNullable()),
      TIME);

    assertColumn(table, "wake_time_tz",
      attributes -> assertTrue(attributes.getNullable()),
      TIME_WITH_TIMEZONE, TIME, TIMESTAMP);

    assertColumn(table, "interval_field",
      attributes -> assertTrue(attributes.getNullable()),
      INTERVAL, NVARCHAR, VARCHAR);

    assertColumn(table, "user_uuid",
      attributes -> assertFalse(attributes.getNullable()),
      UUID, CHAR);

    assertColumn(table, "metadata",
      attributes -> assertTrue(attributes.getNullable()),
      JSON, LONGVARCHAR, NVARCHAR);

    assertColumn(table, "preferences",
      attributes -> assertTrue(attributes.getNullable()),
      JSON, LONGVARCHAR, NVARCHAR);

    assertColumn(table, "tags",
      attributes -> assertTrue(attributes.getNullable()),
      ARRAY, LONGVARCHAR, NVARCHAR);

    assertColumn(table, "ip_address",
      attributes -> assertTrue(attributes.getNullable()),
      INET, NVARCHAR, VARCHAR);

    assertColumn(table, "network_range",
      attributes -> assertTrue(attributes.getNullable()),
      CIDR, NVARCHAR, VARCHAR);

    assertColumn(table, "mac_address",
      attributes -> assertTrue(attributes.getNullable()),
      MACADDR, NVARCHAR, VARCHAR);

    assertColumn(table, "xml_data",
      attributes -> assertTrue(attributes.getNullable()),
      XML, LONGVARCHAR, LONG_NVARCHAR);

    assertColumn(table, "status",
      attributes -> {
        assertTrue(attributes.getDefault().isPresent());
      }, ENUM, CHAR, NVARCHAR, VARCHAR);

    assertColumn(table, "score", INTEGER);

    assertColumn(table, "country_code",
      attributes -> {
        assertTrue(attributes.getDefault().isPresent());
      }, NCHAR, CHAR);
  }

  @Override
  protected Band band() {
    return Bands.DEMO;
  }
}
