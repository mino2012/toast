package fr.istic.crm.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.istic.crm.service.EntrepriseService;
import fr.istic.crm.web.rest.util.HeaderUtil;
import fr.istic.crm.web.rest.util.PaginationUtil;
import fr.istic.crm.service.dto.EntrepriseDTO;

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
 * REST controller for managing Entreprise.
 */
@RestController
@RequestMapping("/api")
public class EntrepriseResource {

    private final Logger log = LoggerFactory.getLogger(EntrepriseResource.class);

    @Inject
    private EntrepriseService entrepriseService;

    /**
     * POST  /entreprises : Create a new entreprise.
     *
     * @param entrepriseDTO the entrepriseDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new entrepriseDTO, or with status 400 (Bad Request) if the entreprise has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/entreprises")
    @Timed
    public ResponseEntity<EntrepriseDTO> createEntreprise(@Valid @RequestBody EntrepriseDTO entrepriseDTO) throws URISyntaxException {
        log.debug("REST request to save Entreprise : {}", entrepriseDTO);
        if (entrepriseDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("entreprise", "idexists", "A new entreprise cannot already have an ID")).body(null);
        }
        EntrepriseDTO result = entrepriseService.save(entrepriseDTO);
        return ResponseEntity.created(new URI("/api/entreprises/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("entreprise", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /entreprises : Updates an existing entreprise.
     *
     * @param entrepriseDTO the entrepriseDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated entrepriseDTO,
     * or with status 400 (Bad Request) if the entrepriseDTO is not valid,
     * or with status 500 (Internal Server Error) if the entrepriseDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/entreprises")
    @Timed
    public ResponseEntity<EntrepriseDTO> updateEntreprise(@Valid @RequestBody EntrepriseDTO entrepriseDTO) throws URISyntaxException {
        log.debug("REST request to update Entreprise : {}", entrepriseDTO);
        if (entrepriseDTO.getId() == null) {
            return createEntreprise(entrepriseDTO);
        }
        EntrepriseDTO result = entrepriseService.save(entrepriseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("entreprise", entrepriseDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /entreprises : get all the entreprises.
     *
     * @param pageable the pagination information
     * @param filter the filter of the request
     * @return the ResponseEntity with status 200 (OK) and the list of entreprises in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/entreprises")
    @Timed
    public ResponseEntity<List<EntrepriseDTO>> getAllEntreprises(@ApiParam Pageable pageable, @RequestParam(required = false) String filter)
        throws URISyntaxException {
        if ("siege-is-null".equals(filter)) {
            log.debug("REST request to get all Entreprises where siege is null");
            return new ResponseEntity<>(entrepriseService.findAllWhereSiegeIsNull(),
                    HttpStatus.OK);
        }
        if ("contact-is-null".equals(filter)) {
            log.debug("REST request to get all Entreprises where contact is null");
            return new ResponseEntity<>(entrepriseService.findAllWhereContactIsNull(),
                    HttpStatus.OK);
        }
        log.debug("REST request to get a page of Entreprises");
        Page<EntrepriseDTO> page = entrepriseService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/entreprises");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /entreprises/:id : get the "id" entreprise.
     *
     * @param id the id of the entrepriseDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the entrepriseDTO, or with status 404 (Not Found)
     */
    @GetMapping("/entreprises/{id}")
    @Timed
    public ResponseEntity<EntrepriseDTO> getEntreprise(@PathVariable Long id) {
        log.debug("REST request to get Entreprise : {}", id);
        EntrepriseDTO entrepriseDTO = entrepriseService.findOne(id);
        return Optional.ofNullable(entrepriseDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /entreprisesOld/:id : get all the entreprises.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of entreprises in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/entreprisesOld/{id}")
    @Timed
    public ResponseEntity<List> getOldEntreprise(@PathVariable Long id)
        throws URISyntaxException {
        return new ResponseEntity<>(entrepriseService.findAnciennesVersions(id), HttpStatus.OK);
    }


    /**
     * DELETE  /entreprises/:id : delete the "id" entreprise.
     *
     * @param id the id of the entrepriseDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/entreprises/{id}")
    @Timed
    public ResponseEntity<Void> deleteEntreprise(@PathVariable Long id) {
        log.debug("REST request to delete Entreprise : {}", id);
        entrepriseService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("entreprise", id.toString())).build();
    }

    /**
     * SEARCH  /_search/entreprises?query=:query : search for the entreprise corresponding
     * to the query.
     *
     * @param query the query of the entreprise search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/entreprises")
    @Timed
    public ResponseEntity<List<EntrepriseDTO>> searchEntreprises(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Entreprises for query {}", query);
        Page<EntrepriseDTO> page = entrepriseService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/entreprises");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /entrepriseCreationStage/ : get entreprise version at the conventionStage creation.
     *
     * @return the ResponseEntity with status 200 (OK) and entreprise entity in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/entrepriseCreationStage/")
    @Timed
    public ResponseEntity<Object> getEntrepriseAtStageCreation(@RequestParam Long id) {
        log.debug("REST request to get Site : {}", id);
        Object entreprise = entrepriseService.findEntrepriseAtCreationStage(id);
        return Optional.ofNullable(entreprise)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}
