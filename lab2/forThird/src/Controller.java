import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    private ObservableList<String> langs = FXCollections.observableArrayList("GET", "POST");

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private TextField login;

    @FXML
    private PasswordField pass;

    @FXML
    private Button butSubmit;

    @FXML
    private TextField info;

    @FXML
    void initialize() {
        choiceBox.setItems(langs);
        choiceBox.setValue("GET");

        butSubmit.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                if (login.getText() == null || login.getText().isEmpty()){
                    info.setText("Введите логин!");
                } else if(pass.getText() == null || pass.getText().isEmpty()){
                    info.setText("Введите пароль!");
                } else {
                    System.out.println();
                    //оформление http запроса
                    URL url = null;
                    HttpURLConnection con = null;
                    if (choiceBox.getValue().equals("GET")){
                        try {
                            url = new URL("http://localhost:8080/lab2/third?login=" + login.getText() + "&pass=" + pass.getText());
                            con = (HttpURLConnection)url.openConnection();
                            con.setRequestMethod("GET");
                            con.setRequestProperty("Content-Type", "text/html");
                            con.setConnectTimeout(5000);
                            con.setReadTimeout(5000);
                        } catch(Exception e) {
                            System.out.println("в гете проблема");
                            return;
                        }

                    } else {
                        try {
                            url = new URL("http://localhost:8080/lab2/third");
                            con = (HttpURLConnection)url.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty(
                                    "User-Agent", "Java client");
                            con.setRequestProperty(
                                    "Content-Type", "application/x-www-form-urlencoded");
                            con.setConnectTimeout(5000);
                            con.setReadTimeout(5000);
                            String args = "login=" + login.getText() + "&pass=" + pass.getText();
                            byte[] data = args.getBytes(StandardCharsets.UTF_8);
                            con.setDoOutput(true);

                            try (var wr = new DataOutputStream(con.getOutputStream())) {
                                wr.write(data);
                            }
                        } catch(Exception e) {
                            System.out.println("в посте проблема");
                            return;
                        }
                    }

                    int status = 0;
                    StringBuffer data = new StringBuffer(); // ответ в виде текста
                    try {
                        status = con.getResponseCode();
                        if(status > 299) {
                            System.out.println("Статус " + status);
                            return;
                        }
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String str = null;
                        while((str = in.readLine()) != null) data.append(str);
                        in.close();
                        con.disconnect();
                    } catch(Exception e) {
                        System.out.println("в ответе проблема");
                        return;
                    }

                    if (data.toString().equals("Access denied")){
                        butSubmit.setDisable(false);
                    }
                    info.setText(data.toString());
                }

            }
        });
    }
}