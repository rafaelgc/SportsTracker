/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seguimientodeportivo;

import controllers.MainScreenController;
import javafx.application.Application;
import javafx.stage.Stage;
import util.SceneTransition;

/**
 *
 * @author Rafa
 */
public class SeguimientoDeportivo extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        SceneTransition.<MainScreenController>showView(stage, "/views/MainScreen.fxml").init(stage);
        stage.setTitle("Seguimiento deportivo");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
