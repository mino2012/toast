(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('Filiere', Filiere);

    Filiere.$inject = ['$resource'];

    function Filiere ($resource) {
        var resourceUrl =  'api/filieres/:id';

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
