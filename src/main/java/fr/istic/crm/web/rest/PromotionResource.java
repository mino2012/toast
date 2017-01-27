package fr.istic.crm.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.istic.crm.service.PromotionService;
import fr.istic.crm.web.rest.util.HeaderUtil;
import fr.istic.crm.web.rest.util.PaginationUtil;
import fr.istic.crm.service.dto.PromotionDTO;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Promotion.
 */
@RestController
@RequestMapping("/api")
public class PromotionResource {

    private final Logger log = LoggerFactory.getLogger(PromotionResource.class);
        
    @Inject
    private PromotionService promotionService;

    /**
     * POST  /promotions : Create a new promotion.
     *
     * @param promotionDTO the promotionDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new promotionDTO, or with status 400 (Bad Request) if the promotion has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/promotions")
    @Timed
    public ResponseEntity<PromotionDTO> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO) throws URISyntaxException {
        log.debug("REST request to save Promotion : {}", promotionDTO);
        if (promotionDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("promotion", "idexists", "A new promotion cannot already have an ID")).body(null);
        }
        PromotionDTO result = promotionService.save(promotionDTO);
        return ResponseEntity.created(new URI("/api/promotions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("promotion", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /promotions : Updates an existing promotion.
     *
     * @param promotionDTO the promotionDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated promotionDTO,
     * or with status 400 (Bad Request) if the promotionDTO is not valid,
     * or with status 500 (Internal Server Error) if the promotionDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/promotions")
    @Timed
    public ResponseEntity<PromotionDTO> updatePromotion(@Valid @RequestBody PromotionDTO promotionDTO) throws URISyntaxException {
        log.debug("REST request to update Promotion : {}", promotionDTO);
        if (promotionDTO.getId() == null) {
            return createPromotion(promotionDTO);
        }
        PromotionDTO result = promotionService.save(promotionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("promotion", promotionDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /promotions : get all the promotions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of promotions in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/promotions")
    @Timed
    public ResponseEntity<List<PromotionDTO>> getAllPromotions(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Promotions");
        Page<PromotionDTO> page = promotionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/promotions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /promotions/:id : get the "id" promotion.
     *
     * @param id the id of the promotionDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the promotionDTO, or with status 404 (Not Found)
     */
    @GetMapping("/promotions/{id}")
    @Timed
    public ResponseEntity<PromotionDTO> getPromotion(@PathVariable Long id) {
        log.debug("REST request to get Promotion : {}", id);
        PromotionDTO promotionDTO = promotionService.findOne(id);
        return Optional.ofNullable(promotionDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /promotions/:id : delete the "id" promotion.
     *
     * @param id the id of the promotionDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/promotions/{id}")
    @Timed
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        log.debug("REST request to delete Promotion : {}", id);
        promotionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("promotion", id.toString())).build();
    }

    /**
     * SEARCH  /_search/promotions?query=:query : search for the promotion corresponding
     * to the query.
     *
     * @param query the query of the promotion search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/promotions")
    @Timed
    public ResponseEntity<List<PromotionDTO>> searchPromotions(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Promotions for query {}", query);
        Page<PromotionDTO> page = promotionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/promotions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
