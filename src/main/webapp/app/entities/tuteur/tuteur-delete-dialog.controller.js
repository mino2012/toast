(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('TuteurDeleteController',TuteurDeleteController);

    TuteurDeleteController.$inject = ['$uibModalInstance', 'entity', 'Tuteur'];

    function TuteurDeleteController($uibModalInstance, entity, Tuteur) {
        var vm = this;

        vm.tuteur = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Tuteur.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
