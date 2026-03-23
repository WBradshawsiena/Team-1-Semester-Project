//
//
//USE W A S D TO MOVE Player 1
//USE ARROWS TO MOVE Player 2
//
//
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
//import java.text.CollationElementIterator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.util.Random;
import java.awt.event.ActionListener;
//import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
public class Platformer implements Runnable, KeyListener
{
    private static JPanel panel1;
    private static JPanel panel2;
    //Index:
    //0 = empty, 1 = walls, 2 = playerStart;
    //objects[y][x]
    private static int[][] layout = {
    {1,1,1,1,1,1,1,1,1,1},
    {1,0,0,0,0,0,0,0,0,1},
    {1,0,0,0,0,0,0,0,0,1},
    {1,2,0,0,0,0,0,0,0,1},
    {1,1,1,1,1,1,1,1,1,1},
    };



    private static GameObject[][] objects;
    private static int playerSpeed = 2;
    private static int playerMaxSpeed = 10;
    private static int FPS = 60;
    private static int scale = 500;
    private static int barSize = 28;
    private static int traction = 1;
    private static int frame1xOffset = 0;
    private static int frame1yOffset = 0;
    private static int frame2xOffset = 0;
    private static int frame2yOffset = 0;
    private static JFrame frame1;
    private static JFrame frame2;
    private static boolean w = false;
    private static boolean a = false;
    private static boolean s = false;
    private static boolean d = false;
    private static boolean up = false;
    private static boolean left = false;
    private static boolean down = false;
    private static boolean right = false;
    public static GameObject player1;
    public static GameObject player2;
    //private static JButton b;
    @Override
    public void run()
    {
        if(System.getProperty("os.name").toLowerCase().contains("mac"))
        {
            barSize = 28;
        }
        if(System.getProperty("os.name").toLowerCase().contains("win"))
        {
            //I need to test this on windows
            barSize = 28;
        }
        player1 = new GameObject("Player 1",0,0,100,100, Color.GREEN);
        player2 = new GameObject("Player 2",0,0,100,100, Color.RED);
        setLayout();
        frame1 = new JFrame("Player 1");
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setPreferredSize(new Dimension(scale,scale + barSize));
        frame1.setResizable(false);
        frame2 = new JFrame("Player 2");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setPreferredSize(new Dimension(scale,scale + barSize));
        frame2.setResizable(false);
        //b = new JButton("Test");
        //b.addActionListener(this);
        panel1 = new JPanel()
        {
            @Override
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.setColor(player2.color);
                g.fillRect(player2.x - frame1xOffset, player2.y - frame1yOffset, player2.width, player2.height);
                g.setColor(player1.color);
                g.fillRect(player1.x - frame1xOffset, player1.y - frame1yOffset, player1.width, player1.height);
                for(int x = 0;x < objects[1].length;x++)
                {
                    for(int y = 0;y < objects.length;y++)
                    {
                        if(objects[y][x] != null)
                        {
                            g.setColor(objects[y][x].color);
                            g.fillRect(objects[y][x].x - frame1xOffset, objects[y][x].y - frame1yOffset, objects[y][x].width, objects[y][x].height);
                        }
                    }
                }
            }
        };
        panel2 = new JPanel()
        {
            @Override
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.setColor(player2.color);
                g.fillRect(player1.x - frame2xOffset, player1.y - frame2yOffset, player1.width, player1.height);
                g.setColor(player1.color);
                g.fillRect(player2.x - frame2xOffset, player2.y - frame2yOffset, player2.width, player2.height);
                for(int x = 0;x < objects[1].length;x++)
                {
                    for(int y = 0;y < objects.length;y++)
                    {
                        if(objects[y][x] != null)
                        {
                            g.setColor(objects[y][x].color);
                            g.fillRect(objects[y][x].x - frame2xOffset, objects[y][x].y - frame2yOffset, objects[y][x].width, objects[y][x].height);
                        }
                    }
                }
            }
        };
        frame1.setLocation(0,0);
        frame2.setLocation(1000,0);
        frame1.add(panel1);
        frame1.pack();
        frame1.setVisible(true);
        frame2.add(panel2);
        frame2.pack();
        frame2.setVisible(true);
        frame1.addKeyListener(this);
        frame1.setFocusable(true);
        frame2.addKeyListener(this);
        frame2.setFocusable(true);
    }
    public class GameObject
    {
        public String name;
        public int x;
        public int y;
        public int xSpeed = 0;
        public int ySpeed = 0;
        public int width = 100;
        public int height = 100;
        public Color color = Color.BLACK;
        public GameObject(String name, int x, int y)
        {
            this.name = name;
            this.x = x;
            this.y = y;
        }
        public GameObject(String name, int x, int y, int width, int height)
        {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        public GameObject(String name, int x, int y, int width, int height, Color color)
        {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
        }
    }
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_W)
        {
            w = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_A)
        {
            a = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_S)
        {
            s = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_D)
        {
            d = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            up = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            left = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            down = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            right = true;
        }
    }
    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_W)
        {
            w = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_A)
        {
            a = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S)
        {
            s = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_D)
        {
            d = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            up = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            down = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            right = false;
        }
    }
    @Override
    public void keyTyped(KeyEvent e)
    {
        //System.out.print(e.getKeyChar());
    }
    public void setLayout()
    {
        objects = new GameObject[layout.length][layout[1].length];
        for(int x = 0;x < layout[1].length;x++)
        {
            for(int y = 0;y < layout.length;y++)
            {
                if(layout[y][x] == 0)
                {
                    //Empty
                }
                else if(layout[y][x] == 1)
                {
                    //Wall
                    objects[y][x] = new GameObject("Wall", x * 100, y * 100);
                }
                else if(layout[y][x] == 2)
                {
                    //Spawn players here
                    player1.x = x * 100;
                    player1.y = y * 100;
                    player2.x = x * 100;
                    player2.y = y * 100;
                }
            }
        }
    }
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Platformer());
        Timer clock = new Timer(1000/FPS, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //Player 1 keys
                if(w)
                {
                    player1.ySpeed -= playerSpeed;
                }
                if(a)
                {
                    player1.xSpeed -= playerSpeed;
                }
                if(s)
                {
                    player1.ySpeed += playerSpeed;
                }
                if(d)
                {
                    player1.xSpeed += playerSpeed;
                }
                //Player 2 keys
                if(up)
                {
                    player2.ySpeed -= playerSpeed;
                }
                if(left)
                {
                    player2.xSpeed -= playerSpeed;
                }
                if(down)
                {
                    player2.ySpeed += playerSpeed;
                }
                if(right)
                {
                    player2.xSpeed += playerSpeed;
                }
                //Player 1 traction
                if(player1.xSpeed > 0)
                {
                    player1.xSpeed -= traction;
                }
                if(player1.xSpeed < 0)
                {
                    player1.xSpeed += traction;
                }
                if(player1.ySpeed > 0)
                {
                    player1.ySpeed -= traction;
                }
                if(player1.ySpeed < 0)
                {
                    player1.ySpeed += traction;
                }
                //Player 2 traction
                if(player2.xSpeed > 0)
                {
                    player2.xSpeed -= traction;
                }
                if(player2.xSpeed < 0)
                {
                    player2.xSpeed += traction;
                }
                if(player2.ySpeed > 0)
                {
                    player2.ySpeed -= traction;
                }
                if(player2.ySpeed < 0)
                {
                    player2.ySpeed += traction;
                }
                //Player 1 max speed
                if(player1.xSpeed > playerMaxSpeed)
                {
                    player1.xSpeed = playerMaxSpeed;
                }
                if(player1.xSpeed < -playerMaxSpeed)
                {
                    player1.xSpeed = -playerMaxSpeed;
                }
                if(player1.ySpeed > playerMaxSpeed)
                {
                    player1.ySpeed = playerMaxSpeed;
                }
                if(player1.ySpeed < -playerMaxSpeed)
                {
                    player1.ySpeed = -playerMaxSpeed;
                }
                //Player 2 max speed
                if(player2.xSpeed > playerMaxSpeed)
                {
                    player2.xSpeed = playerMaxSpeed;
                }
                if(player2.xSpeed < -playerMaxSpeed)
                {
                    player2.xSpeed = -playerMaxSpeed;
                }
                if(player2.ySpeed > playerMaxSpeed)
                {
                    player2.ySpeed = playerMaxSpeed;
                }
                if(player2.ySpeed < -playerMaxSpeed)
                {
                    player2.ySpeed = -playerMaxSpeed;
                }
                //Player 1 frame offset
                if(player1.x > 450 + frame1xOffset)
                {
                    frame1xOffset += 500;
                }
                if(player1.x < -50 + frame1xOffset)
                {
                    frame1xOffset -= 500;
                }
                if(player1.y > 450 + frame1yOffset)
                {
                    frame1yOffset += 500;
                }
                if(player1.y < -50 + frame1yOffset)
                {
                    frame1yOffset -= 500;
                }
                //Player 2 frame offset
                if(player2.x > 450 + frame2xOffset)
                {
                    frame2xOffset += 500;
                }
                if(player2.x < -50 + frame2xOffset)
                {
                    frame2xOffset -= 500;
                }
                if(player2.y > 450 + frame2yOffset)
                {
                    frame2yOffset += 500;
                }
                if(player2.y < -50 + frame2yOffset)
                {
                    frame2yOffset -= 500;
                }
                //Update player posistion 
                player1.x += player1.xSpeed;
                player1.y += player1.ySpeed;
                player2.x += player2.xSpeed;
                player2.y += player2.ySpeed;
                //Reset frame
                frame1.getContentPane().revalidate();
                frame1.getContentPane().repaint();
                frame2.getContentPane().revalidate();
                frame2.getContentPane().repaint();
            }
        });
        clock.start();
    }
}