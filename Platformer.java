//
//
//USE W A S D TO MOVE GREEN SQUARE
//
//
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
    private static Color c = Color.GREEN;
    private static int playerSpeed = 2;
    private static JFrame frame1;
    private static JFrame frame2;
    private static boolean w = false;
    private static boolean a = false;
    private static boolean s = false;
    private static boolean d = false;
    public static GameObject player1;
    //private static JButton b;
    @Override
    public void run()
    {
        player1 = new GameObject("Player 1",0,0,100,100);
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
                g.setColor(c);
                g.fillRect(player1.x,player1.y,player1.width,player1.height);
            }
        };
        panel2 = new JPanel()
        {
            @Override
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.setColor(c);
                g.fillRect(player1.x,player1.y,player1.width,player1.height);
            }
        };
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
        public GameObject(String name, int x, int y, int width, int height)
        {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
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
                frame1.getContentPane().revalidate();
                frame1.getContentPane().repaint();
                frame2.getContentPane().revalidate();
                frame2.getContentPane().repaint();
            }
        });
        clock.start();
    }
}