(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('DiplomeDetailController', DiplomeDetailController);

    DiplomeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Diplome', 'Filiere', 'Partenariat', 'Professionnel', 'DiplomeOld'];

    function DiplomeDetailController($scope, $rootScope, $stateParams, previousState, entity, Diplome, Filiere, Partenariat, Professionnel, DiplomeOld) {
        var vm = this;

        vm.diplome = entity;
        vm.anciennesVersions = DiplomeOld.query({
            id: vm.diplome.id
        });
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('crmisticApp:diplomeUpdate', function(event, result) {
            vm.diplome = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
