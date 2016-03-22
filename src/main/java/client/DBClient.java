package client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import common.DBEntry;
import common.DBOperationResponse;
import common.DBQuery;

import java.io.IOException;
import java.net.Socket;

public class DBClient {
    private String host;
    private int port;

    public DBClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void put(String key, String value) throws IOException, ClassNotFoundException, InterruptedException {
        doNetworkRpc(new DBEntry(key, value));
    }

  public String get(String key) throws IOException, ClassNotFoundException, InterruptedException {

      DBOperationResponse dbOperationResponse = doNetworkRpc(new DBQuery(key));

      return  dbOperationResponse.getResponse();
    }

    private DBOperationResponse doNetworkRpc(Object obj) throws IOException {
        Socket socket = new Socket(this.host, this.port);
        Kryo kryo = new Kryo();
        Output output = new Output(socket.getOutputStream());

        kryo.writeClassAndObject(output, obj);
        kryo.writeClassAndObject(output, "#");
        output.flush();

        Input input = new Input(socket.getInputStream());
        DBOperationResponse dbOperationResponse = kryo.readObject(input, DBOperationResponse.class);

        socket.close();
        return dbOperationResponse;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        DBClient client = new DBClient("localhost", 9999);
        client.put("ac","A");
        client.put("ac","B");
        client.put("ac","C");
        client.put("de","D");
        System.out.println(client.get("ac"));
    }
}
