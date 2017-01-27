package fr.istic.crm.repository;

import fr.istic.crm.domain.ConventionStage;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ConventionStage entity.
 */
@SuppressWarnings("unused")
public interface ConventionStageRepository extends JpaRepository<ConventionStage,Long> {

}
