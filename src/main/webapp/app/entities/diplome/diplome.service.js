(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('Diplome', Diplome);

    Diplome.$inject = ['$resource'];

    function Diplome ($resource) {
        var resourceUrl =  'api/diplomes/:id';

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
