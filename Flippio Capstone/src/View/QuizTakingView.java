package View;

import javax.swing.*;
import java.awt.*;

public class QuizTakingView extends JFrame {
    // Components
    private JLabel questionLabel;
    private JRadioButton[] options;
    private ButtonGroup optionsGroup;
    private JButton btnPrev, btnNext, btnSubmit;

    public QuizTakingView() {
        // Window Setup
        setTitle("Flippio - Quiz");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Add some padding
        setLocationRelativeTo(null);

        // 1. TOP PANEL: Question Display
        JPanel questionPanel = new JPanel();
        questionLabel = new JLabel("Question 1: Placeholder text?");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionPanel.add(questionLabel);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(questionPanel, BorderLayout.NORTH);

        // 2. CENTER PANEL: Options (Radio Buttons)
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(4, 1, 5, 5)); // 4 rows for 4 choices
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        options = new JRadioButton[4];
        optionsGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton("Option " + (i + 1));
            optionsGroup.add(options[i]); // Group them so only one can be selected
            optionsPanel.add(options[i]);
        }
        add(optionsPanel, BorderLayout.CENTER);

        // 3. BOTTOM PANEL: Navigation Buttons
        JPanel navPanel = new JPanel();
        btnPrev = new JButton("Prev"); // [cite: 48]
        btnNext = new JButton("Next"); // [cite: 49]
        btnSubmit = new JButton("Submit"); // [cite: 54]

        // Styling buttons based on prototype
        btnSubmit.setBackground(Color.decode("#9146FF")); // Approximate Flippio purple
        btnSubmit.setForeground(Color.WHITE);

        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        navPanel.add(btnSubmit);
        add(navPanel, BorderLayout.SOUTH);
    }

    // Setters for the Controller to update the view
    public void setQuestionText(String text) {
        questionLabel.setText("<html><body style='width: 400px'>" + text + "</body></html>"); // Wrap text
    }

    public void setOptions(String[] choices) {
        for (int i = 0; i < choices.length; i++) {
            options[i].setText(choices[i]);
            options[i].setSelected(false); // Reset selection
        }
    }

    // Returns the index of the selected radio button (0-3), or -1 if none
    public int getSelectedOptionIndex() {
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSelected()) {
                return i;
            }
        }
        return -1;
    }

    // Sets the selected radio button programmatically (for navigating back)
    public void setSelectedOption(int index) {
        if (index >= 0 && index < options.length) {
            options[index].setSelected(true);
        }
    }

    // Getters for buttons (for the Controller)
    public JButton getBtnNext() { return btnNext; }
    public JButton getBtnPrev() { return btnPrev; }
    public JButton getBtnSubmit() { return btnSubmit; }
}
