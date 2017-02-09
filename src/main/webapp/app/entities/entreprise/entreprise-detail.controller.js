(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('EntrepriseDetailController', EntrepriseDetailController);

    EntrepriseDetailController.$inject = ['$resource', '$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Entreprise', 'Partenariat', 'Site', 'Professionnel', 'Taxe', 'Groupe', 'EntrepriseOld'];

    function EntrepriseDetailController($resource, $scope, $rootScope, $stateParams, previousState, entity, Entreprise, Partenariat, Site, Professionnel, Taxe, Groupe, EntrepriseOld) {
        var vm = this;

        vm.entreprise = entity;
        vm.anciennesVersions = EntrepriseOld.query({
            id: vm.entreprise.id
        });
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('crmisticApp:entrepriseUpdate', function(event, result) {
            vm.entreprise = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
