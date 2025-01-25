
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static java.awt.event.KeyEvent.*;
public class RenderingEngine{
    public static JPanel renderPanel;
    private static boolean isKeyPressed = false;
    static double xRot = 0.0;
    static double yRot = 0.0;

    static ArrayList<Triangle> cube;

    public static void main(String[] args){
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());
        Vertex v1 = new Vertex(100, 100, 100);
        Vertex v2 = new Vertex(100, -100, 100);
        Vertex v3 = new Vertex(-100, -100, 100);
        Vertex v4 = new Vertex(-100, 100, 100);
        Vertex v5 = new Vertex(100, 100, -100);
        Vertex v6 = new Vertex(100, -100, -100);
        Vertex v7 = new Vertex(-100, -100, -100);
        Vertex v8 = new Vertex(-100, 100, -100);

        // Create cube faces (each face is two triangles) + fill with different colors to visualize two
        //triangles
        cube = new ArrayList<>();
        cube.add(new Triangle(v1, v2, v3, Color.RED));
        cube.add(new Triangle(v1, v3, v4, new Color(255, 71, 76)));
        cube.add(new Triangle(v5, v6, v7, Color.GREEN));
        cube.add(new Triangle(v5, v7, v8, new Color(34,139,34)));
        cube.add(new Triangle(v4, v3, v7, Color.DARK_GRAY));
        cube.add(new Triangle(v4, v7, v8, Color.CYAN));
        cube.add(new Triangle(v1, v2, v6, Color.YELLOW));
        cube.add(new Triangle(v1, v6, v5, Color.ORANGE));
        cube.add(new Triangle(v1, v4, v8, Color.LIGHT_GRAY));
        cube.add(new Triangle(v1, v8, v5, Color.BLACK));
        cube.add(new Triangle(v2, v3, v7, Color.PINK));
        cube.add(new Triangle(v2, v7, v6, Color.MAGENTA));
        // panel to display render results
        BufferedImage img;
        renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                double heading = Math.toRadians(xRot);   //convert angle to radians
                Matrix headingTransform = new Matrix(new double[]{   //create matrix for rotation across x axis
                        Math.cos(heading), 0, -Math.sin(heading),
                        0, 1, 0,
                        Math.sin(heading), 0, Math.cos(heading)
                });
                double pitch = Math.toRadians(yRot);
                Matrix pitchTransform = new Matrix(new double[]{   //create matrix for rotation across y axis
                        1, 0, 0,
                        0, Math.cos(pitch), Math.sin(pitch),
                        0, -Math.sin(pitch), Math.cos(pitch)
                });
                Matrix transformation = headingTransform.multiply(pitchTransform);  //multiply to form matrix that will rotate points across x and y axis based on how it is dragged
                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                g2.setColor(Color.BLUE);
                g2.fillRect(0, 0, getWidth(), getHeight());      //create background
                g2.translate(getWidth() / 2, getHeight() / 2);//set background blue
                double[] zBuffer = new double[img.getWidth() * img.getHeight()];
// initialize array with extremely far away depths
                for (int q = 0; q < zBuffer.length; q++) {
                    zBuffer[q] = Double.NEGATIVE_INFINITY;
                }

                for (Triangle t : cube) {
                    Vertex v1 = transformation.transform(t.v1);   //rotate based on angle
                    Vertex v2 = transformation.transform(t.v2);
                    Vertex v3 = transformation.transform(t.v3);
                    //adjust positions so that it is not out of range of imagebuffer
                    double incWid = getWidth()/2.6;
                    double incHei = getHeight()/2.6;
                    v1.x += incWid;
                    v2.x += incWid;
                    v3.x += incWid;
                    v1.y += incHei;
                    v2.y += incHei;
                    v3.y += incHei;

                    //calculate maximum and minimum x and y value triangle may reach when rotated
                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxX = (int) Math.min(img.getWidth() - 1,
                            Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                    int maxY = (int) Math.min(img.getHeight() - 1,
                            Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));
                    //implement z buffer algorithm
                    for(int y = minY; y <= maxY; y++){
                        for(int x = minX; x <= maxX; x++){
                            Vertex pt = new Vertex(x,y,0);
                            boolean vertex1 = Triangle.sameSide(v1,v2,v3,pt);
                            boolean vertex2 = Triangle.sameSide(v2,v3,v1,pt);
                            boolean vertex3 = Triangle.sameSide(v3,v1,v2,pt);

                            if (vertex1 && vertex2 && vertex3) {
                                double depth = v1.z + v2.z + v3.z;
                                int zIndex = y * img.getWidth() + x;
                                if (zBuffer[zIndex] < depth) {
                                    img.setRGB(x, y, t.getColor().getRGB());
                                    zBuffer[zIndex] = depth;
                                }
                            }
                        }
                    }
                }
                //draw image
                g2.drawImage(img,-300,-300, this);
                if(isKeyPressed)
                    renderPanel.repaint();
            }
        };
        //add renderPanel
        pane.add(renderPanel, BorderLayout.CENTER);
        frame.setSize(800, 800);
        frame.setVisible(true);
        //for keylisteners
        renderPanel.setFocusable(true);
        renderPanel.addMouseMotionListener(new MouseMotionListener() {
            //allow rotation of shape by mouse
            @Override
            public void mouseDragged(MouseEvent e) {
                double yi = 180.0 / renderPanel.getHeight();
                double xi = 180.0 / renderPanel.getWidth();
                xRot = (int) (e.getX() * xi);   //calculate angle of rotation across x axis
                yRot = -(int) (e.getY() * yi);  //calculate angle of rotation across y asix
                renderPanel.repaint(); //repaint after angle has changed
            }
            @Override
            public void mouseMoved(MouseEvent e) {}
        });
    }
}

// x indicates the movement in the left and right directions
// y indicates the up-and-down movement on the screen
// z indicates depth (so the z axis is perpendicular to your screen). Positive z means "towards the observer".
class Vertex {
    double x;
    double y;
    double z;

    //constructor
    Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
class Triangle {
    Vertex v1;
    Vertex v2;
    Vertex v3;
    Color color;
    //constructor initializes vertex points on triangle
    Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }

    //using cross product formula to see if vector is on the same side as point returns true if yes else no
    public static boolean sameSide(Vertex a, Vertex b, Vertex c, Vertex d){
        Vertex first = new Vertex(b.x-a.x, b.y - a.y, b.z-a.z);
        Vertex second = new Vertex(c.x-a.x, c.y - a.y, c.z-a.z);
        Vertex third = new Vertex(d.x-a.x, d.y - a.y, d.z-a.z);
        return (first.x*second.y - second.x*first.y) * (first.x*third.y - third.x*first.y) >= 0;
    }

    //get color of side of shape
    public Color getColor(){
        return color;
    }
}
class Matrix{
    double[] values;

    //store values of 3d points and matrix rotation.
    Matrix(double[] values) {
        this.values = values;
    }

    //function to multiply matrices
    Matrix multiply(Matrix other) {
        double[] result = new double[9];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                for (int i = 0; i < 3; i++) {
                    result[row * 3 + col] +=
                            this.values[row * 3 + i] * other.values[i * 3 + col];
                }
            }
        }
        return new Matrix(result);
    }

    //transform vertex based on matrix multiplication
    Vertex transform(Vertex in) {
        return new Vertex(
                in.x * values[0] + in.y * values[3] + in.z * values[6],
                in.x * values[1] + in.y * values[4] + in.z * values[7],
                in.x * values[2] + in.y * values[5] + in.z * values[8]
        );
    }
}
