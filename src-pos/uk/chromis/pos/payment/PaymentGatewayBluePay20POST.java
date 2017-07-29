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

/**
 * <p>Title: AIM Java Version 1.4.1_02-b06</p>
 * <p>Description: Advanced Integration Method</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: BluePay</p>
 * @author Walter Wojcik
 * @version 1.0
 */

/**
 *  Based on sample code and snipptes provided by:
 *  Patrick Phelan, phelan@choicelogic.com
 *  Roedy Green, Canadian Mind Products
 */

// Modifications by Adrian Romero & Mikel Irurita and further modified for blue pay by walter wojcik

package uk.chromis.pos.payment;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppProperties;
import uk.chromis.pos.util.StringUtils;

/**
 *
 *   
 */
public class PaymentGatewayBluePay20POST implements PaymentGateway {
    
    private static String ENDPOINTADDRESS;
    private static final String APPROVED = "1";
    private static final String PAYMENT_ACCOUNT = null;
    
    private String BP_AccountID;
    private String BP_SecretKey;
    private String BP_Tamper_Proof_Seal;
    private String BP_Master_ID;
    private String BP_Trans_Type;
    private boolean BP_TestMode;
    private String BP_Name1;
   

    /** Creates a new instance of PaymentGatewayBluePay20POST
     * @param props */
    public PaymentGatewayBluePay20POST() {
        // Grab some configuration variables
        BP_AccountID = AppConfig.getInstance().getProperty("payment.BluePay20POST.accountID");
        
        this.BP_SecretKey = AppConfig.getInstance().getProperty("payment.BluePay20POST.secretKey");

        BP_TestMode = AppConfig.getInstance().getBoolean("payment.testmode");
        
        ENDPOINTADDRESS = AppConfig.getInstance().getProperty( "payment.BluePay20POST.URL" );
    }


    /**
     *
     * @param payinfo
     */
    @Override
    public void execute(PaymentInfoMagcard payinfo) 
    {
        StringUtils.getCardNumber();

        StringBuilder sb = new StringBuilder();
        try {
            sb.append("ACCOUNT_ID=");    
            sb.append(URLEncoder.encode(BP_AccountID, "UTF-8"));
                        
            sb.append("&AMOUNT=");
            NumberFormat formatter = new DecimalFormat("######.00");
            String amount = formatter.format(Math.abs(payinfo.getTotal()));
            sb.append(URLEncoder.encode(amount.replace(',', '.'), "UTF-8"));
            
            //PAYMENT
            if (payinfo.getTotal() >= 0.0) {
                BP_Master_ID = null;
                BP_Trans_Type = "SALE";
            }
            //REFUND
            else {                
                BP_Trans_Type = "REFUND";
                /**
                 * Detect BluePay Transaction IDs which will start with "1001"
                 * This is not ideal but until the POS Devs get back to me or I 
                 * find a better way to detect this, it should be good enough
                 */
                BP_Master_ID = ( payinfo.getTransactionID().startsWith("1001") ? payinfo.getTransactionID() : null ) ; 
            }
            
            if (payinfo.getTrack1(true) == null) {
                sb.append("&PAYMENT_ACCOUNT=");
                sb.append(URLEncoder.encode(payinfo.getCardNumber(), "UTF-8"));

                sb.append("&CARD_CVV2=");
                String tmp = payinfo.getExpirationDate();
                sb.append(URLEncoder.encode(tmp, "UTF-8"));

                String[] cc_name = payinfo.getHolderName().split(" ");
                sb.append("&NAME1=");
                if (cc_name.length > 0) {
                   sb.append(URLEncoder.encode(cc_name[0], "UTF-8"));
                }
                sb.append("&NAME2=");
                if (cc_name.length > 1) {
                   sb.append(URLEncoder.encode(cc_name[1], "UTF-8"));
                }
            } else {
                // Example Track1
                // %B4111111111111111^PADILLA VISDOMINE/LUIS^0905123000000000000002212322222?5
                
                String swipe = URLEncoder.encode( payinfo.getTrack1(true), "UTF-8" ) + URLEncoder.encode( payinfo.getTrack2(true), "UTF-8" );
                
                swipe = swipe.replace( "%0A", "" );
                swipe = swipe.replace( "%5E", "^" );
                
                if( BP_Trans_Type.equals( "REFUND" ) && BP_Master_ID != null )
                {
                    // Do nothing, SWIPE data is not need if there is a master ID when refunding.
                }
                else
                {
                    sb.append("&SWIPE=");
                    sb.append( swipe );
                }
            }
            
            
            
            //sb.append("&x_method=CC");
            
           
            sb.append("&MODE=");
            sb.append( BP_TestMode ? "TEST" : "LIVE" );
            sb.append("&TRANS_TYPE=");
            sb.append(BP_Trans_Type);
            sb.append("&MASTER_ID=");
            sb.append( BP_Master_ID != null ? BP_Master_ID : "" );
            sb.append( "&NAME1=");
            sb.append( URLEncoder.encode(payinfo.getHolderName().split(" ")[0], "UTF-8") );
            
            // Start Tamper Proof Seal
            MessageDigest md5;
            try {                
                md5 = MessageDigest.getInstance("MD5");
                BP_Tamper_Proof_Seal = BP_SecretKey + BP_AccountID + BP_Trans_Type + amount + ( BP_Master_ID != null ? BP_Master_ID : "" ) + URLEncoder.encode(payinfo.getHolderName().split(" ")[0], "UTF-8") + (PAYMENT_ACCOUNT != null ? PAYMENT_ACCOUNT : "");
                byte[] hash = md5.digest( BP_Tamper_Proof_Seal.getBytes("UTF-8") );
                
                StringBuilder sbhash = new StringBuilder(2*hash.length);
                for(byte b : hash){
                    sbhash.append(String.format("%02x", b&0xff));
                }
                String md5hex = sbhash.toString();
                
                sb.append( "&TAMPER_PROOF_SEAL=");
                
                sb.append( URLEncoder.encode( md5hex, "UTF-8") );
                
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(PaymentGatewayBluePay20POST.class.getName()).log(Level.SEVERE, null, ex);
            } // End Tamper Proof Seal
            
            // open secure connection
            URL url = new URL(ENDPOINTADDRESS);

            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // not necessarily required but fixes a bug with some servers
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                out.write(sb.toString().getBytes());
                out.flush();
            }
            
            String returned;
            
            /**
             * Throws IO Exception when the server returns 400.  The server will
             * return this anytime the request is not formated perfectly
             * This needs to be caught and handled since the server will still 
             * respond with useful information even after returning 400
             */
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                returned = in.readLine();
            }
            
            // Custom parser to handle GET/POST style response
            BluePay20POSTParser results = new BluePay20POSTParser( returned );
            
            /**
             * Server will return:
             *      STATUS=1 when transaction is accepted
             *      STATUS=2 When the transaction
             *      STATUS=E Then there is an error with the request formating or data sent
             */
            if( results.getStatus().equals("1") )
            {
                payinfo.paymentOK( results.getAuth_Code(), results.getTrans_ID(), results.getMessage() );
            }
            else if( results.getStatus().equals("2") )
            {
                if( results.getMessage().equals( "DUPLICATE" ))
                {
                    payinfo.paymentError( "DUPLICATE of Trans # " + results.Trans_ID + "", amount);
                }
                else
                    payinfo.paymentError(results.getMessage(), amount);
            }
            else
            {
                /**
                 * When there is an issue with the request the server will 
                 * return a 400 header and STAUTS=E with a short MESSAGE=Somthing
                 * The 400 header makes BufferedReader Throw an IO Exception and 
                 * until I figure out how to catch it and save there stream there
                 * is nothing to do here.
                 */
            }
            
        } catch (UnsupportedEncodingException | MalformedURLException eUE) {
            payinfo.paymentError(AppLocal.getIntString("message.paymentexceptionservice"), eUE.getMessage());
        } catch(IOException e) // Throw but buffered reader when the server returns a 400 header
        {
            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), e.getMessage());
        }        
    }
    
    private class BluePay20POSTParser
    {
        private String Trans_ID;
        private String Status;
        private String AVS;
        private String CVV2;
        private String Auth_Code;
        private String Message;
        private String Rebid;
        private String Trans_Type;
        
        public BluePay20POSTParser( String result )
        {
            String[] pairs = result.split( "&" );
            for( String pair : pairs )
            {
                String[] parts = pair.split( "=" );
                
                switch( parts[0] )
                {
                    case "TRANS_ID":
                        this.Trans_ID = parts[1];
                        break;
                    case "STATUS":
                        this.Status = parts[1];
                        break;
                    case "AVS":
                        this.AVS = parts[1];
                        break;
                    case "CVV2":
                        this.CVV2 = parts[1];
                        break;
                    case "AUTH_CODE":
                        this.Auth_Code = parts[1];
                        break;
                    case "MESSAGE":
                        this.Message = parts[1];
                        break;
                    case "Rebid":
                        this.Rebid = parts[1];
                        break;
                    case "TRANS_TYPE":
                        this.Trans_Type = parts[1];
                        break;
                    default:
                        break;
                }
            }
        }
        
        public String getTrans_ID()
        {
            return this.Trans_ID;
        }
        
        public String getStatus()
        {
            return this.Status;
        }
        
        public String getAVS()
        {
            return this.AVS;
        }
        
        public String getCVV2()
        {
            return this.CVV2;
        }
        
        public String getAuth_Code()
        {
            return this.Auth_Code;
        }
        
        public String getMessage()
        {
            return this.Message;
        }
        
        public String getRebid()
        {
            return this.Rebid;
        }
        
        public String getTrans_Type()
        {
            return this.Trans_Type;
        }       
    }    
}