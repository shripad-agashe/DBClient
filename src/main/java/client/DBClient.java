package client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import common.DBEntry;
import common.DBOperationResponse;

import java.io.IOException;
import java.net.Socket;

public class DBClient {
    private String host;
    private int port;

    public DBClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void put(String key, String value) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(this.host,this.port);
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//        objectOutputStream.writeObject(new DBEntry(key,value));
//        objectOutputStream.flush();
        Kryo kryo = new Kryo();
        kryo.register(DBEntry.class,new JavaSerializer());
        Output output = new Output(socket.getOutputStream());
            kryo.writeClassAndObject(output,new DBEntry(key,value));


        output.flush();

        Input input = new Input(socket.getInputStream());
        DBOperationResponse dbOperationResponse = kryo.readObject(input, DBOperationResponse.class);
        System.out.printf("D##### " + dbOperationResponse.getResponse());

        socket.close();
//
//
////        objectOutputStream.close();
//
//        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//        DBOperationResponse response = (DBOperationResponse) objectInputStream.readObject();
//        System.out.println(response.getResponse());

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DBClient client = new DBClient("localhost", 9999);
        client.put("ac","de");
    }
}
