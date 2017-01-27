(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('TuteurDialogController', TuteurDialogController);

    TuteurDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Tuteur', 'ConventionStage'];

    function TuteurDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Tuteur, ConventionStage) {
        var vm = this;

        vm.tuteur = entity;
        vm.clear = clear;
        vm.save = save;
        vm.conventionstages = ConventionStage.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.tuteur.id !== null) {
                Tuteur.update(vm.tuteur, onSaveSuccess, onSaveError);
            } else {
                Tuteur.save(vm.tuteur, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('crmisticApp:tuteurUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
