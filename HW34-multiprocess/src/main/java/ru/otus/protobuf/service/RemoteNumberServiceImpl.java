package ru.otus.protobuf.service;

import io.grpc.stub.StreamObserver;
import ru.otus.protobuf.RemoteNumberServiceGrpc;
import ru.otus.protobuf.NumberMessage;
import ru.otus.protobuf.NumberRangeMessage;

import java.util.function.Supplier;

@SuppressWarnings({"squid:S2142", "squid:S106"})
public class RemoteNumberServiceImpl extends RemoteNumberServiceGrpc.RemoteNumberServiceImplBase {

    private final Supplier<NumberProvider> numberProviderSource;
    private final long pauseDuration;

    public RemoteNumberServiceImpl(Supplier<NumberProvider> numberProviderSource, long pauseDuration) {
        this.numberProviderSource = numberProviderSource;
        this.pauseDuration = pauseDuration;
    }

    @Override
    public void getNumbers(NumberRangeMessage request, StreamObserver<NumberMessage> responseObserver) {
        try {
            NumberProvider provider = numberProviderSource.get();
            provider.setRange(request.getStart(), request.getEnd());
            while (provider.hasNext()) {
                int value = provider.next();
                Thread.sleep(pauseDuration);
                responseObserver.onNext(numberToMessage(value));
            }
        } catch (InterruptedException e) {
            // TODO:
        }
        responseObserver.onCompleted();
    }

    private NumberMessage numberToMessage(int number) {
        return NumberMessage.newBuilder()
                .setValue(number)
                .build();
    }
}
