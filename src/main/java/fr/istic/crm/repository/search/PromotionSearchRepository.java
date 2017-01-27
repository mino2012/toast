package fr.istic.crm.repository.search;

import fr.istic.crm.domain.Promotion;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Promotion entity.
 */
public interface PromotionSearchRepository extends ElasticsearchRepository<Promotion, Long> {
}
