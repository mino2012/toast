'use strict';

describe('Controller Tests', function() {

    describe('Filiere Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockFiliere, MockPromotion, MockDiplome;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockFiliere = jasmine.createSpy('MockFiliere');
            MockPromotion = jasmine.createSpy('MockPromotion');
            MockDiplome = jasmine.createSpy('MockDiplome');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Filiere': MockFiliere,
                'Promotion': MockPromotion,
                'Diplome': MockDiplome
            };
            createController = function() {
                $injector.get('$controller')("FiliereDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'crmisticApp:filiereUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
