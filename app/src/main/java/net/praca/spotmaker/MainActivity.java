package net.praca.spotmaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Pattern PASSWORD_PATTERN =  Pattern.compile("^" +
            //"(?=.*[0-9])" +         //minimum jedna cyfra
            //"(?=.*[a-z])" +         //minimum jedna mala litera
            //"(?=.*[A-Z])" +         //minimum jedna duza litera
            "(?=.*[a-zA-Z])" +      //jakakolwiek litera
          //  "(?=.*[@#$%^&+=])" +    //jeden znak specjalny minimum
            "(?=\\S+$)" +           //bez spacji
            ".{4,}" +               //przynajmniej 4 znaki
            "$");
    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonRegister;
    private ProgressDialog progressDialog;

    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewLogin = (TextView) findViewById(R.id.textViewLogin);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        progressDialog = new ProgressDialog(this);

        buttonRegister.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
    }

    private boolean emailValidation(){
        String emailInput = editTextEmail.getText().toString().trim();

        if(emailInput.isEmpty()){
            editTextEmail.setError("Pole e-mail nie moze byc puste!");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            editTextEmail.setError("Prosze podać prawidłowy adres email!");
            return false;
        }
        else {
            editTextEmail.setError(null);
            return true;
            }
        }

    private boolean usernameValidation() {
        String usernameInput = editTextUsername.getText().toString().trim();

        if (usernameInput.isEmpty()) {
            editTextUsername.setError("Pole nazwa użytkownika nie moze byc puste!");
            return false;
        }
        else{
            editTextPassword.setError(null);
            return true;
        }
    }

    private boolean passwordValidation() {
        String passwordInput = editTextPassword.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            editTextPassword.setError("Pole e-mail nie moze byc puste!");
            return false;
        } else if(!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            editTextPassword.setError("Hasło jest zbyt słabe");
            return false;
        }
        else{
            editTextPassword.setError(null);
            return true;
        }
    }
    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        progressDialog.setMessage("Trwa rejestrowanie nowego użytkownika");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };


        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);


    }

    @Override
    public void onClick(View view) {
        if(view == textViewLogin)
            startActivity(new Intent(this, LoginActivity.class));

        if(emailValidation() && passwordValidation() && usernameValidation() && view == buttonRegister){
            registerUser();
        }
    }
}
