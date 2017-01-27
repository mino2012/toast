(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('Professionnel', Professionnel);

    Professionnel.$inject = ['$resource'];

    function Professionnel ($resource) {
        var resourceUrl =  'api/professionnels/:id';

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
