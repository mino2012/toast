(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('ProfessionnelDeleteController',ProfessionnelDeleteController);

    ProfessionnelDeleteController.$inject = ['$uibModalInstance', 'entity', 'Professionnel'];

    function ProfessionnelDeleteController($uibModalInstance, entity, Professionnel) {
        var vm = this;

        vm.professionnel = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Professionnel.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
