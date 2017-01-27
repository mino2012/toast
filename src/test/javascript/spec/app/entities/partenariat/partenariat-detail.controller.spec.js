'use strict';

describe('Controller Tests', function() {

    describe('Partenariat Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockPartenariat, MockDiplome, MockEntreprise;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockPartenariat = jasmine.createSpy('MockPartenariat');
            MockDiplome = jasmine.createSpy('MockDiplome');
            MockEntreprise = jasmine.createSpy('MockEntreprise');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Partenariat': MockPartenariat,
                'Diplome': MockDiplome,
                'Entreprise': MockEntreprise
            };
            createController = function() {
                $injector.get('$controller')("PartenariatDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'crmisticApp:partenariatUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
