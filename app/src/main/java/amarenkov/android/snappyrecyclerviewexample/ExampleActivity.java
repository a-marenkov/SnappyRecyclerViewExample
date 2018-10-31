package amarenkov.android.snappyrecyclerviewexample;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.View;
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
        final SnappyRecyclerView rv4 = this.findViewById(R.id.rv4);
        final SnappyRecyclerView rv5 = this.findViewById(R.id.rv5);

        rv1.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        rv1.setHasFixedSize(true);
        rv1.setAdapter(new ExampleAdapter(items, R.layout.item_thin));

        rv2.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        rv2.setHasFixedSize(true);
        rv2.setAdapter(new ExampleAdapter(items, R.layout.item_thin));
        rv2.post(new Runnable() {
            @Override
            public void run() {
                rv2.smoothScrollBy(1, 0);
            }
        });

        rv3.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        rv3.setHasFixedSize(true);
        int itemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        rv3.setCenteringPadding(itemSize, margin, 0);
        rv3.enableSnapListener(SnappyRecyclerView.Behavior.NOTIFY_ON_IDLE_AND_NO_POSITION);
        rv3.setAdapter(new ExampleAdapter(items, R.layout.item_thin));

        rv4.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        rv4.setHasFixedSize(true);
        rv4.setCenteringPadding(itemSize, margin, 0);
        rv4.enableSnapListener(SnappyRecyclerView.Behavior.NOTIFY_ON_IDLE);
        rv4.setAdapter(new ExampleAdapter(items, R.layout.item_thin));

        View targetView = findViewById(R.id.target);
        rv5.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        rv5.setHasFixedSize(true);
        rv5.setCenteringPadding(targetView, 0);
        rv5.enableSnapListener(SnappyRecyclerView.Behavior.NOTIFY_ON_SCROLL);
        ExampleAdapter adapter = new ExampleAdapter(items, R.layout.item_thin);
        adapter.setCallback(new SnappyAdapter.Callback() {
            @Override
            public void onItemCentered(int position) {
                Snackbar.make(rv4, "Center items index " + position, 1000).show();
            }
        });
        rv5.setAdapter(adapter);
    }
}
