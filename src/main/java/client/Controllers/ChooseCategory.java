package client.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChooseCategory implements Initializable {


        ObjectOutputStream out;
        ObjectInputStream in;
        ScreenNavigator s = new ScreenNavigator();
        ObservableList<Button> buttonList = FXCollections.observableArrayList();


        @FXML
        private Button Cat1Btn;

        @FXML
        private Button Cat2Btn;

        @FXML
        private Button Cat3Btn;

        @FXML
        void categoryChosen(ActionEvent event) throws IOException {

                if  ((event.getSource()).equals(Cat1Btn)) {
                        String send = Cat1Btn.getText();
                        out.writeObject("CATEGORY" + send);
                }
                else if  ((event.getSource()).equals(Cat2Btn)) {
                        out.writeObject("CATEGORY" + Cat2Btn.getText());
                }
                else if  ((event.getSource()).equals(Cat3Btn)) {
                        out.writeObject("CATEGORY" + Cat3Btn.getText());
                }



                s.loadNewScreen(ScreenNavigator.WAITING, Cat1Btn);
        }



        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
                buttonList.addAll(Cat1Btn, Cat2Btn, Cat3Btn);
                out = ScreenNavigator.outputStreamer;
                in = ScreenNavigator.inputStreamer;
                List<String> categorys = new ArrayList<>();
                int counter = 0;
                try {
                        out.writeObject("GET_3_CATEGORIES");
                        categorys = (List<String>) in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                }
                for (var category : categorys) {
                        if (counter==0)
                                Cat1Btn .setText(category);
                        if (counter==1)
                                Cat2Btn .setText(category);
                        if (counter==2)
                                Cat3Btn .setText(category);
                        counter++;
                }

        }
}