package factory;

import common.RemoteDBNode;
import partioning.ConsistentHash;
import partioning.HashFunction;
import rpc.RPCClient;

import java.util.ArrayList;

public class ApplicationFactory {
    public static RPCClient getRPCClient() {
        ConsistentHash<RemoteDBNode> remoteDBNodeConsistentHash = new ConsistentHash<RemoteDBNode>(new HashFunction(),3,new ArrayList<RemoteDBNode>());
        remoteDBNodeConsistentHash.add(new RemoteDBNode("192.168.99.100",9999));
        remoteDBNodeConsistentHash.add(new RemoteDBNode("192.168.99.100",9998));
        remoteDBNodeConsistentHash.add(new RemoteDBNode("192.168.99.100",9997));
        return new RPCClient(remoteDBNodeConsistentHash);
    }



}
