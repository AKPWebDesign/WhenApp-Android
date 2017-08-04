package tech.akpmakes.android.taskkeeper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import tech.akpmakes.android.taskkeeper.models.WhenEvent;

class SwipeHandler extends ItemTouchHelper.SimpleCallback {
    private final MainActivity mainActivity;

    private ColorDrawable bgDelete;
    private ColorDrawable bgRefresh;
    private Drawable refreshIcon;
    private Drawable deleteIcon;
    private int iconMargin;
    private boolean initiated = false;

    SwipeHandler(MainActivity activity) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mainActivity = activity;
    }

    private void init() {
        initiated = true;
        bgDelete = new ColorDrawable(Color.parseColor("#f44336"));
        bgRefresh = new ColorDrawable(Color.parseColor("#263238"));
        refreshIcon = ContextCompat.getDrawable(mainActivity, R.drawable.ic_refresh);
        deleteIcon = ContextCompat.getDrawable(mainActivity, R.drawable.ic_delete);
        iconMargin = (int) mainActivity.getResources().getDimension(R.dimen.ic_clear_margin);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        WhenEventViewHolder vh = (WhenEventViewHolder) viewHolder;
        DatabaseReference item =  mainActivity.getAdapter().getRef(vh.getAdapterPosition());
        if(direction == ItemTouchHelper.LEFT) {
            item.removeValue();
        } else {
            WhenEvent evt = new WhenEvent();
            evt.setName(vh.getName().toString());
            evt.setWhen(new Date().getTime());
            item.setValue(evt);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder.getAdapterPosition() == -1) {
            return;
        }

        if (!initiated) {
            init();
        }

        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            View itemView = viewHolder.itemView;

            if(dX > 0){
                draw(refreshIcon, bgRefresh, itemView, c, (int) dX, true);
            } else {
                draw(deleteIcon, bgDelete, itemView, c, (int) dX, false);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void draw(Drawable icon, ColorDrawable bgColor, View itemView, Canvas c, int dX, boolean fromLeft) {
        bgColor.setBounds(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        if(fromLeft) {
            bgColor.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + dX, itemView.getBottom());
        }
        bgColor.draw(c);

        int itemHeight = itemView.getBottom() - itemView.getTop();
        int intrinsicWidth = icon.getIntrinsicWidth();
        int intrinsicHeight = icon.getIntrinsicWidth();

        int iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
        int iconRight = itemView.getRight() - iconMargin;
        int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
        int iconBottom = iconTop + intrinsicHeight;

        if(fromLeft) {
            iconLeft = itemView.getLeft() + iconMargin;
            iconRight = itemView.getLeft() + iconMargin + intrinsicWidth;
        }
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

        icon.draw(c);
    }
}
