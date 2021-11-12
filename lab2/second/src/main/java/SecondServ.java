import org.apache.catalina.Session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

@WebServlet("/second")
public class SecondServ extends HttpServlet {

    private ArrayList<String> arrOfSession = new ArrayList<>();
    private ArrayList<HttpSession> allSession = new ArrayList<>();
    private ArrayList<Integer> countOfSession = new ArrayList<>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        int index = -1;
        boolean flag = false;
        HttpSession h = req.getSession();
        String s = req.getSession().getId();
        for (int i = 0; i < arrOfSession.size();i++){
            if (s.equals(arrOfSession.get(i))){
                flag = true;
                index = i;
                int c = countOfSession.get(i);
                c++;
                countOfSession.set(i,c);
            }
        }

        if (!flag && s != null){
            allSession.add(h);
            arrOfSession.add(s);
            countOfSession.add(0);
            index = arrOfSession.size() - 1;
        }

        PrintWriter printWriter = resp.getWriter();
        File f = new File("D:/tomcat_apache/apache-tomcat-9.0.54-windows-x64/apache-tomcat-9.0.54/webapps/lab2/WEB-INF/classes/secInfo.txt");
        String text = "";
        try (Scanner scan = new Scanner(f).useDelimiter("\\n")) {
            while(scan.hasNext()) {
                text += scan.next();
            }
        }
        printWriter.write(text);
        String tmp = req.getParameter("usersText");
        String textAnswer = "";
        if (tmp != null && !tmp.isEmpty()){
            Integer i = 0;
            try{
                i = Integer.parseInt(tmp);
            } catch (Exception ex){
                Random r = new Random();
                i = r.nextInt(1000);
            }
            textAnswer = "<p style=\"color:#ff0000\">";
            switch (i%4){
                case 0: {
                    textAnswer += "time:" + allSession.get(index) + " " + allSession.get(index).getCreationTime() + " " + allSession.get(index).getLastAccessedTime();
                    break;
                }
                case 1: {
                    Date date = new Date();
                    textAnswer += "Date: " + date.toString();
                    break;
                }
                case 2:{
                    int count = countOfSession.get(index);
                    count++;
                    countOfSession.set(index, count);
                    textAnswer += "Count: " + count;
                    break;
                }
                default:{
                    textAnswer += "Goose";
                }
            }
            textAnswer += "</p>";
        }
        printWriter.write(textAnswer);

        printWriter.close();
    }
}
