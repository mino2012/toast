(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('PartenariatDialogController', PartenariatDialogController);

    PartenariatDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Partenariat', 'Diplome', 'Entreprise'];

    function PartenariatDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Partenariat, Diplome, Entreprise) {
        var vm = this;

        vm.partenariat = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.diplomes = Diplome.query();
        vm.entreprises = Entreprise.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.partenariat.id !== null) {
                Partenariat.update(vm.partenariat, onSaveSuccess, onSaveError);
            } else {
                Partenariat.save(vm.partenariat, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('crmisticApp:partenariatUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.dateDebut = false;
        vm.datePickerOpenStatus.dateFin = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
