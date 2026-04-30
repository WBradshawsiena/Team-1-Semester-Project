import java.util.Map;
import java.util.HashMap;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

/**
 * Runs a 2D multiplayer platformer engine. This comment should be edited as we
 * make progress to accurately describe the functionality of this class.
 *
 * @author Wyatt Bradshaw, Thomas Hammersma, Ayden McCabe, Andrew Pratt
 * @version 1.1
 */
public class Platformer implements Runnable, KeyListener {

    /**
     * Time until player 1 can fire another icicle, in frames
     */
    private static int icicleCooldown = 3 * 60;

    /**
     * Acceleration for player characters, in pixels/frame.
     */
    private static int playerSpeed = 2;

    /**
     * The maximum height the player can jump.
     */
    private static int playerJump = 21;

    /**
     * The fastest speed the player can run.
     */
    private static int playerMaxSpeed = 10;

    /**
     * The framerate the game runs at.
     */
    private static int FPS = 60;

    /**
     * The size of the window, measured in GameObjects.
     */
    private static int windowSize = 7;

    /**
     * A variable representing a wall in int[][] layout.
     */
    private static int W = 1;

    /**
     * A variable representing where the players spawn in int[][] layout.
     */
    private static int P = 2;

    /**
     * A variable representing an "added" wall in int[][] layout.
     */
    private static int A = 1;

    /**
     * A variable representing a "removed" wall in int[][] layout.
     */
    private static int R = 1 - A;

    /**
     * current animation state of a player
     */
    public enum PlayerState {
        GROUNDED,
        AIRBORNE,
        RUNNING,
        JUMPING,
        FALLING,
        STUNNED
    }

    private static int player1JumpTimer = 0;

    private static int player2JumpTimer = 0;
    /**
     * A 2D array that directly represents the layout of the level.
     *
     * 0 = empty, W = walls, P = playerStart, R = ?
     */
    private static int[][] layout = {
        {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
        {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
        {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
        {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, R, 0, 0, 0, W, W},
        {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, A, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, A, 0, 0, W, W, 0, 0, 0, W, W},
        {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, A, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, 0, 0, 0, A, 0, 0, W, W, 0, 0, 0, W, W},
        {W, 0, 0, 0, 0, 0, W, W, 0, 0, A, 0, 0, W, W, 0, 0, 0, 0, A, 0, A, A, A, A, A, 0, 0, W, W, A, A, A, W, W},
        {W, 0, 0, 0, 0, 0, A, A, A, A, A, A, A, A, A, A, A, A, A, A, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, W, W},
        {W, 0, W, W, W, A, A, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
        {W, 0, 0, A, A, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
        {W, W, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
        {W, W, W, 0, W, 0, W, W, 0, 0, W, 0, 0, 0, W, W, W, 0, 0, 0, W, W, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, W},
        {W, 0, 0, 0, W, 0, W, 0, 0, 0, 0, 0, 0, 0, W, W, W, 0, 0, 0, W, W, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W},
        {W, 0, W, W, W, 0, W, 0, 0, 0, 0, 0, 0, W, W, W, W, A, A, A, W, W, 0, 0, 0, 0, A, W, 0, 0, 0, 0, W, W, W},
        {W, 0, 0, 0, 0, 0, W, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, W, W, 0, 0, 0, 0, 0, W},
        {W, W, W, 0, 0, 0, W, 0, 0, 0, 0, 0, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
        {W, 0, A, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W},
        {W, 0, A, A, A, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
        {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, W},
        {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, W},
        {W, 0, 0, W, A, A, W, W, W, A, A, A, W, W, W, W, A, A, W, W, W, W, A, 0, 0, 0, 0, W, W, A, A, A, A, A, W},
        {W, W, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, A, A, A, A, A, A, A, 0, 0, 0, 0, 0, 0, W},
        {W, 0, 0, A, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
        {W, 0, W, W, 0, 0, W, W, W, 0, 0, 0, 0, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
        {W, 0, 0, W, 0, 0, W, W, W, 0, 0, 0, W, W, W, A, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
        {W, W, 0, W, 0, 0, W, W, W, 0, 0, 0, W, A, A, A, W, 0, 0, 0, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
        {W, 0, 0, W, W, 0, W, W, W, A, A, A, W, A, A, A, W, 0, 0, 0, W, W, A, 0, 0, 0, W, W, W, 0, 0, 0, 0, 0, W},
        {W, 0, W, W, W, 0, W, W, W, W, A, W, W, A, A, A, W, A, A, A, W, W, A, A, A, A, A, W, W, W, W, 0, 0, 0, W},
        {W, 0, 0, 0, 0, 0, W, W, W, W, A, W, 0, 0, 0, 0, W, 0, 0, A, W, W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, W},
        {W, W, W, 0, 0, 0, W, W, W, W, A, W, 0, 0, 0, 0, W, 0, 0, W, W, W, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, W, W},
        {W, W, W, 0, 0, 0, W, W, W, W, A, W, 0, 0, 0, 0, W, 0, 0, W, 0, W, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, W, W},
        {W, W, W, A, A, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, W, 0, W, W, A, 0, 0, 0, W, W, W, A, A, W, W, W},
        {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, A, A, A, A, A, W},
        {W, P, 0, 0, 0, 0, 0, 0, W, 0, W, 0, W, 0, 0, W, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, A, A, A, A, A, A, A, A, W},
        {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W}};

    private static JPanel panel1;
    private static JPanel panel2;

    private static GameObject[][] objects;

    private static int store;
    private static int scale = 100 * windowSize;
    private static int barSize = 28;

    private static int traction = 1;

    private static int frame1xOffset = 0;
    private static int frame1yOffset = 0;
    private static int frame2xOffset = 0;
    private static int frame2yOffset = 0;
    private static int icicleTimer = 0;
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

    private static boolean player1CollisionX = false;
    private static boolean player1CollisionY = false;
    private static boolean player1CollisionXY = false;
    private static boolean player2CollisionX = false;
    private static boolean player2CollisionY = false;
    private static boolean player2CollisionXY = false;

    public static GameObject player1;
    public static GameObject icicle;
    public static Map<String, String> p1Sprites = Map.of(
        "idle", "Platformer/ArtAssets/iceGuyIdle.gif",
        "jump", "Platformer/ArtAssets/iceGuyJump.gif",
        "run", "Platformer/ArtAssets/iceGuyRun.gif",
        "air", "Platformer/ArtAssets/iceGuyAir.gif",
        "fall", "Platformer/ArtAssets/iceGuyFall.gif",
        "burned", "Platformer/ArtAssets/iceGuyBurn.gif"
    );

    public static GameObject player2;
    public static Map<String, String> p2Sprites = Map.of(
        "idle", "Platformer/ArtAssets/fireGuyIdle.gif",
        "jump", "Platformer/ArtAssets/fireGuyJump.gif",
        "run", "Platformer/ArtAssets/fireGuyRun.gif",
        "air", "Platformer/ArtAssets/fireGuyAir.gif",
        "fall", "Platformer/ArtAssets/fireGuyFall.gif",
        "frozen", "Platformer/ArtAssets/fireGuyFreeze.gif"
    );

    //private static JButton b;
    @Override
    public void run() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            barSize = 28;
        }
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            //I need to test this on windows --> tested it, it works :D
            barSize = 27;
        }

        // This is the old constructor call for player 1, which just makes him a green square.
        //player1 = new GameObject("Player 1",0,0,100,100, Color.GREEN);

        //Constructor calls for player characters
        player1 = new GameObject("Player 1", 0, 0, 100, 100, p1Sprites);
        icicle = new GameObject("Icicle", -100, -100, 100,20,Color.CYAN);
        player2 = new GameObject("Player 2", 0, 0, 100, 100, p2Sprites);

        setLayout();

        frame1 = new JFrame("Player 1");
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setPreferredSize(new Dimension(scale, scale + barSize));
        frame1.setResizable(false);
        frame2 = new JFrame("Player 2");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setPreferredSize(new Dimension(scale, scale + barSize));
        frame2.setResizable(false);
        //b = new JButton("Test");
        //b.addActionListener(this);

        panel1 = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (!player2.facingRight) {
                    g.drawImage(player2.spriteImage, (player2.x + 100) - frame1xOffset, player2.y - frame1yOffset, -player2.width, player2.height, this);
                } else {
                    g.drawImage(player2.spriteImage, (player2.x) - frame1xOffset, player2.y - frame1yOffset, player2.width, player2.height, this);
                }

                g.setColor(icicle.color);
                g.fillRect(icicle.x - frame1xOffset, icicle.y - frame1yOffset, icicle.width, icicle.height);

                if (!player1.facingRight) {
                    g.drawImage(player1.spriteImage, (player1.x + 100) - frame1xOffset, player1.y - frame1yOffset, -player1.width, player1.height, this);
                } else {
                    g.drawImage(player1.spriteImage, (player1.x) - frame1xOffset, player1.y - frame1yOffset, player1.width, player1.height, this);
                }

                for (int x = 0; x < objects[1].length; x++) {
                    for (int y = 0; y < objects.length; y++) {
                        if (objects[y][x] != null) {
                            g.setColor(objects[y][x].color);
                            g.fillRect(objects[y][x].x - frame1xOffset, objects[y][x].y - frame1yOffset, objects[y][x].width, objects[y][x].height);
                        }
                    }
                }
            }
        };
        panel2 = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                //g.setColor(player1.color);
                //g.fillRect(player1.x - frame2xOffset, player1.y - frame2yOffset, player1.width, player1.height);
                if (!player1.facingRight) {
                    g.drawImage(player1.spriteImage, (player1.x + 100) - frame2xOffset, player1.y - frame2yOffset, -player1.width, player1.height, this);
                } else {
                    g.drawImage(player1.spriteImage, (player1.x) - frame2xOffset, player1.y - frame2yOffset, player1.width, player1.height, this);
                }

                if (!player2.facingRight) {
                    g.drawImage(player2.spriteImage, (player2.x + 100) - frame2xOffset, player2.y - frame2yOffset, -player2.width, player2.height, this);
                } else {
                    g.drawImage(player2.spriteImage, (player2.x) - frame2xOffset, player2.y - frame2yOffset, player2.width, player2.height, this);
                }

                for (int x = 0; x < objects[1].length; x++) {
                    for (int y = 0; y < objects.length; y++) {
                        if (objects[y][x] != null) {
                            g.setColor(objects[y][x].color);
                            g.fillRect(objects[y][x].x - frame2xOffset, objects[y][x].y - frame2yOffset, objects[y][x].width, objects[y][x].height);
                        }
                    }
                }
            }
        };

        frame1.setLocation(0, 0);
        frame2.setLocation(1000, 0);
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

    public static int updatePlayerState(GameObject player, int jumpTimer) {

        boolean isGrounded = (player == player1 ? player1CollisionY : player2CollisionY);
        PlayerState currentState = player.state;
        PlayerState newState;

        if (jumpTimer > 0) {
            newState = PlayerState.JUMPING;
            jumpTimer--;
        } else if (!isGrounded) {
            if (currentState == PlayerState.JUMPING || currentState == PlayerState.AIRBORNE) {
                newState = PlayerState.AIRBORNE;
            } else {
                newState = PlayerState.FALLING;
            }
        } else {
            if (Math.abs(player.xSpeed) > 0) {
                newState = PlayerState.RUNNING;
            } else {
                newState = PlayerState.GROUNDED;
            }
        }

        if (currentState != newState) {
            player.state = newState;
            // System.out.println("Player state changed from " + currentState + "to" + newState);
        }

        return jumpTimer;

    }

    /**
     * Encapsulated class that represents an object in the game that can be
     * interacted with in some way. Contains name, location, scale, speed,
     * color, and sprite data.
     */
    public class GameObject {

        public PlayerState state  = PlayerState.GROUNDED;

        /**
         * The name of the GameObject.
         */
        public String name;

        /**
         * The x-coordinate of the GameObject.
         */
        public int x;

        /**
         * The y-coordinate of the GameObject.
         */
        public int y;

        /**
         * The GameObject's speed in the x-axis.
         */
        public int xSpeed = 0;

        /**
         * The GameObject's speed in the y-axis.
         */
        public int ySpeed = 0;

        /**
         * The width of the GameObject.
         */
        public int width = 100;

        /**
         * The height of the GameObject.
         */
        public int height = 100;

        /**
         * The color of the GameObject (defaults to black).
         */
        public Color color = Color.BLACK;

        /**
         * The sprite of the GameObject.
         */
        public Image spriteImage;

        /**
         * The file of the GameObject's sprite.
         */
        public ImageIcon sprite;

        /**
         * The Map containing filepaths to the sprites of the GameObject (for objects with multiple sprites).
         */
        public Map<String, String> spriteSet;

        /**
         * The file path to the current sprite.
         */
        public String spritePath;

        /**
         * Used to flip the sprite if false.
         */
        public boolean facingRight = true;

        public GameObject(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }

        public GameObject(String name, int x, int y, int width, int height) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public GameObject(String name, int x, int y, int width, int height, Color color) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
        }

        /**
         * Constructs a GameObject with a specified name, coordinates, size, and
         * sprite.
         *
         * @param name the name of the GameObject
         * @param x the GameObject's x-coordinate
         * @param y the GameObject's y-coordinate
         * @param width the width of the GameObject
         * @param height the height of the GameObject
         * @param spriteFileName the file name of the sprite to represent the
         * GameObject.
         */
        public GameObject(String name, int x, int y, int width, int height, String spriteFileName) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.sprite = new ImageIcon(spriteFileName);
            this.spriteImage = sprite.getImage();
        }

        /**
         * Constructs a GameObject with a specified name, coordinates, size, and
         * sprite set.
         *
         * @param name the name of the GameObject
         * @param x the GameObject's x-coordinate
         * @param y the GameObject's y-coordinate
         * @param width the width of the GameObject
         * @param height the height of the GameObject
         * @param sprites the array of ImageIcons that will be assigned to the
         * SpriteSet.
         */
        public GameObject(String name, int x, int y, int width, int height, Map<String, String> sprites) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.spriteSet = sprites;
            this.spriteImage = getSprite("idle");
        }

        /**
         * Fetches the GameObject's sprite in its SpriteSet with the specified key.
         *
         * @param index the index of the sprite to return
         * @return the sprite at the specified index
         */
        public Image getSprite(String key) {
            spriteImage = new ImageIcon(spriteSet.get(key)).getImage();
            return spriteImage;
        }

        /**
         * Sets the GameObject's sprite.
         */
        public void setSprite(String path) {
            if (!path.equals(spritePath)) {
                spritePath = path;
                Image img = Toolkit.getDefaultToolkit().createImage(spriteSet.get(path));
                sprite = new ImageIcon(img);
                spriteImage = sprite.getImage();
                // System.out.println("Created new ImageIcon pointing to " + path);
            }
        }

        /**
         * Gets the GameObject's sprite image. 
         */
        public Image getImage() {
            return sprite.getImage();
        }

        /**
         * Sets the facing direction for the sprite.
         */
        public void setDirection(boolean right) {
            facingRight = right;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            w = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            a = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            s = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            d = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            up = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            down = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            w = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            a = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            s = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            d = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            up = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            down = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //System.out.print(e.getKeyChar());
    }

    public void setLayout() {
        objects = new GameObject[layout.length][layout[1].length];
        for (int x = 0; x < layout[1].length; x++) {
            for (int y = 0; y < layout.length; y++) {
                if (layout[y][x] == 0) {
                    //Empty
                } else if (layout[y][x] == 1) {
                    //Wall
                    objects[y][x] = new GameObject("Wall", x * 100, y * 100);
                } else if (layout[y][x] == 2) {
                    //Spawn players here
                    player1.x = x * 100;
                    player1.y = y * 100;
                    player2.x = x * 100;
                    player2.y = y * 100;
                }
            }
        }
    }

    public static boolean checkCollision(GameObject player) {
        boolean r = false;
        GameObject wall = null;
        for (int x = 0; x < objects[1].length && !r; x++) {
            for (int y = 0; y < objects.length && !r; y++) {
                wall = objects[y][x];
                if (wall == null) {

                } else if (player.x + player.xSpeed > wall.x && player.x + player.xSpeed < wall.x + wall.width && player.y + player.ySpeed > wall.y && player.y + player.ySpeed < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed + player.width > wall.x && player.x + player.xSpeed < wall.x + wall.width && player.y + player.ySpeed > wall.y && player.y + player.ySpeed < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed > wall.x && player.x + player.xSpeed < wall.x + wall.width && player.y + player.ySpeed + player.height > wall.y && player.y + player.ySpeed + player.height < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed + player.width > wall.x && player.x + player.xSpeed < wall.x + wall.width && player.y + player.ySpeed + player.height > wall.y && player.y + player.ySpeed + player.height <= wall.y + wall.height) {
                    r = true;
                }
            }
        }
        return r;
    }

    public static boolean checkXCollision(GameObject player) {
        boolean r = false;
        GameObject wall = null;
        for (int x = 0; x < objects[1].length && !r; x++) {
            for (int y = 0; y < objects.length && !r; y++) {
                wall = objects[y][x];
                if (wall == null) {

                } else if (player.x + player.xSpeed > wall.x && player.x + player.xSpeed < wall.x + wall.width && player.y > wall.y && player.y < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed + player.width > wall.x && player.x + player.xSpeed < wall.x + wall.width && player.y > wall.y && player.y < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed > wall.x && player.x + player.xSpeed < wall.x + wall.width && player.y + player.height > wall.y && player.y + player.height < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed + player.width > wall.x && player.x + player.xSpeed < wall.x + wall.width && player.y + player.height > wall.y && player.y + player.height <= wall.y + wall.height) {
                    r = true;
                }
            }
        }
        return r;
    }

    public static boolean checkYCollision(GameObject player) {
        boolean r = false;
        GameObject wall = null;
        for (int x = 0; x < objects[1].length && !r; x++) {
            for (int y = 0; y < objects.length && !r; y++) {
                wall = objects[y][x];
                if (wall == null) {

                } else if (player.x > wall.x && player.x < wall.x + wall.width && player.y + player.ySpeed > wall.y && player.y + player.ySpeed < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.width > wall.x && player.x < wall.x + wall.width && player.y + player.ySpeed > wall.y && player.y + player.ySpeed < wall.y + wall.height) {
                    r = true;
                } else if (player.x > wall.x && player.x < wall.x + wall.width && player.y + player.ySpeed + player.height > wall.y && player.y + player.ySpeed + player.height < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.width > wall.x && player.x < wall.x + wall.width && player.y + player.ySpeed + player.height > wall.y && player.y + player.ySpeed + player.height <= wall.y + wall.height) {
                    r = true;
                }
            }
        }
        return r;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Platformer());
        Timer clock = new Timer(1000 / FPS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // up = w;
                // left = a;
                // down = s;
                // right = d;
                //Player 1 keys
                if (w) {   // Player 1 jump
                   store = player1.ySpeed;
                    player1.ySpeed = 1;
                    if (checkYCollision(player1) && player1JumpTimer == 0) {
                        player1.ySpeed = -playerJump;
                        player1JumpTimer = 18;
                    } else {
                        player1.ySpeed = store;
                    }
                } else {
                    player1.ySpeed += playerSpeed;
                }
                if (a) // Player 1 move left
                {
                    player1.xSpeed -= playerSpeed;
                    player1.setDirection(false);
                }
                if (s && icicleTimer <= 0) // Player 1 ability: shoot icicle
                {
                    //player1.ySpeed += playerSpeed;
                    icicleTimer = icicleCooldown;
                    icicle.x = player1.x;
                    icicle.y = player1.y + (player1.height/2 - icicle.height/2);
                    if(player1.facingRight)
                    {
                        icicle.xSpeed = 10;
                    }
                    else
                    {
                        icicle.xSpeed = -10;
                    }
                }
                icicleTimer--;
                if (d) // Player 1 move right
                {
                    player1.xSpeed += playerSpeed;
                    player1.setDirection(true);
                }
                //Player 2 keys
                if (up) {
                    store = player2.ySpeed;
                    player2.ySpeed = 1;
                    if (checkYCollision(player2) && player2JumpTimer == 0) {
                        player2.ySpeed = -playerJump;
                        player2JumpTimer = 21;
                    } else {
                        player2.ySpeed = store;
                    }
                } else {
                    player2.ySpeed += playerSpeed;
                }
                if (left) {
                    player2.xSpeed -= playerSpeed;
                    player2.setDirection(false);
                }
                if (down) {
                    //player2.ySpeed += playerSpeed;
                }
                if (right) {
                    player2.xSpeed += playerSpeed;
                    player2.setDirection(true);
                }
                //Player 1 traction
                if (player1.xSpeed > 0) {
                    player1.xSpeed -= traction;
                }
                if (player1.xSpeed < 0) {
                    player1.xSpeed += traction;
                }
                // if(player1.ySpeed > 0)
                // {
                //     player1.ySpeed -= traction;
                // }
                //if(player1.ySpeed < 0)
                //{
                player1.ySpeed += traction;
                //}
                //Player 2 traction
                if (player2.xSpeed > 0) {
                    player2.xSpeed -= traction;
                }
                if (player2.xSpeed < 0) {
                    player2.xSpeed += traction;
                }
                // if(player2.ySpeed > 0)
                // {
                //     player2.ySpeed -= traction;
                // }
                // if(player2.ySpeed < 0)
                // {
                player2.ySpeed += traction;
                //}
                //Player 1 max speed
                if (player1.xSpeed > playerMaxSpeed) {
                    player1.xSpeed = playerMaxSpeed;
                }
                if (player1.xSpeed < -playerMaxSpeed) {
                    player1.xSpeed = -playerMaxSpeed;
                }
                if (player1.ySpeed > playerMaxSpeed) {
                    player1.ySpeed = playerMaxSpeed;
                }
                // if(player1.ySpeed < -playerMaxSpeed)
                // {
                //     player1.ySpeed = -playerMaxSpeed;
                // }
                //Player 2 max speed
                if (player2.xSpeed > playerMaxSpeed) {
                    player2.xSpeed = playerMaxSpeed;
                }
                if (player2.xSpeed < -playerMaxSpeed) {
                    player2.xSpeed = -playerMaxSpeed;
                }
                if (player2.ySpeed > playerMaxSpeed) {
                    player2.ySpeed = playerMaxSpeed;
                }
                // if(player2.ySpeed < -playerMaxSpeed)
                // {
                //     player2.ySpeed = -playerMaxSpeed;
                // }
                //Player 1 frame offset
                if (player1.x > 100 * windowSize - player1.width / 2 + frame1xOffset) {
                    frame1xOffset += 100 * windowSize;
                }
                if (player1.x < -player1.width / 2 + frame1xOffset) {
                    frame1xOffset -= 100 * windowSize;
                }
                if (player1.y > 100 * windowSize - player1.height / 2 + frame1yOffset) {
                    frame1yOffset += 100 * windowSize;
                }
                if (player1.y < -player1.height / 2 + frame1yOffset) {
                    frame1yOffset -= 100 * windowSize;
                }
                //Player 2 frame offset
                if (player2.x > 100 * windowSize - player2.width / 2 + frame2xOffset) {
                    frame2xOffset += 100 * windowSize;
                }
                if (player2.x < -player2.width / 2 + frame2xOffset) {
                    frame2xOffset -= 100 * windowSize;
                }
                if (player2.y > 100 * windowSize - player2.height / 2 + frame2yOffset) {
                    frame2yOffset += 100 * windowSize;
                }
                if (player2.y < -player2.height / 2 + frame2yOffset) {
                    frame2yOffset -= 100 * windowSize;
                }
                //Collision
                player1CollisionXY = checkCollision(player1);
                player2CollisionXY = checkCollision(player2);
                if (player1CollisionXY) {
                    player1CollisionX = checkXCollision(player1);
                    player1CollisionY = checkYCollision(player1);
                } else {
                    player1CollisionX = false;
                    player1CollisionY = false;
                }
                if (checkCollision(icicle))
                {
                    icicle.x += icicle.xSpeed;
                    icicle.xSpeed = 0;
                }
                if (player2CollisionXY) {
                    player2CollisionX = checkXCollision(player2);
                    player2CollisionY = checkYCollision(player2);
                } else {
                    player2CollisionX = false;
                    player2CollisionY = false;
                }
                if (player1CollisionX && !player1CollisionY) {
                    while (checkXCollision(player1)) {
                        if (player1.xSpeed > 0) {
                            player1.xSpeed -= traction;
                        }
                        if (player1.xSpeed < 0) {
                            player1.xSpeed += traction;
                        }
                    }
                } else if (!player1CollisionX && player1CollisionY) {
                    while (checkYCollision(player1)) {
                        if (player1.ySpeed > 0) {
                            player1.ySpeed -= traction;
                        }
                        if (player1.ySpeed < 0) {
                            player1.ySpeed += traction;
                        }
                    }
                }
                while (checkCollision(player1)) {
                    if (player1.xSpeed > 0) {
                        player1.xSpeed -= traction;
                    }
                    if (player1.xSpeed < 0) {
                        player1.xSpeed += traction;
                    }
                    if (player1.ySpeed > 0) {
                        player1.ySpeed -= traction;
                    }
                    if (player1.ySpeed < 0) {
                        player1.ySpeed += traction;
                    }
                }
                if (player2CollisionX && !player2CollisionY) {
                    while (checkXCollision(player2)) {
                        if (player2.xSpeed > 0) {
                            player2.xSpeed -= traction;
                        }
                        if (player2.xSpeed < 0) {
                            player2.xSpeed += traction;
                        }
                    }
                } else if (!player2CollisionX && player2CollisionY) {
                    while (checkYCollision(player2)) {
                        if (player2.ySpeed > 0) {
                            player2.ySpeed -= traction;
                        }
                        if (player2.ySpeed < 0) {
                            player2.ySpeed += traction;
                        }
                    }
                }
                while (checkCollision(player2)) {
                    if (player2.xSpeed > 0) {
                        player2.xSpeed -= traction;
                    }
                    if (player2.xSpeed < 0) {
                        player2.xSpeed += traction;
                    }
                    if (player2.ySpeed > 0) {
                        player2.ySpeed -= traction;
                    }
                    if (player2.ySpeed < 0) {
                        player2.ySpeed += traction;
                    }
                }
                player1JumpTimer = updatePlayerState(player1, player1JumpTimer);
                player2JumpTimer = updatePlayerState(player2, player2JumpTimer);


                    // Setting player 1 animations
                if (player1.state == PlayerState.AIRBORNE) {
                    player1.setSprite("air");
                } else if (player1.state == PlayerState.RUNNING) {
                    player1.setSprite("run");
                } else if (player1.state == PlayerState.JUMPING) {
                    player1.setSprite("jump");
                } else if (player1.state == PlayerState.FALLING) {
                    player1.setSprite("fall");
                } else if (player1.state == PlayerState.STUNNED) {
                    player1.setSprite("burned");
                } else {
                    player1.setSprite("idle");
                }

                    // Setting player 2 animations
                if (player2.state == PlayerState.AIRBORNE) {
                    player2.setSprite("air");
                } else if (player2.state == PlayerState.JUMPING) {
                    player2.setSprite("jump");
                } else if (player2.state == PlayerState.FALLING) {
                    player2.setSprite("fall");
                } else if (player2.state == PlayerState.STUNNED) {
                    player2.setSprite("frozen");
                } else {
                    player2.setSprite("idle");
                }

                // System.out.println("JumpTimer for P2: " + player2JumpTimer);

                //Update player posistion 
                player1.x += player1.xSpeed;
                player1.y += player1.ySpeed;
                icicle.x += icicle.xSpeed;
                icicle.y += icicle.ySpeed;
                player2.x += player2.xSpeed;
                player2.y += player2.ySpeed;
                //Reset frame
                frame1.repaint();
                frame2.repaint();
            }
        });
        clock.start();
    }
}
