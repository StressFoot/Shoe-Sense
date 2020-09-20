package org.stressfoot.stress.sense.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.stressfoot.stress.sense.R;
import org.stressfoot.stress.sense.models.SliderAdapter;
import org.stressfoot.stress.sense.models.SliderItem;

public class IftttHelperActivity extends AppCompatActivity {
    private SliderAdapter adapter;
    private SliderView sliderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ifttt_helper_activity);

        sliderView = findViewById(R.id.imageSlider);

        adapter = new SliderAdapter(this);
        SliderItem sliderItem = new SliderItem();
        sliderItem.setDescription("Open IFTTT and create your own applet");
        sliderItem.setImageUrl("https://i.imgur.com/Hiw64E9.jpg");
        adapter.addItem(sliderItem);

        SliderItem sliderItem1 = new SliderItem();
        sliderItem1.setDescription("");
        sliderItem1.setImageUrl("https://i.imgur.com/3tfFT8H.jpg");
        adapter.addItem(sliderItem1);

        SliderItem sliderItem2 = new SliderItem();
        sliderItem2.setDescription("Choose \"Android Device\"");
        sliderItem2.setImageUrl("https://i.imgur.com/lENbMyn.jpg");
        adapter.addItem(sliderItem2);

        SliderItem sliderItem3 = new SliderItem();
        sliderItem3.setDescription("");
        sliderItem3.setImageUrl("https://i.imgur.com/jSFVjzZ.jpg");
        adapter.addItem(sliderItem3);

        SliderItem sliderItem4 = new SliderItem();
        sliderItem4.setDescription("Once completed, continue to follow the app instructions to add desired actions");
        sliderItem4.setImageUrl("https://i.imgur.com/fxMtVQC.jpg");
        adapter.addItem(sliderItem4);

        SliderItem sliderItem5 = new SliderItem();
        sliderItem5.setDescription("For example, to change your desktop wallpaper select Google Drive");
        sliderItem5.setImageUrl("https://i.imgur.com/OQeAx9x.jpg");
        adapter.addItem(sliderItem5);

        SliderItem sliderItem6 = new SliderItem();
        sliderItem6.setDescription("");
        sliderItem6.setImageUrl("https://i.imgur.com/Um7uYei.jpg");
        adapter.addItem(sliderItem6);

        SliderItem sliderItem7 = new SliderItem();
        sliderItem7.setDescription("Provide the URL to your image and the folder path");
        sliderItem7.setImageUrl("https://i.imgur.com/5uE29xH.jpg");
        adapter.addItem(sliderItem7);

        SliderItem sliderItem8 = new SliderItem();
        sliderItem8.setDescription("Finally point to the drive folder");
        sliderItem8.setImageUrl("https://i.imgur.com/goB4tf7.png");
        adapter.addItem(sliderItem8);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(false);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds
    }
}