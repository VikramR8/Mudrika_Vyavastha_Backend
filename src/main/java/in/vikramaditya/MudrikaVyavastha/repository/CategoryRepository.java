package in.vikramaditya.MudrikaVyavastha.repository;

import in.vikramaditya.MudrikaVyavastha.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository <CategoryEntity, Long>{

    //Select * from tbl_categories where profile_id = ?1
    List<CategoryEntity> findByProfileId(Long profileId);

    //Select * from tbl_categories where id = ?1 and profile_id = ?2
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    //Select * from tbl_categories where type = ?1 and profile_id = ?2
    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

    //Select 1 from tbl_categories where name = ?1 AND profile_id = ?2 limit 1
    Boolean existsByNameAndProfileId(String name, Long profileId);
}
