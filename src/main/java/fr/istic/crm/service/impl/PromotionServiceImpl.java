package fr.istic.crm.service.impl;

import fr.istic.crm.service.PromotionService;
import fr.istic.crm.domain.Promotion;
import fr.istic.crm.repository.PromotionRepository;
import fr.istic.crm.repository.search.PromotionSearchRepository;
import fr.istic.crm.service.dto.PromotionDTO;
import fr.istic.crm.service.mapper.PromotionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Promotion.
 */
@Service
@Transactional
public class PromotionServiceImpl implements PromotionService{

    private final Logger log = LoggerFactory.getLogger(PromotionServiceImpl.class);
    
    @Inject
    private PromotionRepository promotionRepository;

    @Inject
    private PromotionMapper promotionMapper;

    @Inject
    private PromotionSearchRepository promotionSearchRepository;

    /**
     * Save a promotion.
     *
     * @param promotionDTO the entity to save
     * @return the persisted entity
     */
    public PromotionDTO save(PromotionDTO promotionDTO) {
        log.debug("Request to save Promotion : {}", promotionDTO);
        Promotion promotion = promotionMapper.promotionDTOToPromotion(promotionDTO);
        promotion = promotionRepository.save(promotion);
        PromotionDTO result = promotionMapper.promotionToPromotionDTO(promotion);
        promotionSearchRepository.save(promotion);
        return result;
    }

    /**
     *  Get all the promotions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<PromotionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Promotions");
        Page<Promotion> result = promotionRepository.findAll(pageable);
        return result.map(promotion -> promotionMapper.promotionToPromotionDTO(promotion));
    }

    /**
     *  Get one promotion by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public PromotionDTO findOne(Long id) {
        log.debug("Request to get Promotion : {}", id);
        Promotion promotion = promotionRepository.findOne(id);
        PromotionDTO promotionDTO = promotionMapper.promotionToPromotionDTO(promotion);
        return promotionDTO;
    }

    /**
     *  Delete the  promotion by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Promotion : {}", id);
        promotionRepository.delete(id);
        promotionSearchRepository.delete(id);
    }

    /**
     * Search for the promotion corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PromotionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Promotions for query {}", query);
        Page<Promotion> result = promotionSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(promotion -> promotionMapper.promotionToPromotionDTO(promotion));
    }
}
