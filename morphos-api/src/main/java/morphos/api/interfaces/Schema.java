package morphos.api.interfaces;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
public class Schema implements Serializable {
  private static final long serialVersionUID = 1L;

  private final String value;

  public static Schema of(String value){
    if("null".equalsIgnoreCase(value)) {
      return defaultSchema();
    }

    return new Schema(value);
  }

  public static Schema defaultSchema() {
    return new Schema(null);
  }

  public String value() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
