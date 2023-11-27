package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    MessageRepository messageRepository;
    
    /*
     * The MessageRepository has been Autowired into this class via Constructor injection below. A bean for it 
     * will be injected and made available that way.
     * @param messageRepository
     */
    @Autowired
    public MessageService (MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /*
     * Add a message and persist/record it in the db method 
     * and return this new persisted message entity
     * @param message a new entity being passed as an arg
     */
    public Message addMessage(Message message) {
        return messageRepository.save(message);
    }

    /*
     * return all message entities found in the db method
     */
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> retrieveMessageById(int message_id) {
        Optional<Message> checkMessgByIdOptional = messageRepository.findMessageByID(message_id);
        return checkMessgByIdOptional;
    }

    @Transactional
    public int removeMessageByPerID(int message_id) {
        // Optional<Message> deleteMessgByIdOptional = messageRepository.deleteMessageByID(message_id);
        int rowsdeleted = messageRepository.deleteMessagePerID(message_id);
        
        return rowsdeleted;
    }

    @Transactional
    public int updateMessageByPerID(String message_text, int message_id) {
        int rowsupdated = messageRepository.updateMessagePerItsID(message_text,message_id);
        
        return rowsupdated;
    }

    public List<Message> findMessagesPerAccountID(int account_id) {
        List<Message> messageListByAcctId = messageRepository.retrieveMessagesByAccount(account_id);
        return messageListByAcctId;
    }
}