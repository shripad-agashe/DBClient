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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiFunction;

public class RPCClient {

    private ConsistentHash<Token> consistentHash;

    public RPCClient(ConsistentHash<Token> consistentHash) {
        this.consistentHash = consistentHash;
    }

    public <T extends DBOperation> DBOperationResponse doNetworkRpc(T dbOperation) throws IOException {

        Token token = consistentHash.get(dbOperation.getKey());
//        System.out.println("######### " + token.getPosition());

        List<CompletableFuture<DBOperationResponse>> response = new ArrayList<CompletableFuture<DBOperationResponse>>();

        int replication = 0;

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
            if(replication == 1) {
                break;
            }
//            RPCTask rpcTask = new RPCTask(dbNode, dbOperation);
//            ForkJoinTask<DBOperationResponse> fork = rpcTask.fork();
//            response.add(fork);
            CompletableFuture<DBOperationResponse> dbResponseFuture = new RPCFuture().getDBResponseFuture(dbNode, dbOperation);
            dbResponseFuture.exceptionally(ex -> {
                System.out.println("We have problem: " + ex.getMessage());
                return new DBOperationResponse("Exception");
            });
//            response.add(dbResponseFuture);


            CompletableFuture<String> completableFuture = future.thenCombineAsync(dbResponseFuture, bi);
            future = completableFuture;

            replication ++;

        }
        String resp = "";
//        future.complete("Done");
        while (!future.isDone()) {
//            System.out.println("In loop");
            if (future.isDone()) {
                resp = future.getNow("False");
                System.out.println("Done with future");
                break;
            }
        }

//
//        Stream<DBOperationResponse> dbOperationResponseStream = response.stream().map( future -> {
//            try {
//                return future(new DBOperationResponse("NA"));
//            } catch (Exception e) {
//                return new DBOperationResponse("Error");
//            }
//        });

//        List<DBOperationResponse> collect = dbOperationResponseStream.collect(Collectors.toList());


        System.out.println("Returning response:" + resp);
        return new DBOperationResponse(resp);
    }

    private class RPCFuture {
        public CompletableFuture<DBOperationResponse> getDBResponseFuture(RemoteDBNode node, DBOperation operation){
            return CompletableFuture.supplyAsync(() -> {
                DBOperationResponse dbOperationResponse = getDbOperationResponse(node, operation);
                System.out.println("In RPC Future");
                return dbOperationResponse; }
            );
        }
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
            return getDbOperationResponse(dbNode,operation);
        }


    };

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
