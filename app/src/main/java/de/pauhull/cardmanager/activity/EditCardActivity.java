package de.pauhull.cardmanager.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import de.pauhull.cardmanager.R;
import de.pauhull.cardmanager.dialog.ColorDialog;
import de.pauhull.cardmanager.manager.BarcodeManager;
import de.pauhull.cardmanager.manager.CardManager;
import de.pauhull.cardmanager.util.VibratorUtil;

import static de.pauhull.cardmanager.util.LayoutUtil.dpToPx;

public class EditCardActivity extends AppCompatActivity {

    private CardManager.Card cardToEdit;
    private Button colorButton;
    private ColorDialog colorDialog;
    private int color;
    private int barcodeFormat;
    private String barcodeValue;
    private ImageView codeView;
    private Button cancelButton;
    private Button saveButton;
    private EditText cardName;
    private EditText customerName;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        this.barcodeFormat = BarcodeManager.getInstance().getLastBarcodeFormat();
        this.barcodeValue = BarcodeManager.getInstance().getLastBarcodeValue();

        this.color = Color.RED;
        this.codeView = findViewById(R.id.codeView);
        this.colorButton = findViewById(R.id.colorButton);
        this.cancelButton = findViewById(R.id.cancelButton);
        this.saveButton = findViewById(R.id.saveButton);
        this.cardName = findViewById(R.id.cardNameField);
        this.customerName = findViewById(R.id.customerNameField);

        CardManager.Card lastAddedCard = CardManager.getInstance().getLastAddedCard();
        if (lastAddedCard != null) {
            customerName.setText(lastAddedCard.getCustomerName());
        }

        if (CardManager.getInstance().getCardToEdit() != null) {

            this.cardToEdit = CardManager.getInstance().getCardToEdit();
            this.customerName.setText(cardToEdit.getCustomerName());
            this.cardName.setText(cardToEdit.getCardName());
            this.color = cardToEdit.getColor();
            this.barcodeFormat = cardToEdit.getBarcodeFormat();
            this.barcodeValue = cardToEdit.getBarcodeValue();
        }

        this.colorDialog = new ColorDialog(this, color);

        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setBounds(new Rect(0, 0, dpToPx(25, this), dpToPx(25, this)));
        drawable.setCornerRadius(dpToPx(5, this));

        this.colorButton.setCompoundDrawablesRelative(drawable, null, null, null);

        this.colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorDialog.show();
            }
        });

        this.colorDialog.colorChanged(new Runnable() {
            @Override
            public void run() {
                color = colorDialog.getColor();
                ((GradientDrawable) colorButton.getCompoundDrawablesRelative()[0]).setColor(color);
            }
        });

        this.codeView.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BarcodeManager.getInstance().toBitmap(barcodeValue, BarcodeManager.getInstance().formatFromInt(barcodeFormat), codeView.getWidth(), codeView.getHeight());
                RoundedBitmapDrawable bitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                bitmapDrawable.setCornerRadius(25f);
                codeView.setImageDrawable(bitmapDrawable);
            }
        });

        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void save() {

        if (customerName.getText().length() == 0) {
            customerName.setError(getString(R.string.enter_customer_name));
            customerName.requestFocus();
        }

        if (cardName.getText().length() == 0) {
            cardName.setError(getString(R.string.enter_card_name));
            cardName.requestFocus();
        }

        if (customerName.getText().length() == 0
                || cardName.getText().length() == 0) {

            VibratorUtil.vibratePulse(this);
            return;
        }

        if (cardToEdit == null) {

            CardManager.Card card = new CardManager.Card(cardName.getText().toString(), customerName.getText().toString(), color, barcodeFormat, barcodeValue);
            CardManager.getInstance().getCards().add(card);
        } else {

            cardToEdit.setCardName(cardName.getText().toString());
            cardToEdit.setCustomerName(customerName.getText().toString());
            cardToEdit.setColor(color);
        }

        CardManager.getInstance().saveCards(this.getFilesDir());
        finish();
    }

}
