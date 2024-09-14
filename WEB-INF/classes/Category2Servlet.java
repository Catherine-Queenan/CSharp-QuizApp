import jakarta.servlet.http.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;

@WebServlet("/category2")
public class Category2Servlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        
        String docType = "<!doctype html public \"-//dtd html 4.0 " + "transitional//en\">\n";
        String title = "Category 2 Page";
        
        out.println(docType + 
            "<html>\n" +
            "<head><title>" + title + "</title></head>\n" +
            "<body bgcolor=\"#f0f0f0\">\n" +
            "<h1 align=\"center\">" + title + "</h1>\n" +
            "<p align=\"center\">Welcome to Category 2!</p>\n" +
            "</body></html>");
    }
}
