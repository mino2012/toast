package fr.istic.crm.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.istic.crm.service.TaxeService;
import fr.istic.crm.web.rest.util.HeaderUtil;
import fr.istic.crm.web.rest.util.PaginationUtil;
import fr.istic.crm.service.dto.TaxeDTO;

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
 * REST controller for managing Taxe.
 */
@RestController
@RequestMapping("/api")
public class TaxeResource {

    private final Logger log = LoggerFactory.getLogger(TaxeResource.class);
        
    @Inject
    private TaxeService taxeService;

    /**
     * POST  /taxes : Create a new taxe.
     *
     * @param taxeDTO the taxeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new taxeDTO, or with status 400 (Bad Request) if the taxe has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/taxes")
    @Timed
    public ResponseEntity<TaxeDTO> createTaxe(@RequestBody TaxeDTO taxeDTO) throws URISyntaxException {
        log.debug("REST request to save Taxe : {}", taxeDTO);
        if (taxeDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("taxe", "idexists", "A new taxe cannot already have an ID")).body(null);
        }
        TaxeDTO result = taxeService.save(taxeDTO);
        return ResponseEntity.created(new URI("/api/taxes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("taxe", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /taxes : Updates an existing taxe.
     *
     * @param taxeDTO the taxeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated taxeDTO,
     * or with status 400 (Bad Request) if the taxeDTO is not valid,
     * or with status 500 (Internal Server Error) if the taxeDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/taxes")
    @Timed
    public ResponseEntity<TaxeDTO> updateTaxe(@RequestBody TaxeDTO taxeDTO) throws URISyntaxException {
        log.debug("REST request to update Taxe : {}", taxeDTO);
        if (taxeDTO.getId() == null) {
            return createTaxe(taxeDTO);
        }
        TaxeDTO result = taxeService.save(taxeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("taxe", taxeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /taxes : get all the taxes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of taxes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/taxes")
    @Timed
    public ResponseEntity<List<TaxeDTO>> getAllTaxes(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Taxes");
        Page<TaxeDTO> page = taxeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/taxes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /taxes/:id : get the "id" taxe.
     *
     * @param id the id of the taxeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the taxeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/taxes/{id}")
    @Timed
    public ResponseEntity<TaxeDTO> getTaxe(@PathVariable Long id) {
        log.debug("REST request to get Taxe : {}", id);
        TaxeDTO taxeDTO = taxeService.findOne(id);
        return Optional.ofNullable(taxeDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /taxes/:id : delete the "id" taxe.
     *
     * @param id the id of the taxeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/taxes/{id}")
    @Timed
    public ResponseEntity<Void> deleteTaxe(@PathVariable Long id) {
        log.debug("REST request to delete Taxe : {}", id);
        taxeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("taxe", id.toString())).build();
    }

    /**
     * SEARCH  /_search/taxes?query=:query : search for the taxe corresponding
     * to the query.
     *
     * @param query the query of the taxe search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/taxes")
    @Timed
    public ResponseEntity<List<TaxeDTO>> searchTaxes(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Taxes for query {}", query);
        Page<TaxeDTO> page = taxeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/taxes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
