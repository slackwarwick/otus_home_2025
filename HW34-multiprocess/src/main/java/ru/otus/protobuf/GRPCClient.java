package ru.otus.protobuf;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"squid:S106", "squid:S2142"})
public class GRPCClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;
    private static final int NO_NUMBER = -1;

    private final AtomicInteger serverValue = new AtomicInteger();

    public GRPCClient() {
    }

    public void start() throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();
        var latch = runServerStream(channel);
        var future = runClientLoop();
        latch.await();
        future.join();
        channel.shutdown();
    }

    private CompletableFuture<Integer> runClientLoop() {
        return CompletableFuture.supplyAsync(() -> {
            int currentValue = 1;
            for (int i = 0; i < 50; ++i) {
                int serverValue = this.serverValue.getAndSet(NO_NUMBER);
                if (serverValue != NO_NUMBER) {
                    currentValue += serverValue;
                }
                System.out.printf("currentValue:%d%n", currentValue);
                ++currentValue;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return currentValue;
        });
    }

    private CountDownLatch runServerStream(ManagedChannel channel) {
        var latch = new CountDownLatch(1);
        var stub = RemoteNumberServiceGrpc.newStub(channel);
        stub.getNumbers(numberRangeToMessage(0, 30), new StreamObserver<NumberMessage>() {
            @Override
            public void onNext(NumberMessage nm) {
                System.out.printf("число от сервера: %d%n", nm.getValue());
                serverValue.set(nm.getValue());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });
        return latch;
    }

    private NumberRangeMessage numberRangeToMessage(int start, int end) {
        return NumberRangeMessage.newBuilder()
                .setStart(start)
                .setEnd(end)
                .build();
    }

    public static void main(String[] args) throws InterruptedException {
        new GRPCClient().start();
    }
}
