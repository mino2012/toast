(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('EntrepriseDeleteController',EntrepriseDeleteController);

    EntrepriseDeleteController.$inject = ['$uibModalInstance', 'entity', 'Entreprise'];

    function EntrepriseDeleteController($uibModalInstance, entity, Entreprise) {
        var vm = this;

        vm.entreprise = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Entreprise.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
