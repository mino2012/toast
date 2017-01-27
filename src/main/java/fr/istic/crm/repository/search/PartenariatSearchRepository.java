package fr.istic.crm.repository.search;

import fr.istic.crm.domain.Partenariat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Partenariat entity.
 */
public interface PartenariatSearchRepository extends ElasticsearchRepository<Partenariat, Long> {
}
