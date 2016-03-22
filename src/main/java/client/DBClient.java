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
        Socket socket = new Socket(this.host, this.port);

        Kryo kryo = new Kryo();
        Output output = new Output(socket.getOutputStream());

        kryo.writeClassAndObject(output, new DBEntry(key,value));
        kryo.writeClassAndObject(output, "#");
        output.flush();

        Input input = new Input(socket.getInputStream());
        DBOperationResponse dbOperationResponse = kryo.readObject(input, DBOperationResponse.class);
        System.out.printf("D##### " + dbOperationResponse.getResponse());

        socket.close();
    }

  public String get(String key) throws IOException, ClassNotFoundException, InterruptedException {
        Socket socket = new Socket(this.host, this.port);

        Kryo kryo = new Kryo();
        Output output = new Output(socket.getOutputStream());

        kryo.writeClassAndObject(output, new DBQuery(key));
        kryo.writeClassAndObject(output, "#");
        output.flush();

        Input input = new Input(socket.getInputStream());
        DBOperationResponse dbOperationResponse = kryo.readObject(input, DBOperationResponse.class);
        System.out.printf("D##### " + dbOperationResponse.getResponse());

        socket.close();

      return  dbOperationResponse.getResponse();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        DBClient client = new DBClient("localhost", 9999);
        client.put("ac","GGG");
        client.put("ac","as");
        client.put("ac","GsGG");
        client.put("de","GGG");
        System.out.println(client.get("ac"));
    }
}
