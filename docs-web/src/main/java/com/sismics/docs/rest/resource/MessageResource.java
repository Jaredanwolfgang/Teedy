package com.sismics.docs.rest.resource;

import com.google.common.collect.Lists;
import com.sismics.docs.core.constant.AclTargetType;
import com.sismics.docs.core.constant.MessageType;
import com.sismics.docs.core.dao.MessageDao;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.dao.criteria.MessageCriteria;
import com.sismics.docs.core.dao.dto.MessageDto;
import com.sismics.docs.core.model.jpa.Message;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.util.SecurityUtil;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.ValidationUtil;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Message REST resources.
 * 
 * @author jaredan
 */
@Path("/message")
public class MessageResource extends BaseResource {

    /**
     * Returns the messages linked to a user or group.
     *
     * @api {get} /message Get messages
     * @apiName GetMessages
     * @apiGroup Message
     * @apiParam {String} target_name Target name (username or group name)
     * @apiParam {String="USER","GROUP"} type The message type
     * @apiSuccess {Object[]} messages List of messages
     * @apiSuccess {String} messages.id ID
     * @apiSuccess {String} messages.content Content
     * @apiSuccess {String} messages.creator_name Creator name
     * @apiSuccess {String} messages.creator_email Creator email
     * @apiSuccess {Number} messages.create_date Create date (timestamp)
     * @apiDescription For USER type, returns all messages exchanged between the current user and the target user (bidirectional).
     * For GROUP type, returns all messages sent to the group.
     * @apiError (client) ForbiddenError Access denied
     * @apiError (client) ValidationError Validation error
     * @apiPermission user
     * @apiVersion 1.5.0
     * 
     * @param targetName Target name (username or group name)
     * @param typeStr Message type
     * @return Response
     */
    @GET
    public Response getMessages(
            @QueryParam("target_name") String targetName,
            @QueryParam("type") String typeStr) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        // Validate input data
        if (targetName == null) {
            throw new ClientException("ValidationError", "target_name is required");
        }
        ValidationUtil.validateRequired(typeStr, "type");
        
        MessageType type;
        try {
            type = MessageType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new ServerException("ValidationError", "Invalid message type");
        }
        
        // Convert targetName to targetId
        AclTargetType targetType = type == MessageType.USER ? AclTargetType.USER : AclTargetType.GROUP;
        String targetId = SecurityUtil.getTargetIdFromName(targetName, targetType);
        if (targetId == null) {
            throw new ClientException("ValidationError", "Target not found: " + targetName);
        }
        
        // Check if the user is not trying to chat with himself
        if (type == MessageType.USER && targetId.equals(principal.getId())) {
            throw new ServerException("ValidationError", "You cannot chat with yourself");
        }
        
        // Get the messages
        MessageDao messageDao = new MessageDao();
        MessageCriteria criteria = new MessageCriteria()
                .setTargetIdList(Lists.newArrayList(targetId))
                .setTargetType(type);
        
        // For USER type, set the current user as sender for bidirectional conversation
        if (type == MessageType.USER) {
            criteria.setSenderId(principal.getId());
        }
        
        List<MessageDto> messageDtoList = messageDao.findByCriteria(criteria, null);
        
        // Build the response
        JsonObjectBuilder response = Json.createObjectBuilder();
        JsonArrayBuilder messages = Json.createArrayBuilder();
        
        for (MessageDto messageDto : messageDtoList) {
            messages.add(Json.createObjectBuilder()
                    .add("id", messageDto.getId())
                    .add("content", messageDto.getContent())
                    .add("creator_name", messageDto.getCreatorName())
                    .add("creator_email", messageDto.getCreatorEmail())
                    .add("create_date", messageDto.getCreateTimestamp()));
        }
        
        response.add("messages", messages);
        
        return Response.ok().entity(response.build()).build();
    }
    
    /**
     * Send a message.
     *
     * @api {put} /message Send a message
     * @apiName PutMessage
     * @apiGroup Message
     * @apiParam {String} target_name Target name (username or group name)
     * @apiParam {String="USER","GROUP"} type The message type
     * @apiParam {String} content Message content
     * @apiSuccess {String} status Status OK
     * @apiDescription Sends a message to the specified target. For USER type, messages are part of a bidirectional 
     * conversation between the current user (sender) and the target user. The sender ID is automatically set to 
     * the current authenticated user.
     * @apiError (client) ForbiddenError Access denied
     * @apiError (client) ValidationError Validation error
     * @apiPermission user
     * @apiVersion 1.5.0
     * 
     * @param targetName Target name (username or group name)
     * @param typeStr Message type
     * @param content Message content
     * @return Response
     */
    @PUT
    public Response sendMessage(
            @FormParam("target_name") String targetName,
            @FormParam("type") String typeStr,
            @FormParam("content") String content) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        // Validate input data
        if (targetName == null) {
            throw new ClientException("ValidationError", "target_name is required");
        }
        ValidationUtil.validateRequired(typeStr, "type");
        content = ValidationUtil.validateLength(content, "content", 1, 4000, false);
        
        MessageType type;
        try {
            type = MessageType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new ServerException("ValidationError", "Invalid message type");
        }
        
        // Convert targetName to targetId
        AclTargetType targetType = type == MessageType.USER ? AclTargetType.USER : AclTargetType.GROUP;
        String targetId = SecurityUtil.getTargetIdFromName(targetName, targetType);
        if (targetId == null) {
            throw new ClientException("ValidationError", "Target not found: " + targetName);
        }
        
        // Check if the user is not trying to chat with himself
        UserDao userDao = new UserDao();
        User currentUser = userDao.getById(principal.getId());
        if (type == MessageType.USER && targetName.equals(currentUser.getUsername())) {
            throw new ServerException("ValidationError", "You cannot chat with yourself");
        }
        
        // Create the message
        Message message = new Message();
        message.setContent(content);
        message.setTargetId(targetId);
        message.setType(type.name());
        message.setUserId(principal.getId());
        
        // Write to DB
        MessageDao messageDao = new MessageDao();
        messageDao.create(message, principal.getId());
        
        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        
        return Response.ok().entity(response.build()).build();
    }
    
    /**
     * Delete a message.
     *
     * @api {delete} /message/:id Delete a message
     * @apiName DeleteMessage
     * @apiGroup Message
     * @apiParam {String} id Message ID
     * @apiSuccess {String} status Status OK
     * @apiError (client) ForbiddenError Access denied
     * @apiError (client) NotFound Message not found
     * @apiPermission user
     * @apiVersion 1.5.0
     * 
     * @param id Message ID
     * @return Response
     */
    @DELETE
    @Path("{id: [a-z0-9\\-]+}")
    public Response deleteMessage(@PathParam("id") String id) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        // Delete the message
        MessageDao messageDao = new MessageDao();
        messageDao.delete(id, principal.getId());
        
        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        
        return Response.ok().entity(response.build()).build();
    }
} 