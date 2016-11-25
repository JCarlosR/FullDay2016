package com.youtube.sorcjc.fullday2016.io.response;

import com.google.gson.annotations.SerializedName;
import com.youtube.sorcjc.fullday2016.model.Survey;

import java.util.ArrayList;

/**
 * Created by pc on 21/11/2016.
 */

public class SurveyResponse {
    @SerializedName("questions")
    public ArrayList<Survey> survey;

    public ArrayList<Survey> getSurvey() {
        return survey;
    }

    public void setSurvey(ArrayList<Survey> survey) {
        this.survey = survey;
    }

}
