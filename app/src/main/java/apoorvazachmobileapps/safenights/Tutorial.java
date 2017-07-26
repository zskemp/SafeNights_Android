package apoorvazachmobileapps.safenights;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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

import apoorvazachmobileapps.safenights.R;

//Tutorial: http://blog.iamsuleiman.com/onboarding-android-viewpager-google-way/
public class Tutorial extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int pageCount;
    private int currPage;

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
        pageCount = 7;

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currPage = position;
                ArgbEvaluator evaluator = new ArgbEvaluator();

                switch (currPage) {
                    case 0:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background1);
                        break;
                    case 1:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background2);
                        break;
                    case 2:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background3);
                        break;
                    case 3:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background4);
                        break;
                    case 4:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background5);
                        break;
                    case 5:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background6);
                        break;
                    case 6:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background7);
                        break;
                    default:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background1);
                        break;
                }
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
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background1);
                        break;
                    case 1:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background2);
                        break;
                    case 2:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background3);
                        break;
                    case 3:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background4);
                        break;
                    case 4:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background5);
                        break;
                    case 5:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background6);
                        break;
                    case 6:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background7);
                        break;
                    default:
                        mViewPager.setBackgroundResource(R.drawable.tutorial_background1);
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
                        img.setImageResource(R.mipmap.ic_launcher);
                        break;
                    case 1:
                        img.setImageResource(R.mipmap.ic_launcher);
                        break;
                    case 2:
                        img.setImageResource(R.mipmap.ic_launcher);
                        break;
                    case 3:
                        img.setImageResource(R.mipmap.ic_launcher);
                        break;
                    case 4:
                        img.setImageResource(R.mipmap.ic_launcher);
                        break;
                    case 5:
                        img.setImageResource(R.mipmap.ic_launcher);
                        break;
                    case 6:
                        img.setImageResource(R.mipmap.ic_launcher);
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
                (ImageView) findViewById(R.id.intro_indicator_3),
                (ImageView) findViewById(R.id.intro_indicator_4),
                (ImageView) findViewById(R.id.intro_indicator_5),
                (ImageView) findViewById(R.id.intro_indicator_6)};
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
            //TODO: change image resources
            switch (getArguments().getInt(ARG_SECTION_NUMBER) - 1) {
                case 0: //Intro
                    img.setImageResource(R.mipmap.ic_launcher);
                    break;
                case 1:
                    img.setImageResource(R.mipmap.ic_launcher);
                    break;
                case 2:
                    img.setImageResource(R.mipmap.ic_launcher);
                    break;
                case 3:
                    img.setImageResource(R.mipmap.ic_launcher);
                    break;
                case 4:
                    img.setImageResource(R.mipmap.ic_launcher);
                    break;
                case 5:
                    img.setImageResource(R.mipmap.ic_launcher);
                    break;
                case 6:
                    img.setImageResource(R.mipmap.ic_launcher);
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
                case 4:
                    return "SECTION 5";
                case 5:
                    return "SECTION 6";
                case 6:
                    return "SECTION 7";
            }
            return null;
        }
    }

    public static final String PREFS_NAME = "InaPrefsFile";

    public void tutorialDone(View view) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        settings.edit().putBoolean("tutorial", true).apply();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void tutorialNext(View view) {
        mViewPager.setCurrentItem(++currPage);
    }
}