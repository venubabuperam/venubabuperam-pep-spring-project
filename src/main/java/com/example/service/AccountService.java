package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    AccountRepository accountRepository;

    /*
     * The AccountRepository has been Autowired into this class via Constructor injection below. A bean for it 
     * will be injected and made available that way.
     * @param accountRepository
     */
    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /*
     * Add a new user account in the database and return it
     */
    public void addAccount(Account newAccount) {
        // Optional<Account> accountOptional = accountRepository.findAccountByUsername(newAccount.getUsername());
        // if( accountOptional.isEmpty() && newAccount.getPassword().length() >= 4 && !newAccount.getUsername().isEmpty()  ) {
        //     accountRepository.save(newAccount);
        // } else {
        //     if(!accountOptional.isEmpty())
        // }
         accountRepository.save(newAccount);
    }

     /*
     * retrieve and return all accounts found in the db
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // public Account retrieveAccountByUsername(Account checkAcctByName) {
    //     Optional<Account> checkAcctByNameOptional = accountRepository.findAccountByUsername(checkAcctByName.getUsername());
    //     return checkAcctByNameOptional.get();
    // }
    public Optional<Account> retrieveAccountByUsername(String checkAcctByName) {
        Optional<Account> checkAcctByNameOptional = accountRepository.findAccountByUsername(checkAcctByName);
        return checkAcctByNameOptional;
    }

    public Optional<Account> retrieveAccountById(int checkAcctById) {
        Optional<Account> checkAcctByIdOptional = accountRepository.findAccountByID(checkAcctById);
        return checkAcctByIdOptional;
    }
    
}