package morphos.api.interfaces;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Slf4j
public class MorphosReflector {

  public static MorphosCache reflect(Connection connection) {
    return reflect(connection, Schema.defaultSchema());
  }

  public static MorphosCache reflect(Connection connection, Schema schema) {
    var cache = MorphosCache.of();

    try (var resultSet = connection.getMetaData().getTables(null, schema.value(), "%", new String[]{ObjectType.TABLE.value()})) {
      while (resultSet.next()) {
        final var tableSchema = Schema.of(resultSet.getString("TABLE_SCHEM"));
        final var type = ObjectType.of(resultSet.getString("TABLE_TYPE")).orElse(null);
        final var name = resultSet.getString("TABLE_NAME");

        final var table = Table.create(tableSchema, name, type);
        setColumns(connection, table);
        setAttributes(connection, table);
        setPrimaryKeys(connection, table);
        cache.add(table);
      }

      for (var table : cache.tables()) {
        setForeignKeys(connection, table, cache);
      }
    } catch (SQLException e) {
      logAndThrowError(e, "Could not retrieve tables for schema {}", schema.value());
      log.error("Could not retrieve tables", e);
      throw new RuntimeException(e);
    }
    return cache;
  }

  private static void setColumns(Connection connection, Table table) {
    final var YES = "YES";
    try (var resultSet = connection.getMetaData().getColumns(null, table.getSchema().value(), table.getName(), null);) {
      while (resultSet.next()) {
        var columnType = Column.Type.of(resultSet.getInt("DATA_TYPE"));
        var mappedType = mapColumnType(connection, columnType, resultSet.getString("TYPE_NAME"));

        final var column = Column.create(
          table,
          resultSet.getString("COLUMN_NAME"),
          mappedType,
          resultSet.getLong("COLUMN_SIZE"),
          resultSet.getLong("DECIMAL_DIGITS"),
          YES.equalsIgnoreCase(resultSet.getString("IS_AUTOINCREMENT")),
          YES.equalsIgnoreCase(resultSet.getString("IS_GENERATEDCOLUMN"))
        );

        column.setAttribute(AttributeType.NULLABLE, resultSet.getInt("NULLABLE") == 1);
        column.setAttribute(AttributeType.DEFAULT, resultSet.getString("COLUMN_DEF"));

        table.add(column);
      }
    } catch (SQLException e) {
      logAndThrowError(e, "Could not retrieve columns for table {}", table.getName());
    }
  }

  private static Column.Type mapColumnType(Connection connection, Column.Type columnType, String typeName) {
    if(columnType.equals(Column.Type.OTHER)) {
        return Column.Type.mapFromName(typeName);
    }

    if(!columnType.equals(Column.Type.DISTINCT)) {
      return columnType;
    }

    var typeNamePattern = typeName.replaceAll(".*\\.\"?([^\"]+)\"?$", "%$1%");
    try (ResultSet resultSet = connection.getMetaData().getUDTs(null, null, typeNamePattern, new int[]{Types.DISTINCT})) {
      while (resultSet.next()) {
        final var baseType = resultSet.getInt("BASE_TYPE");
        final var name = resultSet.getString("TYPE_NAME");

        if(typeName.contains(name)) {
          return Column.Type.of(baseType);
        }
      }
    } catch (SQLException e) {
      logAndThrowError(e, "Could not retrieve user defined type {}", typeName);
    }

    return Column.Type.OTHER;
  }

  private static void setAttributes(Connection connection, Table table) {
    try (ResultSet resultSet = connection.getMetaData().getIndexInfo(null, table.getSchema().value(), table.getName(), false, false);) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString("COLUMN_NAME");
        final String indexName = resultSet.getString("INDEX_NAME");
        final boolean unique = !resultSet.getBoolean("NON_UNIQUE");

        table.getColumn(columnName).ifPresent(column -> {
          column.setAttribute(AttributeType.INDEX, indexName);
          column.setAttribute(AttributeType.UNIQUE, unique);
        });
      }
    } catch (SQLException e) {
      logAndThrowError(e, "Could not retrieve constraints for table {}", table.getName());
    }
  }

  private static void setPrimaryKeys(Connection connection,Table table) {
    try (ResultSet resultSet = connection.getMetaData().getPrimaryKeys(null, table.getSchema().value(), table.getName());) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString("COLUMN_NAME");
        final String indexName = resultSet.getString("PK_NAME");

        var column = table.getColumn(columnName).get();
        column.setAttribute(AttributeType.UNIQUE, true);
        PrimaryKey primaryKey = PrimaryKey.create(indexName, column);
        table.add(primaryKey);
      }
    } catch (SQLException e) {
      logAndThrowError(e, "Could not retrieve primary keys for table {}", table.getName());
    }
  }

  private static void setForeignKeys(Connection connection, Table table, MorphosCache cache) {
    try (ResultSet resultSet = connection.getMetaData().getImportedKeys(null, table.getSchema().value(), table.getName());) {
      while (resultSet.next()) {
        final String fkColumnName = resultSet.getString("FKCOLUMN_NAME");
        final String fkIndexName = resultSet.getString("FK_NAME");
        final ForeignKey.Rule onUpdate = ForeignKey.Rule.of(resultSet.getInt("UPDATE_RULE"));
        final ForeignKey.Rule onDelete = ForeignKey.Rule.of(resultSet.getInt("DELETE_RULE"));

        String pkTableName = resultSet.getString("PKTABLE_NAME");
        var pkTable = cache.getTable(pkTableName)
          .orElseThrow(() -> new RuntimeException(String.format("Table %s not found", pkTableName)));

        table.findColumn(fkColumnName).ifPresent(fkColumn -> {
          ForeignKey foreignKey = ForeignKey.create(fkIndexName, pkTable, fkColumn, onUpdate, onDelete);
          table.add(foreignKey);
        });
      }
    } catch (SQLException e) {
      logAndThrowError(e, "Could not retrieve foreign keys for table {}", table.getName());
    }
  }

  private static void logAndThrowError(SQLException e, String message, String... args) {
    log.error(message, args);
    log.error(e.getMessage(), e);
    throw new RuntimeException(e);
  }
}
