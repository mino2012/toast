(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('FiliereDetailController', FiliereDetailController);

    FiliereDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Filiere', 'Promotion', 'Diplome'];

    function FiliereDetailController($scope, $rootScope, $stateParams, previousState, entity, Filiere, Promotion, Diplome) {
        var vm = this;

        vm.filiere = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('crmisticApp:filiereUpdate', function(event, result) {
            vm.filiere = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
