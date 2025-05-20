package br.com.springbank.domain.entities.account;

import br.com.springbank.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal ammount;

    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private AccountEntity sourceAccount;

    @ManyToOne
    @JoinColumn(name = "destination_account_id")
    private AccountEntity destinationAccount;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
