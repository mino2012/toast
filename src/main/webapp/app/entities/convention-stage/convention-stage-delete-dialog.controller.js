(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('ConventionStageDeleteController',ConventionStageDeleteController);

    ConventionStageDeleteController.$inject = ['$uibModalInstance', 'entity', 'ConventionStage'];

    function ConventionStageDeleteController($uibModalInstance, entity, ConventionStage) {
        var vm = this;

        vm.conventionStage = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ConventionStage.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
