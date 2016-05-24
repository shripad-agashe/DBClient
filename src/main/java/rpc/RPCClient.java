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
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class RPCClient {

    private ConsistentHash<Token> consistentHash;

    public RPCClient(ConsistentHash<Token> consistentHash) {
        this.consistentHash = consistentHash;
    }


    public <T extends DBOperation> DBOperationResponse doNetworkRpc(T dbOperation) throws IOException {
           return doNetworkRpc(dbOperation,2);
    }
    public <T extends DBOperation> DBOperationResponse doNetworkRpc(T dbOperation,int replication) throws IOException {

        Token token = consistentHash.get(dbOperation.getKey());
        int replicationCounter = 0;

        CompletableFuture<String> future = new CompletableFuture<>();
        future.complete("Done");

        BiFunction<String, DBOperationResponse,String> bi = (x, y) -> {
            if("Added".equals(y.getResponse())) {
                System.out.println("In for loop Bi function");
                return "Added";
            }
            else return "False";
        };

        for(RemoteDBNode dbNode:token.getNodes()){
            if(replicationCounter == replication) {
                break;
            }
            CompletableFuture<DBOperationResponse> dbResponseFuture = getDBResponseFuture(dbNode, dbOperation);
            dbResponseFuture.exceptionally(ex -> {
                System.out.println("We have problem: " + ex.getMessage());
                return new DBOperationResponse("Exception");
            });


            CompletableFuture<String> completableFuture = future.thenCombineAsync(dbResponseFuture, bi);
            future = completableFuture;

            replication ++;

        }
        String resp = "";
        while (!future.isDone()) {
            if (future.isDone()) {
                resp = future.getNow("False");
                System.out.println("Done with future");
                break;
            }
        }

        System.out.println("Returning response:" + resp);
        return new DBOperationResponse(resp);
    }


    private CompletableFuture<DBOperationResponse> getDBResponseFuture(RemoteDBNode node, DBOperation operation){
        return CompletableFuture.supplyAsync(() -> {
            DBOperationResponse dbOperationResponse = getDbOperationResponse(node, operation);
            System.out.println("In RPC Future");
            return dbOperationResponse; }
        );
    }


    private DBOperationResponse getDbOperationResponse(RemoteDBNode dbNode, DBOperation operation) {
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

        System.out.println("Operation REsponse:" + dbOperationResponse.getResponse());

        return dbOperationResponse;
    }
}
