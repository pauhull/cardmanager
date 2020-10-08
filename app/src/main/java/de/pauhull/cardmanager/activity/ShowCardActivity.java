package de.pauhull.cardmanager.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import de.pauhull.cardmanager.R;
import de.pauhull.cardmanager.manager.BarcodeManager;
import de.pauhull.cardmanager.manager.CardManager;
import de.pauhull.cardmanager.util.ColorUtil;
import de.pauhull.cardmanager.util.LayoutUtil;

public class ShowCardActivity extends AppCompatActivity {

    private TextView cardNameText;
    private TextView customerNameText;
    private TextView tiltDeviceText;
    private ImageView codeView;
    private CardManager.Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_card);

        this.cardNameText = findViewById(R.id.cardNameText);
        this.customerNameText = findViewById(R.id.customerNameText);
        this.codeView = findViewById(R.id.codeView);
        this.tiltDeviceText = findViewById(R.id.tiltDeviceText);
        this.card = CardManager.getInstance().getDisplayedCard();

        if (card == null) {
            return;
        }

        this.getWindow().setStatusBarColor(card.getColor());
        this.getWindow().setBackgroundDrawable(new ColorDrawable(card.getColor()));
        this.cardNameText.setText(card.getCardName());
        this.customerNameText.setText(card.getCustomerName());

        int textColor = ColorUtil.highestContrast(getColor(R.color.colorTextLight), getColor(R.color.colorTextDark), card.getColor());
        this.cardNameText.setTextColor(textColor);
        this.customerNameText.setTextColor(textColor);

        if (tiltDeviceText != null) {
            Drawable tilt = getDrawable(textColor == getColor(R.color.colorTextLight) ? R.drawable.ic_tilt_light : R.drawable.ic_tilt_dark);

            tiltDeviceText.setTextColor(textColor);
            tiltDeviceText.setCompoundDrawablesRelativeWithIntrinsicBounds(tilt, null, null, null);
        }

        this.codeView.post(new Runnable() {
            @Override
            public void run() {
                int width = codeView.getWidth();
                int height = codeView.getHeight();

                if (BarcodeManager.getInstance().isBarcode(card.getBarcodeFormat())) {

                    width = Math.min(width, card.getBarcodeValue().length() * LayoutUtil.dpToPx(20, ShowCardActivity.this));
                    height = (int) Math.min(height, width * 0.7f);
                }

                Bitmap bitmap = BarcodeManager.getInstance().toBitmap(card.getBarcodeValue(), BarcodeManager.getInstance().formatFromInt(card.getBarcodeFormat()), width, height);
                RoundedBitmapDrawable bitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                bitmapDrawable.setCornerRadius(25f);
                codeView.setScaleType(ImageView.ScaleType.CENTER);
                codeView.setImageDrawable(bitmapDrawable);
            }
        });
    }
}