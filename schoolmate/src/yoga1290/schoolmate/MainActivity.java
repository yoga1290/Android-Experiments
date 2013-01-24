package yoga1290.schoolmate;


import yoga1290.schoolmate.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
//import android.app.FragmentManager;

public class MainActivity extends FragmentActivity {

	 
	 
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        
        /** Getting a reference to the ViewPager defined the layout file */
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        
//        PagerTitleStrip pts=(PagerTitleStrip) pager.findViewById(R.id.pager_title_strip);
//        pts.set
 
        /** Getting fragment manager */
        FragmentManager fm = getSupportFragmentManager();
 
        /** Instantiating FragmentPagerAdapter */
        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(fm);
 
        /** Setting the pagerAdapter to the pager object */
        pager.setAdapter(pagerAdapter);
    
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
        return false;
    }
    
    
}


class MyFragmentPagerAdapter extends FragmentPagerAdapter{
	 
    final int PAGE_COUNT = 3;
 
    /** Constructor of the class */
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int v) {
    			
//        MyFragment myFragment = new MyFragment();
//        Bundle data = new Bundle();
//        data.putInt("current_page", arg0+1);
//        frg.setArguments(data);
//        myFragment.setArguments(data);
//        return myFragment;
    		switch(v)
    		{
    			case 0:
    				return new view4sqr();
    			case 1:
    				return new RecorderView();
    		}
        return new ViewLoader();
    }
 
    /** Returns the number of pages */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        
        return "section#"+position;
    }
}
//
//class MyFragment extends Fragment{
//	 
//    int mCurrentPage;
//    View v;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
// 
//        /** Getting the arguments to the Bundle object */
//        Bundle data = getArguments();
// 
//        /** Getting integer data of the key current_page from the bundle */
//        mCurrentPage = data.getInt("current_page", 0);
//        
//    }
// 
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        v = inflater.inflate(R.layout.activity_account, container,false);
////        TextView tv = (TextView ) v.findViewById(R.id.tv);
////        tv.setText("You are viewing the page #" + mCurrentPage + "\n\n" + "Swipe Horizontally left / right");
//        
//        if(mCurrentPage==2)
//        {
//        	
////        		IntentIntegrator integrator = new IntentIntegrator(this.getActivity());
////        		integrator.initiateScan();
//        }
//        return v;
//    }
//    
//    	}
// 
//}