'use strict';

/**
 * User profile controller.
 */
angular.module('docs').controller('UserProfile', function($stateParams, Restangular, $scope, Message, $interval) {
  // Initialize scope variables
  $scope.content = '';
  $scope.messages = [];
  
  // Load user
  Restangular.one('user', $stateParams.username).get().then(function(data) {
    $scope.user = data;
  });
  
  // Load messages if not viewing your own profile
  var messagesPromise;
  $scope.$watch('user', function() {
    if ($scope.user && $scope.userInfo.username !== $scope.user.username) {
      // Load messages initially
      loadMessages();
      
      // Setup refresh interval for messages
      messagesPromise = $interval(loadMessages, 10000);
      
      // Stop the interval when leaving the page
      $scope.$on('$destroy', function() {
        if (messagesPromise) {
          $interval.cancel(messagesPromise);
        }
      });
    }
  });
  
  /**
   * Load messages between current user and viewed user.
   */
  function loadMessages() {
    Message.getMessages($scope.user.username, 'USER').then(function(data) {
      console.log('Messages loaded:', data);
      $scope.messages = data.messages;
    });
  }
  
  /**
   * Send a message to the user.
   */
  $scope.sendMessage = function() {
    // Get the content directly from the DOM as a fallback
    var inputContent = document.getElementById('messageContent').value;
    
    // Use DOM content if scope content is empty
    if (!$scope.content && inputContent) {
      $scope.content = inputContent;
    }
    
    if (!$scope.content || $scope.content.trim() === '') {
      return;
    }
    
    Message.sendMessage($scope.user.username, 'USER', $scope.content).then(function(response) {
      // Clear the content and reload messages
      $scope.content = '';
      document.getElementById('messageContent').value = '';
      loadMessages();
    }).catch(function(error) {
      console.error('Error sending message:', error);
    });
  };
});