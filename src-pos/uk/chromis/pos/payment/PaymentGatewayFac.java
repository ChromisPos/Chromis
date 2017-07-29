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

import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppProperties;

/**
 *
 *   
 */
public class PaymentGatewayFac {
    
    /** Creates a new instance of PaymentGatewayFac */
    private PaymentGatewayFac() {
    }
    
    /**
     *
     * @param props
     * @return
     */
    public static PaymentGateway getPaymentGateway() {
        
        String sReader = AppConfig.getInstance().getProperty("payment.gateway");
        switch (sReader) {
            case "external":
                return new PaymentGatewayExt();
            case "PayPoint / SecPay":
                return new PaymentGatewayPayPoint();
            case "AuthorizeNet":
                return new PaymentGatewayAuthorizeNet();
            case "BluePay AUTH.NET EMU":
                return new PaymentGatewayBluePayAUTHNETEMU();
            case "BluePay 2.0 POST":
                return new PaymentGatewayBluePay20POST();
            case "La Caixa (Spain)":
                return new PaymentGatewayCaixa();
            case "Planetauthorize":
                return new PaymentGatewayPlanetauthorize();
            case "First Data / LinkPoint / YourPay":
                return new PaymentGatewayLinkPoint();
            case "PaymentsGateway.net":
                return new PaymentGatewayPGNET();
            default:
                return null;
        }
    }      
}
