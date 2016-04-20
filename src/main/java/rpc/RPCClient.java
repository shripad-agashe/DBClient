package rpc;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import common.DBOperation;
import common.DBOperationResponse;
import common.RemoteDBNode;
import partioning.ConsistentHash;
import partioning.Token;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RPCClient {

    private ConsistentHash<Token> consistentHash;

    public RPCClient(ConsistentHash<Token> consistentHash) {
        this.consistentHash = consistentHash;
    }

    public <T extends DBOperation> DBOperationResponse doNetworkRpc(T dbOperation) throws IOException {

        Token token = consistentHash.get(dbOperation.getKey());
        System.out.println("######### " + token.getPosition());

        List<ForkJoinTask<DBOperationResponse>> response = new ArrayList<ForkJoinTask<DBOperationResponse>>();

        int replication = 0;
        for(RemoteDBNode dbNode:token.getNodes()){
            if(replication == 2) {
                break;
            }
            RPCTask rpcTask = new RPCTask(dbNode, dbOperation);
            ForkJoinTask<DBOperationResponse> fork = rpcTask.fork();
            response.add(fork);
            replication ++;

        }

        Stream<DBOperationResponse> dbOperationResponseStream = response.stream().map(task -> {
            try {
                return task.join();
            } catch (Exception e) {
                return new DBOperationResponse("Something wrong");
            }
        });

        List<DBOperationResponse> collect = dbOperationResponseStream.collect(Collectors.toList());



        return collect.get(0);
    }

    private class RPCTask extends RecursiveTask<DBOperationResponse>{

        private final RemoteDBNode dbNode;
        private DBOperation operation;

        public RPCTask(RemoteDBNode dbNode, DBOperation operation){
            this.dbNode = dbNode;
            this.operation = operation;
        }

        @Override
        protected DBOperationResponse compute() {
            System.out.println("In thread:" + dbNode.getPort());
            Kryo kryo;
            Output output;
            DBOperationResponse dbOperationResponse = null;
            try (Socket socket = new Socket(dbNode.getHost(), dbNode.getPort())) {
                kryo = new Kryo();
                output = new Output(socket.getOutputStream());
                kryo.writeClassAndObject(output, operation);
                kryo.writeClassAndObject(output, "#");
                output.flush();

                Input input = new Input(socket.getInputStream());
                dbOperationResponse = kryo.readObject(input, DBOperationResponse.class);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                dbOperationResponse = new DBOperationResponse("Error in Network");
            }


            return dbOperationResponse;
        }
    };
}
