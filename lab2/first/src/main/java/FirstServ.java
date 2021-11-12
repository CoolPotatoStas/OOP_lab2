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

@WebServlet("/first")
public class FirstServ extends HttpServlet {

    private ArrayList<String> IDofSession = new ArrayList<>();
    private ArrayList<Integer> countOfSession = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession();
        System.out.println("++++++++++++++" + s);
        String tmp = s.getId();
        int index = -1;
        boolean flag = false;
        for (int i = 0; i < IDofSession.size(); i++){
            if (IDofSession.get(i).equals(tmp)){
                index = i;
                flag = true;
                Integer count = countOfSession.get(i);
                count++;
                countOfSession.set(i, count);
                if (count >= 3 && req.getSession().getId().equals(IDofSession.get(i))){
                    resp.setContentType("text/html");
                    PrintWriter printWriter = resp.getWriter();
                    printWriter.write("Access denied");
                    printWriter.close();
                    return;
                }
                break;
            }
        }

        if (!flag){
            IDofSession.add(s.getId());
            countOfSession.add(0);
            index = IDofSession.size() - 1;
        }

        String log = req.getParameter("login");
        System.out.println("++++++++++++++++++++++++" + log);
        String pasw = req.getParameter("pass");
        System.out.println("++++++++++++++++++++++++" + pasw);

        File fl = new File("D:/tomcat_apache/apache-tomcat-9.0.54-windows-x64/apache-tomcat-9.0.54/webapps/lab2/infoAboutPol.txt");
        FileReader fr = new FileReader(fl);
        Scanner sfr = new Scanner(fr);
        String usLog = sfr.next();
        String usPas = sfr.next();

        String textAnswer = "";
        if (log != null && !log.isEmpty() && pasw != null && !pasw.isEmpty()){
            textAnswer = "<p style=\"color:#ff0000\">";
            if ( log.equals(usLog) && pasw.equals(usPas)){
                textAnswer += "Welcome," + log + " Time: ";
                Date date = new Date();
                textAnswer += date.toString();

                countOfSession.set(index, 0);
            } else {
                int c = countOfSession.get(index);
                c++;
                countOfSession.set(index, c);
                textAnswer += "try Again, count: " + Integer.toString(c);
            }
            textAnswer += "</p>";

        }

        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();
        File f = new File("D:/tomcat_apache/apache-tomcat-9.0.54-windows-x64/apache-tomcat-9.0.54/webapps/lab2/WEB-INF/classes/info.txt");
        String text = "";
        try (Scanner scan = new Scanner(f).useDelimiter("\\n")) {
            while(scan.hasNext()) {
                text += scan.next();
            }
        }
        printWriter.write(text + textAnswer);
        printWriter.close();
        /*RequestDispatcher view = req.getRequestDispatcher("first.html");
        view.forward(req, resp);*/
    }
}
