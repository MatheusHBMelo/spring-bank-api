package br.com.springbank.controller.transaction;

import br.com.springbank.controller.transaction.dto.DepositRequestDto;
import br.com.springbank.controller.transaction.dto.TransferRequestDto;
import br.com.springbank.service.transaction.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(path = "/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequestDto transferRequestDto){
        this.transactionService.transfer(transferRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(path = "/deposit")
    public ResponseEntity<Void> transfer(@RequestBody DepositRequestDto depositRequestDto){
        this.transactionService.deposit(depositRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
