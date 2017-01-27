'use strict';

describe('Controller Tests', function() {

    describe('ConventionStage Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockConventionStage, MockEtudiant, MockSite, MockTuteur, MockProfessionnel;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockConventionStage = jasmine.createSpy('MockConventionStage');
            MockEtudiant = jasmine.createSpy('MockEtudiant');
            MockSite = jasmine.createSpy('MockSite');
            MockTuteur = jasmine.createSpy('MockTuteur');
            MockProfessionnel = jasmine.createSpy('MockProfessionnel');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'ConventionStage': MockConventionStage,
                'Etudiant': MockEtudiant,
                'Site': MockSite,
                'Tuteur': MockTuteur,
                'Professionnel': MockProfessionnel
            };
            createController = function() {
                $injector.get('$controller')("ConventionStageDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'crmisticApp:conventionStageUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
