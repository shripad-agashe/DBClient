package common;

public class RemoteDBNode {
    String host;
    int port;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public RemoteDBNode(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
