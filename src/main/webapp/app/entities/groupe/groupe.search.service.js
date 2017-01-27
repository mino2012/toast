(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .factory('GroupeSearch', GroupeSearch);

    GroupeSearch.$inject = ['$resource'];

    function GroupeSearch($resource) {
        var resourceUrl =  'api/_search/groupes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
