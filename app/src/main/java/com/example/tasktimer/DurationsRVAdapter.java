package com.example.tasktimer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Locale;

public class DurationsRVAdapter extends RecyclerView.Adapter<DurationsRVAdapter.ViewHolder> {

    private Cursor mCursor;

private final java.text.DateFormat mDateFormat;
// module level so we don't keep instantiating in bimdView.

    public DurationsRVAdapter(Context context, Cursor cursor) {
        this.mCursor = cursor;
mDateFormat = DateFormat.getDateFormat(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_durations_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if ((mCursor != null) && (mCursor.getCount() != 0)){
            if (!mCursor.moveToPosition(position)){
                throw new IllegalStateException("Couldn't move cursor to position" + position);
            }
            String name = mCursor.getString(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_NAME));
            String description = mCursor.getString(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DESCRIPTION));
            Long startTime = mCursor.getLong(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_START_TIME));
            Long totalDuration = mCursor.getLong(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DURATION));

            holder.name.setText(name);
            if (holder.description != null){
                holder.description.setText(description);
            }

String userDate = mDateFormat.format(startTime * 1000);
 // The database stores seconds, we need milliseconds
            String totalTime = formatDuration(totalDuration);

holder.startDate.setText(userDate);

            holder.duration.setText(totalTime);
        }
    }

 @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }


    @Override
    public int getItemCount() {
Log.d(TAG, "getItemCount: starts");

        if ((mCursor == null) || (mCursor.getCount() == 0)){
            return 1;// fib, because we populate a single ViewHolder with instructions.
        }else {
            return mCursor.getCount();
        }
    }

    private String formatDuration(long duration){
        // duration is in seconds, convert to hours:minutes:seconds
        // (allowing for >24 hours - so we can't a time data type);
        long hours = duration / 3600;
        long reminder = duration - (hours * 3600);
        long minutes = reminder / 60;
        long seconds = reminder - (minutes * 60);

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

*
     * Swap in a new Cursor, returning the old cursor.
     * The returned old Cursor is <em></em> closed.
     *
     * @param newCursor The new cursor to be used
     * @return Returns the previously set Cursor, or null if there wasn't one
     * If the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned.



    Cursor swapCursor(Cursor newCursor){
        if (newCursor == mCursor){
            return null;
        }

        int numItems = getItemCount();// store old item count

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;

        if (newCursor != null){
            //notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            //notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, numItems); // Use the old count
        }
        return oldCursor;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        TextView startDate;
        TextView duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.td_name);
            this.description = itemView.findViewById(R.id.td_description_heading);
            this.startDate = itemView.findViewById(R.id.td_start);
            this.duration = itemView.findViewById(R.id.td_duration);
        }
    }
}
