package fr.istic.crm.service;

import fr.istic.crm.service.dto.ConventionStageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing ConventionStage.
 */
public interface ConventionStageService {

    /**
     * Save a conventionStage.
     *
     * @param conventionStageDTO the entity to save
     * @return the persisted entity
     */
    ConventionStageDTO save(ConventionStageDTO conventionStageDTO);

    /**
     *  Get all the conventionStages.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ConventionStageDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" conventionStage.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ConventionStageDTO findOne(Long id);

    /**
     *  Delete the "id" conventionStage.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the conventionStage corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ConventionStageDTO> search(String query, Pageable pageable);
}
