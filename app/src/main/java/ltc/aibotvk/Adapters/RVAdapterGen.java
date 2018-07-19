package ltc.aibotvk.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ltc.aibotvk.Fragments.FrGenerated;
import ltc.aibotvk.Models.DataModel;
import ltc.aibotvk.R;
import ltc.aibotvk.SecondActivity;

import static ltc.aibotvk.SecondActivity.UpdateDataGenerated;

/**
 * Created by admin on 30.06.2018.
 */

public class RVAdapterGen extends RecyclerView.Adapter<RVAdapterGen.MyViewHolder> {
    private ArrayList<DataModel> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView rvSent, rvAnswer;
        ImageView rvRemove, rvEdit;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.rvSent = (TextView) itemView.findViewById(R.id.cw_sentence);
            this.rvAnswer = (TextView) itemView.findViewById(R.id.cw_answer);
            this.rvEdit = (ImageView) itemView.findViewById(R.id.cw_edit);
            this.rvRemove = (ImageView) itemView.findViewById(R.id.cw_remove);
        }
    }

    public RVAdapterGen(ArrayList<DataModel> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cw_container, parent, false);


        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView editSentence = holder.rvSent;
        TextView editAnswer = holder.rvAnswer;
        ImageView imgEdit = holder.rvEdit;
        ImageView imgRemove = holder.rvRemove;

        editSentence.setText(dataSet.get(listPosition).getSentence());
        editAnswer.setText(dataSet.get(listPosition).getAnswer());

        imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecondActivity.deleteDataGenerated(null, dataSet.get(listPosition).getSentence(), dataSet.get(listPosition).getAnswer());
                dataSet.remove(listPosition);
                FrGenerated.updateAdapter();
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogGenerated((Activity)v.getContext(), String.valueOf(listPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    void showDialogGenerated(Activity act, String pos) {
        LayoutInflater inflater = act.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.alert_frame, null);
        final EditText etsentence = ((EditText) alertLayout.findViewById(R.id.et_sen));
        final EditText etanswer = ((EditText) alertLayout.findViewById(R.id.et_ans));

        AlertDialog.Builder alert = new AlertDialog.Builder(act);
        alert
                .setView(alertLayout)
                // Positive button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateDataGenerated(pos, etsentence.getText().toString(), etanswer.getText().toString(), dataSet.get(Integer.parseInt(pos)).getSentence(), dataSet.get(Integer.parseInt(pos)).getAnswer());
                        FrGenerated.updateAdapter();
                    }
                })

                // Negative Button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something else
                    }
                });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

}
