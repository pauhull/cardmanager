package de.pauhull.cardmanager.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import de.pauhull.cardmanager.R;
import de.pauhull.cardmanager.manager.CardManager;

public class CardEditDialog extends Dialog {

    private CardManager.Card card;
    private TextView titleText;
    private Button editButton;
    private Button deleteButton;
    private Runnable onEdit, onDelete;

    public CardEditDialog(Activity activity, CardManager.Card card) {

        super(activity, R.style.Dialog);
        this.card = card;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_card_edit);

        Objects.requireNonNull(this.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.titleText = findViewById(R.id.titleText);
        this.editButton = findViewById(R.id.editButton);
        this.deleteButton = findViewById(R.id.deleteButton);

        this.titleText.setText(card.getCardName());

        this.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onEdit != null) onEdit.run();
            }
        });

        this.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onDelete != null) onDelete.run();
            }
        });
    }

    public CardEditDialog onEdit(Runnable onEdit) {

        this.onEdit = onEdit;
        return this;
    }

    public CardEditDialog onDelete(Runnable onDelete) {

        this.onDelete = onDelete;
        return this;
    }
}
