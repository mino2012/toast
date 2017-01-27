(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('DiplomeDeleteController',DiplomeDeleteController);

    DiplomeDeleteController.$inject = ['$uibModalInstance', 'entity', 'Diplome'];

    function DiplomeDeleteController($uibModalInstance, entity, Diplome) {
        var vm = this;

        vm.diplome = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Diplome.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
