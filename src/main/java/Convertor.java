import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Convertor {
        private int id;
        private int Code;
        private String CcyNm_UZ;
        private int Nominal;
        private double Rate;
        private String Date;
    }

