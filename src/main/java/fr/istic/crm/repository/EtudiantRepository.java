package fr.istic.crm.repository;

import fr.istic.crm.domain.Etudiant;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Etudiant entity.
 */
@SuppressWarnings("unused")
public interface EtudiantRepository extends JpaRepository<Etudiant,Long> {

    @Query("select distinct etudiant from Etudiant etudiant left join fetch etudiant.diplomes")
    List<Etudiant> findAllWithEagerRelationships();

    @Query("select etudiant from Etudiant etudiant left join fetch etudiant.diplomes where etudiant.id =:id")
    Etudiant findOneWithEagerRelationships(@Param("id") Long id);

}
