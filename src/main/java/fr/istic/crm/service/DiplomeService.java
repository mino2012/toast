package fr.istic.crm.service;

import fr.istic.crm.service.dto.DiplomeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing Diplome.
 */
public interface DiplomeService {

    /**
     * Save a diplome.
     *
     * @param diplomeDTO the entity to save
     * @return the persisted entity
     */
    DiplomeDTO save(DiplomeDTO diplomeDTO);

    /**
     *  Get all the diplomes.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<DiplomeDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" diplome.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    DiplomeDTO findOne(Long id);

    /**
     *  Get old version of diplome.
     *
     *  @param id the id of the entity
     *  @return list of old version
     */
    List findAnciennesVersions(Long id);

    /**
     *  Delete the "id" diplome.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the diplome corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<DiplomeDTO> search(String query, Pageable pageable);
}
