(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('SiteOld', SiteOld);

    SiteOld.$inject = ['$resource'];

    function SiteOld ($resource) {
        var resourceUrl =  'api/sitesOld/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
