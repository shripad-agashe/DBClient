package factory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import common.RemoteDBNode;
import partioning.ConsistentHash;
import partioning.HashFunction;
import partioning.Token;
import rpc.RPCClient;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationFactory {
    static Config conf = ConfigFactory.load("client");
    public static RPCClient getRPCClient() {
        List<Token> tokens = getTokens(getNodes());
        ConsistentHash<Token> remoteTokeHash = new ConsistentHash<Token>(new HashFunction(), tokens);
        return new RPCClient(remoteTokeHash);
    }

    private static List<Token> getTokens(List<RemoteDBNode> nodes){
        int regions = conf.getInt("config.num_regions");
        List<Token> tokens = new ArrayList<Token>();
        for(int i=0; i <regions; i++){
            Token token = new Token(Integer.toString(i));
            token.setNodes(nodes);
            tokens.add(token);
        }

        return tokens;

    }



    private static List<RemoteDBNode> getNodes() {

        ConfigList nodeList = conf.getList("config.nodes");
        Stream<RemoteDBNode> remoteDBNodeStream = nodeList.stream().map(x -> {
            Map<String, Object> map = (HashMap) x.unwrapped();
            return new RemoteDBNode(map.get("ip").toString(), ((Integer)map.get("port")).intValue());
        });

        List<RemoteDBNode> nodes = remoteDBNodeStream.collect(Collectors.toList());
        return nodes;
    }


}
