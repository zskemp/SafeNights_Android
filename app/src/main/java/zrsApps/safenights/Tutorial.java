package zrsApps.safenights;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

//Tutorial: http://blog.iamsuleiman.com/onboarding-android-viewpager-google-way/
public class Tutorial extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int pageCount;
    private int currPage;
    private Context context;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //keep track of how many pages
        pageCount = 4;

        context = getApplicationContext();
        final int color1 = ContextCompat.getColor(context, R.color.tutorial1);
        final int color2 = ContextCompat.getColor(context, R.color.tutorial2);
        final int color3 = ContextCompat.getColor(context, R.color.tutorial3);
        final int color4 = ContextCompat.getColor(context, R.color.tutorial4);

        final int[] colorList = new int[]{color1, color2, color3, color4};

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currPage = position;
                ArgbEvaluator evaluator = new ArgbEvaluator();

                int colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position == 3 ? position : position + 1]);
                mViewPager.setBackgroundColor(colorUpdate);

            }


            String[] titles = getResources().getStringArray(R.array.tutorial_titles);
            String[] descriptions = getResources().getStringArray(R.array.tutorial_desc);

            //Get resources and Set first title and description
            ImageButton mNextBtn = (ImageButton) findViewById(R.id.intro_btn_next);


            @Override
            public void onPageSelected(int position) {

                currPage = position;
                updateIndicators(currPage);

                //Update Background Color on Page Change
                switch (currPage) {
                    case 0:
                        mViewPager.setBackgroundColor(color1);
                        break;
                    case 1:
                        mViewPager.setBackgroundColor(color2);
                        break;
                    case 2:
                        mViewPager.setBackgroundColor(color3);
                        break;
                    case 3:
                        mViewPager.setBackgroundColor(color4);
                        break;
                    default:
                        mViewPager.setBackgroundColor(color1);
                        break;
                }


                TextView title = (TextView) findViewById(R.id.tutorial_label);
                TextView desc = (TextView) findViewById(R.id.tutorial_desc);
                //Set title of page
                title.setText(titles[position]);

                //Set description of page
                desc.setText(descriptions[position]);

                //TODO: change image resources
                ImageView img = (ImageView) findViewById(R.id.tutorial_img);
                switch (position) {
                    case 0: //Intro
                        img.setImageResource(R.drawable.trust);
                        break;
                    case 1:
                        img.setImageResource(R.drawable.adventure);
                        break;
                    case 2:
                        img.setImageResource(R.drawable.location);
                        break;
                    case 3:
                        img.setImageResource(R.drawable.graph);
                        break;
                    default:
                        img.setImageResource(R.drawable.trust);
                        break;
                }

                mNextBtn.setVisibility(position == (pageCount - 1) ? View.GONE : View.VISIBLE);

                Button mFinishBtn = (Button) findViewById(R.id.intro_btn_finish);
                mFinishBtn.setVisibility(position == (pageCount - 1) ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    void updateIndicators(int position) {
        ImageView[] indicators = {(ImageView) findViewById(R.id.intro_indicator_0),
                (ImageView) findViewById(R.id.intro_indicator_1),
                (ImageView) findViewById(R.id.intro_indicator_2),
                (ImageView) findViewById(R.id.intro_indicator_3)};
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tutorial, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.tutorial_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            TextView title = (TextView) rootView.findViewById(R.id.tutorial_label);
            TextView desc = (TextView) rootView.findViewById(R.id.tutorial_desc);
            String[] titles = getResources().getStringArray(R.array.tutorial_titles);
            String[] descriptions = getResources().getStringArray(R.array.tutorial_desc);
            title.setText(titles[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            desc.setText(descriptions[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            ImageView img = (ImageView) rootView.findViewById(R.id.tutorial_img);
            switch (getArguments().getInt(ARG_SECTION_NUMBER) - 1) {
                case 0: //Intro
                    img.setImageResource(R.drawable.trust);
                    break;
                case 1:
                    img.setImageResource(R.drawable.adventure);
                    break;
                case 2:
                    img.setImageResource(R.drawable.location);
                    break;
                case 3:
                    img.setImageResource(R.drawable.graph);
                    break;
                default:
                    img.setImageResource(R.drawable.trust);
                    break;
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";
            }
            return null;
        }
    }

    public static final String PREFS_NAME = "CoreSkillsPrefsFile";

    public void tutorialDone(View view) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("tutorialComplete", true);
        editor.commit();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void tutorialNext(View view) {
        mViewPager.setCurrentItem(++currPage);
    }
}