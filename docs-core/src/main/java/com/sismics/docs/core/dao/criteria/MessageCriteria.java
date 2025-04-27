package com.sismics.docs.core.dao.criteria;

import com.sismics.docs.core.constant.MessageType;

import java.util.List;

/**
 * Message criteria.
 *
 * @author jaredan
 */
public class MessageCriteria {
    /**
     * Target IDs.
     */
    private List<String> targetIdList;
    
    /**
     * Sender ID.
     */
    private String senderId;
    
    /**
     * Target type.
     */
    private MessageType targetType;

    public List<String> getTargetIdList() {
        return targetIdList;
    }

    public MessageCriteria setTargetIdList(List<String> targetIdList) {
        this.targetIdList = targetIdList;
        return this;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public MessageCriteria setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public MessageType getTargetType() {
        return targetType;
    }

    public MessageCriteria setTargetType(MessageType targetType) {
        this.targetType = targetType;
        return this;
    }
} 