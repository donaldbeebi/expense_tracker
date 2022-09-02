package edu.cuhk.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.cuhk.expensetracker.game.SelectWheelFragment;
import edu.cuhk.expensetracker.game.SpinningWheelFragment;
import edu.cuhk.expensetracker.game.WheelSelector;

public class GameTransactionActivity extends AppCompatActivity {
    private static final String SELECT_WHEEL_FRAGMENT_TAG = "select_wheel_fragment";
    private static final String SPINNING_WHEEL_FRAGMENT_TAG = "spinning_wheel_fragment";

    SelectWheelFragment m_SelectWheelFragment;
    SpinningWheelFragment m_SpinningWheelFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_transaction);

        //Setting up a back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Setting up the navigation buttons
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.spinning_wheel_nav_button) {
                    displaySpinningWheelFragment();
                    return true;
                }
                if (item.getItemId() == R.id.select_wheels_nav_button) {
                    displaySelectWheelsFragment();
                    return true;
                }
                return false;
            }
        });

        //Setting up the managers
        WheelSelector wheelSelector = new WheelSelector();
        wheelSelector.selectWheel(0);

        //Setting up the fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        m_SelectWheelFragment = SelectWheelFragment.newInstance("","");
        fragmentTransaction.add(R.id.transaction_frame_layout, m_SelectWheelFragment, SELECT_WHEEL_FRAGMENT_TAG);
        m_SpinningWheelFragment = SpinningWheelFragment.newInstance("", "");
        fragmentTransaction.add(R.id.transaction_frame_layout, m_SpinningWheelFragment, SPINNING_WHEEL_FRAGMENT_TAG);
        fragmentTransaction.hide(m_SelectWheelFragment);
        fragmentTransaction.show(m_SpinningWheelFragment);
        fragmentTransaction.commitNow();

        //bottomNavigation.setSelectedItemId(R.id.spinning_wheel_nav_button);
        bottomNavigation.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(Activity.RESULT_OK);
        finish();
        return true;
    }

    private void displaySelectWheelsFragment() {
        //Hiding the spinning wheel fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(m_SpinningWheelFragment);
        fragmentTransaction.show(m_SelectWheelFragment);
        fragmentTransaction.commit();
    }

    private void displaySpinningWheelFragment() {
        //Showing the spinning wheel fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        m_SpinningWheelFragment.setSelectedWheel(m_SelectWheelFragment.getSelectedWheel());
        fragmentTransaction.hide(m_SelectWheelFragment);
        fragmentTransaction.show(m_SpinningWheelFragment);
        fragmentTransaction.commit();
    }
}