package fr.istic.crm.service.impl;

import fr.istic.crm.domain.ConventionStage;
import fr.istic.crm.repository.ConventionStageRepository;
import fr.istic.crm.service.EntrepriseService;
import fr.istic.crm.domain.Entreprise;
import fr.istic.crm.repository.EntrepriseRepository;
import fr.istic.crm.repository.search.EntrepriseSearchRepository;
import fr.istic.crm.service.dto.EntrepriseDTO;
import fr.istic.crm.service.mapper.EntrepriseMapper;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Entreprise.
 */
@Service
@Transactional
public class EntrepriseServiceImpl implements EntrepriseService{

    private final Logger log = LoggerFactory.getLogger(EntrepriseServiceImpl.class);

    @Inject
    private EntrepriseRepository entrepriseRepository;

    @Inject
    private ConventionStageRepository conventionStageRepository;

    @Inject
    private EntityManager manager;

    @Inject
    private EntrepriseMapper entrepriseMapper;

    @Inject
    private EntrepriseSearchRepository entrepriseSearchRepository;

    AuditReader reader;

    void init(){
            reader = AuditReaderFactory.get(manager);
    }


    /**
     * Save a entreprise.
     *
     * @param entrepriseDTO the entity to save
     * @return the persisted entity
     */
    public EntrepriseDTO save(EntrepriseDTO entrepriseDTO) {
        log.debug("Request to save Entreprise : {}", entrepriseDTO);
        Entreprise entreprise = entrepriseMapper.entrepriseDTOToEntreprise(entrepriseDTO);
        entreprise = entrepriseRepository.save(entreprise);
        EntrepriseDTO result = entrepriseMapper.entrepriseToEntrepriseDTO(entreprise);
        entrepriseSearchRepository.save(entreprise);
        return result;
    }

    /**
     *  Get all the entreprises.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<EntrepriseDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Entreprises "  + (manager==null));
        Page<Entreprise> result = entrepriseRepository.findAll(pageable);
        return result.map(entreprise -> entrepriseMapper.entrepriseToEntrepriseDTO(entreprise));
    }


    /**
     *  get all the entreprises where Siege is null.
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<EntrepriseDTO> findAllWhereSiegeIsNull() {
        log.debug("Request to get all entreprises where Siege is null");
        return StreamSupport
            .stream(entrepriseRepository.findAll().spliterator(), false)
            .filter(entreprise -> entreprise.getSiege() == null)
            .map(entrepriseMapper::entrepriseToEntrepriseDTO)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     *  get all the entreprises where Contact is null.
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<EntrepriseDTO> findAllWhereContactIsNull() {
        log.debug("Request to get all entreprises where Contact is null");
        return StreamSupport
            .stream(entrepriseRepository.findAll().spliterator(), false)
            .filter(entreprise -> entreprise.getContact() == null)
            .map(entrepriseMapper::entrepriseToEntrepriseDTO)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get one entreprise by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public EntrepriseDTO findOne(Long id) {
        log.debug("Request to get Entreprise : {}", id);
        Entreprise entreprise = entrepriseRepository.findOne(id);
        EntrepriseDTO entrepriseDTO = entrepriseMapper.entrepriseToEntrepriseDTO(entreprise);
        return entrepriseDTO;
    }

    /**
     *  Get old Entreprise versions by id.
     *
     *  @param id the id of the entity
     *  @return the list of old version
     */
    @Transactional(readOnly = true)
    public List findAnciennesVersions(Long id) {
        init();
        log.debug("Request to get Entreprise : {}", id);

        List anciennesVersions = reader.createQuery()
            .forRevisionsOfEntity(Entreprise.class, false, true)
            .add(AuditEntity.id().eq(id))
            .getResultList();

        log.debug("OLD VERSION" + anciennesVersions);

        return anciennesVersions;
    }


    /**
     *  Delete the  entreprise by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Entreprise : {}", id);
        entrepriseRepository.delete(id);
        entrepriseSearchRepository.delete(id);
    }

    /**
     * Search for the entreprise corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<EntrepriseDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Entreprises for query {}", query);
        Page<Entreprise> result = entrepriseSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(entreprise -> entrepriseMapper.entrepriseToEntrepriseDTO(entreprise));
    }

    /**
     *  Get Entreprise version at the start of stage
     *
     *  @param id the id of the conventionStage
     *  @return the revision of Entreprise
     */
    @Transactional(readOnly = true)
    public Object findEntrepriseAtCreationStage(Long id) {
        log.debug("Request to get ConventionStage : {}", id);
        ConventionStage stage = conventionStageRepository.findOne(id);
        if (stage.getDateDebut() != null) {
            Instant instant = stage.getDateDebut().toInstant();
            Date dateDebutStage = java.util.Date.from(instant);
            reader = AuditReaderFactory.get(manager);
            Object revision = reader.createQuery()
                .forRevisionsOfEntity(Entreprise.class, false, true)
                // We are only interested in the first revision
                .add(AuditEntity.id().eq(stage.getLieuStage().getEntrepriseSite().getId()))
                .add(AuditEntity.property("dateModification").le(dateDebutStage.getTime()))
                .addOrder(AuditEntity.property("dateModification").desc())
                .getResultList();
            return revision;
        } else {
            log.error("CONVENTION : Date de début de stage indisponible");
            return null;
        }
    }
}
