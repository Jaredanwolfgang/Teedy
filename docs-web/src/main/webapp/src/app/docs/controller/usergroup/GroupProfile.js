'use strict';

/**
 * Group profile controller.
 */
angular.module('docs').controller('GroupProfile', function($stateParams, Restangular, $scope, Message, $interval) {
  // Load user
  Restangular.one('group', $stateParams.name).get().then(function(data) {
    $scope.group = data;
  });
  
  // Initialize message variables
  $scope.content = '';
  $scope.messages = [];
  
  // Load messages if not viewing your own profile
  var messagesPromise;
  $scope.$watch('group', function() {
    if ($scope.group) {
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
   * Load messages in the group.
   */
  function loadMessages() {
    Message.getMessages($scope.group.name, 'GROUP').then(function(data) {
      console.log('Messages loaded:', data);
      $scope.messages = data.messages;
    });
  }
  
  /**
   * Send a message to the group.
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
    
    Message.sendMessage($scope.group.name, 'GROUP', $scope.content).then(function(response) {
      // Clear the content and reload messages
      $scope.content = '';
      document.getElementById('messageContent').value = '';
      loadMessages();
    }).catch(function(error) {
      console.error('Error sending message:', error);
    });
  };
});