(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('TaxeDialogController', TaxeDialogController);

    TaxeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Taxe', 'Entreprise'];

    function TaxeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Taxe, Entreprise) {
        var vm = this;

        vm.taxe = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.entreprises = Entreprise.query();

        vm.datepickerOptions = {
            mode: 'year',
            minMode: 'year',
            maxMode: 'year'
        };

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.taxe.id !== null) {
                Taxe.update(vm.taxe, onSaveSuccess, onSaveError);
            } else {
                Taxe.save(vm.taxe, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('crmisticApp:taxeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.annee = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
