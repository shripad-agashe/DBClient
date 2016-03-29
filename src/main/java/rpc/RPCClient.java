package rpc;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import common.DBOperation;
import common.DBOperationResponse;
import common.RemoteDBNode;
import partioning.ConsistentHash;

import java.io.IOException;
import java.net.Socket;

public class RPCClient {

    private ConsistentHash<RemoteDBNode> consistentHash;

    public RPCClient(ConsistentHash<RemoteDBNode> consistentHash) {
        this.consistentHash = consistentHash;
    }

    public <T extends DBOperation> DBOperationResponse doNetworkRpc(T dbOperation) throws IOException {

        RemoteDBNode node = consistentHash.get(dbOperation.getKey());
        System.out.println("######### " + node.getPort());

        Socket socket = new Socket(node.getHost(),node.getPort());
        Kryo kryo = new Kryo();
        Output output = new Output(socket.getOutputStream());

        kryo.writeClassAndObject(output, dbOperation);
        kryo.writeClassAndObject(output, "#");
        output.flush();

        Input input = new Input(socket.getInputStream());
        DBOperationResponse dbOperationResponse = kryo.readObject(input, DBOperationResponse.class);

        socket.close();
        return dbOperationResponse;
    }
}
