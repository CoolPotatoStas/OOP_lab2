import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

@WebServlet("/third")
public class ThirdServ extends HttpServlet {

    private ArrayList<String> idOfSession = new ArrayList<>();
    private ArrayList<Integer> countOfSession = new ArrayList<>();

    private String forAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String textAnswer = "";

        HttpSession h = req.getSession();
        String s = h.getId();
        boolean flag = false;
        int index = -1;
        for (int i = 0; i < idOfSession.size(); i++){
            if (idOfSession.get(i).equals(s)){
                flag = true;
                index = i;
                break;
            }
        }
        if (!flag){
            idOfSession.add(s);
            countOfSession.add(0);
            index = idOfSession.size() - 1;
        }

        if (countOfSession.get(index) >= 3) {
            return "Access Denied";
        }

        String log = req.getParameter("login");
        String pasw = req.getParameter("pass");

        File fl = new File("D:/tomcat_apache/apache-tomcat-9.0.54-windows-x64/apache-tomcat-9.0.54/webapps/lab2/infoAboutPol.txt");
        FileReader fr = new FileReader(fl);
        Scanner sfr = new Scanner(fr);
        String usLog = sfr.next();
        String usPas = sfr.next();

        if (log != null && !log.isEmpty() && pasw != null && !pasw.isEmpty()) {
            if (log.equals(usLog) && pasw.equals(usPas)) {
                textAnswer += "Welcome," + log + " Time: ";
                Date date = new Date();
                textAnswer += date.toString();
                countOfSession.set(index, 0);
            } else {
                int c = countOfSession.get(index);
                c++;
                countOfSession.set(index, c);
                textAnswer += "try Again, count: " + c;
            }
        }

        return textAnswer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(forAll(req,resp));
        printWriter.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(forAll(req,resp));
        printWriter.close();
    }
}