package client;

import common.DBEntry;
import common.DBOperationResponse;
import common.DBQuery;
import factory.ApplicationFactory;
import rpc.RPCClient;

import java.io.IOException;

public class DBClient {


    private RPCClient client;

    public DBClient(RPCClient client) {

        this.client = client;
    }

    public void put(String key, String value) throws IOException, ClassNotFoundException, InterruptedException {
        client.doNetworkRpc(new DBEntry(key, value));
    }

  public String get(String key) throws IOException, ClassNotFoundException, InterruptedException {

      DBOperationResponse dbOperationResponse = client.doNetworkRpc(new DBQuery(key));

      return  dbOperationResponse.getResponse();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        DBClient client = new DBClient(ApplicationFactory.getRPCClient());
        client.put("nn","ddd");
        client.put("nn2","ssss");
        client.put("nn3","C");
        client.put("qq","GGGGGGGG");
        client.put("qq1","C");
        System.out.println(client.get("qq"));

        System.out.println("#################################");

        System.out.println(new DBClient(ApplicationFactory.simpleRPCClient("192.168.99.100",9999)).get("qq"));
        System.out.println(new DBClient(ApplicationFactory.simpleRPCClient("192.168.99.100",9998)).get("qq"));
        System.out.println(new DBClient(ApplicationFactory.simpleRPCClient("192.168.99.100",9997)).get("qq"));
    }
}
