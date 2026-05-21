package com.mypham.util;

import jakarta.servlet.http.HttpSession;

public class AuthHelper {

    public static boolean isAdmin(HttpSession session) {
        if (session == null) {
            return false;
        }
        String vaiTro = (String) session.getAttribute("vaiTro");
        return "ADMIN".equals(vaiTro);
    }

    public static boolean isLoggedIn(HttpSession session) {
        if (session == null) {
            return false;
        }
        return session.getAttribute("user") != null;
    }
}
