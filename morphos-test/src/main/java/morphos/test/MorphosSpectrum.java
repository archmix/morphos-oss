package morphos.test;

import lombok.extern.slf4j.Slf4j;
import morphos.api.interfaces.Column;
import morphos.api.interfaces.Attributes;
import morphos.api.interfaces.MorphosCache;
import morphos.api.interfaces.PrimaryKey;
import morphos.api.interfaces.Table;
import spectra.interfaces.Spectrum;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public abstract class MorphosSpectrum extends Spectrum {
  protected MorphosCache cache() {
    return context(MorphosBaseTest.MORPHOS_CACHE_CONTEXT_KEY, MorphosCache.class);
  }

  protected void assertTable(String tableName, Table table, int numberOfColumns, int numberOfPrimaryKeys,
                             int numberOfForeignKeys) {
    assertNotNull(table);
    assertEquals(tableName, table.getName());
    assertEquals(numberOfColumns, table.getColumns().size());
    assertEquals(numberOfPrimaryKeys, table.getPrimaryKeys().size());
    assertEquals(numberOfForeignKeys, table.getForeignKeys().size());
  }

  protected void assertColumn(Table table, String name, Column.Type... types) {
    assertColumn(table, name,
      column -> {
        assertType(column, types);
      }, attributes -> {});
  }

  protected void assertColumn(Table table, String name, Consumer<Attributes> attributes,
                              Column.Type... types) {
    assertColumn(table, name,
      column -> {
        assertType(column, types);
      }, attributes);
  }

  protected void assertColumn(Table table, String name, Consumer<Column> consumer) {
    assertColumn(table, name, consumer, attributes -> {});
  }

  protected void assertColumn(Table table, String name, Consumer<Column> consumer, Consumer<Attributes> attributes) {
    var column = table.getColumn(name).orElse(null);
    assertNotNull(column);
    assertEquals(name, column.getName());
    consumer.accept(column);
    attributes.accept(column.getAttributes());
  }

  protected void assertPrimaryKey(Table table, String name, Column.Type... types) {
    assertPrimaryKey(table, name, primaryKey -> {}, types);
  }

  protected void assertPrimaryKey(Table table, String name, Consumer<PrimaryKey> pkConsumer, Column.Type... types) {
    var primaryKey = table.getPrimaryKey(name).orElse(null);

    assertNotNull(primaryKey);
    assertEquals(name, primaryKey.getName());

    var column = primaryKey.getColumn();
    assertNotNull(column);
    assertEquals(name, column.getName());
    assertType(column, types);

    var attributes = column.getAttributes();
    assertFalse(attributes.getNullable());
    assertTrue(attributes.getUnique());

    pkConsumer.accept(primaryKey);
  }

  protected void assertForeignKey(Table table, String name, Column.Type... types) {
    var foreignKey = table.getForeignKey(name).orElse(null);
    assertNotNull(foreignKey);
    assertEquals(name, foreignKey.getName());

    var column = foreignKey.getColumn();
    assertNotNull(column);
    assertEquals(name, column.getName());
    assertType(column, types);

    var attributes = column.getAttributes();
    assertNotNull(attributes.getNullable());
  }

  protected void assertType(Column column, Column.Type... types){
    log.info("Actual column {} type {}. Trying to validate over {}", column.getName(), column.getType(), types);
    assertTrue(List.of(types).contains(column.getType()));
  }

  public static void assertAny(String value, String... values) {
    assertTrue(Arrays.stream(values).anyMatch(value::equalsIgnoreCase));
  }
}
