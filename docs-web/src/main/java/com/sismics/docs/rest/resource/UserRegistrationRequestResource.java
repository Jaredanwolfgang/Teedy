package com.sismics.docs.rest.resource;

import com.sismics.docs.core.constant.ConfigType;
import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.constant.RegistrationStatus;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.dao.UserRegistrationRequestDao;
import com.sismics.docs.core.dao.dto.UserRegistrationRequestDto;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.model.jpa.UserRegistrationRequest;
import com.sismics.docs.core.util.ConfigUtil;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.ValidationUtil;
import com.sismics.security.UserPrincipal;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * User registration request REST resources.
 * 
 * @author jaredan
 */
@Path("/user/registration")
public class UserRegistrationRequestResource extends BaseResource {
    
    /**
     * Creates a new user registration request.
     *
     * @api {put} /user/registration Request user registration
     * @apiName PutUserRegistration
     * @apiGroup UserRegistration
     * @apiParam {String{3..50}} username Username
     * @apiParam {String{8..50}} password Password
     * @apiParam {String{1..100}} email E-mail
     * @apiSuccess {String} status Status OK
     * @apiError (client) ValidationError Validation error
     * @apiError (client) AlreadyExistingUsername Login already used
     * @apiError (client) AlreadyExistingRequest Registration request with this username is already pending
     * @apiVersion 1.0.0
     *
     * @param username User's username
     * @param password Password
     * @param email E-mail
     * @return Response
     */
    @PUT
    public Response register(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("email") String email) {
        
        // Validate the input data
        username = ValidationUtil.validateLength(username, "username", 3, 50);
        ValidationUtil.validateUsername(username, "username");
        password = ValidationUtil.validateLength(password, "password", 8, 50);
        email = ValidationUtil.validateLength(email, "email", 1, 100);
        ValidationUtil.validateEmail(email, "email");
        
        // Create the registration request
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);
        
        // Save the request
        UserRegistrationRequestDao userRegistrationRequestDao = new UserRegistrationRequestDao();
        try {
            userRegistrationRequestDao.create(request);
        } catch (Exception e) {
            if ("AlreadyExistingUsername".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingUsername", "Login already used", e);
            } else if ("AlreadyExistingRequest".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingRequest", "Registration request with this username is already pending", e);
            } else {
                throw new ServerException("UnknownError", "Unknown server error", e);
            }
        }
        
        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }
    
    /**
     * Returns all registration requests.
     *
     * @api {get} /user/registration Get registration requests
     * @apiName GetUserRegistration
     * @apiGroup UserRegistration
     * @apiSuccess {Object[]} requests List of registration requests
     * @apiSuccess {String} requests.id ID
     * @apiSuccess {String} requests.username Username
     * @apiSuccess {String} requests.email E-mail
     * @apiSuccess {Number} requests.create_date Create date (timestamp)
     * @apiSuccess {String} requests.status Status (PENDING, APPROVED, REJECTED)
     * @apiSuccess {String} [requests.handled_by] Admin user ID who handled the request
     * @apiSuccess {String} [requests.handled_by_username] Admin username who handled the request
     * @apiSuccess {Number} [requests.handling_date] Handling date (timestamp)
     * @apiError (client) ForbiddenError Access denied
     * @apiPermission admin
     * @apiVersion 1.0.0
     *
     * @return Response
     */
    @GET
    public Response list() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);
        
        // Get all registration requests
        UserRegistrationRequestDao userRegistrationRequestDao = new UserRegistrationRequestDao();
        List<UserRegistrationRequestDto> requestList = userRegistrationRequestDao.findAll();
        
        // Build the response
        JsonArrayBuilder requests = Json.createArrayBuilder();
        for (UserRegistrationRequestDto request : requestList) {
            JsonObjectBuilder requestJson = Json.createObjectBuilder()
                    .add("id", request.getId())
                    .add("username", request.getUsername())
                    .add("email", request.getEmail())
                    .add("create_date", request.getCreateDate().getTime())
                    .add("status", request.getStatus());
            
            if (request.getHandledBy() != null) {
                requestJson.add("handled_by", request.getHandledBy());
                if (request.getHandledByUsername() != null) {
                    requestJson.add("handled_by_username", request.getHandledByUsername());
                }
                requestJson.add("handling_date", request.getHandlingDate().getTime());
            }
            
            requests.add(requestJson);
        }
        
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("requests", requests);
        return Response.ok().entity(response.build()).build();
    }
    
    /**
     * Process a registration request.
     *
     * @api {post} /user/registration/:id/:status Process a registration request
     * @apiName PostUserRegistrationStatus
     * @apiGroup UserRegistration
     * @apiParam {String} id Request ID
     * @apiParam {String="APPROVED","REJECTED"} status New status
     * @apiSuccess {String} status Status OK
     * @apiError (client) ForbiddenError Access denied
     * @apiError (client) ValidationError Validation error
     * @apiError (client) NotFound Registration request not found
     * @apiPermission admin
     * @apiVersion 1.0.0
     *
     * @param id Request ID
     * @param status New status
     * @return Response
     */
    @POST
    @Path("{id: [a-z0-9\\-]+}/{status: APPROVED|REJECTED}")
    public Response update(
            @PathParam("id") String id,
            @PathParam("status") String status) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);
        
        // Get the request
        UserRegistrationRequestDao userRegistrationRequestDao = new UserRegistrationRequestDao();
        UserRegistrationRequest request = userRegistrationRequestDao.getById(id);
        if (request == null) {
            throw new ClientException("NotFound", "Registration request not found");
        }
        
        // Update the request status
        UserRegistrationRequest updatedRequest = userRegistrationRequestDao.updateStatus(id, status, principal.getId());
        
        // If the request is approved, create the user
        if (RegistrationStatus.APPROVED.equals(status)) {
            User user = new User();
            user.setRoleId(Constants.DEFAULT_USER_ROLE);
            user.setUsername(updatedRequest.getUsername());
            user.setPassword(updatedRequest.getPassword());
            user.setEmail(updatedRequest.getEmail());
            // Default storage quota: 1GB
            user.setStorageQuota(1000000000L);
            user.setOnboarding(true);
            
            // Create the user
            UserDao userDao = new UserDao();
            try {
                userDao.create(user, principal.getId());
            } catch (Exception e) {
                throw new ServerException("UnknownError", "Unknown server error", e);
            }
        }
        
        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }
} 