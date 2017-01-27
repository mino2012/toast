package fr.istic.crm.repository;

import fr.istic.crm.domain.Professionnel;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Professionnel entity.
 */
@SuppressWarnings("unused")
public interface ProfessionnelRepository extends JpaRepository<Professionnel,Long> {

    @Query("select distinct professionnel from Professionnel professionnel left join fetch professionnel.diplomes")
    List<Professionnel> findAllWithEagerRelationships();

    @Query("select professionnel from Professionnel professionnel left join fetch professionnel.diplomes where professionnel.id =:id")
    Professionnel findOneWithEagerRelationships(@Param("id") Long id);

}
