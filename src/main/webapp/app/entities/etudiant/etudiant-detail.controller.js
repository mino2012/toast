(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('EtudiantDetailController', EtudiantDetailController);

    EtudiantDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Etudiant', 'ConventionStage', 'Diplome'];

    function EtudiantDetailController($scope, $rootScope, $stateParams, previousState, entity, Etudiant, ConventionStage, Diplome) {
        var vm = this;

        vm.etudiant = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('crmisticApp:etudiantUpdate', function(event, result) {
            vm.etudiant = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
