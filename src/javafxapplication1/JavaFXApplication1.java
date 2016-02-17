package javafxapplication1;

import javafx.scene.input.KeyEvent;
import java.util.Random;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;

public class JavaFXApplication1 extends Application {

    public static void main(String[] args) {
        launch(args); // по сути, запуск метода start
    }

    @Override
    public void start(Stage primaryStage) {
        options(primaryStage); // первоначальные настройки приложения
        generateMaze(); // создание лабиринта
        showMaze(); // показ лабиринта
        gameProcess(); // начало игрового процесса (управление стрелками и тд.) 
    }

    //[][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][]
    //][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][
    //[][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][]
    /// ПЕРЕМЕННЫЕ КЛАССА, которые будут доступны во всех методах:
    int height = 40; // высота лабиринта (количество строк)
    int width = 40; // ширина лабиринта (количество столбцов в каждой строке)

    enum GameObject {

        HALL, WALL, CHAR, CASH, ENEMY // BOMB, ENEMY2, BOSS ...
    };

    GameObject[][] maze = new GameObject[height][width];

    ImageView[][] images = new ImageView[height][width]; // массив ссылок
    // на элементы управления, на которых будут размещены картинки

    // пути к картинкам
    Image hall = new Image("/img/hall.png");
    Image wall = new Image("/img/wall.png");
    Image character = new Image("/img/char.png");
    Image cash = new Image("/img/cash2.png");
    Image enemy = new Image("/img/enemy.png");

    GridPane layout; // менеджер компоновки. по сути, это панель, на которую
    // определённым образом выкладываются различные элементы управления

    Stage stage; // ссылка на окно приложения
    Scene scene; // ссылка на клиентскую область окна

    Random r = new Random();

    int smileX = 0;
    int smileY = 2; // стартовая позиция игрового персонажика
    int smileXf = height;
    int smileYf = width-2;// финиш игрового персонажа

    public void options(Stage primaryStage) {
        ////////////////////////////////////////////////////////////////////////
        /// настройки окна
        stage = primaryStage;
        stage.setTitle("Java FX Maze"); // установка текста в заголовке окна !тут выводим ответы по бонусам
        stage.setResizable(false); // размеры окна нельзя будет изменить
        stage.getIcons().add(character); // иконка приложения
        ////////////////////////////////////////////////////////////////////////
        /// настройки панели элементов
        layout = new GridPane(); // элементы будут выкладываться в виде сетки
        layout.setPadding(new Insets(5, 5, 5, 5)); // отступы панели от клиентской части окна
        layout.setStyle("-fx-background-color: rgb(92, 118, 137);"); // фон панели
        //layout.setGridLinesVisible(true); // сделать видимыми границы сетки
        /// жуткая процедура установки количества строк и столбцов панели:
        for (int i = 0; i < height; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / height);
            layout.getRowConstraints().add(rowConst);
        }
        for (int i = 0; i < width; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / width);
            layout.getColumnConstraints().add(colConst);
        }
        ////////////////////////////////////////////////////////////////////////
        /// настройка клиентской области окна: элементы кладём на панель, панель -
        // на клиентскую область, клиентскую область - привязываем к окну
        scene = new Scene(layout, 16 * width, 16 * height); // 16 px - размер
        // одной ячейки лабиринта по ширине и по высоте
        stage.setScene(scene);

        ////////////////////////////////////////////////////////////////////////
        // здесь (возможно) будут другие общие настройки
    }

    public void generateMaze() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                maze[y][x] = GameObject.HALL; // изначально, лабиринт пустой

                // в 1 случае из 5 - ставим стену
                if (r.nextInt(5) == 0) {
                    maze[y][x] = GameObject.WALL;
                }

                // в 1 случае из 250 - кладём денежку
                if (r.nextInt(250) == 0) {
                    maze[y][x] = GameObject.CASH;
                }

                // в 1 случае из 250 - размещаем врага
                if (r.nextInt(250) == 0) {
                    maze[y][x] = GameObject.ENEMY;
                }

                // стены по периметру обязательны
                if (y == 0 || x == 0 || y == height - 1 | x == width - 1) {
                    maze[y][x] = GameObject.WALL;
                }

                // наш персонажик
                if (x == smileX && y == smileY) {
                    maze[y][x] = GameObject.CHAR;
                }

                // есть выход, и соседняя ячейка справа всегда свободна
                if (x == 1 && y == 2 || x == width - 1 && y == height - 3) {
                    maze[y][x] = GameObject.HALL;
                }

            }
        }

    }

    public void showMaze() {

        Image current;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (maze[y][x] == GameObject.HALL) {
                    current = hall;
                } else if (maze[y][x] == GameObject.WALL) {
                    current = wall;
                } else if (maze[y][x] == GameObject.CHAR) {
                    current = character;
                } else if (maze[y][x] == GameObject.CASH) {
                    current = cash;
                } else/* if (maze[y][x] == GameObject.ENEMY)*/ {
                    current = enemy;
                }

                images[y][x] = new ImageView(current);
                layout.add(images[y][x], x, y);
                //GridPane.setHalignment(imgView, HPos.CENTER);

            }
        }

        stage.show();
    }

    public void clearCell(int x, int y) {
        maze[y][x] = GameObject.HALL; // делаем пустую ячейку по указанной позиции
        layout.getChildren().remove(images[y][x]);
        images[y][x] = new ImageView(hall);
        layout.add(images[y][x], x, y);
    }

    public void setSmile(int x, int y) {
        maze[y][x] = GameObject.CHAR;
        layout.getChildren().remove(images[y][x]);
        images[y][x] = new ImageView(character);
        layout.add(images[y][x], x, y);
    }

    public void gameProcess() {
        scene.setOnKeyPressed((KeyEvent t) -> {

            clearCell(smileX, smileY);

            if (t.getCode() == KeyCode.RIGHT && maze[smileY][smileX + 1] != GameObject.WALL) {
                smileX++;
                
                if (t.getCode() == KeyCode.RIGHT && smileX == width - 1 && smileY == height - 3){
                        Alert alert = new Alert(AlertType.INFORMATION);
alert.setTitle("Information Dialog");
alert.setHeaderText("Look, an Information Dialog");
alert.setContentText("YOU ARE WIN!!!");

alert.showAndWait();
System.exit(0);
                    }

            } else if (t.getCode() == KeyCode.LEFT && smileX > 0 && maze[smileY][smileX - 1] != GameObject.WALL) {
                smileX--;
            } else if (t.getCode() == KeyCode.UP && maze[smileY-1][smileX] != GameObject.WALL) {
                smileY--;
            } else if (t.getCode() == KeyCode.DOWN && smileX > 0 && maze[smileY+1][smileX] != GameObject.WALL) {
                smileY++;
            }
            
            
                    

            setSmile(smileX, smileY);

        });
    }
}
