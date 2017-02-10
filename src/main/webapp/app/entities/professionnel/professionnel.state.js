(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('professionnel', {
            parent: 'entity',
            url: '/professionnel?page&sort&search',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.professionnel.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/professionnel/professionnels.html',
                    controller: 'ProfessionnelController',
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
                    $translatePartialLoader.addPart('professionnel');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('professionnel-detail', {
            parent: 'entity',
            url: '/professionnel/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.professionnel.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/professionnel/professionnel-detail.html',
                    controller: 'ProfessionnelDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('professionnel');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Professionnel', function($stateParams, Professionnel) {
                    return Professionnel.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'professionnel',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('professionnel-detail.edit', {
            parent: 'professionnel-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/professionnel/professionnel-dialog.html',
                    controller: 'ProfessionnelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Professionnel', function(Professionnel) {
                            return Professionnel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('professionnel.new', {
            parent: 'professionnel',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/professionnel/professionnel-dialog.html',
                    controller: 'ProfessionnelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nom: null,
                                prenom: null,
                                telephone: null,
                                mail: null,
                                fonction: null,
                                ancienEtudiant: null,
                                dateCreation: null,
                                dateModification: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('professionnel', null, { reload: 'professionnel' });
                }, function() {
                    $state.go('professionnel');
                });
            }]
        })
        .state('professionnel.edit', {
            parent: 'professionnel',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/professionnel/professionnel-dialog.html',
                    controller: 'ProfessionnelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Professionnel', function(Professionnel) {
                            return Professionnel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('professionnel', null, { reload: 'professionnel' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('professionnel.delete', {
            parent: 'professionnel',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/professionnel/professionnel-delete-dialog.html',
                    controller: 'ProfessionnelDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Professionnel', function(Professionnel) {
                            return Professionnel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('professionnel', null, { reload: 'professionnel' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
