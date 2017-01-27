package fr.istic.crm.repository;

import fr.istic.crm.domain.Partenariat;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Partenariat entity.
 */
@SuppressWarnings("unused")
public interface PartenariatRepository extends JpaRepository<Partenariat,Long> {

    @Query("select distinct partenariat from Partenariat partenariat left join fetch partenariat.diplomes")
    List<Partenariat> findAllWithEagerRelationships();

    @Query("select partenariat from Partenariat partenariat left join fetch partenariat.diplomes where partenariat.id =:id")
    Partenariat findOneWithEagerRelationships(@Param("id") Long id);

}
