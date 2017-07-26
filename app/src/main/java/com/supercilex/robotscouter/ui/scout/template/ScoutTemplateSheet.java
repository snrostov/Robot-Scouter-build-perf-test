package com.supercilex.robotscouter.ui.scout.template;

import android.app.Dialog;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.firebase.ui.database.ObservableSnapshotArray;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DatabaseReference;
import com.supercilex.robotscouter.R;
import com.supercilex.robotscouter.RobotScouter;
import com.supercilex.robotscouter.data.model.Metric;
import com.supercilex.robotscouter.data.model.Team;
import com.supercilex.robotscouter.data.util.FirebaseCopier;
import com.supercilex.robotscouter.util.DatabaseUtilsKt;
import com.supercilex.robotscouter.util.TeamUtilsKt;

import java.util.Collections;

import static com.supercilex.robotscouter.util.ConstantsKt.FIREBASE_VALUE;
import static com.supercilex.robotscouter.util.ConstantsKt.getDefaultTemplateListener;
import static com.supercilex.robotscouter.util.ConstantsKt.getFIREBASE_SCOUT_TEMPLATES;
import static com.supercilex.robotscouter.util.ConstantsKt.getTeamsListener;
import static com.supercilex.robotscouter.util.ConstantsKt.getTemplatesListener;
import static com.supercilex.robotscouter.util.FirebaseAdapterUtilsKt.getHighestIntPriority;
import static com.supercilex.robotscouter.util.UserUtilsKt.getTemplateIndicesRef;

public class ScoutTemplateSheet extends BottomSheetDialogFragment
        implements View.OnClickListener, DialogInterface.OnShowListener, RecyclerView.OnItemTouchListener,
        LifecycleRegistryOwner {
    private static final String TAG = "ScoutTemplateSheet";

    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    private View mRootView;
    private FloatingActionMenu mFam;

    private RecyclerView mRecyclerView;
    private ScoutTemplateAdapter mAdapter;
    private LinearLayoutManager mManager;
    private ScoutTemplateItemTouchCallback mItemTouchCallback;
    private boolean mHasAddedItem;

    private String mTemplateKey;

    public static void show(FragmentManager manager, Team team) {
        ScoutTemplateSheet sheet = new ScoutTemplateSheet();
        sheet.setArguments(TeamUtilsKt.toBundle(team));
        sheet.show(manager, TAG);
    }


    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new BottomSheetDialog(getContext()) {
            @Override
            public void onBackPressed() {
                if (mFam.isOpened()) {
                    mFam.close(true);
                } else {
                    super.onBackPressed();
                }
            }
        };
        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        BottomSheetDialog d = (BottomSheetDialog) dialog;
        FrameLayout bottomSheet = d.findViewById(android.support.design.R.id.design_bottom_sheet);
        int parentHeight = getActivity().findViewById(android.R.id.content).getHeight();
        BottomSheetBehavior.from(bottomSheet).setPeekHeight(parentHeight);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_scout_template, container, false);

        getTemplateKey();
        setupRecyclerView();
        initFabMenu();
        mRootView.findViewById(R.id.reset_template_all).setOnClickListener(this);
        mRootView.findViewById(R.id.reset_template_team).setOnClickListener(this);
        mRootView.findViewById(R.id.remove_metrics).setOnClickListener(this);

        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
        mRecyclerView.clearFocus(); // Needed to ensure template is saved if user taps outside sheet
        RobotScouter.Companion.getRefWatcher(getActivity()).watch(this);
    }

    private void getTemplateKey() {
        Team team = TeamUtilsKt.parseTeam(getArguments());
        mTemplateKey = team.getTemplateKey();

        if (TextUtils.isEmpty(mTemplateKey)) {
            DatabaseUtilsKt.observeOnce(getTemplatesListener(), true)
                    .addOnSuccessListener(templates -> {
                        if (templates != null && !templates.isEmpty()) {
                            mTemplateKey = templates.get(0).getKey();
                            TeamUtilsKt.updateTemplateKey(team, mTemplateKey);
                            return;
                        }

                        DatabaseReference newTemplateRef = getFIREBASE_SCOUT_TEMPLATES().push();
                        mTemplateKey = newTemplateRef.getKey();

                        FirebaseCopier.Companion.copyTo(getDefaultTemplateListener().getValue(),
                                                        newTemplateRef);
                        TeamUtilsKt.updateTemplateKey(team, mTemplateKey);
                        getTemplateIndicesRef().child(mTemplateKey).setValue(true);

                        ObservableSnapshotArray<Team> teams = getTeamsListener().getValue();
                        for (int i = 0; i < teams.size(); i++) {
                            Team otherTeam = teams.getObject(i);
                            String templateKey = otherTeam.getTemplateKey();

                            if (TextUtils.isEmpty(templateKey)) {
                                TeamUtilsKt.updateTemplateKey(otherTeam, mTemplateKey);
                            }
                        }
                    });
        }
    }

    private void setupRecyclerView() {
        mRecyclerView = mRootView.findViewById(R.id.list);
        mManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setHasFixedSize(true);
        mItemTouchCallback = new ScoutTemplateItemTouchCallback(mRootView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mItemTouchCallback);
        mItemTouchCallback.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    // User scrolled down -> hide the FAB
                    mFam.hideMenuButton(true);
                } else if (dy < 0) {
                    mFam.showMenuButton(true);
                } else if (mHasAddedItem
                        && (mManager.findFirstCompletelyVisibleItemPosition() != 0
                        || mManager.findLastCompletelyVisibleItemPosition() != mAdapter.getItemCount() - 1)) {
                    mFam.hideMenuButton(true);
                }

                mHasAddedItem = false;
            }
        });

        mAdapter = new ScoutTemplateAdapter(
                getFIREBASE_SCOUT_TEMPLATES().child(mTemplateKey),
                getChildFragmentManager(),
                mRecyclerView,
                mItemTouchCallback,
                this);
        mRecyclerView.setAdapter(mAdapter);
        mItemTouchCallback.setAdapter(mAdapter);
    }

    private void initFabMenu() {
        mFam = mRootView.findViewById(R.id.fab_menu);

        FloatingActionButton header = mRootView.findViewById(R.id.add_header);
        FloatingActionButton checkbox = mRootView.findViewById(R.id.add_checkbox);
        FloatingActionButton stopwatch = mRootView.findViewById(R.id.add_stopwatch);
        FloatingActionButton note = mRootView.findViewById(R.id.add_note);
        FloatingActionButton counter = mRootView.findViewById(R.id.add_counter);
        FloatingActionButton spinner = mRootView.findViewById(R.id.add_spinner);

        header.setOnClickListener(this);
        checkbox.setOnClickListener(this);
        stopwatch.setOnClickListener(this);
        note.setOnClickListener(this);
        counter.setOnClickListener(this);
        spinner.setOnClickListener(this);
        header.setImageDrawable(
                AppCompatResources.getDrawable(getContext(), R.drawable.ic_title_white_24dp));
        checkbox.setImageDrawable(
                AppCompatResources.getDrawable(getContext(), R.drawable.ic_done_white_24dp));
        stopwatch.setImageDrawable(
                AppCompatResources.getDrawable(getContext(), R.drawable.ic_timer_white_24dp));
        note.setImageDrawable(
                AppCompatResources.getDrawable(getContext(), R.drawable.ic_note_white_24dp));
        counter.setImageDrawable(
                AppCompatResources.getDrawable(getContext(), R.drawable.ic_count_white_24dp));
        spinner.setImageDrawable(
                AppCompatResources.getDrawable(getContext(), R.drawable.ic_list_white_24dp));

        // This lets us close the fam when the recyclerview it touched
        mRecyclerView.addOnItemTouchListener(this);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mFam.close(true);
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        DatabaseReference templateRef = getFIREBASE_SCOUT_TEMPLATES().child(mTemplateKey);

        if (id == R.id.reset_template_all || id == R.id.reset_template_team) {
            mRecyclerView.clearFocus();
            ResetTemplateDialog.Companion.show(getChildFragmentManager(),
                                               TeamUtilsKt.parseTeam(getArguments()),
                                               id == R.id.reset_template_all);
            return;
        } else if (id == R.id.remove_metrics) {
            RemoveAllMetricsDialog.Companion.show(getFragmentManager(), templateRef);
            return;
        }

        int priority = getHighestIntPriority(mAdapter.getSnapshots()) + 1;
        DatabaseReference metricRef = templateRef.push();
        switch (id) {
            case R.id.add_checkbox:
                metricRef.setValue(new Metric.Boolean("", false), priority);
                break;
            case R.id.add_counter:
                metricRef.setValue(new Metric.Number("", 0, null), priority);
                break;
            case R.id.add_spinner:
                metricRef.setValue(
                        new Metric.List("", Collections.singletonMap("a", "Item 1"), "a"),
                        priority);
                metricRef.child(FIREBASE_VALUE).child("a").setPriority(0);
                break;
            case R.id.add_note:
                metricRef.setValue(new Metric.Text("", ""), priority);
                break;
            case R.id.add_stopwatch:
                metricRef.setValue(
                        new Metric.Stopwatch("", Collections.emptyList()),
                        priority);
                break;
            case R.id.add_header:
                metricRef.setValue(new Metric.Header(""), priority);
                break;
            default:
                throw new IllegalStateException("Unknown id: " + id);
        }

        mItemTouchCallback.addItemToScrollQueue(mAdapter.getItemCount());
        mFam.close(true);
        mHasAddedItem = true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        // We only care about onInterceptTouchEvent
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // We only care about onInterceptTouchEvent
    }
}
