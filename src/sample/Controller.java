package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
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
    private StackedBarChart killchart;

    @FXML
    private BarChart scores;

    @FXML
    private BarChart reloadchart;

    @FXML
    private ScatterChart camchart;

    private HashMap<String,Vector<int[]>> mehrhuhndeaths = new HashMap<>();
    private HashMap<String,Vector<float[]>> campositions = new HashMap<>();
    private HashMap<String,Vector<Integer>> score = new HashMap<>();
    private HashMap<String,int[]> reloads = new HashMap<>();


    @FXML
    private void loadLog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(new File("I:\\Users\\Jerry\\Documents\\Tests"));
        List<File> files = fileChooser.showOpenMultipleDialog(mainStage.getScene().getWindow());



        String level;
        for(File file : files)
        {
            System.out.println(file.getAbsolutePath());
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

                if(!reloads.containsKey(level)){
                    reloads.put(level,new int[3]);
                }
                l = doc.getElementsByTagName("relode_lmb");
                for (int index = 0; index < l.getLength(); index++)
                {
                    reloads.get(level)[0]++;
                }
                l = doc.getElementsByTagName("relode_rmb");
                for (int index = 0; index < l.getLength(); index++)
                {
                    reloads.get(level)[1]++;
                }
                reloads.get(level)[2]++;


                if(!campositions.containsKey(level)){
                    campositions.put(level,new Vector<>());
                }
                l = doc.getElementsByTagName("camPos");
                for (int index = 0; index < l.getLength(); index++)
                {
                    Node pos = l.item(index);

                    Node time_node = pos.getChildNodes().item(1);
                    String value = time_node.getFirstChild().getNodeValue().replace(',','.');
                    float time = Float.parseFloat(value);

                    Node x_pos_node = pos.getChildNodes().item(3);
                    float x_pos = Float.parseFloat(x_pos_node.getChildNodes().item(1).getFirstChild().getNodeValue().replace(',','.'));

                    campositions.get(level).add(new float[]{x_pos,time});
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        ObservableList<XYChart.Series<String,Number>> level_data = (killchart.getData() == null?FXCollections.observableArrayList():killchart.getData());
        XYChart.Series<String, Number> s_data_025p = new XYChart.Series<>();
        s_data_025p.setName("25P");

        XYChart.Series<String, Number> s_data_050p = new XYChart.Series<>();
        s_data_050p.setName("50P");

        XYChart.Series<String, Number> s_data_100p = new XYChart.Series<>();
        s_data_100p.setName("100P");


        for(String key : mehrhuhndeaths.keySet()){
            float[] kill_count = new float[3];
            for(int[] kills : mehrhuhndeaths.get(key)){
                kill_count[0] += kills[0];
                kill_count[1] += kills[1];
                kill_count[2] += kills[2];
            }

            float kill_size = mehrhuhndeaths.get(key).size();

            s_data_025p.getData().add(new XYChart.Data<>(key, kill_count[0]/kill_size));
            s_data_050p.getData().add(new XYChart.Data<>(key, kill_count[1]/kill_size));
            s_data_100p.getData().add(new XYChart.Data<>(key, kill_count[2]/kill_size));

            System.out.println("Level "+ key+ " 25p: "+(kill_count[0]/mehrhuhndeaths.get(key).size())+ " 50p: "+(kill_count[1]/mehrhuhndeaths.get(key).size())+ " 100p: "+(kill_count[2]/mehrhuhndeaths.get(key).size()));
        }
        level_data.add(s_data_025p);
        level_data.add(s_data_050p);
        level_data.add(s_data_100p);
        killchart.setData(level_data);


        ObservableList<XYChart.Series<String,Number>> score_data = (scores.getData() == null?FXCollections.observableArrayList():scores.getData());
        XYChart.Series<String, Number> s_data_min = new XYChart.Series<>();
        s_data_min.setName("Min Score");

        XYChart.Series<String, Number> s_data_med = new XYChart.Series<>();
        s_data_med.setName("Medium Score");

        XYChart.Series<String, Number> s_data_max = new XYChart.Series<>();
        s_data_max.setName("Max Score");

        for(String key : score.keySet()){

            System.out.println("Level "+ key);

            s_data_max.getData().add(new XYChart.Data<>(key,score.get(key).get(0)));
            s_data_min.getData().add(new XYChart.Data<>(key,score.get(key).get(score.get(key).size()-1)));

            float med = 0;
            for(Integer points : score.get(key))
            {
                med += points;
                System.out.println("Score: "+ points);
            }
            med /= score.get(key).size();
            s_data_med.getData().add(new XYChart.Data<>(key,med));
        }

        score_data.add(s_data_min);
        score_data.add(s_data_med);
        score_data.add(s_data_max);
        scores.setData(score_data);


        ObservableList<XYChart.Series<String,Number>> reload_data = (reloadchart.getData() == null?FXCollections.observableArrayList():reloadchart.getData());
        XYChart.Series<String, Number> s_data_lmb = new XYChart.Series<>();
        s_data_lmb.setName("Empty Clip reload(LMB)");

        XYChart.Series<String, Number> s_data_rmb = new XYChart.Series<>();
        s_data_rmb.setName("Clip reload(RMB)");


        for(String key : reloads.keySet()){
            s_data_lmb.getData().add(new XYChart.Data<>(key,reloads.get(key)[0]/reloads.get(key)[2]));
            s_data_rmb.getData().add(new XYChart.Data<>(key,reloads.get(key)[1]/reloads.get(key)[2]));
        }

        reload_data.add(s_data_lmb);
        reload_data.add(s_data_rmb);
        reloadchart.setData(reload_data);


        ObservableList<XYChart.Series<Number,Number>> Cambos_data_data = (camchart.getData() == null?FXCollections.observableArrayList():camchart.getData());
        for(String key : campositions.keySet()){
            XYChart.Series<Number, Number> level_pos = new XYChart.Series<>();
            level_pos.setName(key);

            for(float[] pos : campositions.get(key))
            {
                level_pos.getData().add(new XYChart.Data<>(pos[0],pos[1]));
            }

            Cambos_data_data.add(level_pos);
        }

        camchart.setData(Cambos_data_data);
    }
}
