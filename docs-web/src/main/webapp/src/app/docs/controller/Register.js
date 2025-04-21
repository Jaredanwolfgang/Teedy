'use strict';

/**
 * Registration request controller.
 */
angular.module('docs').controller('Register', function($scope, $rootScope, $state, User, Restangular, $translate, $dialog) {
  $scope.user = {
    username: '',
    password: '',
    passwordConfirm: '',
    email: ''
  };
  
  /**
   * Register request.
   */
  $scope.register = function() {
    if ($scope.user.password !== $scope.user.passwordConfirm) {
      $scope.registerError = $translate.instant('register.password_confirm_error');
      return;
    }
    
    Restangular.one('user/registration').put({
      username: $scope.user.username,
      password: $scope.user.password,
      email: $scope.user.email
    }).then(function() {
      $dialog.messageBox($translate.instant('register.success.title'),
          $translate.instant('register.success.message'), [{
            label: $translate.instant('ok'),
            action: function(dialog) {
              dialog.close();
              $state.go('login');
            }
          }]);
    }, function(e) {
      if (e.data.type === 'AlreadyExistingUsername') {
        $scope.registerError = $translate.instant('register.error.username_exists');
      } else if (e.data.type === 'AlreadyExistingRequest') {
        $scope.registerError = $translate.instant('register.error.request_exists');
      } else {
        $scope.registerError = $translate.instant('register.error.server');
      }
    });
  };
}); 