package edu.unh.cs.cs619.bulletzone.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import edu.unh.cs.cs619.bulletzone.model.GameUser;
import edu.unh.cs.cs619.bulletzone.model.GridCell;
import edu.unh.cs.cs619.bulletzone.model.GridCellFactory;

// TODO: Give a game user instance and give game user direction

@EBean
public class GridAdapter extends BaseAdapter {

    private GameUser user = GameUser.getInstance();
    private Context context;
    private final Object monitor = new Object();
    @SystemService
    protected LayoutInflater inflater;
    private long[][] mEntities = new long[16][16];

    public void updateList(long[][] entities) {
        synchronized (monitor) {
            this.mEntities = entities;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return 16 * 16;
    }

//    /**
//     *
//     * @param tankID the id of the current players tank
//     */
//    public void setTankID(long tankID) {this.tankID = tankID;}

    /**
     * @param context reference to client activity
     */
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Object getItem(int position) {
        return mEntities[(int) position / 16][position % 16];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(50, 50));
            imageView.setPaddingRelative(0, 0, 0, 0);
        } else
            imageView = (ImageView) convertView;

        int row = position / 16;
        int col = position % 16;

        int[] loc = new int[2];
        loc[0] = row;
        loc[1] = col;

        // TODO: change to GridCell array
        long val = mEntities[row][col];
        long[] adjacent = new long[4]; //n,e, s, w
        if (row > 0)
            adjacent[0] = mEntities[row - 1][col];
        else
            adjacent[0] = -1;
        if (col < 15)
            adjacent[1] = mEntities[row][col + 1];
        else
            adjacent[1] = -1;
        if (row < 15)
            adjacent[2] = mEntities[row + 1][col];
        else
            adjacent[2] = -1;
        if (col > 0)
            adjacent[3] = mEntities[row][col - 1];
        else
            adjacent[3] = -1;

        synchronized (monitor) {
            // TODO: make GridCell when info comes in
            GridCellFactory factory = GridCellFactory.getInstance(context);
            GridCell cell = factory.getCell(val, adjacent);

            // Health Bars
            //get rid of terrain
            val = val % 100000000;

            if (val >= 20000000) {
                long finalVal = val;
                imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    // Dont ask me cuz I dont know~
                    // This also doesn't seem to be working :(
                    @Override
                    public void onGlobalLayout() {
                        // remove the listener to avoid multiple calls
                        imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // create the bitmap using the final dimensions of the ImageView
                        // set w to size of screen and h to size of screen
                        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);

                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint();

                        //draw a rectangle at tge top left
//                        canvas.drawColor(Color.GREEN);
//                        canvas.drawRect(0, 0, 100, 100, paint);
                        // Wow not even this works :(

                        int[] imageViewLocation = new int[2];
                        imageView.getLocationOnScreen(imageViewLocation);

                        // if entity value is a vehicle
                        long id = (finalVal % 10000000) / 10000;
                        // draw a healthbar above them
                        int health = (int) (finalVal % 10000);
                        if (health > 0) {
                            // draw a rectangle
                            if (id == user.getTankId())
                                paint.setColor(Color.GREEN);
                            else
                                paint.setColor(Color.RED);

                            paint.setStyle(Paint.Style.FILL);
                            //get the width and height of the cell
                            int x = imageViewLocation[0];
                            int y = imageViewLocation[1];

                            double width = 100 * (health / 100.0);
                            int height = 50;

                            // turn it into a rectangle bitmap
                            canvas.drawRect(x, y-5, x + (int) width, y + height, paint);
                        }


                        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
                        Drawable[] layers = {bitmapDrawable, cell.getBackground(), cell.getMiddleGround(), cell.getForeground()};
                        LayerDrawable layerDrawable = new LayerDrawable(layers);
                        imageView.setImageDrawable(layerDrawable);
                    }
                });
            } else {
                Drawable[] layers = {cell.getBackground(), cell.getMiddleGround(), cell.getForeground()};
                LayerDrawable layerDrawable = new LayerDrawable(layers);
                imageView.setImageDrawable(layerDrawable);
            }
        }
        return imageView;
    }
}
