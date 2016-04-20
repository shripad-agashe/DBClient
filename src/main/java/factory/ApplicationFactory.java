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
            RemoteDBNode primaryNode = nodes.get(i);
            int secondaryToekn = (i + 1 == regions) ? 0 : i + 1;
            RemoteDBNode secondaryNode = nodes.get(secondaryToekn);
            Token token = new Token(Integer.toString(i));
            System.out.println(primaryNode + " ######### " + secondaryNode);
            token.setNodes(Arrays.asList(primaryNode, secondaryNode));
            tokens.add(token);
        }

        return tokens;

    }

    public static RPCClient simpleRPCClient(String host, int port) {
        Token token = new Token("1");
        token.setNodes(Arrays.asList(new RemoteDBNode(host,port)));

        ConsistentHash<Token> remoteTokeHash = new ConsistentHash<Token>(new HashFunction(), Arrays.asList(token));
        return new RPCClient(remoteTokeHash);
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
