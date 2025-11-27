public class MultipleChoiceQuestion extends Question {
    private final String[] choices;

    public MultipleChoiceQuestion(String text, int answer, String[] choices) {
        super(text, answer); // edited, Ligaray, 11282025
        this.choices = choices;
    }

    @Override
    public void display() {
        System.out.println("Q: " + questionText);
        for (int i = 0; i < choices.length; i++) {
            System.out.println((i + 1) + ". " + choices[i]);
        }
    }

    public String[] getChoices() {
        return choices;
    }
}