package com.ascendcorp.exam.proxy;

import com.ascendcorp.exam.model.TransferResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class BankProxyGatewayTest {

    @Test
    public void test_request_transfer_returns_non_null_response() {
        BankProxyGateway gateway = new BankProxyGateway();

        TransferResponse response = gateway.requestTransfer(
                "tx123",
                new Date(),
                "channel1",
                "bank001",
                "1234567890",
                1000.0,
                "ref1",
                "ref2"
        );

        assertNotNull(response);
    }
}
