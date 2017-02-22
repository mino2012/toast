(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('SiteDetailController', SiteDetailController);

    SiteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Site', 'Entreprise', 'ConventionStage' ,'SiteOld'];

    function SiteDetailController($scope, $rootScope, $stateParams, previousState, entity, Site, Entreprise, ConventionStage, SiteOld) {
        var vm = this;

        vm.site = entity;
        vm.previousState = previousState.name;

        vm.anciennesVersions = SiteOld.query({
            id: vm.site.id
        });

        var unsubscribe = $rootScope.$on('crmisticApp:siteUpdate', function(event, result) {
            vm.site = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
