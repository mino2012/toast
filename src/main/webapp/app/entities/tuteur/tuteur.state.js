(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('tuteur', {
            parent: 'entity',
            url: '/tuteur?page&sort&search',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.tuteur.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tuteur/tuteurs.html',
                    controller: 'TuteurController',
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
                    $translatePartialLoader.addPart('tuteur');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('tuteur-detail', {
            parent: 'entity',
            url: '/tuteur/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'crmisticApp.tuteur.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tuteur/tuteur-detail.html',
                    controller: 'TuteurDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tuteur');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Tuteur', function($stateParams, Tuteur) {
                    return Tuteur.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'tuteur',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('tuteur-detail.edit', {
            parent: 'tuteur-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tuteur/tuteur-dialog.html',
                    controller: 'TuteurDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tuteur', function(Tuteur) {
                            return Tuteur.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tuteur.new', {
            parent: 'tuteur',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tuteur/tuteur-dialog.html',
                    controller: 'TuteurDialogController',
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
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('tuteur', null, { reload: 'tuteur' });
                }, function() {
                    $state.go('tuteur');
                });
            }]
        })
        .state('tuteur.edit', {
            parent: 'tuteur',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tuteur/tuteur-dialog.html',
                    controller: 'TuteurDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tuteur', function(Tuteur) {
                            return Tuteur.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tuteur', null, { reload: 'tuteur' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tuteur.delete', {
            parent: 'tuteur',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tuteur/tuteur-delete-dialog.html',
                    controller: 'TuteurDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Tuteur', function(Tuteur) {
                            return Tuteur.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tuteur', null, { reload: 'tuteur' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
