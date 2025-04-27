'use strict';

/**
 * Message service.
 */
angular.module('docs').factory('Message', function(Restangular) {
  return {
    /**
     * Returns messages for a target.
     * @param targetName Target name (username or group name)
     * @param type Message type (USER or GROUP)
     */
    getMessages: function(targetName, type) {
      return Restangular.one('message').get({
        target_name: targetName,
        type: type
      });
    },

    /**
     * Send a message.
     * @param targetName Target name (username or group name)
     * @param type Message type (USER or GROUP)
     * @param content Message content
     */
    sendMessage: function(targetName, type, content) {
      return Restangular.one('message').put({
        target_name: targetName,
        type: type,
        content: content
      });
    },
    
    /**
     * Delete a message.
     * @param id Message ID
     */
    deleteMessage: function(id) {
      return Restangular.one('message', id).remove();
    }
  };
}); 