package br.com.springbank.domain.repositories.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.springbank.domain.entities.account.TransactionEntity;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
}
