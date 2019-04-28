package com.example.fyptest.database;

import java.util.Date;

public class productGroupClass {
    private String pg_pro_ID;
    private String pg_dateEnd;
    private String string_pgDateCreated;

    public productGroupClass() {

    }

    public productGroupClass(String pg_pro_ID, String pg_dateEnd, String string_pgDateCreated) {
        this.pg_pro_ID = pg_pro_ID;
        this.pg_dateEnd = pg_dateEnd;
        this.string_pgDateCreated = string_pgDateCreated;
    }

    public String getPg_pro_ID() {
        return pg_pro_ID;
    }

    public String getPg_dateEnd() {
        return pg_dateEnd;
    }

    public String getString_pgDateCreated() {
        return string_pgDateCreated;
    }
}
