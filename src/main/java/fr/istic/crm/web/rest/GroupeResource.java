package fr.istic.crm.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.istic.crm.service.GroupeService;
import fr.istic.crm.web.rest.util.HeaderUtil;
import fr.istic.crm.web.rest.util.PaginationUtil;
import fr.istic.crm.service.dto.GroupeDTO;

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
 * REST controller for managing Groupe.
 */
@RestController
@RequestMapping("/api")
public class GroupeResource {

    private final Logger log = LoggerFactory.getLogger(GroupeResource.class);
        
    @Inject
    private GroupeService groupeService;

    /**
     * POST  /groupes : Create a new groupe.
     *
     * @param groupeDTO the groupeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new groupeDTO, or with status 400 (Bad Request) if the groupe has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/groupes")
    @Timed
    public ResponseEntity<GroupeDTO> createGroupe(@RequestBody GroupeDTO groupeDTO) throws URISyntaxException {
        log.debug("REST request to save Groupe : {}", groupeDTO);
        if (groupeDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("groupe", "idexists", "A new groupe cannot already have an ID")).body(null);
        }
        GroupeDTO result = groupeService.save(groupeDTO);
        return ResponseEntity.created(new URI("/api/groupes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("groupe", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /groupes : Updates an existing groupe.
     *
     * @param groupeDTO the groupeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated groupeDTO,
     * or with status 400 (Bad Request) if the groupeDTO is not valid,
     * or with status 500 (Internal Server Error) if the groupeDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/groupes")
    @Timed
    public ResponseEntity<GroupeDTO> updateGroupe(@RequestBody GroupeDTO groupeDTO) throws URISyntaxException {
        log.debug("REST request to update Groupe : {}", groupeDTO);
        if (groupeDTO.getId() == null) {
            return createGroupe(groupeDTO);
        }
        GroupeDTO result = groupeService.save(groupeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("groupe", groupeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /groupes : get all the groupes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of groupes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/groupes")
    @Timed
    public ResponseEntity<List<GroupeDTO>> getAllGroupes(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Groupes");
        Page<GroupeDTO> page = groupeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/groupes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /groupes/:id : get the "id" groupe.
     *
     * @param id the id of the groupeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the groupeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/groupes/{id}")
    @Timed
    public ResponseEntity<GroupeDTO> getGroupe(@PathVariable Long id) {
        log.debug("REST request to get Groupe : {}", id);
        GroupeDTO groupeDTO = groupeService.findOne(id);
        return Optional.ofNullable(groupeDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /groupes/:id : delete the "id" groupe.
     *
     * @param id the id of the groupeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/groupes/{id}")
    @Timed
    public ResponseEntity<Void> deleteGroupe(@PathVariable Long id) {
        log.debug("REST request to delete Groupe : {}", id);
        groupeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("groupe", id.toString())).build();
    }

    /**
     * SEARCH  /_search/groupes?query=:query : search for the groupe corresponding
     * to the query.
     *
     * @param query the query of the groupe search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/groupes")
    @Timed
    public ResponseEntity<List<GroupeDTO>> searchGroupes(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Groupes for query {}", query);
        Page<GroupeDTO> page = groupeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/groupes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
