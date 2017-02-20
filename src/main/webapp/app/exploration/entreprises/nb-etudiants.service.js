(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('NbEtudiants', NbEtudiants);

    NbEtudiants.$inject = ['$resource', 'DateUtils'];

    function NbEtudiants ($resource, DateUtils) {
        var resourceUrl =  'api/nb-etudiants';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
