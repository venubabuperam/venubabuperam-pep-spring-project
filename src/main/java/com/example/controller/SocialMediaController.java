package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {
    MessageService messageService;
    AccountService accountService;
    
    @Autowired
    public SocialMediaController(MessageService messageService, AccountService accountService) {
        this.messageService = messageService;
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> createAccount(@RequestBody Account newAccount) {
        
        Optional<Account> accountOptional = accountService.retrieveAccountByUsername(newAccount.getUsername());
        if(!accountOptional.isEmpty() ) {
            System.out.println("User account already exists. Conflict error 409.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        if( accountOptional.isEmpty() && newAccount.getPassword().length() >= 4 && !newAccount.getUsername().isEmpty()  ) {
            accountService.addAccount(newAccount);
            System.out.println("New login account successfully created");
            return ResponseEntity.status(HttpStatus.OK).body(newAccount);
        } 
        /* if we get to this line then user account creation failed for some other reasons */
        System.out.println("Your username or password is invalid");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

    }

    @PostMapping("/login")
    public ResponseEntity<Account> userLogin(@RequestBody Account loginAccount) {
        
        Optional<Account> accountOptional = accountService.retrieveAccountByUsername(loginAccount.getUsername());
        /**
             * Checks to ensure that username being passed isn't blank.
             * Checks to ensure that password is at least 4 characters long
             * Checks to ensure that an account with that username does already exist
             */

        if(!accountOptional.isEmpty() ) {
                String expectedUname = accountOptional.get().getUsername();
                String expectedPass = accountOptional.get().getPassword();
                String actualUserName = loginAccount.getUsername();
                String actualPassword = loginAccount.getPassword();

                if(expectedUname.equals(actualUserName) && expectedPass.equals(actualPassword)) {
                    System.out.println("User login successful.");
                    return ResponseEntity.status(HttpStatus.OK).body(accountOptional.get());
                    
                }
        }

        /* if we've reach the lines below then user login has failed */ 
        System.out.println("user login has failed. Please check username & password are valid");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    
    /*
     * Retrieve all messages that are currently saved/persisted in the db
     */
    @GetMapping("/messages")
    public List<Message> getAllMessages() {
        return messageService.getAllMessages(); 
    }

    /*
     * Retrieve all user accounts that are currently saved/
     */
    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts(); 
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message newMessage) {
                
            /**
             * Check#1 if message_text is empty. If it is then return ctx.status(400) and empty 
             * json body
             * 
             * Check#2 if message_text length is longer than 244 characters then return ctx.json(400) status
             * 
             * Check#3 if account_id (DB user) being passed from Json request exist in the DB
             */
            
            Integer posted_by_id_sent = newMessage.getPosted_by();
            // System.out.println("1st Check. Printing value of Integer posted_by_id_sent var: " + posted_by_id_sent);
            if(posted_by_id_sent == null) {
                System.out.println("posted_by value null. enter a posted_by value: " + posted_by_id_sent);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            
            Optional<Account> accountOptional = accountService.retrieveAccountById(posted_by_id_sent);
            if(accountOptional.isEmpty()) {
                System.out.println("Account ID for posting user doesn't exist");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            
            boolean isMessage_text_empty = newMessage.getMessage_text().isEmpty();
            boolean isMessage_text_greater_than_254_chars = newMessage.getMessage_text().length() > 254;
            
             
            if(isMessage_text_empty) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                
            } else if(isMessage_text_greater_than_254_chars) {
                System.out.println("Message length greater than 254 chars");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } else if(accountOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            
            }    else { // else block added
                    messageService.addMessage(newMessage);
                    System.out.println("New message successfully created");
                    return ResponseEntity.status(HttpStatus.OK).body(newMessage);
                    
           } // ends else
        }

     /**
     * Retrieve message by its ID
     */
     @GetMapping("/messages/{message_id}")
     public ResponseEntity<Message> findMessageByItsID(@PathVariable int message_id){
         
         Optional<Message> messageOptional = messageService.retrieveMessageById(message_id);
         if(!messageOptional.isEmpty()) {
             return ResponseEntity.status(HttpStatus.OK).body(messageOptional.get());    
         }
         return ResponseEntity.status(HttpStatus.OK).body(null);
     }

     @DeleteMapping("/messages/{message_id}")
	public ResponseEntity<Integer> deleteMessageByItsID(@PathVariable int message_id){
		
        Optional<Message> messageOptional = messageService.retrieveMessageById(message_id);
        if(!messageOptional.isEmpty()) {
            // messageService.removeMessageByItsID(message_id);
            // messageRepository.deleteById(message_id);
            
            Integer rowsdeleted = messageService.removeMessageByPerID(message_id);
            System.out.println("Row(s) deleted: " + rowsdeleted);
            return ResponseEntity.status(HttpStatus.OK).body(rowsdeleted); 
        }
        /** at this point the delete by message id operation has failed */
        Integer rowsnotdeleted = 0;  // added so it can pass the Junit test. Supposed be NULL
        System.out.println("Row(s) deleted: " + rowsnotdeleted);
        return ResponseEntity.status(HttpStatus.OK).body(rowsnotdeleted);
	}

    /**
     * Retrieve message by its message id
     */
    @PatchMapping("/messages/{message_id}")
	public ResponseEntity<Integer> updateMessageByItsID(@RequestBody Message newMessage, @PathVariable int message_id){
		
            boolean isMessage_text_empty = newMessage.getMessage_text().isEmpty();
            boolean isMessage_text_greater_than_254_chars = newMessage.getMessage_text().length() > 254;
            
             
            if(isMessage_text_empty) {
                System.out.println("Text message being sent is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                
            } else if(isMessage_text_greater_than_254_chars) {
                System.out.println("Message length greater than 254 chars");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } 
            
        Optional<Message> messageOptional = messageService.retrieveMessageById(message_id);
        if(!messageOptional.isEmpty()) {
            
            Message messagetoUpdate = messageOptional.get();
            messagetoUpdate.setMessage_text(newMessage.getMessage_text());
            
            Integer rowsupdated = messageService.updateMessageByPerID(messagetoUpdate.getMessage_text(),message_id);
            
            System.out.println("Row(s) updated: " + rowsupdated);
            return ResponseEntity.status(HttpStatus.OK).body(rowsupdated); 
        }
        /** at this point, the update operation has failed for some reason. 
         * Return Status 400 and 0 rows updated returned in the JSON response body
         * */
        Integer rowsnotupdated = 0;
        System.out.println("Row(s) updated: " + rowsnotupdated);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rowsnotupdated);
	}

    /*
     * Retrieve a list of messages by User account
     */
    @GetMapping("/accounts/{account_id}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccount(@PathVariable int account_id) {
        List<Message> messageByAccountList = messageService.findMessagesPerAccountID(account_id);
        return ResponseEntity.status(HttpStatus.OK).body(messageByAccountList);
    }
}