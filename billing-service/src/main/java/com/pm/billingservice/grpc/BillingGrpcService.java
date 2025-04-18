package com.pm.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Achintha Kalunayaka
 * @since 4/18/2025
 */

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(BillingRequest request, StreamObserver<BillingResponse> responseObserver) {
        logger.info("CreateBillingAccount Request Received {}", request.toString());

        BillingResponse billingResponse = BillingResponse.newBuilder().setAccountId("1234").setStatus("ACTIVE").build();

        responseObserver.onNext(billingResponse);
        responseObserver.onCompleted();
    }
}
