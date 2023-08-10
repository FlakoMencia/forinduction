package sv.gob.induction.portal.dto;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "VW_USER_ROLE")
public class VwUserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "C_USER")
    private String cuser;

    @Basic(optional = false)
    @Column(name = "C_ROLE")
    private String crole;
}
