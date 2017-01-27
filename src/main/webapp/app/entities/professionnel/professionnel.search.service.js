(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .factory('ProfessionnelSearch', ProfessionnelSearch);

    ProfessionnelSearch.$inject = ['$resource'];

    function ProfessionnelSearch($resource) {
        var resourceUrl =  'api/_search/professionnels/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
