package barqsoft.footballscores.utils;

import android.content.Context;
import android.graphics.RectF;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LayoutManager;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.object.Header;
import barqsoft.footballscores.object.Score;

// DONE: Add overflow icon to the cardview to handle the share action
// DONE: Highlight the winning team
// TODO: Swap out the missing team icon with a generic icon
// DONE: Added team crest images from Chris Olsen - https://github.com/chrisolsen/football-scores-svgs.
// DONE: Consider adding a section header - group by league
/**
 * Adapter for the score list
 *
 * Utilized the SuperSLiM library to add headers to the RecyclerView for the league name
 * https://github.com/TonicArtos/SuperSLiM
 *
 * Created by yehya khaled on 2/26/2015.
 */
public class Adapters {
    public static class ScoreListAdapter extends RecyclerView.Adapter<ScoreListAdapter.ViewHolder> {
        private static final int ITEM_VIEW_TYPE_HEADER = 0;
        private static final int ITEM_VIEW_TYPE_ITEM = 1;

        private final Context mContext;
        private final List<Score> mItems;
        private final ArrayList<LineItem> mLineItems;
        private OnItemClickListener mOnItemClickListener;
        private OverflowClickListener mOverflowClickListener;
        private int mNumColumns;

        public ScoreListAdapter(Context context, int numColumns) {
            mContext = context;
            mNumColumns = numColumns;
            mItems = new ArrayList<>();
            mLineItems = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v;
            if (viewType == ITEM_VIEW_TYPE_HEADER) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_score_header, viewGroup, false);
            } else {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_score, viewGroup, false);
            }
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            LineItem lineItem = mLineItems.get(position);
            final View itemView = viewHolder.itemView;

            if (lineItem != null) {
                if (lineItem.isHeader) {
                    Header header = lineItem.header;

                    if (header != null) {
                        viewHolder.header.setText(header.getFirstLine());
                    }
                } else {
                    Score item = lineItem.score;

                    // CHECK: Should the scores be swapped depending on the RTL/LTR orientation?
                    if (item != null) {
                        viewHolder.homeTeam.setText(item.getHomeName());
                        viewHolder.homeTeam.setTextColor(mContext.getResources().getColor(R.color.text_normal));

                        viewHolder.awayTeam.setText(item.getAwayName());
                        viewHolder.awayTeam.setTextColor(mContext.getResources().getColor(R.color.text_normal));

                        viewHolder.homeCrest.setContentDescription(mContext.getString(R.string.content_image_desc, item.getHomeName()));
                        viewHolder.awayCrest.setContentDescription(mContext.getString(R.string.content_image_desc, item.getAwayName()));

                        if (item.getHomeGoals() > -1 && item.getAwayGoals() > -1) {
                            viewHolder.homeScore.setText(String.valueOf(item.getHomeGoals()));
                            viewHolder.homeScore.setTextColor(mContext.getResources().getColor(R.color.text_normal));

                            viewHolder.awayScore.setText(String.valueOf(item.getAwayGoals()));
                            viewHolder.awayScore.setTextColor(mContext.getResources().getColor(R.color.text_normal));

                            if (item.getHomeGoals() > item.getAwayGoals()) {
                                viewHolder.homeTeam.setTextColor(mContext.getResources().getColor(R.color.theme_accent));
                                viewHolder.homeScore.setTextColor(mContext.getResources().getColor(R.color.theme_accent));
                            } else {
                                viewHolder.awayTeam.setTextColor(mContext.getResources().getColor(R.color.theme_accent));
                                viewHolder.awayScore.setTextColor(mContext.getResources().getColor(R.color.theme_accent));
                            }
                        } else {
                            viewHolder.homeScore.setText(mContext.getString(R.string.content_empty_score));
                            viewHolder.awayScore.setText(mContext.getString(R.string.content_empty_score));
                        }

                        viewHolder.matchDay.setText(Html.fromHtml(ScoreUtils.getMatchDay(mContext, item.getMatchDay(), item.getLeagueId())));
                        viewHolder.date.setText(Html.fromHtml(ScoreUtils.getMatchTime(mContext, item.getTime())));

                        String homeId = item.getHomeLink().replace("http://api.football-data.org/alpha/teams/", "");
                        String awayId = item.getAwayLink().replace("http://api.football-data.org/alpha/teams/", "");

                        // NOTE: Picasso isn't really needed here as the files are local, but leaving in the event it utilizes retrieving the images from the
                        // football scores API
                        Picasso.with(mContext)
                                .load(ScoreUtils.getTeamCrestByTeam(mContext, homeId))
                                .resize(72, 72)
                                .centerCrop()
                                .into(viewHolder.homeCrest);

                        Picasso.with(mContext)
                                .load(ScoreUtils.getTeamCrestByTeam(mContext, awayId))
                                .resize(72, 72)
                                .centerCrop()
                                .into(viewHolder.awayCrest);

                        if (!UIUtils.isJellyBeanMR2()) {
                            FractionalTouchDelegate.setupDelegate(viewHolder.cardView, viewHolder.overflow, new RectF(0.8f, 0f, 1.0f, 1.0f));
                        }
                        viewHolder.overflow.setOnClickListener(mOverflowClickListener);
                        viewHolder.overflow.setTag(R.id.score_list_menu_position, position);
                    }
                }
            }

            final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());
            // Overrides xml attrs, could use different layouts too.
            if (lineItem.isHeader) {
                lp.headerDisplay = LayoutManager.LayoutParams.HEADER_STICKY | LayoutManager.LayoutParams.HEADER_INLINE;
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.headerEndMarginIsAuto = true;
                lp.headerStartMarginIsAuto = true;
            }
            lp.setSlm(GridSLM.ID);
            lp.setNumColumns(mNumColumns);
            lp.setFirstPosition(lineItem.sectionFirstPosition);
            itemView.setLayoutParams(lp);
        }

        @Override
        public int getItemViewType(int position) {
            return mLineItems.get(position).isHeader ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return mLineItems.size();
        }

        public Score getItem(int position) {
            return mLineItems.get(position).score;
        }

        public void addAll(List<Score> scores) {
            mItems.clear();
            mItems.addAll(scores);

            //Insert headers into list of items.
            Header header;
            int lastLeagueId = -1;
            int sectionManager = -1;
            int headerCount = 0;
            int sectionFirstPosition = 0;
            for (int i = 0; i < mItems.size(); i++) {
                int leagueId = mItems.get(i).getLeagueId();

                if (leagueId != lastLeagueId) {
                    // Insert new header view and update section data.
                    sectionManager = (sectionManager + 1) % 2;
                    sectionFirstPosition = i + headerCount;
                    lastLeagueId = leagueId;
                    headerCount += 1;

                    String league = ScoreUtils.getLeague(mContext, leagueId, false);

                    header = new Header();
                    header.setFirstLine(league);

                    mLineItems.add(new LineItem(null, header, true, sectionManager, sectionFirstPosition));
                }
                mLineItems.add(new LineItem(mItems.get(i), null, false, sectionManager, sectionFirstPosition));
            }
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
            mOnItemClickListener = itemClickListener;
        }

        public void setOverflowClickListener(OverflowClickListener listener) {
            mOverflowClickListener = listener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public final ImageView homeCrest;
            public final ImageView awayCrest;
            public final TextView date;
            public final TextView homeScore;
            public final TextView homeTeam;
            public final TextView awayScore;
            public final TextView awayTeam;
            public final TextView matchDay;
            public final ImageButton overflow;
            public final CardView cardView;

            private TextView header;

            public ViewHolder(View base) {
                super(base);

                homeCrest = (ImageView) base.findViewById(R.id.home_crest);
                awayCrest = (ImageView) base.findViewById(R.id.away_crest);
                date = (TextView) base.findViewById(R.id.date);
                homeScore = (TextView) base.findViewById(R.id.home_score);
                homeTeam = (TextView) base.findViewById(R.id.home_name);
                awayScore = (TextView) base.findViewById(R.id.away_score);
                awayTeam = (TextView) base.findViewById(R.id.away_name);
                matchDay = (TextView) base.findViewById(R.id.match_day);
                overflow = (ImageButton) base.findViewById(R.id.overflow_menu);
                cardView = (CardView) base.findViewById(R.id.item_container);

                header = (TextView) base.findViewById(R.id.header);

                base.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        public interface OverflowClickListener extends View.OnClickListener, PopupMenu.OnMenuItemClickListener {
            @Override
            void onClick(View v);

            @Override
            boolean onMenuItemClick(MenuItem item);
        }

        private static class LineItem {
            public int sectionManager;
            public int sectionFirstPosition;
            public boolean isHeader;
            public Score score;
            public Header header;

            public LineItem(Score score, Header header, boolean isHeader, int sectionManager, int sectionFirstPosition) {
                this.isHeader = isHeader;
                this.score = score;
                this.header = header;
                this.sectionManager = sectionManager;
                this.sectionFirstPosition = sectionFirstPosition;
            }
        }
    }
}
