package com.atguigu.mapreduce.partitioner;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FlowReducer extends Reducer<Text, FlowBean, Text, FlowBean> {
    private FlowBean ontV = new FlowBean();

    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Reducer<Text, FlowBean, Text, FlowBean>.Context context) throws IOException, InterruptedException {
        long toatlUp = 0;
        long toatlDown = 0;
        for (FlowBean value : values) {
            toatlUp += value.getUpFlow();
            toatlDown += value.getDownFlow();
        }
        ontV.setUpFlow(toatlUp);
        ontV.setDownFlow(toatlDown);
        ontV.setSumFlow();

        context.write(key, ontV);

    }
}
