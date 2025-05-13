package br.com.springbank.domain.entities.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_permissions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String name;
}
