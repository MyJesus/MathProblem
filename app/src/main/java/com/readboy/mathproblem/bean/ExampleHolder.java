package com.readboy.mathproblem.bean;

/**
 * Created by oubin on 2017/9/27.
 */

public class ExampleHolder {

    private String content;
    private String solution;
    private String answer;
    private boolean isCheckedSolution;
    private boolean isCheckedAnswer;

    public ExampleHolder(String content, String solution, String answer, boolean isCheckedSolution, boolean isCheckedAnswer) {
        this.content = content;
        this.solution = solution;
        this.answer = answer;
        this.isCheckedSolution = isCheckedSolution;
        this.isCheckedAnswer = isCheckedAnswer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isCheckedSolution() {
        return isCheckedSolution;
    }

    public void setCheckedSolution(boolean checkedSolution) {
        isCheckedSolution = checkedSolution;
    }

    public boolean isCheckedAnswer() {
        return isCheckedAnswer;
    }

    public void setCheckedAnswer(boolean checkedAnswer) {
        isCheckedAnswer = checkedAnswer;
    }
}
