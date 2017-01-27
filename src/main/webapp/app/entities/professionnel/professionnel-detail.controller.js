(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('ProfessionnelDetailController', ProfessionnelDetailController);

    ProfessionnelDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Professionnel', 'Entreprise', 'ConventionStage', 'Diplome'];

    function ProfessionnelDetailController($scope, $rootScope, $stateParams, previousState, entity, Professionnel, Entreprise, ConventionStage, Diplome) {
        var vm = this;

        vm.professionnel = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('crmisticApp:professionnelUpdate', function(event, result) {
            vm.professionnel = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
