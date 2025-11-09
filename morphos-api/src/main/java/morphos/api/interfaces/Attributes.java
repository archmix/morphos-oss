package morphos.api.interfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Attributes {
  private final Map<AttributeType, Object> attributes = new HashMap<>();

  public Attributes() {
    this.attributes.put(AttributeType.NULLABLE, false);
    this.attributes.put(AttributeType.UNIQUE, false);
  }

  void add(AttributeType type, Object value) {
    this.attributes.put(type, value);
  }

  <T> Optional<T> valueOf(AttributeType type) {
    return Optional.ofNullable((T) this.attributes.get(type));
  }

  public Boolean getNullable() {
    return (Boolean) valueOf(AttributeType.NULLABLE).get();
  }

  public Boolean getUnique() {
    return (Boolean) valueOf(AttributeType.UNIQUE).get();
  }

  public Optional<String> getDefault() {
    return valueOf(AttributeType.DEFAULT);
  }

  public Optional<String> getIndex() {
    return valueOf(AttributeType.INDEX);
  }
}
