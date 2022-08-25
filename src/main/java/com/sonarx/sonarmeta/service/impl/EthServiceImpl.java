package com.sonarx.sonarmeta.service.impl;

import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.common.EthTransactionException;
import com.sonarx.sonarmeta.domain.enums.BusinessError;
import com.sonarx.sonarmeta.service.Web3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
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
import java.util.LinkedList;
import java.util.List;

/**
 * @description: Web3 相关功能
 * @author: liuxuanming
 */
@Slf4j
@Service
public class EthServiceImpl implements Web3Service {

    @Resource
    Web3j web3j;

    @Value("${web3j.contracts.main}")
    private String mainContract;

    @Value("${web3j.contracts.ERC20}")
    private String ERC20Contract;

    @Value("${web3j.contracts.ERC721}")
    private String ERC721Contract;

    @Value("${web3j.contracts.ERC998}")
    private String ERC998Contract;

    @Value("${web3j.accounts-pk.controller1}")
    private String controller1PrivateKey;

    @Override
    public Long mintERC721(String address) {
        List<Type> inputParameters = new LinkedList<>();
        List<TypeReference<?>> outputParameters = new LinkedList<>();
        inputParameters.add(new Address(address));
        outputParameters.add(new TypeReference<Uint256>() {
        });
        TransactionReceipt receipt = invokeContractWithParameters(
                getAccountFromPrivateKey(controller1PrivateKey),
                mainContract,
                "mintERC721",
                inputParameters,
                outputParameters
        );

        List<Log> logs = receipt.getLogs();
        List<String> topic = receipt.getLogs().size() > 0 ? receipt.getLogs().get(1).getTopics() : null;
        String requiredTopic = topic.size() > 0 ? topic.get(2) : null;
        if(requiredTopic == null) {
            throw new EthTransactionException();
        }
        return Long.parseLong(requiredTopic.substring(2), 16);
    }

    public Long getHeight() {
        EthBlockNumber blockNumber;

        try {
            blockNumber = web3j.ethBlockNumber().send();
        } catch (IOException e) {
            throw new EthTransactionException(e.getMessage());
        }

        return blockNumber.getBlockNumber().longValue();
    }

    public BigInteger getNonce(Web3j web3j, String address) {
        try {
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
            return ethGetTransactionCount.getTransactionCount();
        } catch (IOException e) {
            throw new EthTransactionException(e.getMessage());
        }
    }

    public BigDecimal getEthBalance(Web3j web3j, String address) {
        try {
            EthGetBalance balanceWei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            return Convert.fromWei(balanceWei.getBalance().toString(), Convert.Unit.ETHER);
        } catch (IOException e) {
            throw new EthTransactionException(e.getMessage());
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
                EthSendTransaction sendTransaction = web3j.ethSendRawTransaction(signedDataHexValue).send();
                String txHash = sendTransaction.getTransactionHash();
                if (txHash == null) {
                    throw new EthTransactionException(sendTransaction.getError().getMessage());
                }

                TransactionReceiptProcessor receiptProcessor =
                        new PollingTransactionReceiptProcessor(web3j, TransactionManager.DEFAULT_POLLING_FREQUENCY,
                                TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
                return receiptProcessor.waitForTransactionReceipt(sendTransaction.getTransactionHash());
            } catch (IOException | TransactionException e) {
                throw new EthTransactionException(e.getMessage());
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
            throw new EthTransactionException(e.getMessage());
        }
    }

    /**
     * Call contract function with parameters, may modify the contract state,
     * which means the transaction should be signed by the credential.
     */
    public TransactionReceipt invokeContractWithParameters(Credentials credentials, String contractAddress, String methodName, List<Type> inputParameters, List<TypeReference<?>> outputParameters) {
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        try {
            TransactionManager transactionManager = new FastRawTransactionManager(web3j, credentials);
            EthSendTransaction sendTransaction = transactionManager.sendTransaction(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(6700000),
                    contractAddress, data, BigInteger.ZERO);
            String txHash = sendTransaction.getTransactionHash();
            if (sendTransaction.hasError() || txHash == null) {
                throw new EthTransactionException(sendTransaction.getError().getMessage());
            }

            TransactionReceiptProcessor receiptProcessor =
                    new PollingTransactionReceiptProcessor(web3j, TransactionManager.DEFAULT_POLLING_FREQUENCY,
                            TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
            return receiptProcessor.waitForTransactionReceipt(txHash);
        } catch (IOException | TransactionException e) {
            throw new EthTransactionException(e.getMessage());
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
            throw new EthTransactionException(e.getMessage());
        }
    }
}
