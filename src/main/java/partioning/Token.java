package partioning;

import common.RemoteDBNode;

import java.util.List;

public class Token  {
    private String position;
    private List<RemoteDBNode> nodes;

    public Token(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public List<RemoteDBNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<RemoteDBNode> nodes) {
        this.nodes = nodes;
    }
}
