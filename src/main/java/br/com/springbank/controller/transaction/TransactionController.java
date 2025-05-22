package br.com.springbank.controller.transaction;

import br.com.springbank.controller.transaction.dto.DepositRequestDto;
import br.com.springbank.controller.transaction.dto.StatementResponseDto;
import br.com.springbank.controller.transaction.dto.TransferRequestDto;
import br.com.springbank.controller.transaction.dto.WithdrawRequestDto;
import br.com.springbank.service.transaction.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(path = "/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequestDto transferRequestDto) {
        this.transactionService.transfer(transferRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(path = "/deposit")
    public ResponseEntity<Void> transfer(@RequestBody DepositRequestDto depositRequestDto) {
        this.transactionService.deposit(depositRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(path = "/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody WithdrawRequestDto withdrawRequestDto) {
        this.transactionService.withdraw(withdrawRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(path = "/statement")
    public ResponseEntity<List<StatementResponseDto>> bankStatement() {
        return ResponseEntity.status(HttpStatus.OK).body(this.transactionService.bankStatement());
    }
}
