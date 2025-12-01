package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CreateQuizView extends JFrame {
    // Inputs
    private JTextField quizTitleField;
    private JTextArea questionTextField;
    private JTextField[] optionFields;
    private JComboBox<String> correctAnsBox;

    // Actions
    private JButton addQuestionBtn;
    private JButton saveQuizBtn;
    private JButton cancelBtn;

    // Display
    private JTextArea addedQuestionsPreview;

    public CreateQuizView() {
        setTitle("Flippio - Create New Quiz");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // 1. TOP: Quiz Details
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Quiz Details"));
        topPanel.add(new JLabel("Quiz Title: "), BorderLayout.WEST);
        quizTitleField = new JTextField();
        topPanel.add(quizTitleField, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 2. CENTER: Question Entry Form
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Add Questions"));

        // Question Text
        centerPanel.add(new JLabel("Question Text:"));
        questionTextField = new JTextArea(3, 20);
        questionTextField.setLineWrap(true);
        centerPanel.add(new JScrollPane(questionTextField));
        centerPanel.add(Box.createVerticalStrut(10));

        // Options (1-4)
        optionFields = new JTextField[4];
        JPanel optionsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        for (int i = 0; i < 4; i++) {
            optionsPanel.add(new JLabel("Option " + (i + 1) + ":"));
            optionFields[i] = new JTextField();
            optionsPanel.add(optionFields[i]);
        }
        centerPanel.add(optionsPanel);
        centerPanel.add(Box.createVerticalStrut(10));

        // Correct Answer Selection
        JPanel ansPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ansPanel.add(new JLabel("Correct Answer:"));
        String[] indices = {"Option 1", "Option 2", "Option 3", "Option 4"};
        correctAnsBox = new JComboBox<>(indices);
        ansPanel.add(correctAnsBox);
        centerPanel.add(ansPanel);

        // Add Button
        addQuestionBtn = new JButton("Add Question to List");
        centerPanel.add(addQuestionBtn);

        // Preview Area
        centerPanel.add(new JLabel("Added Questions Preview:"));
        addedQuestionsPreview = new JTextArea(5, 20);
        addedQuestionsPreview.setEditable(false);
        centerPanel.add(new JScrollPane(addedQuestionsPreview));

        add(centerPanel, BorderLayout.CENTER);

        // 3. BOTTOM: Main Actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelBtn = new JButton("Cancel");
        saveQuizBtn = new JButton("Save Quiz");
        saveQuizBtn.setBackground(Color.decode("#9146FF"));
        saveQuizBtn.setForeground(Color.WHITE);

        bottomPanel.add(cancelBtn);
        bottomPanel.add(saveQuizBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Getters for inputs
    public String getQuizTitle() { return quizTitleField.getText().trim(); }
    public String getQuestionText() { return questionTextField.getText().trim(); }
    public String getOption(int index) { return optionFields[index].getText().trim(); }
    public int getSelectedCorrectIndex() { return correctAnsBox.getSelectedIndex(); }

    // Logic Helpers
    public void clearQuestionInputs() {
        questionTextField.setText("");
        for (JTextField f : optionFields) f.setText("");
        correctAnsBox.setSelectedIndex(0);
        questionTextField.requestFocus();
    }

    public void appendToPreview(String text) {
        addedQuestionsPreview.append(text + "\n");
    }

    // Listeners
    public void addAddQuestionListener(ActionListener l) { addQuestionBtn.addActionListener(l); }
    public void addSaveQuizListener(ActionListener l) { saveQuizBtn.addActionListener(l); }
    public void addCancelListener(ActionListener l) { cancelBtn.addActionListener(l); }
}

