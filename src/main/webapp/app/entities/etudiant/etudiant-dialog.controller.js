(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('EtudiantDialogController', EtudiantDialogController);

    EtudiantDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Etudiant', 'ConventionStage', 'Diplome'];

    function EtudiantDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Etudiant, ConventionStage, Diplome) {
        var vm = this;

        vm.etudiant = entity;
        vm.clear = clear;
        vm.save = save;
        vm.conventionstages = ConventionStage.query();
        vm.diplomes = Diplome.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.etudiant.id !== null) {
                Etudiant.update(vm.etudiant, onSaveSuccess, onSaveError);
            } else {
                Etudiant.save(vm.etudiant, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('crmisticApp:etudiantUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
