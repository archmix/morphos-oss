package morphos.api.interfaces;

import java.util.Optional;

public enum ObjectType  {
    TABLE("TABLE"),
    VIEW("VIEW"),
    SYSTEM_TABLE("SYSTEM TABLE"),
    GLOBAL_TEMPORARY("GLOBAL TEMPORARY"),
    LOCAL_TEMPORARY("LOCAL TEMPORARY"),
    ALIAS("ALIAS"),
    SYNONYM("SYNONYM");

    private String value;

    ObjectType(String value) {
      this.value = value;
    }

    public String value() {
      return this.value;
    }

    public static Optional<ObjectType> of(String code) {
      for (ObjectType type : ObjectType.values()) {
        if (code.equalsIgnoreCase(type.value)) {
          return Optional.of(type);
        }
      }
      return Optional.empty();
    }
  }