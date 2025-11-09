package morphos.api.interfaces;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Types;

@RequiredArgsConstructor(staticName = "create")
@Builder
public class Column implements Field {
  private final Table table;

  @Getter
  private final String name;

  @Getter
  private final Type type;

  @Getter
  private final Long size;

  @Getter
  private final Long decimalDigits;

  private final Boolean autoIncrement;

  private final Boolean generated;

  @Getter
  private final Attributes attributes = new Attributes();

  void setAttribute(AttributeType constraintType, Object value) {
    this.attributes.add(constraintType, value);
  }

  public enum Type {
    BIT(Types.BIT),
    TINYINT(Types.TINYINT),
    SMALLINT(Types.SMALLINT),
    INTEGER(Types.INTEGER),
    BIGINT(Types.BIGINT),
    FLOAT(Types.FLOAT),
    REAL(Types.REAL),
    DOUBLE(Types.DOUBLE),
    NUMERIC(Types.NUMERIC),
    DECIMAL(Types.DECIMAL),
    CHAR(Types.CHAR),
    VARCHAR(Types.VARCHAR),
    LONGVARCHAR(Types.LONGVARCHAR),
    DATE(Types.DATE),
    TIME(Types.TIME),
    TIMESTAMP(Types.TIMESTAMP),
    BINARY(Types.BINARY),
    VARBINARY(Types.VARBINARY),
    LONGVARBINARY(Types.LONGVARBINARY),
    NULL(Types.NULL),
    BLOB(Types.BLOB),
    CLOB(Types.CLOB),
    BOOLEAN(Types.BOOLEAN),
    NCHAR(Types.NCHAR),
    NVARCHAR(Types.NVARCHAR),
    LONG_NVARCHAR(Types.LONGNVARCHAR),
    NCLOB(Types.NCLOB),
    TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),
    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE),
    DISTINCT(Types.DISTINCT),
    OTHER(Types.OTHER),
    UUID("UUID".hashCode()),
    ENUM("ENUM".hashCode()),
    XML("XML".hashCode()),
    MACADDR("MACADDR".hashCode()),
    INET("INET".hashCode()),
    CIDR("CIDR".hashCode()),
    JSON("JSON".hashCode()),
    ARRAY("ARRAY".hashCode()),
    INTERVAL("INTERVAL".hashCode());

    private final int value;

    Type(int value) {
      this.value = value;
    }

    static Type of(int code) {
      for (Type type : Type.values()) {
        if (type.value == code) {
          return type;
        }
      }

      return OTHER;
    }

    static Type mapFromName(String typeName) {
      for (Type type : Type.values()) {
        if (type.name().equalsIgnoreCase(typeName)) {
          return type;
        }
      }

      if (typeName.equalsIgnoreCase("JSONB")) {
        return JSON;
      }

      if (typeName.equalsIgnoreCase("datetimeoffset")) {
        return TIMESTAMP;
      }

      if(typeName.equalsIgnoreCase("_text")) {
        return ARRAY;
      }

      return OTHER;
    }

    public int value() {
      return this.value;
    }
  }

  Boolean getGenerated() {
    Boolean defaultValue = this.attributes.valueOf(AttributeType.DEFAULT).isPresent();
    return generated || autoIncrement || defaultValue;
  }

  public static class ColumnBuilder {
    private Table.TableBuilder root;

    ColumnBuilder root(Table.TableBuilder root) {
      this.root = root;
      return this;
    }

    public Table.TableBuilder add() {
      return this.root.add(this.build());
    }

    public Table.TableBuilder asPrimaryKey(String indexName) {
      var primaryKey = PrimaryKey.create(indexName, this.build());
      this.root.add(primaryKey);
      return this.root;
    }

    public ForeignKey.ForeignKeyBuilder asForeignKey(String indexName) {
      return ForeignKey.builder().root(this.root).column(this.build());
    }
  }
}
