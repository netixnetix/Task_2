package models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Ingredient {

    private String _id;
    private String name;
    private String type;
    private Integer price;
}
