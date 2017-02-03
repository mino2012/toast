(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('EtudiantsController', EtudiantsController);

    EtudiantsController.$inject = ['$scope', 'Principal', 'LoginService', '$state','$log','$http'];

    function EtudiantsController ($scope, Principal, LoginService, $state,$log,$http) {
        var vm = this;

    }
})();
