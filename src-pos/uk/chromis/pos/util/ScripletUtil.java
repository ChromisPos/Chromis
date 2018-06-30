package uk.chromis.pos.util;

import uk.chromis.data.loader.BaseSentence;
import uk.chromis.data.loader.DataResultSet;
import uk.chromis.data.loader.SerializerReadInteger;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.ticket.TicketInfo;
import uk.chromis.pos.util.AltEncrypter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScripletUtil {

    private Session session;

    public String printAddi(int paramInt) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String str1 = "select * from ADDI where TICKETID =" + paramInt + "ORDER BY OTHERID ASC";
        StaticSentence localStaticSentence = new StaticSentence(getSession(), str1, null, SerializerReadInteger.INSTANCE);
        int i = 0;
        try {
            DataResultSet localDataResultSet2 = localStaticSentence.openExec(null);
            while (localDataResultSet2.next()) {
                i++;
            }
            localDataResultSet2.close();
        } catch (Exception localException1) {
            Logger.getLogger(ScripletUtil.class.getName()).log(Level.SEVERE, null, localException1);
        }
        String str2 = "";
        try {
            DataResultSet localDataResultSet1 = localStaticSentence.openExec(null);
            for (int j = 1; localDataResultSet1.next(); j++) {
                String str3 = localDataResultSet1.getString(1);
                int k = localDataResultSet1.getInt(2).intValue();
                String str4 = Formats.CURRENCY.formatValue(new Double(localDataResultSet1.getDouble(4).doubleValue()));
                Double localDouble = localDataResultSet1.getDouble(4);
                String str5 = localSimpleDateFormat.format(localDataResultSet1.getTimestamp(3));
                if (j < i) {
                    if (localDouble.doubleValue() > 9.99D) {
                        str2 = "&lt;ref numeroRef=&quot;" + k + "-" + j + "&quot; dateRef=&quot;20" + str5 + "&quot; mtRefAvTaxes=&quot;+0000" + str4 + "&quot; /&gt;" + str2;
                    } else {
                        str2 = "&lt;ref numeroRef=&quot;" + k + "-" + j + "&quot; dateRef=&quot;20" + str5 + "&quot; mtRefAvTaxes=&quot;+00000" + str4 + "&quot; /&gt;" + str2;
                    }
                }
            }
            localDataResultSet1.close();
        } catch (Exception localException2) {
            Logger.getLogger(ScripletUtil.class.getName()).log(Level.SEVERE, null, localException2);
        }
        return str2;
    }

    public String printAddi(TicketInfo paramTicketInfo) {
        return paramTicketInfo == null ? "" : printAddi(paramTicketInfo);
    }

    public int countAddi(int paramInt) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyMMddhhmmss");
        String str = "select * from ADDI where TICKETID =" + paramInt;
        StaticSentence localStaticSentence = new StaticSentence(getSession(), str, null, SerializerReadInteger.INSTANCE);
        int i = 0;
        try {
            DataResultSet localDataResultSet = localStaticSentence.openExec(null);
            int j = 0;
            while (localDataResultSet.next()) {
                j++;
                if (j > 0) {
                    i = j;
                }
            }
            localDataResultSet.close();
        } catch (Exception localException) {
            Logger.getLogger(ScripletUtil.class.getName()).log(Level.SEVERE, null, localException);
        }
        return i;
    }

    public String countAddi(TicketInfo paramTicketInfo) {
        return paramTicketInfo == null ? "" : countAddi(paramTicketInfo);
    }

    public Session getSession() {
        if (this.session == null) {
            AppConfig localAppConfig;
            localAppConfig = new AppConfig(new String[0]);
            localAppConfig.load();
            String str1 = localAppConfig.getProperty("db.URL");
            String str2 = localAppConfig.getProperty("db.user");
            String str3 = localAppConfig.getProperty("db.password");
            if ((str2 != null) && (str3 != null) && (str3.startsWith("crypt:"))) {
                AltEncrypter localAltEncrypter = new AltEncrypter("cypherkey" + str2);
                str3 = localAltEncrypter.decrypt(str3.substring(6));
            }
            try {
                this.session = new Session(str1, str2, str3);
            } catch (SQLException localSQLException) {
                Logger.getLogger(ScripletUtil.class.getName()).log(Level.SEVERE, null, localSQLException);
            }
        }
        return this.session;
    }
}
