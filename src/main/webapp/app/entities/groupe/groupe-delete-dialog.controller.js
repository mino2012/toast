(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('GroupeDeleteController',GroupeDeleteController);

    GroupeDeleteController.$inject = ['$uibModalInstance', 'entity', 'Groupe'];

    function GroupeDeleteController($uibModalInstance, entity, Groupe) {
        var vm = this;

        vm.groupe = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Groupe.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
