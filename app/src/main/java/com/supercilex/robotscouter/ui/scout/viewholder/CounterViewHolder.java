package com.supercilex.robotscouter.ui.scout.viewholder;

import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.Query;
import com.supercilex.robotscouter.R;
import com.supercilex.robotscouter.data.model.ScoutMetric;

public class CounterViewHolder extends ScoutViewHolder<Integer, TextView> {
    private ImageButton mIncrement;
    private TextView mCount;
    private ImageButton mDecrement;

    public CounterViewHolder(View itemView) {
        super(itemView);
        mIncrement = (ImageButton) itemView.findViewById(R.id.increment_counter);
        mCount = (TextView) itemView.findViewById(R.id.count);
        mDecrement = (ImageButton) itemView.findViewById(R.id.decrement_counter);
    }

    @Override
    public void bind(ScoutMetric<Integer> metric) {
        super.bind(metric);
        mCount.setText(String.valueOf(mMetric.getValue()));
    }

    @Override
    public void setClickListeners(final Query query,
                                  final SimpleItemAnimator animator) {
        mIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(mCount.getText().toString()) + 1;
                mCount.setText(String.valueOf(value));
                mMetric.setValue(query, value, animator);
            }
        });

        mDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(mCount.getText().toString()) > 0) {
                    int value = Integer.parseInt(mCount.getText().toString()) - 1;
                    mCount.setText(String.valueOf(value));
                    mMetric.setValue(query, value, animator);
                }
            }
        });
    }
}
