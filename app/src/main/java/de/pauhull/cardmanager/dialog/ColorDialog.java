package de.pauhull.cardmanager.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.Objects;

import de.pauhull.cardmanager.R;

public class ColorDialog extends Dialog {

    private ImageView hueView, saturationView, valueView;
    private SeekBar hueSlider, saturationSlider, valueSlider;
    private float hue, saturation, value;
    private Runnable r;
    private ImageView selectedColor;
    private Button closeButton;

    public ColorDialog(Activity activity, int startColor) {

        super(activity, R.style.Dialog);

        float[] hsv = new float[3];
        Color.colorToHSV(startColor, hsv);
        this.hue = hsv[0];
        this.saturation = hsv[1];
        this.value = hsv[2];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_color);

        Objects.requireNonNull(this.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.hueView = findViewById(R.id.hueView);
        this.saturationView = findViewById(R.id.saturationView);
        this.valueView = findViewById(R.id.valueView);
        this.hueSlider = findViewById(R.id.hueSlider);
        this.saturationSlider = findViewById(R.id.saturationSlider);
        this.valueSlider = findViewById(R.id.valueSlider);
        this.selectedColor = findViewById(R.id.selectedColor);
        this.closeButton = findViewById(R.id.closeButton);
        this.updateColors();

        hueSlider.setProgress((int) (hueSlider.getMax() * (hue / 360f)));
        hueSlider.setOnSeekBarChangeListener(new BarListener() {
            @Override
            public void accept(float f) {
                hue = f * 360f;
            }
        });

        saturationSlider.setProgress((int) (saturationSlider.getMax() * saturation));
        saturationSlider.setOnSeekBarChangeListener(new BarListener() {
            @Override
            public void accept(float f) {
                saturation = f;
            }
        });

        valueSlider.setProgress((int) (valueSlider.getMax() * value));
        valueSlider.setOnSeekBarChangeListener(new BarListener() {
            @Override
            public void accept(float f) {
                value = f;
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void updateColors() {

        // hue

        int[] gradientColors = new int[36];

        for (int i = 0; i < gradientColors.length; i++) {

            float hue = ((float) i / (float) (gradientColors.length - 1)) * 360f;
            gradientColors[i] = Color.HSVToColor(new float[]{hue, saturation, value});
        }

        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        gradient.setCornerRadius(5f);
        hueView.setBackground(gradient);

        // saturation

        gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{
                Color.HSVToColor(new float[]{hue, 0f, value}),
                Color.HSVToColor(new float[]{hue, 1f, value})
        });
        gradient.setCornerRadius(5f);
        saturationView.setBackground(gradient);

        // value

        gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{
                Color.HSVToColor(new float[]{hue, saturation, 0f}),
                Color.HSVToColor(new float[]{hue, saturation, 1f})
        });
        gradient.setCornerRadius(5f);
        valueView.setBackground(gradient);

        gradient = new GradientDrawable();
        gradient.setColor(getColor());
        gradient.setCornerRadius(15f);
        selectedColor.setBackground(gradient);

        if (r != null) {
            r.run();
        }
    }

    public void colorChanged(Runnable r) {
        this.r = r;
    }

    public int getColor() {

        return Color.HSVToColor(new float[]{hue, saturation, value});
    }

    private abstract class BarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            accept((float) progress / (float) seekBar.getMax());
            updateColors();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        public abstract void accept(float f);
    }
}
