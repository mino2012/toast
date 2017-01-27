'use strict';

describe('Controller Tests', function() {

    describe('Site Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockSite, MockEntreprise, MockConventionStage;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockSite = jasmine.createSpy('MockSite');
            MockEntreprise = jasmine.createSpy('MockEntreprise');
            MockConventionStage = jasmine.createSpy('MockConventionStage');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Site': MockSite,
                'Entreprise': MockEntreprise,
                'ConventionStage': MockConventionStage
            };
            createController = function() {
                $injector.get('$controller')("SiteDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'crmisticApp:siteUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
