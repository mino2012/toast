(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('Taxe', Taxe);

    Taxe.$inject = ['$resource', 'DateUtils'];

    function Taxe ($resource, DateUtils) {
        var resourceUrl =  'api/taxes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.annee = DateUtils.convertDateTimeFromServer(data.annee);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
