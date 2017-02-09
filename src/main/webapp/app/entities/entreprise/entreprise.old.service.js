(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('EntrepriseOld', EntrepriseOld);

    EntrepriseOld.$inject = ['$resource'];

    function EntrepriseOld ($resource) {
        var resourceUrl =  'api/entreprisesOld/:id';

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
