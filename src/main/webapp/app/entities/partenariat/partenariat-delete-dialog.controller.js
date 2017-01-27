(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('PartenariatDeleteController',PartenariatDeleteController);

    PartenariatDeleteController.$inject = ['$uibModalInstance', 'entity', 'Partenariat'];

    function PartenariatDeleteController($uibModalInstance, entity, Partenariat) {
        var vm = this;

        vm.partenariat = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Partenariat.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
