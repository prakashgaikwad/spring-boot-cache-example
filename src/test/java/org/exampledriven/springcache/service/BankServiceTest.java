package org.exampledriven.springcache.service;

import org.exampledriven.springcache.domain.Account;
import org.exampledriven.springcache.domain.Transfer;
import org.exampledriven.springcache.repository.TransferRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BankServiceTest {

    @SpyBean
    private TransferRepository transferRepository;

    @Autowired
    private BankService bankService;

    @Autowired
    CacheManager cacheManager;

    @Test
    public void readTransfer() {
        createTransfer();
    }

    @Test
    public void readTransferByObject() {

        Transfer transfer = bankService.createTransfer(new Account(), new Account(), 1);

        Transfer transfer1 = bankService.readTransfer(transfer);
        Transfer transfer2 = bankService.readTransfer(transfer.getId(), false);

        assertEquals(transfer, transfer1);
        assertEquals(transfer, transfer2);

        verify(transferRepository, times(1)).readTransfer(transfer.getId());
    }

    public Transfer createTransfer() {

        Transfer transfer = bankService.createTransfer(new Account(), new Account(), 1);

        Transfer transfer1 = bankService.readTransfer(transfer.getId(), false);
        Transfer transfer2 = bankService.readTransfer(transfer.getId(), false);

        assertEquals(transfer, transfer1);
        assertEquals(transfer, transfer2);

        verify(transferRepository, times(2)).readTransfer(transfer.getId());

        bankService.completeTransfer(transfer.getId(), true, true);

        Transfer transfer3 = bankService.readTransfer(transfer.getId(), false);
        Transfer transfer4 = bankService.readTransfer(transfer.getId(), false);

        assertEquals(transfer, transfer3);
        assertEquals(transfer, transfer4);

        verify(transferRepository, times(2)).readTransfer(transfer.getId());

        return transfer;

    }

    @Test
    public void archiveTransfer() {

        Transfer transfer = createTransfer();

        assertEquals(transfer, cacheManager.getCache("transfer").get(transfer.getId()).get());

        bankService.archiveTransfer(transfer.getId());

        assertNull(cacheManager.getCache("transfer").get(transfer.getId()));

    }

    @Test
    public void handleEmpty() {
        assertNull(bankService.readTransfer(-1, false));
    }

}