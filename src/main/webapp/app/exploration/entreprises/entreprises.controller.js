function initMap () {
    angular.bootstrap(document.getElementById("map"), ['crmisticApp.ui-map']);
}

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

        var data = [
            {
                "nom": "Sopra Steria",
                "adresse": "",
                "codePostal": "",
                "ville": "Rennes",
                "nbStagiaires": 22
            },
            {
                "nom": "Cap",
                "adresse": "7 rue du gast",
                "codePostal": "35410",
                "ville": "Chateaugiron",
                "nbStagiaires": 8
            }
        ];

        $scope.myMarkers = [];

        $scope.mapOptions = {
            center: new google.maps.LatLng(48.117266, -1.6777925999999752),
            zoom: 7,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };

        $scope.addMarker = function($params, entreprise) {
            var marker = new google.maps.Marker({
                map: $scope.myMap,
                label: entreprise.nbStagiaires+'',
                position: new google.maps.LatLng($params.lat(), $params.lng())
            });
            marker.entreprise = entreprise;

            $scope.myMarkers.push(marker);
        };

        $scope.openMarkerInfo = function (marker) {
            $scope.currentMarker = marker;
            $scope.currentMarkerLat = marker.getPosition().lat();
            $scope.currentMarkerLng = marker.getPosition().lng();
            $scope.currentLabel = marker.getLabel();
            $scope.currentEntreprise = marker.entreprise;
            $scope.myInfoWindow.open($scope.myMap, marker);
        };

        $scope.geocoding = function (entreprise) {
            var rawAddress = entreprise.adresse + " " + entreprise.codePostal + " "+ entreprise.ville;

            var geocoder = new google.maps.Geocoder();
            geocoder.geocode( { "address": rawAddress }, function(results, status) {
                if (status == google.maps.GeocoderStatus.OK && results.length > 0) {
                    var location = results[0].geometry.location;
                    $log.debug(location);

                    $scope.addMarker(location, entreprise);

                }
                else {
                    // the geocoding failed
                }
            });
        };

        $log.debug($scope.myMap);

        angular.forEach(data, function(entreprise, index) {
            $scope.geocoding(entreprise);
        });


    }
})();
