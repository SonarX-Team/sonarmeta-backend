package com.sonarx.sonarmeta.service;

import com.sonarx.sonarmeta.common.Web3TransactionException;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;

public interface Web3Service {

    void fundTreasury(Double amount) throws Web3TransactionException;

    void transferERC20(String to, Double amount) throws Web3TransactionException;

    void transferERC20UsingSonarMetaAllowance(String from, String to, Double amount) throws Web3TransactionException;

    void grantERC721UsingSonarMetaApproval(Long tokenId, String to) throws Web3TransactionException;

    void transferERC721UsingSonarMetaApproval(Long tokenId, String to) throws Web3TransactionException;

    Long mintERC721(String to) throws Web3TransactionException;

    void grantERC998UsingSonarMetaApproval(Long tokenId, String to) throws Web3TransactionException;

    void transferERC998UsingSonarMetaApproval(Long tokenId, String to) throws Web3TransactionException;

    Long mintERC998(String to) throws Web3TransactionException;

    Long mintERC998WithBatchTokens(String to, List<Long> childTokenIds) throws Web3TransactionException;
}
