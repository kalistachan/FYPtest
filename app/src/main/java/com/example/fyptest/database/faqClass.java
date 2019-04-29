package com.example.fyptest.database;

public class faqClass {
    private String faq_ID;
    private String faq_Question;
    private String faq_Answer;

    public faqClass() {

    }

    public faqClass(String faq_ID, String faq_Question, String faq_Answer) {
        this.faq_ID = faq_ID;
        this.faq_Question = faq_Question;
        this.faq_Answer = faq_Answer;
    }

    public String getFaq_ID() {
        return faq_ID;
    }

    public String getFaq_Question() {
        return faq_Question;
    }

    public String getFaq_Answer() {
        return faq_Answer;
    }
}
