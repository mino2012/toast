(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .factory('SiteSearch', SiteSearch);

    SiteSearch.$inject = ['$resource'];

    function SiteSearch($resource) {
        var resourceUrl =  'api/_search/sites/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
