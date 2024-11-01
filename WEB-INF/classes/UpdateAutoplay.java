import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

import java.util.*;
import javax.print.attribute.standard.RequestingUserName;

public class UpdateAutoplay extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.sendRedirect("login");
            return;
        }

        String enabled = req.getParameter("enabled");
        session.setAttribute("autoplay", Boolean.parseBoolean(enabled));

        res.setStatus(302);
        res.sendRedirect("questions");
    }
}
