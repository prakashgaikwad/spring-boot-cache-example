package org.exampledriven.springcache.service;

import org.exampledriven.springcache.domain.Account;
import org.exampledriven.springcache.domain.Transfer;
import org.exampledriven.springcache.repository.TransferRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BankServiceTest {

    @SpyBean
    private TransferRepository transferRepository;

    @Autowired
    private BankService bankService;

    @Test
    public void getTransfer() {

        Transfer transfer = bankService.createTransfer(new Account(), new Account(), 1);

        Transfer transfer1 = bankService.readTransfer(transfer.getId());
        Transfer transfer2 = bankService.readTransfer(transfer.getId());

        assertEquals(transfer, transfer1);
        assertEquals(transfer, transfer2);

        verify(transferRepository, times(2)).readTransfer(transfer.getId());

        bankService.completeTransfer(transfer);

        Transfer transfer3 = bankService.readTransfer(transfer.getId());
        Transfer transfer4 = bankService.readTransfer(transfer.getId());

        assertEquals(transfer, transfer3);
        assertEquals(transfer, transfer4);

        verify(transferRepository, times(3)).readTransfer(transfer.getId());

    }

}