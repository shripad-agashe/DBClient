package common;

public class DBQuery implements  DBOperation{

    static final long serialVersionUID = 1L;
    private final String key;

    public DBQuery(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
