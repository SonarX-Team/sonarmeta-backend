package com.sonarx.sonarmeta.service;

import com.sonarx.sonarmeta.common.EthTransactionException;

import java.math.BigInteger;
import java.util.List;

public interface Web3Service {

    void fundTreasury(BigInteger amount) throws EthTransactionException;

    void transferERC20UsingSonarMetaAllowance(String to, BigInteger amount) throws EthTransactionException;

    void grantERC721UsingSonarMetaApproval(Long tokenId, String to) throws EthTransactionException;

    void transferERC721UsingSonarMetaApproval(Long tokenId, String to) throws EthTransactionException;

    Long mintERC721(String to) throws EthTransactionException;

    void grantERC998UsingSonarMetaApproval(Long tokenId, String to) throws EthTransactionException;

    void transferERC998UsingSonarMetaApproval(Long tokenId, String to) throws EthTransactionException;

    Long mintERC998(String to) throws EthTransactionException;

    Long mintERC998WithBatchTokens(String to, List<Long> childTokenIds) throws EthTransactionException;


}
