package com.example.fyptest.Data_Creation_Class;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fyptest.R;
import com.example.fyptest.database.adminInfoClass;
import com.example.fyptest.database.faqClass;
import com.example.fyptest.database.orderStatusClass;
import com.example.fyptest.database.productType;
import com.example.fyptest.database.qtyConditionClass;
import com.example.fyptest.database.sellerInfoClass;
import com.example.fyptest.database.userClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class dbDataConstructor extends AppCompatActivity {

    EditText editText1, editText2, editText3, editText4,
            editText5, editText6, editText7, editText8,
            editText9, editText10;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_data_constructor);

        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        editText6 = (EditText) findViewById(R.id.editText6);
        editText7 = (EditText) findViewById(R.id.editText7);
        editText8 = (EditText) findViewById(R.id.editText8);
        editText9 = (EditText) findViewById(R.id.editText9);
        editText10 = (EditText) findViewById(R.id.editText10);


        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFAQ();
            }
        });
    }

    public  void addFAQ() {
        String question = editText9.getText().toString().trim();
        String answer = editText10.getText().toString().trim();

        DatabaseReference dbDAQ = FirebaseDatabase.getInstance().getReference("FAQ");
        String FAQID = dbDAQ.push().getKey();

        faqClass faqClass = new faqClass(FAQID, question, answer);
        dbDAQ.child(FAQID).setValue(faqClass);
    }

    public void addSeller() {
        DatabaseReference userDB, sellerInfoDB;
        userDB = FirebaseDatabase.getInstance().getReference("User");
        sellerInfoDB = FirebaseDatabase.getInstance().getReference("Seller Information");

        String userID = sellerInfoDB.push().getKey();

        String email = editText1.getText().toString();
        String pw = editText5.getText().toString();
        String contactNum = editText2.getText().toString();
        String fName = editText3.getText().toString();

        userClass userClass = new userClass(userID, email, pw, contactNum, "seller");
        sellerInfoClass sellerInfoClass = new sellerInfoClass(userID, fName, "seller");

        userDB.child(userID).setValue(userClass);
        sellerInfoDB.child(userID).setValue(sellerInfoClass);
    }

    public void addAdmin() {
        DatabaseReference userDB, adminInfoDB;
        userDB = FirebaseDatabase.getInstance().getReference("User");
        adminInfoDB = FirebaseDatabase.getInstance().getReference("Admin Information");

        String userID = adminInfoDB.push().getKey();

        String email = editText1.getText().toString();
        String pw = editText5.getText().toString();
        String contactNum = editText2.getText().toString();
        String fName = editText3.getText().toString();
        String lName = editText4.getText().toString();

        userClass userClass = new userClass(userID, email, pw, contactNum, "admin");
        adminInfoClass adminInfoClass = new adminInfoClass(userID, fName, lName, "admin");

        userDB.child(userID).setValue(userClass);
        adminInfoDB.child(userID).setValue(adminInfoClass);
    }

    public void addproductType(EditText productTypeName) {
        DatabaseReference databaseProductType = FirebaseDatabase.getInstance().getReference("Product Type");
        String name = productTypeName.getText().toString().trim();
        productTypeName.setText("");

        if (!TextUtils.isEmpty(name)) {
            String id = databaseProductType.push().getKey();

            productType productType = new productType(id, name);
            databaseProductType.child(id).setValue(productType);

            Toast.makeText(this, "Input Added", Toast.LENGTH_LONG).show();
        } else {
            productTypeName.setError("Field Empty");
        }
    }

    public void addorderStatus(EditText orderStatusName) {
        DatabaseReference databaseOrderStatus = FirebaseDatabase.getInstance().getReference("Order Status");
        String name = orderStatusName.getText().toString().trim();
        orderStatusName.setText("");

        if (!TextUtils.isEmpty(name)) {
            String id = databaseOrderStatus.push().getKey();

            orderStatusClass orderStatusClass = new orderStatusClass(id, name);
            databaseOrderStatus.child(id).setValue(orderStatusClass);

            Toast.makeText(this, "Input Added", Toast.LENGTH_LONG).show();
        } else {
            orderStatusName.setError("Field Empty");
        }
    }
}
