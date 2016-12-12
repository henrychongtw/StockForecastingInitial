package tw.edu.ntust.stockoraclet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by henrychong on 2016/9/19.
 */
public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPageNo;

    public static PageFragment newInstance(int pageNo){
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page,container,false);
        TextView textView = (TextView) view;
        textView.setText("Fragment #"+ mPageNo);
        return view;
    }
}
