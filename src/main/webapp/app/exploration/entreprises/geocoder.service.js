(function() {
    'use strict';
    angular
        .module('crmisticApp')
        .factory('Geocoder', Geocoder);

    Geocoder.$inject = ['$log'];

    function Geocoder ($log) {

        var geocoding = {};

        var geocoder = new google.maps.Geocoder();

        geocoding.geocode = function(site, callback) {
            var rawAddress = [site[0].adresse, site[0].codePostal, site[0].ville, site[0].pays].join(" ");

            geocoder.geocode( { "address": rawAddress }, function(results, status) {
                if (status == google.maps.GeocoderStatus.OK && results.length > 0) {
                    var location = results[0].geometry.location;

                    callback(location);
                }
                else {
                    $log.debug("geocoding failed "+ status);
                }
            });
        };

        return geocoding;
    }
})();
