public class Paint {
    public static void main(String[] args) {
        new PaintController();
    }
}

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The Controller handles user input
 * When the user clicks on a button, on the canvas or drags his mouse, the Controller
 * will change the model
 * When the Controller has changed the model, it will notify the PaintView that the model
 * was changed and tell it to redraw itself
 * The Controller needs a reference to both the PaintModel and the PaintView
 */
public class PaintController extends JFrame implements ActionListener {

    private PaintModel paintModel;
    private PaintView paintView;

    public PaintController(){
        paintModel = new PaintModel();
        paintView = new PaintView(paintModel);

        //Adding listeners to all of the buttons we created in PaintView

        paintView.black.addActionListener(this);
        paintView.red.addActionListener(this);
        paintView.green.addActionListener(this);
        paintView.blue.addActionListener(this);

        paintView.dot.addActionListener(this);
        paintView.oval.addActionListener(this);
        paintView.rect.addActionListener(this);

        paintView.save.addActionListener(this);
        paintView.undo.addActionListener(this);
        paintView.load.addActionListener(this);
        paintView.reset.addActionListener(this);

        //Make the paintView listen for mouse events
        //This will be necessary in order to draw Shapes on the PaintView

        paintView.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                String color = paintModel.getColor();
                String form = paintModel.getShape();
                int x = e.getX();
                int y = e.getY();
                Shape shape = new Shape(color, form, x, y);
                paintModel.addShape(shape);
                paintView.repaint();
            }
        });

        paintView.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                //This function gets called everytime the mouse cursor is moved
                //we will need to update the xMoved and yMoved variable everytime
                //this happens
                int x = e.getX();
                int y = e.getY();
                paintModel.mouseMoved(x,y);
                paintView.repaint();
            }
        });


        //JFRAME METHODS - necessary to display the window
        setVisible(true);
        add(paintView);
        pack();
        setLocationRelativeTo(null);
        //Will terminate the program when the window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case "Black":
                paintModel.setColor("black");
                System.out.println(paintModel.getColor());
                break;
            case "Red":
                paintModel.setColor("red");
                System.out.println(paintModel.getColor());
                break;
            case "Green":
                paintModel.setColor("green");
                System.out.println(paintModel.getColor());
                break;
            case "Blue":
                paintModel.setColor("blue");
                System.out.println(paintModel.getColor());
                break;
            case "Dot":
                paintModel.setShape("dot");
                System.out.println(paintModel.getShape());
                break;
            case "Oval":
                paintModel.setShape("oval");
                System.out.println(paintModel.getShape());
                break;
            case "Rectangle":
                paintModel.setShape("rectangle");
                System.out.println(paintModel.getShape());
                break;
            case "Save":
                try {
                    paintModel.save();
                    System.out.println("Save was successful");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "Undo":
                paintModel.undo();
                paintView.repaint();
                System.out.println("Undid successfully");
                break;
            case "Load":
                try {
                    paintModel.load();
                    paintView.repaint();
                    System.out.println("Load was successful");
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            case "Reset":
                paintModel.setShapes(new ArrayList<>());
                paintView.repaint();
        }
        paintView.setMode();
    }

}

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Stores all the data of our application, such as the current color and shape
 * Stores an ArrayList of all the Shapes that have been drawn
 * Contains methods for saving to a file/loading from a file and undoing
 * Independent of View and Controller.
 */
public class PaintModel {

    private String color;
    private String shape;
    private ArrayList<Shape> shapes;

    public PaintModel(){
        //Initialize to default values
        this.color = "black";
        this.shape = "dot";
        shapes = new ArrayList<>();
    }

    //GETTER METHODS

    public String getColor() {
        return color;
    }

    public String getShape() {
        return shape;
    }

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    //SETTER METHODS

    public void setColor(String color) {
        this.color = color;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public void setShapes(ArrayList<Shape> shapes) {
        this.shapes = shapes;
    }

    //LOGIC METHODS - undo/save/load
    public void undo(){
        if (!shapes.isEmpty()){
            shapes.remove(shapes.size()-1);
        }
    }

    public void save() throws IOException {
        JFileChooser jFileChooser = new JFileChooser();

        if (jFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
            File file = jFileChooser.getSelectedFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(shapes);
            objectOutputStream.flush();
            objectOutputStream.close();
        }
    }

    public void load() throws IOException, ClassNotFoundException {
        JFileChooser jFileChooser = new JFileChooser();

        if (jFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
            File file = jFileChooser.getSelectedFile();
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            shapes = (ArrayList<Shape>) objectInputStream.readObject();
            objectInputStream.close();
        }

    }

    public void addShape(Shape shape) {
        shapes.add(shape);
    }


    public void mouseMoved(int x, int y) {
        if (!shapes.isEmpty()){
            Shape shape = shapes.get(shapes.size()-1);
            shape.mouseMoved(x,y);
        }
    }
}

import javax.swing.*;
import java.awt.*;

/**
 * Holds all graphical components of our app, such as buttons, labels and JPanels
 * Holds a reference to the PaintModel in order to draw/display it
 */
public class PaintView extends JPanel {

    private PaintModel paintModel;
    private JPanel topPanel;
    private JPanel paintPanel;

    //Buttons for selecting color
    JButton black;
    JButton red;
    JButton green;
    JButton blue;

    //Buttons for selecting shape
    JButton dot;
    JButton oval;
    JButton rect;

    //Buttons for saving drawing to a file/loading drawing from a file and to undo
    JButton save;
    JButton undo;
    JButton load;
    JButton reset;

    //Adding the text that displays the current color and shape
    JLabel mode;

    //CONSTRUCTOR
    public PaintView(PaintModel paintModel){
        this.paintModel = paintModel;
        topPanel = new JPanel(new GridLayout(1,11));
        paintPanel = new JPanel(new BorderLayout());

        //Initializing buttons

        black = new JButton("Black");
        red = new JButton("Red");
        green = new JButton("Green");
        blue = new JButton("Blue");

        dot = new JButton("Dot");
        oval = new JButton("Oval");
        rect = new JButton("Rectangle");

        save = new JButton("Save");
        undo = new JButton("Undo");
        load = new JButton("Load");
        reset = new JButton("Reset");

        mode = new JLabel("The current color is "+paintModel.getColor()+" "+
                "and the current shape is "+paintModel.getShape());
        mode.setFont(new Font("Default",Font.BOLD,16));

        //Adding the JButtons to the topPanel

        topPanel.add(black);
        topPanel.add(red);
        topPanel.add(green);
        topPanel.add(blue);

        topPanel.add(dot);
        topPanel.add(oval);
        topPanel.add(rect);

        topPanel.add(save);
        topPanel.add(undo);
        topPanel.add(load);
        topPanel.add(reset);

        paintPanel.add(mode,BorderLayout.SOUTH);


        //Adding the topPanel and paintPanel to the main JPanel - the PaintView
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(750,450));
        this.add(topPanel,BorderLayout.NORTH);
        this.add(paintPanel,BorderLayout.SOUTH);

    }

    public void drawDot(Graphics g, Shape shape){
        g.fillOval(shape.getX()-5, shape.getY()-5, 10,10);
    }

    public void drawOval(Graphics g, Shape shape){
        g.fillOval(shape.getNewX(),shape.getNewY(),shape.getWidth(),shape.getHeight());
    }

    public void drawRect(Graphics g, Shape shape){
        g.fillRect(shape.getNewX(),shape.getNewY(),shape.getWidth(),shape.getHeight());
    }

    public void drawShape(Graphics g, Shape shape){
        String color = shape.getColor();
        changeColor(g,color);
        String form = shape.getShape();
        if (form.equals("dot")) {
            drawDot(g,shape);
        } else if (form.equals("oval")){
            drawOval(g,shape);
        } else {
            drawRect(g,shape);
        }
    }

    public void changeColor(Graphics g, String color){
        switch (color){
            case "black":
                g.setColor(Color.black);
                break;
            case "red":
                g.setColor(Color.red);
                break;
            case "green":
                g.setColor(Color.green);
                break;
            case "blue":
                g.setColor(Color.blue);
                break;
        }
    }

    //The function needed to draw on the paintPanel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Shape shape : paintModel.getShapes()){
            drawShape(g,shape);
        }

    }

    public void setMode(){
        mode.setText("The current color is "+paintModel.getColor()+" "+
                "and the current shape is "+paintModel.getShape());
    }

    //Time to test our view class
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.add(new PaintView(new PaintModel()));
        window.setVisible(true);
    }


}

import java.io.Serializable;

/**
 * A class that represents a Shape
 * A Shape is a drawable object with a color, ex:"green", and a shape, ex: "oval"
 * A Shape always has an x and y coordinate
 */
public class Shape implements Serializable{

    private String color;
    private String shape;

    //The x coordinate of the Shape - where the shape starts in the x direction
    private int x;
    //The y coordinate of the Shape - where the shape starts in the y direction
    private int y;

    //When we are drawing ovals and rectangles, we need to keep track of where the
    //mouse cursor is moving
    private int xMoved;
    private int yMoved;

    //CONSTRUCTOR
    public Shape(String color, String shape, int x, int y){
        this.color = color;
        this.shape = shape;
        this.x = x;
        this.y = y;
        this.xMoved = x;
        this.yMoved = y;
    }

    //GETTER methods
    public String getColor() {
        return color;
    }

    public String getShape() {
        return shape;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getNewX(){
        return Math.min(x,xMoved);
    }

    public int getNewY(){
        return Math.min(y,yMoved);
    }

    //When we draw ovals and rectangles, we want to know their width and height
    public int getWidth(){
        return Math.abs(x-xMoved);
    }

    public int getHeight(){
        return Math.abs(y-yMoved);
    }


    public void mouseMoved(int x, int y) {
        xMoved = x;
        yMoved = y;
    }
}

