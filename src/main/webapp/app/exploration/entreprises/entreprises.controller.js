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
        vm.loadAll = loadAll();

        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;

        function loadAll () {
            NbEtudiants.query({
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
                $log.debug($scope.myMap);

                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.nbEtudiants = data;
                vm.page = pagingParams.page;
                $log.debug(vm.nbEtudiants);
                displaySitesOnMap(vm.nbEtudiants);

            }
            function onError(error) {
                AlertService.error(error.data.message);
            }

        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

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

            var geocoder = new google.maps.Geocoder();
            geocoder.geocode( { "address": rawAddress }, function(results, status) {
                if (status == google.maps.GeocoderStatus.OK && results.length > 0) {
                    var location = results[0].geometry.location;
                    $log.debug(location);

                    $scope.addMarker(location, site);

                }
                else {
                    // the geocoding failed
                }
            });
        };

        function displaySitesOnMap (sites) {
            angular.forEach(sites, function (site) {

                $log.debug(site);
                $scope.geocoding(site);
            })
        }

    }
})();
