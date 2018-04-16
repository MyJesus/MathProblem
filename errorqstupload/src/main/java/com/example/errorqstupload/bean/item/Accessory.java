package com.example.errorqstupload.bean.item;

import java.util.ArrayList;

/**
 * Created by guh on 2017/8/1.
 */

public class Accessory {
    private int mType = 0;

    /**   显示方式    */
    private int mMode = 0;

    /**  内容  */
    private String mContent = null;

    /**   标签   */
    private String mLabel = null;

    /**   参考译文   */
    private String mTranslation = null;

    /**    语音    */
    private ArrayList<String> mSpeechs = new ArrayList<>();

    /**    选项    */
    private ArrayList<String> mOptions = new ArrayList<>();

    /**    核心词汇    */
    private ArrayList<String> mWords = new ArrayList<>();

    /**    单词翻译    */
    private ArrayList<String> mTransWords = new ArrayList<>();


    public int getType() {
        return mType;
    }

    public ArrayList<String> getOption() {
        return mOptions;
    }

    public void addOption(String option) {
        this.mOptions.add(option);
    }

    public ArrayList<String> getSpeech() {
        return mSpeechs;
    }

    public void addSpeech(String speech) {
        this.mSpeechs.add(speech);
    }

    public ArrayList<String> getWord() {
        return mWords;
    }

    public void addWord(String word) {
        this.mWords.add(word);
    }

    public ArrayList<String> getTransWord() {
        return mTransWords;
    }

    public void addTransWord(String transword) {
        this.mTransWords.add(transword);
    }

    public ArrayList<String> getOptions() {
        return mOptions;
    }

    public ArrayList<String> getSpeechs() {
        return mSpeechs;
    }

    public ArrayList<String> getTransWords() {
        return mTransWords;
    }

    public ArrayList<String> getWords() {
        return mWords;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }

    public int getMode() {
        return mMode;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setTranslation(String translation) {
        this.mTranslation = translation;
    }

    public String getTranslation() {
        return mTranslation;
    }
}
