(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('groupe', {
            parent: 'entity',
            url: '/groupe?page&sort&search',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.groupe.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/groupe/groupes.html',
                    controller: 'GroupeController',
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
                    $translatePartialLoader.addPart('groupe');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('groupe-detail', {
            parent: 'entity',
            url: '/groupe/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.groupe.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/groupe/groupe-detail.html',
                    controller: 'GroupeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('groupe');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Groupe', function($stateParams, Groupe) {
                    return Groupe.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'groupe',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('groupe-detail.edit', {
            parent: 'groupe-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/groupe/groupe-dialog.html',
                    controller: 'GroupeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Groupe', function(Groupe) {
                            return Groupe.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('groupe.new', {
            parent: 'groupe',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/groupe/groupe-dialog.html',
                    controller: 'GroupeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nom: null,
                                dateCreation: null,
                                dateModification: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('groupe', null, { reload: 'groupe' });
                }, function() {
                    $state.go('groupe');
                });
            }]
        })
        .state('groupe.edit', {
            parent: 'groupe',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/groupe/groupe-dialog.html',
                    controller: 'GroupeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Groupe', function(Groupe) {
                            return Groupe.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('groupe', null, { reload: 'groupe' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('groupe.delete', {
            parent: 'groupe',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/groupe/groupe-delete-dialog.html',
                    controller: 'GroupeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Groupe', function(Groupe) {
                            return Groupe.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('groupe', null, { reload: 'groupe' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
