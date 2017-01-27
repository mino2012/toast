(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('SiteDetailController', SiteDetailController);

    SiteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Site', 'Entreprise', 'ConventionStage'];

    function SiteDetailController($scope, $rootScope, $stateParams, previousState, entity, Site, Entreprise, ConventionStage) {
        var vm = this;

        vm.site = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('crmisticApp:siteUpdate', function(event, result) {
            vm.site = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
