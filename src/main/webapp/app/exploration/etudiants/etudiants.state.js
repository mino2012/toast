(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('etudiants', {
            parent: 'exploration',
            url: '/exploration/etudiants',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.etudiants.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/exploration/etudiants/etudiants.html',
                    controller: 'EtudiantsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('etudiants');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })


    }

})();
