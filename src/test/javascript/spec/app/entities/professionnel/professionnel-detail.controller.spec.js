'use strict';

describe('Controller Tests', function() {

    describe('Professionnel Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockProfessionnel, MockEntreprise, MockConventionStage, MockDiplome;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockProfessionnel = jasmine.createSpy('MockProfessionnel');
            MockEntreprise = jasmine.createSpy('MockEntreprise');
            MockConventionStage = jasmine.createSpy('MockConventionStage');
            MockDiplome = jasmine.createSpy('MockDiplome');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Professionnel': MockProfessionnel,
                'Entreprise': MockEntreprise,
                'ConventionStage': MockConventionStage,
                'Diplome': MockDiplome
            };
            createController = function() {
                $injector.get('$controller')("ProfessionnelDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'crmisticApp:professionnelUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
