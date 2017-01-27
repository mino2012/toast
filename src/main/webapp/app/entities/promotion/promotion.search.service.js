(function() {
    'use strict';

    angular
        .module('crmisticApp')
        .factory('PromotionSearch', PromotionSearch);

    PromotionSearch.$inject = ['$resource'];

    function PromotionSearch($resource) {
        var resourceUrl =  'api/_search/promotions/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
