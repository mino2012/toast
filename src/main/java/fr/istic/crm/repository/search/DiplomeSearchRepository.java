package fr.istic.crm.repository.search;

import fr.istic.crm.domain.Diplome;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Diplome entity.
 */
public interface DiplomeSearchRepository extends ElasticsearchRepository<Diplome, Long> {
}
