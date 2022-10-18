package com.sonarx.sonarmeta.web;

import com.alipay.mychain.sdk.api.MychainClient;
import com.alipay.mychain.sdk.api.env.ClientEnv;
import com.alipay.mychain.sdk.api.env.ISslOption;
import com.alipay.mychain.sdk.api.env.SignerOption;
import com.alipay.mychain.sdk.api.env.SslBytesOption;
import com.alipay.mychain.sdk.api.utils.Utils;
import com.alipay.mychain.sdk.crypto.MyCrypto;
import com.alipay.mychain.sdk.crypto.keyoperator.Pkcs8KeyOperator;
import com.alipay.mychain.sdk.crypto.keypair.Keypair;
import com.alipay.mychain.sdk.crypto.signer.SignerBase;
import com.alipay.mychain.sdk.domain.account.Identity;
import com.alipay.mychain.sdk.utils.IOUtil;
import com.sonarx.sonarmeta.SonarmetaApplication;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @description: config of MychainSDK
 * @author: liuxuanming
 */
@Configuration
@Slf4j
public class MychainConfig {

    @Value("${mychain.rpc-host}")
    private String rpcHost;
    @Value("${mychain.rpc-port}")
    private Integer rpcPort;
    @Value("${mychain.account}")
    private String account;
    @Value("${mychain.user-password}")
    private String userPassword;
    @Value("${mychain.key-password}")
    private String keyPassword;
    @Value("${mychain.trust-store-password}")
    private String trustStorePassword;

    private static final String MYCHAIN_RESOURCE_PREFIX = "/mychain/";
    private static final String USER_PRIVATE_KEY_FILE = "user.key";
    private static final String SDK_KEY_FILE = "client.key";
    private static final String SDK_CERT_FILE = "client.crt";
    private static final String SDK_TRUST_STORE_FILE = "trustCa";

    @Bean
    public MychainClient mychainSDK() throws Exception {
        // get user key pair
        Identity userIdentity = Utils.getIdentityByName(account);
        Pkcs8KeyOperator pkcs8KeyOperator = new Pkcs8KeyOperator();
        Keypair userKeypair = pkcs8KeyOperator.load(IOUtil.inputStreamToByte(Objects.requireNonNull(SonarmetaApplication.class.getResourceAsStream(MYCHAIN_RESOURCE_PREFIX + USER_PRIVATE_KEY_FILE))), userPassword);
        // add signer
        List<SignerBase> signerBaseList = new ArrayList<>();
        SignerBase signerBase = MyCrypto.getInstance().createSigner(userKeypair);
        signerBaseList.add(signerBase);
        SignerOption signerOption = new SignerOption();
        signerOption.setSigners(signerBaseList);

        // init socket
        List<InetSocketAddress> socketAddressArrayList = new ArrayList<>();
        socketAddressArrayList.add(InetSocketAddress.createUnresolved(rpcHost, rpcPort));

        MychainClient sdk = new MychainClient();
        boolean res = sdk.init(
                ClientEnv.build(
                socketAddressArrayList,
                new SslBytesOption.Builder()
                        .keyBytes(IOUtil.inputStreamToByte(Objects.requireNonNull(SonarmetaApplication.class.getResourceAsStream(MYCHAIN_RESOURCE_PREFIX + SDK_KEY_FILE))))
                        .certBytes(IOUtil.inputStreamToByte(Objects.requireNonNull(SonarmetaApplication.class.getResourceAsStream(MYCHAIN_RESOURCE_PREFIX + SDK_CERT_FILE))))
                        .keyPassword(keyPassword)
                        .trustStorePassword(trustStorePassword)
                        .trustStoreBytes(IOUtil.inputStreamToByte(Objects.requireNonNull(SonarmetaApplication.class.getResourceAsStream(MYCHAIN_RESOURCE_PREFIX + SDK_TRUST_STORE_FILE))))
                        .build()
                , signerOption)
        );

        if(!res) {
            log.error("初始化蚂蚁链SDK失败");
        }
        return sdk;
    }
}
