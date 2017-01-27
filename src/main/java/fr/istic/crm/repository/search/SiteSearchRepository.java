package fr.istic.crm.repository.search;

import fr.istic.crm.domain.Site;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Site entity.
 */
public interface SiteSearchRepository extends ElasticsearchRepository<Site, Long> {
}
