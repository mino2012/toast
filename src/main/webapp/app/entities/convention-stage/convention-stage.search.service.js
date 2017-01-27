(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .factory('ConventionStageSearch', ConventionStageSearch);

    ConventionStageSearch.$inject = ['$resource'];

    function ConventionStageSearch($resource) {
        var resourceUrl =  'api/_search/convention-stages/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
