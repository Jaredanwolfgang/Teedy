package com.sismics.docs.core.dao.dto;

/**
 * Message DTO.
 *
 * @author jaredan
 */
public class MessageDto {
    /**
     * Message ID.
     */
    private String id;
    
    /**
     * Message content.
     */
    private String content;
    
    /**
     * Message type.
     */
    private String type;
    
    /**
     * Target ID.
     */
    private String targetId;
    
    /**
     * User ID.
     */
    private String userId;
    
    /**
     * Creator name.
     */
    private String creatorName;
    
    /**
     * Creator email.
     */
    private String creatorEmail;
    
    /**
     * Creation date.
     */
    private Long createTimestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }
} 