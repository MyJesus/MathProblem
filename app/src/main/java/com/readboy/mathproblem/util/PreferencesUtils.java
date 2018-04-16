package com.readboy.mathproblem.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.application.SubjectType;

/**
 * Created by oubin on 2017/10/13.
 */

public final class PreferencesUtils {

    private static final String KEY_GRADE = "grade";
    private static final String KEY_SUBJECT = "subject";

    private PreferencesUtils() {
    }

    public static void saveGrade(int grade) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(MathApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_GRADE, grade);
        editor.apply();
    }

    public static int getGrade(int defaultGrade) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(MathApplication.getInstance());
        return preferences.getInt(KEY_GRADE, defaultGrade);
    }

    public static void saveSubject(SubjectType type) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(MathApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_SUBJECT, type.ordinal());
        editor.apply();
    }

    public static SubjectType getSubject(SubjectType type) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(MathApplication.getInstance());
        return SubjectType.valueOf(preferences.getInt(KEY_SUBJECT, type.ordinal()));
    }

}
