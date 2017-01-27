(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('ProfessionnelDialogController', ProfessionnelDialogController);

    ProfessionnelDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Professionnel', 'Entreprise', 'ConventionStage', 'Diplome'];

    function ProfessionnelDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Professionnel, Entreprise, ConventionStage, Diplome) {
        var vm = this;

        vm.professionnel = entity;
        vm.clear = clear;
        vm.save = save;
        vm.entreprisecontacts = Entreprise.query({filter: 'contact(nom)-is-null'});
        $q.all([vm.professionnel.$promise, vm.entreprisecontacts.$promise]).then(function() {
            if (!vm.professionnel.entrepriseContactId) {
                return $q.reject();
            }
            return Entreprise.get({id : vm.professionnel.entrepriseContactId}).$promise;
        }).then(function(entrepriseContact) {
            vm.entreprisecontacts.push(entrepriseContact);
        });
        vm.conventionstages = ConventionStage.query();
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
            if (vm.professionnel.id !== null) {
                Professionnel.update(vm.professionnel, onSaveSuccess, onSaveError);
            } else {
                Professionnel.save(vm.professionnel, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('crmisticApp:professionnelUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
