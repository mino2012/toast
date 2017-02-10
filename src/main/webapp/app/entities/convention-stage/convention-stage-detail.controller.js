(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('ConventionStageDetailController', ConventionStageDetailController);

    ConventionStageDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ConventionStage', 'Etudiant', 'Site', 'Tuteur', 'Professionnel','Principal', 'LoginService'];

    function ConventionStageDetailController($scope, $rootScope, $stateParams, previousState, entity, ConventionStage, Etudiant, Site, Tuteur, Professionnel, Principal, LoginService) {
        var vm = this;

        vm.conventionStage = entity;
        vm.previousState = previousState.name;
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


        var unsubscribe = $rootScope.$on('crmisticApp:conventionStageUpdate', function(event, result) {
            vm.conventionStage = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
