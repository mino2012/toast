package fr.istic.crm.service;

import fr.istic.crm.service.dto.PromotionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing Promotion.
 */
public interface PromotionService {

    /**
     * Save a promotion.
     *
     * @param promotionDTO the entity to save
     * @return the persisted entity
     */
    PromotionDTO save(PromotionDTO promotionDTO);

    /**
     *  Get all the promotions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<PromotionDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" promotion.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    PromotionDTO findOne(Long id);

    /**
     *  Delete the "id" promotion.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the promotion corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<PromotionDTO> search(String query, Pageable pageable);
}
