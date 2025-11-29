import java.util.List;
import java.util.Scanner;

public class Quiz {
    private String quizName;
    private int timeLimitMinutes;
    private List<Question> questions;

    private Subject subject;


    public Quiz(Subject subject, String quizName, int timeLimitMinutes) {
        this.subject = subject;
        this.quizName = quizName;
        this.timeLimitMinutes = timeLimitMinutes;
        this.questions = subject.getQuestions();
    }

    //t
    public Quiz(Subject subject) {
        // We set default values so the code doesn't crash
        this(subject, subject.getSubjectName() + " Assessment", 30);
    }

    //methods

    public String getQuizName() {
        return quizName;
    }

    public int getTimeLimitMinutes() {
        return timeLimitMinutes;
    }


    public List<Question> getQuestion() {
        return questions;
    }

    public void addQuestion(Question q) {
        if (this.questions != null) {
            this.questions.add(q);
        }
    }


    public void startQuiz(Student student) throws EmptyAnswerException {
        Scanner sc = new Scanner(System.in);
        int score = 0;

        // Updated display to show the specific Quiz Name and Time Limit
        System.out.println("\n--- " + this.quizName + " ---");
        System.out.println("Subject: " + subject.getSubjectName());
        System.out.println("Time Limit: " + this.timeLimitMinutes + " minutes");
        System.out.println("-----------------------------");

        for (Question q : questions) {
            q.display(); // Polymorphic call
            System.out.print("Your Answer: ");
            String ans = sc.nextLine();

            if (ans.trim().isEmpty()) {
                throw new EmptyAnswerException("You cannot submit an empty answer!");
            }

            int answerIndex;
            try {
                // Assuming user types "1" for first choice, we convert to index 0 //
                answerIndex = Integer.parseInt(ans) - 1;
            } catch (NumberFormatException e) {
                // Re-throwing as a specific error message
                throw new NumberFormatException("Invalid input. Please enter a number.");
            }

            if (q.checkAnswer(answerIndex)) {
                score++;
            }
        }

        System.out.println("Quiz Finished! Score: " + score + "/" + questions.size());

        // Update student records
        student.setSubjectScore(subject.getSubjectName(), score);
    }
}


//old vers, keep for backup
//import java.util.List;
//import java.util.Scanner;
//
//public class Quiz {
//    private Subject subject;
//    private List<Question> questions;
//
//    public Quiz(Subject subject) {
//        this.subject = subject;
//        this.questions = subject.getQuestions();
//    }
//
//    public void startQuiz(Student student) throws EmptyAnswerException {
//        Scanner sc = new Scanner(System.in);
//        int score = 0;
//
//        System.out.println("Starting Quiz for: " + subject.getSubjectName());
//
//        for (Question q : questions) {
//            q.display(); // Polymorphic call
//            System.out.print("Your Answer: ");
//            String ans = sc.nextLine();
//
//            if (ans.trim().isEmpty()) {
//                throw new EmptyAnswerException("You cannot submit an empty answer!");
//            }
//
//            int answerIndex; // edited, Ligaray, 11282025
//            try{
//                answerIndex = Integer.parseInt(ans) - 1;
//            }catch(NumberFormatException e){
//                throw new NumberFormatException("Invalid input");
//            }
//
//            if (q.checkAnswer(answerIndex)) {
//                score++; // edited, Ligaray, 11282025
//            }
//        }
//
//        System.out.println("Quiz Finished! Score: " + score + "/" + questions.size());
//
//        student.setSubjectScore(subject.getSubjectName(), score);
//    }
//}