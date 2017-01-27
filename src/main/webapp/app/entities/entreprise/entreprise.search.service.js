(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .factory('EntrepriseSearch', EntrepriseSearch);

    EntrepriseSearch.$inject = ['$resource'];

    function EntrepriseSearch($resource) {
        var resourceUrl =  'api/_search/entreprises/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
