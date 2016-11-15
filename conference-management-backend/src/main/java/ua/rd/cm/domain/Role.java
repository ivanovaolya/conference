package ua.rd.cm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Artem_Pryzhkov
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {

    @TableGenerator(
            name = "roleGen",
            table = "role_id_gen",
            pkColumnName = "gen_key",
            valueColumnName = "gen_value",
            pkColumnValue = "role_id",
            allocationSize = 1
    )

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "roleGen")
    private Long id;

    @NotNull
    @Column(name = "role_name", nullable = false)
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
