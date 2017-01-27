(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('TuteurDetailController', TuteurDetailController);

    TuteurDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Tuteur', 'ConventionStage'];

    function TuteurDetailController($scope, $rootScope, $stateParams, previousState, entity, Tuteur, ConventionStage) {
        var vm = this;

        vm.tuteur = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('crmisticApp:tuteurUpdate', function(event, result) {
            vm.tuteur = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
