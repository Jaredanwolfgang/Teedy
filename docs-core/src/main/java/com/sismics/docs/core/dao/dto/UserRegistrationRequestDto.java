package com.sismics.docs.core.dao.dto;

import java.util.Date;

/**
 * User registration request DTO.
 *
 * @author jaredan
 */
public class UserRegistrationRequestDto {
    /**
     * Request ID.
     */
    private String id;
    
    /**
     * Username.
     */
    private String username;
    
    /**
     * Email address.
     */
    private String email;
    
    /**
     * Creation date.
     */
    private Date createDate;
    
    /**
     * Status.
     */
    private String status;
    
    /**
     * Handled by (user ID).
     */
    private String handledBy;
    
    /**
     * Handling date.
     */
    private Date handlingDate;
    
    /**
     * Admin username who handled the request.
     */
    private String handledByUsername;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(String handledBy) {
        this.handledBy = handledBy;
    }

    public Date getHandlingDate() {
        return handlingDate;
    }

    public void setHandlingDate(Date handlingDate) {
        this.handlingDate = handlingDate;
    }

    public String getHandledByUsername() {
        return handledByUsername;
    }

    public void setHandledByUsername(String handledByUsername) {
        this.handledByUsername = handledByUsername;
    }
} 