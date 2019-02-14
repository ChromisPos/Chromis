/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.blobreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.data.loader.ConnectionFactory;

/**
 *
 * @author John
 */
public class Reader {

    private static Connection con;

    public static void main(String[] args) {
        con = ConnectionFactory.getInstance().getConnection();

        new File(System.getProperty("user.home") + "/ChromisFiles").mkdirs();
        
        try {
            String sql = "select * from resources where restype='0'";
            Statement pstmt = con.createStatement();

            ResultSet rs = pstmt.executeQuery(sql);
            while (rs.next()) {
                java.sql.Blob blob = rs.getBlob("CONTENT");

                if (rs.getString("NAME").indexOf('/') == -1){
                InputStream in = blob.getBinaryStream();
                OutputStream out = new FileOutputStream(System.getProperty("user.home") + "/ChromisFiles/" + rs.getString("name"));
                byte[] buff = new byte[4096];  // how much of the blob to read/write at a time
                int len = 0;

                while ((len = in.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }

                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
