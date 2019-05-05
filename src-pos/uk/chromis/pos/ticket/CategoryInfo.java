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

package uk.chromis.pos.ticket;

import java.awt.image.BufferedImage;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.IKeyed;
import uk.chromis.data.loader.ImageUtils;
import uk.chromis.data.loader.SerializerRead;

/**
 *
 * @author Adrian
 * @version
 */
public class CategoryInfo implements IKeyed {

    private static final long serialVersionUID = 8612449444103L;
    private String m_sID;
    private String m_sName;
    private String m_sTextTip;
    private String m_sPath;
    private BufferedImage m_Image;
    private Boolean m_bCatShowName;
    private String m_sColour;
    private Integer m_iCatOrder;

    /**
     * Creates new CategoryInfo
     *
     * @param id
     * @param name
     * @param image
     * @param texttip
     * @param catshowname
     */
    public CategoryInfo(String id, String name, BufferedImage image, String texttip, Boolean catshowname, String colour, Integer catorder) {
        m_sID = id;
        m_sName = name;
       // m_sPath = path;
        m_Image = image;
        m_sTextTip = texttip;
        m_bCatShowName = catshowname;
        m_sColour = colour;
        m_iCatOrder = catorder;
    }

    @Override
    public Object getKey() {
        return m_sID;
    }

    public void setID(String sID) {
        m_sID = sID;
    }

    public String getID() {
        return m_sID;
    }

    public String getName() {
        return m_sName;
    }

    public void setName(String sName) {
        m_sName = sName;
    }

    public String getTextTip() {
        return m_sTextTip;
    }

    public void setTextTip(String sName) {
        m_sTextTip = sName;
    }

    public Boolean getCatShowName() {
        return m_bCatShowName;
    }

    public void setCatShowName(Boolean bcatshowname) {
        m_bCatShowName = bcatshowname;
    }

    public Object getColour() {
        return m_sColour;
    }

    public void setColour(String sColour) {
        m_sColour = sColour;
    }

    public Integer getCatOrder() {
        return m_iCatOrder;
    }

    public void setColour(Integer catorder) {
        m_iCatOrder = catorder;
    }

    public BufferedImage getImage() {
        return m_Image;
    }

    public void setImage(BufferedImage img) {
        m_Image = img;
    }

    @Override
    public String toString() {
        return m_sName;
    }

    public String getPath() {
        return m_sPath;
    }

    public void setPath(String sPath) {
        m_sPath = sPath;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new CategoryInfo(dr.getString(1), dr.getString(2),  ImageUtils.readImage(dr.getBytes(3)), dr.getString(4), dr.getBoolean(5), dr.getString(6), dr.getInt(7));
            }
        };
    }
}
