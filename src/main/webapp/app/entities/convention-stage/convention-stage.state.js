(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('convention-stage', {
            parent: 'entity',
            url: '/convention-stage?page&sort&search',
            data: {
                authorities: ['ROLE_USER','ROLE_ADMIN'],
                pageTitle: 'crmisticApp.conventionStage.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/convention-stage/convention-stages.html',
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
                    $translatePartialLoader.addPart('conventionStage');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('convention-stage-detail', {
            parent: 'entity',
            url: '/convention-stage/{id}',
            data: {
                authorities: ['ROLE_USER','ROLE_ADMIN'],
                pageTitle: 'crmisticApp.conventionStage.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/convention-stage/convention-stage-detail.html',
                    controller: 'ConventionStageDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('conventionStage');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ConventionStage', function($stateParams, ConventionStage) {
                    return ConventionStage.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'convention-stage',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('convention-stage-detail.edit', {
            parent: 'convention-stage-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/convention-stage/convention-stage-dialog.html',
                    controller: 'ConventionStageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ConventionStage', function(ConventionStage) {
                            return ConventionStage.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('convention-stage.new', {
            parent: 'convention-stage',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/convention-stage/convention-stage-dialog.html',
                    controller: 'ConventionStageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                sujet: null,
                                fonctions: null,
                                competences: null,
                                dateDebut: null,
                                dateFin: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('convention-stage', null, { reload: 'convention-stage' });
                }, function() {
                    $state.go('convention-stage');
                });
            }]
        })
        .state('convention-stage.edit', {
            parent: 'convention-stage',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/convention-stage/convention-stage-dialog.html',
                    controller: 'ConventionStageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ConventionStage', function(ConventionStage) {
                            return ConventionStage.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('convention-stage', null, { reload: 'convention-stage' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('convention-stage.delete', {
            parent: 'convention-stage',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/convention-stage/convention-stage-delete-dialog.html',
                    controller: 'ConventionStageDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ConventionStage', function(ConventionStage) {
                            return ConventionStage.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('convention-stage', null, { reload: 'convention-stage' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
