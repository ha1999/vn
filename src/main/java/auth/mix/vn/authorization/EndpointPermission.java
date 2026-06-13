package auth.mix.vn.authorization;

import auth.mix.vn.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "endpoint_permissions")
@Getter
@Setter
public class EndpointPermission extends BaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 10)
    private String httpMethod;

    @Column(nullable = false, length = 255)
    private String pathPattern;

    @Column(length = 100)
    private String requiredPermission;

    private boolean enabled = true;
}
