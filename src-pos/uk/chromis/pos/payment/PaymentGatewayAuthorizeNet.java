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
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Authorize.Net</p>
 * @author Authorize.Net
 * @version 3.1
 */

/**
 *  Based on sample code and snippets provided by:
 *  Patrick Phelan, phelan@choicelogic.com
 *  Roedy Green, Canadian Mind Products
 *  Authorize.net - //github.com/AuthorizeNet
 */

// Modifications by Adrian Romero & Mikel Irurita


package uk.chromis.pos.payment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.chromis.data.loader.LocalRes;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppProperties;
import uk.chromis.pos.util.AltEncrypter;


/**
 *
 *   
 */
public class PaymentGatewayAuthorizeNet implements PaymentGateway {
    
    private static String ENDPOINTADDRESS;
    private static final String OPERATIONVALIDATE = "AUTH_CAPTURE";
    private static final String OPERATIONREFUND = "CREDIT";
    private static final String APPROVED = "1";
    
    private String m_sCommerceID;
    private String m_sCommercePassword;
    private boolean m_bTestMode;
  
    /** Creates a new instance of PaymentGatewayAuthorizeNet
     * @param props */
    public PaymentGatewayAuthorizeNet() {
        // Grab some configuration variables
        m_sCommerceID = AppConfig.getInstance().getProperty("payment.commerceid");
        
        AltEncrypter cypher = new AltEncrypter("cypherkey" + AppConfig.getInstance().getProperty("payment.commerceid"));
        this.m_sCommercePassword = cypher.decrypt(AppConfig.getInstance().getProperty("payment.commercepassword").substring(6));

        m_bTestMode = Boolean.parseBoolean(AppConfig.getInstance().getProperty("payment.testmode"));
        
        ENDPOINTADDRESS = (m_bTestMode) 
                ? "https://test.authorize.net/gateway/transact.dll"
                : "https://cardpresent.authorize.net/gateway/transact.dll";
    }



    /**
     *
     * @param payinfo
     */
    @Override
    public void execute(PaymentInfoMagcard payinfo) {

        StringBuilder sb = new StringBuilder();
        try {

            sb.append("x_cpversion=1.0"); // current supported version
            
            sb.append("&x_market_type=2"); // 2 = Retail            
            sb.append("&x_device_type=5"); //5 = PC based terminal
            
            sb.append("&x_login=");        
            sb.append(URLEncoder.encode(m_sCommerceID, "UTF-8"));
                        
            sb.append("&x_tran_key=");
            sb.append(URLEncoder.encode(m_sCommercePassword, "UTF-8"));
             
            sb.append("&x_amount=");
            NumberFormat formatter = new DecimalFormat("0000.00");
            String amount = formatter.format(Math.abs(payinfo.getTotal()));
            sb.append(URLEncoder.encode(amount.replace(',', '.'), "UTF-8"));
            
            if (payinfo.getTrack1(true) == null) {
                sb.append("&x_card_num=");
                sb.append(URLEncoder.encode(payinfo.getCardNumber(), "UTF-8"));

                sb.append("&x_exp_date=");
                String tmp = payinfo.getExpirationDate();
                sb.append(URLEncoder.encode(tmp, "UTF-8"));

                String[] cc_name = payinfo.getHolderName().split(" ");
                sb.append("&x_first_name=");
                if (cc_name.length > 0) {
                   sb.append(URLEncoder.encode(cc_name[0], "UTF-8"));
                }
                sb.append("&x_last_name=");
                if (cc_name.length > 1) {
                   sb.append(URLEncoder.encode(cc_name[1], "UTF-8"));
                }
            } else {
                // Example Track1
                // %B4111111111111111^PADILLA VISDOMINE/LUIS^0905123000000000000002212322222?5
                sb.append("&x_track1=");
                sb.append(payinfo.getTrack1(false));
                sb.append("&x_track2=");
                sb.append(payinfo.getTrack2(false));
            }

            sb.append("&x_method=CC");
            sb.append("&x_version=3.1");
            sb.append("&x_repsonse_format=0");            
            sb.append("&x_delim_char=|");
            sb.append("&x_relay_response=FALSE");
            sb.append("&x_test_request=");
            sb.append(m_bTestMode);
            
            //PAYMENT
            if (payinfo.getTotal() >= 0.0) {
                sb.append("&x_type=");
                sb.append(OPERATIONVALIDATE);
                //sb.append("&x_card_code=340"); //CCV
            }
            //REFUND
            else {
                sb.append("&x_type=");
                sb.append(OPERATIONREFUND);
                sb.append("&x_trans_id=");
                sb.append(payinfo.getTransactionID());
            }

            // open secure connection
            URL url = new URL(ENDPOINTADDRESS);

            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // not necessarily required but fixes a bug with some servers
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {

                out.write(sb.toString().getBytes());
                out.flush();
                out.close();                
            }
            String returned;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                returned = in.readLine();
              
                in.close();
            }
            
            
            AuthorizeNetParser anp = new AuthorizeNetParser(returned);
            Map props = anp.splitXML();

            if (anp.getResult().equals(LocalRes.getIntString("Button.OK"))) {
                if (APPROVED.equals(props.get("ResponseCode"))) { 
                    //Transaction approved
//                    payinfo.paymentOK((String) props.get("AuthCode"), (String) props.get("TransID"), returned);
                    payinfo.paymentOK(props.get("AuthCode").toString(), props.get("TransID").toString(), returned);                    
                } else {
                    StringBuilder errorLine = new StringBuilder();
                    //Transaction declined
                    if (anp.getNumErrors()>0) {
                        
                        for (int i=1; i<=anp.getNumErrors(); i++) {
                            errorLine.append(props.get("ErrorCode"+Integer.toString(i)));
                            errorLine.append(": ");
                            errorLine.append(props.get("ErrorText"+Integer.toString(i)));
                            errorLine.append("\n");
                        }
                    
                    payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), errorLine.toString());
                    }
                }
            }
            else {
                payinfo.paymentError(anp.getResult(), "");
            }
           
        } catch (UnsupportedEncodingException | MalformedURLException eUE) {
            payinfo.paymentError(AppLocal.getIntString("message.paymentexceptionservice"), eUE.getMessage());
        } catch(IOException e){
            payinfo.paymentError(AppLocal.getIntString("message.paymenterror"), e.getMessage());
        }
        
    }
    
    private class AuthorizeNetParser extends DefaultHandler {
    
    private SAXParser m_sp = null;
    private final Map props = new HashMap();
    private String text;
    private final InputStream is;
    private String result;
    private int numMessages = 0;
    private int numErrors = 0;
    
    public AuthorizeNetParser(String input) {
        is = new ByteArrayInputStream(input.getBytes());
    }
 
    public Map splitXML(){
        try {
            if (m_sp == null) {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                m_sp = spf.newSAXParser();
            }

            m_sp.parse(is, this);
 
         } catch (ParserConfigurationException ePC) {
            result = LocalRes.getIntString("exception.parserconfig");
        } catch (SAXException eSAX) {
            result = LocalRes.getIntString("exception.xmlfile");
        } catch (IOException eIO) {
            result = LocalRes.getIntString("exception.iofile");
        }
        result = LocalRes.getIntString("Button.OK");
        return props;
    }
      
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
 
        try {
            if (qName.equals("ResponseCode")) {
                props.put("ResponseCode", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("ErrorCode")){
                numErrors++;
                props.put("ErrorCode"+Integer.toString(numErrors), URLDecoder.decode(text, "UTF-8"));
                text = "";
            } else if (qName.equals("ErrorText")) {
                props.put("ErrorText"+Integer.toString(numErrors), URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("Code")) {
                numMessages++;
                props.put("Code"+Integer.toString(numMessages), URLDecoder.decode(text, "UTF-8"));
                text = "";
            } else if (qName.equals("Description")) {
                props.put("Description"+Integer.toString(numMessages), URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("AuthCode")) {
                props.put("AuthCode", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("AVSResultCode")) {
                props.put("AVSResultCode", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("CVVResultCode")) {
                props.put("CVVResultCode", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("TransID")) {
                props.put("TransID", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("RefTransID")) {
                props.put("RefTransID", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("TransHash")) {
                props.put("TransHash", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("TestMode")) {
                props.put("TestMode", URLDecoder.decode(text, "UTF-8"));
                text="";
            } else if (qName.equals("UserRef")) {
                props.put("UserRef", URLDecoder.decode(text, "UTF-8"));
                text="";
            }
        }
        catch(UnsupportedEncodingException eUE){
            result = eUE.getMessage();
        }
    }
    
    @Override
    public void startDocument() throws SAXException {
        text = new String();
    }

    @Override
    public void endDocument() throws SAXException {
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (text!=null) {
            text = new String(ch, start, length);
        }
    }
    
    public String getResult(){
        return this.result;
    }
    
    public int getNumErrors(){
        return numErrors;
    }
    
    public int getNumMessages(){
        return numMessages;
    }
    
 }

}