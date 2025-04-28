package com.sismics.docs.core.model.jpa;

import com.google.common.base.MoreObjects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

/**
 * User registration request entity.
 * 
 * @author jaredan
 */
@Entity
@Table(name = "T_USER_REGISTRATION_REQUEST")
public class UserRegistrationRequest implements Loggable {
    /**
     * Request ID.
     */
    @Id
    @Column(name = "URR_ID_C", length = 36)
    private String id;
    
    /**
     * Username.
     */
    @Column(name = "URR_USERNAME_C", nullable = false, length = 50)
    private String username;
    
    /**
     * Password.
     */
    @Column(name = "URR_PASSWORD_C", nullable = false, length = 100)
    private String password;

    /**
     * Email address.
     */
    @Column(name = "URR_EMAIL_C", nullable = false, length = 100)
    private String email;
    
    /**
     * Creation date.
     */
    @Column(name = "URR_CREATEDATE_D", nullable = false)
    private Date createDate;
    
    /**
     * Status: 'PENDING', 'APPROVED', 'REJECTED'
     */
    @Column(name = "URR_STATUS_C", nullable = false, length = 10)
    private String status;
    
    /**
     * Handled by (user ID).
     */
    @Column(name = "URR_HANDLEDBY_C", length = 36)
    private String handledBy;
    
    /**
     * Handling date.
     */
    @Column(name = "URR_HANDLINGDATE_D")
    private Date handlingDate;

    public String getId() {
        return id;
    }

    public UserRegistrationRequest setId(String id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserRegistrationRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserRegistrationRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserRegistrationRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public UserRegistrationRequest setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserRegistrationRequest setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public UserRegistrationRequest setHandledBy(String handledBy) {
        this.handledBy = handledBy;
        return this;
    }

    public Date getHandlingDate() {
        return handlingDate;
    }

    public UserRegistrationRequest setHandlingDate(Date handlingDate) {
        this.handlingDate = handlingDate;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("username", username)
                .add("email", email)
                .add("status", status)
                .toString();
    }

    @Override
    public Date getDeleteDate() {
        return null;
    }

    @Override
    public String toMessage() {
        return username;
    }
} 