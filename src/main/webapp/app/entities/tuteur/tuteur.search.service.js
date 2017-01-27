(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .factory('TuteurSearch', TuteurSearch);

    TuteurSearch.$inject = ['$resource'];

    function TuteurSearch($resource) {
        var resourceUrl =  'api/_search/tuteurs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
