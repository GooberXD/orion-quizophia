import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class Quizophia extends JFrame {

    // Core Components
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private FileHandler fileHandler;
    private List<Subject> loadedSubjects;

    // User Data
    private Teacher currentTeacher;
    private Student currentStudent;

    public Quizophia() {
        fileHandler = new FileHandler();
        loadedSubjects = fileHandler.loadSubjects();

        // Frame Settings
        setTitle("The Quiz Master - Capstone Project");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout Manager (CardLayout allows swapping screens)
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add Screens (Cards)
        mainPanel.add(createLoginPanel(), "Login");
        mainPanel.add(createTeacherDashboard(), "Teacher");

        // Initialize Frame
        add(mainPanel);
        setVisible(true);
    }

    // =====================================================
    // 1. LOGIN SCREEN
    // =====================================================
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel title = new JLabel("THE QUIZ MASTER", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder("Enter Name"));

        JButton btnTeacher = new JButton("Login as Teacher");
        JButton btnStudent = new JButton("Login as Student");

        // Teacher Login Action
        btnTeacher.addActionListener(e -> {
            if(nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
                return;
            }
            currentTeacher = new Teacher("T" + System.currentTimeMillis(), nameField.getText());
            fileHandler.saveUser(currentTeacher); // Save log
            cardLayout.show(mainPanel, "Teacher");
        });

        // Student Login Action
        btnStudent.addActionListener(e -> {
            if(nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
                return;
            }
            currentStudent = new Student("S" + System.currentTimeMillis(), nameField.getText());
            fileHandler.saveUser(currentStudent); // Save log

            // Refresh subjects before showing student panel
            loadedSubjects = fileHandler.loadSubjects();
            mainPanel.add(createStudentDashboard(), "Student"); // Re-create to get fresh data
            cardLayout.show(mainPanel, "Student");
        });

        panel.add(title);
        panel.add(nameField);
        panel.add(btnTeacher);
        panel.add(btnStudent);

        return panel;
    }

    // =====================================================
    // 2. TEACHER DASHBOARD (Add Questions)
    // =====================================================
    private JPanel createTeacherDashboard() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Question"));

        JTextField txtSubject = new JTextField();
        JTextField txtQuestion = new JTextField();
        JTextField txtOpt1 = new JTextField();
        JTextField txtOpt2 = new JTextField();
        JTextField txtOpt3 = new JTextField();
        JTextField txtOpt4 = new JTextField();

        // Combo box to select which option is correct
        String[] correctOpts = {"Option 1", "Option 2", "Option 3", "Option 4"};
        JComboBox<String> cmbCorrect = new JComboBox<>(correctOpts);

        formPanel.add(new JLabel("Subject Name:")); formPanel.add(txtSubject);
        formPanel.add(new JLabel("Question Text:")); formPanel.add(txtQuestion);
        formPanel.add(new JLabel("Option 1:")); formPanel.add(txtOpt1);
        formPanel.add(new JLabel("Option 2:")); formPanel.add(txtOpt2);
        formPanel.add(new JLabel("Option 3:")); formPanel.add(txtOpt3);
        formPanel.add(new JLabel("Option 4:")); formPanel.add(txtOpt4);
        formPanel.add(new JLabel("Correct Answer:")); formPanel.add(cmbCorrect);

        JButton btnSave = new JButton("Save Question to File");
        JButton btnLogout = new JButton("Logout");

        btnSave.addActionListener(e -> {
            // 1. Create the object
            String[] choices = {txtOpt1.getText(), txtOpt2.getText(), txtOpt3.getText(), txtOpt4.getText()};

            // Determine actual string of correct answer based on selection
            int selectedIndex = cmbCorrect.getSelectedIndex(); // 0 to 3
//            String actualAnswer = choices[selectedIndex]; // edited, Ligaray, 11282025

            MultipleChoiceQuestion q = new MultipleChoiceQuestion(
//                    txtQuestion.getText(), actualAnswer, choices
                    txtQuestion.getText(), selectedIndex, choices // edited, Ligaray, 11282025
            );

            Subject sub = new Subject(txtSubject.getText());
            sub.addQuestion(q);

            // 2. Save using logic
            List<Subject> temp = new ArrayList<>();
            temp.add(sub);
            fileHandler.saveSubjects(temp);

            JOptionPane.showMessageDialog(this, "Question Saved Successfully!");

            // Clear fields
            txtQuestion.setText("");
            txtOpt1.setText(""); txtOpt2.setText(""); txtOpt3.setText(""); txtOpt4.setText("");
        });

        btnLogout.addActionListener(e -> cardLayout.show(mainPanel, "Login"));

        JPanel botPanel = new JPanel();
        botPanel.add(btnSave);
        botPanel.add(btnLogout);

        panel.add(new JLabel("Teacher Dashboard", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(botPanel, BorderLayout.SOUTH);

        return panel;
    }

    // =====================================================
    // 3. STUDENT DASHBOARD (Select Subject)
    // =====================================================
    private JPanel createStudentDashboard() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lblWelcome = new JLabel("Welcome, " + currentStudent.getName() + ". Choose a Subject:", SwingConstants.CENTER);

        JPanel listPanel = new JPanel(new GridLayout(0, 1, 10, 10));

        if (loadedSubjects.isEmpty()) {
            listPanel.add(new JLabel("No subjects available. Ask teacher to add some."));
        } else {
            for (Subject sub : loadedSubjects) {
                JButton btnSub = new JButton(sub.getSubjectName());
                btnSub.addActionListener(e -> {
                    // Start Quiz for this subject
                    mainPanel.add(createQuizTakingPanel(sub), "Quiz");
                    cardLayout.show(mainPanel, "Quiz");
                });
                listPanel.add(btnSub);
            }
        }

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> cardLayout.show(mainPanel, "Login"));

        panel.add(lblWelcome, BorderLayout.NORTH);
        panel.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        panel.add(btnLogout, BorderLayout.SOUTH);

        return panel;
    }

    // =====================================================
    // 4. QUIZ TAKING PANEL (The actual game)
    // =====================================================
    private JPanel createQuizTakingPanel(Subject subject) {
        JPanel panel = new JPanel(new BorderLayout());

        List<Question> questions = subject.getQuestions();
        final int[] currentQIndex = {0};
        final int[] score = {0};

        JLabel lblQInfo = new JLabel("Question 1 of " + questions.size(), SwingConstants.CENTER);
        JTextArea txtQuestionDisplay = new JTextArea();
        txtQuestionDisplay.setEditable(false);
        txtQuestionDisplay.setLineWrap(true);
        txtQuestionDisplay.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1));
        ButtonGroup bg = new ButtonGroup();
        JRadioButton r1 = new JRadioButton();
        JRadioButton r2 = new JRadioButton();
        JRadioButton r3 = new JRadioButton();
        JRadioButton r4 = new JRadioButton();

        bg.add(r1); bg.add(r2); bg.add(r3); bg.add(r4);
        optionsPanel.add(r1); optionsPanel.add(r2); optionsPanel.add(r3); optionsPanel.add(r4);

        JButton btnNext = new JButton("Next Question");

        // Helper to load question to UI
        Runnable loadQuestion = () -> {
            if (currentQIndex[0] < questions.size()) {
                MultipleChoiceQuestion q = (MultipleChoiceQuestion) questions.get(currentQIndex[0]);
                lblQInfo.setText("Question " + (currentQIndex[0] + 1) + " of " + questions.size());
                txtQuestionDisplay.setText(q.getQuestionText());

                String[] opts = q.getChoices();
                r1.setText(opts[0]); r1.setActionCommand(opts[0]);
                r2.setText(opts[1]); r2.setActionCommand(opts[1]);
                r3.setText(opts[2]); r3.setActionCommand(opts[2]);
                r4.setText(opts[3]); r4.setActionCommand(opts[3]);

                bg.clearSelection();
            } else {
                // Quiz Finished
                fileHandler.saveScore(currentStudent.getId(), subject.getSubjectName(), score[0]);
                JOptionPane.showMessageDialog(this, "Quiz Finished!\nYour Score: " + score[0] + " / " + questions.size());
                cardLayout.show(mainPanel, "Student");
            }
        };

        // Initial load
        loadQuestion.run();

        btnNext.addActionListener(e -> {
            // EXCEPTION HANDLING DEMONSTRATION
            try {
                if (bg.getSelection() == null) {
                    throw new EmptyAnswerException("You must select an answer before proceeding!");
                }

                // Check answer
                String selectedAnswer = bg.getSelection().getActionCommand();
                Question currentQ = questions.get(currentQIndex[0]);

                if (currentQ.checkAnswer(selectedAnswer)) {
                    score[0]++;
                }

                currentQIndex[0]++;
                loadQuestion.run();

            } catch (EmptyAnswerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(lblQInfo, BorderLayout.NORTH);
        panel.add(txtQuestionDisplay, BorderLayout.CENTER);

        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(optionsPanel, BorderLayout.CENTER);
        southContainer.add(btnNext, BorderLayout.SOUTH);

        panel.add(southContainer, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        new Quizophia();
    }
}