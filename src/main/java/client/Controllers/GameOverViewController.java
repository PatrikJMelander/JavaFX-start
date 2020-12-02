package client.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.io.Serializable;
import java.util.ResourceBundle;

public class GameOverViewController implements Initializable, Runnable, Serializable{
    Thread thread = new Thread(this);
    ScreenNavigator s = new ScreenNavigator();
    ObjectInputStream in;
    ObjectOutputStream out;
    int rounds = 0;

    @FXML
    public Button nextRoundBtn1;

    @FXML
    private Text resultText;

    @FXML
    private Button playAgainBtn;

    @FXML
    private FlowPane fPanePl1;

    @FXML
    private FlowPane fPanePl2;

    @FXML
    private FlowPane roundsTextBox;


    @FXML
    void playAgainBtnClicked(ActionEvent event) throws IOException, ClassNotFoundException {
        s.loadNewScreen(ScreenNavigator.MAIN_MENU, playAgainBtn);
    }

    @FXML
    void endGameBtnClicked(ActionEvent event) throws IOException, ClassNotFoundException {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        out = ScreenNavigator.outputStreamer;
        in = ScreenNavigator.inputStreamer;

        playAgainBtn.managedProperty().bind(playAgainBtn.visibleProperty());
        nextRoundBtn1.managedProperty().bind(nextRoundBtn1.visibleProperty());

        thread.start();
    }

    public void nextRoundBtnClicked(ActionEvent actionEvent) {
        try {
            out.writeObject("START_NEXT_ROUND");
            out.flush();

            updateGameWindow();
          
            } catch(IOException  | ClassNotFoundException e){
                e.printStackTrace();
            }
    }

    @Override
    public void run()
    {
        try
        {
            int counter = 0;
            out.writeObject("IS_GAME_OVER");
            Object inputObject;
            
            while((inputObject = in.readObject())!=null)
            {
                if (inputObject instanceof Boolean)
                {
                    if ((Boolean) inputObject) nextRoundBtn1.setVisible(false);
                    else playAgainBtn.setVisible(false);
                    out.writeObject("RESULT");

                }
                
                else if (inputObject instanceof String)
                {
                    if (inputObject.toString().startsWith("POINTS"))
                    {
                        addPoints(inputObject.toString());
                        
                    }

                }
                
                else if (inputObject instanceof int[][])
                {
                    int[][] resultArray = (int[][]) inputObject;
                    if(counter == 0)
                    {
                        addcircles(resultArray , fPanePl1);
                        out.writeObject("PLAYER2");
                        counter++;
    
                    }

                    else if(counter == 1)
                    {
                        addcircles(resultArray , fPanePl2);
                        addRoundText(rounds);
                        break;
                    }
                    
                }
                
                else if (inputObject instanceof long[][])
                {
                    long[][] resultArray = (long[][]) inputObject;
                    
                    if (counter == 0)
                    {
                        addcircles(resultArray, fPanePl1);
                        out.writeObject("PLAYER2");
                        counter++;
                    
                    }
                    
                    else if (counter == 1)
                    {
                        addcircles(resultArray, fPanePl2);
                        addRoundText(rounds);
                        break;
                    }
                
                }
                
            }
        }
    
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
            
        }
        
    }

    public void updateGameWindow() throws IOException, ClassNotFoundException {
        String temp;
        temp = in.readObject().toString();
        if (temp.equals("WAITING"))
            s.loadNewScreen(ScreenNavigator.WAITING, nextRoundBtn1);

        else if (temp.equals("GO_TO_CHOOSE_CATEGORY"))
            s.loadNewScreen(ScreenNavigator.SELECT_CATEGORY, nextRoundBtn1);

    }

    public void addcircles(int[][] array, FlowPane flowPane)
    {
        for (int[] ints : array)
        {
            for (int j = 0; j < array.length; j++)
            {
                Circle c = new Circle();
                if (ints[j] == 1) c.setFill(Color.GREENYELLOW);
                else if (ints[j] == 2) c.setFill(Color.RED);
                else if (ints[j] == 0) c.setVisible(false);
                c.setRadius(12);
                
                Platform.runLater(() ->
                {
                    flowPane.getChildren().add(c);
                    
                });
                
            }
            
        }
        
    }
    
    
    public void addcircles(long[][] array, FlowPane flowPane)
    {
        for (long[] longs : array)
        {
            for (int j = 0; j < array.length; j++)
            {
                Circle c = new Circle();
                if (longs[j] == 1) c.setFill(Color.GREENYELLOW);
                else if (longs[j] == 2) c.setFill(Color.RED);
                else if (longs[j] == 0) c.setVisible(false);
                c.setRadius(12);
                
                Platform.runLater(() ->
                {
                    flowPane.getChildren().add(c);
                    
                });
                
            }
            
        }
        
    }
    

    public void addRoundText(int qtyRounds) {
        for (int i = 0; i < qtyRounds; i++) {
            Text temp = new Text("Omgång " + (i + 1));
            temp.setTextAlignment(TextAlignment.CENTER);
            temp.setFont(Font.font(null, FontWeight.BOLD, 16));
            temp.setFill(Color.WHITE);
            temp.setWrappingWidth(100.0);
            Platform.runLater(() -> roundsTextBox.getChildren().add(temp));
        }
    }

    public void addPoints(String input)
    {
        String
            inputCorrecterS,
            pointsHolder1,
            pointsHolder2,
            outputResultS;
        
        int
            inputCorrecterInt,
            roundPlcement;
    
        inputCorrecterInt = input.indexOf("-1");
        
        
        System.out.println("Så här ser poängsträngen ut: " + input);
        
        
        try
        {
            if(inputCorrecterInt != -1)
            {
                inputCorrecterS = input.substring(0, inputCorrecterInt);
    
                if(inputCorrecterS.equals("POINTS"))
                {
                    outputResultS = "PLAYER1_CONCEDE";
                    pointsHolder1 = "0";
                    pointsHolder2 = input.substring(8 , 9);
    
                    System.out.println("SPELARE 1 GAVE UPP");
                    
                }
                
                else
                {
                    outputResultS = "PLAYER2_CONCEDE";
                    pointsHolder1 = input.substring(7 , 8);
                    pointsHolder2 = "0";
    
                    System.out.println("SPELARE 2 GAVE UPP");
                    
                }
    
                roundPlcement = 9;
                
            }
            
            else
            {
                outputResultS = "PLAYER1";
                pointsHolder1 = input.substring(6 , 7);
                pointsHolder2 = input.substring(7 , 8);
                roundPlcement = 8;
                
            }
    
    
            int pointPlayer1 = Integer.parseInt(pointsHolder1);
            int pointPlayer2 = Integer.parseInt(pointsHolder2);
            this.rounds = Integer.parseInt(input.substring(roundPlcement));
            this.resultText.setText(pointPlayer1 + " - " + pointPlayer2);
            System.out.println(pointPlayer1 + " - " + pointPlayer2);
            out.writeObject(outputResultS);
    
    
            if(inputCorrecterInt != -1)
            {
                s.loadNewScreen("CONCEDE", playAgainBtn);
                
            }
            
            
        }
        
        catch(IOException e)
        {
            e.printStackTrace();
            
        }
        
    }
}