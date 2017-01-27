package fr.istic.crm.repository.search;

import fr.istic.crm.domain.Taxe;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Taxe entity.
 */
public interface TaxeSearchRepository extends ElasticsearchRepository<Taxe, Long> {
}
