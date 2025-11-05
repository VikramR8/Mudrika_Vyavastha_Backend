package in.vikramaditya.MudrikaVyavastha.repository;

import in.vikramaditya.MudrikaVyavastha.entity.ProfileEntity;
import org.hibernate.property.access.internal.PropertyAccessFieldImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    //Select * from tbl_profiles where email = ?
    Optional<ProfileEntity> findByEmail (String email);

    //Select * from tbl_profiles where activationToken = ?
    Optional<ProfileEntity>findByActivationToken(String activationToken);

}
