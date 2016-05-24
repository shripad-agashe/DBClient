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

    public DBOperationResponse put(String key, String value) throws IOException, ClassNotFoundException, InterruptedException {
        return client.doNetworkRpc(new DBEntry(key, value));
    }

  public String get(String key) throws IOException, ClassNotFoundException, InterruptedException {

      DBOperationResponse dbOperationResponse = client.doNetworkRpc(new DBQuery(key));

      return  dbOperationResponse.getResponse();
    }



    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        DBClient client = new DBClient(ApplicationFactory.getRPCClient());
        DBOperationResponse put = client.put("nn", "ddd");
        System.out.println(put.getResponse());
        client.put("nn2","ssss");
        client.put("nn3","C");
        client.put("qq","GGGGGGGG");
        client.put("qq1","C");
        System.out.println(client.get("qq"));

        System.out.println("#################################");

        System.out.println(new DBClient(ApplicationFactory.simpleRPCClient("localhost",9999)).get("qq"));
        System.out.println(new DBClient(ApplicationFactory.simpleRPCClient("localhost",9998)).get("qq"));
        System.out.println(new DBClient(ApplicationFactory.simpleRPCClient("localhost",9997)).get("qq"));

    }
}
