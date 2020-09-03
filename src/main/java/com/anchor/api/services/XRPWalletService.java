package com.anchor.api.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.xpring.common.XrplNetwork;
import io.xpring.ilp.IlpClient;
import io.xpring.ilp.IlpException;
import io.xpring.ilp.model.AccountBalance;
import io.xpring.payid.PayIdException;
import io.xpring.payid.XrpPayIdClient;
import io.xpring.payid.generated.model.Address;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XrpClient;
import io.xpring.xrpl.XrpException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class XRPWalletService {
    private static final Logger LOGGER = LoggerFactory.getLogger(
            XRPWalletService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    private static final String DEVELOPMENT_MODE = "dev", SEPARATOR = "$";

    public XRPWalletService() {
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 XRP Wallet Service constructed and waiting for shit! \uD83E\uDD68");
    }
    @Value("${spring.profiles.active}")
    private String activeProfile;
    @Value("${xrpURL}")
    private String xrpURL;

    /*
    😎 😎 😎
    XrpClient is a gateway into the XRP Ledger. XrpClient is initialized with a two parameters:
    The URL of the gRPC API on the remote rippled node
    An enum representing the XRPL Network the remote rippled node is attached to
     */
    public BigInteger getXRPWalletBalance(String address) throws XrpException, PayIdException, IlpException {
        LOGGER.info("\uD83C\uDFB2 \uD83C\uDFB2 Get XRP Wallet Balance using WalletService:  \uD83E\uDD68 \uD83E\uDD68 \uD83E\uDD68 ...");

        XrpClient xrpClient = new XrpClient(xrpURL, activeProfile.equalsIgnoreCase(DEVELOPMENT_MODE)? XrplNetwork.TEST: XrplNetwork.MAIN);
        LOGGER.info("\uD83C\uDF4E  \uD83C\uDF4E XRP Client: getNetworkName: \uD83D\uDC99 \uD83D\uDC99 "
                .concat(xrpClient.getNetwork().getNetworkName()));
        BigInteger balance = xrpClient.getBalance(address);
        LOGGER.info("\uD83C\uDF4E  \uD83C\uDF4E XRP Client: Wallet Balance: \uD83E\uDD66 \uD83E\uDD66 "
                .concat((balance.divide(new BigInteger(String.valueOf(1000000)))).toString())
                .concat(" XRP \uD83E\uDD66 \uD83E\uDD66"));

        LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E ............. getXRPWalletBalance completed ".concat("\n\n"));

        return balance;
    }

    @NotNull
    public Wallet getXRPWallet(String seed) throws XrpException {
        Wallet seedWallet = new Wallet(seed);
        LOGGER.info("\n\uD83C\uDF4E Wallet Address: ".concat(seedWallet.getAddress()
                .concat("\n\uD83C\uDF4E Wallet Public Key: ").concat(seedWallet.getPublicKey()
                        .concat("\n\uD83C\uDF4E Wallet Private Key: ").concat(seedWallet.getPrivateKey()))));
        return seedWallet;
    }

    @Value("${gRPC}")
    private String gRPC; //so-called Hermes endpoint

    public AccountBalance getPayIdWalletBalance(String payId, String accessToken)
            throws Exception {
        LOGGER.info("\uD83C\uDFB2 \uD83C\uDFB2 Get PayID Wallet Balance using WalletService:  \uD83E\uDD68 \uD83E\uDD68 \uD83E\uDD68 ...");
        XrplNetwork xrpNetwork = activeProfile.equalsIgnoreCase(DEVELOPMENT_MODE)? XrplNetwork.TEST : XrplNetwork.MAIN;
        XrpPayIdClient xrpPayIdClient = new XrpPayIdClient(xrpNetwork);
        List<Address> addressList = xrpPayIdClient.allAddressesForPayId(payId);
        LOGGER.info(("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 number of payID addresses:" +
                " \uD83D\uDE0E ").concat("" + addressList.size()));
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 XRP Ledger network: "
                .concat(xrpPayIdClient.getXrplNetwork().getNetworkName()));
        if (addressList.isEmpty()) {
            LOGGER.info("\uD83D\uDC80 \uD83D\uDC80 \uD83D\uDC80 ...... No addresses found here!");
        } else {
            for (Address address1 : addressList) {
                LOGGER.info("\uD83C\uDFB2 \uD83C\uDFB2 Address Payment Network:  \uD83C\uDFB2 :"
                        .concat(address1.getPaymentNetwork()));
            }
        }

        IlpClient ilpClient = new IlpClient(gRPC);
        int index = payId.indexOf(SEPARATOR);
        String account = payId.substring(0,index);
        LOGGER.info("\uD83D\uDE0E \uD83D\uDE0E PayId accountId: " + account + " from payId: " + payId);

        AccountBalance accountBalance = ilpClient.getBalance(account, accessToken); // Just a demo user on Testnet
        LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 \uD83C\uDF51 Net balance was " + accountBalance.netBalance()
                + " \uD83E\uDD68 with asset scale "
                + accountBalance.assetScale() + " \uD83D\uDC99 calculated XRP: \uD83D\uDC99 "
                + accountBalance.netBalance().divide(new BigInteger("1000000000")));
        LOGGER.info("\uD83D\uDE0E \uD83D\uDE0E \uD83D\uDE0E PAYID ACCOUNT BALANCE: ".concat(G.toJson(accountBalance))
        .concat(" \uD83D\uDE0E"));
        return accountBalance;
    }

    public static void main(String[] args) throws Exception {
        XRPWalletService service = new XRPWalletService();
        service.getXRPWalletBalance("ssms4QXcpuBhFkGxU8w4czQCiKFNN");
    }
}
