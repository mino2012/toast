(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('DiplomeOld', DiplomeOld);

    DiplomeOld.$inject = ['$resource'];

    function DiplomeOld ($resource) {
        var resourceUrl =  'api/diplomesOld/:id';

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
        });
    }
})();
