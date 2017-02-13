(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('entreprises', {
            parent: 'exploration',
            url: '/exploration/entreprises',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.entreprises.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/exploration/entreprises/entreprises.html',
                    controller: 'EntreprisesController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('entreprises');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })


    }

})();
