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

package uk.chromis.pos.sync;

import uk.chromis.data.loader.IKeyed;


public class SitesInfo implements IKeyed {

    private String guid;
    private String name;
    private String address1;
    private String address2;
    private String city;
    private String postcode;
    private String taxcode;
    private String telephone;
    private Boolean siteactive;
    private String siteurl;
    private String siteusername;
    private String sitepassword;
    private Boolean datatransfered;

  
    public SitesInfo(String guid, String name, String address1, String address2, String city, String postcode, String taxcode, String telephone, Boolean siteactive,
            String siteurl, String siteusername, String sitepassword, Boolean datatransfered) {
        this.guid = guid;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.postcode = postcode;
        this.taxcode = taxcode;
        this.telephone = telephone;
        this.siteactive = siteactive;
        this.siteurl = siteurl;
        this.siteusername = siteusername;
        this.sitepassword = sitepassword;
        this.datatransfered = datatransfered;

    }

    public SitesInfo(String guid, String name) {
        this.guid = guid;
        this.name = name;
    }

    public SitesInfo(String name) {        
        this.name = name;
    }

    @Override
    public Object getKey() {
        return guid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String addr1) {
        this.address1 = addr1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String addr2) {
        this.address2 = addr2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getTaxcode() {
        return taxcode;
    }

    public void setTaxcode(String taxcode) {
        this.taxcode = taxcode;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Boolean getSiteactive() {
        return siteactive;
    }

    public void setSiteactive(Boolean siteactive) {
        this.siteactive = siteactive;
    }

    public String getSiteurl() {
        return siteurl;
    }

    public void setSiteurl(String siteurl) {
        this.siteurl = siteurl;
    }

    public String getSiteusername() {
        return siteusername;
    }

    public void setSiteusername(String siteusername) {
        this.siteusername = siteusername;
    }

    public String getSitepassword() {
        return sitepassword;
    }

    public void setSitepassword(String sitepassword) {
        this.sitepassword = sitepassword;
    }

    public Boolean getDatatransfered() {
        return datatransfered;
    }

    public void setDatatransfered(Boolean datatransfered) {
        this.datatransfered = datatransfered;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
/*
    static SerializerRead getSerializerRead() {
        @Override
        public void readValues
        (DataRead dr) throws BasicException {
            guid = dr.getString(1);
            name = dr.getString(2);
        }
    }
*/
}
