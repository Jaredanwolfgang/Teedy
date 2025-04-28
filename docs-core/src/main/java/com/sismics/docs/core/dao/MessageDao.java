package com.sismics.docs.core.dao;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sismics.docs.core.constant.AuditLogType;
import com.sismics.docs.core.constant.MessageType;
import com.sismics.docs.core.dao.criteria.MessageCriteria;
import com.sismics.docs.core.dao.dto.MessageDto;
import com.sismics.docs.core.model.jpa.Message;
import com.sismics.docs.core.util.AuditLogUtil;
import com.sismics.docs.core.util.jpa.QueryParam;
import com.sismics.docs.core.util.jpa.QueryUtil;
import com.sismics.docs.core.util.jpa.SortCriteria;
import com.sismics.util.context.ThreadLocalContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import java.util.*;

/**
 * Message DAO.
 * 
 * @author jaredan
 */
public class MessageDao {
    /**
     * Creates a new message.
     * 
     * @param message Message
     * @param userId User ID
     * @return ID
     */
    public String create(Message message, String userId) {
        // Create the UUID
        message.setId(UUID.randomUUID().toString());
        message.setCreateDate(new Date());
        
        // Create the message
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(message);
        
        // Create audit log
        AuditLogUtil.create(message, AuditLogType.CREATE, userId);
        
        return message.getId();
    }
    
    /**
     * Gets a message by ID.
     * 
     * @param id Message ID
     * @return Message
     */
    public Message getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(Message.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Get active messages by criteria.
     * 
     * @param criteria Search criteria
     * @param sortCriteria Sort criteria
     * @return List of messages
     */
    public List<MessageDto> findByCriteria(MessageCriteria criteria, SortCriteria sortCriteria) {
        Map<String, Object> parameterMap = new HashMap<>();
        List<String> criteriaList = new ArrayList<>();
        
        StringBuilder sb = new StringBuilder("select m.MSG_ID_C, m.MSG_CONTENT_C, m.MSG_IDTYPE_C, m.MSG_IDTARGET_C, m.MSG_IDUSER_C, m.MSG_CREATEDATE_D, " +
                "u.USE_USERNAME_C, u.USE_EMAIL_C ")
                .append(" from T_MESSAGE m ");
        
        // Join with the user
        sb.append(" join T_USER u on u.USE_ID_C = m.MSG_IDUSER_C ");
        
        // Add search criteria
        criteriaList.add("m.MSG_DELETEDATE_D is null");
        
        // For bidirectional conversation between two specific users
        if (criteria.getTargetIdList() != null && !criteria.getTargetIdList().isEmpty() && criteria.getSenderId() != null) {
            String targetId = criteria.getTargetIdList().get(0);
            
            // We want to get messages where:
            // 1. Current user is the sender and target user is the recipient, OR
            // 2. Target user is the sender and current user is the recipient
            sb.append(" and (");
            
            // Current user sent to target
            sb.append("(m.MSG_IDUSER_C = :senderId and m.MSG_IDTARGET_C = :targetId and m.MSG_IDTYPE_C = :targetType)");
            
            // Target sent to current user
            sb.append(" or ");
            sb.append("(m.MSG_IDUSER_C = :targetId and m.MSG_IDTARGET_C = :senderId and m.MSG_IDTYPE_C = :targetType)");
            
            sb.append(")");
            
            parameterMap.put("senderId", criteria.getSenderId());
            parameterMap.put("targetId", targetId);
            parameterMap.put("targetType", criteria.getTargetType().name());
        } else {
            // Original logic for non-bidirectional queries
            if (criteria.getTargetIdList() != null && !criteria.getTargetIdList().isEmpty()) {
                criteriaList.add("m.MSG_IDTARGET_C in (:targetIdList)");
                parameterMap.put("targetIdList", criteria.getTargetIdList());
            }
            
            if (criteria.getTargetType() != null) {
                criteriaList.add("m.MSG_IDTYPE_C = :targetType");
                parameterMap.put("targetType", criteria.getTargetType().name());
            }
            
            sb.append(" where ");
            sb.append(Joiner.on(" and ").join(criteriaList));
        }
        
        // Add order
        if (sortCriteria != null) {
            sb.append(" order by ");
            sb.append(sortCriteria.getColumn());
            if (!sortCriteria.isAsc()) {
                sb.append(" desc");
            }
        } else {
            sb.append(" order by m.MSG_CREATEDATE_D asc");
        }
        
        // Execute the query
        QueryParam queryParam = new QueryParam(sb.toString(), parameterMap);
        Query q = QueryUtil.getNativeQuery(queryParam);
        @SuppressWarnings("unchecked")
        List<Object[]> l = q.getResultList();
        
        // Assemble results
        List<MessageDto> messageDtoList = new ArrayList<>();
        for (Object[] o : l) {
            int i = 0;
            MessageDto messageDto = new MessageDto();
            messageDto.setId((String) o[i++]);
            messageDto.setContent((String) o[i++]);
            messageDto.setType((String) o[i++]);
            messageDto.setTargetId((String) o[i++]);
            messageDto.setUserId((String) o[i++]);
            messageDto.setCreateTimestamp(((Date) o[i++]).getTime());
            messageDto.setCreatorName((String) o[i++]);
            messageDto.setCreatorEmail((String) o[i++]);
            messageDtoList.add(messageDto);
        }
        
        return messageDtoList;
    }

    /**
     * Delete a message.
     * 
     * @param id Message ID
     * @param userId User ID
     */
    public void delete(String id, String userId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        
        // Get the message
        Message messageDb = getById(id);
        if (messageDb == null) {
            return;
        }
        
        // Delete the message
        Date dateNow = new Date();
        messageDb.setDeleteDate(dateNow);
        
        // Create audit log
        AuditLogUtil.create(messageDb, AuditLogType.DELETE, userId);
    }
} 