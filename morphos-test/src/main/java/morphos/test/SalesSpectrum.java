package morphos.test;

import morphos.api.interfaces.Column;
import morphos.api.interfaces.Table;
import spectra.interfaces.Band;
import spectra.interfaces.Ray;
import spectra.interfaces.SpectrumSpecification;

import static morphos.api.interfaces.Column.Type.*;
import static org.junit.jupiter.api.Assertions.*;

@SpectrumSpecification
public class SalesSpectrum extends MorphosSpectrum {

  @Ray
  void givenTablesWhenGetFromCacheTableThenReturnDataModel() {
    assertCustomersTable(cache().getTable("customers").orElse(null));
    assertProductsTable(cache().getTable("products").orElse(null));
    assertOrdersTable(cache().getTable("orders").orElse(null));
    assertOrderItemsTable(cache().getTable("order_items").orElse(null));
  }

  private void assertCustomersTable(Table table) {
    assertTable("customers", table, 4, 1, 0);

    assertPrimaryKey(table, "id", BIGINT);

    assertColumn(table, "full_name",
      attributes -> {
        assertFalse(attributes.getNullable());
      }, NVARCHAR, VARCHAR);

    assertColumn(table, "document_number",
      attributes -> {
        assertTrue(attributes.getNullable());
        assertTrue(attributes.getUnique());
      }, NVARCHAR, VARCHAR);

    assertColumn(table, "email",
      attributes -> {
        assertTrue(attributes.getNullable());
      }, NVARCHAR, VARCHAR);

    assertColumn(table, "registered_at",
      attributes -> {
        assertTrue(attributes.getDefault().isPresent());
      }, TIMESTAMP);
  }

  private void assertProductsTable(Table table) {
    assertTable("products", table, 4, 1, 0);

    assertPrimaryKey(table, "id", pk -> {
      assertTrue(pk.getGenerated());
    }, BIGINT);

    assertColumn(table, "name", attributes -> {
      assertFalse(attributes.getNullable());
    }, NVARCHAR, VARCHAR);

    assertColumn(table, "price", attributes -> {
      assertTrue(attributes.getNullable());
      assertTrue(attributes.getDefault().isPresent());
    }, DECIMAL, NUMERIC);

    assertColumn(table, "stock", attributes -> {
      assertTrue(attributes.getNullable());
      assertTrue(attributes.getDefault().isPresent());
    }, NUMERIC, INTEGER);

    assertColumn(table, "category", VARCHAR, NVARCHAR);
  }

  private void assertOrdersTable(Table table) {
    assertTable("orders", table, 3, 1, 1);

    assertPrimaryKey(table, "id", PK ->{
      assertTrue(PK.getGenerated());
    }, BIGINT);

    assertForeignKey(table, "customer_id", BIGINT);

    assertColumn(table, "created_at", attributes -> {
      assertTrue(attributes.getDefault().isPresent());
    }, TIMESTAMP);

    assertColumn(table, "status", attributes -> {
      assertTrue(attributes.getDefault().isPresent());
    }, NVARCHAR, VARCHAR);

    assertColumn(table, "total_amount", attributes -> {
      assertTrue(attributes.getNullable());
    }, NUMERIC, DECIMAL);
  }

  private void assertOrderItemsTable(Table table) {
    assertTable("order_items", table, 2, 2, 2);

    assertPrimaryKey(table, "order_id", BIGINT, INTEGER);
    assertForeignKey(table, "order_id", BIGINT, INTEGER);

    assertPrimaryKey(table, "product_id", BIGINT, INTEGER);
    assertForeignKey(table, "product_id", BIGINT, INTEGER);

    assertColumn(table, "quantity",
      attributes -> {
        assertTrue(attributes.getDefault().isPresent());
      }, INTEGER);

    assertColumn(table, "price",
      column -> {
        assertType(column, NUMERIC, DECIMAL);
        assertEquals(10, column.getSize());
        assertEquals(2, column.getDecimalDigits());
      }, attributes -> {
        assertFalse(attributes.getNullable());
      });
  }

  @Override
  protected Band band() {
    return Bands.SALES;
  }
}
