package com.example.fyptest.database;

import java.util.Date;

public class productGroupClass {
    private String pg_pro_ID;
    private String pg_dateEnd;
    private String pg_dateCreated;

    public productGroupClass() {

    }

    public productGroupClass(String pg_pro_ID, String pg_dateEnd, String pg_dateCreated) {
        this.pg_pro_ID = pg_pro_ID;
        this.pg_dateEnd = pg_dateEnd;
        this.pg_dateCreated = pg_dateCreated;
    }

    public String getPg_pro_ID() {
        return pg_pro_ID;
    }

    public String getPg_dateEnd() {
        return pg_dateEnd;
    }

    public String getPg_dateCreated() {
        return pg_dateCreated;
    }
}
