import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * Runs a 2D multiplayer platformer engine. This comment should be edited as we
 * make progress to accurately describe the functionality of this class.
 *
 * @author Wyatt Bradshaw, Thomas Hammersma, Ayden McCabe, Andrew Pratt
 * @version 5.1.26
 */
public class Platformer implements Runnable, KeyListener {

    /**
     * Time until player 1 gets stuuned, in frames
     */
    private static int player1Stun = 3 * 60;

    /**
     * Time until player 2 gets stuuned, in frames
     */
    private static int player2Stun = 3 * 60;

    /**
     * Time until player 1 can fire another icicle, in frames
     */
    private static int icicleCooldown = 3 * 60;

    /**
     * Speed of the icicle, in pixels/frame.
     */
    private static int icicleSpeed = 20;

    /**
     * Time until player 2 can use the spear, in frames
     */
    private static int spearCooldown = 3 * 60;

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
     * A variable representing the end goal in int[][] layout.
     */
    private static int F = 3;

    /**
     * current animation state of a player
     */
    public enum PlayerState {
        GROUNDED,
        AIRBORNE,
        RUNNING,
        JUMPING,
        FALLING,
        STUNNED,
        ATTACKING
    }

    private static int player1JumpTimer = 0;

    private static int player2JumpTimer = 0;
    /**
     * A 2D array that directly represents the layout of the level.
     *
     * 0 = empty, W = walls, P = playerStart, R = ?
     */
    private static int[][] layout = {
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 3, 1 },
            { 1, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1 },
            { 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1 },
            { 1, 2, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
    };

    private static java.util.HashMap<String, ImageIcon> scoreImages = new java.util.HashMap<>();

    private static void loadScoreImages() {
        String[] names = {
                "StartGame.png", "fire1.png", "fire2.png", "firewin.png",
                "ice1.png", "ice2.png", "icewin.png",
                "fire1ice1.png", "ice1fire1.png"
        };
        for (String name : names) {
            scoreImages.put(name, new ImageIcon("Platformer/Scoreboard/" + name));
        }
    }

    // race logic
    private static String gameState = "START";
    private static int fireWins = 0;
    private static int iceWins = 0;
    private static String winOrder = "";
    private static int screenTimer = 0;
    private static List<int[][]> raceMaps = new ArrayList<>();
    private static Random rand = new Random();

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
    private static int spearTimer = 0;
    private static int player1Stunned = 0;
    private static int player2Stunned = 0;
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

    /** Player 1. */
    public static GameObject player1;

    /** Used to determine whether player 1 was facing left or right when firing his icicle. */
    public static boolean p1facingRightWhenLaunched;

    /** The length of player 1's attack animation. */
    public static final int ATTACK_LENGTH = 13;

    /** The rectangle that acts as Player 1's icicle attack hurtbox. */
    public static GameObject icicle;

    /** Map that assigns simple keywords to filepaths for player 1's sprite animations. */
    public static Map<String, String> p1Sprites = Map.of(
        "idle", "Platformer/ArtAssets/iceGuyIdle.gif",
        "jump", "Platformer/ArtAssets/iceGuyJump.gif",
        "run", "Platformer/ArtAssets/iceGuyRun.gif",
        "air", "Platformer/ArtAssets/iceGuyAir.gif",
        "fall", "Platformer/ArtAssets/iceGuyFall.gif",
        "burned", "Platformer/ArtAssets/iceGuyBurned.gif",
        "shoot", "Platformer/ArtAssets/iceGuyShoot.gif"
    );

    /** Player 2. */
    public static GameObject player2;

    /** The rectangle that acts as Player 2's spear attack hurtbox. */
    public static GameObject spear;

    /** Used to determine whether player 2 was facing left or right when casting his spear. */
    public static boolean p2facingRightWhenLaunched;

    /** Map that assigns simple keywords to filepaths for player 2's sprite animations. */
    public static Map<String, String> p2Sprites = Map.of(
        "idle", "Platformer/ArtAssets/fireGuyIdle.gif",
        "jump", "Platformer/ArtAssets/fireGuyJump.gif",
        "run", "Platformer/ArtAssets/fireGuyRun.gif",
        "air", "Platformer/ArtAssets/fireGuyAir.gif",
        "fall", "Platformer/ArtAssets/fireGuyFall.gif",
        "frozen", "Platformer/ArtAssets/fireGuyFreeze.gif",
        "poke", "Platformer/ArtAssets/fireGuyPoke.gif"
    );

    /** The tile that triggers end-of-level logic when touched. */
    public static GameObject flag;

    // private static JButton b;
    @Override
    public void run() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            barSize = 28;
        }
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // I need to test this on windows --> tested it, it works :D
            barSize = 27;
        }

        // This is the old constructor call for player 1, which just makes him a green
        // square.
        // player1 = new GameObject("Player 1",0,0,100,100, Color.GREEN);

        // Constructor calls for player characters and items
        player1 = new GameObject("Player 1", 0, 0, 100, 100, p1Sprites);
        icicle = new GameObject("Icicle", -100, -100, 100,20, "Platformer/ArtAssets/icicle.png");
        player2 = new GameObject("Player 2", 0, 0, 100, 100, p2Sprites);
        spear = new GameObject("Spear", -100, -100, 200, 20, "Platformer/ArtAssets/spear.gif");
        flag = new GameObject("Flag", -100, -100, 20, 100, Color.YELLOW);

        loadMapsFromFile("Platformer/Levels2.txt");
        loadRandomMap();
        screenTimer = 3 * FPS;
        loadScoreImages();

        frame1 = new JFrame("Player 1");
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setPreferredSize(new Dimension(scale, scale + barSize));
        frame1.setResizable(false);
        frame2 = new JFrame("Player 2");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setPreferredSize(new Dimension(scale, scale + barSize));
        frame2.setResizable(false);
        // b = new JButton("Test");
        // b.addActionListener(this);

        panel1 = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw background
                g.drawImage(new ImageIcon("Platformer/ArtAssets/background.png").getImage(), 0, 0, getWidth(), getHeight(), this);

                // Show Score
                if (!gameState.equals("RACING")) {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    Image img = getScreenImage();
                    if (img != null) {
                        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                    }
                    drawWinSprites(g, this);
                    return;
                }

                // Paints the end goal.
                g.setColor(flag.color);
                g.fillRect(flag.x - frame1xOffset, flag.y - frame1yOffset, flag.width, flag.height);

                // Paints player 2.
                if (!player2.facingRight) {
                    g.drawImage(player2.spriteImage, (player2.x + player1.width) - frame1xOffset, player2.y - frame1yOffset,
                            -player2.width, player2.height, this);
                } else {
                    g.drawImage(player2.spriteImage, (player2.x) - frame1xOffset, player2.y - frame1yOffset,
                            player2.width, player2.height, this);
                }

                // Paints player 1.
                if (!player1.facingRight) {
                    g.drawImage(player1.spriteImage, (player1.x + player1.width) - frame1xOffset, player1.y - frame1yOffset,
                            -player1.width, player1.height, this);
                } else {
                    g.drawImage(player1.spriteImage, (player1.x) - frame1xOffset, player1.y - frame1yOffset,
                            player1.width, player1.height, this);
                }

                // Paints player 2's spear when he attacks.
                if (!p2facingRightWhenLaunched) {
                    g.drawImage(spear.getImage(), (spear.x + spear.width) - frame1xOffset, spear.y - frame1yOffset, -spear.width, spear.height, this);
                } else {
                    g.drawImage(spear.getImage(), spear.x - frame1xOffset, spear.y - frame1yOffset, spear.width, spear.height, this);
                }

                // Paints player 1's icicle when he attacks.
                if (!p1facingRightWhenLaunched) {
                    g.drawImage(icicle.getImage(), (icicle.x + 100) - frame1xOffset, icicle.y - frame1yOffset, -icicle.width, icicle.height, this);
                } else {
                    g.drawImage(icicle.getImage(), icicle.x - frame1xOffset, icicle.y - frame1yOffset, icicle.width, icicle.height, this);
                }

                // Paints the level.
                for (int x = 0; x < objects[1].length; x++) {
                    for (int y = 0; y < objects.length; y++) {
                        if (objects[y][x] != null) {
                            // g.setColor(objects[y][x].color);
                            // g.fillRect(objects[y][x].x - frame1xOffset, objects[y][x].y - frame1yOffset,
                                    // objects[y][x].width, objects[y][x].height);
                            ImageIcon tile = new ImageIcon("Platformer/ArtAssets/brick.png");
                            g.drawImage(tile.getImage(), objects[y][x].x - frame1xOffset, objects[y][x].y - frame1yOffset, objects[y][x].width, objects[y][x].height, this);
                        }
                    }
                }
            }
        };
        panel2 = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw background
                g.drawImage(new ImageIcon("Platformer/ArtAssets/background.png").getImage(), 0, 0, getWidth(), getHeight(), this);

                // Display Transition Screen
                if (!gameState.equals("RACING")) {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    Image img = getScreenImage();
                    if (img != null) {
                        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                    }
                    drawWinSprites(g, this);
                    return;
                }

                // Paints the end goal.
                g.setColor(flag.color);
                g.fillRect(flag.x - frame2xOffset, flag.y - frame2yOffset, flag.width, flag.height);

                // Paints player 1.
                if (!player1.facingRight) {
                    g.drawImage(player1.spriteImage, (player1.x + player1.width) - frame2xOffset, player1.y - frame2yOffset,
                            -player1.width, player1.height, this);
                } else {
                    g.drawImage(player1.spriteImage, (player1.x) - frame2xOffset, player1.y - frame2yOffset,
                            player1.width, player1.height, this);
                }

                // Paints player 2.
                if (!player2.facingRight) {
                    g.drawImage(player2.spriteImage, (player2.x + player2.width) - frame2xOffset, player2.y - frame2yOffset,
                            -player2.width, player2.height, this);
                } else {
                    g.drawImage(player2.spriteImage, (player2.x) - frame2xOffset, player2.y - frame2yOffset,
                            player2.width, player2.height, this);
                }

                // Paints player 1's icicle when he attacks.
                if (!p1facingRightWhenLaunched) {
                    g.drawImage(icicle.getImage(), (icicle.x + player1.width) - frame2xOffset, icicle.y - frame2yOffset, -icicle.width, icicle.height, this);
                } else {
                    g.drawImage(icicle.getImage(), icicle.x - frame2xOffset, icicle.y - frame2yOffset, icicle.width, icicle.height, this);
                }

                // Paints player 2's spear when he attacks.
                if (!p2facingRightWhenLaunched) {
                    g.drawImage(spear.getImage(), (spear.x + spear.width) - frame2xOffset, spear.y - frame2yOffset, -spear.width, spear.height, this);
                } else {
                    g.drawImage(spear.getImage(), spear.x - frame2xOffset, spear.y - frame2yOffset, spear.width, spear.height, this);
                }

                // Paints the level.
                for (int x = 0; x < objects[1].length; x++) {
                    for (int y = 0; y < objects.length; y++) {
                        if (objects[y][x] != null) {
                            // g.setColor(objects[y][x].color);
                            // g.fillRect(objects[y][x].x - frame2xOffset, objects[y][x].y - frame2yOffset,
                                    // objects[y][x].width, objects[y][x].height);
                            ImageIcon tile = new ImageIcon("Platformer/ArtAssets/brick.png");
                            g.drawImage(tile.getImage(), objects[y][x].x - frame2xOffset, objects[y][x].y - frame2yOffset, objects[y][x].width, objects[y][x].height, this);
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

    /**
     * Called every frame to determine what state the player is in.
     * Used to assign animations at the correct times.
     * 
     * @param player    the player being assessed
     * @param jumpTimer the player's jumpTimer
     * @return the updated jumpTimer
     */
    public static int updatePlayerState(GameObject player, int jumpTimer) {

        boolean isGrounded = (player == player1 ? player1CollisionY : player2CollisionY);
        PlayerState currentState = player.state;
        PlayerState newState;

        if ((player == player1 && player1Stunned > 0) || (player == player2 && player2Stunned > 0)) {
            newState = PlayerState.STUNNED;
            if (currentState != newState) {
                player.state = newState;
            }
            return jumpTimer;
        }

        if ((player == player1 && icicleTimer > (icicleCooldown - ATTACK_LENGTH)) || (player == player2 && spearTimer > (spearCooldown - ATTACK_LENGTH))) {
            newState = PlayerState.ATTACKING;
            if (currentState != newState) {
                player.state = newState;
            }
            return jumpTimer;
        }

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
            // System.out.println("Player state changed from " + currentState + "to" +
            // newState);
        }

        return jumpTimer;

    }

    /**
     * Encapsulated class that represents an object in the game that can be
     * interacted with in some way. Contains name, location, scale, speed,
     * color, and sprite data.
     */
    public static class GameObject {

        public PlayerState state = PlayerState.GROUNDED;

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
         * The Map containing filepaths to the sprites of the GameObject (for objects
         * with multiple sprites).
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
         * @param name           the name of the GameObject
         * @param x              the GameObject's x-coordinate
         * @param y              the GameObject's y-coordinate
         * @param width          the width of the GameObject
         * @param height         the height of the GameObject
         * @param spriteFileName the file name of the sprite to represent the
         *                       GameObject.
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
         * @param name    the name of the GameObject
         * @param x       the GameObject's x-coordinate
         * @param y       the GameObject's y-coordinate
         * @param width   the width of the GameObject
         * @param height  the height of the GameObject
         * @param sprites the array of ImageIcons that will be assigned to the
         *                SpriteSet.
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
         * 
         * @param path the file path to be set to, as passed through the spriteSet Map.
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
        // System.out.print(e.getKeyChar());
    }

    public static void loadMapsFromFile(String path) {
        raceMaps.clear();
        try {
            Scanner sc = new Scanner(new java.io.File(path));
            List<int[]> currentRows = null;
            while (sc.hasNextLine()) {
                String s = sc.nextLine().trim();
                if (s.equals("{")) {
                    currentRows = new ArrayList<>();
                } else if (s.equals("};") && currentRows != null) {
                    int[][] map = new int[currentRows.size()][];
                    for (int i = 0; i < currentRows.size(); i++) {
                        map[i] = currentRows.get(i);
                    }
                    raceMaps.add(map);
                    currentRows = null;
                } else if (currentRows != null && s.startsWith("{")) {
                    //Thank you Professor White for helping me learn Regex
                    String inner = s.replaceAll("^\\{", "").replaceAll("\\},?$", "").trim();
                    String[] parts = inner.split("[,\\s]+");
                    int[] row = new int[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        row[i] = Integer.parseInt(parts[i].trim());
                    }
                    currentRows.add(row);
                }
            }
            sc.close();
        } catch (java.io.FileNotFoundException ex) {
        }
    }

    public static void drawWinSprites(Graphics g, java.awt.Component obs) {
        if (!gameState.equals("ROUND_END") && !gameState.equals("GAME_OVER"))
            return;
        int spriteW = 150;
        int spriteH = 150;
        int y = 400;
        boolean fireWon = winOrder.length() > 0 && winOrder.charAt(winOrder.length() - 1) == 'F';
        Image fireImg = new ImageIcon(fireWon ? p2Sprites.get("idle") : p2Sprites.get("frozen")).getImage();
        Image iceImg = new ImageIcon(!fireWon ? p1Sprites.get("idle") : p1Sprites.get("burned")).getImage();
        if (fireWon) {
            g.drawImage(iceImg, 50, y, spriteW, spriteH, obs);
            g.drawImage(fireImg, 500, y, spriteW, spriteH, obs);
        } else {
            g.drawImage(fireImg, 50, y, spriteW, spriteH, obs);
            g.drawImage(iceImg, 500, y, spriteW, spriteH, obs);
        }
    }

    public static void loadRandomMap() {
        if (raceMaps.isEmpty()) {
            return;
        }
        layout = raceMaps.get(rand.nextInt(raceMaps.size()));
    }

    public static void resetPlayers() {
        player1.xSpeed = 0;
        player1.ySpeed = 0;
        player2.xSpeed = 0;
        player2.ySpeed = 0;
        player1Stunned = 0;
        player2Stunned = 0;
        player1JumpTimer = 0;
        player2JumpTimer = 0;
        icicleTimer = icicleCooldown - ATTACK_LENGTH;
        spearTimer = spearCooldown - ATTACK_LENGTH;
        icicle.x = -100;
        icicle.y = -100;
        icicle.xSpeed = 0;
        spear.x = -100;
        spear.y = -100;
        spear.xSpeed = 0;
        frame1xOffset = 0;
        frame1yOffset = 0;
        frame2xOffset = 0;
        frame2yOffset = 0;
    }

    public static String getScreenImagePath() {
        if (gameState.equals("START")) {
            return "Platformer/Scoreboard/StartGame.png";
        }
        if (gameState.equals("GAME_OVER")) {
            if (fireWins >= 2)
                return "Platformer/Scoreboard/firewin.png";
            if (iceWins >= 2)
                return "Platformer/Scoreboard/icewin.png";
        }
        if (gameState.equals("ROUND_END")) {
            if (winOrder.endsWith("FF"))
                return "Platformer/Scoreboard/fire2.png";
            if (winOrder.endsWith("II"))
                return "Platformer/Scoreboard/ice2.png";
            if (winOrder.equals("FI"))
                return "Platformer/Scoreboard/fire1ice1.png";
            if (winOrder.equals("IF"))
                return "Platformer/Scoreboard/ice1fire1.png";
            if (winOrder.equals("F"))
                return "Platformer/Scoreboard/fire1.png";
            if (winOrder.equals("I"))
                return "Platformer/Scoreboard/ice1.png";
        }//shouldnt get past this
        return null;
    }

    public static Image getScreenImage() {
        String path = getScreenImagePath();
        if (path == null)
            return null;
        String key = path.substring(path.lastIndexOf('/') + 1);
        ImageIcon icon = scoreImages.get(key);
        return icon != null ? icon.getImage() : null;
    }

    public static void playSound(String path) {
        try {
            java.io.File f = new java.io.File(path);
            if (!f.exists())
                return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (Exception ex) {
        }
    }

    public static void setLayout() {
        objects = new GameObject[layout.length][layout[1].length];
        for (int x = 0; x < layout[1].length; x++) {
            for (int y = 0; y < layout.length; y++) {
                if (layout[y][x] == 0) {
                    // Empty
                } else if (layout[y][x] == 1) {
                    // Wall
                    objects[y][x] = new GameObject("Wall", x * 100, y * 100, 100, 100, "Platformer/ArtAssets/brick.png");
                } else if (layout[y][x] == 2) {
                    // Spawn players here
                    player1.x = x * 100;
                    player1.y = y * 100;
                    player2.x = x * 100;
                    player2.y = y * 100;
                } else if (layout[y][x] == 3) {
                    // Flag
                    flag.x = (x * 100) + (50 - (flag.width / 2));
                    flag.y = y * 100;
                }
            }
        }
    }

    public static boolean checkSingleCollision(GameObject Object1, GameObject Object2) {
        boolean r = false;
        if (Object1.x > Object2.x && Object1.x < Object2.x + Object2.width && Object1.y > Object2.y
                && Object1.y < Object2.y + Object2.height) {
            r = true;
        } else if (Object1.x + Object1.width > Object2.x && Object1.x < Object2.x + Object2.width
                && Object1.y > Object2.y && Object1.y < Object2.y + Object2.height) {
            r = true;
        } else if (Object1.x > Object2.x && Object1.x < Object2.x + Object2.width
                && Object1.y + Object1.height > Object2.y && Object1.y + Object1.height < Object2.y + Object2.height) {
            r = true;
        } else if (Object1.x + Object1.width > Object2.x && Object1.x < Object2.x + Object2.width
                && Object1.y + Object1.height > Object2.y && Object1.y + Object1.height <= Object2.y + Object2.height) {
            r = true;
        }
        return r;
    }

    public static boolean checkCollision(GameObject player) {
        boolean r = false;
        GameObject wall = null;
        for (int x = 0; x < objects[1].length && !r; x++) {
            for (int y = 0; y < objects.length && !r; y++) {
                wall = objects[y][x];
                if (wall == null) {

                } else if (player.x + player.xSpeed > wall.x && player.x + player.xSpeed < wall.x + wall.width
                        && player.y + player.ySpeed > wall.y && player.y + player.ySpeed < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed + player.width > wall.x
                        && player.x + player.xSpeed < wall.x + wall.width && player.y + player.ySpeed > wall.y
                        && player.y + player.ySpeed < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed > wall.x && player.x + player.xSpeed < wall.x + wall.width
                        && player.y + player.ySpeed + player.height > wall.y
                        && player.y + player.ySpeed + player.height < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed + player.width > wall.x
                        && player.x + player.xSpeed < wall.x + wall.width
                        && player.y + player.ySpeed + player.height > wall.y
                        && player.y + player.ySpeed + player.height <= wall.y + wall.height) {
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

                } else if (player.x + player.xSpeed > wall.x && player.x + player.xSpeed < wall.x + wall.width
                        && player.y > wall.y && player.y < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed + player.width > wall.x
                        && player.x + player.xSpeed < wall.x + wall.width && player.y > wall.y
                        && player.y < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed > wall.x && player.x + player.xSpeed < wall.x + wall.width
                        && player.y + player.height > wall.y && player.y + player.height < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.xSpeed + player.width > wall.x
                        && player.x + player.xSpeed < wall.x + wall.width && player.y + player.height > wall.y
                        && player.y + player.height <= wall.y + wall.height) {
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

                } else if (player.x > wall.x && player.x < wall.x + wall.width && player.y + player.ySpeed > wall.y
                        && player.y + player.ySpeed < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.width > wall.x && player.x < wall.x + wall.width
                        && player.y + player.ySpeed > wall.y && player.y + player.ySpeed < wall.y + wall.height) {
                    r = true;
                } else if (player.x > wall.x && player.x < wall.x + wall.width
                        && player.y + player.ySpeed + player.height > wall.y
                        && player.y + player.ySpeed + player.height < wall.y + wall.height) {
                    r = true;
                } else if (player.x + player.width > wall.x && player.x < wall.x + wall.width
                        && player.y + player.ySpeed + player.height > wall.y
                        && player.y + player.ySpeed + player.height <= wall.y + wall.height) {
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

                // ScoreBoard
                if (!gameState.equals("RACING")) {
                    if (screenTimer > 0) {
                        screenTimer--;
                        frame1.repaint();
                        frame2.repaint();
                    } else if (screenTimer == 0 && !gameState.equals("GAME_OVER")) {
                        loadRandomMap();
                        setLayout();
                        resetPlayers();
                        gameState = "RACING";
                        playSound("Platformer/Sound/Start.wav");
                        frame1.repaint();
                        frame2.repaint();
                    }
                    return;
                }

                // Player 1 keys
                if (player1Stunned > 0) {
                    w = false;
                    a = false;
                    s = false;
                    d = false;
                }
                if (player2Stunned > 0) {
                    up = false;
                    left = false;
                    down = false;
                    right = false;
                }
                if (w) { // Player 1 jump
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
                    icicleTimer = icicleCooldown;
                    icicle.x = player1.x;
                    icicle.y = player1.y + (player1.height / 2 - icicle.height / 2);
                    if (player1.facingRight) {
                        icicle.xSpeed = icicleSpeed;
                    } else {
                        icicle.xSpeed = -icicleSpeed;
                    }
                    p1facingRightWhenLaunched = player1.facingRight;
                }
                if (icicle.xSpeed != 0) {
                    if (checkSingleCollision(icicle, player2)) {
                        player2Stunned = player2Stun;
                        player2.xSpeed = icicle.xSpeed;
                    }
                }
                icicleTimer--;
                if (icicleTimer <= 0) {
                    icicle.x = -100;
                    icicle.y = -100;
                    icicle.xSpeed = 0;
                }
                if (d) // Player 1 move right
                {
                    player1.xSpeed += playerSpeed;
                    player1.setDirection(true);
                }
                // Player 2 keys
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
                if (down && spearTimer <= 0) // Player 2 ability: use spear
                {
                    spearTimer = spearCooldown;
                    spear.x = player2.x;
                    spear.y = player2.y + (player2.height / 2 - spear.height / 2);
                    if (player2.facingRight) {
                        spear.x += player2.width;
                        spear.xSpeed = 20;
                    } else {
                        spear.x -= player2.width + 100;
                        spear.xSpeed = -20;
                    }
                    p2facingRightWhenLaunched = player2.facingRight;
                }
                if (spearTimer < spearCooldown - 30) {
                    spear.x = -100;
                    spear.y = -100;
                    spear.xSpeed = 0;
                }
                if (spear.xSpeed != 0) {
                    if (checkSingleCollision(spear, player1)) {
                        player1Stunned = player1Stun;
                        player1.xSpeed = spear.xSpeed;
                    }
                }
                spearTimer--;
                if (right) {
                    player2.xSpeed += playerSpeed;
                    player2.setDirection(true);
                }
                // Player 1 traction
                if (player1.xSpeed > 0) {
                    player1.xSpeed -= traction;
                }
                if (player1.xSpeed < 0) {
                    player1.xSpeed += traction;
                }
                // if(player1.ySpeed > 0)
                // {
                // player1.ySpeed -= traction;
                // }
                // if(player1.ySpeed < 0)
                // {
                player1.ySpeed += traction;
                // }
                // Player 2 traction
                if (player2.xSpeed > 0) {
                    player2.xSpeed -= traction;
                }
                if (player2.xSpeed < 0) {
                    player2.xSpeed += traction;
                }
                // if(player2.ySpeed > 0)
                // {
                // player2.ySpeed -= traction;
                // }
                // if(player2.ySpeed < 0)
                // {
                player2.ySpeed += traction;
                // }
                // Player 1 max speed
                if (player1.xSpeed > playerMaxSpeed) {
                    player1.xSpeed--;
                }
                if (player1.xSpeed < -playerMaxSpeed) {
                    player1.xSpeed++;
                }
                if (player1.ySpeed > playerMaxSpeed) {
                    player1.ySpeed = playerMaxSpeed;
                }
                // if(player1.ySpeed < -playerMaxSpeed)
                // {
                // player1.ySpeed = -playerMaxSpeed;
                // }
                // Player 2 max speed
                if (player2.xSpeed > playerMaxSpeed) {
                    player2.xSpeed--;
                }
                if (player2.xSpeed < -playerMaxSpeed) {
                    player2.xSpeed++;
                }
                if (player2.ySpeed > playerMaxSpeed) {
                    player2.ySpeed = playerMaxSpeed;
                }
                // if(player2.ySpeed < -playerMaxSpeed)
                // {
                // player2.ySpeed = -playerMaxSpeed;
                // }
                // Player 1 frame offset
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
                // Player 2 frame offset
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
                // Collision
                player1CollisionXY = checkCollision(player1);
                player2CollisionXY = checkCollision(player2);
                if (player1CollisionXY) {
                    player1CollisionX = checkXCollision(player1);
                    player1CollisionY = checkYCollision(player1);
                } else {
                    player1CollisionX = false;
                    player1CollisionY = false;
                }
                if (checkCollision(icicle)) {
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
                } else if (player1.state == PlayerState.ATTACKING) {
                    player1.setSprite("shoot");
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
                } else if (player2.state == PlayerState.ATTACKING) {
                    player2.setSprite("poke");
                } else {
                    player2.setSprite("idle");
                }

                // System.out.println("JumpTimer for P2: " + player2JumpTimer);

                // Update player posistion
                player1.x += player1.xSpeed;
                player1.y += player1.ySpeed;
                icicle.x += icicle.xSpeed;
                icicle.y += icicle.ySpeed;
                player2.x += player2.xSpeed;
                player2.y += player2.ySpeed;
                // Reset frame
                frame1.repaint();
                frame2.repaint();
                player1Stunned--;
                player2Stunned--;
                if (checkSingleCollision(player1, flag)) {
                    // p1 (ice) flag
                    playSound("Platformer/Sound/FlagPole.wav");
                    iceWins++;
                    winOrder += "I";
                    if (iceWins >= 2) {
                        gameState = "GAME_OVER";
                        screenTimer = -1;
                    } else {
                        gameState = "ROUND_END";
                        screenTimer = 3 * FPS;
                    }
                }
                if (checkSingleCollision(player2, flag)) {
                    // p2 (fire) flag
                    playSound("Platformer/Sound/FlagPole.wav");
                    fireWins++;
                    winOrder += "F";
                    if (fireWins >= 2) {
                        gameState = "GAME_OVER";
                        screenTimer = -1;
                    } else {
                        gameState = "ROUND_END";
                        screenTimer = 3 * FPS;
                    }
                }
            }
        });
        clock.start();
    }
}
