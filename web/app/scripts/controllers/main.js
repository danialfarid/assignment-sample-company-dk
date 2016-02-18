'use strict';

/**
 * @ngdoc function
 * @name webApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the webApp
 */
angular.module('webApp')
  .controller('MainCtrl', function ($scope, $resource) {
    var Company = $resource('https://calm-meadow-37274.herokuapp.com/company/:companyId', {companyId:'@id'});
    $scope.companies = Company.query();
    $scope.currentCompany = {};
    $scope.showDetails = function(company, index) {
      Company.get({companyId: company.id}, function(fullCompany) {
        $scope.companies.splice(index, 1, fullCompany);
        $scope.currentCompany = fullCompany;
      });
    };

    $scope.$watchCollection('currentCompany', function(v) {
      if ($scope.currentCompany.$save) {
        $scope.currentCompany.$save();
      }
      console.log(v);
    });
  });
