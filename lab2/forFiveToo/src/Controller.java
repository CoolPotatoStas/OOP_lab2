import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ChoiceBox<String> first;

    private ObservableList<String> firList = FXCollections.observableArrayList("Валюта", "Металл");

    @FXML
    private ChoiceBox<String> second;

    private ObservableList<String> secList = FXCollections.observableArrayList("Сегодня", "Период");

    @FXML
    private TextField firDate;

    @FXML
    private TextField secDate;

    @FXML
    private Button Submit;

    @FXML
    private TextArea textArea;

    @FXML
    void initialize() {
        first.setItems(firList);
        first.setValue("Валюта");
        second.setItems(secList);
        second.setValue("Сегодня");

        Submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                textArea.setText("");
                URL url = null;
                HttpURLConnection con = null;
                String f = "";
                String s = "";
                if (first.getValue().equals("Валюта")) {
                    f = "valuta";

                } else {
                    f = "metall";
                }
                if (second.getValue().equals("Сегодня")) {
                    s = "now";
                } else {
                    s = "another";
                }

                String th1 = "";
                String th2 = "";

                if (firDate != null && !firDate.getText().isEmpty() && secDate != null && !secDate.getText().isEmpty()) {
                    th1 = firDate.getText();
                    th2 = secDate.getText();
                } else {
                    th1 = th2 = "22/10/2021";
                }

                try {
                    url = new URL("http://localhost:8080/lab2/five?choice=" + f + "&date=" + s + "&firPer=" + th1 + "&secPer=" + th2);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-Type", "text/html");
                    con.setConnectTimeout(5000);
                    con.setReadTimeout(5000);
                } catch (Exception e) {
                    System.out.println("в гете проблема");
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
                //System.out.println(data);

                Scanner sc = new Scanner(data.toString());

                while (sc.hasNextLine()){
                    textArea.setText(textArea.getText() + " " + sc.nextLine() + "\n");
                }

                //textArea.setText(data.toString());
            }

        });

    }
}
