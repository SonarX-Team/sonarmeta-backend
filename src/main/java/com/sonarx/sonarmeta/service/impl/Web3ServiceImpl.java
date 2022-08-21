package com.sonarx.sonarmeta.service.impl;

import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.enums.BusinessError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @description: Web3 相关功能
 * @author: liuxuanming
 */
@Slf4j
@Service
public class Web3ServiceImpl {

    @Resource
    Web3j web3j;

    @Resource
    public static final int pollingInterval = 3000;

    public Long getHeight() {
        EthBlockNumber blockNumber = null;

        try {
            blockNumber = web3j.ethBlockNumber().send();
        } catch (IOException e) {
            throw new BusinessException(BusinessError.TRANSACTION_ERROR + e.getMessage());
        }

        return blockNumber.getBlockNumber().longValue();
    }

    public BigInteger getNonce(Web3j web3j, String address) {
        try {
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
            return ethGetTransactionCount.getTransactionCount();
        } catch (IOException e) {
            throw new BusinessException(BusinessError.TRANSACTION_ERROR + e.getMessage());
        }
    }

    public BigDecimal getEthBalance(Web3j web3j, String address) {
        try {
            EthGetBalance balanceWei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            return Convert.fromWei(balanceWei.getBalance().toString(), Convert.Unit.ETHER);
        } catch (IOException e) {
            throw new BusinessException(BusinessError.TRANSACTION_ERROR + e.getMessage());
        }
    }

    public TransactionReceipt signAndSendTransaction(
            BigInteger nonce,
            BigInteger gasPrice, // BigInteger gasLimit = BigInteger.valueOf(21000);
            BigInteger gasLimit, // BigInteger gasPrice = Convert.toWei("1", Unit.GWEI).toBigInteger();
            String recipientAddress,
            BigInteger value,
            String data,
            String privateKey) {
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, recipientAddress, value, data);

        // Sign the transaction
        if (privateKey.startsWith("0x")) {
            privateKey = privateKey.substring(2);
        }
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, getAccountFromPrivateKey(privateKey));
        // Convert it to Hexadecimal String to be sent to the node
        String signedDataHexValue = Numeric.toHexString(signedMessage);

        if (!"".equals(signedDataHexValue)) {
            try {
                EthSendTransaction send = web3j.ethSendRawTransaction(signedDataHexValue).send();
                TransactionReceiptProcessor receiptProcessor =
                        new PollingTransactionReceiptProcessor(web3j, TransactionManager.DEFAULT_POLLING_FREQUENCY,
                                TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
                return receiptProcessor.waitForTransactionReceipt(send.getTransactionHash());
            } catch (IOException | TransactionException e) {
                throw new BusinessException(BusinessError.TRANSACTION_ERROR + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Call contract function with parameters,
     * but don't modify the contract state.
     */
    public List<Type> callContractWithParameters(String from, String contractAddress, String methodName, List<Type> inputParameters, List<TypeReference<?>> outputParameters) {
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        try {
            Transaction transaction = Transaction.createEthCallTransaction(from, contractAddress, data);
            EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            return FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        } catch (IOException e) {
            throw new BusinessException(BusinessError.TRANSACTION_ERROR + e.getMessage());
        }
    }

    /**
     * Call contract function with parameters, may modify the contract state,
     * which means the transaction should be signed by the credential.
     */
    public TransactionReceipt invokeContractWithParameters(String from, Credentials credentials, String contractAddress, String methodName, List<Type> inputParameters, List<TypeReference<?>> outputParameters) {
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        try {
            TransactionManager transactionManager = new FastRawTransactionManager(web3j, credentials);
            String txHash = transactionManager.sendTransaction(DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT,
                    contractAddress, data, BigInteger.ZERO).getTransactionHash();

            TransactionReceiptProcessor receiptProcessor =
                    new PollingTransactionReceiptProcessor(web3j, TransactionManager.DEFAULT_POLLING_FREQUENCY,
                            TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
            return receiptProcessor.waitForTransactionReceipt(txHash);
        } catch (IOException | TransactionException e) {
            throw new BusinessException(BusinessError.TRANSACTION_ERROR + e.getMessage());
        }
    }

    public static Credentials getAccountFromCredentials(String password, String path) throws CipherException, IOException {
        return WalletUtils.loadCredentials(password, path);
        // String accountAddress = credentials.getAddress();
        // String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
    }

    public static Credentials getAccountFromMnemonic(String password, String mnemonic) {
        return WalletUtils.loadBip39Credentials(password, mnemonic);
    }

    public static Credentials getAccountFromPrivateKey(String privateKey) {
        return Credentials.create(privateKey);
    }

    public TransactionReceipt transferEth(Credentials credentials, String recipientAddress, BigDecimal amount, Convert.Unit unit) {
        try {
            return Transfer.sendFunds(web3j, credentials, recipientAddress, amount, unit).send();
        } catch (Exception e) {
            throw new BusinessException(BusinessError.TRANSACTION_ERROR + e.getMessage());
        }
    }

}
