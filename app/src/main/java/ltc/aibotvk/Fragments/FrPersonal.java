package ltc.aibotvk.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ltc.aibotvk.Adapters.RVAdapterPersonal;
import ltc.aibotvk.Models.DataModel;
import ltc.aibotvk.R;

import static ltc.aibotvk.SecondActivity.viewAllPersonal;

/**
 * Created by admin on 28.06.2018.
 */

public class FrPersonal extends Fragment {
    private static RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
   // private RecyclerView.LayoutManager layoutManager;
    private static ArrayList<DataModel> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_personal, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_personal);
        recyclerView.setHasFixedSize(true);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();

        updateAdapter();
        return v;
    }

    public static void updateAdapter(){
        data = viewAllPersonal();
        adapter = new RVAdapterPersonal(data);
        recyclerView.setAdapter(adapter);
    }
}
