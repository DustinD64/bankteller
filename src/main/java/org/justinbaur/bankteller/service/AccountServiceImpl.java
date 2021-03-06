package org.justinbaur.bankteller.service;

import org.justinbaur.bankteller.domain.Account;
import org.justinbaur.bankteller.exceptions.AccountNotFound;
import org.justinbaur.bankteller.exceptions.JsonReadException;
import org.justinbaur.bankteller.exceptions.JsonWriteException;
import org.justinbaur.bankteller.exceptions.UpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountServiceImpl implements AccountService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    JsonFileHandler handler;

    Time myTime = Time.getInstance();

    public AccountServiceImpl(){
        LOG.info(myTime.printTime() + " - Setting up in memory bank accounts");
    }

    public Integer getBalance(Integer accountId) throws AccountNotFound {
        if (handler.getAccountsMap().containsKey(accountId)) {
            return handler.getAccountsMap().get(accountId).getBalance();
        } else {
            throw new AccountNotFound("No account found");
        }
    }

    public void addBalance(Integer accountId, Integer addAmount) throws AccountNotFound, UpdateException {
        if (handler.getAccountsMap().containsKey(accountId)) {
            Account acct = handler.getAccountsMap().get(accountId);
            acct.setBalance(getBalance(accountId) + addAmount);
            try {
                handler.updateAccount(acct);
            } catch (JsonReadException e) {
                LOG.error("Failed to read from file:" + e.getLocalizedMessage());
            } catch (JsonWriteException e) {
                LOG.error("Failed to write file:" + e.getLocalizedMessage());
            }
        } else {
            throw new AccountNotFound("No account found");
        }
    }

    public void subtractBalance(Integer accountId, Integer subtractAmount) throws AccountNotFound, UpdateException {
        if (handler.getAccountsMap().containsKey(accountId)) {
            Account acct = handler.getAccountsMap().get(accountId);
            acct.setBalance(getBalance(accountId) - subtractAmount);
            try {
                handler.updateAccount(acct);
            } catch (JsonReadException e) {
                LOG.error("Failed to read from file:" + e.getLocalizedMessage());
            } catch (JsonWriteException e) {
                LOG.error("Failed to write file:" + e.getLocalizedMessage());
            }
        } else {
            throw new AccountNotFound("No account found");
        }
    }

    @Override
    public Boolean checkAccount(Integer accountId) {
        return handler.getAccountsMap().containsKey(accountId);
    }
}
