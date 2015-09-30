package barqsoft.footballscores;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.tonicartos.superslim.LayoutManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.content.ProviderUtils;
import barqsoft.footballscores.object.Score;
import barqsoft.footballscores.utils.Adapters;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.ScoreUtils;

// DONE: (ERROR) This fragment should not be calling the score service - it is called 5 times. The service call has been moved to the MainActivity
// DONE: Add a date above the scores - the layout is a bit confusing and can lose track of what "today" actually means if the
// scores haven't been updated
// DONE: Save the list on rotation
// DONE: Implement the share action
/**
 * Score fragment for each day's matches
 */
public class ScoresFragment extends Fragment {
    private Activity mActivity;
    private ProviderUtils mProvider;

    private View mRootView;
    private RecyclerView mRecyclerView;
    private Adapters.ScoreListAdapter mAdapter;

    private List<Score> mScores;
    private String mDate;
    private int mPosition;

    public ScoresFragment() {
    }

    public void setFragmentDate(String date) {
        mDate = date;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        mProvider = ProviderUtils.Factory.get(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.scores_list);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LayoutManager(mActivity));

        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelableArrayList(Score.class.getName()) != null) {
                mScores = savedInstanceState.getParcelableArrayList(Score.class.getName());
            }
            if (!TextUtils.isEmpty(savedInstanceState.getString(Constants.Extra.MATCH_DATE))) {
                mDate = savedInstanceState.getString(Constants.Extra.MATCH_DATE);
            }
        }

        TextView matchDate = (TextView) mRootView.findViewById(R.id.match_date);
        matchDate.setText(mDate);

        getScores();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(Score.class.getName(), (ArrayList) mScores);
        outState.putString(Constants.Extra.MATCH_DATE, mDate);
    }

    private void getScores() {
        mAdapter = new Adapters.ScoreListAdapter(mActivity, mActivity.getResources().getInteger(R.integer.scores_per_row));
        mAdapter.setOverflowClickListener(mOverflowClickListener);
        mRecyclerView.setAdapter(mAdapter);

        Runnable load = new Runnable() {
            public void run() {
                try {
                    if (mScores == null || mScores.isEmpty()) {
                        mScores = mProvider.getScoreList(mDate);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    mActivity.runOnUiThread(getScoresRunnable);
                }
            }
        };

        Thread thread = new Thread(null, load, "getScores");
        thread.start();
    }

    private final Runnable getScoresRunnable = new Runnable() {
        public void run() {
            mRootView.findViewById(R.id.scores_list).setVisibility(View.VISIBLE);

            if (mScores != null && mScores.size() > 0) {
                mAdapter.addAll(mScores);

                mRootView.findViewById(R.id.empty_score_list_container).setVisibility(View.GONE);
            } else {
                mRootView.findViewById(R.id.empty_score_list_container).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.scores_list).setVisibility(View.GONE);
            }
        }
    };

    private void handlePopupMenu(MenuItem item, Score score) {
        switch (item.getItemId()) {
            case R.id.action_share:
                String subject = mActivity.getString(R.string.content_share_subject, score.getHomeName(), score.getAwayName());
                String message = score.getHomeGoals() >= 0 ?
                        mActivity.getString(R.string.content_share_message_result, score.getHomeName(), score.getHomeGoals(),
                                score.getAwayName(), score.getAwayGoals()) :
                        mActivity.getString(R.string.content_share_message_upcoming, score.getMatchDay(), score.getTime(),
                                ScoreUtils.getLeague(mActivity, score.getLeagueId(), false));

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, message);

                mActivity.startActivity(Intent.createChooser(intent, mActivity.getString(R.string.content_share_app)));
                break;
        }
    }

    private void showPopupMenu(View view, PopupMenu.OnMenuItemClickListener listener) {
        PopupMenu popup = new PopupMenu(mActivity, view);
        popup.setOnMenuItemClickListener(listener);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_score_list, popup.getMenu());
        popup.show();
    }

    private final Adapters.ScoreListAdapter.OverflowClickListener mOverflowClickListener = new Adapters.ScoreListAdapter.OverflowClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v, this);
            mPosition = (Integer) v.getTag(R.id.score_list_menu_position);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            handlePopupMenu(item, mAdapter.getItem(mPosition));
            return false;
        }
    };
}
