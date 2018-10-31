package amarenkov.android.snappyrecyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public abstract class SnappyAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected Snapper snapper = null;
    private MSnapper mSnapper = null;
    private SnappyAdapter.Callback mCallback = null;
    private VH mCenteredVh = null;

    final void setSnapper(Snapper snapper) {
        this.snapper = snapper;
    }

    final void setMSnapper(MSnapper snapper) {
        mSnapper = snapper;
    }

    public final void setCallback(SnappyAdapter.Callback callback) {
        mCallback = callback;
    }

    final void onNewItemCentered(int position) {
        if (mSnapper.notifyOnSnap()) {
            if (mCenteredVh != null) {
                onSnapedFromCenter(mCenteredVh);
                mCenteredVh = null;
            }
            if (position != RecyclerView.NO_POSITION) notifyItemChanged(position);
        } else if (mCallback != null) mCallback.onItemCentered(position);
    }

    @Override
    public final void onBindViewHolder(@NonNull VH vh, int i) {
        if (mSnapper == null) return;
        if (i == mSnapper.getCurrentPosition() && mSnapper.notifyOnSnap()) {
            mCenteredVh = vh;
            onBindViewHolder(vh, i, true);
            if (mCallback != null) mCallback.onItemCentered(i);
        } else onBindViewHolder(vh, i, false);
    }

    /**
     * Similar to onBindViewHolder(@NonNull VH vh, int position)
     * If centered item is different from the other, layout of the item should be binded with
     * respect of @param isAtTheCenter
     * <p>
     * provides with @param isAtTheCenter only if enableSnapListener of recyclerview had been called,
     * otherwise isAtTheCenter will always be false
     *
     * @param isAtTheCenter true if item is at the center
     */
    protected abstract void onBindViewHolder(@NonNull VH vh, int position, boolean isAtTheCenter);

    /**
     * Notifies when the item that had been at the center moved.
     * If centered item is different from the other, here layout of the item should be updated
     * using @param vh
     * <p>
     * fires only if enableSnapListener of recyclerview had been called
     */
    protected abstract void onSnapedFromCenter(@NonNull VH vh);

    public interface Snapper {
        int getSnappedPosition();

        void snapToPosition(int position);

        void smoothSnapToPosition(int position);

        void smoothSnapBy(int dx, int dy);
    }

    public interface Callback {
        void onItemCentered(int position);
    }

    interface MSnapper {
        int getCurrentPosition();

        boolean notifyOnSnap();
    }
}