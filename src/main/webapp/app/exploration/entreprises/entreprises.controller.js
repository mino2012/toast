(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('EntreprisesController', EntreprisesController);

    EntreprisesController.$inject = ['$timeout','$scope', 'Principal', 'LoginService', '$state','$log','$http'];

    function EntreprisesController ($timeout, $scope, Principal, LoginService, $state,$log,$http) {
        var vm = this;

        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });


        vm.datePickerOpenStatus.dateDebut = false;
        vm.datePickerOpenStatus.dateFin = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

    }
})();
