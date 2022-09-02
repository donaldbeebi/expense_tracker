package edu.cuhk.expensetracker.game;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Random;

import edu.cuhk.expensetracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpinningWheelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpinningWheelFragment extends Fragment {
    private static final String DRAWABLE_FILE_PATH = "android.resource://edu.cuhk.expensetracker/drawable/";

    private TextView m_BucksText;
    private ImageView m_Wheel;
    private TextView m_ResultText;
    private TextView m_CostText;
    private Button m_SpinButton;
    private DecimalFormat m_Formatter;

    private int m_SelectedWheel;
    private static final int[] SPIN_COSTS = {
            5, //savanna
            10, //safari
            15 //wetland
    };

    private int m_CurrentDegree;
    //TODO: Properly place this static memory
    private SharedPreferencesManager m_SPM;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SpinningWheelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpinningWheelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpinningWheelFragment newInstance(String param1, String param2) {
        SpinningWheelFragment fragment = new SpinningWheelFragment();
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
        m_Formatter = new DecimalFormat("#,###");
        m_SelectedWheel = 0;
        m_SPM = SharedPreferencesManager.getInstance();
        m_CurrentDegree = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spinning_wheel, container, false);

        m_BucksText = view.findViewById(R.id.bucks_text_view_spinning_wheel);
        m_Wheel = view.findViewById(R.id.spinning_wheel);
        m_ResultText = view.findViewById(R.id.result_text_view);
        m_SpinButton = view.findViewById(R.id.spin_button);
        m_CostText = view.findViewById(R.id.buck_cost_text_view);

        //initializing the bucks text
        m_BucksText.setText(m_Formatter.format(m_SPM.getBucks()));

        updateData(m_SelectedWheel);

        m_SpinButton.setOnClickListener(this::spin);

        return view;
    }

    //TODO: RESET THE DEGREE AFTER SELECTING A DIFFERENT WHEEL
    //TODO: ADD THE ABILITY TO CHARGE
    public void spin(View view) {
        m_SpinButton.setEnabled(false);
        m_SPM.deductBucks(SPIN_COSTS[m_SelectedWheel]);
        m_BucksText.setText(String.valueOf(m_SPM.getBucks()));

        //rotating
        Random random = new Random();
        int degree = random.nextInt(360);
        int result = ((360 - degree + 30) % 360) / 60;
        int typeInWheel;
        int count;
        if(result % 2 == 1) {
            typeInWheel = 0;
            count = 1 + result / 2;
        }
        else {
            if(result != 0) {
                typeInWheel = 1;
                count = result / 2;
            }
            else {
                typeInWheel = 2;
                count = 1;
            }
        }
        m_SPM.addGameObjectData(m_SelectedWheel * 3 + typeInWheel, count);
        String animalName = GameObject.ANIMAL_TYPES[m_SelectedWheel][typeInWheel].substring(0, 1).toUpperCase() + GameObject.ANIMAL_TYPES[m_SelectedWheel][typeInWheel].substring(1) + " x" + count;
        Log.d("degree", String.valueOf(degree));
        Log.d("result", String.valueOf(result));

        RotateAnimation rotateAnimation = new RotateAnimation(m_CurrentDegree, 720 + degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(3000);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                m_ResultText.setText("You got " + animalName + "!");
                m_CurrentDegree = degree;
                if(m_SPM.getBucks() < SPIN_COSTS[m_SelectedWheel]) {
                    m_SpinButton.setEnabled(false);
                    m_CostText.setTextColor(Color.RED);
                }
                else {
                    m_SpinButton.setEnabled(true);
                    m_CostText.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        m_Wheel.startAnimation(rotateAnimation);
    }

    public void setSelectedWheel(int selectedWheel) {
        //getting the proper wheel
        m_SelectedWheel = selectedWheel;
        updateData(selectedWheel);
    }

    private void updateData(int selectedWheel) {
        String imagePath = DRAWABLE_FILE_PATH + "spinning_wheel_" + Background.HABITAT_TYPES[selectedWheel];
        Uri uri = Uri.parse(imagePath);
        m_Wheel.setImageURI(uri);

        //setting the cost right
        //Checking if the user has enough bucks
        m_CostText.setText("Cost: " + String.valueOf(SPIN_COSTS[selectedWheel]));
        if(m_SPM.getBucks() < SPIN_COSTS[selectedWheel]) {
            m_SpinButton.setEnabled(false);
            m_CostText.setTextColor(Color.RED);
        }
        else {
            m_SpinButton.setEnabled(true);
            m_CostText.setTextColor(Color.GREEN);
        }
    }
}