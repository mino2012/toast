package fr.istic.crm.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.istic.crm.service.ConventionStageService;
import fr.istic.crm.web.rest.util.HeaderUtil;
import fr.istic.crm.web.rest.util.PaginationUtil;
import fr.istic.crm.service.dto.ConventionStageDTO;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ConventionStage.
 */
@RestController
@RequestMapping("/api")
public class ConventionStageResource {

    private final Logger log = LoggerFactory.getLogger(ConventionStageResource.class);
        
    @Inject
    private ConventionStageService conventionStageService;

    /**
     * POST  /convention-stages : Create a new conventionStage.
     *
     * @param conventionStageDTO the conventionStageDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new conventionStageDTO, or with status 400 (Bad Request) if the conventionStage has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/convention-stages")
    @Timed
    public ResponseEntity<ConventionStageDTO> createConventionStage(@RequestBody ConventionStageDTO conventionStageDTO) throws URISyntaxException {
        log.debug("REST request to save ConventionStage : {}", conventionStageDTO);
        if (conventionStageDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("conventionStage", "idexists", "A new conventionStage cannot already have an ID")).body(null);
        }
        ConventionStageDTO result = conventionStageService.save(conventionStageDTO);
        return ResponseEntity.created(new URI("/api/convention-stages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("conventionStage", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /convention-stages : Updates an existing conventionStage.
     *
     * @param conventionStageDTO the conventionStageDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated conventionStageDTO,
     * or with status 400 (Bad Request) if the conventionStageDTO is not valid,
     * or with status 500 (Internal Server Error) if the conventionStageDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/convention-stages")
    @Timed
    public ResponseEntity<ConventionStageDTO> updateConventionStage(@RequestBody ConventionStageDTO conventionStageDTO) throws URISyntaxException {
        log.debug("REST request to update ConventionStage : {}", conventionStageDTO);
        if (conventionStageDTO.getId() == null) {
            return createConventionStage(conventionStageDTO);
        }
        ConventionStageDTO result = conventionStageService.save(conventionStageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("conventionStage", conventionStageDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /convention-stages : get all the conventionStages.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of conventionStages in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/convention-stages")
    @Timed
    public ResponseEntity<List<ConventionStageDTO>> getAllConventionStages(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ConventionStages");
        Page<ConventionStageDTO> page = conventionStageService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/convention-stages");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /convention-stages/:id : get the "id" conventionStage.
     *
     * @param id the id of the conventionStageDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the conventionStageDTO, or with status 404 (Not Found)
     */
    @GetMapping("/convention-stages/{id}")
    @Timed
    public ResponseEntity<ConventionStageDTO> getConventionStage(@PathVariable Long id) {
        log.debug("REST request to get ConventionStage : {}", id);
        ConventionStageDTO conventionStageDTO = conventionStageService.findOne(id);
        return Optional.ofNullable(conventionStageDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /convention-stages/:id : delete the "id" conventionStage.
     *
     * @param id the id of the conventionStageDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/convention-stages/{id}")
    @Timed
    public ResponseEntity<Void> deleteConventionStage(@PathVariable Long id) {
        log.debug("REST request to delete ConventionStage : {}", id);
        conventionStageService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("conventionStage", id.toString())).build();
    }

    /**
     * SEARCH  /_search/convention-stages?query=:query : search for the conventionStage corresponding
     * to the query.
     *
     * @param query the query of the conventionStage search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/convention-stages")
    @Timed
    public ResponseEntity<List<ConventionStageDTO>> searchConventionStages(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ConventionStages for query {}", query);
        Page<ConventionStageDTO> page = conventionStageService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/convention-stages");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
