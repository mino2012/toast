(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('PromotionDialogController', PromotionDialogController);

    PromotionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Promotion', 'Filiere', 'Etudiant', 'Diplome'];

    function PromotionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Promotion, Filiere, Etudiant, Diplome) {
        var vm = this;

        vm.promotion = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.diplomes = Diplome.query();
        vm.filieres = Filiere.query();
        vm.etudiants = Etudiant.query();

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
            if (vm.promotion.id !== null) {
                Promotion.update(vm.promotion, onSaveSuccess, onSaveError);
            } else {
                Promotion.save(vm.promotion, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('crmisticApp:promotionUpdate', result);
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
