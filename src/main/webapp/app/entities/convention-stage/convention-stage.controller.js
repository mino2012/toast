(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('ConventionStageController', ConventionStageController);

    ConventionStageController.$inject = ['$scope', '$state', '$http', 'ConventionStage', 'ConventionStageSearch', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams', 'Principal', 'LoginService'];

    function ConventionStageController ($scope, $state, $http, ConventionStage, ConventionStageSearch, ParseLinks, AlertService, paginationConstants, pagingParams, Principal, LoginService) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = pagingParams.search;
        vm.currentSearch = pagingParams.search;
        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }

        function register () {
            $state.go('register');
        }

        loadAll();

        function loadAll () {
            if (pagingParams.search) {
                ConventionStageSearch.query({
                    query: pagingParams.search,
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                ConventionStage.query({
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            }
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                /* Update site at date creation of convention */
                angular.forEach(data, function (convention) {
                    /* Call synchrone function to wait the answer */
                    $http({
                        url: 'api/siteCreationStage/',
                        method: "GET",
                        params: {id: convention.id}
                    }).then(function (response) {
                        if (response) {
                            response = angular.fromJson(response);
                            convention.lieuStageAdresse = response.data[0][0].adresse;
                        }
                    });
                    $http({
                        url: 'api/entrepriseCreationStage/',
                        method: "GET",
                        params: {id: convention.id}
                    }).then(function (response) {
                        if (response) {
                            response = angular.fromJson(response);
                            convention.entreprise.nom = response.data[0][0].nom;
                        }
                    });
                });
                vm.conventionStages = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }

        function search(searchQuery) {
            if (!searchQuery){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.currentSearch = null;
            vm.transition();
        }
    }
})();
