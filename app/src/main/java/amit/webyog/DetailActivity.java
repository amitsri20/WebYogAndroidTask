package amit.webyog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import amit.webyog.Api.ApiClient;
import amit.webyog.Api.WebyogApi;
import amit.webyog.model.EmailDetails;
import amit.webyog.model.Participants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    TextView emailBody;
    TextView emailParticipents;
    TextView emailSubject;
    TextView emailTime;
    ImageView emailStarred;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        emailBody =(TextView) findViewById(R.id.emailbody);
        emailParticipents = (TextView) findViewById(R.id.email_participents);
        emailSubject = (TextView) findViewById(R.id.email_subject);
        emailTime = (TextView) findViewById(R.id.email_time);
        emailStarred = (ImageView) findViewById(R.id.email_star);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        String id = extras.getString("EMAILID");
        if (id != null) {
//            Toast.makeText(this, "Email will be fetched from id:"+id, Toast.LENGTH_SHORT).show();
            loadEmail(id);
        }

    }

    public void loadEmail(String id) {
            WebyogApi apiService =
                    ApiClient.getClient().create(WebyogApi.class);

            Call<EmailDetails> call = apiService.getEmailDetails(id);
            call.enqueue(new Callback<EmailDetails>() {

                @Override
                public void onResponse(Call<EmailDetails> call, Response<EmailDetails> response) {
                    try {
                        EmailDetails emailDetails = response.body();

                        emailParticipents.setText(getParticipents(emailDetails.getParticipants()));
                        Log.d("Subject: ",emailDetails.getSubject());
                        emailSubject.setText(emailDetails.getSubject());
                        emailTime.setText(getTime(emailDetails.getTs()));
                        if(emailDetails.getIsStarred().equals("true")) {
                            emailStarred.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_18dp));
                        }
                        else
                        {
                            emailStarred.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_black_18dp));
                        }
                        emailBody.setText(response.body().getBody());

                    } catch (Exception e) {
                        Log.d("onResponse", "There is an error");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<EmailDetails> call, Throwable t) {

                }


            });
    }

    private String getParticipents(List<Participants> participants) {
        StringBuffer allP=new StringBuffer();
        for(Participants p:participants)
        {
            allP.append(String.format(p.getName()+"("+p.getEmail()+"), "));
        }
        return allP.toString().replaceAll(", $", "");
    }

    private String getTime(String ts) {
        long dv = Long.valueOf(ts)*1000;
        Date df = new java.util.Date(dv);
        String time = new SimpleDateFormat("MMMM dd, yyyy hh:mma").format(df);
        return time;
    }
}
