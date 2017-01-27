(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('TaxeDetailController', TaxeDetailController);

    TaxeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Taxe', 'Entreprise'];

    function TaxeDetailController($scope, $rootScope, $stateParams, previousState, entity, Taxe, Entreprise) {
        var vm = this;

        vm.taxe = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('crmisticApp:taxeUpdate', function(event, result) {
            vm.taxe = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
