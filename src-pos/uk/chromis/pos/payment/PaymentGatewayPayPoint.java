/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c)2015-2016
**    http://www.chromis.co.uk
**
**    This file is part of Chromis POS Version V0.60.2 beta
**
**    Chromis POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    Chromis POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>
**
**
*/

package uk.chromis.pos.payment;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.security.Security;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppProperties;
import uk.chromis.pos.util.AltEncrypter;

/**
 *
 *   
 */
public class PaymentGatewayPayPoint implements PaymentGateway {
    
    private static final String ENDPOINTADDRESS = "https://www.secpay.com/java-bin/soap";
    //private static final String ENDPOINTADDRESS = "https://www.secpay.com/java-bin/ValCard";
    private static final QName OPERATIONVALIDATE = new QName("SECCardService", "validateCardFull");
    private static final QName OPERATIONREFUND = new QName("SECCardService", "refundCardFull");
    
    private String m_sCommerceID;
    private String m_sCommercePassword;
    private String m_sCurrency;
    private boolean m_bTestMode;
    
    /** Creates a new instance of PaymentGatewaySECPay
     * @param props */
    public PaymentGatewayPayPoint() {
        
        // Propiedades del sistema
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol" );
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            
        // Configuracion del pago
        m_sCommerceID = AppConfig.getInstance().getProperty("payment.commerceid");
        
        AltEncrypter cypher = new AltEncrypter("cypherkey" + AppConfig.getInstance().getProperty("payment.commerceid"));
        this.m_sCommercePassword = cypher.decrypt(AppConfig.getInstance().getProperty("payment.commercepassword").substring(6));
        
        m_bTestMode = AppConfig.getInstance().getBoolean("payment.testmode");
        m_sCurrency = (Locale.getDefault().getCountry().isEmpty())
            ? Currency.getInstance("EUR").getCurrencyCode()
            : Currency.getInstance(Locale.getDefault()).getCurrencyCode();
    }
    

    
    /**
     *
     * @param payinfo
     */
    @Override
    public void execute(PaymentInfoMagcard payinfo) {
        //test -> login: secpay / pass: secpay 
        try {
        Service service = new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(ENDPOINTADDRESS);
        Object[] methodParams;
 
        //AMOUNT must be > 1
        if (payinfo.getTotal() > 0.0) {            
            call.setOperationName(OPERATIONVALIDATE);    
            methodParams = new Object[] {
                m_sCommerceID,
                m_sCommercePassword,
                payinfo.getTransactionID(), //unique
                InetAddress.getLocalHost().getHostAddress(),
                payinfo.getHolderName(),
                payinfo.getCardNumber(),
                Double.toString(payinfo.getTotal()),
                payinfo.getExpirationDate(),
                "", // issue number
                "", // start date
                "", // order
                "", // shipping
                "", // billing
                //Options (StringUtils.getCardNumber())
                (m_bTestMode ? "test_status=true," : "test_status=live,") + "dups=false,currency=" + m_sCurrency
            };
        } else {
            //payinfo.paymentError(AppLocal.getIntString("message.paymentrefundsnotsupported"));
            call.setOperationName(OPERATIONREFUND);
            methodParams = new Object[] {
                m_sCommerceID,
                m_sCommercePassword,
                payinfo.getTransactionID(),
                Double.toString(Math.abs(payinfo.getTotal())),
                "secpay",
                "refund"+payinfo.getTransactionID()
            };    
        }
        
        String returned = (String) call.invoke(methodParams);
        // "?valid=true&trans_id=Twem0003&code=A&auth_code=9999&message=TEST AUTH&currency=EUR&amount=50.0&test_status=true"     

        payinfo.setReturnMessage(returned);
        
        if (returned == null) {
            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Response empty.");
        } else {
            Map props = new HashMap();
            StringTokenizer tk = new java.util.StringTokenizer(returned, "?&");
            while(tk.hasMoreTokens()) {
                String sToken = tk.nextToken();
                int i = sToken.indexOf('=');
                if (i >= 0) {
                    props.put(URLDecoder.decode(sToken.substring(0, i), "UTF-8"), URLDecoder.decode(sToken.substring(i + 1), "UTF-8"));
                } else {
                    props.put(URLDecoder.decode(sToken, "UTF-8"), null);
                }                   
            }

            if ("true".equals(props.get("valid"))) {
                // A  Transaction authorised by bank. auth_code available as bank reference
                payinfo.paymentOK((String) props.get("auth_code"), (String) props.get("trans_id"), returned);
            } else {
                String sCode = (String) props.get("code");
                switch (sCode) {
                        case "N":
                            // N Transaction not authorised. Failure message text available to merchant
                            payinfo.paymentError(AppLocal.getIntString("message.paymentnotauthorised"), (String) props.get("message"));
                            break;
                        case "C":
                            // C Communication problem. Trying again later may well work
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Communication problem. Trying again later may well work.");
                            break;
                        case "P:A":
                            // P:A Pre-bank checks. Amount not supplied or invalid
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Amount not supplied or invalid.");
                            break;
                        case "P:X":
                            // P:X Pre-bank checks. Not all mandatory parameters supplied
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Not all mandatory parameters supplied.");
                            break;
                        case "P:P":
                            // P:P Pre-bank checks. Same payment presented twice
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Same payment presented twice.");
                            break;
                        case "P:S":
                            // P:S Pre-bank checks. Start date invalid
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Start date invalid.");
                            break;
                        case "P:E":
                            // P:E Pre-bank checks. Expiry date invalid
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Expiry date invalid.");
                            break;
                        case "P:I":
                            // P:I Pre-bank checks. Issue number invalid
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Issue number invalid.");
                            break;
                        case "P:C":
                            // P:C Pre-bank checks. Card number fails LUHN check
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Card number fails LUHN check.");
                            break;
                        case "P:T":
                            // P:T Pre-bank checks. Card type invalid - i.e. does not match card number prefix
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Card type invalid - i.e. does not match card number prefix.");
                            break;
                        case "P:N":
                            // P:N Pre-bank checks. Customer name not supplied
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Customer name not supplied.");
                            break;
                        case "P:M":
                            // P:M Pre-bank checks. Merchant does not exist or not registered yet
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Merchant does not exist or not registered yet.");
                            break;
                        case "P:B":
                            // P:B Pre-bank checks. Merchant account for card type does not exist
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Merchant account for card type does not exist.");
                            break;
                        case "P:D":
                            // P:D Pre-bank checks. Merchant account for this currency does not exist
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Merchant account for this currency does not exist.");
                            break;
                        case "P:V":
                            // P:V Pre-bank checks. CVV2 security code mandatory and not supplied / invalid
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "CVV2 security code mandatory and not supplied / invalid.");
                            break;
                        case "P:R":
                            // P:R Pre-bank checks. Transaction timed out awaiting a virtual circuit. Merchant may not have enough virtual circuits for the volume of business.
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "Transaction timed out awaiting a virtual circuit. Merchant may not have enough virtual circuits for the volume of business.");
                            break;
                        case "P:#":
                            // P:# Pre-bank checks. No MD5 hash / token key set up against account
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), "No MD5 hash / token key set up against account.");
                            break;
                        default:
                            payinfo.paymentError(AppLocal.getIntString("message.paymenterrorunknown"), "");
                            break;
                    }
            }
        }
        } catch (UnknownHostException | UnsupportedEncodingException | ServiceException eUH) {
            payinfo.paymentError(AppLocal.getIntString("message.paymentexceptionservice"), eUH.getMessage());
        } catch (RemoteException remoteException) {
            payinfo.paymentError(AppLocal.getIntString("message.paymentexceptionremote"), remoteException.getMessage());

        }
    }
}
