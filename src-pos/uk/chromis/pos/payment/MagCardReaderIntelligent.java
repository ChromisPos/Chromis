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

/**
 *
 *   
 */
public final class MagCardReaderIntelligent implements MagCardReader {
    
    private String m_sHolderName;
    private String m_sCardNumber;
    private String m_sExpirationDate;
    
    private StringBuilder m_sField;
    
    private static final int READING_HOLDER = 0;
    private static final int READING_NUMBER = 1;
    private static final int READING_DATE = 2;
    private static final int READING_FINISHED = 3;
    private int m_iAutomState;
            
    /** Creates a new instance of BasicMagCardReader */
    public MagCardReaderIntelligent() {
        reset();
    }
 
    /**
     *
     * @return
     */
    @Override
    public String getReaderName() {
        return "Basic magnetic card reader";
    }
    
    /**
     *
     */
    @Override
    public void reset() {
        m_sHolderName = null;
        m_sCardNumber = null;
        m_sExpirationDate = null;
        m_sField = new StringBuilder();
        m_iAutomState = READING_HOLDER;
    }
    
    /**
     *
     * @param c
     */
    @Override
    public void appendChar(char c) {
       
        switch (m_iAutomState) {
            case READING_HOLDER:
            case READING_FINISHED:
                if (c == 0x0009) {
                    m_sHolderName = m_sField.toString();
                    m_sField = new StringBuilder();
                    m_iAutomState = READING_NUMBER;
                } else if (c == 0x000A) {
                    m_sHolderName = null;
                    m_sCardNumber = null;
                    m_sExpirationDate = null;
                    m_sField = new StringBuilder();
                    m_iAutomState = READING_HOLDER;
                } else {
                    m_sField.append(c);
                    m_iAutomState = READING_HOLDER;
                }
                break;
            case READING_NUMBER:
                if (c == 0x0009) {
                    m_sCardNumber = m_sField.toString();
                    m_sField = new StringBuilder();
                    m_iAutomState = READING_DATE;
                } else if (c == 0x000A) {
                    m_sHolderName = null;
                    m_sCardNumber = null;
                    m_sExpirationDate = null;
                    m_sField = new StringBuilder();
                    m_iAutomState = READING_HOLDER;
                } else {
                    m_sField.append(c);
                }
                break;                
            case READING_DATE:
                if (c == 0x0009) {
                    m_sHolderName = m_sCardNumber;
                    m_sCardNumber = m_sExpirationDate;
                    m_sExpirationDate = null;
                    m_sField = new StringBuilder();
                } else if (c == 0x000A) {
                    m_sExpirationDate = m_sField.toString();
                    m_sField = new StringBuilder();
                    m_iAutomState = READING_FINISHED;
                } else {
                    m_sField.append(c);
                }
                break;  
        }
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean isComplete() {
        return m_iAutomState == READING_FINISHED;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String getHolderName() {
        return m_sHolderName;
    }

    /**
     *
     * @return
     */
    @Override
    public String getCardNumber() {
        return m_sCardNumber;
    }

    /**
     *
     * @return
     */
    @Override
    public String getExpirationDate() {
        return m_sExpirationDate;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTrack1() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTrack2() {
        return null;
    }    

    /**
     *
     * @return
     */
    @Override
    public String getTrack3() {
        return null;
    }       
}
