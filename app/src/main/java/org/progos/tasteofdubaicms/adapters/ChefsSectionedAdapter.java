package org.progos.tasteofdubaicms.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.model.Chef;
import org.progos.tasteofdubaicms.ui.ChefDetailsFragment;
import org.progos.tasteofdubaicms.ui.ChefsFragment;
import org.progos.tasteofdubaicms.utility.Strings;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by NomBhatti on 12/18/2015.
 */

public class ChefsSectionedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private static final int SECTION_TYPE = 0;

    private boolean mValid = true;
    private int mSectionResourceId;
    private int mTextResourceId, mImageResourceId;
    private LayoutInflater mLayoutInflater;
    private RecyclerView.Adapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<Section>();
    private RecyclerView mRecyclerView;
    int itemHeight;

    public ChefsSectionedAdapter(Context context, int sectionResourceId, int textResourceId,
                                 int imageResourceId,
                                 RecyclerView recyclerView,
                                 RecyclerView.Adapter baseAdapter) {

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSectionResourceId = sectionResourceId;
        mTextResourceId = textResourceId;
        mImageResourceId = imageResourceId;
        mBaseAdapter = baseAdapter;
        mContext = context;
        mRecyclerView = recyclerView;

        itemHeight = getSectionItemHeight();
        mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });

        final GridLayoutManager layoutManager = (GridLayoutManager) (mRecyclerView.getLayoutManager());
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (isSectionHeaderPosition(position)) || position == 0 ? layoutManager.getSpanCount() : 1;
            }
        });
    }


    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView imageView;

        public SectionViewHolder(View view, int mTextResourceid, int imageResourceId, int itemHeight) {
            super(view);
            title = (TextView) view.findViewById(mTextResourceid);
            imageView = (ImageView) view.findViewById(imageResourceId);

            imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, itemHeight));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            final View view = LayoutInflater.from(mContext).inflate(mSectionResourceId, parent, false);
            return new SectionViewHolder(view, mTextResourceId, mImageResourceId, itemHeight);
        } else {
            return mBaseAdapter.onCreateViewHolder(parent, typeView - 1);
        }
    }

    private int getSectionItemHeight() {
        int height;
        DisplayMetrics metrics = new DisplayMetrics();
        (((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()).getMetrics(metrics);
        int width = metrics.widthPixels;
        height = width / 2;
        return height;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {
        if (isSectionHeaderPosition(position)) {

            final Chef chef = mSections.get(position).getChef();
            String name = (chef.getName()).toString();
            name = Html.fromHtml(name).toString();
            ((SectionViewHolder) sectionViewHolder).title.setText(name);
            ImageView imageView = ((SectionViewHolder) sectionViewHolder).imageView;
            Picasso.with(mContext).load(chef.getImageUrl()).into(imageView);

            /*sectionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mContext instanceof FragmentActivity) {
                        FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Fragment fragment = fm.findFragmentByTag(Strings.FRAGMENT_CHEFS);
                        if (fragment instanceof ChefsFragment) {
                            ft.hide(fragment);
                            Bundle args = new Bundle();
                            args.putSerializable(Strings.CHEF_OBJ, chef);
                            ChefDetailsFragment chefDetailsFragment = new ChefDetailsFragment();
                            chefDetailsFragment.setArguments(args);
                            ft.add(R.id.fragmentsContainerLayout, chefDetailsFragment);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }
                }
            });*/

            imageView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageView view = (ImageView) v;
                            //overlay is black with transparency of 0x77 (119)
                            Drawable drawable = view.getDrawable();
                            if(drawable!=null) {
                                drawable.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                            if (mContext instanceof FragmentActivity) {
                                FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                Fragment fragment = fm.findFragmentByTag(Strings.FRAGMENT_CHEFS);
                                if (fragment instanceof ChefsFragment) {
                                    ft.hide(fragment);
                                    Bundle args = new Bundle();
                                    args.putSerializable(Strings.CHEF_OBJ, chef);
                                    ChefDetailsFragment chefDetailsFragment = new ChefDetailsFragment();
                                    chefDetailsFragment.setArguments(args);
                                    ft.add(R.id.fragmentsContainerLayout, chefDetailsFragment);
                                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                            }

                        case MotionEvent.ACTION_CANCEL: {
                            ImageView view = (ImageView) v;
                            //clear the overlay
                            Drawable drawable = view.getDrawable();
                            if(drawable!=null) {
                                drawable.clearColorFilter();
                                view.invalidate();
                            }

                            break;
                        }
                    }
                    return true;
                }
            });


        } else {
            mBaseAdapter.onBindViewHolder(sectionViewHolder, sectionedPositionToPosition(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : mBaseAdapter.getItemViewType(sectionedPositionToPosition(position)) + 1;
    }


    public static class Section {
        int firstPosition;
        int sectionedPosition;
        Chef chef;

        public Section(int firstPosition, Chef chef) {
            this.firstPosition = firstPosition;
            this.chef = chef;
        }

        public Chef getChef() {
            return chef;
        }

    }


    public void setSections(Section[] sections) {
        mSections.clear();

        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            mSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }


    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemCount() {
        return (mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0);
    }


}