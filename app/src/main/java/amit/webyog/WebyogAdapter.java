package amit.webyog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import amit.webyog.Api.ApiClient;
import amit.webyog.Api.WebyogApi;
import amit.webyog.model.Email;
import retrofit2.Response;

/**
 * Created by amit on 9/25/2016.
 */

public class WebyogAdapter extends RecyclerView.Adapter<WebyogAdapter.ViewHolder> {

        private List<Email> emails;
        private int rowLayout;
        private Context context;
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    List<Email> itemsPendingRemoval;
    int lastInsertedIndex; // so we can add some more items for testing purposes
    boolean undoOn; // is undo on, you can turn it on from the toolbar menu

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<Email, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    public static class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout emailLayout;
            TextView emailSubject;
            TextView emailPreview;
            TextView emailTime;
            ImageView emailStarred;
            TextView emailRead;
            Button undoButton;

            public ViewHolder(View v) {
                super(v);
                emailLayout = (LinearLayout) v.findViewById(R.id.container_layout);
                emailSubject = (TextView) v.findViewById(R.id.email_subject);
                emailPreview = (TextView) v.findViewById(R.id.email_preview);
                emailTime = (TextView) v.findViewById(R.id.email_time);
                emailStarred = (ImageView) v.findViewById(R.id.email_star);
                emailRead = (TextView) v.findViewById(R.id.email_read);
                undoButton = (Button) itemView.findViewById(R.id.undo_button);
            }
        }

        public WebyogAdapter(List<Email> emails, int rowLayout, Context context) {
            this.emails = emails;
            this.rowLayout = rowLayout;
            this.context = context;
            itemsPendingRemoval = new ArrayList<>();
        }

        @Override
        public WebyogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {


            final Email item = emails.get(position);

            if (itemsPendingRemoval.contains(item)) {
                // we need to show the "undo" state of the row
                holder.itemView.setBackgroundColor(Color.RED);
                holder.emailLayout.setVisibility(View.GONE);
                holder.undoButton.setVisibility(View.VISIBLE);
                holder.undoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // user wants to undo the removal, let's cancel the pending task
                        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                        pendingRunnables.remove(item);
                        if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
                        itemsPendingRemoval.remove(item);
                        // this will rebind the row in "normal" state
                        notifyItemChanged(emails.indexOf(item));
                    }
                });
            } else {
                // we need to show the "normal" state
                holder.itemView.setBackgroundColor(Color.WHITE);
                holder.emailLayout.setVisibility(View.VISIBLE);
                holder.undoButton.setVisibility(View.GONE);
                holder.undoButton.setOnClickListener(null);

                holder.emailSubject.setText(emails.get(position).getSubject());
                holder.emailPreview.setText(emails.get(position).getPreview());
                holder.emailTime.setText(getTime(emails.get(position).getTs()));
                if(emails.get(position).getIsStarred().equals("true")) {
                    holder.emailStarred.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_black_18dp));
                }
                else
                {
                    holder.emailStarred.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_black_18dp));
                }
                if(emails.get(position).getIsRead().equals("true")) {
                    holder.emailRead.setText("");
                }
                else
                {
                holder.emailRead.setText("New");
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(context, "Item at id clicked is:"+emails.get(position).getId(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context,DetailActivity.class);
                        intent.putExtra("EMAILID",emails.get(position).getId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }
        }

    private String getTime(String ts) {
        long dv = Long.valueOf(ts)*1000;
        Date df = new java.util.Date(dv);
        String time = new SimpleDateFormat("MMMM dd, yyyy hh:mma").format(df);
        return time;
    }

    @Override
        public int getItemCount() {
            return emails.size();
        }


    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        final Email item = emails.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(emails.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        Email item = emails.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (emails.contains(item)) {
            emails.remove(position);

            Log.d("Swiped","Item removed");
            notifyItemRemoved(position);
//            deleteEmail(emails.get(position).getId());
            Toast.makeText(context,"Email deleted!",Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEmail(String id) {
            WebyogApi apiService =
                    ApiClient.getClient().create(WebyogApi.class);

            Response call = apiService.deleteItem(id);
        if(call.isSuccessful())
        {
            Toast.makeText(context,"Email deleted!",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isPendingRemoval(int position) {
        Email item = emails.get(position);
        return itemsPendingRemoval.contains(item);
    }
}
