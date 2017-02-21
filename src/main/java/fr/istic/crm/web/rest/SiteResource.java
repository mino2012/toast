package fr.istic.crm.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.istic.crm.service.SiteService;
import fr.istic.crm.web.rest.util.HeaderUtil;
import fr.istic.crm.web.rest.util.PaginationUtil;
import fr.istic.crm.service.dto.SiteDTO;

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
 * REST controller for managing Site.
 */
@RestController
@RequestMapping("/api")
public class SiteResource {

    private final Logger log = LoggerFactory.getLogger(SiteResource.class);

    @Inject
    private SiteService siteService;

    /**
     * POST  /sites : Create a new site.
     *
     * @param siteDTO the siteDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new siteDTO, or with status 400 (Bad Request) if the site has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/sites")
    @Timed
    public ResponseEntity<SiteDTO> createSite(@RequestBody SiteDTO siteDTO) throws URISyntaxException {
        log.debug("REST request to save Site : {}", siteDTO);
        if (siteDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("site", "idexists", "A new site cannot already have an ID")).body(null);
        }
        SiteDTO result = siteService.save(siteDTO);
        return ResponseEntity.created(new URI("/api/sites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("site", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sites : Updates an existing site.
     *
     * @param siteDTO the siteDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated siteDTO,
     * or with status 400 (Bad Request) if the siteDTO is not valid,
     * or with status 500 (Internal Server Error) if the siteDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/sites")
    @Timed
    public ResponseEntity<SiteDTO> updateSite(@RequestBody SiteDTO siteDTO) throws URISyntaxException {
        log.debug("REST request to update Site : {}", siteDTO);
        if (siteDTO.getId() == null) {
            return createSite(siteDTO);
        }
        SiteDTO result = siteService.save(siteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("site", siteDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sites : get all the sites.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of sites in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/sites")
    @Timed
    public ResponseEntity<List<SiteDTO>> getAllSites(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Sites");
        Page<SiteDTO> page = siteService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sites");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /sites/:id : get the "id" site.
     *
     * @param id the id of the siteDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the siteDTO, or with status 404 (Not Found)
     */
    @GetMapping("/sites/{id}")
    @Timed
    public ResponseEntity<SiteDTO> getSite(@PathVariable Long id) {
        log.debug("REST request to get Site : {}", id);
        SiteDTO siteDTO = siteService.findOne(id);
        return Optional.ofNullable(siteDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sites/:id : delete the "id" site.
     *
     * @param id the id of the siteDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/sites/{id}")
    @Timed
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        log.debug("REST request to delete Site : {}", id);
        siteService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("site", id.toString())).build();
    }

    /**
     * SEARCH  /_search/sites?query=:query : search for the site corresponding
     * to the query.
     *
     * @param query the query of the site search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/sites")
    @Timed
    public ResponseEntity<List<SiteDTO>> searchSites(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Sites for query {}", query);
        Page<SiteDTO> page = siteService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/sites");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /siteCreationStage/ : get site version at the conventionStage creation.
     *
     * @return the ResponseEntity with status 200 (OK) and site entity in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/siteCreationStage/")
    @Timed
    public ResponseEntity<Object> getSiteAtStageCreation(@RequestParam Long id) {
        log.debug("REST request to get Site : {}", id);
        Object site = siteService.findSiteAtCreationStage(id);
        return Optional.ofNullable(site)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}
