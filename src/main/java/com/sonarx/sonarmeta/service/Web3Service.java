package com.sonarx.sonarmeta.service;

import java.math.BigInteger;
import java.util.List;

public interface Web3Service {

    void fundTreasury(BigInteger amount);

    void transferERC20UsingSonarMetaAllowance(String to, BigInteger amount);

    void grantERC721UsingSonarMetaApproval(Long tokenId, String to);

    void transferERC721UsingSonarMetaApproval(Long tokenId, String to);

    Long mintERC721(String to);

    void grantERC998UsingSonarMetaApproval(Long tokenId, String to);

    void transferERC998UsingSonarMetaApproval(Long tokenId, String to);

    Long mintERC998(String to);

    Long mintERC998WithBatchTokens(String to, List<Long> childTokenIds);


}
