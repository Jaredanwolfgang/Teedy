package com.sismics.docs.core.dao;

import com.sismics.docs.core.constant.AuditLogType;
import com.sismics.docs.core.constant.RegistrationStatus;
import com.sismics.docs.core.dao.dto.UserRegistrationRequestDto;
import com.sismics.docs.core.model.jpa.UserRegistrationRequest;
import com.sismics.docs.core.util.AuditLogUtil;
import com.sismics.util.context.ThreadLocalContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User registration request DAO.
 * 
 * @author jaredan
 */
public class UserRegistrationRequestDao {
    
    /**
     * Creates a new registration request.
     * 
     * @param request Registration request to create
     * @return Request ID
     * @throws Exception e
     */
    public String create(UserRegistrationRequest request) throws Exception {
        // Create the request UUID
        request.setId(UUID.randomUUID().toString());
        
        // Check for username unicity
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select u from User u where u.username = :username and u.deleteDate is null");
        q.setParameter("username", request.getUsername());
        List<?> l = q.getResultList();
        if (l.size() > 0) {
            throw new Exception("AlreadyExistingUsername");
        }
        
        // Check for pending requests with same username
        q = em.createQuery("select r from UserRegistrationRequest r where r.username = :username and r.status = :status");
        q.setParameter("username", request.getUsername());
        q.setParameter("status", RegistrationStatus.PENDING);
        l = q.getResultList();
        if (l.size() > 0) {
            throw new Exception("AlreadyExistingRequest");
        }
        
        // Create the request
        request.setCreateDate(new Date());
        request.setStatus(RegistrationStatus.PENDING);
        em.persist(request);
        
        return request.getId();
    }
    
    /**
     * Get a registration request by ID.
     * 
     * @param id Request ID
     * @return Registration request
     */
    public UserRegistrationRequest getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(UserRegistrationRequest.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Get a registration request by username.
     * 
     * @param username Username
     * @return Pending registration request or null
     */
    public UserRegistrationRequest getPendingByUsername(String username) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            TypedQuery<UserRegistrationRequest> q = em.createQuery(
                    "select r from UserRegistrationRequest r where r.username = :username and r.status = :status", 
                    UserRegistrationRequest.class);
            q.setParameter("username", username);
            q.setParameter("status", RegistrationStatus.PENDING);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Updates the status of a registration request.
     * 
     * @param id Request ID
     * @param status New status
     * @param userId Admin user ID
     * @return Updated request
     */
    public UserRegistrationRequest updateStatus(String id, String status, String userId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        
        // Get the request
        UserRegistrationRequest request = getById(id);
        if (request == null) {
            return null;
        }
        
        // Update the request
        request.setStatus(status);
        request.setHandledBy(userId);
        request.setHandlingDate(new Date());
        
        return request;
    }
    
    /**
     * Returns all registration requests.
     * 
     * @return List of registration requests
     */
    public List<UserRegistrationRequestDto> findAll() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        
        // Get all requests
        TypedQuery<UserRegistrationRequest> q = em.createQuery(
                "select r from UserRegistrationRequest r order by r.createDate desc", 
                UserRegistrationRequest.class);
        List<UserRegistrationRequest> requestList = q.getResultList();
        
        // Build the DTO list
        List<UserRegistrationRequestDto> dtoList = new ArrayList<>();
        
        // Get usernames for admin users who handled requests
        UserDao userDao = new UserDao();
        
        for (UserRegistrationRequest request : requestList) {
            UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
            dto.setId(request.getId());
            dto.setUsername(request.getUsername());
            dto.setEmail(request.getEmail());
            dto.setCreateDate(request.getCreateDate());
            dto.setStatus(request.getStatus());
            dto.setHandledBy(request.getHandledBy());
            dto.setHandlingDate(request.getHandlingDate());
            
            // Add admin username if available
            if (request.getHandledBy() != null) {
                dto.setHandledByUsername(userDao.getById(request.getHandledBy()).getUsername());
            }
            
            dtoList.add(dto);
        }
        
        return dtoList;
    }
    
    /**
     * Returns pending registration requests.
     * 
     * @return List of pending registration requests
     */
    public List<UserRegistrationRequestDto> findPending() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        
        // Get pending requests
        TypedQuery<UserRegistrationRequest> q = em.createQuery(
                "select r from UserRegistrationRequest r where r.status = :status order by r.createDate desc", 
                UserRegistrationRequest.class);
        q.setParameter("status", RegistrationStatus.PENDING);
        List<UserRegistrationRequest> requestList = q.getResultList();
        
        // Build the DTO list
        List<UserRegistrationRequestDto> dtoList = new ArrayList<>();
        
        for (UserRegistrationRequest request : requestList) {
            UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
            dto.setId(request.getId());
            dto.setUsername(request.getUsername());
            dto.setEmail(request.getEmail());
            dto.setCreateDate(request.getCreateDate());
            dto.setStatus(request.getStatus());
            
            dtoList.add(dto);
        }
        
        return dtoList;
    }
} 