package auth.mix.vn.v1.permission.entity;

import auth.mix.vn.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission extends BaseEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    private boolean enabled = true;
}
