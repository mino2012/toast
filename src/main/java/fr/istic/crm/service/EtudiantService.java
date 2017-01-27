package fr.istic.crm.service;

import fr.istic.crm.service.dto.EtudiantDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing Etudiant.
 */
public interface EtudiantService {

    /**
     * Save a etudiant.
     *
     * @param etudiantDTO the entity to save
     * @return the persisted entity
     */
    EtudiantDTO save(EtudiantDTO etudiantDTO);

    /**
     *  Get all the etudiants.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<EtudiantDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" etudiant.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    EtudiantDTO findOne(Long id);

    /**
     *  Delete the "id" etudiant.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the etudiant corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<EtudiantDTO> search(String query, Pageable pageable);
}
