package sv.gob.induction.portal.domain;

import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "cc_telefono_titular")
public class CcTelefonoTitular implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "sk_tel_titular")
    @EqualsAndHashCode.Include
    @JsonView
    private Integer skTelTitular; 

    @Column(name = "cd_telefono_titular" )
    private String cdTelefonoTitular; 

    @Column(name = "st_telefono_titular" )
    private String stTelefonoTitular; 


    // delegates de ids
}
