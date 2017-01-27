(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .factory('DiplomeSearch', DiplomeSearch);

    DiplomeSearch.$inject = ['$resource'];

    function DiplomeSearch($resource) {
        var resourceUrl =  'api/_search/diplomes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
