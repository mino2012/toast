// maps.googleapis callback
function initMap () {
    // angular.bootstrap(document.getElementById("map"), ['crmisticApp.ui-map']);
}

(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .controller('EntreprisesController', EntreprisesController);

    EntreprisesController.$inject = ['$timeout','$scope', 'Principal', 'LoginService', '$state','$log','$http','NbEtudiants','AlertService','paginationConstants','pagingParams','ParseLinks'];

    function EntreprisesController ($timeout, $scope, Principal, LoginService, $state,$log,$http,NbEtudiants,AlertService,paginationConstants,pagingParams,ParseLinks) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;

        vm.datepickerDebut = getDateNYearsLaterOrBeforeToday(-1);
        vm.datepickerFin = new Date(Date.now());

        vm.loadAll = loadAll();
        vm.nbEtudiantsMin = 1;

        var geocoder = new google.maps.Geocoder();

        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;

        function loadAll () {
            if (vm.datepickerDebut === null) {
                vm.datepickerDebut = getDateNYearsLaterOrBeforeToday(-50);
            }
            if (vm.datepickerFin === null) {
                vm.datepickerFin = getDateNYearsLaterOrBeforeToday(50);
            }
            if (typeof vm.nbEtudiantsMin === 'undefined') {
                vm.nbEtudiantsMin = 1;
            }

            NbEtudiants.query({
                dateDebutDatepicker: vm.datepickerDebut,
                dateFinDatepicker: vm.datepickerFin,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);

            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'lieuStage.adresse') {
                    result.push('lieuStage.adresse');
                }
                return result;
            }

            function onSuccess(data, headers) {

                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.nbEtudiants = data;
                vm.page = pagingParams.page;
                displaySitesOnMap(vm.nbEtudiants);

            }
            function onError(error) {
                AlertService.error(error.data.message);
            }

        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function getDateNYearsLaterOrBeforeToday(nbYears) {
            return new Date(new Date().setFullYear(new Date().getFullYear() + nbYears));
        }

        vm.datePickerOpenStatus.dateDebut = false;
        vm.datePickerOpenStatus.dateFin = false;

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')
            });
        }

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        $scope.myMarkers = [];

        $scope.mapOptions = {
            center: new google.maps.LatLng(48.117266, -1.6777925999999752),
            zoom: 7,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };

        $scope.addMarker = function($params, site) {
            var marker = new google.maps.Marker({
                map: $scope.myMap,
                label: site[1]+'',
                position: new google.maps.LatLng($params.lat(), $params.lng())
            });
            marker.site = site;

            $scope.myMarkers.push(marker);
        };

        $scope.openMarkerInfo = function (marker) {
            $scope.currentMarker = marker;
            $scope.currentMarkerLat = marker.getPosition().lat();
            $scope.currentMarkerLng = marker.getPosition().lng();
            $scope.currentLabel = marker.getLabel();
            $scope.currentSite = marker.site;
            $scope.myInfoWindow.open($scope.myMap, marker);
        };

        $scope.geocoding = function (site) {
            var rawAddress = site[0].adresse + " " + site[0].codePostal + " "+ site[0].ville + " "+ site[0].pays;

            geocoder.geocode( { "address": rawAddress }, function(results, status) {
                if (status == google.maps.GeocoderStatus.OK && results.length > 0) {
                    var location = results[0].geometry.location;

                    if (site[1] >= vm.nbEtudiantsMin) {
                        $scope.addMarker(location, site);
                    }
                }
                else {
                    $log.debug("geocoding failed "+ status);
                }
            });
        };

        function displaySitesOnMap (sites) {
            angular.forEach(sites, function (site) {

                $scope.geocoding(site);
            });
        }

        vm.inputFilter = function () {

            clearAllMarkers();
            vm.loadAll = loadAll();
        };

        function clearAllMarkers() {
            angular.forEach($scope.myMarkers, function (marker) {
                marker.setMap(null);
            });

            $scope.myMarkers = [];
        }

    }
})();
