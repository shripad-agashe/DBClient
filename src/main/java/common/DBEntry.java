package common;

import java.io.Serializable;

public class DBEntry implements Serializable, DBOperation{
    static final long serialVersionUID = 1L;
    private final String key;
    private final String value;

    public DBEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
