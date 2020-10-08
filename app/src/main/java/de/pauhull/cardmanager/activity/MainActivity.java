package de.pauhull.cardmanager.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.pauhull.cardmanager.R;
import de.pauhull.cardmanager.dialog.CardEditDialog;
import de.pauhull.cardmanager.manager.BarcodeManager;
import de.pauhull.cardmanager.manager.CameraManager;
import de.pauhull.cardmanager.manager.CardManager;
import de.pauhull.cardmanager.util.ColorUtil;

import static de.pauhull.cardmanager.util.LayoutUtil.dpToPx;

public class MainActivity extends AppCompatActivity {

    private Button scanButton;
    private TextView noCardText;
    private LinearLayout cardListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BarcodeManager.create();
        CameraManager.create();
        CardManager.create();

        this.scanButton = findViewById(R.id.scanButton);
        this.noCardText = findViewById(R.id.noCardText);
        this.cardListLayout = findViewById(R.id.cardListLayout);

        CardManager.getInstance().readCards(this.getFilesDir());

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanner();
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
        this.updateList();
    }

    private void updateList() {

        List<CardManager.Card> cards = CardManager.getInstance().getCards();

        noCardText.setVisibility(cards.size() == 0 ? View.VISIBLE : View.GONE);

        if (cards.size() == 0) {

            noCardText.setVisibility(View.VISIBLE);
            noCardText.setAlpha(0);
            noCardText.setTranslationY(dpToPx(10, this));
            noCardText.animate().alpha(1).translationY(0).setDuration(200);
        } else {

            noCardText.setVisibility(View.GONE);
        }

        int animDelay = 0;

        for (final CardManager.Card card : cards) {

            createButton(card);

            Button button = card.getButton();
            button.setAlpha(0);
            button.setTranslationX(dpToPx(10, this));
            button.animate().translationX(0).alpha(1).setDuration(250).setStartDelay(animDelay);
            animDelay += 50;
        }
    }

    private void createButton(final CardManager.Card card) {

        Button button;

        if (card.getButton() != null) {

            button = card.getButton();
        } else {

            int id = View.generateViewId();
            button = new Button(this);
            button.setId(id);
            card.setButton(button);
            cardListLayout.addView(button);
        }

        LinearLayout.LayoutParams layoutParams = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layoutParams.setMargins(dpToPx(25, this),
                dpToPx(10, this),
                dpToPx(25, this), 0);
        button.setLayoutParams(layoutParams);

        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(ColorUtil.mixColor(card.getColor(), Color.TRANSPARENT, 0.3f));
        backgroundDrawable.setCornerRadius(dpToPx(15, this));

        float[] outerRadii = new float[8];
        Arrays.fill(outerRadii, dpToPx(15, this));
        RoundRectShape shape = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable mask = new ShapeDrawable(shape);
        ColorStateList stateList = ColorStateList.valueOf(card.getColor());
        button.setBackground(new RippleDrawable(stateList, backgroundDrawable, mask));

        button.setText(card.getCardName());
        button.setTextColor(getColor(R.color.colorTextLight));
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        button.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        button.setPadding(dpToPx(15, this), dpToPx(15, this), dpToPx(15, this), dpToPx(15, this));
        button.setAllCaps(false);
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CardManager.getInstance().setDisplayedCard(card);
                startActivity(new Intent(MainActivity.this, ShowCardActivity.class));
            }
        });

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                CardEditDialog dialog = new CardEditDialog(MainActivity.this, card);
                dialog.onDelete(new Runnable() {
                    @Override
                    public void run() {
                        deleteCard(card);
                    }
                });
                dialog.onEdit(new Runnable() {
                    @Override
                    public void run() {
                        editCard(card);
                    }
                });
                dialog.show();
                return true;
            }
        });

        button.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                card.setButton(null);
            }
        });
    }

    private void editCard(final CardManager.Card card) {

        CardManager.getInstance().setCardToEdit(card);
        startActivity(new Intent(this, EditCardActivity.class));
    }

    private void deleteCard(final CardManager.Card card) {

        final List<ObjectAnimator> animators = new ArrayList<>();
        final List<CardManager.Card> cards = CardManager.getInstance().getCards();
        final int shiftUp = cardListLayout.indexOfChild(card.getButton());

        for (int i = 0; i < cards.size(); i++) {
            if (cardListLayout.indexOfChild(cards.get(i).getButton()) > shiftUp) {
                ObjectAnimator animation = ObjectAnimator.ofFloat(cards.get(i).getButton(), "translationY", -card.getButton().getHeight() - dpToPx(10, MainActivity.this));
                animation.setStartDelay(250);
                animators.add(animation);
            }
        }

        animators.add(ObjectAnimator.ofFloat(card.getButton(), "translationX", dpToPx(10, this)));
        animators.add(ObjectAnimator.ofFloat(card.getButton(), "alpha", 0));

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animators.toArray(new ObjectAnimator[0]));
        animSet.setDuration(250);
        animSet.start();

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                for (CardManager.Card reset : cards) {
                    reset.getButton().setTranslationY(0);
                }

                cardListLayout.removeView(card.getButton());
                updateList();
            }
        });

        CardManager.getInstance().getCards().remove(card);
        CardManager.getInstance().saveCards(this.getFilesDir());
    }

    private void openScanner() {

        if (CameraManager.getInstance().checkCameraPermissions(this)) {

            CameraManager.getInstance().initCamera(this);
            startActivity(new Intent(MainActivity.this, ScanActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 201) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openScanner();
            }
        }
    }
}