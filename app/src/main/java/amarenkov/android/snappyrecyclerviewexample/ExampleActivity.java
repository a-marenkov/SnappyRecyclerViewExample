package amarenkov.android.snappyrecyclerviewexample;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import amarenkov.android.snappyrecyclerview.SnappyAdapter;
import amarenkov.android.snappyrecyclerview.SnappyRecyclerView;

public class ExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        List<Integer> items = new ArrayList<>();
        for (int i = 1; i < 16; i++) {
            items.add(i);
        }

        final SnappyRecyclerView rv1 = this.findViewById(R.id.rv1);
        final SnappyRecyclerView rv2 = this.findViewById(R.id.rv2);
        final SnappyRecyclerView rv3 = this.findViewById(R.id.rv3);
        final Button btn = this.findViewById(R.id.btn);

        rv1.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        rv1.setHasFixedSize(true);
        int itemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        rv1.setCenteringPadding(itemSize, margin, 0);
        rv1.enableSnapListener(SnappyRecyclerView.Behavior.NOTIFY_ON_IDLE_AND_NO_POSITION);
        rv1.setAdapter(new ExampleAdapter(items));

        rv2.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        rv2.setHasFixedSize(true);
        rv2.setCenteringPadding(itemSize, margin, 2);
        rv2.enableSnapListener(SnappyRecyclerView.Behavior.NOTIFY_ON_IDLE);
        rv2.setAdapter(new ExampleAdapter(items));

        final View targetView = findViewById(R.id.target);
        final View targetView2 = findViewById(R.id.target2);
        rv3.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        rv3.setHasFixedSize(true);
        rv3.setCenteringPadding(targetView, 0);
        rv3.enableSnapListener(SnappyRecyclerView.Behavior.NOTIFY_ON_SCROLL);
        final ExampleAdapter adapter = new ExampleAdapter(items);
        adapter.setCallback(new SnappyAdapter.Callback() {
            @Override
            public void onItemCentered(int position) {
                Snackbar.make(rv3, "Center items index " + position, 1000).show();
            }
        });
        rv3.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.layout == R.layout.item_thin)
                    rv3.resetCenteringPadding(targetView2, rv3.getSnappedPosition());
                else rv3.resetCenteringPadding(targetView, rv3.getSnappedPosition());
                adapter.toggleItemWidth();
            }
        });
    }
}
