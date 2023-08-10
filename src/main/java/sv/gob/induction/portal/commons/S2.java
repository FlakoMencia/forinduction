package sv.gob.induction.portal.commons;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class S2 implements Serializable{
    private static final long serialVersionUID = 1L;

    private String id;
    private String text;
    private Serializable extraData;

    public S2(String id, String text) {
        this.id = id;
        this.text = text;
    }
}
