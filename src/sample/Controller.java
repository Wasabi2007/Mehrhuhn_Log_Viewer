package sample;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class Controller {

    @FXML
    private BorderPane mainStage;

    @FXML
    private BarChart killchart;

    private HashMap<String,Vector<int[]>> mehrhuhndeaths = new HashMap<>();
    private HashMap<String,Vector<Integer>> score = new HashMap<>();

    @FXML
    private void loadLog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(new File("I:\\Users\\Jerry\\Documents\\Tests"));
        List<File> files = fileChooser.showOpenMultipleDialog(mainStage.getScene().getWindow());

        String level;
        for(File file : files)
        {
            try (FileInputStream in = new FileInputStream(file))
            {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(in);

                level = doc.getElementsByTagName("Level").item(0).getAttributes().item(0).getNodeValue();
                if(!mehrhuhndeaths.containsKey(level)){
                    mehrhuhndeaths.put(level,new Vector<>());
                }
                int[] kill_count = new int[3];
                int pointscore = 0;

                NodeList l = doc.getElementsByTagName("mehrhuhn_death");
                for (int index = 0; index < l.getLength(); index++)
                {
                    Node kill = l.item(index);

                    Node point_node = kill.getChildNodes().item(3);
                    String points = point_node.getFirstChild().getNodeValue();
                    switch (points){
                        case "25":
                            kill_count[0]++;
                            pointscore += 25;
                            break;
                        case "50":
                            kill_count[1]++;
                            pointscore += 50;

                            break;
                        case "100":
                            kill_count[2]++;
                            pointscore += 100;
                            break;
                    }
                }

                mehrhuhndeaths.get(level).add(kill_count);

                //score;
                if(!score.containsKey(level)){
                    score.put(level,new Vector<>());
                }

                score.get(level).add(pointscore);
                score.get(level).sort((a,b)->b-a);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        for(String key : mehrhuhndeaths.keySet()){
            float[] kill_count = new float[3];
            for(int[] kills : mehrhuhndeaths.get(key)){
                kill_count[0] += kills[0];
                kill_count[1] += kills[1];
                kill_count[2] += kills[2];
            }

            System.out.println("Level "+ key+ " 25p: "+(kill_count[0]/mehrhuhndeaths.get(key).size())+ " 50p: "+(kill_count[1]/mehrhuhndeaths.get(key).size())+ " 100p: "+(kill_count[2]/mehrhuhndeaths.get(key).size()));
        }

        for(String key : score.keySet()){

            System.out.println("Level "+ key);
            for(Integer points : score.get(key))
            {
                System.out.println("Score: "+ points);
            }

        }
    }
}
