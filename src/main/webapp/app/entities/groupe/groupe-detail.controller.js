(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('GroupeDetailController', GroupeDetailController);

    GroupeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Groupe', 'Entreprise'];

    function GroupeDetailController($scope, $rootScope, $stateParams, previousState, entity, Groupe, Entreprise) {
        var vm = this;

        vm.groupe = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('crmisticApp:groupeUpdate', function(event, result) {
            vm.groupe = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
