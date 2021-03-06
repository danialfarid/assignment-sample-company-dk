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
    var Company = $resource('https://calm-meadow-37274.herokuapp.com/company/:companyId', {companyId:'@id'}, {'update': { method:'PUT' }});
    var Owner = $resource('https://calm-meadow-37274.herokuapp.com/company/:companyId/owner/:ownerId', {companyId:'@id', ownerId: '@id'});
    $scope.companies = Company.query();
    $scope.currentCompany = {};

    $scope.addCompany = function() {
      var newCompany = {name: 'New Company', owners: [{}]}
      $scope.companies.unshift(newCompany);
      $scope.currentCompany = newCompany;
    };

    $scope.addOwner = function(company) {
      var newOwner = {name: 'New Owner'};
      company.owners.unshift(newOwner);
    };

    $scope.createCompany = function(company) {
      Company.save(company, function(companyId) {
        company.id = companyId.id;
      });
    };

    $scope.showDetails = function(company, index) {
      if (company.id) {
        if ($scope.currentCompany == company) {
          $scope.currentCompany = {};
        } else {
          Company.get({companyId: company.id}, function (fullCompany) {
            $scope.companies.splice(index, 1, fullCompany);
            $scope.currentCompany = fullCompany;
          });
        }
      }
    };

    $scope.$watchCollection('currentCompany', function(v, oldV) {
      if ($scope.currentCompany.id && oldV === v) {
        Company.update({companyId: $scope.currentCompany.id}, $scope.currentCompany);
      }
    });

    $scope.$watchCollection('currentCompany.owners', function(v, oldV) {
      if ($scope.currentCompany.id && oldV != null) {
        Company.update({companyId: $scope.currentCompany.id}, $scope.currentCompany);
      }
    });

    $scope.removeCompany = function(company, index) {
      if (company.id) {
        company.$remove(function () {
          $scope.currentCompany = {};
          $scope.companies.splice(index, 1);
        });
      } else {
        $scope.currentCompany = {};
        $scope.companies.splice(index, 1);
      }
    };

    $scope.removeOwner = function(company, owner, index) {
      if (owner.id) {
        Owner.remove({companyId: company.id, ownerId: owner.id}, function () {
          company.owners.splice(index, 1);
        });
      } else {
        company.owners.splice(index, 1);
      }
    };
  });
