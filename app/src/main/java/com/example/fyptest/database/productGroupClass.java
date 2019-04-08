package com.example.fyptest.database;

import java.util.Date;

public class productGroupClass {
    private String pg_ID;
    private Date pg_dateCreated;
    private Date pg_dateEnd;
    private String pg_pro_ID;

    public productGroupClass() {

    }

    public productGroupClass(String pg_ID, Date pg_dateCreated, Date pg_dateEnd, String pg_pro_ID) {
        this.pg_ID = pg_ID;
        this.pg_dateCreated = pg_dateCreated;
        this.pg_dateEnd = pg_dateEnd;
        this.pg_pro_ID = pg_pro_ID;
    }

    public String getPg_ID() {
        return pg_ID;
    }

    public Date getPg_dateCreated() {
        return pg_dateCreated;
    }

    public Date getPg_dateEnd() {
        return pg_dateEnd;
    }

    public String getPg_pro_ID() {
        return pg_pro_ID;
    }
}
