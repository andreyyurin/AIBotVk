package ltc.aibotvk.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ltc.aibotvk.Adapters.RVAdapterGen;
import ltc.aibotvk.Adapters.RVAdapterPersonal;
import ltc.aibotvk.Models.DataModel;
import ltc.aibotvk.R;

import static ltc.aibotvk.SecondActivity.sortBase;
import static ltc.aibotvk.SecondActivity.viewAllGenerated;

/**
 * Created by admin on 28.06.2018.
 */

public class FrGenerated extends Fragment {
    private static RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    private static RecyclerView.LayoutManager layoutManager;
    private static ArrayList<DataModel> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generated, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_generated);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();

        updateAdapter();
        return v;
    }
    public static void updateAdapter(){
        data = sortBase(viewAllGenerated());
        adapter = new RVAdapterGen(data);
        recyclerView.setAdapter(adapter);
    }
}
