package com.example.errorqstupload.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.errorqstupload.bean.item.Ability;
import com.example.errorqstupload.bean.item.Accessory;
import com.example.errorqstupload.bean.item.KeyPoint;
import com.example.errorqstupload.bean.item.Material;
import com.example.errorqstupload.bean.item.Section;

import java.util.ArrayList;

/**
 * Created by guh on 2017/8/1.
 */

public class QuestionInfo implements Parcelable {

    public static final int CHOICE_QUESTION = 101; // 单选题 Radio examination  A multiple-choice question

    public static final int MULTIPLE_CHOICE_QUESTION = 102;//

    public static final int JUDGMENT_QUESTION = 103;//Judgment question

    public static final int COMPLETION_QUESTION = 201;

    public static final int SORTING_QUESTION = 301;

    public static final int MATCHING_QUESTION = 302;

    private String orgInfo;

    private int mId = 0;

    /**    类型：题型 面向程序   */
    private int mType = 0;

    /**    难度级别 ：1～10   */
    private int mDifficulty = 0;

    /**    分类   面向用户 */
    private int mCategory = 0;

    /**    题干    */
    private String mContent = null;

    /**    题目数据出处   */
    private int mFrom = 0;

    /**    课程    */
    private int mCourseId = 0;
    private int mGrade = 0;
    private int mSubject = 0;

    /**    资源质量    1：普通，默认值，2：良好， 3：优质*/
    private int mQuality = 0;

    /**    角色 ： 0：普通题，1:大小题中的大题，2：大小题中的小题*/
    private int mRole = 0;

    /**    关联题    */
    private ArrayList<Integer> mRelation = new ArrayList<Integer>();

    /**    显示答案   */
    private String mAnswer = null;

    /**    解析    */
    private String mSolution = null;

    private ArrayList<Material> mMaterial = new ArrayList<>();


    /**    状态 0：正常状态，1：标签未转换，2：上下文相关，不可抽题，3：题目被举报，4：未完成，仍需编辑    */
    private int mStatus = 0;

    /**    思路启发    */
    private String mIdea = null;

    /**    解题过程    */
    private String mStep = null;

    /**    归纳总结    */
    private String mSummary = null;

    /**    评价总结  0：其它，1：易错题，2：典型题，3：常考题，4：创新题， 5：压轴题*/
    private ArrayList<Integer> mOpinion = new ArrayList<>();

    /**    正确答案    */
    private ArrayList<String> mCorrectAnswer = new ArrayList<String>();

    /**  */
    private ArrayList<Ability> mAbility = new ArrayList<>();

    /**    知识点    */
    private ArrayList<KeyPoint> mKeyPoint = new ArrayList<KeyPoint>();

    /**    所属章节    */
    private ArrayList<Section> mSection = new ArrayList<Section>();

    /**    题干附加    */
    private ArrayList<Accessory> mAccessory = new ArrayList<Accessory>();

    /**    解析附加    */
    private ArrayList<Accessory> mSolutionAccessory = new ArrayList<Accessory>();

    public QuestionInfo() {
    }

    protected QuestionInfo(Parcel in) {
        mId = in.readInt();
        mType = in.readInt();
        mDifficulty = in.readInt();
        mCategory = in.readInt();
        mContent = in.readString();
        mFrom = in.readInt();
        mCourseId = in.readInt();
        mGrade = in.readInt();
        mSubject = in.readInt();
        mQuality = in.readInt();
        mRole = in.readInt();
        mAnswer = in.readString();
        mSolution = in.readString();
        mStatus = in.readInt();
        mIdea = in.readString();
        mStep = in.readString();
        mSummary = in.readString();
        mCorrectAnswer = in.createStringArrayList();
    }

    public static final Creator<QuestionInfo> CREATOR = new Creator<QuestionInfo>() {
        @Override
        public QuestionInfo createFromParcel(Parcel in) {
            return new QuestionInfo(in);
        }

        @Override
        public QuestionInfo[] newArray(int size) {
            return new QuestionInfo[size];
        }
    };

    public int getId() {
        return mId;
    }

    public ArrayList<Ability> getAbility() {
        return mAbility;
    }

    public ArrayList<KeyPoint> getKeyPoint() {
        return mKeyPoint;
    }

    public ArrayList<String> getCorrectAnswer() {
        return mCorrectAnswer;
    }

    public ArrayList<Section> getSection() {
        return mSection;
    }

    public int getType() {
        return mType;
    }

    public String getContent() {
        return mContent;
    }

    public int getStatus() {
        return mStatus;
    }

    public ArrayList<Accessory> getAccessory() {
        return mAccessory;
    }

    public ArrayList<Accessory> getSolutionAccessory() {
        return mSolutionAccessory;
    }

    public ArrayList<Integer> getOpinion() {
        return mOpinion;
    }

    public ArrayList<Integer> getmRelation() {
        return mRelation;
    }

    public ArrayList<Material> getMaterial() {
        return mMaterial;
    }

    public int getCategory() {
        return mCategory;
    }

    public int getCourseId() {
        return mCourseId;
    }

    public int getDifficulty() {
        return mDifficulty;
    }

    public int getFrom() {
        return mFrom;
    }

    public int getGrade() {
        return mGrade;
    }

    public int getQuality() {
        return mQuality;
    }

    public int getRole() {
        return mRole;
    }

    public int getSubject() {
        return mSubject;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public String getIdea() {
        return mIdea;
    }

    public String getSolution() {
        return mSolution;
    }

    public String getStep() {
        return mStep;
    }

    public String getSummary() {
        return mSummary;
    }

    public void addAbility(Ability ability) {
        this.mAbility.add(ability);
    }

    public void addSection(Section section) {
        this.mSection.add(section);
    }

    public void addKeyPoint(KeyPoint keypoint) {
        this.mKeyPoint.add(keypoint);
    }

    public void addCorrectAnswer(String correctAnswer) {
        this.mCorrectAnswer.add(correctAnswer);
    }

    public void addAccessory(Accessory accessory) {
        this.mAccessory.add(accessory);
    }

    public void setId(int id) {
        this.mId = id;
    }

    public void setAbility(ArrayList<Ability> ability) {
        this.mAbility = ability;
    }

    public void setAccessory(ArrayList<Accessory> accessory) {
        this.mAccessory = accessory;
    }

    public void setAnswer(String answer) {
        this.mAnswer = answer;
    }

    public void setCorrectAnswer(ArrayList<String> correctAnswer) {
        this.mCorrectAnswer = correctAnswer;
    }

    public void setCourseId(int courseId) {
        this.mCourseId = courseId;
    }

    public void setDifficulty(int difficulty) {
        this.mDifficulty = difficulty;
    }

    public void setFrom(int from) {
        this.mFrom = from;
    }

    public void setGrade(int grade) {
        this.mGrade = grade;
    }

    public void setIdea(String idea) {
        this.mIdea = idea;
    }

    public void setKeyPoint(ArrayList<KeyPoint> keyPoint) {
        this.mKeyPoint = keyPoint;
    }

    public void setMaterial(ArrayList<Material> material) {
        this.mMaterial = material;
    }

    public void setOpinion(ArrayList<Integer> opinion) {
        this.mOpinion = opinion;
    }

    public void setQuality(int quality) {
        this.mQuality = quality;
    }

    public void setRelation(ArrayList<Integer> relation) {
        this.mRelation = relation;
    }

    public void setRole(int role) {
        this.mRole = role;
    }

    public void setSection(ArrayList<Section> section) {
        this.mSection = section;
    }

    public void setSolution(String solution) {
        this.mSolution = solution;
    }

    public void setSolutionAccessory(ArrayList<Accessory> solutionAccessory) {
        this.mSolutionAccessory = solutionAccessory;
    }

    public void setStep(String step) {
        this.mStep = step;
    }

    public void setSubject(int subject) {
        this.mSubject = subject;
    }

    public void setSummary(String summary) {
        this.mSummary = summary;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public void setCategory(int category) {
        this.mCategory = category;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeInt(mType);
        parcel.writeInt(mDifficulty);
        parcel.writeInt(mCategory);
        parcel.writeString(mContent);
        parcel.writeInt(mFrom);
        parcel.writeInt(mCourseId);
        parcel.writeInt(mGrade);
        parcel.writeInt(mSubject);
        parcel.writeInt(mQuality);
        parcel.writeInt(mRole);
        parcel.writeString(mAnswer);
        parcel.writeString(mSolution);
        parcel.writeInt(mStatus);
        parcel.writeString(mIdea);
        parcel.writeString(mStep);
        parcel.writeString(mSummary);
        parcel.writeStringList(mCorrectAnswer);
    }

    public String getOrgInfo() {
        return orgInfo;
    }

    public void setOrgInfo(String orgInfo) {
        this.orgInfo = orgInfo;
    }
}
