(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('partenariat', {
            parent: 'entity',
            url: '/partenariat?page&sort&search',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.partenariat.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/partenariat/partenariats.html',
                    controller: 'PartenariatController',
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
                    $translatePartialLoader.addPart('partenariat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('partenariat-detail', {
            parent: 'entity',
            url: '/partenariat/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.partenariat.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/partenariat/partenariat-detail.html',
                    controller: 'PartenariatDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('partenariat');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Partenariat', function($stateParams, Partenariat) {
                    return Partenariat.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'partenariat',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('partenariat-detail.edit', {
            parent: 'partenariat-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/partenariat/partenariat-dialog.html',
                    controller: 'PartenariatDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Partenariat', function(Partenariat) {
                            return Partenariat.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('partenariat.new', {
            parent: 'partenariat',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/partenariat/partenariat-dialog.html',
                    controller: 'PartenariatDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                dateDebut: null,
                                dateFin: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('partenariat', null, { reload: 'partenariat' });
                }, function() {
                    $state.go('partenariat');
                });
            }]
        })
        .state('partenariat.edit', {
            parent: 'partenariat',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/partenariat/partenariat-dialog.html',
                    controller: 'PartenariatDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Partenariat', function(Partenariat) {
                            return Partenariat.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('partenariat', null, { reload: 'partenariat' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('partenariat.delete', {
            parent: 'partenariat',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/partenariat/partenariat-delete-dialog.html',
                    controller: 'PartenariatDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Partenariat', function(Partenariat) {
                            return Partenariat.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('partenariat', null, { reload: 'partenariat' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
