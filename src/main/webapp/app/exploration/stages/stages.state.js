(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('stages', {
                parent: 'exploration',
                url: '/exploration/stages',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'crmisticApp.stages.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/exploration/stages/stages.html',
                        controller: 'ConventionStageController',
                        controllerAs: 'vm'
                    }
                },
                params: {
                    page: {
                        value: '1',
                        squash: true
                    },
                    sort: {
                        value: 'id,asc',
                        squash: true
                    },
                    search: null
                },
                resolve: {
                    pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                        return {
                            page: PaginationUtil.parsePage($stateParams.page),
                            sort: $stateParams.sort,
                            predicate: PaginationUtil.parsePredicate($stateParams.sort),
                            ascending: PaginationUtil.parseAscending($stateParams.sort),
                            search: $stateParams.search
                        };
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stages');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('stages-detail', {
                parent: 'exploration',
                url: 'exploration/stages',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'crmisticApp.stages.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/exploration/stages/stages.html',
                        controller: 'ConventionStageController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stages');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Stages', function($stateParams, Stages) {
                        return Stages.get({id : $stateParams.id}).$promise;
                    }],
                    previousState: ["$state", function ($state) {
                        var currentStateData = {
                            name: $state.current.name || 'stages',
                            params: $state.params,
                            url: $state.href($state.current.name, $state.params)
                        };
                        return currentStateData;
                    }]
                }
            })

    }

})();
