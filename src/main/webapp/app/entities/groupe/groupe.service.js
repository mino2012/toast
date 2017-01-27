(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('Groupe', Groupe);

    Groupe.$inject = ['$resource'];

    function Groupe ($resource) {
        var resourceUrl =  'api/groupes/:id';

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
