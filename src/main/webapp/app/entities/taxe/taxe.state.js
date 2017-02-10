(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('taxe', {
            parent: 'entity',
            url: '/taxe?page&sort&search',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.taxe.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/taxe/taxes.html',
                    controller: 'TaxeController',
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
                    $translatePartialLoader.addPart('taxe');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('taxe-detail', {
            parent: 'entity',
            url: '/taxe/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.taxe.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/taxe/taxe-detail.html',
                    controller: 'TaxeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('taxe');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Taxe', function($stateParams, Taxe) {
                    return Taxe.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'taxe',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('taxe-detail.edit', {
            parent: 'taxe-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/taxe/taxe-dialog.html',
                    controller: 'TaxeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Taxe', function(Taxe) {
                            return Taxe.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('taxe.new', {
            parent: 'taxe',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/taxe/taxe-dialog.html',
                    controller: 'TaxeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                montant: null,
                                annee: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('taxe', null, { reload: 'taxe' });
                }, function() {
                    $state.go('taxe');
                });
            }]
        })
        .state('taxe.edit', {
            parent: 'taxe',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/taxe/taxe-dialog.html',
                    controller: 'TaxeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Taxe', function(Taxe) {
                            return Taxe.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('taxe', null, { reload: 'taxe' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('taxe.delete', {
            parent: 'taxe',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/taxe/taxe-delete-dialog.html',
                    controller: 'TaxeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Taxe', function(Taxe) {
                            return Taxe.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('taxe', null, { reload: 'taxe' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
