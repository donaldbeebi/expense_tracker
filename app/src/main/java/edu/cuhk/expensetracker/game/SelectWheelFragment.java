package edu.cuhk.expensetracker.game;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import edu.cuhk.expensetracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectWheelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectWheelFragment extends Fragment {
    private RecyclerView m_RecyclerView;
    private SelectWheelAdapter m_Adapter;
    private WheelSelector m_WheelSelector;
    private DecimalFormat m_Formatter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SelectWheelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectWheelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectWheelFragment newInstance(String param1, String param2) {
        SelectWheelFragment fragment = new SelectWheelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        m_WheelSelector = new WheelSelector();
        m_WheelSelector.selectWheel(0);

        m_Formatter = new DecimalFormat("#,###");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_wheel, container, false);

        m_RecyclerView = view.findViewById(R.id.recyclerview);
        m_Adapter = new SelectWheelAdapter(m_WheelSelector, view, getActivity().getLayoutInflater());
        m_RecyclerView.setAdapter(m_Adapter);
        m_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView coinView = view.findViewById(R.id.coin_cost_text_view);
        coinView.setText(m_Formatter.format(CoinManager.getInstance().getTotalNumberOfCoins()));

        return view;
    }

    public int getSelectedWheel() {
        return m_WheelSelector.getSelectedWheel();
    }
}