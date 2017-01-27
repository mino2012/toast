(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('PromotionDetailController', PromotionDetailController);

    PromotionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Promotion', 'Filiere'];

    function PromotionDetailController($scope, $rootScope, $stateParams, previousState, entity, Promotion, Filiere) {
        var vm = this;

        vm.promotion = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('crmisticApp:promotionUpdate', function(event, result) {
            vm.promotion = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
