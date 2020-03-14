package com.gamingwe.cubewerubiksolver.manual;

import com.gamingwe.cubewerubiksolver.R;
import com.gamingwe.cubewerubiksolver.manual.utils.AnimationHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import xyz.klinker.android.floating_tutorial.FloatingTutorialActivity;
import xyz.klinker.android.floating_tutorial.TutorialPage;

public class Floating3dActivity extends FloatingTutorialActivity {
    @NotNull
    @Override
    public List<TutorialPage> getPages() {
        List<TutorialPage> pages = new ArrayList<>();

        pages.add(new TutorialPage(this) {
            @Override
            public void initPage() {
                setContentView(R.layout.activity_ultra_pager);
                setNextButtonText(R.string.generate_solution);
            }

            @Override
            public void animateLayout() {
                AnimationHelper.quickViewReveal(findViewById(R.id.generate_solution), 300);
            }
        });

        return pages;
    }
}
