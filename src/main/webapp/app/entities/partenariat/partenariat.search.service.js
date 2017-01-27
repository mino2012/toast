(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .factory('PartenariatSearch', PartenariatSearch);

    PartenariatSearch.$inject = ['$resource'];

    function PartenariatSearch($resource) {
        var resourceUrl =  'api/_search/partenariats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
