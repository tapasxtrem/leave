package Support;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Goutam
 */
public class GlobalClass {

    public String getStringFromRequest(HttpServletRequest request) {
        
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = request.getReader();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } finally {
                reader.close();
            }
        } catch (IOException ex) {
            DBRelated.DBPool.log(ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(GlobalClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sb.toString();
    }

}
