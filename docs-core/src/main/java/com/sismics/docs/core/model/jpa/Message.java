package com.sismics.docs.core.model.jpa;

import com.google.common.base.MoreObjects;
import com.sismics.docs.core.constant.MessageType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

/**
 * Message entity.
 * 
 * @author jaredan
 */
@Entity
@Table(name = "T_MESSAGE")
public class Message implements Loggable {
    /**
     * Message ID.
     */
    @Id
    @Column(name = "MSG_ID_C", length = 36)
    private String id;
    
    /**
     * Content.
     */
    @Column(name = "MSG_CONTENT_C", nullable = false, length = 4000)
    private String content;
    
    /**
     * Type (USER or GROUP).
     */
    @Column(name = "MSG_IDTYPE_C", nullable = false, length = 20)
    private String type;
    
    /**
     * Target ID (user or group ID).
     */
    @Column(name = "MSG_IDTARGET_C", nullable = false, length = 36)
    private String targetId;
    
    /**
     * Creator ID.
     */
    @Column(name = "MSG_IDUSER_C", nullable = false, length = 36)
    private String userId;
    
    /**
     * Creation date.
     */
    @Column(name = "MSG_CREATEDATE_D", nullable = false)
    private Date createDate;
    
    /**
     * Deletion date.
     */
    @Column(name = "MSG_DELETEDATE_D")
    private Date deleteDate;

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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("targetId", targetId)
                .add("userId", userId)
                .toString();
    }

    @Override
    public String toMessage() {
        return content;
    }
} 