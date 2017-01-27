package fr.istic.crm.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.istic.crm.service.ProfessionnelService;
import fr.istic.crm.web.rest.util.HeaderUtil;
import fr.istic.crm.web.rest.util.PaginationUtil;
import fr.istic.crm.service.dto.ProfessionnelDTO;

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
 * REST controller for managing Professionnel.
 */
@RestController
@RequestMapping("/api")
public class ProfessionnelResource {

    private final Logger log = LoggerFactory.getLogger(ProfessionnelResource.class);
        
    @Inject
    private ProfessionnelService professionnelService;

    /**
     * POST  /professionnels : Create a new professionnel.
     *
     * @param professionnelDTO the professionnelDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new professionnelDTO, or with status 400 (Bad Request) if the professionnel has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/professionnels")
    @Timed
    public ResponseEntity<ProfessionnelDTO> createProfessionnel(@RequestBody ProfessionnelDTO professionnelDTO) throws URISyntaxException {
        log.debug("REST request to save Professionnel : {}", professionnelDTO);
        if (professionnelDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("professionnel", "idexists", "A new professionnel cannot already have an ID")).body(null);
        }
        ProfessionnelDTO result = professionnelService.save(professionnelDTO);
        return ResponseEntity.created(new URI("/api/professionnels/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("professionnel", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /professionnels : Updates an existing professionnel.
     *
     * @param professionnelDTO the professionnelDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated professionnelDTO,
     * or with status 400 (Bad Request) if the professionnelDTO is not valid,
     * or with status 500 (Internal Server Error) if the professionnelDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/professionnels")
    @Timed
    public ResponseEntity<ProfessionnelDTO> updateProfessionnel(@RequestBody ProfessionnelDTO professionnelDTO) throws URISyntaxException {
        log.debug("REST request to update Professionnel : {}", professionnelDTO);
        if (professionnelDTO.getId() == null) {
            return createProfessionnel(professionnelDTO);
        }
        ProfessionnelDTO result = professionnelService.save(professionnelDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("professionnel", professionnelDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /professionnels : get all the professionnels.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of professionnels in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/professionnels")
    @Timed
    public ResponseEntity<List<ProfessionnelDTO>> getAllProfessionnels(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Professionnels");
        Page<ProfessionnelDTO> page = professionnelService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/professionnels");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /professionnels/:id : get the "id" professionnel.
     *
     * @param id the id of the professionnelDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the professionnelDTO, or with status 404 (Not Found)
     */
    @GetMapping("/professionnels/{id}")
    @Timed
    public ResponseEntity<ProfessionnelDTO> getProfessionnel(@PathVariable Long id) {
        log.debug("REST request to get Professionnel : {}", id);
        ProfessionnelDTO professionnelDTO = professionnelService.findOne(id);
        return Optional.ofNullable(professionnelDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /professionnels/:id : delete the "id" professionnel.
     *
     * @param id the id of the professionnelDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/professionnels/{id}")
    @Timed
    public ResponseEntity<Void> deleteProfessionnel(@PathVariable Long id) {
        log.debug("REST request to delete Professionnel : {}", id);
        professionnelService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("professionnel", id.toString())).build();
    }

    /**
     * SEARCH  /_search/professionnels?query=:query : search for the professionnel corresponding
     * to the query.
     *
     * @param query the query of the professionnel search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/professionnels")
    @Timed
    public ResponseEntity<List<ProfessionnelDTO>> searchProfessionnels(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Professionnels for query {}", query);
        Page<ProfessionnelDTO> page = professionnelService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/professionnels");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
