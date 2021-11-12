import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@WebServlet("/five")
public class fiveServ extends HttpServlet {
    private static Document convertStringToXMLDocument(String xmlString)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //не готово
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String firstChoice = req.getParameter("choice");
        String secondChoice = req.getParameter("date");

        if (firstChoice != null && !firstChoice.isEmpty() && secondChoice != null && !secondChoice.isEmpty()) {

            String firPeriod = req.getParameter("firPer");
            String secPeriod = req.getParameter("secPer");

            int flag1 = 0;
            int flag2 = 0;
            if (firstChoice.equals("valuta")) {
                flag1 = 1;
            } else {
                flag1 = 2;
            }

            if (secondChoice.equals("now")) {
                flag2 = 1;
            } else {
                flag2 = 2;
            }

            String myReq = "";
            if (flag1 == 1 && flag2 == 1) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                // myReq = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + dateFormat.format(date);
                myReq = "http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=" + dateFormat.format(date) + "&date_req2=" + dateFormat.format(date) + "&VAL_NM_RQ=R01235";
            } else if (flag1 == 1 && flag2 == 2) {//добавить выбор периода
                myReq = "http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=" + firPeriod + "&date_req2=" + secPeriod + "&VAL_NM_RQ=R01235";
            } else if (flag1 == 2 && flag2 == 1) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                myReq = "http://www.cbr.ru/scripts/xml_metall.asp?date_req1=" + dateFormat.format(date) + "&date_req2=" + dateFormat.format(date);
            } else { //добавить выбор периодов
                myReq = "http://www.cbr.ru/scripts/xml_metall.asp?date_req1=" + firPeriod + "&date_req2=" + secPeriod;
            }

            URL url = null;
            HttpURLConnection con = null;
            try {
                url = new URL(myReq);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "text/html");
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
            } catch (Exception e) {
                System.out.println("в запросе проблема");
                return;
            }

            int status = 0;
            StringBuffer data = new StringBuffer(); // ответ в виде текста
            try {
                status = con.getResponseCode();
                if (status > 299) {
                    System.out.println("Статус " + status);
                    return;
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String str = null;
                while ((str = in.readLine()) != null) data.append(str);
                in.close();
                con.disconnect();
            } catch (Exception e) {
                System.out.println("в ответе проблема");
                return;
            }

            String textAnswer = "";
            Document doc = convertStringToXMLDocument(data.toString());
            doc.getDocumentElement().normalize();
            if (flag1 == 1 && flag2 == 1) {
                NodeList nodes = doc.getElementsByTagName("Record");;
                for (int i = 0; i < nodes.getLength(); i++) {
                    NodeList nl = nodes.item(i).getChildNodes();
                    textAnswer += "Cena 1 dollara: " + nl.item(1).getTextContent() + "\n";
                }

            } else if (flag1 == 1 && flag2 == 2) {
                NodeList nodes = doc.getElementsByTagName("Record");
                textAnswer += "Period " + firPeriod + " - " + secPeriod + "\n";
                for (int i = 0; i < nodes.getLength(); i++) {
                    textAnswer += "  next  \n";
                    NodeList nl = nodes.item(i).getChildNodes();
                    textAnswer += "Cena 1 dollara: " + nl.item(1).getTextContent() + "\n";
                }
            } else if (flag1 == 2 && flag2 == 1) {
                NodeList nodes = doc.getElementsByTagName("Record");
                for (int i = 0; i < nodes.getLength(); i++) {
                    NodeList nl = nodes.item(i).getChildNodes();
                    if (i % 4 == 0) {
                        textAnswer += "Zoloto: ";
                    } else if (i % 4 == 1) {
                        textAnswer += "Serebro: ";
                    } else if (i % 4 == 2) {
                        textAnswer += "Platina: ";
                    } else {
                        textAnswer += "Palladiy: ";
                    }

                    textAnswer += nl.item(0).getTextContent() + " " + nl.item(0).getTextContent() + "\n";
                }
            } else {
                textAnswer += "Period " + firPeriod + " - " + secPeriod + "\n";
                NodeList nodes = doc.getElementsByTagName("Record");
                int k = 0;
                for (int i = 0; i < nodes.getLength(); i++) {
                    textAnswer += "next\n";
                    k++;
                    NodeList nl = nodes.item(i).getChildNodes();
                    if (i % 4 == 0) {
                        textAnswer += "Zoloto: ";
                    } else if (i % 4 == 1) {
                        textAnswer += "Serebro: ";
                    } else if (i % 4 == 2) {
                        textAnswer += "Platina: ";
                    } else {
                        textAnswer += "Palladiy: ";
                    }

                    textAnswer += nl.item(0).getTextContent() + " " + nl.item(0).getTextContent() + "\n";
                    if (k == 3) {
                        k = 0;
                        textAnswer += "\n";
                    }
                }
            }
            resp.setContentType("text/plain");
            PrintWriter printWriter = resp.getWriter();
            printWriter.write(textAnswer);
            printWriter.close();
        }
    }
}
