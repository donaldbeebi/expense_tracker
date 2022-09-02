package edu.cuhk.expensetracker.game;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.text.DecimalFormat;

import edu.cuhk.expensetracker.R;

public class SelectWheelAdapter extends Adapter<SelectWheelAdapter.SelectWheelViewHolder> {
    private static final String DRAWABLE_FILE_PATH = "android.resource://edu.cuhk.csci3310_finaciallogger/drawable/";

    WheelSelector m_WheelSelector;
    SharedPreferencesManager m_SPM;
    CoinManager m_CoinManager;
    boolean[] m_WheelPurchaseStates;
    private final View m_View;
    private final LayoutInflater m_Inflater;

    class SelectWheelViewHolder extends RecyclerView.ViewHolder {
        ImageView m_WheelPreview;
        TextView m_Title;
        float m_Price;
        TextView m_PriceText;
        ImageView m_Coin;
        Button m_Button;
        boolean m_Purchased;
        int m_ButtonPosition;
        DecimalFormat m_Formatter;

        public SelectWheelViewHolder(View itemView) {
            super(itemView);
            m_WheelPreview = itemView.findViewById(R.id.item_wheel_preview);
            m_Title = itemView.findViewById(R.id.item_title);
            m_Price = 0.0f;
            m_PriceText = itemView.findViewById(R.id.item_price);
            m_Coin = itemView.findViewById(R.id.coin_cost_image_view);
            m_Button = itemView.findViewById(R.id.item_button);
            m_Purchased = false;
            m_ButtonPosition = 0;
            m_Formatter = new DecimalFormat("#,###");

            m_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(m_Purchased) {
                        //select
                        m_WheelSelector.selectWheel(m_ButtonPosition);
                    }
                    else {
                        //buy
                        m_CoinManager.deduct(m_Price);
                        TextView coinCost = m_View.findViewById(R.id.coin_cost_text_view);
                        coinCost.setText(m_Formatter.format(m_CoinManager.getTotalNumberOfCoins()));

                        m_SPM.updateWheelPurchaseState(m_ButtonPosition);
                        m_WheelPurchaseStates = m_SPM.getWheelPurchaseStates();
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void bindData(String title, float price, boolean purchased, int buttonPosition) {
            //loading the preview
            String imagePath = DRAWABLE_FILE_PATH + "spinning_wheel_" + title;
            Uri uri = Uri.parse(imagePath);
            m_WheelPreview.setImageURI(uri);

            //loading the title
            m_Title.setText(title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase());

            //loading the price
            m_Price = price;
            m_PriceText.setText("$" + m_Formatter.format(price));

            //loading the button
            m_ButtonPosition = buttonPosition;
            m_Purchased = purchased;
            if(purchased) {
                //select/selected
                m_PriceText.setText("OWNED");
                m_PriceText.setTextColor(Color.BLACK);
                m_Coin.setVisibility(View.INVISIBLE);
                if(buttonPosition == m_WheelSelector.getSelectedWheel()) {
                    m_Button.setText("Selected");
                    m_Button.setTextSize(8);
                    m_Button.setEnabled(false);
                }
                else {
                    m_Button.setText("Select");
                    m_Button.setTextSize(8);
                    m_Button.setEnabled(true);
                }
            }
            else {
                //buy
                m_Button.setText("Buy");
                m_Button.setTextSize(16);
                m_Coin.setVisibility(View.VISIBLE);
                if(CoinManager.HABITAT_PRICES[buttonPosition] > m_CoinManager.getTotalNumberOfCoins()) {
                    m_Button.setEnabled(false);
                    m_PriceText.setTextColor(Color.RED);
                }
                else {
                    m_Button.setEnabled(true);
                    m_PriceText.setTextColor(Color.GREEN);
                }
            }
        };
    }

    SelectWheelAdapter(WheelSelector wheelSelector, View view, LayoutInflater inflater) {
        m_WheelSelector = wheelSelector;
        m_SPM = SharedPreferencesManager.getInstance();
        m_WheelPurchaseStates = m_SPM.getWheelPurchaseStates();
        m_CoinManager = CoinManager.getInstance();
        m_Inflater = inflater;
        m_View = view;
    }

    @NonNull
    @Override
    public SelectWheelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = m_Inflater.inflate(R.layout.wheel_list_item, parent, false);
        return new SelectWheelViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectWheelViewHolder holder, int position) {
        holder.bindData(Background.HABITAT_TYPES[position], CoinManager.HABITAT_PRICES[position], m_WheelPurchaseStates[position], position);
    }

    @Override
    public int getItemCount() {
        return Background.HABITAT_TYPES.length;
    }
}
