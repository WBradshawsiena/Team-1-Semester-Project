//
//
//USE W A S D TO MOVE GREEN SQUARE
//USE ARROWS TO MOVE RED SQUARE
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
    //private static Color c = Color.GREEN;
    private static int playerSpeed = 2;
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
        player1 = new GameObject("Player 1",0,0,100,100, Color.GREEN);
        player2 = new GameObject("Player 2",0,0,100,100, Color.RED);
        frame1 = new JFrame("Player 1");
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setPreferredSize(new Dimension(500,500));
        frame1.setResizable(false);
        frame2 = new JFrame("Player 2");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setPreferredSize(new Dimension(500,500));
        frame2.setResizable(false);
        //b = new JButton("Test");
        //b.addActionListener(this);
        panel1 = new JPanel()
        {
            @Override
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.setColor(player1.color);
                g.fillRect(player1.x,player1.y,player1.width,player1.height);
                g.setColor(player2.color);
                g.fillRect(player2.x,player2.y,player2.width,player2.height);
            }
        };
        panel2 = new JPanel()
        {
            @Override
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.setColor(player1.color);
                g.fillRect(player1.x,player1.y,player1.width,player1.height);
                g.setColor(player2.color);
                g.fillRect(player2.x,player2.y,player2.width,player2.height);
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
        public int width;
        public int height;
        public Color color = Color.BLACK;
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
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Platformer());
        Timer clock = new Timer(1000/60, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(w)
                {
                    player1.y -= playerSpeed;
                }
                if(a)
                {
                    player1.x -= playerSpeed;
                }
                if(s)
                {
                    player1.y += playerSpeed;
                }
                if(d)
                {
                    player1.x += playerSpeed;
                }
                if(up)
                {
                    player2.y -= playerSpeed;
                }
                if(left)
                {
                    player2.x -= playerSpeed;
                }
                if(down)
                {
                    player2.y += playerSpeed;
                }
                if(right)
                {
                    player2.x += playerSpeed;
                }
                frame1.getContentPane().revalidate();
                frame1.getContentPane().repaint();
                frame2.getContentPane().revalidate();
                frame2.getContentPane().repaint();
            }
        });
        clock.start();
    }
}