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
    private double goalCalories;
 
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
        label2 = new JLabel("<html>Weight (kg): " + weight + "<br>Height (cm): " + height + "<br>Goal Calories: " + goalCalories + "</html>", JLabel.CENTER);
        
        JButton button1 = new JButton("Goal Calories");
        button1.addActionListener(e -> getInputFromField(2));
        
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

        

        bmiLabel = new JLabel("Make sure to set your weight and height on Page 1 first.", JLabel.CENTER);
        bmiLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JButton calcButton = new JButton("Calculate BMI");
        calcButton.addActionListener(e -> calculateBMI());
        
        
        JTextField walkField = new JTextField(5);
        JLabel walkResult = new JLabel("Calories burned: -");
        JButton walkButton = new JButton("Calculate Walk");
        walkButton.addActionListener(e -> {
        String input = walkField.getText().trim();
        if (input.isEmpty()) {
            walkResult.setText("Please enter a value.");
            return;
        }
        double km = Double.parseDouble(input);
        double calories = weight * km * 0.9;
        walkResult.setText(String.format("Calories burned: %.1f kcal", calories));
    });

        JTextField runField = new JTextField(5);
        JLabel runResult = new JLabel("Calories burned: -");
        JButton runButton = new JButton("Calculate Run");
        runButton.addActionListener(e -> {
        String input = runField.getText().trim();
        if (input.isEmpty()) {
            runResult.setText("Please enter a value.");
            return;
    }
        double km = Double.parseDouble(input);
        double calories = weight * km * 1.036;
        runResult.setText(String.format("Calories burned: %.1f kcal", calories));
});

        JTextField swimField = new JTextField(5);
        JLabel swimResult = new JLabel("Calories burned: -");
        JButton swimButton = new JButton("Calculate Swim");
        swimButton.addActionListener(e -> {
        String input = swimField.getText().trim();
        if (input.isEmpty()) {
            swimResult.setText("Please enter a value.");
            return;
    }
        double meters = Double.parseDouble(input);
        double calories = weight * (meters / 1000) * 0.95;
        swimResult.setText(String.format("Calories burned: %.1f kcal", calories));
});
        
        JPanel centerPanel = new JPanel();
        
        centerPanel.setBackground(new Color(255, 240, 240));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        bmiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        calcButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel walkPanel = new JPanel();
        walkPanel.setBackground(new Color(255, 240, 240));
        walkPanel.add(new JLabel("Walk (km):"));
        walkPanel.add(walkField);
        walkPanel.add(walkButton);
        walkPanel.add(walkResult);

        JPanel runPanel = new JPanel();
        runPanel.setBackground(new Color(255, 240, 240));
        runPanel.add(new JLabel("Run (km):"));
        runPanel.add(runField);
        runPanel.add(runButton);
        runPanel.add(runResult);

        JPanel swimPanel = new JPanel();
        swimPanel.setBackground(new Color(255, 240, 240));
        swimPanel.add(new JLabel("Swim (m):"));
        swimPanel.add(swimField);
        swimPanel.add(swimButton);
        swimPanel.add(swimResult);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(bmiLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(calcButton);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(walkPanel);
        centerPanel.add(runPanel);
        centerPanel.add(swimPanel);
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
            JOptionPane.showMessageDialog(this, "The text field is empty!", "Input Error", JOptionPane.WARNING_MESSAGE); 
            return;
        }
        if (index == 0) {
            weight = Double.parseDouble(input);
        }
        if (index == 1) {
            height = Integer.parseInt(input);
        }
        if (index == 2) {
            goalCalories = Double.parseDouble(input);
}
        label2.setText("<html>Weight (kg): " + weight + "<br>Height (cm): " + height + "<br>Goal Calories: " + goalCalories + "</html>");
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
