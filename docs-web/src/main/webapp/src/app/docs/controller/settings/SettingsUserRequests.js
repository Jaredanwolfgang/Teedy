'use strict';

/**
 * Settings user registration requests controller.
 */
angular.module('docs').controller('SettingsUserRequests', function($scope, $rootScope, Restangular, $translate, $q, $timeout) {
  // Load registration requests
  $scope.loadRequests = function() {
    Restangular.one('user/registration').get().then(function(data) {
      $scope.requests = data.requests;
      $scope.pendingRequestCount = 0;
      
      // Count pending requests
      angular.forEach($scope.requests, function(request) {
        if (request.status === 'PENDING') {
          $scope.pendingRequestCount++;
        }
      });
      
      // Update the parent scope
      $rootScope.pendingRequestCount = $scope.pendingRequestCount;
    });
  };
  
  // Process a registration request
  $scope.processRequest = function(requestId, status) {
    Restangular.one('user/registration', requestId).one(status).post().then(function() {
      $scope.loadRequests();
    });
  };
  
  $scope.loadRequests();
}); 