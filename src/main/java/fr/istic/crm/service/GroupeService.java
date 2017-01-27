package fr.istic.crm.service;

import fr.istic.crm.service.dto.GroupeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing Groupe.
 */
public interface GroupeService {

    /**
     * Save a groupe.
     *
     * @param groupeDTO the entity to save
     * @return the persisted entity
     */
    GroupeDTO save(GroupeDTO groupeDTO);

    /**
     *  Get all the groupes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<GroupeDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" groupe.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    GroupeDTO findOne(Long id);

    /**
     *  Delete the "id" groupe.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the groupe corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<GroupeDTO> search(String query, Pageable pageable);
}
