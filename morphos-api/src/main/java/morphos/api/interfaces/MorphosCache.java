package morphos.api.interfaces;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor(staticName = "of")
public class MorphosCache implements Serializable {
  private final Map<String, Table> tables = new ConcurrentHashMap<>();

  public Optional<Table> getTable(String name) {
    return Optional.ofNullable(tables.get(name));
  }

  public void add(Table table) {
    tables.put(table.getName(), table);
  }

  public boolean contains(String name) {
    return tables.containsKey(name);
  }

  public void clear() {
    tables.clear();
  }

  public int size() {
    return tables.size();
  }

  public Collection<Table> tables() {
    return Collections.unmodifiableCollection(this.tables.values());
  }
}
