(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('FiliereDeleteController',FiliereDeleteController);

    FiliereDeleteController.$inject = ['$uibModalInstance', 'entity', 'Filiere'];

    function FiliereDeleteController($uibModalInstance, entity, Filiere) {
        var vm = this;

        vm.filiere = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Filiere.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
