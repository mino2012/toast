(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('Entreprise', Entreprise);

    Entreprise.$inject = ['$resource'];

    function Entreprise ($resource) {
        var resourceUrl =  'api/entreprises/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
