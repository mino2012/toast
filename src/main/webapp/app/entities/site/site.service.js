(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('Site', Site);

    Site.$inject = ['$resource'];

    function Site ($resource) {
        var resourceUrl =  'api/sites/:id';

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
