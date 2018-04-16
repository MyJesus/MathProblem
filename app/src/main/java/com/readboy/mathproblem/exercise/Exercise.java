package com.readboy.mathproblem.exercise;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oubin on 2017/9/21.
 */
@Deprecated
public class Exercise {
    private static final String TAG = "Exercise";

    public static final int INVALID_ANSWER = -1;

    private static final List<String> CHOICES = new ArrayList<>();

    static {
        CHOICES.add("A");
        CHOICES.add("B");
        CHOICES.add("C");
        CHOICES.add("D");
    }


    private String content;
    private List<String> options = new ArrayList<>();
    private String solution;
    //0-3
    private int correctAnswerIndex;
    private String correctAnswer;
    //-1 代表没有选择，选项0-3
    private int myAnswer = INVALID_ANSWER;
    private int projectId;
    private boolean showSolution = false;

    public Exercise() {
    }

    private Exercise(Builder builder) {
        this.content = builder.content;
        this.options = builder.options;
        this.solution = builder.solution;
        this.correctAnswerIndex = builder.correctAnswerIndex;
        this.correctAnswer = builder.correctAnswer;
        this.myAnswer = builder.myAnswer;
        this.projectId = builder.projectId;
        this.showSolution = builder.showSolution;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer.toUpperCase();
        this.correctAnswerIndex = CHOICES.indexOf(this.correctAnswer);
    }

    public void setCorrectAnswerIndex(int index) {
        this.correctAnswerIndex = index;
        this.correctAnswer = CHOICES.get(index);
    }

    public void setMyAnswer(int myAnswer) {
        this.myAnswer = myAnswer;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public boolean isShowSolution() {
        return showSolution;
    }

    public void setShowSolution(boolean showSolution) {
        this.showSolution = showSolution;
    }

    public void clear() {
        myAnswer = -1;
        showSolution = false;
    }

    public Builder newBuilder() {
        Builder builder = new Builder()
                .content(this.content)
                .options(this.options)
                .solution(this.solution)
                .correctAnswer(this.correctAnswer)
                .myAnswer(this.myAnswer)
                .projectId(projectId)
                .showSolution(showSolution);

        return builder;

    }

    public String getContent() {
        return content;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getSolution() {
        return solution;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public int getMyAnswer() {
        return myAnswer;
    }

    public int getProjectId() {
        return projectId;
    }


    public static class Builder {
        private String content;
        private List<String> options;
        private String solution;
        private int correctAnswerIndex;
        private String correctAnswer;
        private int myAnswer = INVALID_ANSWER;
        private int projectId;
        private boolean showSolution = false;

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder options(List<String> options) {
            this.options = options;
            return this;
        }

        public Builder solution(String solution) {
            this.solution = solution;
            return this;
        }

        public Builder correctAnswerIndex(int correctAnswerIndex) {
            this.correctAnswerIndex = correctAnswerIndex;
            this.correctAnswer = CHOICES.get(correctAnswerIndex);
            return this;
        }

        public Builder correctAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer.toUpperCase();
            this.correctAnswerIndex = CHOICES.indexOf(correctAnswer.toUpperCase());
            return this;
        }

        public Builder myAnswer(int myAnswer) {
            this.myAnswer = myAnswer;
            return this;
        }

        public Builder projectId(int projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder showSolution(boolean showSolution) {
            this.showSolution = showSolution;
            return this;
        }

        public Builder fromPrototype(Exercise prototype) {
            content = prototype.content;
            options = prototype.options;
            solution = prototype.solution;
            correctAnswerIndex = prototype.correctAnswerIndex;
            correctAnswer = prototype.correctAnswer;
            myAnswer = prototype.myAnswer;
            projectId = prototype.projectId;
            showSolution = prototype.showSolution;
            return this;
        }

        public Exercise build() {
            return new Exercise(this);
        }
    }
}
