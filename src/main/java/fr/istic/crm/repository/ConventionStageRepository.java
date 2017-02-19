package fr.istic.crm.repository;

import fr.istic.crm.domain.ConventionStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the ConventionStage entity.
 */
@SuppressWarnings("unused")
public interface ConventionStageRepository extends JpaRepository<ConventionStage,Long> {

    @Query("select conventionStage.lieuStage, COUNT(conventionStage) as nbEtudiants from ConventionStage conventionStage GROUP BY conventionStage.lieuStage")
    Page<Object> findNbEtudiantsBySite(Pageable pageable);
}
