package diplome.ingridientsDto;

import lombok.Data;

import java.util.List;

@Data
public class IngridientsResponse {
    private boolean success;
    private List<Item> data;
}
