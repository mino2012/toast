(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('Promotion', Promotion);

    Promotion.$inject = ['$resource', 'DateUtils'];

    function Promotion ($resource, DateUtils) {
        var resourceUrl =  'api/promotions/:id';

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
