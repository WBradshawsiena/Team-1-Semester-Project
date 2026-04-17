import java.awt.*;
import javax.swing.*;
public class BMIAPP extends JFrame {
    private JPanel page1;
    private JPanel page2;
    private JPanel currentPage;
    private JButton toggleButton;
    private double weight;
    private int height;
    private JTextField textField; 
    private JLabel label2;
    private JLabel bmiLabel; 
    public BMIAPP() {
        setTitle("BMIAPP Toggle Example Page");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createPage1();
        createPage2();

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        toggleButton = new JButton("Switch to Page 2");
        toggleButton.addActionListener(e -> togglePage());
        topPanel.add(toggleButton);

        add(topPanel, BorderLayout.NORTH);

        currentPage = page1;
        add(currentPage, BorderLayout.CENTER);
    }

    private void createPage1() {
        page1 = new JPanel(new BorderLayout());
        page1.setBackground(new Color(240, 240, 255));

        JLabel label = new JLabel("Personal Tracker", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
         label2= new JLabel("Weight (kg):"+ weight+"\nHeight (cm):"+ height, JLabel.CENTER);
        JButton button1 = new JButton("Goal Tracker");
        button1.addActionListener(e -> JOptionPane.showMessageDialog(this, "Hello from Page 1!"));
        textField = new JTextField(10);           // <-- Text field
        textField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton getInputButton = new JButton("Set Weight");
        getInputButton.addActionListener(e -> getInputFromField(0));

        JButton getInputButton2 = new JButton("Set Height");
        getInputButton2.addActionListener(e -> getInputFromField(1));
        JPanel centerPanel = new JPanel();
        centerPanel.add(textField);
        centerPanel.add(getInputButton);
        centerPanel.add(getInputButton2);
        centerPanel.add(button1);
        page1.add(label, BorderLayout.NORTH);
        page1.add(label2, BorderLayout.SOUTH);
        page1.add(centerPanel, BorderLayout.CENTER);
    }

    private void createPage2() {
        page2 = new JPanel(new BorderLayout());
        page2.setBackground(new Color(255, 240, 240));

        JLabel label = new JLabel("BMI Results", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));

        JButton button2 = new JButton("Click Me on Page 2");
        button2.addActionListener(e -> JOptionPane.showMessageDialog(this, "Hello from Page 2!"));

        bmiLabel = new JLabel("Make sure to set your weight and height on Page 1 first.", JLabel.CENTER);
        bmiLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JButton calcButton = new JButton("Calculate BMI");
        calcButton.addActionListener(e -> calculateBMI());
        
        JPanel centerPanel = new JPanel();
        
        centerPanel.setBackground(new Color(255, 240, 240));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        bmiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        calcButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(Box.createVerticalStrut(60));
        centerPanel.add(bmiLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(calcButton);
        
        page2.add(label, BorderLayout.NORTH);
        page2.add(centerPanel, BorderLayout.CENTER);}

    private void calculateBMI() {
        if (weight == 0 || height == 0) {
            bmiLabel.setText("Please set both weight and height on Page 1.");
            return;
        }
        double heightInMeters = height / 100.0;
        double bmi = weight / (heightInMeters * heightInMeters);
        bmiLabel.setText(String.format("BMI: %.2f", bmi));
        
    
        revalidate();
        repaint();
    }

    
    private void getInputFromField(int index) {
        String input = textField.getText().trim(); 
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "The text field is empty!", 
                "Input Error", 
                JOptionPane.WARNING_MESSAGE); 
            return;
        }
        if (index == 0) {
            weight = Double.parseDouble(input);
        }
        if (index == 1) {
            height = Integer.parseInt(input);
        }
        label2.setText("<html>Weight (kg): " + weight + "<br>Height (cm): " + height + "</html>");
        revalidate();
        repaint();
    }
    private void togglePage() {
        remove(currentPage);

        if (currentPage == page1) {
            currentPage = page2;
            toggleButton.setText("Switch to Page 1");
        } else {
            currentPage = page1;
            toggleButton.setText("Switch to Page 2");
        }

        add(currentPage, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {new BMIAPP().setVisible(true);});
    }
}
