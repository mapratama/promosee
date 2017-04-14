package com.android.promosee.models;

import com.android.promosee.core.ParseJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Faq extends RealmObject {
    @PrimaryKey
    private int id;

    private String question, answer;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public static void fromJSONArray(JSONArray response) throws JSONException {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < response.length(); i++)
            fromJSON(response.getJSONObject(i), realm);
        realm.commitTransaction();
    }

    public static Faq fromJSON(JSONObject response, Realm realm) throws JSONException {
        Faq faq = new Faq();
        faq.setId(ParseJSON.getInt(response.getString("id")));
        faq.setQuestion(response.optString("question"));
        faq.setAnswer(response.optString("answer"));
        realm.copyToRealmOrUpdate(faq);

        return faq;
    }

    public static Faq get(int id) {
        return Realm.getDefaultInstance().where(Faq.class).equalTo("id", id).findFirst();
    }
}

