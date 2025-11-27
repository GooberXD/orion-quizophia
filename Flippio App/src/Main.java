import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FileHandler fileHandler = new FileHandler();
        Scanner sc = new Scanner(System.in);

        // 1. Load Data on Startup
        System.out.println("Loading system data...");
        List<User> users = fileHandler.loadUsers(); // edited, Ligaray, 11282025
        List<Subject> subjects = fileHandler.loadSubjects();

        // Temporary: If no users exist, create a default Teacher
        if (users.isEmpty()) {
            Teacher t = new Teacher("T01", "Mr. Admin");
            users.add(t);
            fileHandler.saveUser(t); // Write to file
            System.out.println("Default Teacher created.");
        }

        // 2. Simple Login Simulation
        System.out.println("--- QUIZ MASTER SYSTEM ---");
        System.out.println("1. Teacher Login");
        System.out.println("2. Student Login");
        int choice = sc.nextInt();
        sc.nextLine(); // Consume newline

        if (choice == 1) {
            // Teacher Mode: Add Questions
            Teacher currentTeacher = (Teacher) users.get(0); // Just taking first user for demo
            System.out.println("Welcome " + currentTeacher.getName());

            Subject math = new Subject("Math");
            String[] choices = {"4", "5", "2", "10"};
//            Question q1 = new MultipleChoiceQuestion("2+2=?", "4", choices);
            Question q1 = new MultipleChoiceQuestion("2+2=?", 0, choices); // edited, Ligaray, 11282025
            math.addQuestion(q1);
            currentTeacher.addSubject(math);

            // SAVE to file
            fileHandler.saveSubjects(currentTeacher.getSubjects());

        } else {
            // Student Mode: Take Quiz
            if (subjects.isEmpty()) {
                System.out.println("No subjects loaded from file.");
            } else {
                Student student = new Student("S01", "Student One");
                Subject selectedSubject = subjects.get(0); // Pick first subject

                Quiz quiz = new Quiz(selectedSubject);
                try {
                    quiz.startQuiz(student); // Student takes quiz

                    // Get the score derived in the quiz
                    // (You might need to adjust startQuiz to return int, or get last score from student)
                    int lastScore = student.getScores().get(student.getScores().size()-1).getScore();

                    // SAVE Score to file
                    fileHandler.saveScore(student.getId(), selectedSubject.getSubjectName(), lastScore);
                    System.out.println("Score saved to file!");

                } catch (EmptyAnswerException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}