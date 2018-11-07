package amarenkov.androidx.snappyrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SnappyRecyclerView extends RecyclerView implements SnappyAdapter.Snapper, SnappyAdapter.MSnapper {
    private LinearSnapHelper mSnapHelper = new LinearSnapHelper();
    private Behavior mBehavior = Behavior.NOTIFY_ON_IDLE_AND_NO_POSITION;
    private SnappyAdapter mAdapter = null;
    private boolean mIsPaddingSet = false;
    private boolean mIsPaddingApplied = false;
    private boolean mIsScrollEnabled = false;
    private int mItemSize = 0;
    private int mEdgeMargin = 0;
    private int mCurentPosition = RecyclerView.NO_POSITION;

    public SnappyRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public SnappyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnappyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mSnapHelper.attachToRecyclerView(this);
    }

    public void setCustomSnapHelper(LinearSnapHelper snapHelper) {
        mSnapHelper.attachToRecyclerView(null);
        mSnapHelper = snapHelper;
        mSnapHelper.attachToRecyclerView(this);
    }

    /**
     * Sets required padding to place items in the center
     *
     * @param itemSize   height (if orientation is vertical) or weight (if orientation is horizontal) of the item
     * @param edgeMargin edge margin (e.g. left for vertical and top for horizontal orientation) of the item
     * @param position   initial position of the centered item (usually position = 0)
     */
    public final void setCenteringPadding(int itemSize, int edgeMargin, int position) {
        setClipChildren(false);
        setClipToPadding(false);
        mItemSize = itemSize;
        mEdgeMargin = edgeMargin;
        mCurentPosition = position;
        mIsPaddingSet = true;
    }

    /**
     * Resets required padding to place items in the center
     *
     * @param itemSize   height (if orientation is vertical) or weight (if orientation is horizontal) of the item
     * @param edgeMargin edge margin (e.g. left for vertical and top for horizontal orientation) of the item
     * @param position   initial position of the centered item (usually position = 0)
     */
    public final void resetCenteringPadding(int itemSize, int edgeMargin, int position) {
        setCenteringPadding(itemSize, edgeMargin, position);
        mIsPaddingApplied = false;
        requestLayout();
    }

    /**
     * Sets required padding to place items in the center. Using view to obtain params
     *
     * @param target   the view that is identical to item view
     * @param position initial position of the centered item (usually position = 0)
     */
    public final void setCenteringPadding(final View target, final int position) {
        target.setVisibility(View.INVISIBLE);
        target.post(new Runnable() {
            @Override
            public void run() {
                if (getLayoutManager() == null) return;
                switch (((LinearLayoutManager) getLayoutManager()).getOrientation()) {
                    case RecyclerView.HORIZONTAL:
                        mItemSize = target.getWidth();
                        mEdgeMargin = ((FrameLayout.LayoutParams) target.getLayoutParams()).leftMargin;
                        break;
                    case RecyclerView.VERTICAL:
                        mItemSize = target.getHeight();
                        mEdgeMargin = ((FrameLayout.LayoutParams) target.getLayoutParams()).topMargin;
                        break;
                }
                setClipChildren(false);
                setClipToPadding(false);
                mCurentPosition = position;
                mIsPaddingSet = true;
                target.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Resets required padding to place items in the center. Using view to obtain params
     *
     * @param target   the view that is identical to item view
     * @param position initial position of the centered item (usually position = 0)
     */
    public final void resetCenteringPadding(final View target, final int position) {
        target.setVisibility(View.INVISIBLE);
        target.post(new Runnable() {
            @Override
            public void run() {
                if (getLayoutManager() == null) return;
                switch (((LinearLayoutManager) getLayoutManager()).getOrientation()) {
                    case RecyclerView.HORIZONTAL:
                        mItemSize = target.getWidth();
                        mEdgeMargin = ((FrameLayout.LayoutParams) target.getLayoutParams()).leftMargin;
                        break;
                    case RecyclerView.VERTICAL:
                        mItemSize = target.getHeight();
                        mEdgeMargin = ((FrameLayout.LayoutParams) target.getLayoutParams()).topMargin;
                        break;
                }
                setClipChildren(false);
                setClipToPadding(false);
                mCurentPosition = position;
                mIsPaddingSet = true;
                target.setVisibility(View.GONE);
                mIsPaddingApplied = false;
                requestLayout();
            }
        });
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layout) {
        super.setLayoutManager(layout);
        if (mIsScrollEnabled) enableSnapListener();
    }

    private void enableSnapListener() {
        mIsScrollEnabled = false;
        enableSnapListener(mBehavior);
    }

    /**
     * Enables scroll listener that will notify on snapped item change
     */
    public final void enableSnapListener(Behavior behavior) {
        if (mIsScrollEnabled) return;
        mIsScrollEnabled = true;
        mBehavior = behavior;
        if (getLayoutManager() == null) return;

        final boolean isVertical = ((LinearLayoutManager) getLayoutManager())
                .getOrientation() == RecyclerView.VERTICAL;

        switch (behavior) {
            case NOTIFY_ON_SCROLL:
                addOnScrollListener(new OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        if (dx != 0 || dy != 0) onItemCentered();
                    }
                });
                break;

            default:
                addOnScrollListener(new OnScrollListener() {
                    boolean hasBeenDragged = false;

                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        switch (newState) {
                            case SCROLL_STATE_IDLE:
                                onItemCentered();
                                break;
                            case SCROLL_STATE_DRAGGING:
                                if (mBehavior == Behavior.NOTIFY_ON_IDLE_AND_NO_POSITION)
                                    hasBeenDragged = true;
                                break;
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        if (hasBeenDragged && mCurentPosition != NO_POSITION) {
                            hasBeenDragged = false;
                            onNoItemCentered();
                        } else if (isVertical) {
                            if (!canScrollVertically(1) || !canScrollVertically(-1))
                                recyclerView.stopScroll();
                        } else if (!canScrollHorizontally(1) || !canScrollHorizontally(-1))
                            recyclerView.stopScroll();
                    }
                });
                break;
        }
    }

    private void onItemCentered() {
        if (getLayoutManager() == null) return;
        View target = mSnapHelper.findSnapView(getLayoutManager());
        if (target == null) return;
        int position = getLayoutManager().getPosition(target);
        if (mCurentPosition != position) {
            mCurentPosition = position;
            mAdapter.onNewItemCentered(mCurentPosition);
        }
    }

    private void onNoItemCentered() {
        mCurentPosition = RecyclerView.NO_POSITION;
        mAdapter.onNewItemCentered(mCurentPosition);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        if (adapter == null) super.setAdapter(null);
        else try {
            mAdapter = (SnappyAdapter) adapter;
            mAdapter.setSnapper(this);
            mAdapter.setMSnapper(this);
            super.setAdapter(mAdapter);
        } catch (Throwable ex) {
            super.setAdapter(null);
            ex.printStackTrace();
            Toast.makeText(getContext(), "Error: cannot set adapter", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @return current snapped position (position of the center item)
     */
    public int getSnappedPosition() {
        if (mIsScrollEnabled) return mCurentPosition;
        if (getLayoutManager() == null) return NO_POSITION;
        View target = mSnapHelper.findSnapView(getLayoutManager());
        if (target == null) return NO_POSITION;
        return getLayoutManager().getPosition(target);
    }

    /**
     * @return saved current snapped position (position of the center item)
     */
    public int getCurrentPosition() {
        return mCurentPosition;
    }

    /**
     * @return true if layout of the snapped item should be updated
     */
    public boolean notifyOnSnap() {
        return mBehavior != Behavior.NOTIFY_ON_SCROLL;
    }

    /**
     * Smothly scrolls to position
     *
     * @param position position of the item that will be placed in the center
     */
    public void smoothSnapToPosition(int position) {
        if (getLayoutManager() == null || mAdapter == null) return;
        if (mBehavior == Behavior.NOTIFY_ON_IDLE_AND_NO_POSITION
                && mCurentPosition != NO_POSITION) onNoItemCentered();
        smoothSnap(position);
    }

    private void smoothSnap(int position) {
        View target = getLayoutManager().findViewByPosition(position);
        if (target == null) {
            smoothScrollToPosition(position);
            target = getLayoutManager().findViewByPosition(position);
            if (target == null) return;
        }
        int[] dists = mSnapHelper.calculateDistanceToFinalSnap(getLayoutManager(), target);
        if (dists == null) return;
        smoothScrollBy(dists[0], dists[1]);
    }

    /**
     * Smothly scrolls to position by pixels
     */
    public void smoothSnapBy(int dx, int dy) {
        if (mAdapter == null) return;
        if (mBehavior == Behavior.NOTIFY_ON_IDLE_AND_NO_POSITION
                && mCurentPosition != NO_POSITION) onNoItemCentered();
        smoothScrollBy(dx, dy);
    }

    /**
     * Instantly scrolls to position
     */
    public void snapToPosition(int position) {
        View target = getLayoutManager().findViewByPosition(position);
        if (target == null) {
            scrollToPosition(position);
            target = getLayoutManager().findViewByPosition(position);
            if (target == null) return;
        }
        int[] dists = mSnapHelper.calculateDistanceToFinalSnap(getLayoutManager(), target);
        if (dists == null) return;
        scrollBy(dists[0], dists[1]);
        onItemCentered();
    }

    /**
     * @return true if padding has been aplied and items centered
     */
    public boolean isPaddingApplied() {
        return mIsPaddingApplied;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setCenteringPadding();
    }

    private void setCenteringPadding() {
        if (getLayoutManager() == null) return;
        if (!mIsPaddingApplied && mIsPaddingSet) {
            mIsPaddingApplied = true;
            int edgeSpacing = (getWidth() - mItemSize) / 2 - mEdgeMargin;
            switch (((LinearLayoutManager) getLayoutManager()).getOrientation()) {
                case RecyclerView.HORIZONTAL:
                    setPadding(edgeSpacing, 0, edgeSpacing, 0);
                    break;
                case RecyclerView.VERTICAL:
                    setPadding(0, edgeSpacing, 0, edgeSpacing);
                    break;
            }
            int snapTo = 0;
            if (mCurentPosition != RecyclerView.NO_POSITION) {
                snapTo = mCurentPosition;
                mCurentPosition = RecyclerView.NO_POSITION;
            }
            snapToPosition(snapTo);
        }
    }

    public enum Behavior {
        /**
         * Adapter will be notified on state idle with position of the centered item
         * and state dragging with position = -1
         * <p>
         * onBindViewHolder, onSnapedFromCenter and Callback's onItemCentered will be called
         */
        NOTIFY_ON_IDLE_AND_NO_POSITION,

        /**
         * Adapter will be notified only on state idle with position of the centered item
         * <p>
         * onBindViewHolder, onSnapedFromCenter and Callback's onItemCentered will be called
         */
        NOTIFY_ON_IDLE,

        /**
         * Adapter will be notified with position of the centered item even while dragging
         * <p>
         * Only Callback's onItemCentered will be called
         */
        NOTIFY_ON_SCROLL
    }
}