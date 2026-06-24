package com.example.appmoney;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.appmoney.R;

public class ReportsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TextView tv = new TextView(requireContext());
        tv.setText("📊 Báo cáo");
        tv.setTextColor(getResources().getColor(R.color.text_primary, null));
        tv.setTextSize(24f);
        tv.setGravity(android.view.Gravity.CENTER);
        return tv;
    }
}