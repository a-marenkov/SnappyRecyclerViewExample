package amarenkov.android.snappyrecyclerviewexample;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import amarenkov.android.snappyrecyclerview.SnappyAdapter;

public class ExampleAdapter extends SnappyAdapter<ExampleAdapter.ViewHolder> {
    private List<Integer> list;
    int layout = R.layout.item_thin;

    ExampleAdapter(List<Integer> list) {
        this.list = list;
    }

    void toggleItemWidth() {
        if (layout == R.layout.item_thin) layout = R.layout.item_wide;
        else layout = R.layout.item_thin;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (layout == R.layout.item_thin) return R.layout.item_thin;
        else return R.layout.item_wide;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, boolean isAtTheCenter) {
        if (isAtTheCenter) viewHolder.select();
        else viewHolder.deselect();
    }

    @Override
    protected void onSnapedFromCenter(@NonNull ViewHolder viewHolder) {
        viewHolder.deselect();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tag;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (snapper.getSnappedPosition() != getAdapterPosition()) {
                        snapper.smoothSnapToPosition(getAdapterPosition());
                    }
                }
            });
            tag = itemView.findViewById(R.id.tag);
        }

        void select() {
            tag.setText("HERE");
        }

        void deselect() {
            tag.setText(String.valueOf(getAdapterPosition() + 1));
        }
    }
}
