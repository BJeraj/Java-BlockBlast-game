 


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.Arrays;

public class BlockBlastGame extends Application {


    int points  = 0;

    int[] placed = {0, 0, 0};
    int[] canNotBePlaced = {0,0,0};
    int[][] grid = new int[8][8];
    public static final Color[] colors = {
            Color.RED, Color.GREEN, Color.BLUE,
            Color.YELLOW, Color.PURPLE, Color.CYAN, Color.ORANGE
    };

    Text text = new Text();


    int[][][] predefinedShapes = {
            {{0, 1, 0},
             {1, 1, 0}, 
             {0, 1, 0}},
            {{1, 1, 0}, 
             {1, 1, 0}, 
             {0, 0, 0}},
            {{1, 0, 0}, 
             {1, 1, 1},
             {0, 0, 0}},
            {{0, 1, 0},
             {1, 1, 0},
             {1, 0, 0}},
            {{1, 1, 1},
             {1, 1, 1},
             {1, 1, 1}},
            {{1, 1, 0}, 
             {1, 1, 0},
             {1, 1, 0}},
            {{1, 0, 0},
             {1, 0, 0},
             {0, 0, 0}},
            {{1, 1, 1},
             {0, 0, 1},
             {0, 0, 1}},
            {{1, 0, 0},
             {1, 0, 0},
             {1, 0, 0}}
    };

    BlockShape[] tab = new BlockShape[predefinedShapes.length];

    int rmd = (int) (Math.random() * predefinedShapes.length);
    //int rmdColor = (int) (Math.random() * colors.length);
    Pane pane = new Pane();



    @Override
    public void start(Stage primaryStage) {


        for (int i = 0; i < predefinedShapes.length; i++) {
            tab[i] = new BlockShape(predefinedShapes[i]);
        }

        text.setX(50);
        text.setY(500);
        text.setText("Points: " + points);
        text.setFont(Font.font("Arial", 30));

        pane.getChildren().add(text);

        Scene scene = new Scene(pane, 800, 700);
        createGrid();
        printGrid(grid);

        generateShapes();

        primaryStage.setScene(scene);
        primaryStage.setTitle("FakeBlockBlast");
        primaryStage.show();
    }
    private void createGrid() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle rectangle = new Rectangle(10 + i * 50, 10 + j * 50, 50, 50);
                rectangle.setFill(Color.GREY);
                rectangle.setStroke(Color.BLACK);
                pane.getChildren().add(rectangle);
            }
        }
    }
    private void generateShapes() {
        rmd = (int) (Math.random() * predefinedShapes.length);
        BlockShape shapeClone1 = new BlockShape(tab[rmd]);
        shapeClone1.color = colors[(int) (Math.random() * colors.length)];
        createDraggableShape(pane, shapeClone1, 500, 30, 0);

        rmd = (int) (Math.random() * predefinedShapes.length);
        BlockShape shapeClone2 = new BlockShape(tab[rmd]);
        shapeClone2.color = colors[(int) (Math.random() * colors.length)];
        createDraggableShape(pane, shapeClone2, 500, 250, 1);

        rmd = (int) (Math.random() * predefinedShapes.length);
        BlockShape shapeClone3 = new BlockShape(tab[rmd]);
        shapeClone3.color = colors[(int) (Math.random() * colors.length)];
        createDraggableShape(pane, shapeClone3, 500, 470, 2);
        
        checkAll();

    }
    int [][][] tmr_shapes = new int[3][3][3];
    private void createDraggableShape(Pane pane, BlockShape blockShape, int startX, int startY, int index) {
        Pane shapePane = new Pane();

        int rotationAngle = (int) (Math.random() * 4) * 90;
        int[][] rotatedShape = rotateShape(blockShape.shape, rotationAngle);

        tmr_shapes[index] = rotatedShape;

        //blockShape.color = colors[(int) (Math.random() * colors.length)];
        //shapeColors.put(blockShape, color);

        printShape(rotatedShape);

        drawShape(shapePane, rotatedShape, 0, 0, blockShape.color);
        shapePane.setLayoutX(startX);
        shapePane.setLayoutY(startY);

        pane.getChildren().add(shapePane);

        //checkAll();

        shapePane.setOnMouseDragged(event -> {
            shapePane.setLayoutX(event.getSceneX());
            shapePane.setLayoutY(event.getSceneY());

            shapePane.toFront();
        });

        shapePane.setOnMouseReleased(event -> {
            double snapX = Math.round((shapePane.getLayoutX() - 10) / 50.0) * 50 + 10;
            double snapY = Math.round((shapePane.getLayoutY() - 10) / 50.0) * 50 + 10;

            if (isPlacementValid(grid, rotatedShape, (int) snapX, (int) snapY)) {
                drawShape(pane, rotatedShape, (int) snapX, (int) snapY, blockShape.color);
                updateGrid(grid, rotatedShape, (int) snapX, (int) snapY);
                printGrid(grid);
                points += countSquares(blockShape);
                text.setText("Points: " + points * 10);

                pane.getChildren().remove(shapePane);
                placed[index] = 1;

                breakRows(checkRows(grid));
                breakCollumns(checkColumns(grid));

                if (placed[0] == 1 && placed[1] == 1 && placed[2] == 1) {
                    resetPlacedArray();
                    generateShapes();
                }

                checkAll();

            } else {
                shapePane.setLayoutX(startX);
                shapePane.setLayoutY(startY);
            }
            shapePane.setMouseTransparent(false);
        });


    }

    public static void printShape(int[][] shape){
        for(int i = 0; i<3;i++){
            for(int j = 0; j<3;j++){
                System.out.print(shape[i][j]+" ");

            }
            System.out.println();
        }
        System.out.println("----------");
    }

    private void checkAll(){
        
        Arrays.fill(canNotBePlaced, 0);
        for(int i = 0; i<3;i++){
            if(placed[i] == 0){
                if(!checkGameOver(tmr_shapes[i])){
                    canNotBePlaced[i] = 1;
                }
            }            
        }
        check();
    }
    private void check(){

        //System.out.println("Checking: placed = " + Arrays.toString(placed) + "  canNotBePlaced = "+Arrays.toString(canNotBePlaced));
        if(Arrays.compare(canNotBePlaced, new int[]{1, 1, 1}) == 0 ){
            gameOver();
        }else if(Arrays.compare(placed, new int[]{1,1,0}) == 0 && canNotBePlaced[2] == 1){
            gameOver();
        }else if(Arrays.compare(placed, new int[]{1,0,1}) == 0 && canNotBePlaced[1] == 1){
            gameOver();
        }else if(Arrays.compare(placed, new int[]{0,1,1}) == 0 && canNotBePlaced[0] == 1){
            gameOver();
        }else if(Arrays.compare(placed, new int[]{1,0,0}) == 0 && canNotBePlaced[1] == 1 && canNotBePlaced[2] == 1){
            gameOver();
        }else if(Arrays.compare(placed, new int[]{0,1,0}) == 0 && canNotBePlaced[0] == 1 && canNotBePlaced[2] == 1){
            gameOver();
        }else if(Arrays.compare(placed, new int[]{0,0,1}) == 0 && canNotBePlaced[0] == 1 && canNotBePlaced[1] == 1){
            gameOver();
        }
    }
    private boolean checkGameOver(int[][] shape) {

        for (int x = -2; x < 8; x++) {
            for (int y = -2; y < 8; y++) { //-2 ker lahko ima oblika 2 prazna stolpca ali vrstici
                if (isPlacementValid(grid, shape,x * 50 + 10,y *50 + 10)) {
                    return true;
                }
            }
        }

        return false;
    }
    private void gameOver() {

        Text gameOverText = new Text("GAME OVER");


        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        gameOverText.setFill(Color.RED);
        gameOverText.setStroke(Color.BLACK);
        gameOverText.setStrokeWidth(2);


        gameOverText.setX((pane.getWidth() - gameOverText.getLayoutBounds().getWidth()) / 2);
        gameOverText.setY((pane.getHeight() + gameOverText.getLayoutBounds().getHeight()) / 2);



        Rectangle bg = new Rectangle(
                gameOverText.getX() - 20,
                gameOverText.getY() - gameOverText.getLayoutBounds().getHeight() - 10,
                gameOverText.getLayoutBounds().getWidth() + 40,
                gameOverText.getLayoutBounds().getHeight() + 20
        );
        bg.setFill(Color.rgb(0, 0, 0, 0.7));
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(2);


        pane.getChildren().addAll(bg, gameOverText);


        //pane.setDisable(true);


        Button restartButton = new Button("Play Again");
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        restartButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        restartButton.setOnAction(e -> restartGame());


        restartButton.setLayoutX((pane.getWidth() - 150) / 2);
        restartButton.setLayoutY(gameOverText.getY() + 50);

        pane.getChildren().add(restartButton);
    }

    private void restartGame() {

        points = 0;
        text.setText("Points: " + points);
        Arrays.fill(placed, 0);
        Arrays.fill(canNotBePlaced, 0);


        for (int i = 0; i < 8; i++) {
            Arrays.fill(grid[i], 0);
        }


        pane.getChildren().clear();
        pane.getChildren().add(text);
        createGrid();


        pane.setDisable(false);
        generateShapes();
    }
    private int countSquares(BlockShape blockShape){
        int p = 0;
        for(int i = 0;i <blockShape.shape.length;i++){
            for(int j = 0; j<blockShape.shape[i].length;j++){
                if(blockShape.shape[i][j]==1){
                    p++;
                }
            }
        }
        return p;
    }

    private void resetPlacedArray() {
        for (int i = 0; i < placed.length; i++) {
            placed[i] = 0;
        }
    }

    private int[] checkColumns(int[][] grid){
        int[] full = new int[8];
        for(int i = 0; i<8;i++){full[i] = 1;};
        for(int i = 0; i<8; i++){
            for(int j = 0; j<8; j++){
                if(grid[j][i] == 0){
                    full[i] = 0;
                    break;
                }
            }
        }
        //System.out.println("Columns: " + Arrays.toString(full));
        return full;
    }
    private int[] checkRows(int[][] grid){
        int[] full = new int[8];
        for(int i = 0; i<8;i++){full[i] = 1;};
        for(int i = 0; i<8; i++){
            for(int j = 0; j<8; j++){
                if(grid[i][j] == 0){
                    full[i] = 0;
                    break;
                }
            }
        }
        //System.out.println("Rows: " + Arrays.toString(full));
        return full;
    }
    private void breakRows(int[] full){
        for(int i = 0;i < 8;i++){
            if(full[i] == 1){
                for (int j = 0; j < 8; j++) {
                    Rectangle rectangle = new Rectangle(10 + j * 50, 10 + i * 50, 50, 50);
                    rectangle.setFill(Color.GREY);
                    rectangle.setStroke(Color.BLACK);
                    pane.getChildren().add(rectangle);
                    grid[i][j] = 0;
                }
                points += 8;
                text.setText("Points: " + points*10);
                printGrid(grid);
            }
        }
    }
    private void breakCollumns(int[] full){
        for(int i = 0;i < 8;i++){
            if(full[i] == 1){
                for (int j = 0; j < 8; j++) {
                    Rectangle rectangle = new Rectangle(10 + i * 50, 10 + j * 50, 50, 50);
                    rectangle.setFill(Color.GREY);
                    rectangle.setStroke(Color.BLACK);
                    pane.getChildren().add(rectangle);
                    grid[j][i] = 0;
                }
                points += 8;
                text.setText("Points: " + points*10);
                printGrid(grid);
            }
        }

    }
    private int[][] rotateShape(int[][] shape, int angle){
        int[][] rotated;
        int n = shape.length;
        int m = shape[0].length;

        switch (angle) {
            case 90:
                rotated = new int[m][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        rotated[j][n - 1 - i] = shape[i][j];
                    }
                }
                break;

            case 180:
                rotated = new int[n][m];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        rotated[n - 1 - i][m - 1 - j] = shape[i][j];
                    }
                }
                break;

            case 270:
                rotated = new int[m][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        rotated[m - 1 - j][i] = shape[i][j];
                    }
                }
                break;

            default: // 0° rotation
                rotated = shape;
                break;
        }
        return rotated;
    }
    private void drawShape(Pane pane, int[][] shape, int startX, int startY, Color color) {
        int tileSize = 50; // rectangle size

        for (int j = 0; j < shape.length; j++) { //rows
            for (int i = 0; i < shape[j].length; i++) { //columns
                if (shape[j][i] == 1) {
                    // Calculate the position based on indices
                    int x = startX + i * tileSize;
                    int y = startY + j * tileSize;


                    Rectangle rectangle = new Rectangle(x, y, tileSize, tileSize);
                    rectangle.setFill(color);
                    rectangle.setStroke(Color.BLACK);


                    pane.getChildren().add(rectangle);
                }
            }
        }
    }

    private boolean isPlacementValid(int[][] grid, int[][] shape, int startX, int startY) {
        int gridX = (startX - 10) / 50;
        int gridY = (startY - 10) / 50;

        for (int j = 0; j < shape.length; j++) {
            for (int i = 0; i < shape[j].length; i++) {
                if (shape[j][i] == 1) {
                    int x = gridX + i;
                    int y = gridY + j;

                    //Check grid borders
                    if (x < 0 || x >= 8 || y < 0 || y >= 8) {
                        return false;
                    }

                    // Check if the cell is already occupied
                    if (grid[y][x] == 1) {
                        return false;
                    }
                }
            }
        }
        //System.out.println("placement is valid");
        return true;
    }

    private void updateGrid(int[][] grid, int[][] shape, int startX, int startY) {
        int gridX = (startX - 10) / 50;
        int gridY = (startY - 10) / 50;

        for (int j = 0; j < shape.length; j++) {
            for (int i = 0; i < shape[j].length; i++) {
                if (shape[j][i] == 1) {
                    int x = gridX + i;
                    int y = gridY + j;

                    grid[y][x] = 1; // Mark as occupied
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void printGrid(int[][] grid){
        System.out.println("-------");
        for(int i = 0; i<8;i++){
            for(int j = 0; j<8; j++){
                System.out.print(grid[i][j]+"  ");
            }
            System.out.println();
        }
        System.out.println("-------");
    }
    



}
class BlockShape {
    public int[][] shape;
    public Color color;

    public BlockShape(int[][] shape) {
        this.shape = shape;
    }

    public BlockShape(BlockShape other) {
        this.shape = new int[other.shape.length][];
        for (int i = 0; i < other.shape.length; i++) {
            this.shape[i] = Arrays.copyOf(other.shape[i], other.shape[i].length);
        }
        this.color = other.color;
    }
}
