(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .factory('TaxeSearch', TaxeSearch);

    TaxeSearch.$inject = ['$resource'];

    function TaxeSearch($resource) {
        var resourceUrl =  'api/_search/taxes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
