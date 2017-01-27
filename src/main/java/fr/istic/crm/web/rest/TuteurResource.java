package fr.istic.crm.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.istic.crm.service.TuteurService;
import fr.istic.crm.web.rest.util.HeaderUtil;
import fr.istic.crm.web.rest.util.PaginationUtil;
import fr.istic.crm.service.dto.TuteurDTO;

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
 * REST controller for managing Tuteur.
 */
@RestController
@RequestMapping("/api")
public class TuteurResource {

    private final Logger log = LoggerFactory.getLogger(TuteurResource.class);
        
    @Inject
    private TuteurService tuteurService;

    /**
     * POST  /tuteurs : Create a new tuteur.
     *
     * @param tuteurDTO the tuteurDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tuteurDTO, or with status 400 (Bad Request) if the tuteur has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tuteurs")
    @Timed
    public ResponseEntity<TuteurDTO> createTuteur(@RequestBody TuteurDTO tuteurDTO) throws URISyntaxException {
        log.debug("REST request to save Tuteur : {}", tuteurDTO);
        if (tuteurDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("tuteur", "idexists", "A new tuteur cannot already have an ID")).body(null);
        }
        TuteurDTO result = tuteurService.save(tuteurDTO);
        return ResponseEntity.created(new URI("/api/tuteurs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("tuteur", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tuteurs : Updates an existing tuteur.
     *
     * @param tuteurDTO the tuteurDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tuteurDTO,
     * or with status 400 (Bad Request) if the tuteurDTO is not valid,
     * or with status 500 (Internal Server Error) if the tuteurDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tuteurs")
    @Timed
    public ResponseEntity<TuteurDTO> updateTuteur(@RequestBody TuteurDTO tuteurDTO) throws URISyntaxException {
        log.debug("REST request to update Tuteur : {}", tuteurDTO);
        if (tuteurDTO.getId() == null) {
            return createTuteur(tuteurDTO);
        }
        TuteurDTO result = tuteurService.save(tuteurDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("tuteur", tuteurDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tuteurs : get all the tuteurs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of tuteurs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/tuteurs")
    @Timed
    public ResponseEntity<List<TuteurDTO>> getAllTuteurs(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Tuteurs");
        Page<TuteurDTO> page = tuteurService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tuteurs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tuteurs/:id : get the "id" tuteur.
     *
     * @param id the id of the tuteurDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tuteurDTO, or with status 404 (Not Found)
     */
    @GetMapping("/tuteurs/{id}")
    @Timed
    public ResponseEntity<TuteurDTO> getTuteur(@PathVariable Long id) {
        log.debug("REST request to get Tuteur : {}", id);
        TuteurDTO tuteurDTO = tuteurService.findOne(id);
        return Optional.ofNullable(tuteurDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tuteurs/:id : delete the "id" tuteur.
     *
     * @param id the id of the tuteurDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tuteurs/{id}")
    @Timed
    public ResponseEntity<Void> deleteTuteur(@PathVariable Long id) {
        log.debug("REST request to delete Tuteur : {}", id);
        tuteurService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("tuteur", id.toString())).build();
    }

    /**
     * SEARCH  /_search/tuteurs?query=:query : search for the tuteur corresponding
     * to the query.
     *
     * @param query the query of the tuteur search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/tuteurs")
    @Timed
    public ResponseEntity<List<TuteurDTO>> searchTuteurs(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Tuteurs for query {}", query);
        Page<TuteurDTO> page = tuteurService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/tuteurs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
