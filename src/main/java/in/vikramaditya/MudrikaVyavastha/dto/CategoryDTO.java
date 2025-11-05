package in.vikramaditya.MudrikaVyavastha.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;
    private Long profileId;
    private String name;
    private  String icon;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
