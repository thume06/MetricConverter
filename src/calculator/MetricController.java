package calculator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.*;
import java.io.*;

public class MetricController implements Initializable, ControlledScreen {
    private ScreensController myController;
    private Main mainClass;
    private int count = 0;
    private static boolean toAdd = false;
    private boolean unloaded = true;

    ArrayList<MetricIngredient> metricArray = new ArrayList<MetricIngredient>();
    static ArrayList<Ingredient> addList = new ArrayList<Ingredient>();
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    TextInputDialog dialog = new TextInputDialog();

    @FXML Button btnReturn;
    @FXML ListView convertOptions;
    @FXML Button btnAdd;
    @FXML Button btnRemove;
    @FXML TextField metricAmount2;
    @FXML Button btnReload;

    public void initialize(URL url, ResourceBundle rb) {
        mainClass = Main.getInstance();

    }

    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @FXML public void Calculator(){
        myController.setScreen(Main.screen1ID);
    }

    @FXML public void Add(){
        mainClass.resize(300, 250);
        myController.setScreen(Main.screen5ID);
    }

    @FXML public void Remove(){
        String removeFile;
        int selectionIndex = convertOptions.getSelectionModel().getSelectedIndex();
        convertOptions.getItems().remove(selectionIndex);
        convertOptions.getSelectionModel().select(selectionIndex);
        String toRemove = metricArray.get(selectionIndex).getTextInfo();
        System.out.println("To remove: " + toRemove);
        convertOptions.getSelectionModel().clearSelection();

        //Remove from the saved list
        try {
            File original = new File("conversions.txt");
            File tempFile = new File("tmpconversions.txt");
            int writeCount = 0;
            boolean nameCheck;
            boolean saCheck;
            boolean suCheck;
            boolean maCheck;

            Scanner read = new Scanner (new File("conversions.txt"));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));

            read.useDelimiter(",|\\n");
            String name, sa, su, ma;
            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while (read.hasNext()){
                name = read.next();
                nameCheck = !name.equals(metricArray.get(selectionIndex).getName());
                sa = read.next();
                saCheck = !String.valueOf(Double.valueOf(sa)).equals(String.valueOf(metricArray.get(selectionIndex).getStdAmnt()));
                su = read.next();
                suCheck = !su.equals(metricArray.get(selectionIndex).getStdUnit());
                ma = read.next();
                maCheck = !String.valueOf(Double.valueOf(ma)).equals(String.valueOf(metricArray.get(selectionIndex).getMetricAmnt()));
                if(nameCheck || saCheck || suCheck || maCheck){
                    //If this is not what you wanted to remove
                    removeFile = (name + "," + sa + "," + su +"," + ma);
                    if(!removeFile.equals(toRemove)) {
                        System.out.println(removeFile);
                        if (writeCount > 0) {
                            bw.newLine();
                        }
                        bw.write(removeFile);
                    }
                    writeCount = writeCount + 1;
                }
            }
            bw.close();
            read.close();
            metricArray.remove(selectionIndex);
            original.delete();
            tempFile.renameTo(new File("conversions.txt"));
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML public void Convert(){
        double grams;
        try{
            Double.parseDouble(metricAmount2.getText());
            grams = Double.valueOf(metricAmount2.getText());
        }
        catch(NumberFormatException e){
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid decimal or whole number value in metric amount field");
            alert.showAndWait();
            return;
        }
        int selectionIndex = convertOptions.getSelectionModel().getSelectedIndex();
        metricArray.get(selectionIndex).GramstoStd(grams);

        confirm.setTitle("Conversion");
        confirm.setHeaderText(null);
        confirm.setContentText("The standard amount of " + grams + " grams for " + metricArray.get(selectionIndex).getName() + " is about " + metricArray.get(selectionIndex).getStdAmnt() + " " + metricArray.get(selectionIndex).getStdUnit() + "\n\nWould you like to add this to your recipe?");
        confirm.setGraphic(null);

        ButtonType buttonTypeOne = new ButtonType("Add");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirm.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.get() == buttonTypeCancel){
            return;
        }

        addList.add(new Ingredient(metricArray.get(selectionIndex).getName(), metricArray.get(selectionIndex).getStdAmnt(), metricArray.get(selectionIndex).getStdUnit()));
        toAdd = true;
    }

    @FXML public void Load(){
        if(unloaded){
            //read file
            try{
                Scanner read = new Scanner (new File("conversions.txt"));
                read.useDelimiter(",|\\n");
                String name, sa, su, ma;

                int writeCount = 0;
                while(read.hasNext()){
                    name = read.next();
                    sa = read.next();
                    su = read.next();
                    ma = read.next();
                    metricArray.add(new MetricIngredient(name, Double.valueOf(sa), su, Double.valueOf(ma)));
                    convertOptions.getItems().add(name + " (" + sa + " " +su + " = " + ma + " grams)");
                    writeCount = writeCount + 1;
                }
                read.close();
            }
            catch(FileNotFoundException ex){
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to load existing metric conversions.");
                alert.showAndWait();
                unloaded = false;
                return;
            }
            unloaded = false;
        }
        if(AddController.toAdd){
            convertOptions.getItems().add(AddController.addString);
            AddController.toAdd = false;
            metricArray.add(new MetricIngredient(AddController.metricArray.get(0).getName(), AddController.metricArray.get(0).getStdAmnt(), AddController.metricArray.get(0).getStdUnit(), AddController.metricArray.get(0).getMetricAmnt()));
            AddController.metricArray.clear();
        }
    }

    @FXML public void Reload(){
        confirm.setTitle("Reload");
        confirm.setHeaderText(null);
        confirm.setContentText("This will reload the original list of conversions, not including any that you have added. Do you wish to continue?");
        confirm.setGraphic(null);

        ButtonType buttonTypeOne = new ButtonType("Continue");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirm.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.get() == buttonTypeCancel){
            return;
        }
        try{
            Scanner read = new Scanner (new File("conversionsOriginal.txt"));
            File tempFile = new File("tempOriginal.txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
            read.useDelimiter(",|\\n");
            String name, sa, su, ma;
            int writeCount = 0;
            metricArray.clear();
            convertOptions.getItems().clear();
            while(read.hasNext()){
                if(writeCount > 0){
                    bw.write(",");
                }
                name = read.next();
                sa = read.next();
                su = read.next();
                ma = read.next();
                metricArray.add(new MetricIngredient(name, Double.valueOf(sa), su, Double.valueOf(ma)));
                bw.write(metricArray.get(metricArray.size() - 1).getTextInfo());
                convertOptions.getItems().add(name + " (" + sa + " " +su + " = " + ma + " grams)");
                writeCount = writeCount + 1;
            }
            bw.close();
            read.close();
            File conversions = new File("conversions.txt");
            conversions.delete();
            tempFile.renameTo(new File("conversions.txt"));
        }
        catch(FileNotFoundException ex){
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load/save original metric conversions.");
            alert.showAndWait();
            return;
        }
        catch(IOException e){
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load/save original metric conversions.");
            alert.showAndWait();
        }

    }

    public static boolean getToAdd(){
        return toAdd;
    }
    public static void setToAdd(boolean a){
        toAdd = a;
    }
}
