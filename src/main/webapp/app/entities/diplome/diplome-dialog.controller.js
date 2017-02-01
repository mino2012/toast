(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('DiplomeDialogController', DiplomeDialogController);

    DiplomeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Diplome', 'Filiere', 'Partenariat', 'Professionnel'];

    function DiplomeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Diplome, Filiere, Partenariat, Professionnel) {
        var vm = this;

        vm.diplome = entity;
        vm.clear = clear;
        vm.save = save;
        vm.filieres = Filiere.query();
        vm.partenariats = Partenariat.query();
        vm.professionnels = Professionnel.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.diplome.id !== null) {
                Diplome.update(vm.diplome, onSaveSuccess, onSaveError);
            } else {
                Diplome.save(vm.diplome, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('crmisticApp:diplomeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
