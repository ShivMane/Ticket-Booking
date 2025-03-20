package org.example.util;

import org.mindrot.jbcrypt.BCrypt;

public class UserServiceUtil {

    public static String hashPassword(String plainPassword){
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String HashedPassword){
        return BCrypt.checkpw(plainPassword, HashedPassword);
    }
}
