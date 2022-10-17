package com.sonarx.sonarmeta.service.impl;

import com.alipay.mychain.sdk.api.MychainClient;
import com.alipay.mychain.sdk.api.utils.Utils;
import com.alipay.mychain.sdk.common.VMTypeEnum;
import com.alipay.mychain.sdk.domain.account.Identity;
import com.alipay.mychain.sdk.domain.transaction.LogEntry;
import com.alipay.mychain.sdk.message.transaction.TransactionReceiptResponse;
import com.alipay.mychain.sdk.message.transaction.contract.CallContractRequest;
import com.alipay.mychain.sdk.vm.EVMParameter;
import com.sonarx.sonarmeta.common.Web3TransactionException;
import com.sonarx.sonarmeta.domain.enums.BusinessError;
import com.sonarx.sonarmeta.service.Web3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: Mychain区块链服务实现
 * @author: liuxuanming
 */
@Primary
@Service
@Slf4j
public class MyChainServiceImpl implements Web3Service {

    @Resource
    MychainClient mychainClient;

    @Value("${mychain.account}")
    private String account;

    @Value("${mychain.contracts.main}")
    private String mainContract;

    @Value("${mychain.contracts.ERC20}")
    private String ERC20Contract;

    @Value("${mychain.contracts.ERC721}")
    private String ERC721Contract;

    @Value("${mychain.contracts.ERC998}")
    private String ERC998Contract;

    @Override
    public void fundTreasury(Double amount) throws Web3TransactionException {
        EVMParameter parameters = new EVMParameter("fundTreasury(uint256)");
        parameters.addUint(new BigDecimal(amount).multiply(new BigDecimal("1E18")).toBigInteger());

        TransactionReceiptResponse receipt = mychainClient.getContractService()
                .callContract(
                        new CallContractRequest(
                                Utils.getIdentityByName(account),
                                new Identity(mainContract),
                                parameters,
                                BigInteger.ZERO,
                                VMTypeEnum.EVM)
                );
        log.info("调用合约方法，得到transactionHash：{}", receipt.getTxHash());
        if (!receipt.isSuccess() || receipt.getTransactionReceipt().getResult() != 0) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc() + receipt.getExceptionMessage());
        }
    }

    @Override
    public void transferERC20(String to, Double amount) throws Web3TransactionException {
        EVMParameter parameters = new EVMParameter("fundTreasury(uint256)");
        parameters.addIdentity(new Identity(to));
        parameters.addUint(new BigDecimal(amount).multiply(new BigDecimal("1E18")).toBigInteger());

        TransactionReceiptResponse receipt = mychainClient.getContractService()
                .callContract(
                        new CallContractRequest(
                                Utils.getIdentityByName(account),
                                new Identity(ERC20Contract),
                                parameters,
                                BigInteger.ZERO,
                                VMTypeEnum.EVM)
                );
        log.info("调用合约方法，得到transactionHash：{}", receipt.getTxHash());
        if (!receipt.isSuccess() || receipt.getTransactionReceipt().getResult() != 0) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc() + receipt.getExceptionMessage());
        }
    }

    @Override
    public void transferERC20UsingSonarMetaAllowance(String from, String to, Double amount) throws Web3TransactionException {
        EVMParameter parameters = new EVMParameter("transferERC20UsingSonarMetaAllowance(identity,identity,uint256)");
        parameters.addIdentity(new Identity(from));
        parameters.addIdentity(new Identity(to));
        parameters.addUint(new BigDecimal(amount).multiply(new BigDecimal("1E18")).toBigInteger());

        TransactionReceiptResponse receipt = mychainClient.getContractService()
                .callContract(
                        new CallContractRequest(
                                Utils.getIdentityByName(account),
                                new Identity(mainContract),
                                parameters,
                                BigInteger.ZERO,
                                VMTypeEnum.EVM)
                );
        log.info("调用合约方法，得到transactionHash：{}", receipt.getTxHash());
        if (!receipt.isSuccess() || receipt.getTransactionReceipt().getResult() != 0) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc() + receipt.getExceptionMessage());
        }
    }

    @Override
    public void grantERC721UsingSonarMetaApproval(Long tokenId, String to) throws Web3TransactionException {
        EVMParameter parameters = new EVMParameter("grantERC721UsingSonarMetaApproval(uint256,identity)");
        parameters.addUint(BigInteger.valueOf(tokenId));
        parameters.addIdentity(new Identity(to));

        TransactionReceiptResponse receipt = mychainClient.getContractService()
                .callContract(
                        new CallContractRequest(
                                Utils.getIdentityByName(account),
                                new Identity(ERC721Contract),
                                parameters,
                                BigInteger.ZERO,
                                VMTypeEnum.EVM)
                );
        log.info("调用合约方法，得到transactionHash：{}", receipt.getTxHash());
        if (!receipt.isSuccess() || receipt.getTransactionReceipt().getResult() != 0) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc() + receipt.getExceptionMessage());
        }
    }

    @Override
    public void transferERC721UsingSonarMetaApproval(Long tokenId, String to) throws Web3TransactionException {
        EVMParameter parameters = new EVMParameter("transferERC721UsingSonarMetaApproval(uint256,identity)");
        parameters.addUint(BigInteger.valueOf(tokenId));
        parameters.addIdentity(new Identity(to));

        TransactionReceiptResponse receipt = mychainClient.getContractService()
                .callContract(
                        new CallContractRequest(
                                Utils.getIdentityByName(account),
                                new Identity(ERC721Contract),
                                parameters,
                                BigInteger.ZERO,
                                VMTypeEnum.EVM)
                );
        log.info("调用合约方法，得到transactionHash：{}", receipt.getTxHash());
        if (!receipt.isSuccess() || receipt.getTransactionReceipt().getResult() != 0) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc() + receipt.getExceptionMessage());
        }
    }

    @Override
    public Long mintERC721(String to) throws Web3TransactionException {
        EVMParameter parameters = new EVMParameter("mintERC721(identity)");
        parameters.addIdentity(new Identity(to));

        TransactionReceiptResponse receipt = mychainClient.getContractService()
                .callContract(
                        new CallContractRequest(
                                Utils.getIdentityByName(account),
                                new Identity(ERC721Contract),
                                parameters,
                                BigInteger.ZERO,
                                VMTypeEnum.EVM)
                );
        log.info("调用合约方法，得到transactionHash：{}", receipt.getTxHash());
        if (!receipt.isSuccess() || receipt.getTransactionReceipt().getResult() != 0) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc() + receipt.getExceptionMessage());
        }

        // Get return value from logs in receipt
        List<LogEntry> logs = receipt.getTransactionReceipt().getLogs();
        if (logs.size() == 0) {
            throw new Web3TransactionException(BusinessError.INCORRECT_CONTRACT.getDesc());
        }
        List<String> topics = receipt.getTransactionReceipt().getLogs().get(1).getTopics();
        String requiredTopic = topics != null && topics.size() > 1 ? topics.get(2) : null;
        if (requiredTopic == null) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc());
        }
        return Long.parseLong(requiredTopic.substring(2), 16);
    }

    @Override
    public void grantERC998UsingSonarMetaApproval(Long tokenId, String to) throws Web3TransactionException {
        EVMParameter parameters = new EVMParameter("grantERC998UsingSonarMetaApproval(uint256,identity)");
        parameters.addUint(BigInteger.valueOf(tokenId));
        parameters.addIdentity(new Identity(to));

        TransactionReceiptResponse receipt = mychainClient.getContractService()
                .callContract(
                        new CallContractRequest(
                                Utils.getIdentityByName(account),
                                new Identity(ERC998Contract),
                                parameters,
                                BigInteger.ZERO,
                                VMTypeEnum.EVM)
                );
        log.info("调用合约方法，得到transactionHash：{}", receipt.getTxHash());
        if (!receipt.isSuccess() || receipt.getTransactionReceipt().getResult() != 0) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc() + receipt.getExceptionMessage());
        }
    }

    @Override
    public void transferERC998UsingSonarMetaApproval(Long tokenId, String to) throws Web3TransactionException {
        EVMParameter parameters = new EVMParameter("transferERC998UsingSonarMetaApproval(uint256,identity)");
        parameters.addUint(BigInteger.valueOf(tokenId));
        parameters.addIdentity(new Identity(to));

        TransactionReceiptResponse receipt = mychainClient.getContractService()
                .callContract(
                        new CallContractRequest(
                                Utils.getIdentityByName(account),
                                new Identity(ERC998Contract),
                                parameters,
                                BigInteger.ZERO,
                                VMTypeEnum.EVM)
                );
        log.info("调用合约方法，得到transactionHash：{}", receipt.getTxHash());
        if (!receipt.isSuccess() || receipt.getTransactionReceipt().getResult() != 0) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc() + receipt.getExceptionMessage());
        }
    }

    @Override
    public Long mintERC998(String to) throws Web3TransactionException {
        EVMParameter parameters = new EVMParameter("mintERC998(identity)");
        parameters.addIdentity(new Identity(to));

        TransactionReceiptResponse receipt = mychainClient.getContractService()
                .callContract(
                        new CallContractRequest(
                                Utils.getIdentityByName(account),
                                new Identity(ERC998Contract),
                                parameters,
                                BigInteger.ZERO,
                                VMTypeEnum.EVM)
                );
        log.info("调用合约方法，得到transactionHash：{}", receipt.getTxHash());
        if (!receipt.isSuccess() || receipt.getTransactionReceipt().getResult() != 0) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc() + receipt.getExceptionMessage());
        }

        // Get return value from logs in receipt
        List<LogEntry> logs = receipt.getTransactionReceipt().getLogs();
        if (logs.size() == 0) {
            throw new Web3TransactionException(BusinessError.INCORRECT_CONTRACT.getDesc());
        }
        List<String> topics = receipt.getTransactionReceipt().getLogs().get(1).getTopics();
        String requiredTopic = topics != null && topics.size() > 1 ? topics.get(2) : null;
        if (requiredTopic == null) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc());
        }
        return Long.parseLong(requiredTopic.substring(2), 16);
    }

    @Override
    public Long mintERC998WithBatchTokens(String to, List<Long> childTokenIds) throws Web3TransactionException {
        EVMParameter parameters = new EVMParameter("mintERC998WithBatchTokens(identity, uint256[])");
        parameters.addIdentity(new Identity(to));
        parameters.addUintArray(childTokenIds.stream().map(BigInteger::valueOf).collect(Collectors.toList()));

        TransactionReceiptResponse receipt = mychainClient.getContractService()
                .callContract(
                        new CallContractRequest(
                                Utils.getIdentityByName(account),
                                new Identity(ERC998Contract),
                                parameters,
                                BigInteger.ZERO,
                                VMTypeEnum.EVM)
                );
        log.info("调用合约方法，得到transactionHash：{}", receipt.getTxHash());
        if (!receipt.isSuccess() || receipt.getTransactionReceipt().getResult() != 0) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc() + receipt.getExceptionMessage());
        }

        // Get return value from logs in receipt
        List<LogEntry> logs = receipt.getTransactionReceipt().getLogs();
        if (logs.size() == 0) {
            throw new Web3TransactionException(BusinessError.INCORRECT_CONTRACT.getDesc());
        }
        List<String> topics = receipt.getTransactionReceipt().getLogs().get(1).getTopics();
        String requiredTopic = topics != null && topics.size() > 1 ? topics.get(2) : null;
        if (requiredTopic == null) {
            throw new Web3TransactionException(BusinessError.ETH_TRANSACTION_ERROR.getDesc());
        }
        return Long.parseLong(requiredTopic.substring(2), 16);
    }
}
