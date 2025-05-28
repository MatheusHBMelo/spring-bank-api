package br.com.springbank.domain.repositories.account;

import br.com.springbank.domain.entities.account.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    @Query("""
                SELECT t FROM TransactionEntity t
                WHERE t.sourceAccount.id = :accountId
                   OR t.destinationAccount.id = :accountId
                ORDER BY t.createdAt DESC
            """)
    List<TransactionEntity> findAllByAccountId(Long accountId);
}
