package morphos.test;

import morphos.api.interfaces.Table;
import spectra.interfaces.Band;
import spectra.interfaces.Ray;
import spectra.interfaces.SpectrumSpecification;

import static morphos.api.interfaces.Column.Type.*;
import static org.junit.jupiter.api.Assertions.*;

@SpectrumSpecification
public class AuditSpectrum extends MorphosSpectrum {

  @Ray
  public void givenTablesWhenGetFromCacheTableThenReturnDataModel() {
    assertChangeLogTable(cache().getTable("change_log").orElse(null));
  }

  private void assertChangeLogTable(Table table) {
    assertTable("change_log", table, 5, 1, 0);

    assertPrimaryKey(table, "id", BIGINT);

    assertColumn(table, "table_name",
      attributes -> {
        assertFalse(attributes.getNullable());
      }, VARCHAR, NVARCHAR);

    assertColumn(table, "record_id",
      attributes -> {
        assertFalse(attributes.getNullable());
      }, BIGINT, VARCHAR, NVARCHAR);

    assertColumn(table, "operation",
      attributes -> {
        assertTrue(attributes.getNullable());
      }, CHAR, VARCHAR, NVARCHAR);

    assertColumn(table, "changed_at",
      attributes -> {
        assertTrue(attributes.getDefault().isPresent());
      }, TIMESTAMP);

    assertColumn(table, "changed_by",
      attributes -> {
        assertTrue(attributes.getNullable());
      }, VARCHAR, NVARCHAR);
  }

  @Override
  protected Band band() {
    return Bands.AUDIT;
  }
}