package ru.otus.protobuf;

import io.grpc.ServerBuilder;
import java.io.IOException;
import ru.otus.protobuf.service.NumberProvider;
import ru.otus.protobuf.service.RemoteNumberServiceImpl;

@SuppressWarnings({"squid:S106"})
public class GRPCServer {

    public static final int SERVER_PORT = 8190;

    public GRPCServer() {
    }

    public void start() throws InterruptedException, IOException {
        var service = new RemoteNumberServiceImpl(NumberProvider::new, 2000);
        var server = ServerBuilder.forPort(SERVER_PORT).addService(service).build();
        server.start();
        System.out.println("server waiting for client connections...");
        server.awaitTermination();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new GRPCServer().start();
    }
}
