import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

public class MainServlet extends HttpServlet{
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if(session == null) {
            res.setStatus(302);
            res.sendRedirect("login");
        }

        //TEMP CODE FOR TESTING
        String title = "Logged in as: ";
        title += session.getAttribute("USER_ID");
        res.setContentType("text/html");
        String docType = "<!doctype html public \"-//dtd html 4.0 " + "transitional//en\">\n";
        String html = docType + "<html>\n" + 
            "<head><title>" + title + "</title></head>\n" + 
            "<body bgcolor=\"#f0f0f0\">\n" + 
            "<h1 align=\"center\">" + title + "</h1>\n" +
            "<div align=\"center\">\n" +
            "<form action=\"category1\" method=\"get\">\n" + 
            "    <input type=\"submit\" value=\"Category 1\" />\n" + 
            "</form>\n" +
            "<form action=\"category2\" method=\"get\">\n" + 
            "    <input type=\"submit\" value=\"Category 2\" />\n" + 
            "</form>\n" +
            "<form action=\"category3\" method=\"get\">\n" + 
            "    <input type=\"submit\" value=\"Category 3\" />\n" + 
            "</form>\n" +
            "</div>\n" +
            "</body></html>";
        PrintWriter out = res.getWriter();
        out.println(html);
        

    }
}
